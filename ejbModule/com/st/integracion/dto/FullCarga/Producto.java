package com.st.integracion.dto.FullCarga;

public class Producto {
	  
private Long codigo; 
private String descripcion; 
private Long estadoFk; 
private Long producto; 
private Long codigoEmpresa; 
private String descripcionEmpresa;			  
private String  entidadconciliacion; 
private String entidadRecaudo; 
private String puntoPago; 
private String	 programa; 
private String tipoReferencia; 
private String terminal; 
private int idfc; 
//private String	tipoProducto; 
private String formaPago;

	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Long getEstadoFk() {
		return estadoFk;
	}
	public void setEstadoFk(Long estadoFk) {
		this.estadoFk = estadoFk;
	}
	public Long getProducto() {
		return producto;
	}
	public void setProducto(Long producto) {
		this.producto = producto;
	}
	public String getDescripcionEmpresa() {
		return descripcionEmpresa;
	}
	public void setDescripcionEmpresa(String descripcionEmpresa) {
		this.descripcionEmpresa = descripcionEmpresa;
	}
	public Long getCodigoEmpresa() {
		return codigoEmpresa;
	}
	public void setCodigoEmpresa(Long codigoEmpresa) {
		this.codigoEmpresa = codigoEmpresa;
	}
	public String getEntidadconciliacion() {
		return entidadconciliacion;
	}
	public void setEntidadconciliacion(String entidadconciliacion) {
		this.entidadconciliacion = entidadconciliacion;
	}
	public String getEntidadRecaudo() {
		return entidadRecaudo;
	}
	public void setEntidadRecaudo(String entidadRecaudo) {
		this.entidadRecaudo = entidadRecaudo;
	}
	public String getPuntoPago() {
		return puntoPago;
	}
	public void setPuntoPago(String puntoPago) {
		this.puntoPago = puntoPago;
	}
	public String getPrograma() {
		return programa;
	}
	public void setPrograma(String programa) {
		this.programa = programa;
	}
	public String getTipoReferencia() {
		return tipoReferencia;
	}
	public void setTipoReferencia(String tipoReferencia) {
		this.tipoReferencia = tipoReferencia;
	}
	public String getTerminal() {
		return terminal;
	}
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}
	/*public String getTipoProducto() {
		return tipoProducto;
	}
	public void setTipoProducto(String tipoProducto) {
		this.tipoProducto = tipoProducto;
	}*/
	
	public String getFormaPago() {
		return formaPago;
	}
	public int getIdfc() {
		return idfc;
	}
	public void setIdfc(int idfc) {
		this.idfc = idfc;
	}
	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}
	@Override
	public String toString() {
		return "Producto [codigo=" + codigo + ", descripcion=" + descripcion + ", estadoFk=" + estadoFk + ", producto="
				+ producto + ", codigoEmpresa=" + codigoEmpresa + ", descripcionEmpresa=" + descripcionEmpresa
				+ ", entidadconciliacion=" + entidadconciliacion + ", entidadRecaudo=" + entidadRecaudo + ", puntoPago="
				+ puntoPago + ", programa=" + programa + ", tipoReferencia=" + tipoReferencia + ", terminal=" + terminal
				+ ", tipoProducto=" + /*tipoProducto +*/ ", formaPago=" + formaPago + "]";
	}
	
	
	
}
