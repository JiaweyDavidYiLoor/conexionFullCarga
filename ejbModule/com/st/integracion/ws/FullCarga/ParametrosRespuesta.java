package com.st.integracion.ws.FullCarga;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", 
			propOrder = {
			"codigoRespuestaST",
			"descripcionRespuestaST", 
			"cuenta",
			"nombre",
	        "deuda", 
	        "valorSugerido", 
	        "cargo",
	        "total",
	        "valorMinimo",
	        "valorMaximo",
	        "factura",
	        "internacional",
	        "totalNacional",
	        "monedaNacional",
	        "simboloNacional",
	        "monedaInternacional",
	        "simboloInternacional",
	        "factorConversion",
	        "cargoNacional",
	        "requerimientoConsulta",
	        "respuestaConsulta"
	        })
	@XmlRootElement(name="ParametrosRespuesta")
	public class ParametrosRespuesta {
		
		@XmlElement( required=true)
		 protected String codigoRespuestaST;
		@XmlElement(required=true)
		 protected String descripcionRespuestaST;
		@XmlElement(required=true)
		 protected String cuenta;
		 @XmlElement(required=true)
		 protected String nombre;
		 @XmlElement(required=true)
		 protected String deuda;
		 @XmlElement(required=true)
		 protected String valorSugerido;
		 @XmlElement(required=true)
		 protected String cargo;
		 @XmlElement(required=true)
		 protected String total;
		 @XmlElement(required=true)
		 protected String valorMinimo;
		 @XmlElement(required=true)
		 protected String valorMaximo;		 
		 @XmlElement(required=true)
		 protected String factura;		 
		 @XmlElement(required=true)
		 protected String internacional;		 
		 @XmlElement(required=true)
	     protected String  totalNacional;
		 @XmlElement(required=true)
	     protected String  monedaNacional;
		 @XmlElement(required=true)
		 protected String  simboloNacional;
		 @XmlElement(required=true)
	     protected String  monedaInternacional;
		 @XmlElement(required=true)
	     protected String  simboloInternacional;
		 @XmlElement(required=true)
	     protected String factorConversion;
		 @XmlElement(required=true)
		 protected String cargoNacional;
		 @XmlElement(required=true)
		 protected String requerimientoConsulta;
		 @XmlElement(required=true)
		 protected String respuestaConsulta;
		 
		public String getCodigoRespuestaST() {
			return codigoRespuestaST;
		}
		public void setCodigoRespuestaST(String codigoRespuestaST) {
			this.codigoRespuestaST = codigoRespuestaST;
		}
		public String getDescripcionRespuestaST() {
			return descripcionRespuestaST;
		}
		public void setDescripcionRespuestaST(String descripcionRespuestaST) {
			this.descripcionRespuestaST = descripcionRespuestaST;
		}
		public String getCuenta() {
			return cuenta;
		}
		public void setCuenta(String cuenta) {
			this.cuenta = cuenta;
		}
		public String getNombre() {
			return nombre;
		}
		public void setNombre(String nombre) {
			this.nombre = nombre;
		}
		public String getDeuda() {
			return deuda;
		}
		public void setDeuda(String deuda) {
			this.deuda = deuda;
		}
		public String getValorSugerido() {
			return valorSugerido;
		}
		public void setValorSugerido(String valorSugerido) {
			this.valorSugerido = valorSugerido;
		}
		public String getCargo() {
			return cargo;
		}
		public void setCargo(String cargo) {
			this.cargo = cargo;
		}
		public String getTotal() {
			return total;
		}
		public void setTotal(String total) {
			this.total = total;
		}
		public String getValorMinimo() {
			return valorMinimo;
		}
		public void setValorMinimo(String valorMinimo) {
			this.valorMinimo = valorMinimo;
		}
		public String getValorMaximo() {
			return valorMaximo;
		}
		public void setValorMaximo(String valorMaximo) {
			this.valorMaximo = valorMaximo;
		}
		public String getFactura() {
			return factura;
		}
		public void setFactura(String factura) {
			this.factura = factura;
		}
		public String getInternacional() {
			return internacional;
		}
		public void setInternacional(String internacional) {
			this.internacional = internacional;
		}
		public String getTotalNacional() {
			return totalNacional;
		}
		public void setTotalNacional(String totalNacional) {
			this.totalNacional = totalNacional;
		}
		public String getMonedaNacional() {
			return monedaNacional;
		}
		public void setMonedaNacional(String monedaNacional) {
			this.monedaNacional = monedaNacional;
		}
		public String getSimboloNacional() {
			return simboloNacional;
		}
		public void setSimboloNacional(String simboloNacional) {
			this.simboloNacional = simboloNacional;
		}
		public String getMonedaInternacional() {
			return monedaInternacional;
		}
		public void setMonedaInternacional(String monedaInternacional) {
			this.monedaInternacional = monedaInternacional;
		}
		public String getSimboloInternacional() {
			return simboloInternacional;
		}
		public void setSimboloInternacional(String simboloInternacional) {
			this.simboloInternacional = simboloInternacional;
		}
		public String getFactorConversion() {
			return factorConversion;
		}
		public void setFactorConversion(String factorConversion) {
			this.factorConversion = factorConversion;
		}
		public String getCargoNacional() {
			return cargoNacional;
		}
		public void setCargoNacional(String cargoNacional) {
			this.cargoNacional = cargoNacional;
		}
		public String getRequerimientoConsulta() {
			return requerimientoConsulta;
		}
		public void setRequerimientoConsulta(String requerimientoConsulta) {
			this.requerimientoConsulta = requerimientoConsulta;
		}
		public String getRespuestaConsulta() {
			return respuestaConsulta;
		}
		public void setRespuestaConsulta(String respuestaConsulta) {
			this.respuestaConsulta = respuestaConsulta;
		}
			
}
