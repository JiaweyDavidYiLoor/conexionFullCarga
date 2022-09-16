package com.st.integracion.servicios.FullCarga;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Named;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;


import com.st.integracion.beans.AdminTransaccionLocal;
import com.st.integracion.beans.FullCarga.AdminFullCargaLocal;
import com.st.integracion.dto.FullCarga.ConfiguracionCorreo;
import com.st.integracion.dto.FullCarga.DatosConfiguracion;
import com.st.integracion.dto.FullCarga.DatosConfiguracionWsdl;
import com.st.integracion.dto.FullCarga.Empresa;
import com.st.integracion.dto.FullCarga.EmpresaClienteRoll;
import com.st.integracion.dto.FullCarga.Producto;
import com.st.integracion.dto.FullCarga.Productos;
import com.st.integracion.servicios.AdminConeccionMBean;
import com.st.integracion.servicios.PropiedadesProveedorProducto;
import com.st.integracion.servicios.ServicioConeccion;
import com.st.integracion.servicios.ServicioSt;

@Singleton
@Startup
@Named("VariablesFullCarga")
@DependsOn(value = { "AdminFullCargaBean" }) 
public class VariablesFullCarga implements VariablesFullCargaLocal, VariablesFullCargaMXBean
{
	private static final Logger log = Logger.getLogger(VariablesFullCarga.class);
	private static String   MBEAN_NAME = "com.st.integracion:type= VariablesFullCarga";
	@EJB(mappedName="java:global/conexionFullCarga/AdminFullCargaBean!com.st.integracion.beans.FullCarga.AdminFullCargaLocal")
	private AdminFullCargaLocal aPul; 
	private Map<Long, PropiedadesProveedorProducto> propiedadesProductos;
	private long[] proveedores;
	private ServicioSt servicioSt;
	 
	private Map<Long,DatosConfiguracion> datosConfig = new HashMap<Long, DatosConfiguracion>();
	private Map<Long,DatosConfiguracionWsdl> datosConfiguracionWsdl = new HashMap<Long, DatosConfiguracionWsdl>();
	private List<EmpresaClienteRoll> listaEmpresaClienteRoll = new ArrayList<EmpresaClienteRoll>();
	private List<Productos> listarEmpresaProductos;
	
	private Map<Long, Producto> listaProductosInterno;
	private Map<Long, Producto> listaProductos;
	private Map<Long, Empresa>  listaEmpresa;
    private ConfiguracionCorreo configuracionCorreo;
 
	public VariablesFullCarga() {}
	
	@PostConstruct
	public void init()
	{	 		 		 
	 	log.info("Iniciando VariablesFullCarga");	 		 	
	 	try {
			start() ;
		} catch (Exception e) {
			e.printStackTrace();}	 	
	 	registrarMbean();	
	}
	
	private void registrarMbean(){
		MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
		try {
			ObjectName objectName = new ObjectName(MBEAN_NAME);
			if (mbeanServer.isRegistered(objectName)){
				log.info(this.getClass().getName() + " registrado llamando a unregister");
				unregister();
			}
			mbeanServer.registerMBean(this, objectName);
		}
		catch (JMException ex) {
			log.info("register mbean error", ex);
		}
	}
	
	public  void unregister() {
		try {
			MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
			mbeanServer.unregisterMBean(new ObjectName(MBEAN_NAME));
		} catch (JMException e)  {
			throw new IllegalArgumentException(MBEAN_NAME, e);
		}
	}

	@Override
	public void create() throws Exception {}

	@Override
	public void destroy() {}

	@Override
	public void start() throws Exception {
		
		long []proveedores = aPul.listarProductos();
		this.setProveedores(proveedores);

		Map<Long, PropiedadesProveedorProducto> propiedadesProductos = aPul.listarPropiedadesProductos();
		this.setPropiedadesProductos(propiedadesProductos);
		
		//List<ClienteHorarioExtendido> clintHorExt = aPul.listarClientesHorarioExtendido();
		//this.setClientesHorExt(clintHorExt);
		
		Map<Long,DatosConfiguracion> datosConf = aPul.listarConfiguracion();
		this.setDatosConfig(datosConf);
		
		Map<Long,DatosConfiguracionWsdl> datosConfiguracionWsdl = aPul.listarConfiguracionWsdl();
		this.setDatosConfiguracionWsdl(datosConfiguracionWsdl);
		
		List<EmpresaClienteRoll> listaEmpresaCliRoll = aPul.listarEmpresaClienteRoll();
		this.setListaEmpresaClienteRoll(listaEmpresaCliRoll);
		
		
		//Map<Long, Producto> listaInterno=aPul.listarProductosInternoBancoPacifico();
		//this.setListaProductosInterno(listaInterno);
		
		Map<Long, Producto> listaProductoBancoPacifico=aPul.listarProductosInteragua();
		this.setListaProductos(listaProductoBancoPacifico);
		
	    ConfiguracionCorreo cfc = aPul.listarConfiguracionCorreo();
	    this.setConfiguracionCorreo(cfc);
	    
	    List<Productos> listarEmpresaProductos=aPul.listarEmpresaProductos();
	    this.setListarEmpresaProductos(listarEmpresaProductos);
	    

		//Map<Long,Empresa> listaEmpresaTem = aPul.obtenerMapEmpresa();
		//this.setListaEmpresa(listaEmpresaTem);
		
		//List<TarjetaBines> listaTarjetaBines = aPul.obtenerListaBines();
		//this.setListaTarjetaBines(listaTarjetaBines);
				
		//List<AreaCompaniaCinergia>listaAreaCompaniaTmp = aPul.obtenerListaAreaCompaniaCinergia();
		//this.setListaAreaCompaniaCinergia(listaAreaCompaniaTmp); 
		
		//ConfiguracionConciliacionDeposito confConciliacionDep = aPul.obtenerConfiguracionConciliacionDeposito();
		//this.setConfiguracionConciliacionDep(confConciliacionDep);
				
		/*log.info("Lista de Clientes para Horario Extendido");
		for(int i=0; i< clientesHorExt.size(); i++)
		{
			log.info("Rango["+i+"] Clienteinicio:"+clientesHorExt.get(i).getClienteInicial()+"  Clientefinal:"+clientesHorExt.get(i).getClienteFinal());
		}
		String[]muestra=new String[proveedores.length];
		for (int i = 0; i < muestra.length; i++) {
			muestra[i]=Long.toString(proveedores[i]);
		}*/
		log.info("VariablesEasyCash-start: servicio iniciado");
	}

	@Override
	public void stop() {
		log.info("VariablesEasyCash-stop: servicio detenido");
	}
	@Override
	public long[] getProveedores() {
		return proveedores;
	}
	
	public void setProveedores(long[] proveedores) {
		this.proveedores = proveedores;
	}
//	@Override
	public Map<Long, PropiedadesProveedorProducto> getPropiedadesProductos() {
		return propiedadesProductos;
	}

	public void setPropiedadesProductos(
			Map<Long, PropiedadesProveedorProducto> propiedadesProductos) {
		this.propiedadesProductos = propiedadesProductos;
	}
	
	
	
	public ServicioSt getServicioSt(int action) 
	{
		AdminTransaccionLocal adminTran=this.servicioSt.getAdminTransaccion();
		AdminConeccionMBean adminConeccion=this.servicioSt.getAdminConeccion();
		ServicioConeccion servicioConeccion=this.servicioSt.getServicioConeccion();
		
		long []proveedores = null;
		Map<Long, PropiedadesProveedorProducto> propiedadesProductos = null;
		if(action==0)
		{
			proveedores = this.getProveedores();
			propiedadesProductos = this.getPropiedadesProductos();
			this.setProveedores(null);
			this.setPropiedadesProductos(null);
		}
		else
		{
			proveedores =  aPul.listarProductos();
			propiedadesProductos = aPul.listarPropiedadesProductos();
			this.setProveedores(proveedores);
			this.setPropiedadesProductos(propiedadesProductos);
		}
		//ServicioSt servicioStOne = new ServicioSt(proveedores, adminTran, adminConeccion, servicioConeccion);
		ServicioSt servicioStOne = new ServicioSt(propiedadesProductos, proveedores, adminTran, adminConeccion, servicioConeccion);
		return servicioStOne;
	}
	
	public void setServicioSt(ServicioSt servicioSt) {
		this.servicioSt = servicioSt;
	}

//    @Override
	public Map<Long, Producto> getListaProductosInterno() {
		return listaProductosInterno;
	}

	public void setListaProductosInterno(Map<Long, Producto> listaProductosInterno) {
		this.listaProductosInterno = listaProductosInterno;
	}

//	@Override
	public Map<Long, Producto> getListaProductos() {
		return listaProductos;
	}

	public void setListaProductos(Map<Long, Producto> listaProductos) {
		this.listaProductos = listaProductos;
	}
		
//	@Override
	 public Map<Long, DatosConfiguracion> getDatosConfig() {
		return datosConfig;
	}

	public void setDatosConfig(Map<Long, DatosConfiguracion> datosConfig) {
		this.datosConfig = datosConfig;
	}
	
//

//	@Override
	public Map<Long, Empresa> getListaEmpresa() {
		return listaEmpresa;
	}

	
	public void setListaEmpresa(Map<Long, Empresa> listaEmpresa) {
		this.listaEmpresa = listaEmpresa;
	}

	public ConfiguracionCorreo getConfiguracionCorreo() {
		return configuracionCorreo;
	}

	public void setConfiguracionCorreo(ConfiguracionCorreo configuracionCorreo) {
		this.configuracionCorreo = configuracionCorreo;
	}

	public Map<Long, DatosConfiguracionWsdl> getDatosConfiguracionWsdl() {
		return datosConfiguracionWsdl;
	}

	public void setDatosConfiguracionWsdl(Map<Long, DatosConfiguracionWsdl> datosConfiguracionWsdl) {
		this.datosConfiguracionWsdl = datosConfiguracionWsdl;
	}

	public List<EmpresaClienteRoll> getListaEmpresaClienteRoll() {
		return listaEmpresaClienteRoll;
	}

	public void setListaEmpresaClienteRoll(List<EmpresaClienteRoll> listaEmpresaClienteRoll) {
		this.listaEmpresaClienteRoll = listaEmpresaClienteRoll;
	}

	public List<Productos> getListarEmpresaProductos() {
		return listarEmpresaProductos;
	}

	public void setListarEmpresaProductos(List<Productos> listarEmpresaProductos) {
		this.listarEmpresaProductos = listarEmpresaProductos;
	}
	

}
