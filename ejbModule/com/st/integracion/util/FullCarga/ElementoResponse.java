package com.st.integracion.util.FullCarga;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", 
		propOrder = {
		"indice",
		"valor"
        })
@XmlRootElement(name="parametros")
public class ElementoResponse {
	@XmlElement( required=false)
	private int indice;
	@XmlElement( required=false)
	private String valor;
	public int getIndice() {
		return indice;
	}
	public void setIndice(int indice) {
		this.indice = indice;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	
}
