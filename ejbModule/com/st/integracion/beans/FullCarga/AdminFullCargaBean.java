package com.st.integracion.beans.FullCarga;

import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.google.common.collect.ImmutableMap;
import com.srvtrn.cliente.entidades.CltMovimiento;
import com.st.integracion.beans.AdminTransaccionBean;
import com.st.integracion.beans.UtilitariaWs;
import com.st.integracion.cliente.st.ConsumoService;
import com.st.integracion.cliente.st.ConsumoServiceService;
import com.st.integracion.data.FullCarga.AnulacionDb;
import com.st.integracion.data.FullCarga.Datos;
import com.st.integracion.dto.Transaccion;
import com.st.integracion.dto.Transaccion.TipoOperacion;
import com.st.integracion.dto.FullCarga.ConfiguracionCorreo;
import com.st.integracion.dto.FullCarga.DatosConfiguracion;
import com.st.integracion.dto.FullCarga.DatosConfiguracionWsdl;
import com.st.integracion.dto.FullCarga.Empresa;
import com.st.integracion.dto.FullCarga.EmpresaClienteRoll;
import com.st.integracion.dto.FullCarga.Producto;
import com.st.integracion.dto.FullCarga.Productos;
import com.st.integracion.dto.FullCarga.ReversoAnulacionObj;
import com.st.integracion.dto.FullCarga.TransaccionFullCarga;
import com.st.integracion.dto.FullCarga.Transaccional;
import com.st.integracion.exceptions.TransaccionException;
import com.st.integracion.servicios.PropiedadesProveedorProducto;
import com.st.integracion.servicios.RegistroServicios;
import com.st.integracion.servicios.ServicioConeccion;
import com.st.integracion.servicios.FullCarga.ServicioFullCarga;
import com.st.integracion.servicios.FullCarga.VariablesFullCargaLocal;
import com.st.integracion.ticket.TicketsBeanRemote;
import com.st.integracion.util.FullCarga.TicketPrint;
import com.st.integracion.util.FullCarga.Utilitaria;
import com.st.integracion.ws.FullCarga.ConsultaFullCargaSt;
import com.st.integracion.ws.FullCarga.ParametrosRespuesta;
import com.thoughtworks.xstream.XStream;

@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
@Local(AdminFullCargaLocal.class)
public class AdminFullCargaBean extends AdminTransaccionBean implements AdminFullCargaLocal {
	@Resource
	private SessionContext context;
	@Resource(lookup = "java:jboss/datasource/FullCarga")
	private DataSource postgresTrbXADS;
//	@Resource(mappedName="java:jboss/DefaultJMSConnectionFactory")
//	private QueueConnectionFactory respuestaFactory;
//	@Resource(mappedName="java:jboss/exported/jms/queue/RespQueue")
//	private Queue queqeResponse;
	private Datos odatos = new Datos();
	private Utilitaria utl = new Utilitaria();
	private static final Logger log = Logger.getLogger(AdminFullCargaBean.class);
	private AnulacionDb anulacionDb = new AnulacionDb();

	private static final ImmutableMap<Long, Producto> NULL_MAP_ = ImmutableMap.of();
	private ImmutableMap<Long, Producto> listaProdts = NULL_MAP_;

	@Override
	public Transaccion transaccionRecibida(CltMovimiento mvm) throws TransaccionException {
		// TODO Auto-generated method stub
		// mvm.getProveedor().
		Long codigoMovimiento = mvm.getMvmCodigo(); // ID de esta entidad en la tabla Clt_movimiento de la base de datos
		Long numTransaccion = mvm.getMvmNumTransaccion(); // Es un identificador que permite agrupar a varios
															// movimientos correspondientes a la misma transacci�n.
		java.util.Date fecha = mvm.getMvmFecha(); // Es la Fecha en la que se crea el Movimiento
		String descripcion = mvm.getMvmDescripcion(); // Es una Breve rese�a del Movimiento, debe contener un mensaje
														// tal que al leer se entienda el por que fue creado el
														// Movimiento
		Long codigoCliente = mvm.getMvmClienteFk(); // Es el ID del Cliente due�o del Movimiento (Transacci�n);
		Long codigoUsuario = mvm.getMvmCodigoUsuario(); // Es el ID del usuario que est� realizando la transacci�n
		BigDecimal valorContable = mvm.getMvmValorContable(); // Monto te�rico de dinero por el cual se realiza la
																// transacci�n
		String referenciaCliente = mvm.getMvmReferencia(); // Es un mensaje que hace referencia al Cliente

		String mvmParametroXmlIn = mvm.getMvmParametroXmlIn(); // Parametro Xml de entrada para realizar un consumo
		String identificadorHost = mvm.getMvmIdentificadorHost(); // Este par�metro es usado en caso de que una
																	// integraci�n de un Cliente al sistema deba poseer
																	// un identificador
		String referenciaProveedor = mvm.getMvmReferenciaProveedor(); // Es un mensaje que hace referencia al proveedor
		String mvmParametroXmlOut = mvm.getMvmParametroXmlOut(); // Parametro Xml de Salida (Actualmente no se usa)
		Long codigoBodega = mvm.getMvmBodegaFk(); // Es el ID de la Bodega de la cual se obtendr� el producto a consumir
		Long codigoProveedorProducto = mvm.getMvmProveedorProductoFk(); // Atributo que contiene el ID de la relaci�n
																		// ProveedorProducto que esta en la base de
																		// datos
		Long codigoEstadoConexion = mvm.getCodigoEstadoConexion();

		int codigoEstado;
		int codigoProceso;
		TipoOperacion tipoOperacion = null;
		TransaccionFullCarga trn = new TransaccionFullCarga();
		trn.setCodigoMovimiento(codigoMovimiento);
		trn.setNumTransaccion(numTransaccion);
		trn.setFecha(fecha);
		trn.setDescripcion(descripcion);
		trn.setCodigoCliente(codigoCliente);
		trn.setCodigoUsuario(codigoUsuario);
		trn.setValorContable(valorContable);
		trn.setReferenciaCliente(referenciaCliente);
		trn.setMvmParametroXmlIn(mvmParametroXmlIn);
		trn.setIdentificadorHost(identificadorHost);
		trn.setReferenciaProveedor(referenciaProveedor);
		trn.setMvmParametroXmlOut(mvmParametroXmlOut);
		trn.setCodigoBodega(codigoBodega);
		trn.setCodigoProveedorProducto(codigoProveedorProducto);
		trn.setCodigoEstadoConexion(codigoEstadoConexion);
		trn.setFechaColaPeticionCompraPinInternet(new Date());
		trn.setFechaColaPeticionRecarga(new Date());

		String descripcionM = null;
		descripcionM = "AdminInteragua-TransaccionRecibida: Inicio";
		AdminFullCargaBean.log.info(trn + descripcionM);
		Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			if (codigoEstadoConexion == 1) {
				tipoOperacion = TipoOperacion.RECARGA;
				codigoEstado = 7;// Recibida del MDB para proceso de recarga
				codigoProceso = 9;// Recibida del MDB para proceso de recarga
				trn.setCodigoEstado(codigoEstado);
				trn.setCodigoProceso(codigoProceso);
				trn.setTipoOperacion(tipoOperacion);

				DocumentBuilder builder1;
				builder1 = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputSource is = new InputSource(new StringReader(mvmParametroXmlIn));
				Document root = builder1.parse(is);

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				DocumentBuilder builder = factory.newDocumentBuilder();

				trn.setParametrosXmlInTransaccion("");

				Node sasInfoEmpresa = root.getElementsByTagName("InfoEmpresa").item(0);
				Document newInfoEmpresa = builder.newDocument();
				Node importedNodeInfoEmpresa = newInfoEmpresa.importNode(sasInfoEmpresa, true);
				newInfoEmpresa.appendChild(importedNodeInfoEmpresa);

				String infoEmpresa = utl.getStringFromDoc(newInfoEmpresa);
				trn.setParametrosInfoEmpresa(infoEmpresa);
				trn.setFechaEnvioRecarga(fecha);

				this.odatos.insertarTransaccional(Conn, codigoMovimiento, numTransaccion, fecha,
						trn.getFechaColaPeticionRecarga(), descripcion, codigoCliente, codigoUsuario, valorContable,
						referenciaCliente, mvmParametroXmlIn, identificadorHost, referenciaProveedor,
						mvmParametroXmlOut, codigoBodega, codigoProveedorProducto, codigoEstado, codigoProceso);

				descripcionM = "AdminFullCargaBean-TransaccionRecibida: La transaccion numero :"
						+ trn.getNumTransaccion() + "|" + " ,sera procesada para RECARGA";
				log.info(descripcionM);

			}
		}

		catch (SQLException e) {
			descripcion = "SQLException-AdminInteraguaBean-TransaccionRecibida: SQLException: Insertando en la transaccional";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			context.setRollbackOnly();
			trn = null;
			throw new TransaccionException(e);
		} catch (Exception e) {
			log.info("Exception-AdminInteraguaBean-TransaccionRecibida:" + e.getMessage());
			e.printStackTrace();
			this.context.setRollbackOnly();
			trn = null;
			throw new TransaccionException(e);
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return trn;
	}

	@Override
	protected void transaccionEnviadaCompraPinInternet(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		Connection Conn = null;
		String descripcion = null;
		TransaccionFullCarga trn = (TransaccionFullCarga) tx;
		descripcion = "AdminInteraguaBean-TransaccionEnviadaRecarga: Inicio";
		AdminFullCargaBean.log.info(trn + descripcion);
		try {
			Date fechaEnvioCompraPinInternet = trn.getFechaEnvioCompraPinInternet();
			int codigoEstado = 71; // Compra de Pin intenet en proceso
			int codigoProceso = 61; // Peticion Compra de Pin intenet
			trn.setCodigoEstado(codigoEstado);
			trn.setCodigoProceso(codigoProceso);

			Long codigoMovimiento = trn.getCodigoMovimiento();
			Long numTransaccion = trn.getNumTransaccion();
			Long transacId = trn.getTransacId();
			String numeroTransaccion = trn.getNumeroTransaccion();
			String fechaTransaccion = trn.getFechaTransaccion();
			BigDecimal valor = trn.getValorContable();
			String formaPago = "TEST";
			String tipoReferencia = "TEST";
			String codigoContrato = "TEST";
			Date fechaConciliacion = new Date();
			BigDecimal valorPagado = trn.getValorContable();
			Date fechaPago = trn.getFechaEnvioCompraPinInternet();
			String programa = "TEST";
			String terminal = "TEST";
			String puntoPago = "TEST";
			String tramaEnvio = trn.getTramaTxRequerimiento();
			String tramaRespuesta = trn.getTramaTxRespuesta();
			log.info("TRAMAR:" + tramaEnvio);
			String fechaConciliacionStr = Utilitaria.formatFecha(new Date(), "dd-MM-yyyy");
			String etapaTransaccion = "TX";
			Conn = postgresTrbXADS.getConnection();

			this.odatos.insertarTransaccionalDetalleEnvioRecarga(Conn, codigoMovimiento, numTransaccion, transacId,
					numeroTransaccion, fechaEnvioCompraPinInternet, valor, fechaConciliacionStr);

			this.odatos.actualizarTransaccionalEnvioRecarga(Conn, fechaEnvioCompraPinInternet, codigoEstado,
					codigoProceso, transacId, numTransaccion);

			descripcion = "AdminInteraguaBean-TransaccionEnviadaRecarga: actualizando transaccional e insertando detalle";
			AdminFullCargaBean.log.info(trn + descripcion);
		} catch (SQLException e) {
			descripcion = "SQLException-AdminInteraguaBean-TransaccionEnviadaRecarga: actualizando transaccional e insertando detalle";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			context.setRollbackOnly();
			throw new TransaccionException();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			descripcion = "Exception-AdminInteraguaBean-TransaccionEnviadaRecarga: actualizando transaccional e insertando detalle";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			this.context.setRollbackOnly();
			throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	protected void transaccionEnviadaAnulacionCompraPinInternet(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		java.sql.Connection Conn = null;
		String descripcion = null;
		TransaccionFullCarga trn = (TransaccionFullCarga) tx;
		descripcion = "AdminInteragua-transaccionEnviadaAnulacionCompraPinInternet: Inicio";
		AdminFullCargaBean.log.info(trn + descripcion);
		try {
			// Date fechaEnvioAnulacion = trn.getFechaEnvioAnulacionCompraPin();
			Date fechaEnvioAnulacion = new Date();
			int codigoEstado = 74;// Anulacion compra pin pendiente
			int codigoProceso = 53; // Peticion Anulacion compra pin
			trn.setCodigoEstado(codigoEstado);
			trn.setCodigoProceso(codigoProceso);
			Long numeroTransaccion = trn.getNumTransaccion();
			Long codigoMovimiento = trn.getCodigoMovimiento();
			Long transacId = trn.getTransacId();

			Conn = postgresTrbXADS.getConnection();

			// this.odatos.insertarTransaccionalDetalleEnvioAnulacion(Conn,
			// fechaEnvioAnulacion,
			// datosTransaccion, transacId, codigoMovimiento, numeroTransaccion);

			this.odatos.insertarTransaccionalDetalleEnvioAnulacion(Conn, codigoMovimiento, numeroTransaccion,
					fechaEnvioAnulacion, transacId, tx.getValorContable() + "");

			this.odatos.actualizarTransaccionalEnvioAnulacion(Conn, codigoEstado, codigoProceso, transacId,
					codigoMovimiento, numeroTransaccion, fechaEnvioAnulacion);

			descripcion = "AdminInteragua-transaccionEnviadaAnulacionCompraPinInternet: actualizando transaccional e insertando detalle";
			AdminFullCargaBean.log.info(trn + descripcion);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			descripcion = "SQLExeption-AdminInteragua-transaccionEnviadaAnulacionCompraPinInternet: actualizando transaccional e insertando detalle";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			this.context.setRollbackOnly();
			throw new TransaccionException();
		} catch (Exception e) {
			descripcion = "Exception-AdminInteragua-transaccionEnviadaAnulacionCompraPinInternet: actualizando transaccional e insertando detalle";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			this.context.setRollbackOnly();
			throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	protected Transaccion respuestaRecibidaCompraPinInternet(Transaccion tx) throws TransaccionException {
		String descripcion = null;
		TransaccionFullCarga trn = (TransaccionFullCarga) tx;
		Connection Conn = null;
		descripcion = "AdminFullCargaBean-respuestaRecibidaRecarga: Inicio";
		AdminFullCargaBean.log.info(trn + descripcion);
		try {
			trn = this.utl.procesarRespuesta(trn);

			Conn = postgresTrbXADS.getConnection();

			String mensajeProveedor = trn.getCodigoRetorno();

			int codigoEstado = trn.getCodigoEstado();
			int codigoProceso = trn.getCodigoProceso();
			Date fechaRespuestaComprarPinInternet = trn.getFechaRespuestaRecarga();
			String referenciaProveedor = trn.getReferenciaProveedor();
			String codigoRetorno = trn.getCodigoRetorno();
			String mensajeRetorno = trn.getMensajeRetorno();
			Long codigoMovimiento = trn.getCodigoMovimiento();
			Long numeroTransaccion = trn.getNumTransaccion();
			Long transacId = trn.getTransacId();
			String tramaEnvio = trn.getTramaTxRequerimiento(), tramaResp = trn.getTramaTxRespuesta(),
					tramaTipoPrd = trn.getTipoProductoStr();
			String etapaTransaccion = "TX";

			odatos.actualizarTransaccionalRespuesta(Conn, codigoEstado, codigoProceso, fechaRespuestaComprarPinInternet,
					referenciaProveedor, mensajeRetorno, numeroTransaccion, codigoMovimiento, transacId, codigoRetorno);

			odatos.actualizarTransaccionalDetalleRespuesta(Conn, numeroTransaccion, codigoMovimiento, transacId,
					codigoRetorno, mensajeRetorno, tramaEnvio, tramaResp, tramaTipoPrd);

			descripcion = "AdminFullCargaBean-respuestaRecibidaRecarga; actualizando transaccional y actualizando detalle | Codigo Movimiento: "
					+ trn.getCodigoMovimiento() + " Numero Serie : " + trn.getReferenciaCliente();
			AdminFullCargaBean.log.info(trn + descripcion);
			responderCola(trn);
		} catch (SQLException e) {
			descripcion = "SQLException-AdminInteraguaBean-respuestaRecibidaRecarga: actualizando transaccional y actualizando detalle";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			context.setRollbackOnly();
			throw new TransaccionException(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			descripcion = "Exception-AdminInteraguaBean-respuestaRecibidaRecarga: actualizando transaccional y actualizando detalle";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			this.context.setRollbackOnly();
			throw new TransaccionException(e);
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return trn;
	}

	@Override
	protected Transaccion respuestaRecibidaAnulacionCompraPinInternet(

			Transaccion tx) throws TransaccionException {
		String descripcion = null;
		TransaccionFullCarga trn = (TransaccionFullCarga) tx;
		Connection Conn = null;
		descripcion = "AdminInteraguaBean-respuestaRecibidaAnulacion:Inicio";
		AdminFullCargaBean.log.info(trn + descripcion);

		try {
			trn = this.utl.procesarRespuesta(trn);
			Date fechaRespuestaAnulacion = trn.getFechaRespuestaCompraPinInternet();
			int codigoEstado = trn.getCodigoEstado();
			int codigoProceso = trn.getCodigoProceso();
			Long codigoMovimiento = trn.getCodigoMovimiento();
			Long numeroTransaccion = trn.getNumTransaccion();
			Long transacId = trn.getTransacId();
			String referenciaProveedor = trn.getReferenciaProveedor();

			String codigoRetorno = trn.getCodigoRetorno();
			String mensajeRetorno = trn.getMensajeRetorno();

			String tramaReq = trn.getTramaTxRequerimiento(), tramaResp = trn.getTramaTxRespuesta();

			if (trn.getCodigoRetorno().equals("CL_065")) {
				trn.setTipoProductoStr("FIJO");
			}

			Conn = postgresTrbXADS.getConnection();

			odatos.actualizarTransaccionalDetalleRespuesta(Conn, numeroTransaccion, codigoMovimiento, transacId,
					codigoRetorno, mensajeRetorno, tramaReq, tramaResp, trn.getTipoProductoStr());

			odatos.actualizarTransaccionalRespuesta(Conn, codigoEstado, codigoProceso, fechaRespuestaAnulacion,
					referenciaProveedor, mensajeRetorno, numeroTransaccion, codigoMovimiento, transacId, codigoRetorno);

			// Si es anulacion exitosa se cambia el estado de 75 a 73 para que sea devuelta
			// en la plataforma
			if (trn.getCodigoEstado() == 75)
				trn.setCodigoEstado(73);

			descripcion = "AdminInteragua-respuestaRecibidaAnulacion: actualizando transaccional y actualizando detalle | Codigo Movimiento: "
					+ trn.getCodigoMovimiento() + " Numero Serie : " + trn.getReferenciaCliente();
			AdminFullCargaBean.log.info(trn + descripcion);
			responderCola(trn);

		} catch (SQLException e) {
			descripcion = "SQLException-AdminInteragua-respuestaRecibidaAnulacion: actualizando transaccional y actualizando detalle";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			context.setRollbackOnly();
			throw new TransaccionException(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			descripcion = "Exception-AdminInteragua-respuestaRecibidaAnulacion: actualizando transaccional y actualizando detalle";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			this.context.setRollbackOnly();
			throw new TransaccionException(e);
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return trn;

	}

	@Override
	protected void transaccionNoEnviadaRecarga(Transaccion tx) throws TransaccionException {

	}

	@Override
	protected void transaccionNoEnviadaCompraPinInternet(Transaccion tx) throws TransaccionException {

		java.sql.Connection Conn = null;

		String descripcion = null;

		TransaccionFullCarga trn = (TransaccionFullCarga) tx;
		descripcion = "AdminInteraguaBean-transaccionNoEnviadaCompraPinInternet: Inicio";
		int codigoEstado = 0; // Recarga no enviada
		int codigoProceso = 0; // Peticion Recarga

		AdminFullCargaBean.log.info(trn + descripcion);
		try {
			codigoEstado = 73; // Recarga no enviada
			codigoProceso = 62; // Peticion Recarga

			Long codigoEstadoConexion = (long) codigoEstado;
			String referenciaProveedor = "NAK";
			trn.setReferenciaProveedor(referenciaProveedor);
			trn.setCodigoEstado(codigoEstado);
			trn.setCodigoProceso(codigoProceso);
			trn.setCodigoEstadoConexion(codigoEstadoConexion);
			if (trn.getMensajeRetorno() == null)
				trn.setMensajeRetorno("No se pudo Enviar Transaccion");
			Long numTransaccion = trn.getNumTransaccion();
			Long codigoMovimiento = trn.getCodigoMovimiento();

			Conn = this.postgresTrbXADS.getConnection();

			this.odatos.actualizarTransaccionalEstadoPorNumeroticketFinal(Conn, codigoEstado, codigoProceso,
					referenciaProveedor, numTransaccion, codigoMovimiento, trn.getMensajeRetorno());

			/*
			 * this.odatos.insertarTramaTransaccionRequerimiento(Conn,
			 * trn.getNumTransaccion(), codigoMovimiento, trn.getTransacId(),
			 * "NO SE ENVIO TRANSACCION");
			 * this.odatos.insertarTramaTransaccionRespuesta(Conn, trn.getNumTransaccion(),
			 * codigoMovimiento, trn.getTransacId(), "NO SE ENVIO TRANSACCION");
			 */
			descripcion = "AdminInteraguaBean-transaccionNoEnviadaRecarga: actualizando transaccional campo estado";
			AdminFullCargaBean.log.info(trn + descripcion);
			responderCola(trn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			descripcion = "SQLException-AdminInteraguaBean-transaccionNoEnviadaRecarga: actualizando transaccional campo estado";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			this.context.setRollbackOnly();
			throw new TransaccionException();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			descripcion = "Exception-AdminInteraguaBean-transaccionNoEnviadaRecarga: actualizando transaccional campo estado";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			this.context.setRollbackOnly();
			throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	protected void transaccionNoEnviadaCompraPin(Transaccion tx) throws TransaccionException {
	}

	@Override
	public long ultimaTransacId() {
		// TODO: manejarlo bien
		long transascId = 0L;
		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			transascId = this.odatos.consultarUltimoTransacid(Conn);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return transascId;
	}

	@Override
	public List<Transaccion> listarTransaccionesPendientes() {

		List<Transaccion> retorno = new ArrayList<Transaccion>();
		List<Transaccion> devolverRecarga = new ArrayList<Transaccion>();
		List<Transaccion> devolverAnulacion = new ArrayList<Transaccion>();
		boolean banDevolverRecarga = false;
		boolean banDevolverAnulacion = false;

		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			List<Transaccional> lTrn = this.odatos.consultarTransaccionalPendientes(Conn);
			int tam = lTrn.size();
			for (int i = 0; i < tam; i++) {
				Transaccional oTransaccional = lTrn.get(i);

				TransaccionFullCarga trn = new TransaccionFullCarga();

				long codigoMovimiento = oTransaccional.getCodigoMovimiento(); // 1
				long numeroTransaccion = oTransaccional.getNumTransaccion(); // 2
				Date fecha = oTransaccional.getFecha();// 3
				String descripcio = oTransaccional.getDescripcion(); // 4
				long codigoCliente = oTransaccional.getNumeroCliente(); // 5
				long codigoUsuario = oTransaccional.getCodigoUsuario(); // 6
				BigDecimal importe = oTransaccional.getValorContable();// 7
				String nicNpe = oTransaccional.getReferenciaCliente();// 8
				String parametroXmlIn = oTransaccional.getParametroXmlIn(); // 9
				String identificadorClienteHost = oTransaccional.getIdentificadorHost(); // 10
				String referenciaProveedor = oTransaccional.getReferenciaProveedor(); // 11
				String parametroXmlOut = oTransaccional.getParametroXmlOut();// 12
				long codigoBodega = oTransaccional.getBodega();// 13
				long numeroProveedor = oTransaccional.getProveedorProducto(); // 14
				int codigoEstado = oTransaccional.getEstadoFk();// 15
				int codigoProceso = oTransaccional.getProcesoFk();// 16
				long trannsacId = oTransaccional.getTransacId();// 17
				Date fechaRecibidaColaPeticionRecarga = oTransaccional.getFechaRecibidaColaPeticionRecarga(); // 18
				Date fechaEnvioRecarga = oTransaccional.getFechaEnvioRecarga();// 19
				Date fechaRespuestaRecarga = oTransaccional.getFechaRespuestaRecarga(); // 20
				Date fechaRecibidaColaPeticionAnulacion = oTransaccional.getFechaRecibidaColaPeticionAnulacion();// 24
				Date fechaEnvioAnulacion = oTransaccional.getFechaEnvioAnulacion();// 25
				Date fechaRespuestaAnulacion = oTransaccional.getFechaRespuestaAnulacion();// 26
				String descripcionProveedor = oTransaccional.getDescripcionProveedor(); // 30

				TipoOperacion tipoOperacion = null;

				if (codigoEstado == 71) {// Recarga en proceso

					tipoOperacion = TipoOperacion.ANULACION_CMP_PIN_INTERNET;
				}
				if (codigoEstado == 74) {// Anulacion en proceso
					tipoOperacion = TipoOperacion.ANULACION_CMP_PIN_INTERNET;
				}
				if (codigoEstado == 77) {// Recibida del MDB para proceso de Recarga || Recarga No Enviada al Proveedor
					long miliSegundos = ((new Date().getTime()) - fechaRecibidaColaPeticionRecarga.getTime());
					long segundos = miliSegundos / 1000;
					long minutos = segundos / 60;
					if (minutos <= 1) {
						tipoOperacion = TipoOperacion.COMPRAR_PIN_INTERNET; // Se envia una Recarga si ha esperado menos
																			// de 1 minuto
					} else {
						banDevolverRecarga = true;
					}
				}
				if (codigoEstado == 79) {// Recibida del MDB para proceso de Anulacion
					long miliSegundos = ((new Date().getTime()) - fechaRecibidaColaPeticionAnulacion.getTime());
					long segundos = miliSegundos / 1000;
					long minutos = segundos / 60;
					if (minutos <= 1) {
						tipoOperacion = TipoOperacion.ANULACION_CMP_PIN_INTERNET;
					} else {
						banDevolverAnulacion = true;
					}
				}

				trn.setCodigoMovimiento(codigoMovimiento); // 1
				trn.setNumTransaccion(numeroTransaccion);// 2
				trn.setFecha(fecha); // 3
				trn.setDescripcion(descripcio); // 4
				trn.setCodigoCliente(codigoCliente);// 5
				trn.setCodigoUsuario(codigoUsuario); // 6
				trn.setValorContable(importe); // 7
				trn.setReferenciaCliente(nicNpe); // 8
				trn.setMvmParametroXmlIn(parametroXmlIn); // 9
				trn.setIdentificadorHost(identificadorClienteHost); // 10
				trn.setReferenciaProveedor(referenciaProveedor);// 11
				trn.setMvmParametroXmlOut(parametroXmlOut); // 12
				trn.setCodigoBodega(codigoBodega); // 13
				trn.setCodigoProveedorProducto(numeroProveedor); // 14
				trn.setCodigoEstado(codigoEstado); // 15
				trn.setCodigoProceso(codigoProceso);// 16
				trn.setTransacId(trannsacId); // 17

				trn.setFechaColaPeticion(fechaRecibidaColaPeticionRecarga);// 18
				trn.setFechaEnvioRecarga(fechaEnvioRecarga);// 19
				trn.setFechaRespuestaRecarga(fechaRespuestaRecarga);// 20
				trn.setFechaColaPeticionCompraPinInternet(fechaRecibidaColaPeticionRecarga);
				trn.setFechaEnvioCompraPinInternet(fechaEnvioRecarga);
				trn.setFechaRespuestaCompraPinInternet(fechaRespuestaRecarga);

				trn.setFechaColaPeticionAnulacion(fechaRecibidaColaPeticionAnulacion); // 24
				trn.setFechaEnvioAnulacion(fechaEnvioAnulacion);// 25
				trn.setFechaRespuestaAnulacion(fechaRespuestaAnulacion);// 26
				trn.setFechaColaPeticionAnulacionCompraPinInternet(fechaRecibidaColaPeticionAnulacion); // 24
				trn.setFechaEnvioAnulacionCompraPinInternet(fechaEnvioAnulacion);// 25
				trn.setFechaRespuestaAnulacionCompraPinInternet(fechaRespuestaAnulacion);// 26

				trn.setReferenciaProveedor(descripcionProveedor); // 28
				trn.setTipoOperacion(tipoOperacion);

				if (banDevolverRecarga) {
					devolverRecarga.add(trn);
					banDevolverRecarga = false;
				} else if (banDevolverAnulacion) {
					devolverAnulacion.add(trn);
					banDevolverAnulacion = false;
				} else {
					retorno.add(trn);
				}

			}
			for (int i = 0; i < devolverRecarga.size(); i++) {
				TransaccionFullCarga tran = (TransaccionFullCarga) devolverRecarga.get(i);
				System.out.println("devolviendo Recarga ... : " + tran.getNumTransaccion());
				transaccionNoEnviadaRecarga(tran);
			}

			for (int j = 0; j < devolverAnulacion.size(); j++) {

				TransaccionFullCarga tra = (TransaccionFullCarga) devolverAnulacion.get(j);
				System.out.println("devolviendo Anulacion ... : " + tra.getNumTransaccion());
				transaccionNoEnviadaAnulacion(tra);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return retorno;

	}

	@Override
	public Map<Long, Producto> listarProductosInteragua() {
		Map<Long, Producto> hm = null;
		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			hm = this.odatos.consultarProductos(Conn);
		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return hm;
	}

	public Map<Long, DatosConfiguracion> listarConfiguracion() {

		Map<Long, DatosConfiguracion> hm = null;
		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			hm = this.odatos.consultarConfiguracion(Conn);
		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return hm;

	}

	public Map<Long, DatosConfiguracionWsdl> listarConfiguracionWsdl() {

		Map<Long, DatosConfiguracionWsdl> hm = null;
		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			hm = this.odatos.consultarConfiguracionWsdl(Conn);
		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return hm;

	}

	public List<Productos> listarEmpresaProductos() {
		List<Productos> localP = null;
		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			localP = this.odatos.getProductosProovedor(Conn);
		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return localP;
	}

	public List<EmpresaClienteRoll> listarEmpresaClienteRoll() {
		List<EmpresaClienteRoll> hm = null;
		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			hm = this.odatos.consultarEmpresaClienteRoll(Conn);
		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return hm;

	}

	private void responderCola(TransaccionFullCarga trn) throws JMSException {

		Long codigoMovimiento = trn.getCodigoMovimiento();
		Long numTransaccion = trn.getNumTransaccion();
		java.util.Date fecha = trn.getFecha();
		String descripcion = trn.getDescripcion(); // trn.getDescripcionErrorRsp
		Long codigoCliente = trn.getCodigoCliente();
		Long codigoUsuario = trn.getCodigoUsuario();
		BigDecimal valorContable = trn.getValorContable();
		String referenciaCliente = trn.getReferenciaCliente();
		String mvmParametroXmlIn = trn.getMvmParametroXmlIn();
		String identificadorHost = trn.getIdentificadorHost();
		String referenciaProveedor = trn.getReferenciaProveedor();
		String mvmParametroXmlOut = trn.getMvmParametroXmlOut();
		Long codigoBodega = trn.getCodigoBodega();
		Long codigoProveedorProducto = trn.getCodigoProveedorProducto();
		Long estadoFK = trn.getEstadoFK();
		int codigoEstado = trn.getCodigoEstado();
		trn.setCodigoEstadoConexion((long) codigoEstado);
		Long codigoEstadoConexion = trn.getCodigoEstadoConexion();
		String idMensajeProveedor = trn.getCodigoRetorno();
		// log.info("idMensajeProveedor = "+idMensajeProveedor);
		QueueConnection conn = null;
		QueueSession session = null;
		try {
//			conn=this.respuestaFactory.createQueueConnection();
//			session = conn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);			
			CltMovimiento mvmNuevo = new CltMovimiento();
			mvmNuevo.setMvmCodigo(codigoMovimiento);
			mvmNuevo.setMvmNumTransaccion(numTransaccion);
			mvmNuevo.setMvmFecha(fecha);
			mvmNuevo.setMvmDescripcion(descripcion);
			mvmNuevo.setMensajeProveedor(descripcion);
			mvmNuevo.setIdMensajeProveedor(idMensajeProveedor);
			mvmNuevo.setMvmClienteFk(codigoCliente);
			mvmNuevo.setMvmCodigoUsuario(codigoUsuario);
			mvmNuevo.setMvmValorContable(valorContable);
			mvmNuevo.setMvmReferencia(referenciaCliente);
			mvmNuevo.setMvmParametroXmlIn(mvmParametroXmlIn);
			mvmNuevo.setMvmIdentificadorHost(identificadorHost);
			mvmNuevo.setMvmParametroXmlOut(mvmParametroXmlOut);
			mvmNuevo.setMvmBodegaFk(codigoBodega);
			mvmNuevo.setMvmProveedorProductoFk(codigoProveedorProducto);
			mvmNuevo.setMvmEstadoFk(estadoFK);
			mvmNuevo.setCodigoEstadoConexion(codigoEstadoConexion);

			log.info("XmlIn devuelto: " + mvmNuevo.getMvmParametroXmlIn());
			log.info("MvmParametroXmlOut: " + mvmNuevo.getMvmParametroXmlOut());

			log.info("CODIGO ESTADO: " + codigoEstado);

			if (codigoEstado == 1) // Recarga en proceso
			{
				descripcion = "Recarga en Proceso-AdminFullCarga-responderCola: llamada a consultarRecarga de ServicioConeccion | No. transaccion :"
						+ trn.getNumTransaccion() + " No. Serie : " + trn.getReferenciaCliente();
				AdminFullCargaBean.log.info(trn + descripcion);
				ServicioConeccion sc = RegistroServicios.registroInstance()
						.getServicioConeccion(codigoProveedorProducto);
				// Utilitaria.analizarTransaccionNoReversada(trn);
				sc.anularCompraPinInternet(trn);
				return;
			} else if (codigoEstado == 2) // Recarga Realizada
			{
				RegistroServicios registro;
				registro = RegistroServicios.registroInstance();

				ServicioFullCarga servicioInteragua = (ServicioFullCarga) registro
						.getServicioConeccion(codigoProveedorProducto);
				VariablesFullCargaLocal varInteragua = servicioInteragua.getVariables();
				Map<Long, Producto> listaProductos = varInteragua.getListaProductos();

				Producto prd = listaProductos.get(codigoProveedorProducto);

				TicketPrint ticketPrint = new TicketPrint(prd, trn.getParametrosXmlInTransaccion(), trn);

				ArrayList ticketComprobante = ticketPrint.armarTicketImpresion(trn.getValorContable(),
						new BigDecimal(0.12));

				TicketsBeanRemote adminCliente = (TicketsBeanRemote) this.context
						.lookup("java:global/conexionBase2.0/TicketsBean!com.st.integracion.ticket.TicketsBeanRemote");

				String ticketComprobanteStr = adminCliente.procesarTickets(ticketComprobante, 40);

				log.info("Ticket Comprobante recarga =" + ticketComprobanteStr);

				mvmNuevo.setMvmReferenciaProveedor(referenciaProveedor);
				descripcion = "Recarga Realizada AdminFullCarga-responderCola: Enviando recarga a la cola de respuesta| No. transaccion :"
						+ trn.getNumTransaccion() + " No. Serie : " + trn.getReferenciaCliente();
				AdminFullCargaBean.log.info(trn + descripcion);

				String xmlOutRespuesta = "<TransaccionRealizada>" + "<tickets>" + "<ticketTransaccion>"
						+ ticketComprobanteStr + "</ticketTransaccion>" + "<ticketFactura></ticketFactura>"
						+ "<comprobanteFacturaElectronica></comprobanteFacturaElectronica>" + "</tickets>"
						+ "</TransaccionRealizada>";

				mvmNuevo.setMvmParametroXmlOut(xmlOutRespuesta);

			} else if (codigoEstado == 3) // Recarga No Realizada
			{
				mvmNuevo.setMvmReferenciaProveedor("NAK");
				descripcion = "Recarga No Realizada-AdminInteragua-responderCola: Enviando Tarjetas_vendidas a la cola de respuesta | No. transaccion :"
						+ trn.getNumTransaccion() + " No. Serie : " + trn.getReferenciaCliente();
				AdminFullCargaBean.log.info(trn + descripcion);
			}

			log.info("CltMovimiento  a responder =" + mvmNuevo.toString());

			enviarMensajeWebServiceRespuesta(mvmNuevo);

			log.info("idMensajeProveedor = " + idMensajeProveedor);

			log.info("AdminInteragua-responderCola: mensaje enviado a la cola de respuesta reloaded " + trn);
		} catch (Exception e) {
			this.context.setRollbackOnly();
			e.printStackTrace();
		} finally {
			try {
				if (session != null)
					session.close();
				if (conn != null)
					conn.close();
			} catch (JMSException e) {
			}
		}
	}

	private int enviarMensajeWebServiceRespuesta(CltMovimiento clt) {
		int retorno = 0;

		try {
			// usuario: pipepulido; Clave: 789321

			ConsumoServiceService consumoServiceSer = new ConsumoServiceService();
			ConsumoService consumo = consumoServiceSer.getConsumoServicePort();
			BindingProvider bp = (BindingProvider) consumo;
			bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, "pipepulido");
			bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, "789321");
			UtilitariaWs utilWs = new UtilitariaWs();
			String msgNew = utilWs.obtenerXmlCltMovimiento(clt);
			// String msgNew = xml.replaceAll("&lt;", "<");
			// msgNew = msgNew.replaceAll("&gt;", ">");

			log.info("transformacion de xml: " + msgNew);
			consumo.respuestaXmlTransaccion(msgNew);
			log.info("Transcion no realizada respondida a web metodo respuestaXmlTransaccion: " + msgNew);
			retorno = 1;
		} catch (Exception e) {
			log.info("Excepcion en metodo enviarMensajeWebServiceRespuesta: " + e.getMessage());
			log.info("Transaccion no se p�do enviar al web metodo respuestaXmlTransaccion" + clt.toString());
		}
		return retorno;
	}

	@Override
	protected void transaccionEnviadaRecarga(Transaccion tx) throws TransaccionException {
		Connection Conn = null;
		String descripcion = null;
		TransaccionFullCarga trn = (TransaccionFullCarga) tx;
		descripcion = "AdminFullCargaBean-TransaccionEnviadaRecarga: Inicio";
		AdminFullCargaBean.log.info(trn + descripcion);
		try {
			Date fechaEnvioRecarga = trn.getFechaEnvioRecarga();
			int codigoEstado = 1; // Recarga en proceso
			int codigoProceso = 1; // Peticion Recarga
			trn.setCodigoEstado(codigoEstado);
			trn.setCodigoProceso(codigoProceso);

			Long codigoMovimiento = trn.getCodigoMovimiento();
			Long numTransaccion = trn.getNumTransaccion();
			Long transacId = trn.getTransacId();
			String numeroTransaccion = trn.getNumeroTransaccion();
			BigDecimal valor = trn.getValorContable();
			String fechaConciliacionStr = Utilitaria.formatFecha(new Date(), "dd-MM-yyyy");

			Conn = postgresTrbXADS.getConnection();

			this.odatos.insertarTransaccionalDetalleEnvioRecarga(Conn, codigoMovimiento, numTransaccion, transacId,
					numeroTransaccion, fechaEnvioRecarga, valor, fechaConciliacionStr);

			this.odatos.actualizarTransaccionalEnvioRecarga(Conn, fechaEnvioRecarga, codigoEstado, codigoProceso,
					transacId, numTransaccion);

			descripcion = "AdminFullCargaBean-TransaccionEnviadaRecarga: actualizando transaccional e insertando detalle";
			AdminFullCargaBean.log.info(trn + descripcion);
		} catch (SQLException e) {
			descripcion = "SQLException-AdminFullCargaBean-TransaccionEnviadaRecarga: actualizando transaccional e insertando detalle";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			context.setRollbackOnly();
			throw new TransaccionException();
		} catch (Exception e) {
			descripcion = "Exception-FullCargaBean-TransaccionEnviadaRecarga: actualizando transaccional e insertando detalle";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			this.context.setRollbackOnly();
			throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	protected void transaccionEnviadaAnulacion(Transaccion tx) throws TransaccionException {
	}

	@Override
	protected void transaccionEnviadaCompraPin(Transaccion tx) throws TransaccionException {
	}

	@Override
	protected Transaccion respuestaRecibidaRecarga(Transaccion tx) throws TransaccionException {
		String descripcion = null;
		TransaccionFullCarga trn = (TransaccionFullCarga) tx;
		Connection Conn = null;
		descripcion = "AdminFullCargaBean-respuestaRecibidaRecarga: Inicio";
		AdminFullCargaBean.log.info(trn + descripcion);
		try {

			trn = this.utl.procesarRespuesta(trn);

			Conn = postgresTrbXADS.getConnection();

			String mensajeProveedor = trn.getCodigoRetorno();

			int codigoEstado = trn.getCodigoEstado();
			int codigoProceso = trn.getCodigoProceso();
			Date fechaRespuestaRecarga = trn.getFechaRespuestaRecarga();
			String referenciaProveedor = trn.getReferenciaProveedor();
			String codigoRetorno = trn.getCodigoRetorno();
			String mensajeRetorno = trn.getMensajeRetorno();
			Long codigoMovimiento = trn.getCodigoMovimiento();
			Long numeroTransaccion = trn.getNumTransaccion();
			Long transacId = trn.getTransacId();
			String tramaEnvio = trn.getTramaTxRequerimiento(), tramaResp = trn.getTramaTxRespuesta(),
					tramaTipoPrd = trn.getTipoProductoStr();
			String etapaTransaccion = "TX";

			odatos.actualizarTransaccionalRespuesta(Conn, codigoEstado, codigoProceso, fechaRespuestaRecarga,
					referenciaProveedor, mensajeRetorno, numeroTransaccion, codigoMovimiento, transacId, codigoRetorno);

			odatos.actualizarTransaccionalDetalleRespuesta(Conn, numeroTransaccion, codigoMovimiento, transacId,
					codigoRetorno, mensajeRetorno, tramaEnvio, tramaResp, tramaTipoPrd);

			descripcion = "AdminFullCargaBean-respuestaRecibidaRecarga; actualizando transaccional y actualizando detalle | Codigo Movimiento: "
					+ trn.getCodigoMovimiento() + " Numero Serie : " + trn.getReferenciaCliente();
			AdminFullCargaBean.log.info(trn + descripcion);
			responderCola(trn);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			descripcion = "Exception-AdminFullCargaBean-respuestaRecibidaRecarga: actualizando transaccional y actualizando detalle";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			this.context.setRollbackOnly();
			throw new TransaccionException(e);
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return trn;
	}

	@Override
	protected Transaccion respuestaRecibidaCompraPin(Transaccion tx) throws TransaccionException {
		TransaccionFullCarga trn = (TransaccionFullCarga) tx;
		return trn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.rx.integracion.beans.AdminTransaccionBean#respuestaRecibidaAnulacion(com.
	 * rx.integracion.dto.Transaccion)
	 */
	@Override
	protected Transaccion respuestaRecibidaAnulacion(Transaccion tx) throws TransaccionException {

		String descripcion = null;
		TransaccionFullCarga trn = (TransaccionFullCarga) tx;
		Connection Conn = null;
		descripcion = "AdminFullCargaBean-respuestaRecibidaAnulacion:Inicio";
		AdminFullCargaBean.log.info(trn + descripcion);
		try {
			trn = this.utl.procesarRespuesta(trn);
			Date fechaRespuestaAnulacion = trn.getFechaRespuestaAnulacion();
			int codigoEstado = trn.getCodigoEstado();
			int codigoProceso = trn.getCodigoProceso();
			Long codigoMovimiento = trn.getCodigoMovimiento();
			Long numeroTransaccion = trn.getNumTransaccion();
			Long transacId = trn.getTransacId();
			String referenciaProveedor = trn.getReferenciaProveedor();
			String codigoErrorRsp = null;// =trn.getCodigoErrorRsp();
			String descripcionErrorRsp = null;// =trn.getDescripcionErrorRsp();

			// String fechaContable = trn.getFechaContable();
			String codigoRetorno = trn.getCodigoRetorno();
			String mensajeRetorno = trn.getMensajeRetorno();
			/*
			 * String fechaContableResponse = trn.getFechaContableResponse(); String
			 * ordenBanco = trn.getOrdenBanco(); String secuencial = trn.getSecuencial();
			 * String referencia = trn.getReferencia(); String banderaNotaVenta =
			 * trn.getBanderaNotaVenta(); String rucBanco = trn.getRucBanco(); String
			 * localidad = trn.getLocalidad(); String puntoVenta = trn.getPuntoVenta();
			 * String secuencialDetalle = trn.getSecuencialDetalle(); String autorizacionSRI
			 * = trn.getAutorizacionSRI(); String direccionBanco = trn.getDireccionBanco();
			 * String numeroTransaccionComision = trn.getNumeroTransaccionComision(); String
			 * fechaVigenciaSri = trn.getFechaVigenciaSri(); String fechaInicioSri =
			 * trn.getFechaInicioSri(); String fechaResolucionSri =
			 * trn.getFechaResolucionSri();
			 */
			String etapaTransaccion = "TX_REVERSO";

			Conn = postgresTrbXADS.getConnection();
			/*
			 * odatos.actualizarTransaccionalDetalleRespuesta(Conn, rucBanco, localidad,
			 * puntoVenta, secuencial, autorizacionSRI, direccionBanco, fechaVigenciaSri,
			 * fechaInicioSRI, fechaResolucionSRI, fecha_Proceso, codigo_Retorno,
			 * descripcion_Retorno, secuencial_Banco, secuencial_Banco_Comision,
			 * codigo_Tercero, fechaRespuestaAnulacion, numeroTransaccion, codigoMovimiento,
			 * transacId);
			 */
			/*
			 * odatos.actualizarTransaccionalDetalleRespuesta(Conn, fechaContableResponse,
			 * codigoRetorno, mensajeRetorno, ordenBanco, secuencial, referencia,
			 * banderaNotaVenta, rucBanco, localidad, puntoVenta, secuencialDetalle,
			 * autorizacionSRI, direccionBanco, numeroTransaccionComision, fechaVigenciaSri,
			 * fechaInicioSri, fechaResolucionSri, numeroTransaccion, codigoMovimiento,
			 * transacId,etapaTransaccion);
			 */
			/*
			 * odatos.actualizarTransaccionalRespuesta(Conn, codigoEstado, codigoProceso,
			 * fechaRespuestaAnulacion, referenciaProveedor, descripcionErrorRsp,
			 * numeroTransaccion, codigoMovimiento, transacId);
			 */

			descripcion = "AdminFullCarga-respuestaRecibidaAnulacion: actualizando transaccional y actualizando detalle | Codigo Movimiento: "
					+ trn.getCodigoMovimiento() + " Numero Serie : " + trn.getReferenciaCliente();
			AdminFullCargaBean.log.info(trn + descripcion);
			responderCola(trn);

		} catch (SQLException e) {
			descripcion = "SQLException-AdminFullCarga-respuestaRecibidaAnulacion: actualizando transaccional y actualizando detalle";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			context.setRollbackOnly();
			throw new TransaccionException(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			descripcion = "Exception-AdminFullCarga-respuestaRecibidaAnulacion: actualizando transaccional y actualizando detalle";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			this.context.setRollbackOnly();
			throw new TransaccionException(e);
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return trn;

	}

	@Override
	protected Transaccion respuestaRecibidaConsultaAnulacion(Transaccion tx) throws TransaccionException {
		throw new UnsupportedOperationException("metodo respuestaRecibidaConsultaAnulacion no esta soportado");
	}

	// @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Override
	protected void transaccionEnviadaConsultaRecarga(Transaccion tx) throws TransaccionException {
	}

	@Override
	protected void transaccionEnviadaConsultaCompraPin(Transaccion tx) throws TransaccionException {
	}

	@Override
	protected Transaccion respuestaRecibidaConsultaRecarga(Transaccion tx) throws TransaccionException {
		TransaccionFullCarga trn = (TransaccionFullCarga) tx;
		return trn;
	}

	@Override
	protected Transaccion respuestaRecibidaConsultaCompraPin(Transaccion tx) throws TransaccionException {

		TransaccionFullCarga trn = (TransaccionFullCarga) tx;
		return trn;
	}

	@Override
	protected void transaccionNoEnviadaConsultaRecarga(Transaccion tx) throws TransaccionException {
		TransaccionFullCarga trn = (TransaccionFullCarga) tx;
		String descripcion = "AdminFullCargaBean-transaccionNoEnviadaConsultaRecarga: Inicio; no se hace nada en este metodo";
		AdminFullCargaBean.log.info(trn + descripcion);
		int codigoEstado = 1; // Recarga en Proceso
		int codigoProceso = 1; // Peticion Recarga
		trn.setCodigoEstado(codigoEstado);
		trn.setCodigoProceso(codigoProceso);
		try {
			responderCola(trn);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void transaccionNoEnviadaConsultaCompraPin(Transaccion tx) throws TransaccionException {
		TransaccionFullCarga trn = (TransaccionFullCarga) tx;
		String descripcion = "AdminFullCargaBean-transaccionNoEnviadaConsultaRecarga: Inicio; no se hace nada en este metodo";
		AdminFullCargaBean.log.info(trn + descripcion);
		int codigoEstado = 57; // COMPRA PIN EN PROCESO
		int codigoProceso = 49; // Peticion COMPRA PIN
		trn.setCodigoEstado(codigoEstado);
		trn.setCodigoProceso(codigoProceso);
		try {
			responderCola(trn);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected Transaccion respuestaRecibidaActivacion(Transaccion tx) throws TransaccionException {
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaConsultaActivacion(Transaccion tx) throws TransaccionException {
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaConsultaDesactivacion(Transaccion tx) throws TransaccionException {
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaDesactivacion(Transaccion tx) throws TransaccionException {
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaAnulacionCompraPin(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaConsultaAnulacionCompraPin(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaConsultaCompraPinInternet(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaConsultaAnulacionCompraPinInternet(Transaccion tx)
			throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaCompraPinInternacional(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaConsultaCompraPinInternacional(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaAnulacionCompraPinInternacional(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaConsultaAnulacionCompraPinInternacional(Transaccion tx)
			throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaRecargaInternacionalTelefono(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaConsultaRecargaInternacionalTelefono(Transaccion tx)
			throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaAnulacionRecargaInternacionalTelefono(Transaccion tx)
			throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaConsultaAnulacionRecargaInternacionalTelefono(Transaccion tx)
			throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaRecargaInternacionalTarjeta(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaConsultaRecargaInternacionalTarjeta(Transaccion tx)
			throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaAnulacionRecargaInternacionalTarjeta(Transaccion tx)
			throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected Transaccion respuestaRecibidaConsultaAnulacionRecargaInternacionalTarjeta(Transaccion tx)
			throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaConsultaAnulacion(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaActivacion(Transaccion tx) throws TransaccionException {
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaConsultaActivacion(Transaccion tx) throws TransaccionException {
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaConsultaDesactivacion(Transaccion tx) throws TransaccionException {
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaDesactivacion(Transaccion tx) throws TransaccionException {
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaAnulacionCompraPin(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaConsultaAnulacionCompraPin(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaConsultaCompraPinInternet(Transaccion tx) throws TransaccionException {

		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaConsultaAnulacionCompraPinInternet(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaCompraPinInternacional(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaConsultaCompraPinInternacional(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaAnulacionCompraPinInternacional(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaConsultaAnulacionCompraPinInternacional(Transaccion tx)
			throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaRecargaInternacionalTelefono(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaConsultaRecargaInternacionalTelefono(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaAnulacionRecargaInternacionalTelefono(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaConsultaAnulacionRecargaInternacionalTelefono(Transaccion tx)
			throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaRecargaInternacionalTarjeta(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaConsultaRecargaInternacionalTarjeta(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaAnulacionRecargaInternacionalTarjeta(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionEnviadaConsultaAnulacionRecargaInternacionalTarjeta(Transaccion tx)
			throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaAnulacion(Transaccion tx) throws TransaccionException {
		java.sql.Connection Conn = null;
		String descripcion = null;
		TransaccionFullCarga trn = (TransaccionFullCarga) tx;
		descripcion = "Metodo: transaccionNoEnviadaAnulacion; Inicio";
		AdminFullCargaBean.log.info(trn + descripcion);
		try {/*
				 * int codigoEstado = 0; int codigoProceso = 0;
				 * if(trn.getTipoServicioEasycash().equals(TipoServicioEasyCash.TIEMPO_AIRE)) {
				 * codigoEstado = 1; codigoProceso = 1;
				 * 
				 * } else if(trn.getTipoServicioEasycash().equals(TipoServicioEasyCash.RECAUDO))
				 * { codigoEstado = 71; codigoProceso = 61; }
				 * 
				 * Long codigoEstadoConexion = (long)codigoEstado ; String
				 * referenciaProveedor=Messages.getString("configuracion.transaccionFallida");
				 * trn.setReferenciaProveedor(referenciaProveedor);
				 * trn.setCodigoEstado(codigoEstado); trn.setCodigoProceso(codigoProceso);
				 * trn.setCodigoEstadoConexion(codigoEstadoConexion);
				 * if(trn.getDescripcionErrorRsp()==null)
				 * trn.setDescripcionErrorRsp("Anulacion no se pudo Enviar Transaccion"); Long
				 * numTransaccion = trn.getNumTransaccion(); Long codigoMovimiento =
				 * trn.getCodigoMovimiento();
				 * 
				 * Conn = this.postgresInteraguaXADS.getConnection();
				 * 
				 * 
				 * 
				 * this.odatos.actualizarTransaccionalEstadoPorNumeroticketFinal(Conn,
				 * codigoEstado, codigoProceso, referenciaProveedor, numTransaccion,
				 * codigoMovimiento, trn.getDescripcionErrorRsp()); descripcion =
				 * "Metodo: transaccionNoEnviadaRecarga; actualizando transaccional campo estado"
				 * ;
				 * 
				 * AdminInteraguaBean.log.info(trn + descripcion); responderCola(trn); } catch
				 * (SQLException e) { // TODO Auto-generated catch block descripcion =
				 * "Metodo: transaccionNoEnviadaRecarga; SQLException: actualizando transaccional campo estado"
				 * ; AdminInteraguaBean.log.info(trn+descripcion); e.printStackTrace();
				 * this.context.setRollbackOnly(); throw new TransaccionException();
				 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			descripcion = "Metodo: transaccionNoEnviadaRecarga; Exception: actualizando transaccional campo estado";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			this.context.setRollbackOnly();
			throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void transaccionNoEnviadaConsultaAnulacion(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		/* No se hace nada */
		String descripcion = null;
		TransaccionFullCarga trn = (TransaccionFullCarga) tx;
		descripcion = "Metodo: transaccionNoEnviadaConsultaAnulacion; Inicio; no se hace nada en este metodo";
		AdminFullCargaBean.log.info(trn + descripcion);
	}

	@Override
	protected void transaccionNoEnviadaActivacion(Transaccion tx) throws TransaccionException {
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaConsultaActivacion(Transaccion tx) throws TransaccionException {
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaConsultaDesactivacion(Transaccion tx) throws TransaccionException {
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaDesactivacion(Transaccion tx) throws TransaccionException {
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaAnulacionCompraPin(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaConsultaAnulacionCompraPin(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaConsultaCompraPinInternet(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaAnulacionCompraPinInternet(Transaccion tx) throws TransaccionException {
		java.sql.Connection Conn = null;

		String descripcion = null;
		// ddss

		TransaccionFullCarga trn = (TransaccionFullCarga) tx;
		descripcion = "AdminTrbBean-transaccionNoEnviadaAnulacionCompraPinInternet: Inicio";
		int codigoEstado = 0; // Recarga no enviada
		int codigoProceso = 0; // Peticion Recarga

		AdminFullCargaBean.log.info(trn + descripcion);
		try {
			codigoEstado = 76; // Anulacion Compra Pin Internet no realizada
			codigoProceso = 66; // Respuesta Anulacion Compra Pin Internet

			Long codigoEstadoConexion = (long) codigoEstado;
			// String referenciaProveedor="NAK"; //no se actualiza la referencia
			// trn.setReferenciaProveedor(referenciaProveedor);
			trn.setCodigoEstado(codigoEstado);
			trn.setCodigoProceso(codigoProceso);
			trn.setCodigoEstadoConexion(codigoEstadoConexion);
			if (trn.getMensajeRetorno() == null)
				trn.setMensajeRetorno("No se pudo Enviar Anulacion Transaccion");
			Long numTransaccion = trn.getNumTransaccion();
			Long codigoMovimiento = trn.getCodigoMovimiento();

			Conn = this.postgresTrbXADS.getConnection();

			this.odatos.actualizarTransaccionAnulacionEstadoPorNumeroticketFinal(Conn, codigoEstado, codigoProceso,
					numTransaccion, codigoMovimiento, trn.getMensajeRetorno());

			/*
			 * this.odatos.insertarTramaTransaccionRequerimiento(Conn,
			 * trn.getNumTransaccion(), codigoMovimiento, trn.getTransacId(),
			 * "NO SE ENVIO TRANSACCION");
			 * this.odatos.insertarTramaTransaccionRespuesta(Conn, trn.getNumTransaccion(),
			 * codigoMovimiento, trn.getTransacId(), "NO SE ENVIO TRANSACCION");
			 */
			descripcion = "AdminTrbBean-transaccionNoEnviadaAnulacionCompraPinInternet: actualizando transaccional campo estado";
			AdminFullCargaBean.log.info(trn + descripcion);
			responderCola(trn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			descripcion = "SQLException-AdminTrbBean-transaccionNoEnviadaAnulacionCompraPinInternet: actualizando transaccional campo estado";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			this.context.setRollbackOnly();
			throw new TransaccionException();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			descripcion = "Exception-AdminTrbBean-transaccionNoEnviadaAnulacionCompraPinInternet: actualizando transaccional campo estado";
			AdminFullCargaBean.log.info(trn + descripcion);
			e.printStackTrace();
			this.context.setRollbackOnly();
			throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void transaccionNoEnviadaConsultaAnulacionCompraPinInternet(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void transaccionNoEnviadaCompraPinInternacional(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaConsultaCompraPinInternacional(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaAnulacionCompraPinInternacional(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaConsultaAnulacionCompraPinInternacional(Transaccion tx)
			throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaRecargaInternacionalTarjeta(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaConsultaRecargaInternacionalTarjeta(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaAnulacionRecargaInternacionalTarjeta(Transaccion tx)
			throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaConsultaAnulacionRecargaInternacionalTarjeta(Transaccion tx)
			throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaRecargaInternacionalTelefono(Transaccion tx) throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaConsultaRecargaInternacionalTelefono(Transaccion tx)
			throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaAnulacionRecargaInternacionalTelefono(Transaccion tx)
			throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	@Override
	protected void transaccionNoEnviadaConsultaAnulacionRecargaInternacionalTelefono(Transaccion tx)
			throws TransaccionException {
		// TODO Auto-generated method stub
		throw this.metodoNoSoportado((TransaccionFullCarga) tx);
	}

	private TransaccionException metodoNoSoportado(TransaccionFullCarga tx) {
		log.info("metodo no soportado !!! " + tx);
		return new TransaccionException("metodo no soportado");
	}

	@Override
	public long[] listarProductos() {
		long[] proveedores = null;
		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			proveedores = this.odatos.consultarProductosProveedor(Conn);
		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return proveedores;
	}

	@Override
	public Map<Long, PropiedadesProveedorProducto> listarPropiedadesProductos() {
		Map<Long, PropiedadesProveedorProducto> propiedadesProveedores = null;
		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			propiedadesProveedores = this.odatos.consultarPropiedadesProductosProveedor(Conn);
		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return propiedadesProveedores;
	}

//	@Override
//	public CltMovimiento correccionRecibida(CltMovimiento mvm)
//	{
//	java.sql.Connection Conn = null;
//	try {
//		Conn = this.postgresInteraguaXADS.getConnection();
//	
//	log.info("Entro a Corregir transaccion");
//	SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
//			
//	if(mvm.getMvmEstadoFk()==3) //correccion transaccion exitosa
//	{ 	
//		mvm.setMensajeProveedor("Transaccion Exitosa");
//		
//		String referencia = dateFormat.format(new Date())+"-"+mvm.getMvmNumTransaccion();
//		mvm.setMvmReferenciaProveedor(referencia);
//		mvm.setCodigoEstadoConexion(2);
//		this.odatos.corregirTransaccion(Conn,72,62 ,referencia, "Transaccion Realizada", mvm.getMvmCodigo() ,mvm.getMvmNumTransaccion());
//	
//	}
//	else if(mvm.getMvmEstadoFk()==2) //correccion transaccion no realizada
//	{
//		mvm.setMensajeProveedor("Transaccion no Realizada");
//		mvm.setMvmReferenciaProveedor("NAK");
//		this.odatos.corregirTransaccion(Conn,73,62 ,"NAK", "Transaccion no Realizada", mvm.getMvmCodigo() ,mvm.getMvmNumTransaccion());
//		
//	}								
//	log.info("Salio a Corregir transaccion");
//	return mvm;
//}
// catch(SQLException e)
//    {
// 	   log.info((""));
//       context.setRollbackOnly();
//    }
//    catch(Exception e)
//    {
//       log.info((""));	            
//       context.setRollbackOnly();
//    }
//    finally{if(Conn != null){try{Conn.close();}catch(SQLException e){e.printStackTrace();}}}
//    
//    return mvm;
//	}

	@Override
	public Long obtenerNumTransacConsulta(Long codEmpresa) {
		// TODO: manejarlo bien
		Long transascId = 0L;

		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			transascId = this.odatos.obtenerNumConsulta(Conn, codEmpresa);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return transascId;
	}

	public Long actualizarNumTransacConsulta(Long num, Long codEmpresa) {
		// TODO: manejarlo bien
		Long transascId = num;

		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			transascId = this.odatos.actualizarNumConsulta(Conn, transascId, codEmpresa);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return transascId;
	}

	@Override
	public String realizarConsultaRecaudo(String xmlConsulta, Long codigoProveedorProducto, String referencia) {

		ConsultaFullCargaSt consultaBP = new ConsultaFullCargaSt();

		String resp = null;
		try {
			resp = consultaBP.realizarConsultaBancoPacificoSt(xmlConsulta, codigoProveedorProducto, referencia);
		} catch (Exception e) {

			ParametrosRespuesta params = new ParametrosRespuesta();
			params.setCodigoRespuestaST("-2");
			params.setDescripcionRespuestaST("Error interno en el Sistema");
			params.setDeuda("0");
			params.setTotal("0");
			params.setValorMaximo("");
			params.setValorSugerido("");
			params.setCargo("0");
			params.setCuenta("");
			params.setNombre("0");
			params.setValorMinimo("");

			XStream xstreamTem = new XStream();
			xstreamTem.alias("ParametrosRespuesta", ParametrosRespuesta.class);
			String xmlRespuesta = xstreamTem.toXML(params);

			resp = xmlRespuesta;
			e.printStackTrace();
			// TODO Auto-generated catch block
			log.info("Excepcion : " + e.getMessage());
		}
		// TODO Auto-generated method stub
		return resp;
	}

	@Override
	public Date obtenerUltimaFechaConciliacion() {

		Date fachaUltimaConciliacion = null;

		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			fachaUltimaConciliacion = this.odatos.consultarUltimaFechaConciliacion(Conn);
		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return fachaUltimaConciliacion;
	}

	@Override
	public boolean insertarRegistroDeConcilidacionBd(String fecha, String nombreArchivo, Long transaccionConsilidadas,
			Long transaccionNoConcilidada) {

		boolean result = false;

		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			result = this.odatos.insertarRegistroDeConcilidacion(Conn, fecha, nombreArchivo, transaccionConsilidadas,
					transaccionNoConcilidada);
		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	@Override
	public boolean borrarRegistroDeConcilidacionBd(String nombreArchivo) {

		boolean result = false;

		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			result = this.odatos.borrarRegistroDeConcilidacion(Conn, nombreArchivo);
		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	@Override
	public Map<Long, Empresa> obtenerMapEmpresa() {

		Map<Long, Empresa> mapEmpresa = new HashMap<Long, Empresa>();

		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			mapEmpresa = this.odatos.listaEmpresa(Conn);
		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return mapEmpresa;
	}

	@Override
	public ConfiguracionCorreo listarConfiguracionCorreo() {

		ConfiguracionCorreo configuracionCorreo = new ConfiguracionCorreo();

		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			configuracionCorreo = this.odatos.listarConfiguracionCorreo(Conn);
		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return configuracionCorreo;
	}

	@Override
	public void insertarTramaRequerimiento(String trama, long numeroTransaccion, long codigoMovimiento,
			long transacId) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ReversoAnulacionObj> listarTransaccionRevAnu(String fechaInicial, String fechaFinal,
			long codigoEstado) {
		List<ReversoAnulacionObj> listReverAnu = new ArrayList<ReversoAnulacionObj>();
		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			listReverAnu = this.anulacionDb.listaTransaccionReversoAnulacion(Conn, fechaInicial, fechaFinal,
					codigoEstado);

		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return listReverAnu;
	}

	@Override
	public TransaccionFullCarga consultarTransaccionPorNumTransaccion(long numeroTrn) {
		TransaccionFullCarga trn = null;
		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			trn = this.anulacionDb.consultarTransaccionalPorNumeroTx(Conn, numeroTrn);

		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return trn;
	}

	@Override
	public boolean actualizarRespuestaReverso(long estado, Date fechaRespuesta, long numTrn) {
		boolean rsp = false;
		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			rsp = this.anulacionDb.actualizarRespuestaTblReverso(Conn, estado, fechaRespuesta, numTrn);

		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return rsp;
	}

	@Override
	public boolean actualizarAAnulacionNoRealizada(TransaccionFullCarga trn) {
		boolean rsp = false;
		java.sql.Connection Conn = null;
		try {

			int codigoEstado = trn.getCodigoEstado();
			int codigoProceso = trn.getCodigoProceso();
			long transacId = trn.getTransacId();
			Long codigoMovimiento = trn.getCodigoMovimiento();
			Long numTransaccion = trn.getNumTransaccion();
			Date fechaRespuestaAnulacionPinInternet = new Date();
			String codigoRespuesta = trn.getCodigoRetorno();
			String descripcioError = trn.getMensajeRetorno();

			Conn = this.postgresTrbXADS.getConnection();

			odatos.actualizarAnulacionNoRealizada(Conn, codigoEstado, codigoProceso, transacId, codigoMovimiento,
					numTransaccion, fechaRespuestaAnulacionPinInternet, codigoRespuesta, descripcioError);

		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return rsp;
	}
	/*
	 * @Override public String
	 * consultarTramasTXPorNumeroTransaccionYCodProveedorProducto( Long
	 * codigoProveedorProducto, Long numeroTransaccion) { Connection Conn = null;
	 * List<String> tramas = null;
	 * log.info("codigo Proveedor Producto = "+codigoProveedorProducto); String
	 * retorno = null;
	 * 
	 * try { String txEnviada = "\nTrama Transaccion Enviada: \n"; String
	 * txRespuesta = "\nTrama Transaccion Respuesta: \n"; String txReversoEnviada =
	 * "\nTrama Reverso Enviada: \n"; String txReversoRespuesta =
	 * "\nTrama Reverso Respuesta: \n";
	 * 
	 * Conn = this.postgresInteraguaXADS.getConnection();
	 * 
	 * tramas = this.odatos.consultarTramasPorNumeroTransaccion(Conn,
	 * numeroTransaccion); for(String tmp: tramas){ log.info("trama = "+tmp); }
	 * if(tramas.size()==1){ retorno = tramas.get(0); }else if(tramas.size()>1){
	 * retorno = txEnviada+tramas.get(0)+txRespuesta+tramas.get(1);
	 * if(tramas.size()>2){ retorno =
	 * retorno+txReversoEnviada+tramas.get(2)+txReversoRespuesta+tramas.get(3); }
	 * }else{ retorno = "No existe Transaccion"; }
	 * 
	 * } catch (SQLException e) { log.
	 * info("AdminInteraguaBean - consultarTramasTXPorNumeroTransaccionYCodProveedorProducto - SQL Exception"
	 * ); // TODO Auto-generated catch block e.printStackTrace(); }catch (Exception
	 * e){ log.
	 * info("AdminInteraguaBean - consultarTramasTXPorNumeroTransaccionYCodProveedorProducto - Exception"
	 * ); e.printStackTrace(); }finally { if (Conn != null) { try { Conn.close(); }
	 * catch (SQLException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } } } return retorno; }
	 */

	@Override
	public long consultarEstadoPorNumTransaccion(long numeroTrn) {

		long estado = 0;
		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			estado = this.anulacionDb.consultarEstdoTxPorNumeroTrnTablaConexion(Conn, numeroTrn);

		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return estado;
	}

	@Override
	public boolean actualizarEnvioReverso(long estado, Date fechaEnvio, long numIntentos, long numTrn) {

		boolean rsp = false;
		java.sql.Connection Conn = null;
		try {
			Conn = this.postgresTrbXADS.getConnection();
			rsp = this.anulacionDb.actualizarEnvioRegistroTblReverso(Conn, estado, fechaEnvio, numIntentos, numTrn);

		} catch (SQLException e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} catch (Exception e) {
			e.printStackTrace();
			// throw new TransaccionException();
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return rsp;
	}

	@Override
	public void reponderColaTrnAnu(TransaccionFullCarga trn) throws JMSException {
		responderCola(trn);
	}

	private void addReverso(TransaccionFullCarga trn, Date fechaPeticion, long estado, Connection Conn) {
		long numeroPeticion = 1;
		long numeroIntentos = 0;
		long resp = 0;

		try {
			resp = this.anulacionDb.consultarTxPorNumeroTrnTablaReverso(Conn, trn.getNumTransaccion());
			if (resp == 0) {
				this.anulacionDb.addRegistroTblReverso(Conn, trn.getNumTransaccion(), estado, fechaPeticion,
						numeroIntentos, numeroPeticion, trn.getCodigoProveedorProducto(), trn.getValorContable(),
						trn.getParametrosInfoEmpresa(), trn.getParametrosXmlInTransaccion());
			} else
				this.anulacionDb.actualizarNumPeticionesTblReverso(Conn, trn.getNumTransaccion());
			// }
		} catch (Exception e) {
			log.info("Exception metodo addReverso :" + e.getMessage());
		} finally {
			if (Conn != null) {
				try {
					Conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					log.info("Exception metodo addReverso| e:" + e.getMessage());
				}
			}
		}
		return;
	}

	@Override
	public Map<Long, DatosConfiguracion> listarConfiguracionCodigoCliente() {
		// TODO Auto-generated method stub
		return null;
	}

}
