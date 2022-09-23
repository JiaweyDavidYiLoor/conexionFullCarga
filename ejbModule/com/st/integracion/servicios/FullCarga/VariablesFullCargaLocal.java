package com.st.integracion.servicios.FullCarga;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import com.st.integracion.dto.FullCarga.ConfiguracionCorreo;
import com.st.integracion.dto.FullCarga.DatosConfiguracion;
import com.st.integracion.dto.FullCarga.DatosConfiguracionWsdl;
import com.st.integracion.dto.FullCarga.EmpresaClienteRoll;
import com.st.integracion.dto.FullCarga.Producto;
import com.st.integracion.dto.FullCarga.Productos;
import com.st.integracion.servicios.PropiedadesProveedorProducto;

@Local
public interface VariablesFullCargaLocal {
	public long[] getProveedores();
	public Map<Long, PropiedadesProveedorProducto> getPropiedadesProductos();
	public Map<Long, DatosConfiguracion> getDatosConfig();
	public Map<Long, Producto> getListaProductos();	
	public ConfiguracionCorreo getConfiguracionCorreo();
	public List<EmpresaClienteRoll> getListaEmpresaClienteRoll();
	public Map<Long, DatosConfiguracionWsdl> getDatosConfiguracionWsdl();
	public List<Productos> getListarEmpresaProductos();
	
}
