package com.st.integracion.util.FullCarga;

import java.io.Serializable;



public  class Configuracion implements Serializable {
	
	public static final long serialVersionUID = -5838320691933115657L;
	private String transaccionFallida=Messages.getString("configuracion.transaccionFallida");
	private String transaccionAnulada=Messages.getString("configuracion.transaccionAnulada");
	private String numeroMaximoConsultas=Messages.getString("configuracion.numeroMaximoConsultas");
	private String timeoutBase=Messages.getString("configuracion.timeoutBase");
	private String codErrorGeneral=Messages.getString("configuracion.codErrorGeneral");
	private String desErrorGeneral=Messages.getString("configuracion.desErrorGeneral");
	private String desTimeoutBase= Messages.getString("configuracion.desTimeoutBase");
	private String porcentajeComision=Messages.getString("configuracion.porcentajeComision");
	private String urlWebServiceConsulta=Messages.getString("configuracion.urlWebServiceConsulta");
	
	
	
	public String getTransaccionFallida() {
		return transaccionFallida;
	}
	public void setTransaccionFallida(String transaccionFallida) {
		this.transaccionFallida = transaccionFallida;
	}
	public String getTransaccionAnulada() {
		return transaccionAnulada;
	}
	public void setTransaccionAnulada(String transaccionAnulada) {
		this.transaccionAnulada = transaccionAnulada;
	}
	public String getNumeroMaximoConsultas() {
		return numeroMaximoConsultas;
	}
	public void setNumeroMaximoConsultas(String numeroMaximoConsultas) {
		this.numeroMaximoConsultas = numeroMaximoConsultas;
	}
	public String getTimeoutBase() {
		return timeoutBase;
	}
	public void setTimeoutBase(String timeoutBase) {
		this.timeoutBase = timeoutBase;
	}
	public String getCodErrorGeneral() {
		return codErrorGeneral;
	}
	public void setCodErrorGeneral(String codErrorGeneral) {
		this.codErrorGeneral = codErrorGeneral;
	}
	public String getDesErrorGeneral() {
		return desErrorGeneral;
	}
	public void setDesErrorGeneral(String desErrorGeneral) {
		this.desErrorGeneral = desErrorGeneral;
	}
	public String getDesTimeoutBase() {
		return desTimeoutBase;
	}
	public void setDesTimeoutBase(String desTimeoutBase) {
		this.desTimeoutBase = desTimeoutBase;
	}
	public String getPorcentajeComision() {
		return porcentajeComision;
	}
	public void setPorcentajeComision(String porcentajeComision) {
		this.porcentajeComision = porcentajeComision;
	}
	public String getUrlWebServiceConsulta() {
		return urlWebServiceConsulta;
	}
	public void setUrlWebServiceConsulta(String urlWebServiceConsulta) {
		this.urlWebServiceConsulta = urlWebServiceConsulta;
	}
	
	
	
	
}
