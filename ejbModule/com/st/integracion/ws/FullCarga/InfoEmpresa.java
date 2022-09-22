package com.st.integracion.ws.FullCarga;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "rucEmpresa", "direccionEmpresa", "codigoEmpresa", "telefonoEmpresa", "nombreEmpresa",
		"codigoRol", "codigoCliente" })
@XmlRootElement(name = "InfoEmpresa")
public class InfoEmpresa {

	@XmlElement(name = "rucEmpresa", required = true)
	protected String rucEmpresa;
	@XmlElement(name = "direccionEmpresa", required = true)
	protected String direccionEmpresa;
	@XmlElement(name = "codigoEmpresa", required = true)
	protected Long codigoEmpresa;
	@XmlElement(name = "telefonoEmpresa", required = true)
	protected String telefonoEmpresa;
	@XmlElement(name = "nombreEmpresa", required = true)
	protected String nombreEmpresa;

	@XmlElement(name = "codigoRol", required = true)
	protected Long codigoRol;

	@XmlElement(name = "codigoCliente", required = true)
	protected Long codigoCliente;

	public String getRucEmpresa() {
		return rucEmpresa;
	}

	public void setRucEmpresa(String rucEmpresa) {
		this.rucEmpresa = rucEmpresa;
	}

	public String getDireccionEmpresa() {
		return direccionEmpresa;
	}

	public void setDireccionEmpresa(String direccionEmpresa) {
		this.direccionEmpresa = direccionEmpresa;
	}

	public Long getCodigoEmpresa() {
		return codigoEmpresa;
	}

	public void setCodigoEmpresa(Long codigoEmpresa) {
		this.codigoEmpresa = codigoEmpresa;
	}

	public String getTelefonoEmpresa() {
		return telefonoEmpresa;
	}

	public void setTelefonoEmpresa(String telefonoEmpresa) {
		this.telefonoEmpresa = telefonoEmpresa;
	}

	public String getNombreEmpresa() {
		return nombreEmpresa;
	}

	public void setNombreEmpresa(String nombreEmpresa) {
		this.nombreEmpresa = nombreEmpresa;
	}

	public Long getCodigoRol() {
		return codigoRol;
	}

	public void setCodigoRol(Long codigoRol) {
		this.codigoRol = codigoRol;
	}

	public Long getCodigoCliente() {
		return codigoCliente;
	}

	public void setCodigoCliente(Long codigoCliente) {
		this.codigoCliente = codigoCliente;
	}

	@Override
	public String toString() {
		return "InfoEmpresa [rucEmpresa=" + rucEmpresa + ", direccionEmpresa=" + direccionEmpresa + ", codigoEmpresa="
				+ codigoEmpresa + ", telefonoEmpresa=" + telefonoEmpresa + ", nombreEmpresa=" + nombreEmpresa
				+ ", codigoRol=" + codigoRol + ", codigoCliente=" + codigoCliente + "]";
	}

}
