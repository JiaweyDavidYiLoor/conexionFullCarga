package com.st.integracion.ws.FullCarga;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;




@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", 
		propOrder = {
		"campoConsulta"		
        })
@XmlRootElement(name="CamposConsulta")
public class CamposConsulta {
	
	@XmlElement(required=true)		
	protected List<CampoConsulta> campoConsulta = new ArrayList<CampoConsulta>();

	public List<CampoConsulta> getCampoConsulta() {
		if(campoConsulta == null)
			campoConsulta = new ArrayList<CampoConsulta>();
		return campoConsulta;
	}		
}
