package com.st.integracion.network.FullCarga;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String tramaCoco = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body><GetServiceTransactionResponse xmlns=\"http://tempuri.org/\"><GetServiceTransactionResult>0001|RESPUESTA_OK|<dsSalidaPago><dtDatosTransaccion><Fecha_Proceso>20141017</Fecha_Proceso><Codigo_Retorno>7985</Codigo_Retorno><Descripcion_Retorno>Problemas en Ambiente de Comunicacion</Descripcion_Retorno><Secuencial_Banco></Secuencial_Banco><Secuencial_Banco_Comision></Secuencial_Banco_Comision><Codigo_Tercero></Codigo_Tercero></dtDatosTransaccion></dsSalidaPago></GetServiceTransactionResult></GetServiceTransactionResponse></s:Body></s:Envelope>";
		InputSource is=new InputSource(new StringReader(tramaCoco));
		Document root=null;
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			root = builder.parse(is);
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String trama=null;
		try{ trama=root.getElementsByTagName("GetServiceTransactionResponse").item(0).getTextContent();}catch(Exception e){};
		
		System.out.println(trama);
		 
		
		String validacionContexto = trama.substring(0, 18);
		if(validacionContexto.compareTo("0001|RESPUESTA_OK|")==0)
		{
			System.out.println("trama exitosa");
		}
		
		
	}

}
