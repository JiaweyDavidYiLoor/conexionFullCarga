package com.st.integracion.util.FullCarga;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "responseCode", "refOperador", "referenciaSigma", "importe", "saldo", "parametros",
		"errMessage" })
@XmlRootElement(name = "response")
public class ElementosResponse {
	@XmlElement(required = false)
	protected String responseCode;
	@XmlElement(required = false)
	protected int refOperador;
	@XmlElement(required = false)
	protected int referenciaSigma;
	@XmlElement(required = false)
	protected Double importe;
	@XmlElement(required = false)
	protected String saldo;
	@XmlElement(required = false)
	protected List<ElementoResponse> parametros;

	@XmlElement(name = "errMessage")
	protected String errMessage;

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public int getRefOperador() {
		return refOperador;
	}

	public void setRefOperador(int refOperador) {
		this.refOperador = refOperador;
	}

	public int getReferenciaSigma() {
		return referenciaSigma;
	}

	public void setReferenciaSigma(int referenciaSigma) {
		this.referenciaSigma = referenciaSigma;
	}

	public Double getImporte() {
		return importe;
	}

	public void setImporte(Double importe) {
		this.importe = importe;
	}

	public String getSaldo() {
		return saldo;
	}

	public void setSaldo(String saldo) {
		this.saldo = saldo;
	}

	public List<ElementoResponse> getParametros() {
		if (parametros == null) {
			parametros = new ArrayList<ElementoResponse>();
		}
		return parametros;
	}

	public void setParametros(List<ElementoResponse> parametros) {
		this.parametros = parametros;
	}

	public String getErrMessage() {
		return errMessage;
	}

	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}

}
