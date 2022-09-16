package com.st.integracion.ws.FullCarga;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", 
		propOrder = {
		"detallesCliente"
        })
@XmlRootElement(name="datosRecarga")
public class datosRecarga {
	@XmlElement( required=true)
	 protected detallesCliente detallesCliente;

	public detallesCliente getDetallesCliente() {
		return detallesCliente;
	}

	public void setDetallesCliente(detallesCliente detallesCliente) {
		this.detallesCliente = detallesCliente;
	}
}
