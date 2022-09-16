package com.st.integracion.util.FullCarga;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", 
		propOrder = {
		"tipoIdentificacion",
		"identificacion", 
		"nombres",
		"apellidos",
        "correoElectronico", 
        "tipoPersona"
        })
@XmlRootElement(name="PagoTransaccion")

public class PagoTransaccion 
{
	
	@XmlElement( required=true)
	protected String tipoIdentificacion;
	@XmlElement( required=true)
	protected String identificacion;
	@XmlElement( required=true)
	protected String nombres;
	@XmlElement( required=true)
	protected String apellidos;
	@XmlElement( required=true)
	protected String correoElectronico;
	@XmlElement( required=true)
	protected String tipoPersona;
	
	
	public String getTipoIdentificacion() {
		return tipoIdentificacion;
	}
	public void setTipoIdentificacion(String tipoIdentificacion) {
		this.tipoIdentificacion = tipoIdentificacion;
	}
	public String getIdentificacion() {
		return identificacion;
	}
	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public String getCorreoElectronico() {
		return correoElectronico;
	}
	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}
	public String getTipoPersona() {
		return tipoPersona;
	}
	public void setTipoPersona(String tipoPersona) {
		this.tipoPersona = tipoPersona;
	}
	
	

}
