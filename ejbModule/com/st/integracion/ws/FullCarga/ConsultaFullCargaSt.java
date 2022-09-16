package com.st.integracion.ws.FullCarga;

import org.apache.log4j.Logger;

import com.st.integracion.util.FullCarga.Utilitaria;
import com.thoughtworks.xstream.XStream;   


public class ConsultaFullCargaSt {
	
	private static final Logger log=Logger.getLogger(ConsultaFullCargaSt.class);	
	private static final String error_producto_no_configurado = " codigo producto no tiene una configuracion establecida";
	private static final String codig_error_producto_no_configurado = "7001";	
	   
	private Utilitaria util = new Utilitaria();
	
	public ConsultaFullCargaSt() {}
		
	public String realizarConsultaBancoPacificoSt(String xmlParametros , Long codigoProveedorProducto,String referencia)	throws Exception
	{
		return "";			
	}
	
	
	private String crearRespuestaError(String codigoError , String descripcionError , String referenciaCliente)
	{
		
		ParametrosRespuesta params = new ParametrosRespuesta();
		params.setCodigoRespuestaST(codigoError);
		params.setDescripcionRespuestaST(descripcionError);
		params.setDeuda("0");
		params.setTotal("0");
		params.setValorMaximo("");
		params.setValorSugerido("");
		params.setCargo("0");
		params.setCuenta(referenciaCliente);
		params.setNombre("0");		
		params.setValorMinimo("");				
		XStream xstreamTem = new XStream();  
		xstreamTem.alias("ParametrosRespuesta", ParametrosRespuesta.class);
        String xmlRespuesta = xstreamTem.toXML(params); 			
      
        return  xmlRespuesta+"SEPARADORST<ConsultaTransaccion></ConsultaTransaccion>" ;				
	}
	
	
	
  	
}
