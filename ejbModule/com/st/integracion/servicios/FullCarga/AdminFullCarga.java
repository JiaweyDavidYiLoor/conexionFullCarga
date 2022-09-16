package com.st.integracion.servicios.FullCarga;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.ejb.DependsOn;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;

import com.google.common.collect.ImmutableMap;
import com.st.integracion.beans.FullCarga.AdminFullCargaLocal;
import com.st.integracion.network.FullCarga.ClaroClientPipelineFactory;
import com.st.integracion.servicios.AdminConeccionMBean;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.concurrent.Future;

@Named("AdminFullCarga")
@ApplicationScoped
@DependsOn(value = { "VariablesFullCarga"})
public class AdminFullCarga implements AdminConeccionMBean {
	
	private static final Logger log=Logger.getLogger(AdminFullCarga.class);
	private ImmutableMap<Long, Integer> mapVisor;
	private String url;
	private String adminTranStr;
	private AdminFullCargaLocal adminTran;
	private ServicioFullCarga servicio;
	private Bootstrap bootstrap;
	private ChannelGroup channelGroup;
	private String dsJndi;
	
	private Scheduler scheduler;
	private static final String SCHED_GROUP_CONCILIACION = "InteraguaConciliacion";
	
	
	public AdminFullCarga() {}
	
	@Override
	public void setMapVisor(Map<Long, Integer> mapVisor) {
		this.mapVisor=ImmutableMap.copyOf(mapVisor);
	}
	@Override
	public Map<Long, Integer> getMapVisor() {
		return mapVisor;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
	@Override
	public void start() throws Exception
	{
		log.info("iniciando servicio AdminEasyCash");
		this.bootstrap=new Bootstrap();
		SslContext sslCtx = null;
		//Agregar cuando la direccion es HTTPS
		sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();		
		ClaroClientPipelineFactory pipelineFactory=new ClaroClientPipelineFactory(sslCtx);
		EventLoopGroup group = new NioEventLoopGroup();
		bootstrap.group(group)
        .channel(NioSocketChannel.class)
        .handler(pipelineFactory);
		//this.bootstrap.setOption("tcpNoDelay", true);
		//this.bootstrap.setOption("keepAlive", true);
		//this.channelGroup=new DefaultChannelGroup("ct channels");
		
//		this.adminTran=(AdminInteraguaLocal) new InitialContext().lookup("java:global/conexion-interagua/AdminInteraguaBean!com.st.integracion.beans.interagua.AdminInteraguaLocal");
		
		/*List<Transaccion> pendientes=(List<Transaccion>) adminTran.listarTransaccionesPendientes();
		log.info(String.format("AdminEasyCash-start: se han retornado %d transacciones pendientes", pendientes.size()));
		Function<Transaccion, TransaccionInteragua> f=new Function<Transaccion, TransaccionInteragua>() {
			public TransaccionInteragua apply(Transaccion from) {
				return (TransaccionInteragua) from;
			};
		};
		List<TransaccionInteragua> listaPendientes=new ArrayList<TransaccionInteragua>();
		listaPendientes.addAll(Collections2.transform(pendientes, f));
		this.servicio.realizarPendientes(listaPendientes);*/
//		ConfiguracionConciliacionDeposito confConcilaicionDep= adminTran.obtenerConfiguracionConciliacionDeposito();
//		log.info(confConcilaicionDep.toString());
//		iniciarConciliacionQuartz(confConcilaicionDep.getConfiguracionQuartz()/*"0 32 22 * * ?"*/,1L);
		log.info("AdminEasyCash-start: servicio iniciado");		
	}
	@Override
	public void restart() throws Exception {
		this.stop();
		Thread.sleep(1000);
		this.start();
		this.detenerQuartz();
	}
	@Override
	public void stop() throws Exception {
		log.info("AdminEasyCash-stop: parando servicio");
		this.servicio.cancelarPendientes();
		this.servicio.cancelTimer();
		log.info("AdminEasyCash-stop: servicio parado");
		this.detenerQuartz();
	}

	public void setAdminTran(String adminTran) {
		this.adminTranStr = adminTran;
	}
	public void setServicio(ServicioFullCarga servicio) {
		this.servicio = servicio;
	}
	public ServicioFullCarga getServicio() {
		return servicio;
	}
	
//	public void setFactory(ClientSocketChannelFactory factory) {
//		this.factory = factory;
//	}
//	public ClientSocketChannelFactory getFactory() {
//		return factory;
//	}
	
	public String getHost()throws URISyntaxException {
		URI uri = new URI(this.url);
		String scheme = uri.getScheme() == null? "http" : uri.getScheme();
		String host = uri.getHost() == null? "localhost" : uri.getHost();
		if(!scheme.equals("http"))
			throw new URISyntaxException(this.url, "only http is supported");
		return host;
	}

	public int getPort()throws URISyntaxException {
		URI uri = new URI(this.url);
		String scheme = uri.getScheme() == null? "http" : uri.getScheme();
		int port = uri.getPort() == -1? 80 : uri.getPort();
		if(!scheme.equals("http"))
			throw new URISyntaxException(this.url, "only http is supported");
		return port;
	}

	public Channel getChannel(String host, int port) {
		//String host;int port;
		//try {host=this.getHost();port=this.getPort();} 
		//catch (URISyntaxException e) {return null;}
		//host=host;
		log.info("host = "+host);
		log.info("puerto = "+port);
		
		ChannelFuture f=this.bootstrap.connect(new InetSocketAddress(host, port));
		f.awaitUninterruptibly(3000L);
		
		Channel c=f.channel();
//		this.channelGroup.add(c);		 
		return c;//this.bootstrap.connect(new InetSocketAddress(host, port)).channel();
	}
	
	@Override
	public String getDsJndi() {
		return this.dsJndi;
	}

	@Override
	public void setDsJndi(String dsJndi) {
		this.dsJndi=dsJndi;
	}
	
	private int iniciarConciliacionQuartz(String horaInicioQuartz, long CodigoProveedorProducto)
	{
		/** CONFIGURACION DE QUARTZ */
		int retorno = -1;
		CronTriggerImpl ct = null;
		try 
		{ 
			ct =new CronTriggerImpl();
			ct.setName("CronTriggerConexionInteragua");
			ct.setGroup(SCHED_GROUP_CONCILIACION);
			ct.setCronExpression(horaInicioQuartz);			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		if(ct != null)
		{
			SchedulerFactory schedulerFactory=new StdSchedulerFactory();
			
			try 
			{
				scheduler = schedulerFactory.getScheduler();
				scheduler.start();
//				@SuppressWarnings("static-access")
				//JobDetail jd= JobBuilder.newJob(ConciliacionQuartsDeposito.class).withIdentity("myjobConexionInteragua",SCHED_GROUP_CONCILIACION).build();				
				//jd.getJobDataMap().put("codigoProveedorProducto", String.valueOf(CodigoProveedorProducto));
				//scheduler.scheduleJob(jd, ct);
				retorno = 0;
	 		} 
			catch (SchedulerException e)
			{
				log.info("Exception :"+e.getMessage());
			}
		}
		return retorno;
	
}

public int detenerQuartz(){
	  int retorno=0;
	  
	   //SchedulerFactory sf=new StdSchedulerFactory();
	   log.info("deteniendo Quartz" + SCHED_GROUP_CONCILIACION);
	   try {
	    scheduler.getListenerManager().removeTriggerListener("CronTriggerConexionInteragua");
	    scheduler.getListenerManager().removeJobListener("myjobConexionInteragua");
	    //sched1.removeSchedulerListener(sched1);
	        
	    scheduler.shutdown();
	    	    
	   } catch (SchedulerException e) {
	    log.info("Exception detenerQuartz1:"+e.getMessage());
	    // TODO Auto-generated catch block
	    retorno = -1;
	   }
	    
	   log.info("Quartz detenido"); 
	  return retorno;
	 
	 }
}
