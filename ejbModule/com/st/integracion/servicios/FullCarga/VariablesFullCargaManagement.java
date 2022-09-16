package com.st.integracion.servicios.FullCarga;

import java.util.List;
import java.util.Map;

import com.st.integracion.dto.FullCarga.DatosConfiguracion;
import com.st.integracion.dto.FullCarga.Empresa;
import com.st.integracion.dto.FullCarga.Producto;
import com.st.integracion.servicios.PropiedadesProveedorProducto;
import com.st.integracion.servicios.ServicioSt;


public interface VariablesFullCargaManagement {
	void setServicioSt(ServicioSt servicioSt);
	Map<Long, PropiedadesProveedorProducto> getPropiedadesProductos();
	Map<Long, Producto> getListaProductosInterno();
	Map<Long, Producto> getListaProductos();
	long[] getProveedores();
	void create() throws Exception;
	void start() throws Exception;
	void stop();
	void destroy();
	
	public Map<Long, DatosConfiguracion> getDatosConfig();
	public Map<Long, Empresa> getListaEmpresa();
	
	
	public Map<Long, DatosConfiguracion> getDatosConfigCodigoCliente();
	
}
