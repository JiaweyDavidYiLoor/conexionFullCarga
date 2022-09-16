package com.st.integracion.util.FullCarga;

import java.net.URL;

import com.google.common.io.Resources;


//import groovy.util.GroovyScriptEngine;

public class TicketGrovy {

	protected String idTicket ;
	protected String data;
	protected String cabecera;
	protected String formato;
	
	
	public String getIdTicket() {
		return idTicket;
	}
	public void setIdTicket(String idTicket) {
		this.idTicket = idTicket;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getCabecera() {
		return cabecera;
	}
	public void setCabecera(String cabecera) {
		this.cabecera = cabecera;
	}
	public String getFormato() {
		return formato;
	}
	public void setFormato(String formato) {
		this.formato = formato;
	}
	
	
	public int incializarGrovy(String nombreArchivo)
	{
//		GroovyScriptEngine engine = new GroovyScriptEngine(new URL[] { Resources.getResource(
//	 			TicketGrovy.class, "ticket/") });
	 	
	 	
	 return 0;	
	}
	
	
	
	
	
}
