package com.st.integracion.ws.FullCarga;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", 
		propOrder = {
		"idParametrosBp",
		"tagParametro",
		"valorFormato"
        })
//@XmlRootElement(name="campoConsulta")
public class CampoConsulta {

	@XmlElement(name="idParametrosBp" ,required=true)
	protected String idParametrosBp;
	
	@XmlElement(name="tagParametro" ,required=true)
	protected String	tagParametro;
	
	@XmlElement(name="valorFormato", required=false)
	protected List<ValorFormato> valorFormato;
	
	
	public String getIdParametrosBp() {
		return idParametrosBp;
	}

	public void setIdParametrosBp(String idParametrosBp) {
		this.idParametrosBp = idParametrosBp;
	}

	public String getTagParametro() {
		return tagParametro;
	}

	public void setTagParametro(String tagParametro) {
		this.tagParametro = tagParametro;
	}
	
	public List<ValorFormato> getValorFormato(){
		return this.valorFormato;
	}
	
	public void setValorFormato(List<ValorFormato> valorFormato){
		this.valorFormato = valorFormato;
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {
	        "campo","pad","valorInicial","valorFinal"})
	//@XmlRootElement(name="valorFormato")
  public static class ValorFormato
  {
	@XmlElement(required=false)
	String 	campo;
	@XmlElement(required=false)
	String 	valorInicial;
	@XmlElement(required=false)
	String valorFinal;
	@XmlElement(required=false)
	String pad;
	public String getCampo() {
		return this.campo;
	}
	public void setCampo(String campo) {
		this.campo = campo;
	}
	public String getPad(){
		return this.pad;
	}
	public String setPad(String pad){
		return this.pad = pad;
	}
	public String getValorInicial() {
		return this.valorInicial;
	}
	public void setValorInicial(String valorInicial) {
		this.valorInicial = valorInicial;
	}		  
	public String getValorFinal(){
		return this.valorFinal;
	}
	public void setValorFinal(String valorFinal){
		this.valorFinal = valorFinal;
	}
	
	
  }	
	
}
