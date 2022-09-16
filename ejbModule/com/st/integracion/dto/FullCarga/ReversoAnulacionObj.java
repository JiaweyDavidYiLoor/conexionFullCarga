package com.st.integracion.dto.FullCarga;

import java.math.BigDecimal;
import java.util.Date;

public class ReversoAnulacionObj {
	
  private long codigo; 
  private long numTransaccion; 
  private long estado; 
  private Date fechaPeticion; 
  private Date fechaEnvio; 
  private Date fechaRespuesta; 
  private long numerosIntentos; 
  private long numeroPeticiones;
  private long codigoProvProd;
  private BigDecimal valorTrn;
  private String xmlParamInfoEmpresa;
  private String xmlParamInTransaccion;
  
  
	public long getCodigo() {
		return codigo;
	}
	public void setCodigo(long codigo) {
		this.codigo = codigo;
	}
	public long getNumTransaccion() {
		return numTransaccion;
	}
	public void setNumTransaccion(long numTransaccion) {
		this.numTransaccion = numTransaccion;
	}
	public long getEstado() {
		return estado;
	}
	public void setEstado(long estado) {
		this.estado = estado;
	}
	public Date getFechaPeticion() {
		return fechaPeticion;
	}
	public void setFechaPeticion(Date fechaPeticion) {
		this.fechaPeticion = fechaPeticion;
	}
	public Date getFechaEnvio() {
		return fechaEnvio;
	}
	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}
	public Date getFechaRespuesta() {
		return fechaRespuesta;
	}
	public void setFechaRespuesta(Date fechaRespuesta) {
		this.fechaRespuesta = fechaRespuesta;
	}
	public long getNumerosIntentos() {
		return numerosIntentos;
	}
	public void setNumerosIntentos(long numerosIntentos) {
		this.numerosIntentos = numerosIntentos;
	}
	public long getNumeroPeticiones() {
		return numeroPeticiones;
	}
	public void setNumeroPeticiones(long numeroPeticiones) {
		this.numeroPeticiones = numeroPeticiones;
	}
	public long getCodigoProvProd() {
		return codigoProvProd;
	}
	public void setCodigoProvProd(long codigoProvProd) {
		this.codigoProvProd = codigoProvProd;
	}
	public BigDecimal getValorTrn() {
		return valorTrn;
	}
	public void setValorTrn(BigDecimal valorTrn) {
		this.valorTrn = valorTrn;
	}
	public String getXmlParamInfoEmpresa() {
		return xmlParamInfoEmpresa;
	}
	public void setXmlParamInfoEmpresa(String xmlParamInfoEmpresa) {
		this.xmlParamInfoEmpresa = xmlParamInfoEmpresa;
	}
	public String getXmlParamInTransaccion() {
		return xmlParamInTransaccion;
	}
	public void setXmlParamInTransaccion(String xmlParamInTransaccion) {
		this.xmlParamInTransaccion = xmlParamInTransaccion;
	}
	
	@Override
	public String toString() {
		return "ReversoAnulacionObj [codigo=" + codigo + ", numTransaccion="
				+ numTransaccion + ", estado=" + estado + ", fechaPeticion="
				+ fechaPeticion + ", fechaEnvio=" + fechaEnvio
				+ ", fechaRespuesta=" + fechaRespuesta + ", numerosIntentos="
				+ numerosIntentos + ", numeroPeticiones=" + numeroPeticiones
				+ ", codigoProvProd=" + codigoProvProd + ", valorTrn="
				+ valorTrn + ", xmlParamInfoEmpresa=" + xmlParamInfoEmpresa
				+ ", xmlParamInTransaccion=" + xmlParamInTransaccion + "]";
	}
			
}
