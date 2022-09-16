package com.st.integracion.servicios.FullCarga;

import java.util.List;
import java.util.Map;

import javax.inject.Named;

import com.st.integracion.dto.FullCarga.DatosConfiguracion;

;


@Named("VariablesClaroMXBean")
public interface VariablesFullCargaMXBean {
	
	//void setServicioSt(ServicioSt servicioSt);
	//Map<Long, PropiedadesProveedorProducto> getPropiedadesProductos();
	//Map<Long, Producto> getListaProductosInterno();
	//Map<Long, Producto> getListaProductos();
	//long[] getProveedores();
	void create() throws Exception;
	void start() throws Exception;
	void stop();
	void destroy();
	public long[] getProveedores();
	public Map<Long, DatosConfiguracion> getDatosConfig();
	public void setDatosConfig(Map<Long, DatosConfiguracion> datosConfig);
	//public Map<Long, PropiedadesProveedorProducto> getPropiedadesProductos();
	//public List<DatosConfiguracion> getDatosConfig();
	//public Map<Long, Empresa> getListaEmpresa();
	//public List<DatosConfiguracion> getDatosConfigCodigoCliente() ;
	//public Map<Long, DatosoConexionWsdl> getListaDatosConexionWsdl()
	
}
