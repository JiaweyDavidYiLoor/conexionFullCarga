package com.st.integracion.dto.FullCarga;

import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "", propOrder = {
			"contrato",
			"saldo",
			"nombre",
			"direccionCobro",
			"fechaLimite",
			"saldoReclamo",
			"saldoBanco"})
@XmlRootElement(name="OCUDATACURSOR")
public class OcuDataCursor {
	
	@XmlElement(name="CONTRATO")
	String contrato;
	@XmlElement(name="SALDO")	
	BigDecimal saldo;
	@XmlElement(name="NOMBRE")
	String nombre;
	@XmlElement(name="DIRECCION_COBRO")
	String direccionCobro;
	@XmlElement(name="FECHA_LIMITE")
	Date fechaLimite;
	@XmlElement(name="SALDO_RECLAMO")
	BigDecimal saldoReclamo;
	@XmlElement(name="SALDO_BANCO")
	BigDecimal saldoBanco;
	
	public String getContrato() {
		return contrato;
	}
	public void setContrato(String contrato) {
		this.contrato = contrato;
	}
	public BigDecimal getSaldo() {
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDireccionCobro() {
		return direccionCobro;
	}
	public void setDireccionCobro(String direccionCobro) {
		this.direccionCobro = direccionCobro;
	}
	public Date getFechaLimite() {
		return fechaLimite;
	}
	public void setFechaLimite(Date fechaLimite) {
		this.fechaLimite = fechaLimite;
	}
	public BigDecimal getSaldoReclamo() {
		return saldoReclamo;
	}
	public void setSaldoReclamo(BigDecimal saldoReclamo) {
		this.saldoReclamo = saldoReclamo;
	}
	public BigDecimal getSaldoBanco() {
		return saldoBanco;
	}
	public void setSaldoBanco(BigDecimal saldoBanco) {
		this.saldoBanco = saldoBanco;
	}
	@Override
	public String toString() {
		return "OcuDataCursor [contrato=" + contrato + ", nombre=" + nombre
				+ ", direccionCobro=" + direccionCobro + ", saldoBanco="
				+ saldoBanco + "]";
	}	
}
