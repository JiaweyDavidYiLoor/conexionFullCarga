package com.st.integracion.beans.FullCarga;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;

import com.st.integracion.beans.AdminTransaccionLocal;
import com.st.integracion.dto.FullCarga.ConfiguracionCorreo;
import com.st.integracion.dto.FullCarga.DatosConfiguracion;
import com.st.integracion.dto.FullCarga.DatosConfiguracionWsdl;
import com.st.integracion.dto.FullCarga.Empresa;
import com.st.integracion.dto.FullCarga.EmpresaClienteRoll;
import com.st.integracion.dto.FullCarga.Producto;
import com.st.integracion.dto.FullCarga.Productos;
import com.st.integracion.dto.FullCarga.ReversoAnulacionObj;
import com.st.integracion.dto.FullCarga.TransaccionFullCarga;
import com.st.integracion.servicios.PropiedadesProveedorProducto;

public interface AdminFullCargaLocal extends AdminTransaccionLocal{

	public long ultimaTransacId();
	public Map<Long, Producto> listarProductosInteragua();
	public long[] listarProductos();
	public Map<Long, PropiedadesProveedorProducto> listarPropiedadesProductos();

	public Long actualizarNumTransacConsulta(Long num, Long codEmpresa);
	public Long obtenerNumTransacConsulta(Long codEmpresa) ;
		public boolean insertarRegistroDeConcilidacionBd(String fecha ,String nombreArchivo,Long transaccionConsilidadas,
			Long transaccionNoConcilidada) ;
	public boolean borrarRegistroDeConcilidacionBd(String nombreArchivo) ;
	
	public Map<Long, Empresa> obtenerMapEmpresa() ;

	public Map<Long,DatosConfiguracion>  listarConfiguracionCodigoCliente();
	public Date obtenerUltimaFechaConciliacion();
	 
	
	public void insertarTramaRequerimiento(String trama,long numeroTransaccion,long codigoMovimiento,long transacId);
	
	public ConfiguracionCorreo listarConfiguracionCorreo() ;
	
	public List<ReversoAnulacionObj> listarTransaccionRevAnu( String fechaInicial , String fechaFinal , long codigoEstado );
	 
	 public long consultarEstadoPorNumTransaccion(long numeroTrn );
	 public TransaccionFullCarga consultarTransaccionPorNumTransaccion(long numeroTrn );
	 public boolean actualizarRespuestaReverso(long estado, Date fechaRespuesta , long numTrn );
	 public boolean actualizarEnvioReverso( long estado, Date fechaEnvio, long numIntentos , long numTrn);
	 public void reponderColaTrnAnu(TransaccionFullCarga trn) throws JMSException;
	 public boolean actualizarAAnulacionNoRealizada(TransaccionFullCarga trn );
	 
	 public Map<Long,DatosConfiguracion>  listarConfiguracion();
	 public Map<Long,DatosConfiguracionWsdl>  listarConfiguracionWsdl();
	 public List<EmpresaClienteRoll>  listarEmpresaClienteRoll();
	 
	 public List<Productos> listarEmpresaProductos();
	 
}
