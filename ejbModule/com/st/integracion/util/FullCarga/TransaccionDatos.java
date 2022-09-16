package com.st.integracion.util.FullCarga;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", 
		propOrder = {
		"numero_telefono",
		"valor", 
		"id_institucion",
		"id_canal",
        "fecha_contable", 
        "fecha_trx",
        "id_pago_banco",
        "id_terminal",
        "valor_recibido"
        })
@XmlRootElement(name="PagoAsp")
public class TransaccionDatos {
	@XmlElement( required=true)
	protected String numero_telefono;
	@XmlElement( required=true)
	protected String valor;
	@XmlElement( required=true)
	protected String id_institucion;
	@XmlElement( required=true)
	protected String id_canal;
	@XmlElement( required=true)
	protected String fecha_contable;
	@XmlElement( required=true)
	protected String fecha_trx;
	@XmlElement( required=true)
	protected String id_pago_banco;
	@XmlElement( required=true)
	protected String id_terminal;
	@XmlElement( required=true)
	protected String valor_recibido;
	public String getNumero_telefono() {
		return numero_telefono;
	}
	public void setNumero_telefono(String numero_telefono) {
		this.numero_telefono = numero_telefono;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getId_institucion() {
		return id_institucion;
	}
	public void setId_institucion(String id_institucion) {
		this.id_institucion = id_institucion;
	}
	public String getId_canal() {
		return id_canal;
	}
	public void setId_canal(String id_canal) {
		this.id_canal = id_canal;
	}
	public String getFecha_contable() {
		return fecha_contable;
	}
	public void setFecha_contable(String fecha_contable) {
		this.fecha_contable = fecha_contable;
	}
	public String getFecha_trx() {
		return fecha_trx;
	}
	public void setFecha_trx(String fecha_trx) {
		this.fecha_trx = fecha_trx;
	}
	public String getId_pago_banco() {
		return id_pago_banco;
	}
	public void setId_pago_banco(String id_pago_banco) {
		this.id_pago_banco = id_pago_banco;
	}
	public String getId_terminal() {
		return id_terminal;
	}
	public void setId_terminal(String id_terminal) {
		this.id_terminal = id_terminal;
	}
	public String getValor_recibido() {
		return valor_recibido;
	}
	public void setValor_recibido(String valor_recibido) {
		this.valor_recibido = valor_recibido;
	}
	
}
