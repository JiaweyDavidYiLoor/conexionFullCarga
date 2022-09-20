package com.st.integracion.util.FullCarga;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.st.integracion.dto.FullCarga.ConfiguracionCorreo;
import com.st.integracion.dto.FullCarga.DatosConfiguracion;
import com.st.integracion.dto.FullCarga.EmpresaClienteRoll;
import com.st.integracion.dto.FullCarga.OcuDataCursor;
import com.st.integracion.dto.FullCarga.TransaccionFullCarga;
import com.st.integracion.dto.FullCarga.TransaccionFullCarga.TipoProducto;
import com.st.integracion.dto.Transaccion.TipoOperacion;
import com.st.integracion.servicios.FullCarga.VariablesFullCargaLocal;
import com.st.integracion.ws.FullCarga.InfoEmpresa;
import com.st.integracion.ws.FullCarga.ParametrosRespuesta;



public class Utilitaria {
	
	private static final String ORIGINAL= "��������������"; 
    private static final String REPLACEMENT= "AaEeIiOoUuNnUu";
	
	private static final Logger log=Logger.getLogger(Utilitaria.class);

	public TransaccionFullCarga procesarRespuesta (TransaccionFullCarga trn)
	{		
		TransaccionFullCarga retorno = null;
		TipoOperacion tipoOperacion = trn.getTipoOperacion();
		
		if(tipoOperacion.compareTo(TipoOperacion.COMPRAR_PIN_INTERNET)==0)
		{
			retorno = procesarRespuestaComprarPinInternet (trn);
		}
		else if(tipoOperacion.compareTo(TipoOperacion.ANULACION_CMP_PIN_INTERNET)==0)
		{
			retorno = procesarRespuestaAnulacion(trn);
		}else if (tipoOperacion.compareTo(TipoOperacion.RECARGA)==0) {
			retorno = procesarRespuestaRecarga(trn);
		}
		return retorno;
	} 
	
private TransaccionFullCarga procesarRespuestaComprarPinInternet (TransaccionFullCarga trn)
	{
		TransaccionFullCarga retorno = null;		
		String codigoErrorRespuesta = trn.getCodigoRetorno();//trn.getCodigoRsp();
						
			if(codigoErrorRespuesta.compareTo("00")==0)
			{				
			       trn.setCodigoEstado(72);	//Compra de pin de internet realizada
				   trn.setCodigoProceso(62);// Respuesta Compra Pin Internet			
				  // trn.setReferenciaProveedor(trn.getCuponPagado());
				   /*Agregando el campo <ext> al xml de respuesta */
			      //trn.setDescripcion("Transacci�n exitosa");
			      trn.setEstadoFK(3L);

			}
			else if(codigoErrorRespuesta.compareTo("TRMALFOR")==0)
			{
				trn.setCodigoEstado(71);	//Compra de pin de internet pendiente
				trn.setCodigoProceso(61);// Respuesta Compra Pin Internet pendiente
               // trn.setReferenciaProveedor("NAK");
				
				trn.setDescripcion(trn.getMensajeRetorno());
			    //trn.setCodigoProceso(62);// Respuesta compra de pin internet
				//trn.setCodigoEstado(73);//Compra de pin de internet no realizada	
				trn.setEstadoFK(1L);
			}
			else 
			{
				trn.setReferenciaProveedor("NAK");
				
				trn.setDescripcion(trn.getMensajeRetorno());
			    trn.setCodigoProceso(62);// Respuesta compra de pin internet
				trn.setCodigoEstado(73);//Compra de pin de internet no realizada	
				trn.setEstadoFK(2L);
			
		}
		retorno = trn;
		return retorno;

	}
private TransaccionFullCarga procesarRespuestaRecarga (TransaccionFullCarga trn)
{
	TransaccionFullCarga retorno = null;		
	String codigoErrorRespuesta = trn.getCodigoRetorno();//trn.getCodigoRsp();
					
		if(codigoErrorRespuesta.compareTo("00")==0)
		{				
		       trn.setCodigoEstado(2);	//Recarga realizada
			   trn.setCodigoProceso(2);// Recarga respondida			
			   //trn.setReferenciaProveedor(trn.getReferenciaSigma());
			   /*Agregando el campo <ext> al xml de respuesta */
		      //trn.setDescripcion("Transacci�n exitosa");
		      trn.setEstadoFK(2L);

		}
		/*else if(codigoErrorRespuesta.compareTo("TRMALFOR")==0)
		{
			trn.setCodigoEstado(71);	//Compra de pin de internet pendiente
			trn.setCodigoProceso(61);// Respuesta Compra Pin Internet pendiente
           // trn.setReferenciaProveedor("NAK");
			
			trn.setDescripcion(trn.getMensajeRetorno());
		    //trn.setCodigoProceso(62);// Respuesta compra de pin internet
			//trn.setCodigoEstado(73);//Compra de pin de internet no realizada	
			trn.setEstadoFK(1L);
		}*/
		else 
		{
			trn.setReferenciaProveedor("NAK");
			
			trn.setDescripcion(trn.getMensajeRetorno());
		    trn.setCodigoProceso(3);// Respuesta compra de pin internet
			trn.setCodigoEstado(3);//Compra de pin de internet no realizada	
			trn.setEstadoFK(3L);
		
	}
	retorno = trn;
	return retorno;

}

	private TransaccionFullCarga procesarRespuestaAnulacion (TransaccionFullCarga trn)
	{
		
		TransaccionFullCarga retorno = null;		
		String codigoErrorRespuesta = trn.getCodigoRetorno();
		
		if(codigoErrorRespuesta==null)
		{
			trn.setCodigoEstado(74);	//Anulacon Pendiente
			trn.setCodigoProceso(62);//
			//trn.setEstadoFK(1L);
		}
		else
		{
			if(codigoErrorRespuesta.compareTo("CL_065")==0  )
			{
				trn.setReferenciaProveedor("NAK");
				trn.setCodigoEstado(75);	//ANULACION REALIZADA
				trn.setCodigoProceso(62);// ANULACION RECARGA
				trn.setEstadoFK(2L);
				trn.setDescripcion("Transaccion no Realizada");		
			}
			else
			{				
				trn.setCodigoProceso(62);// ANULACION RECARGA
				trn.setCodigoEstado(76);//ANULACION NO REALIZADA	
				//trn.setEstadoFK(1L);								
			}
		}
		retorno = trn;
		return retorno;

	}

		
	public int validadHorarioTransaccion(Date fechaTransaccionRecibida ,String horarioConfiguradoProducto)
	{
     int retorno =-1;
     
     try {
	
	  if(horarioConfiguradoProducto.equals("ABIERTO"))
	  {
		retorno=0;  
		  
	  }  
	  else
	  {
		  String Inicio = horarioConfiguradoProducto.substring(0,5)+":00";
		  String Fin = horarioConfiguradoProducto.substring(6,11)+":00";
		        
		  String Ingreso = new SimpleDateFormat("HH:mm:ss").format(fechaTransaccionRecibida);
		  //boolean retorno=false;
		  
		  
		        Date HoraInicio = new SimpleDateFormat("HH:mm:ss").parse(Inicio);
		        log.info("Horario de inicio:"+HoraInicio); 
		        Date HoraFin = new SimpleDateFormat("HH:mm:ss").parse(Fin);
		        log.info("Horario de fin:"+HoraFin);
		        
		        Date HoraIngreso = new SimpleDateFormat("HH:mm:ss").parse(Ingreso);
		        if(HoraIngreso.after(HoraInicio) && HoraIngreso.before(HoraFin))
		        	retorno=0;//esta dentro del horario
		        else
		        	retorno =1; //esta fuera de horario	      
	   }	
     }
     catch(Exception e)
     {
    	log.info("Excepciton :"+e.getMessage());
    	 
     }
		return retorno;
	}
	
	public static String formatFecha(Date fecha)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		
		return dateFormat.format(fecha);
		
	}
	
	
	public static String formatFecha(Date fecha, String formato)
	{
		DateFormat dateFormat = new SimpleDateFormat(formato);
		
		return dateFormat.format(fecha);
		
	}
	
	public static String generarXMLOffline()
	 {
	  String XML = null;
	  Random rnd = new Random();
	  
	  String Fecha_ProcesoRsp = formatFecha(new Date()); //8Digitos
	  String Secuencial_BancoRsp = String.valueOf(rnd.nextInt(9000000) + 1000000);//7Digitos
	  String Secuencial_Banco_ComisionRsp = String.valueOf(rnd.nextInt(9000000) + 1000000);//7Digitos
	  String Codigo_TerceroRsp = String.valueOf(rnd.nextInt(90000000) + 10000000); //8Digitos
	  String SecuencialTBBARsp = String.valueOf(rnd.nextInt(90000000) + 10000000); //8Digitos	  
	  
	  XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
	   "<dsSalidaPagoRsp>" +
	    "<dtNotaVentaRsp>" +
	    "<RucBancoRsp>R0990005737001</RucBancoRsp>" +
	    "<LocalidadRsp></LocalidadRsp>" +
	    "<PuntoVentaRsp></PuntoVentaRsp>" +
	    "<SecuencialRsp>000000000</SecuencialRsp>" +
	    "<AutorizacionSRIRsp></AutorizacionSRIRsp>" +
	    "<DireccionBancoRsp>000000000</DireccionBancoRsp>" +
	    "<FechaVigenciaSriRsp>01-01-1900</FechaVigenciaSriRsp>" +
	    "<FechaInicioSRIRsp>01-01-1900</FechaInicioSRIRsp>" +
	    "<FechaResolucionSRIRsp>08-08-1995</FechaResolucionSRIRsp>" +
	    "</dtNotaVentaRsp>" +
	    "<dtDatosTransaccionRsp>" +
	    "<Fecha_ProcesoRsp>"+Fecha_ProcesoRsp+"</Fecha_ProcesoRsp>" +
	    "<Codigo_RetornoRsp>0001</Codigo_RetornoRsp>" +
	    "<Descripcion_RetornoRsp>PROCESO OK</Descripcion_RetornoRsp>" +
	    "<Secuencial_BancoRsp>"+Secuencial_BancoRsp+"</Secuencial_BancoRsp>" +
	    "<Secuencial_Banco_ComisionRsp>"+Secuencial_Banco_ComisionRsp+"</Secuencial_Banco_ComisionRsp>" +
	    "<Codigo_TerceroRsp>"+Codigo_TerceroRsp+"</Codigo_TerceroRsp>" +
	    "<SecuencialTBBARsp>"+SecuencialTBBARsp+"</SecuencialTBBARsp>" +
	    "</dtDatosTransaccionRsp>" +
	    "<ext>2</ext>"+
	    "</dsSalidaPagoRsp>";
	    
	  return XML;
	 }
	
/*
	public ObjConciliacion llenarLista(File archivoConciliacion)
	{
	      
		ObjConciliacion listaObj = new ObjConciliacion();
		//listaObj.setLista();
		List<ObjDetalleConciliacion>listaDetalle = new ArrayList();
	    FileReader fr = null;
	    BufferedReader br = null;
	   	
	      try {
	          // Apertura del fichero y creacion de BufferedReader para poder
	          // hacer una lectura comoda (disponer del metodo readLine()).
	       
	          fr = new FileReader (archivoConciliacion);
	          br = new BufferedReader(fr);
              	          	          
	          // Lectura del fichero
	          String linea;
	          while((linea=br.readLine())!=null)
	          {
	        	  
	        	 String cod  = linea.substring(0, 1);
	        	 if(cod.equals("C"))
	        	 {
	        		// String codigo = linea.substring(0, 1);	        	           
	        	     String fecha = linea.substring(1,9);
	        	     listaObj.setFechaContable(fecha);
	        		 String numeroRegistro = linea.substring(9,17);
	        		 listaObj.setCantidadRegistros(numeroRegistro);
	        		 String valorTotal = linea.substring(17,32);	        		 
	        		 listaObj.setValor(valorTotal);
	        	 }
	        	 else if(cod.equals("D"))
	        	 {
	        		 ObjDetalleConciliacion  temp = new ObjDetalleConciliacion();
	        		 // String codigoD = linea.substring(0, 1);
	        	     // System.out.println("codigo="+codigoD);      	        	      
	        	      String fechaContable = linea.substring(1,9);	
	        	      temp.setFechaContable(fechaContable);
	        	      
	        	      String horaTransaccion = linea.substring(9,15);
	        	      temp.setHoraTransaccion(horaTransaccion);
	        	      
	        	      String numeroTransaccion = linea.substring(15,22);
	        	      temp.setNumeroTransaccion(numeroTransaccion);
	        	      
	        	      String empresa = linea.substring(22,37);
	        	      temp.setEmpresa(empresa);
	        	      
	        	      String producto = linea.substring(37,38);
	        	      temp.setProducto(producto);
	        	      
	        	      String servicio = linea.substring(38,48);	
	        	      temp.setServicio(servicio);
	        	      
	        	      String fechaTransaccion = linea.substring(48,56);
	        	      temp.setFechaTransaccion(fechaTransaccion);
	        	      
	        	      String oficinaCPT = linea.substring(56,64);
	        	      temp.setOficinaCPT(oficinaCPT);
	        	      
	        	      String codigContrapartida = linea.substring(64,84);
	        	      temp.setCodigo(codigContrapartida);
	        	      
	        	      String nombre = linea.substring(84,144);
	        	      temp.setNombre(nombre);
	        	      
	        	      String valorCobro = linea.substring(144,159);
	        	      temp.setValor(valorCobro);
	        	      
	        	      String ordenBanco = linea.substring(159,167);	
	        	      temp.setOrdenBanco(ordenBanco);
	        	      
	        	      String secuencial = linea.substring(167,174);	        	      
	        		  temp.setSecuencial(secuencial);
	        		  listaDetalle.add(temp);
	        	 }	        	 	        	  
	          }	
	          
	          listaObj.setLista(listaDetalle);
	          
	       }
	       catch(Exception e){
	          e.printStackTrace();
	       }finally{
	          // En el finally cerramos el fichero, para asegurarnos
	          // que se cierra tanto si todo va bien como si salta 
	          // una excepcion.
	          try{                    
	             if( null != fr ){   
	                fr.close();     
	             }                  
	          }catch (Exception e2){ 
	             e2.printStackTrace();
	          }
	       }
		
	 return listaObj;	
	}
*/
	public String formatoFechaConcilidacion(Date fecha) 
	{
		
			String strFecha = null;
		    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		    strFecha = format.format(fecha);
		    return strFecha;
		
	}
	
	public static String  getStringFromDoc(org.w3c.dom.Document doc)    {
        try
        {
           DOMSource domSource = new DOMSource(doc);
           StringWriter writer = new StringWriter();
           StreamResult result = new StreamResult(writer);
           TransformerFactory tf = TransformerFactory.newInstance();
           Transformer transformer = tf.newTransformer();
           transformer.transform(domSource, result);
           writer.flush();
           return writer.toString();
        }
        catch(TransformerException ex)
        {
           log.info("Exception metodo getStringFromDoc:"+ex.getMessage());
           return "";
        }
    }
	
	public static String stripAccents(String str) {
		if (str == null) {
		    return null;
		}
		char[] array = str.toCharArray();
		for (int index = 0; index < array.length; index++) {
		    int pos = ORIGINAL.indexOf(array[index]);
		    if (pos > -1) {
		        array[index] = REPLACEMENT.charAt(pos);
		    }
		}
		return new String(array);
		}
	
	
	/**
	 * Retorna un emascaramiento de la tarjeta de credito
	 * @param  numeroTarjeta numero de tarjeta a enmascaras
	 * @return retorna la tajeta con el formato NNNNXXXXXXXXXXNNN
	*/
	public static String enmascararTarjetaCredito(String numeroTarjeta)
	{
	 
	  String enmascaramiento = "";
	  int cantidadEnmascarar = numeroTarjeta.length() -10;
	  String parteInicial = numeroTarjeta.substring(0,6);
	  String parteFinal = numeroTarjeta.substring(numeroTarjeta.length()-4,numeroTarjeta.length());
	  
	  for(int i=0;i<cantidadEnmascarar; i++)
	  {
		  enmascaramiento=enmascaramiento+"X"; 
		  
	  }
		
	  return parteInicial+enmascaramiento+parteFinal;	
	}
	
	
	
	public static String enmascararNumeroCuenta(String numeroCuenta)
	{
	 
	  String enmascaramiento = "";
	  int cantidadEnmascarar = numeroCuenta.length() -4;
	  String parteInicial = numeroCuenta.substring(0,2);
	  String parteFinal = numeroCuenta.substring(numeroCuenta.length()-2,numeroCuenta.length());
	  
	  for(int i=0;i<cantidadEnmascarar; i++)
	  {
		  enmascaramiento=enmascaramiento+"X"; 
		  
	  }
		
	  return parteInicial+enmascaramiento+parteFinal;	
	}
	
	
	
	
	  /**
     * Retorna una cadena padeadas con ceros a la izquierda
     * @return  The name of the file or directory denoted by this abstract
     *          pathname, or the empty string if this pathname's name sequence
     *          is empty
     */
	
	public static String agregarCeros(String string, int largo)
	{
		String ceros = "";

		int cantidad = largo - string.length();

		if (cantidad >= 1)
		{
			for(int i=0;i<cantidad;i++)
			{
				ceros += "0";
			}

			return (ceros + string);
		}
		else
			return string;
	}
	
	public static Object convertirXmlToObjeto(String xml, Class clase){
		Object objeto=null;
		try{
		JAXBContext jaxbContext = JAXBContext.newInstance(clase);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		StringReader reader = new StringReader(xml);
		objeto = unmarshaller.unmarshal(reader);
		}catch(JAXBException e){
			e.printStackTrace();
		}
		return objeto;
	}
	
	public String convertirObjetoToXml(Object obj){
		String retorno = null;
		try {					
			JAXBContext jc = JAXBContext.newInstance(obj.getClass());
			Marshaller marshaller = jc.createMarshaller();
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
	        StringWriter sw = new StringWriter();
	        marshaller.marshal(obj,sw);
	        retorno = sw.toString();
	        //System.out.println(retorno);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		//retorno = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+retorno;
		return retorno;
	}
	
	
	public static  String obtenerConsultaRespuesta(String xml , String nombreTag)
	{
		String retorno = "";
		try
		{
			DocumentBuilder builder1;
			builder1=DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource is=new InputSource(new StringReader(xml));
			Document root=builder1.parse(is);
			
			Node sas = root.getElementsByTagName(nombreTag).item(0);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document newDocument = builder.newDocument();
			Node importedNode = newDocument.importNode(sas, true);
			newDocument.appendChild(importedNode);									
			
			 retorno = getStringFromDoc(newDocument);	
		}
		catch (Exception e) {
			log.info("Excepcion en metodo obtenerConsultaRespuesta:"+e.getMessage());
		}
		return retorno;
		
	}
	
	public Date calcularFechaConciliacion(Date fecha) {
		boolean flag = true;
		
		if(excedioHoraLimite(fecha)) {
			fecha = sumarDia(fecha);
		}		
		do {			
			if(esFeriado(fecha) || esSabadoDomingo(fecha))
				fecha = sumarDia(fecha);
			else 
				flag = false;							
		}while(flag);
		
		return fecha;
	}
	
	public boolean excedioHoraLimite(Date fecha) {
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(fecha); 
		calendar.set(Calendar.MILLISECOND, 0); 
		calendar.set(Calendar.SECOND, 0); 
		calendar.set(Calendar.MINUTE, 0); 
		calendar.set(Calendar.HOUR_OF_DAY, 17);

		Date limite = calendar.getTime();
		
		long diferencia = (limite.getTime() - fecha.getTime()) / 1000;
		
		if (diferencia <=0)
			return true;
		
		return false;
	}
	
	public Date sumarDia(Date fecha) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fecha);
		calendar.add(Calendar.DATE, 1);
		return calendar.getTime();
	}
	
	public boolean esSabadoDomingo(Date fecha) {
		boolean ban = false;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fecha);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); 			
		if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
			ban = true;		
		return ban;
	}
	
	
	public boolean esFeriado(Date fecha) {		
		ArrayList<Date> feriados = getFeriados();
		
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(fecha); 
		calendar.set(Calendar.MILLISECOND, 0); 
		calendar.set(Calendar.SECOND, 0); 
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR, 0);
		Date fecha2 = calendar.getTime();
		boolean existe = true; 
		for(Date date: feriados) {				
			calendar.setTime(date); 
			calendar.set(Calendar.MILLISECOND, 0); 
			calendar.set(Calendar.SECOND, 0); 
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR, 0);
			date = calendar.getTime();		
			existe = date.equals(fecha2);
			if(existe) {
				return true;
			}
		}
		
		return false;
	}
	
	public ArrayList<Date> getFeriados(){
		ArrayList<Date> feriados = new ArrayList<Date>();
		Date fecha = new Date();
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(fecha); 		
		
		calendar = Calendar.getInstance(); 
		calendar.setTime(fecha);
		calendar.set(Calendar.MILLISECOND, 0); 
		calendar.set(Calendar.SECOND, 0); 
		calendar.set(Calendar.MINUTE, 0); 
		calendar.set(Calendar.DATE, 2); 
		calendar.set(Calendar.MONTH, 3);
		feriados.add(calendar.getTime());				
		
		return feriados;
	}

	public static Document obtenerHijoDocument(Document padre  , String nombreHijo) throws ParserConfigurationException
	{
		Node hijo = padre.getElementsByTagName(nombreHijo).item(0);				
		Document hijoDocument =crearDocumentNuevo();				
		Node importedNode = hijoDocument.importNode(hijo, true);
		hijoDocument.appendChild(importedNode);
		return hijoDocument;		
	}
	
	public static Document crearDocumentNuevo() throws ParserConfigurationException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document newDocument = builder.newDocument();
		return newDocument;		
	}
	
    public static  String obtenerHijoXml(String padreXmlIn , String nombreHijoXml ) throws ParserConfigurationException, SAXException, IOException
	{    	
		Document padre=transformarXlmStringToDocument(padreXmlIn);	    	
		Document hijo = obtenerHijoDocument(padre, nombreHijoXml);
		return  transformarDocumentToStringXml(hijo);    	
	}
    
    public static String transformarDocumentToStringXml(org.w3c.dom.Document doc)    {
        try
        {
           DOMSource domSource = new DOMSource(doc);
           StringWriter writer = new StringWriter();
           StreamResult result = new StreamResult(writer);
           TransformerFactory tf = TransformerFactory.newInstance();
           Transformer transformer = tf.newTransformer();
           transformer.transform(domSource, result);
           writer.flush();
           return writer.toString();
        }
        catch(TransformerException ex)
        {
           log.info("transformarDocumentToStringXml:"+ex.getMessage());
           return null;
        }
    }
    
    public static Document transformarXlmStringToDocument(String xmlString) throws ParserConfigurationException, SAXException, IOException
    {
    	DocumentBuilder builder1;
		builder1=DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is=new InputSource(new StringReader(xmlString));
		Document root=builder1.parse(is);	    	
    	return root;
    }
	


 	public static Document parseXmlFile(String in) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(in));
			return db.parse(is);
		} catch (ParserConfigurationException e) {
		throw new RuntimeException(e);
		} catch (SAXException e) {
		throw new RuntimeException(e);
		} catch (IOException e) {
		throw new RuntimeException(e);
		}
	}
	
 	
 	private static void enviarCorreo( String userCuenta, String passworCuenta, String destinatarios, String destinatariosCopia, String tituloCorreo,String mensaje) throws MessagingException, AddressException{

		String fromUser=userCuenta;
		String fromPassword=passworCuenta;
		Properties props = new Properties();
		props.setProperty("mail.smtp.host", "smtp.gmail.com");
		props.setProperty("mail.smtp.starttls.enable", "true");
		props.setProperty("mail.smtp.port","587");
		props.setProperty("mail.smtp.user", fromUser);
		props.setProperty("mail.smtp.auth", "true");
		Session session = Session.getDefaultInstance(props);
		session.setDebug(true);
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress( fromUser));
		
		String[] to = destinatarios.split("\\|");
	    String[] para;
		para=new String[to.length];
		para=to;
		InternetAddress[] addressr;
		addressr=new InternetAddress[para.length];
		for(int i=0;i<para.length;i++){
			addressr[i]=new InternetAddress(para[i]);	
			
		}
		
		String[] cc = destinatariosCopia.split("\\|");
	    String[] paraCC;
		paraCC=new String[cc.length];
		paraCC=cc;
		InternetAddress[] addressrCC;
		addressrCC=new InternetAddress[paraCC.length];
		for(int i=0;i<paraCC.length;i++){
			addressrCC[i]=new InternetAddress(paraCC[i]);	
			
		}		
		message.addRecipients(Message.RecipientType.TO,addressr);
		message.addRecipients(Message.RecipientType.CC,addressrCC);
				
		//message.setSubject("Transaccion Dexpago "+ producto  +"N� =" +tx.getReferenciaCliente() +", realizar Anulacion " );
		message.setSubject( tituloCorreo);
		message.setText(mensaje);

		
		Transport t = session.getTransport("smtp");
		t.connect(fromUser,fromPassword);
		t.sendMessage(message,message.getAllRecipients());
		t.close();
	}
 	
 	public static TransaccionFullCarga configurarTipoProducto(String tipoProducto , TransaccionFullCarga trn)
 	{
 		if(tipoProducto.compareTo("MOVIL")==0)
 			trn.setTipoProducto(TipoProducto.MOVIL);
 			 			 		 		
 		return trn;
 	}
	
	
	public static Object convertirXmlToObj(String xmlSrt ,  Class<?> ...clases) throws JAXBException
	{
	    JAXBContext jaxbContextTemp2 = JAXBContext.newInstance(clases);
		Unmarshaller unmarshallerTemp2 = jaxbContextTemp2.createUnmarshaller();
		StringReader readerTemp2 = new StringReader(xmlSrt);
		Object objt = clases.getClass();
		objt =  unmarshallerTemp2.unmarshal(readerTemp2);
		return  objt;
		
	}
	
	public static long obtenerCodigoConfiguracion(List<EmpresaClienteRoll> listaEmpresaCliRoll , InfoEmpresa infoEmp )
	{
		long retorno =0;
		
		for(EmpresaClienteRoll emprCliRoll :listaEmpresaCliRoll)
		{
			log.info(emprCliRoll.getEmpresaFk() + " Y " + infoEmp.getCodigoEmpresa().longValue());
			log.info(emprCliRoll.getClienteRollFk() + " Y " + infoEmp.getCodigoRol().longValue());
			if(emprCliRoll.getCodigoClienteFk() == infoEmp.getCodigoCliente().longValue() && emprCliRoll.getEmpresaFk() == infoEmp.getCodigoEmpresa().longValue()
					&& emprCliRoll.getClienteRollFk()== infoEmp.getCodigoRol().longValue() )
				 retorno = emprCliRoll.getCodigoConfiguracionFk() ;
		}		
		return retorno;
	}
}
