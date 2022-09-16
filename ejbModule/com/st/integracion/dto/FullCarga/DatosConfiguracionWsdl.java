package com.st.integracion.dto.FullCarga;

import java.util.HashMap;
import java.util.Map;

import com.st.integracion.servicios.FullCarga.VariablesFullCargaLocal;

public class DatosConfiguracionWsdl {
 
	private long id ;
	private String  canal;
	private String terminal;
	private String idInstitucion;
	
	
	public DatosConfiguracionWsdl() {};
	
	 
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCanal() {
		return canal;
	}
	public void setCanal(String canal) {
		this.canal = canal;
	}
	public String getTerminal() {
		return terminal;
	}
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}
	public String getIdInstitucion() {
		return idInstitucion;
	}
	public void setIdInstitucion(String idInstitucion) {
		this.idInstitucion = idInstitucion;
	}
	
	
	
	@Override
	public String toString() {
		return "DatosConfiguracionWsdl [id=" + id + ", canal=" + canal + ", terminal=" + terminal + ", idInstitucion="
				+ idInstitucion + "]";
	}
	 

}
