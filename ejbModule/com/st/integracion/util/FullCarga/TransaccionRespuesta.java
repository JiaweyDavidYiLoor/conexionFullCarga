package com.st.integracion.util.FullCarga;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", 
		propOrder = {
        "element"
        })
@XmlRootElement(name="elements")
public class TransaccionRespuesta {
	@XmlElement( required=true)
	private ElementosResponse element;

	public ElementosResponse getElement() {
		return element;
	}

	public void setElement(ElementosResponse element) {
		this.element = element;
	}
	
}
