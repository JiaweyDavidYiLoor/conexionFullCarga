package com.st.integracion.servicios.FullCarga;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

import com.st.integracion.beans.AdminTransaccionLocal;
import com.st.integracion.servicios.IServicioStFactory;
import com.st.integracion.servicios.PropiedadesProveedorProducto;
import com.st.integracion.servicios.ServicioSt;

@ApplicationScoped
@Named("ServicioFullCargaFactory")
public class ServicioFullCargaFactory implements IServicioStFactory  {
	private Map<Long, PropiedadesProveedorProducto> propiedadesProveedores;
	private long[] proveedores;
	private  @Inject  @Named("AdminFullCarga") AdminFullCarga adminTrb;
    private  @Inject  @Named("ServicioFullCarga") ServicioFullCarga servicioTrb; 
    private @Inject VariablesFullCargaLocal variables;

	private String adminBean;
	private static final Logger log=Logger.getLogger(ServicioFullCargaFactory.class);
	
	public ServicioFullCargaFactory() {}
	
	@Override
	@Produces @Preferred 
	public ServicioSt createServicioSt() throws NamingException {

		log.info("Registrando ServicioSt ");
		ServicioSt servicioSt = null;
		try
		{					
			adminTrb.start();
			servicioTrb.init();
			Context ctx=new InitialContext();
			AdminTransaccionLocal adminBean=(AdminTransaccionLocal) ctx.lookup("java:global/conexionFullCarga/AdminFullCargaBean!com.st.integracion.beans.FullCarga.AdminFullCargaLocal");
			Map<Long, PropiedadesProveedorProducto> propiedadesProductos = variables.getPropiedadesProductos();
			long[] proveedores = variables.getProveedores();
			servicioSt = new ServicioSt(propiedadesProductos, proveedores, adminBean, this.adminTrb, this.servicioTrb);		
		}catch (IllegalArgumentException e) {
			log.error("ServicioTrbFactory-createServicioSt() =" + e.getMessage());
		}
		catch(NamingException ex)
		{
		  log.error("NamingException createServicioSt ="+ex.getMessage());	
		}
		catch(Exception exc)
		{
			log.error("Exception createServicioSt ="+exc.getMessage());			
		}
		
		return servicioSt;	
	}
	
	public void finalizarScheler()
	{
		try {
			servicioTrb.finalizarScheler();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			 log.error("finalizarSchelert ="+e.getMessage());	
		}
		
	}
		
	public Map<Long, PropiedadesProveedorProducto> getPropiedadesProveedores() {
		return propiedadesProveedores;
	}
	public void setPropiedadesProveedores(
			Map<Long, PropiedadesProveedorProducto> propiedadesProveedores) {
		this.propiedadesProveedores = propiedadesProveedores;
	}
	public long[] getProveedores() {
		return proveedores;
	}
	public void setProveedores(long[] proveedores) {
		this.proveedores = proveedores;
	}
	public String getAdminBean() {
		return adminBean;
	}
	public void setAdminBean(String adminBean) {
		this.adminBean = adminBean;
	}

	public AdminFullCarga getAdminInteragua() {
		return adminTrb;
	}

	public void setAdminInteragua(AdminFullCarga adminInteragua) {
		this.adminTrb = adminInteragua;
	}

	public ServicioFullCarga getServicioInteragua() {
		return servicioTrb;
	}

	public void setServicioInteragua(ServicioFullCarga servicioInteragua) {
		this.servicioTrb = servicioInteragua;
	}
	
	

	
	
}
