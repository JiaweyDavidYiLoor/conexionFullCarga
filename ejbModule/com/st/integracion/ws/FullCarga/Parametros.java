package com.st.integracion.ws.FullCarga;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", 
		propOrder = {
		"cedula",
		"cuenta", 
		"codigo",
		"ruc",
        "nombres", 
        "apellidos", 
        "fecha",
        "telefono",
        "pasaporte",
        "tarjeta",
        "numeroCliente",
        "referencia"
        })
@XmlRootElement(name="Parametros")
public class Parametros {
	@XmlElement(name="cedula" ,required=true)
	 protected String cedula;
	@XmlElement(required=true)
	 protected String cuenta;
	@XmlElement(required=true)
	 protected String codigo;
	 @XmlElement(required=true)
	 protected String ruc;
	 @XmlElement(required=true)
	 protected String nombres;
	 @XmlElement(required=true)
	 protected String apellidos;
	 @XmlElement(required=true)
	 protected String fecha;
	 @XmlElement(required=true)
	 protected String telefono;
	 @XmlElement(required=true)
	 protected String pasaporte;
	 @XmlElement(required=true)
	 protected String tarjeta;
	 @XmlElement(required=true)
	 protected String numeroCliente;
	 @XmlElement(required=true)
	 protected String referencia;
	 
	
	
	public String getCedula() {
		return cedula;
	}
	public void setCedula(String cedula) {
		this.cedula = cedula;
	}
	public String getCuenta() {
		return cuenta;
	}
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getRuc() {
		return ruc;
	}
	public void setRuc(String ruc) {
		this.ruc = ruc;
	}
	public String getNombres() {
		return nombres;
	}
	public  void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public  String getApellidos() {
		return apellidos;
	}
	public  void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public  String getFecha() {
		return fecha;
	}
	public  void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public  String getTelefono() {
		return telefono;
	}
	public  void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public  String getPasaporte() {
		return pasaporte;
	}
	public  void setPasaporte(String pasaporte) {
		this.pasaporte = pasaporte;
	}
	public  String getTarjeta() {
		return tarjeta;
	}
	public  void setTarjeta(String tarjeta) {
		this.tarjeta = tarjeta;
	}
	public  String getNumeroCliente() {
		return numeroCliente;
	}
	public  void setNumeroCliente(String numeroCliente) {
		this.numeroCliente = numeroCliente;
	}
	public  String getReferencia() {
		return referencia;
	}
	public  void setReferencia(String referencia) {
		this.referencia = referencia;
	}
}
