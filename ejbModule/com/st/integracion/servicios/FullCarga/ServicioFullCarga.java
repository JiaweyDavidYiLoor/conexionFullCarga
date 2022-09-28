package com.st.integracion.servicios.FullCarga;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.st.integracion.beans.AdminTransaccionLocal;
import com.st.integracion.beans.FullCarga.AdminFullCargaLocal;
import com.st.integracion.dto.Transaccion;
import com.st.integracion.dto.Transaccion.TipoOperacion;
import com.st.integracion.dto.FullCarga.DatosConfiguracion;
import com.st.integracion.dto.FullCarga.DatosConfiguracionWsdl;
import com.st.integracion.dto.FullCarga.EmpresaClienteRoll;
import com.st.integracion.dto.FullCarga.Producto;
import com.st.integracion.dto.FullCarga.Productos;
import com.st.integracion.dto.FullCarga.TransaccionFullCarga;
import com.st.integracion.exceptions.TransaccionException;
import com.st.integracion.network.FullCarga.FullCargaChannelHandler;
import com.st.integracion.network.FullCarga.FullCargaTrbListener;
import com.st.integracion.servicios.RegistroServicios;
import com.st.integracion.servicios.ServicioConeccion;
import com.st.integracion.util.SchedulerAdmin;
import com.st.integracion.util.ServicioTriggerFactory;
import com.st.integracion.util.FullCarga.Configuracion;
import com.st.integracion.util.FullCarga.Utilitaria;
import com.st.integracion.ws.FullCarga.InfoEmpresa;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

@Named("ServicioFullCarga")
@ApplicationScoped
public class ServicioFullCarga implements ServicioConeccion, Job {

	private static final Logger log = Logger.getLogger(ServicioFullCarga.class);
	private static final AtomicLong NULL_ATOMIC_LONG = new AtomicLong(Long.MIN_VALUE);
	private AtomicLong transacId = NULL_ATOMIC_LONG;
	private Timer timer;
	private RegistroServicios registro;
	private boolean running = false;
	private ConcurrentMap<TransaccionFullCarga, FullCargaTrbListener> pendientes;
	private long periodoEspera = 90000, periodoEsperaReverso = 30000;
	private static final String SCHED_GROUP = "EasyCash";
	private static final long SCHED_BASE = 2L;
	private static final long SCHED_MAX_EXP = 10L;
	private Scheduler scheduler;
	private static final ImmutableMap<Long, Producto> NULL_PROD_MAP = ImmutableMap.of();
	private ImmutableMap<Long, Producto> listaProductos = NULL_PROD_MAP;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd"); // 2014/10/14
	private Utilitaria util = new Utilitaria();
	private VariablesFullCarga srv;
	private long[] listaProveedores;
	@Inject
	private VariablesFullCargaLocal variables;

	public ServicioFullCarga() {
	}

	public void init() {
		log.info("Inciando servicio ServicioDtFullCarga");
		log.info("valor memoria servicio =" + this.toString());

		this.scheduler = SchedulerAdmin.getSchedulerAdmin().getScheduler();

		this.pendientes = new ConcurrentSkipListMap<TransaccionFullCarga, FullCargaTrbListener>();
		if (periodoEspera > 0)
			this.periodoEspera = periodoEspera;
		if (periodoEsperaReverso > 0)
			this.periodoEsperaReverso = periodoEsperaReverso;
		this.registro = RegistroServicios.registroInstance();
		this.timer = new Timer("Thread " + ServicioFullCarga.class.getSimpleName(), true);
		this.running = true;
	}

	public void finalizarScheler() throws SchedulerException {
		if (this.scheduler.isStarted()) {
			this.scheduler.clear();
			this.scheduler.shutdown();
		}
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
			TransaccionFullCarga tx = (TransaccionFullCarga) jobDataMap.get("tx");
			AdminFullCarga adminInteragua = (AdminFullCarga) RegistroServicios.registroInstance()
					.getAdminConeccion(tx.getCodigoProveedorProducto());
			ServicioFullCarga srv = (ServicioFullCarga) RegistroServicios.registroInstance()
					.getServicioConeccion(tx.getCodigoProveedorProducto());
			srv.writeTransaccion(tx, adminInteragua);
		} catch (Exception e) {
			throw new JobExecutionException(e, false);
		}
	}

	/**
	 * INICIA EL TIMER Y LO MARCA PARA QUE ESTE LISTO PARA ESCUCHAR
	 */
	public void run() {
		this.timer = new Timer("Thread " + ServicioFullCarga.class.getSimpleName(), true);
		this.running = true;
	}

	@Override
	public void recargar(Transaccion tran) {

		TransaccionFullCarga tx = (TransaccionFullCarga) tran;

		this.jitInit(tran.getCodigoProveedorProducto());
		tx = this.prepararTransaccion(tran, TipoOperacion.RECARGA);
		try {
			if (tx != null)
				this.scheduler.scheduleJob(getJobDetail(tx), getTrigger(tx));
		} catch (SchedulerException e) {
			log.info(
					"SchedulerException-ServicioFullCarga-recargar: no se pudo instanciar con JobDetail && Trigger..!");
			log.info("SchedulerException exception:" + e.getMessage());

		}

	}

	@Override
	public void anular(Transaccion tran) {
	}

	@Override
	public void consultarRecarga(Transaccion tran) {
		TransaccionFullCarga tx = this.prepararTransaccion(tran, TipoOperacion.CONSULTA_RX);
		try {
			List<TransaccionFullCarga> ticketsPendientes = this.pendientesPorTicket(tran.getNumTransaccion(),
					TipoOperacion.RECARGA, TipoOperacion.CONSULTA_RX);
			for (TransaccionFullCarga t : ticketsPendientes) {
				FullCargaTrbListener listener = (FullCargaTrbListener) this.pendientes.get(t);
				this.removeAndCancelPendiente(listener);
			}
		} catch (Exception e) {
			log.info("Exception-ServicioFullCarga-consultarRecarga: " + tran + " al realizar purging de transaccion ");
		}
		try {
			if (tx != null)
				this.scheduler.scheduleJob(getJobDetail(tx), getTrigger(tx));

		} catch (SchedulerException e) {
			log.info(
					"ScheluderException-ServicioFullCarga-consultarRecarga: anulacion no se pudo instanciar con GftJobDetail && GftTrigger.. !");
			e.printStackTrace();
		}
	}

	@Override
	public void comprarPin(Transaccion tran) {
	}

	@Override
	public void consultarCompraPin(Transaccion tran) {
	}

	public void realizarPendientes(final List<TransaccionFullCarga> pendientes) {
		long delay = 15000L;
		TimerTask conTask = new TimerTask() {
			@Override
			public void run() {
				try {
					realizarPendientesAction(pendientes);
				} catch (Exception e) {
					log.warn(
							"Exception-ServicioFullCarga-realizarPendientes: TransaccionTelebillete Erronea saldra en el log ",
							e);
				}
			}
		};
		this.timer.schedule(conTask, delay);
	}

	public void realizarPendientesAction(List<TransaccionFullCarga> pendientes) {
		Configuracion conf = new Configuracion();
		for (TransaccionFullCarga tx : pendientes) {
			switch (tx.getTipoOperacion()) {
			case RECARGA:
				Date fechaColaPeticion = tx.getFechaColaPeticion();
				long miliSegundos = ((new Date().getTime()) - fechaColaPeticion.getTime());
				long segundos = miliSegundos / 1000;
				long minutos = segundos / 60;
				if (minutos <= Integer.parseInt(conf.getTimeoutBase()))
					this.recargar(tx);
				else {
					try {
						tx.setDescripcion(conf.getDesTimeoutBase());
						this.transaccionNoEnviadaProveedor(tx);
					} catch (Exception e) {
						log.info("Exception-ServicioFullCarga-realizarPendientesAction: Transaccion No Ticket: "
								+ tx.getNumTransaccion()
								+ " no se ha enviado a el proveedor y no se ha guardado en BASE DATOS");
					}
				}
				break;
			}
		}
	}

	/**
	 * REMUEVE Y CANCELA LAS TRANSACCIONES PENDIENTES
	 * 
	 * @param channel
	 */
	public void cancelarPendientes() {
		for (TransaccionFullCarga tx : this.pendientes.keySet()) {
			FullCargaTrbListener l = this.pendientes.remove(tx);
			((TimerTask) l).cancel();
		}
	}

	public void transaccionEnviadaProveedor(FullCargaTrbListener l) {
		this.addAndRunPendiente(l);
	}

	public void transaccionNoEnviadaProveedor(TransaccionFullCarga txR) {
		try {
			AdminTransaccionLocal adminTran = this.registro.getAdminTransaccion(txR.getCodigoProveedorProducto());
			adminTran.transaccionNoEnviada(txR.clone());
		} catch (TransaccionException e) {
			String msg = String.format(
					"Exception-ServicioFullCarga-transaccionNoEnviadaProveedor: no sera sacada de pendientes la transaccion %s ",
					txR);
			log.info(msg, e);
			return;
		}
	}

	public void respuestaRecibidaProveedor(FullCargaTrbListener l) {
		TransaccionFullCarga tx = l.getTx();
		try {
			AdminTransaccionLocal adminTran = this.registro.getAdminTransaccion(tx.getCodigoProveedorProducto());
			adminTran.respuestaRecibida(tx.clone());
		} catch (TransaccionException e) {
			String msg = String.format(
					"Exception-ServicioFullCarga-respuestaRecibidaProveedor: no sera sacada de pendientes la transaccion %s ",
					tx);
			log.info(msg, e);
			return;
		}
		this.removeAndCancelPendiente(l);
	}

	private JobDetail getJobDetail(TransaccionFullCarga tx) {
		long mili = (new Date()).getTime();
		String name = String.valueOf(tx.getNumTransaccion() + String.valueOf(mili));
		tx.setJob(name);
		// log.info("nombre de job TS creado : " + name );
		JobDetail jd = JobBuilder.newJob(ServicioFullCarga.class).withIdentity(name, SCHED_GROUP).build();
		jd.getJobDataMap().put("tx", tx);
		return jd;
	}

	private Trigger getTrigger(TransaccionFullCarga tx) {
		TransaccionFullCarga tcopia = new TransaccionFullCarga();
		String numeroT = String.valueOf(tx.getNumTransaccion());
		double d = Math.random();
		int total = 0;
		total = (int) (d * 1000);
		numeroT = numeroT + String.valueOf(total);

		Long ticket = 0L;
		try {
			ticket = Long.valueOf(numeroT);
		} catch (Exception e) {
			ticket = (long) total;
		}

		tcopia.setCodigoMovimiento(ticket);
		Integer cantReqRealizados = null;
		cantReqRealizados = tx.getCantReqRealizados();
		if (cantReqRealizados == null) {
			log.info(
					"ServicioEasyCash-getTrigger: cantRequerimientosRealizados fue null : tx.getCantReqRealizados =  null");
			cantReqRealizados = 0;
		} else {
			try {
				cantReqRealizados = cantReqRealizados + 1;
			} catch (Exception e) {
				cantReqRealizados = 0;
			}
		}
		Integer cantidadCopia = cantReqRealizados;
		tx.setCantReqRealizados(cantReqRealizados);
		// log.info("Trigger preparado con Cantidad Requerimientos Realizados :"+
		// tx.getCantReqRealizados());
		tcopia.setCantReqRealizados(cantidadCopia);
		return ServicioTriggerFactory.triggerExponencial(SCHED_GROUP, SCHED_BASE, tcopia, SCHED_MAX_EXP);
	}

	public VariablesFullCargaLocal getVariables() {
		return variables;
	}

	public void setVariables(VariablesFullCargaLocal variables) {
		this.variables = variables;
	}

	/**
	 * ADICIONA LA TRANSACCION RECIEN ENVIADA COMO PENDIENTE (Y SU CORRESPONDIENTE
	 * LISTENER) Y ADICIONA EL LISTENER AL HANDLER
	 * 
	 * @param l
	 */
	private void addAndRunPendiente(FullCargaTrbListener l) {
		this.pendientes.putIfAbsent(l.getTx(), l);
		FullCargaChannelHandler.addChannelListener(l);
		if (this.pendientes.size() > 0)
			log.info(String.format("ServicioFullCarga-addAndRunPendiente: actualmente hay %d transacciones pendientes",
					this.pendientes.size()));
		if (l.getTx().getTipoOperacion().compareTo(TipoOperacion.ANULACION_CMP_PIN_INTERNET) == 0) {
			this.timer.scheduleAtFixedRate(l, this.periodoEsperaReverso, this.periodoEsperaReverso);
			log.info(String.format("ServicioFullCarga-addAndRunPendiente: ha sido adicionada y programada por "
					+ this.periodoEsperaReverso / 1000 + " segundos la transaccion %s ", l.getTx()));
		} else {
			this.timer.scheduleAtFixedRate(l, this.periodoEspera, this.periodoEspera);
			log.info(String.format("ServicioFullCarga-addAndRunPendiente: ha sido adicionada y programada por "
					+ this.periodoEspera / 1000 + " segundos la transaccion %s ", l.getTx()));
		}

	}

	private void removeAndCancelPendiente(FullCargaTrbListener l) {
		FullCargaTrbListener listener = this.pendientes.remove(l.getTx());
		FullCargaChannelHandler.removeChannelListener(listener);
		if (this.pendientes.size() > 0)
			log.info(String.format(
					"ServicioFullCarga-removeAndCancelPendiente: actualmente hay %d transacciones pendientes",
					this.pendientes.size()));
		try {
			((TimerTask) listener).cancel();
		} catch (NullPointerException npe) {
		}
		;
		log.info(String.format(
				"ServicioFullCarga-removeAndCancelPendiente: la transaccion %s ha sido removida y cancelada",
				l.getTx()));
	}

	private void writeTransaccion(final TransaccionFullCarga tx, final AdminFullCarga adminInteragua)
			throws JAXBException {
		int numeroIntentosConexion = 0;
		int maximoNumeroIntentosConexion = 3;
		Channel channel = null;
		do {
			numeroIntentosConexion = numeroIntentosConexion + 1;
			tx.setIntentos(numeroIntentosConexion);
			ServicioFullCarga sr = (ServicioFullCarga) this.getRegistro()
					.getServicioConeccion(tx.getCodigoProveedorProducto());
			log.info("obtenido de registro =" + sr.toString());
			log.info("obtenido de this =" + this.toString());
			VariablesFullCargaLocal varInteragua = this.getVariables();

			Map<Long, DatosConfiguracion> mapaDatosConf = varInteragua.getDatosConfig();

			InfoEmpresa infoEmpresa = (InfoEmpresa) util.convertirXmlToObjeto(tx.getParametrosInfoEmpresa(),
					InfoEmpresa.class);

			List<EmpresaClienteRoll> listaEmpresaCliRoll = varInteragua.getListaEmpresaClienteRoll();

			long codigoConfig = Utilitaria.obtenerCodigoConfiguracion(listaEmpresaCliRoll, infoEmpresa);

			DatosConfiguracion datosConf = new DatosConfiguracion();

			datosConf = mapaDatosConf.get(codigoConfig);

			long puertoL = datosConf.getPuertoWs();

			int puert = (int) puertoL;

			log.info(infoEmpresa.getCodigoEmpresa() + "");

			log.info("ip = " + datosConf.getIpWs() + " puerto = " + puert);
			channel = adminInteragua.getChannel(datosConf.getIpWs(), puert);
			if (channel != null) {
				if (channel.isOpen()) {
					numeroIntentosConexion = maximoNumeroIntentosConexion;
				}
			}
		} while (numeroIntentosConexion < maximoNumeroIntentosConexion);

		if (channel == null) {
			String descripcionError = String.format("ServicioFullCarga-writeTransaccion: error channel is null");
			tx.setDescripcion(descripcionError);
			transaccionNoEnviadaProveedor(tx);
			return;
		} else {
			if (channel.isOpen()) {
				tx.setChannelEnvio(channel.id().hashCode());
				tx.setTipoProductoStr(getDescripcionProducto(tx.getCodigoProveedorProducto()));
				ChannelFuture f = channel.writeAndFlush(buildHttpRequest(tx));
				f.addListener(new FullCargaTrbListener(this, tx));
			} else {
				String descripcionError = String
						.format("ServicioFullCarga-writeTransaccion: error channel no connected");
				tx.setDescripcion(descripcionError);
				transaccionNoEnviadaProveedor(tx);
				channel.close();
				return;
			}
		}
	}

	private synchronized void jitInit(long producto) {
		AdminFullCargaLocal adminTran = (AdminFullCargaLocal) this.registro.getAdminTransaccion(producto);
		if (NULL_ATOMIC_LONG == this.transacId)
			this.transacId = new AtomicLong(adminTran.ultimaTransacId() + 1);

		if (NULL_PROD_MAP == this.listaProductos) {
			Map<Long, Producto> lista = adminTran.listarProductosInteragua();
			this.listaProductos = ImmutableMap.copyOf(lista);
		}
	}

	private TransaccionFullCarga prepararTransaccion(Transaccion tran, TipoOperacion op) {
		if (!this.running)
			return null;
		TransaccionFullCarga tx = null;
		try {
			tx = (TransaccionFullCarga) tran;
			Producto prd = this.listaProductos.get(tx.getCodigoProveedorProducto());
			if (prd == null) {
				tx.setMensajeRetorno("Producto no configurado");
				try {
					transaccionNoEnviadaProveedor(tx);
				} catch (Exception e) {
					log.info("Exception:" + e.getMessage());
				}
				return null;
			}

			ServicioFullCarga servicioInteragua = (ServicioFullCarga) this.registro
					.getServicioConeccion(tx.getCodigoProveedorProducto());
			VariablesFullCargaLocal varInteragua = variables;

			Map<Long, DatosConfiguracion> mapaDatosConf = varInteragua.getDatosConfig();

			DatosConfiguracion datosConf = new DatosConfiguracion();

			this.transacId.compareAndSet(10000000L, 0L);
			tx.setTransacId(this.transacId.getAndIncrement());

			tx.setTipoOperacion(op);
			Date actual = new Date();
			String fechaStr = Utilitaria.formatFecha(actual, "dd-MM-yyyy");
			tx.setNumeroTransaccion(Long.toString(tx.getNumTransaccion()));

			tx.setFechaTransaccion(fechaStr);

			try {
				switch (op) {
				case RECARGA:

					BigDecimal valorContable = tx.getValorContable();
					String valor = String.valueOf(valorContable);
					String[] valorArray = valor.split("[.]");//[0, 25]
					if(valorArray[0].contains("0")) {
						tx.setImporte(valorContable.multiply(new BigDecimal(1)));
					}else {
						tx.setImporte(valorContable.multiply(new BigDecimal(100)).setScale(0)); //325.00
					}
					break;

				default:
					log.info(String.format(
							"ServicioFullCarga-prepararTransaccion: %s no se puede preparar la operacion de la tx ",
							tx.getNumeroTransaccion()));
					return null;
				}
				log.info("ServicioFullCarga-prepararTransaccion: " + tx.getNumeroTransaccion()
						+ "transaccion preparada para enviarse a procesar: ");

				this.registro.getAdminTransaccion(tx.getCodigoProveedorProducto()).transaccionEnviada(tx);
			} catch (TransaccionException e) {
				log.info("Exception-ServicioFullCarga-prepararTransaccion: Error al marcar la transaccion como enviada",
						e);
				return null;
			}
		} catch (Exception e) {
			log.info("Exception-ServicioFullCarga: Error al mapear el XMLconsulta |Exception:", e);
			return null;
		}
		return tx;
	}

	private String obtenerValorTag(Element elemento, String nombreTag) {
		String retorno = " ";
		try {
			NodeList nodo = elemento.getElementsByTagName(nombreTag);
			Element tag = (Element) nodo.item(0);

			if (tag.getTextContent() != null)
				retorno = tag.getTextContent();
		} catch (Exception e) {
			log.info("Exception-ServicioFullCarga-obtenerValorTag: " + e);
			retorno = " ";
		}
		log.info("ServicioFullCarga-obtenerValorTag: retorna " + retorno);
		return retorno;
	}

	private List<TransaccionFullCarga> pendientesPorTicket(long numeroTransaccion, TipoOperacion... operaciones) {
		List<TransaccionFullCarga> respuesta = new ArrayList<TransaccionFullCarga>();
		for (TipoOperacion op : operaciones) {
			for (TransaccionFullCarga tx : this.pendientes.keySet()) {
				if (numeroTransaccion == tx.getNumTransaccion() && op != null && op.equals(tx.getTipoOperacion()))
					respuesta.add(tx);
			}
		}
		return respuesta;
	}

	public void cancelTimer() {
		this.running = false;
		this.timer.cancel();
		this.timer.purge();
	}

	@Override
	public void comprarPinInternet(Transaccion tran) {

		TransaccionFullCarga tx = (TransaccionFullCarga) tran;

		this.jitInit(tran.getCodigoProveedorProducto());
		tx = this.prepararTransaccion(tran, TipoOperacion.COMPRAR_PIN_INTERNET);
		try {
			if (tx != null)
				this.scheduler.scheduleJob(getJobDetail(tx), getTrigger(tx));
		} catch (SchedulerException e) {
			log.info(
					"SchedulerException-ServicioFullCarga-recargar: no se pudo instanciar con JobDetail && Trigger..!");
			log.info("SchedulerException exception:" + e.getMessage());

		}

	}

	@Override
	public void consultarCompraPinInternet(Transaccion tran) {
	}

	@Override
	public void anularCompraPinInternet(Transaccion tran) {
		this.jitInit(tran.getCodigoProveedorProducto());
		TransaccionFullCarga tx = this.prepararTransaccion(tran, TipoOperacion.ANULACION_CMP_PIN_INTERNET);
		// log.info("realizando limpieza de transaccion "+tran);
		try {
			List<TransaccionFullCarga> ticketsPendientes = this.pendientesPorTicket(tran.getNumTransaccion(),
					TipoOperacion.COMPRAR_PIN_INTERNET, TipoOperacion.ANULACION_CMP_PIN_INTERNET);
			for (TransaccionFullCarga t : ticketsPendientes) {
				FullCargaTrbListener listener = (FullCargaTrbListener) this.pendientes.get(t);
				this.removeAndCancelPendiente(listener);
			}
		} catch (Exception e) {
			log.info("Exception-ServicioFullCarga-anularCompraPinInternet: Al realizar purging de transaccion " + tran);
		}
		try {
			if (tx != null)
				this.scheduler.scheduleJob(getJobDetail(tx), getTrigger(tx));

		} catch (SchedulerException e) {
			log.info(
					"ScheluderException-ServicioFullCarga-anularCompraPinInternet: No se pudo instanciar con GftJobDetail && GftTrigger.. !");
			e.printStackTrace();
		}
	}

	@Override
	public void consultarAnulacionCompraPinInternet(Transaccion tran) {
	}

	@Override
	public void consultarAnulacion(Transaccion tran) {
	}

	@Override
	public void activar(Transaccion tran) {
	}

	@Override
	public void desactivar(Transaccion tran) {
	}

	@Override
	public void anularCompraPin(Transaccion tran) {
	}

	@Override
	public void consultarActivacion(Transaccion tx) {
	}

	@Override
	public void consultarDesactivacion(Transaccion tx) {
	}

	@Override
	public void anularCompraPinInternacional(Transaccion tx) {
	}

	@Override
	public void anularRecargaInternacionalTarjeta(Transaccion tx) {
	}

	@Override
	public void anularRecargaInternacionalTelefono(Transaccion tx) {
	}

	@Override
	public void comprarPinInternacional(Transaccion tx) {
	}

	@Override
	public void consultarAnulacionCompraPin(Transaccion tx) {
	}

	@Override
	public void consultarAnulacionCompraPinInternacional(Transaccion tx) {
	}

	@Override
	public void consultarAnulacionRecargaInternacionalTarjeta(Transaccion tx) {
	}

	@Override
	public void consultarAnulacionRecargaInternacionalTelefono(Transaccion tx) {
	}

	@Override
	public void consultarCompraPinInternacional(Transaccion tx) {
	}

	@Override
	public void consultarRecargaInternacionalTarjeta(Transaccion tx) {
	}

	@Override
	public void consultarRecargaInternacionalTelefono(Transaccion tx) {
	}

	@Override
	public void recargarInternacionalTarjeta(Transaccion tx) {
	}

	@Override
	public void recargarInternacionalTelefono(Transaccion tx) {
	}

	public void iniciarServicioInternoVariables() {
//		if (srv==null)
//				this.buscarServicio();
		this.listaProveedores = srv.getProveedores();
	}

	public VariablesFullCarga getSrv() {
//		if (srv==null)
//			this.buscarServicio();
		return srv;
	}

	public void setSrv(VariablesFullCarga srv) {
		this.srv = srv;
	}

	public long[] getListaProveedores() {
		return listaProveedores;
	}

	public void setListaProveedores(long[] listaProveedores) {
		this.listaProveedores = listaProveedores;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	// @Override
	public int removerPendientePorNumeroTransacion(Long numeroTransaccion) {
		try {
			List<TransaccionFullCarga> ticketsPendientes = this.pendientesPorTicket(numeroTransaccion,
					TipoOperacion.RECARGA);
			for (TransaccionFullCarga t : ticketsPendientes) {
				FullCargaTrbListener listener = (FullCargaTrbListener) this.pendientes.get(t);
				this.removeAndCancelPendiente(listener);
			}
		} catch (Exception e) {
			log.info("error al realizar purging de transaccion:" + numeroTransaccion);
			return 1;
		}

		return 0;
	}

	public void guardarTrama(long producto, String trama, long numeroTransaccion, long codigoMovimiento,
			long transacId) {
		AdminFullCargaLocal adminTran = (AdminFullCargaLocal) this.registro.getAdminTransaccion(producto);
		adminTran.insertarTramaRequerimiento(trama, numeroTransaccion, codigoMovimiento, transacId);
	}

	public String editNumber(String x) {
		if (x.contains("+593")) {
			return x.replace("+593", "0");
		} else {
			return x;
		}
	}

	public String getIDProducto(long proovedorProducto) {
		VariablesFullCargaLocal varInteragua = this.getVariables();
		List<Productos> localP = varInteragua.getListarEmpresaProductos();
		Map<Long, Producto> listaProductos = varInteragua.getListaProductos();
		Producto localProducto = new Producto();
		String localName = "";
		for (Producto p : listaProductos.values()) {
			if (p.getCodigo() == proovedorProducto) {
				localProducto = p;
			}
		}
		for (Productos p : localP) {
			if (localProducto.getIdfc() == p.getCodigo()) {
				localName = p.getId();
			}
		}
		return localName;
	}

	public String getDescripcionProducto(long proovedorProducto) {
		VariablesFullCargaLocal varInteragua = this.getVariables();
		List<Productos> localP = varInteragua.getListarEmpresaProductos();
		Map<Long, Producto> listaProductos = varInteragua.getListaProductos();
		Producto localProducto = new Producto();
		String localName = "";
		for (Producto p : listaProductos.values()) {
			if (p.getCodigo() == proovedorProducto) {
				localProducto = p;
			}
		}
		for (Productos p : localP) {
			if (localProducto.getIdfc() == p.getCodigo()) {
				localName = p.getDescripcion();
			}
		}
		return localName;
	}

	private HttpRequest buildHttpRequest(TransaccionFullCarga tx) throws JAXBException {
		RegistroServicios registro;
		registro = RegistroServicios.registroInstance();
		VariablesFullCargaLocal varInteragua = this.getVariables();
		Map<Long, Producto> listaProductos = varInteragua.getListaProductos();
		Producto prd = listaProductos.get(tx.getCodigoProveedorProducto());

		Map<Long, DatosConfiguracion> mapaDatosConf = varInteragua.getDatosConfig();

		Map<Long, DatosConfiguracionWsdl> mapDatosConexWsdl = varInteragua.getDatosConfiguracionWsdl();

		JAXBContext jaxbContextTemp2 = JAXBContext.newInstance(InfoEmpresa.class);
		Unmarshaller unmarshallerTemp2 = jaxbContextTemp2.createUnmarshaller();
		StringReader readerTemp2 = new StringReader(tx.getParametrosInfoEmpresa());

		InfoEmpresa infoEmpresa = (InfoEmpresa) unmarshallerTemp2.unmarshal(readerTemp2);
		List<EmpresaClienteRoll> listaEmpresaCliRoll = varInteragua.getListaEmpresaClienteRoll();

		long codigoConfig = Utilitaria.obtenerCodigoConfiguracion(listaEmpresaCliRoll, infoEmpresa);

		DatosConfiguracionWsdl datConWsdl = mapDatosConexWsdl.get(codigoConfig);

		DatosConfiguracion datosConf = mapaDatosConf.get(codigoConfig);

		StringBuilder builderC = new StringBuilder();

		builderC.append(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:net=\"net.sigma.h2h.ws\">\r");
		builderC.append("<soapenv:Header/>\r");
		builderC.append("<soapenv:Body>\r");
		if (tx.getTipoOperacion() == TipoOperacion.RECARGA) {
			builderC.append("<net:venta>\r");
			builderC.append("<tpv>" + datConWsdl.getCanal() + "</tpv>\r");
			builderC.append("<claveTpv>" + datConWsdl.getTerminal() + "</claveTpv>\r");
			builderC.append(
					"<codigoProducto>" + getIDProducto(tx.getCodigoProveedorProducto()) + "</codigoProducto>\r");
			builderC.append("<referenciaOperacion>" + tx.getNumeroTransaccion() + "</referenciaOperacion>\r");
			builderC.append("<fechaHoraOperacion>" + Utilitaria.formatFecha(new Date(), "yyyy-MM-dd") + "T"
					+ Utilitaria.formatFecha(new Date(), "hh:mm:ss") + "</fechaHoraOperacion>\r");
			builderC.append("<parametrosOperacion>\r");
			builderC.append("<indice>" + 1 + "</indice>\r");
			builderC.append("<valor>" + tx.getReferenciaCliente() + "</valor>\r");
			builderC.append("</parametrosOperacion>\r");
			builderC.append("<parametrosOperacion>\r");
			builderC.append("<indice>" + 2 + "</indice>\r");
			builderC.append("<valor>" + tx.getImporte() + "</valor>\r");
			builderC.append("</parametrosOperacion>\r");
			builderC.append("</net:venta>\r");
		}
		
		builderC.append("</soapenv:Body>\r");
		builderC.append("</soapenv:Envelope>");
		tx.setTramaTxRequerimiento(builderC.toString());
		log.info("EasyCashChannelHandler-buildHttpRequest: enviando mensaje: " + builderC);
		String uri = datosConf.getPathUrlWs() + "SigmaWSLayer/SigmaService";
		ByteBuf buf = Unpooled.copiedBuffer(builderC.toString(), Charsets.UTF_8);
		FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri, buf);
		req.headers().set("SOAPAction", "\"\" ");
		req.headers().set(HttpHeaderNames.USER_AGENT, "Netty/4.1.48.Final");

		req.headers().set(HttpHeaderNames.HOST, datosConf.getIpWs() + ":" + datosConf.getPuertoWs());
		req.headers().set(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(buf.readableBytes()));
		req.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/xml;charset=UTF-8");
		req.headers().set(HttpHeaderNames.ACCEPT_ENCODING, "gzip,deflate");
		req.headers().set(HttpHeaderNames.CONNECTION, "Keep-Alive");

		log.info("estructura request = " + req);

		return req;
	}

	public RegistroServicios getRegistro() {
		return registro;
	}

	public void setRegistro(RegistroServicios registro) {
		this.registro = registro;
	}
}