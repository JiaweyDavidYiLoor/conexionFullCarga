package com.st.integracion.ws.FullCarga;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", 
		propOrder = {
		"codigoProducto",
		"cuenta",
		"importe"
        })
@XmlRootElement(name="detallesCliente")
public class detallesCliente {
	@XmlElement( required=true)
	 protected String codigoProducto;
	@XmlElement( required=true)
	 protected String cuenta;
	@XmlElement( required=true)
	 protected String importe;
	public String getCodigoProducto() {
		return codigoProducto;
	}
	public void setCodigoProducto(String codigoProducto) {
		this.codigoProducto = codigoProducto;
	}
	public String getCuenta() {
		return cuenta;
	}
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	public String getImporte() {
		return importe;
	}
	public void setImporte(String importe) {
		this.importe = importe;
	}

}
