package com.st.integracion.dto.FullCarga;

import java.math.BigDecimal;
import java.util.Date;

public class Transaccional {
	

	
	private Long codigoMovimiento;
	private Long numTransaccion;
	private Date fecha;
	private String  descripcion;
	private Long  numeroCliente;
	private Long  codigoUsuario;
	private BigDecimal  valorContable;
	private String  referenciaCliente;
	private String  parametroXmlIn;
	private String  identificadorHost;
	private String  referenciaProveedor;
	private String  parametroXmlOut;
	private Long   bodega;
	private Long  proveedorProducto;
	private int  estadoFk;
	private int  procesoFk;
	private Long  transacId;
	private Date fechaRecibidaColaPeticionRecarga;  
	private Date  fechaEnvioRecarga;
	private Date fechaRespuestaRecarga;
	private long  numeroReintentosRecargaRealizados;
	private Date  fechaReintentoRecargaEnvio;
	private Date  fechaReintentoRecargaRespuesta;
	private Date  fechaRecibidaColaPeticionAnulacion; 
	private Date  fechaEnvioAnulacion;
	private Date  fechaRespuestaAnulacion; 
	private long  numeroReintentosAnulacionRealizados;
	private Date  fechaReintentoAnulacionEnvio;
	private Date  fechaReintentoAnulacionRespuesta;
	private String  descripcionProveedor;
	public Long getCodigoMovimiento() {
		return codigoMovimiento;
	}
	public void setCodigoMovimiento(Long codigoMovimiento) {
		this.codigoMovimiento = codigoMovimiento;
	}
	public Long getNumTransaccion() {
		return numTransaccion;
	}
	public void setNumTransaccion(Long numTransaccion) {
		this.numTransaccion = numTransaccion;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Long getNumeroCliente() {
		return numeroCliente;
	}
	public void setNumeroCliente(Long numeroCliente) {
		this.numeroCliente = numeroCliente;
	}
	public Long getCodigoUsuario() {
		return codigoUsuario;
	}
	public void setCodigoUsuario(Long codigoUsuario) {
		this.codigoUsuario = codigoUsuario;
	}
	public BigDecimal getValorContable() {
		return valorContable;
	}
	public void setValorContable(BigDecimal valorContable) {
		this.valorContable = valorContable;
	}
	public String getReferenciaCliente() {
		return referenciaCliente;
	}
	public void setReferenciaCliente(String referenciaCliente) {
		this.referenciaCliente = referenciaCliente;
	}
	public String getParametroXmlIn() {
		return parametroXmlIn;
	}
	public void setParametroXmlIn(String parametroXmlIn) {
		this.parametroXmlIn = parametroXmlIn;
	}
	public String getIdentificadorHost() {
		return identificadorHost;
	}
	public void setIdentificadorHost(String identificadorHost) {
		this.identificadorHost = identificadorHost;
	}
	public String getReferenciaProveedor() {
		return referenciaProveedor;
	}
	public void setReferenciaProveedor(String referenciaProveedor) {
		this.referenciaProveedor = referenciaProveedor;
	}
	public String getParametroXmlOut() {
		return parametroXmlOut;
	}
	public void setParametroXmlOut(String parametroXmlOut) {
		this.parametroXmlOut = parametroXmlOut;
	}
	public Long getBodega() {
		return bodega;
	}
	public void setBodega(Long bodega) {
		this.bodega = bodega;
	}
	public Long getProveedorProducto() {
		return proveedorProducto;
	}
	public void setProveedorProducto(Long proveedorProducto) {
		this.proveedorProducto = proveedorProducto;
	}
	public int getEstadoFk() {
		return estadoFk;
	}
	public void setEstadoFk(int estadoFk) {
		this.estadoFk = estadoFk;
	}
	public int getProcesoFk() {
		return procesoFk;
	}
	public void setProcesoFk(int procesoFk) {
		this.procesoFk = procesoFk;
	}
	public Long getTransacId() {
		return transacId;
	}
	public void setTransacId(Long transacId) {
		this.transacId = transacId;
	}
	public Date getFechaRecibidaColaPeticionRecarga() {
		return fechaRecibidaColaPeticionRecarga;
	}
	public void setFechaRecibidaColaPeticionRecarga(
			Date fechaRecibidaColaPeticionRecarga) {
		this.fechaRecibidaColaPeticionRecarga = fechaRecibidaColaPeticionRecarga;
	}
	public Date getFechaEnvioRecarga() {
		return fechaEnvioRecarga;
	}
	public void setFechaEnvioRecarga(Date fechaEnvioRecarga) {
		this.fechaEnvioRecarga = fechaEnvioRecarga;
	}
	public Date getFechaRespuestaRecarga() {
		return fechaRespuestaRecarga;
	}
	public void setFechaRespuestaRecarga(Date fechaRespuestaRecarga) {
		this.fechaRespuestaRecarga = fechaRespuestaRecarga;
	}
	public long getNumeroReintentosRecargaRealizados() {
		return numeroReintentosRecargaRealizados;
	}
	public void setNumeroReintentosRecargaRealizados(
			long numeroReintentosRecargaRealizados) {
		this.numeroReintentosRecargaRealizados = numeroReintentosRecargaRealizados;
	}
	public Date getFechaReintentoRecargaEnvio() {
		return fechaReintentoRecargaEnvio;
	}
	public void setFechaReintentoRecargaEnvio(Date fechaReintentoRecargaEnvio) {
		this.fechaReintentoRecargaEnvio = fechaReintentoRecargaEnvio;
	}
	public Date getFechaReintentoRecargaRespuesta() {
		return fechaReintentoRecargaRespuesta;
	}
	public void setFechaReintentoRecargaRespuesta(
			Date fechaReintentoRecargaRespuesta) {
		this.fechaReintentoRecargaRespuesta = fechaReintentoRecargaRespuesta;
	}
	public Date getFechaRecibidaColaPeticionAnulacion() {
		return fechaRecibidaColaPeticionAnulacion;
	}
	public void setFechaRecibidaColaPeticionAnulacion(
			Date fechaRecibidaColaPeticionAnulacion) {
		this.fechaRecibidaColaPeticionAnulacion = fechaRecibidaColaPeticionAnulacion;
	}
	public Date getFechaEnvioAnulacion() {
		return fechaEnvioAnulacion;
	}
	public void setFechaEnvioAnulacion(Date fechaEnvioAnulacion) {
		this.fechaEnvioAnulacion = fechaEnvioAnulacion;
	}
	public Date getFechaRespuestaAnulacion() {
		return fechaRespuestaAnulacion;
	}
	public void setFechaRespuestaAnulacion(Date fechaRespuestaAnulacion) {
		this.fechaRespuestaAnulacion = fechaRespuestaAnulacion;
	}
	public long getNumeroReintentosAnulacionRealizados() {
		return numeroReintentosAnulacionRealizados;
	}
	public void setNumeroReintentosAnulacionRealizados(
			long numeroReintentosAnulacionRealizados) {
		this.numeroReintentosAnulacionRealizados = numeroReintentosAnulacionRealizados;
	}
	public Date getFechaReintentoAnulacionEnvio() {
		return fechaReintentoAnulacionEnvio;
	}
	public void setFechaReintentoAnulacionEnvio(Date fechaReintentoAnulacionEnvio) {
		this.fechaReintentoAnulacionEnvio = fechaReintentoAnulacionEnvio;
	}
	public Date getFechaReintentoAnulacionRespuesta() {
		return fechaReintentoAnulacionRespuesta;
	}
	public void setFechaReintentoAnulacionRespuesta(
			Date fechaReintentoAnulacionRespuesta) {
		this.fechaReintentoAnulacionRespuesta = fechaReintentoAnulacionRespuesta;
	}
	public String getDescripcionProveedor() {
		return descripcionProveedor;
	}
	public void setDescripcionProveedor(String descripcionProveedor) {
		this.descripcionProveedor = descripcionProveedor;
	}
	

	
	
}
