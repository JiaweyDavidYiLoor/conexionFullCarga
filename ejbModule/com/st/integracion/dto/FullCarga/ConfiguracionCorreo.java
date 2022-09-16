package com.st.integracion.dto.FullCarga;

public class ConfiguracionCorreo {

	private String userCorreo;
	private String passwordCorreo;
	private String destinatarios;
	private String destinatariosCopia;
	
	
	public String getUserCorreo() {
		return userCorreo;
	}
	public void setUserCorreo(String userCorreo) {
		this.userCorreo = userCorreo;
	}
	public String getPasswordCorreo() {
		return passwordCorreo;
	}
	public void setPasswordCorreo(String passwordCorreo) {
		this.passwordCorreo = passwordCorreo;
	}
	public String getDestinatarios() {
		return destinatarios;
	}
	public void setDestinatarios(String destinatarios) {
		this.destinatarios = destinatarios;
	}
	public String getDestinatariosCopia() {
		return destinatariosCopia;
	}
	public void setDestinatariosCopia(String destinatariosCopia) {
		this.destinatariosCopia = destinatariosCopia;
	}
	
	
	@Override
	public String toString() {
		return "ConfiguracionCorreo [userCorreo=" + userCorreo + ", passwordCorreo=" + passwordCorreo
				+ ", destinatarios=" + destinatarios + ", destinatariosCopia=" + destinatariosCopia + "]";
	}
	
			
}
