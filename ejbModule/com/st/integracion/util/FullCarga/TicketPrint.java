package com.st.integracion.util.FullCarga;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.st.integracion.dto.FullCarga.DatosConfiguracion;
import com.st.integracion.dto.FullCarga.DatosConfiguracionWsdl;
import com.st.integracion.dto.FullCarga.OcuDataCursor;
import com.st.integracion.dto.FullCarga.Producto;
import com.st.integracion.dto.FullCarga.TransaccionFullCarga;
import com.st.integracion.servicios.RegistroServicios;
import com.st.integracion.servicios.FullCarga.ServicioFullCarga;
import com.st.integracion.servicios.FullCarga.VariablesFullCargaLocal;
import com.st.integracion.util.FullCarga.Utilitaria;
import com.st.integracion.ws.FullCarga.InfoEmpresa;
import com.st.integracion.ws.FullCarga.ParametrosRespuesta;


public class TicketPrint 
{
	Producto producto;
	String xmlIn;
	TransaccionFullCarga tx = new TransaccionFullCarga(); 
	
	String leyendaResolucion = "De acuerdo a resoluci�n No. NAC-DGERCG12-00105 del SRI publicada en "+
	"Registro Oficial No.666 las instituciones financieras tienen la obligatoriedad de emitir los comprobantes "+
	"de ventas, retenci�n y documentos complementarios �nicamente a trav�s de mensaje de datos y firmados "+
	"Electr�nicamente. El Banco pone a su disposici�n en su portal web, la opci�n de consulta documento Tributarios."+
	"*Para no clientes: www.bancodelpacifico.com. Ingrese su identificaci�n y el n�mero que consta en el documento "+
	"transaccional, seleccione el tipo de documento tributario que se desea imprimir y respaldar.";
	
	String leyendaEtapa ="Este comprobante no es v�lido para efectos tributarios. El usuario dispone que ETAPA EP mantenga en custodia su comp de venta hasta que este sea retirado en la Empresa (Cl�usula 5ta)";
	
	
	String leyendaDepositoBAncario = "Este comprobante de transacci�n no es negociabe, ni transferible, ni puede ser objeto de ning�n tipo de comercializaci�n o negociaci�n por parte de su tenedor"; 
	public TicketPrint(Producto prd, String xmlIn, TransaccionFullCarga trn)
	{
		this.producto =prd;
		this.xmlIn =xmlIn;
		this.tx = trn;
	}
	
	public ArrayList armarTicketImpresion(BigDecimal monto, BigDecimal iva) throws JAXBException
	{
		RegistroServicios registro;
	    registro=RegistroServicios.registroInstance();
		ServicioFullCarga servicioLatinTravelGirosPagos = (ServicioFullCarga)registro.getServicioConeccion(tx.getCodigoProveedorProducto()); 
		VariablesFullCargaLocal varBancoPacifico = servicioLatinTravelGirosPagos.getVariables();
		List<DatosConfiguracion> mapaDatosConf =  new ArrayList<DatosConfiguracion>();// varBancoPacifico.getDatosConfig();
		Map<Long, DatosConfiguracionWsdl> mapDatosConexWsdl = new HashMap<Long, DatosConfiguracionWsdl>();// varBancoPacifico.getListaDatosConexionWsdl();
			
		JAXBContext jaxbContextTemp2 = JAXBContext.newInstance(InfoEmpresa.class);
		Unmarshaller unmarshallerTemp2 = jaxbContextTemp2.createUnmarshaller();
		StringReader readerTemp2 = new StringReader(tx.getParametrosInfoEmpresa());
		InfoEmpresa infoEmpresa = (InfoEmpresa) unmarshallerTemp2.unmarshal(readerTemp2);				
		
		DatosConfiguracion datosConf = new DatosConfiguracion();			
		long IdEmpresa = infoEmpresa.getCodigoEmpresa();
		long codigoRol = infoEmpresa.getCodigoRol();
		for(DatosConfiguracion datCon: mapaDatosConf){
			if(datCon.getEmpresa()==IdEmpresa){
				datosConf = datCon;
			}
		}
		
		Double comision=0.0, ivaLocal=0.12, valorPagado=tx.getValorContable().doubleValue();
			
	    ArrayList listaTicketComprobanteFac = new ArrayList<String>();

		listaTicketComprobanteFac.add("CFULL CARGA");
		listaTicketComprobanteFac.add("D ");
		
		listaTicketComprobanteFac.add("IFECHA: &D"+Utilitaria.formatFecha(new Date(),"yyyy-MM-dd HH:mm:ss"));
		listaTicketComprobanteFac.add("========================================");
		
		listaTicketComprobanteFac.add("IPAGO:&D"+"RECARGA");
		listaTicketComprobanteFac.add("IDESCRIPCION:&D"+tx.getTipoProductoStr());
		listaTicketComprobanteFac.add("ITRANSACCION:&D"+tx.getNumTransaccion());
		listaTicketComprobanteFac.add("ICUENTA:&D"+tx.getReferenciaCliente());
		listaTicketComprobanteFac.add("========================================");
		
		listaTicketComprobanteFac.add("ITRANSACCION:&D"+"RECARGA");
		listaTicketComprobanteFac.add("IVALOR PAGADO:&D"+tx.getValorContable());
		listaTicketComprobanteFac.add("IVALOR COMISION:&D"+comision);
		listaTicketComprobanteFac.add("IVALOR IVA:&D"+ivaLocal);
		
		Double totalLocal=((valorPagado+comision)*ivaLocal)+(valorPagado+comision);
		String resultIVA = String.format("%.2f", totalLocal);
		
		listaTicketComprobanteFac.add("IVALOR TOTAL:&D"+resultIVA);
		listaTicketComprobanteFac.add("IFECHA:&D"+Utilitaria.formatFecha(new Date(),"yyyy-MM-dd HH:mm:ss"));
		listaTicketComprobanteFac.add(" ");
		listaTicketComprobanteFac.add(" ");
		listaTicketComprobanteFac.add(" ");
		listaTicketComprobanteFac.add("========================================");
		listaTicketComprobanteFac.add("C* REALIZA EL PAGO DE TUS SERVICIOS");
		listaTicketComprobanteFac.add("CBASICOS, SRI, IESS, TRANSITO Y MAS! *");
		listaTicketComprobanteFac.add("========================================");

		//listaTicketComprobanteFac.add("IREF PROVEEDOR:&D"+tx.getReferenciaProveedor());

		return listaTicketComprobanteFac;
	}
	
	
	
	public ArrayList armarTicketComprabanteFactura(BigDecimal monto, BigDecimal iva) throws JAXBException
	{
		
		ArrayList listaTicketComprobanteFac = new ArrayList<String>();
		if(false)
		{
			
		
		
        BigDecimal comision = null;//new BigDecimal(tx.getValorComision()).setScale(2, RoundingMode.HALF_UP).divide(new BigDecimal(100.00));
		
		BigDecimal valorIva = comision.multiply(iva).setScale(2, RoundingMode.HALF_UP); 
		
		BigDecimal factorIva = new BigDecimal(1.00).add(iva);
		
		BigDecimal cargo = comision.multiply(factorIva.setScale(2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
		
		//BigDecimal efectivo = monto.subtract(cargo).setScale(2, RoundingMode.HALF_UP);
		
		//BigDecimal total = cargo.add(efectivo).setScale(2, RoundingMode.HALF_UP);
		
		
		listaTicketComprobanteFac.add("CBANCO DEL PACIFICO S.A");
		//listaTicketComprobanteFac.add("CGUAYAQUIL-PRINCIPAL");
		listaTicketComprobanteFac.add("D ");
		
		listaTicketComprobanteFac.add("ITRANSACCION:&D"+producto.getDescripcion());
		
		//listaTicketComprobanteFac.add("ISERVICIOS:&D"+producto.getDescripcionServicio());
		
		listaTicketComprobanteFac.add("IVALOR COMISION:&D"+String.valueOf(comision));
		listaTicketComprobanteFac.add("IVALOR IVA:&D"+String.valueOf(valorIva));
		//listaTicketComprobanteFac.add("ICOMPENSACION&D ");
		//listaTicketComprobanteFac.add("ISOLIDARIA 2%:&D0.00");
		listaTicketComprobanteFac.add("IVALOR TOTAL:&D"+cargo);
		//listaTicketComprobanteFac.add("IUSUARIO:&D"+tx.getUsuarioCajero());
		listaTicketComprobanteFac.add("IFECHA:&D"+Utilitaria.formatFecha(tx.getFecha(),"yyyy-MM-dd HH:mm:ss"));
		
		//if(tx.getDireccionBanco()!=null &&  !tx.getDireccionBanco().isEmpty() )
		//  listaTicketComprobanteFac.add("IN�COMPROBANTE:&D"+tx.getDireccionBanco());
		/*else if(producto.getCodProductoEspecial()==6) //Mastercard
		{
			listaTicketComprobanteFac.add("IN�COMPROBANTE:&D"+tx.getNumeroTransaccion());
		}*/
		
		//if(tx.getNumeroTransaccionComision()!=null &&  !tx.getNumeroTransaccionComision().isEmpty() )
		//listaTicketComprobanteFac.add("INUT:&D"+tx.getNumeroTransaccionComision());
		
		}
		return listaTicketComprobanteFac;
	
	}
	
			
	public ArrayList armarTicketInternacional(ParametrosRespuesta paramRespuesta) throws JAXBException
	{
		ArrayList listaTicketComprobanteFac = new ArrayList<String>();
				       
										
        BigDecimal valor = new BigDecimal(paramRespuesta.getTotalNacional()).setScale(2, RoundingMode.HALF_UP);
		
		BigDecimal cargo = new BigDecimal(paramRespuesta.getCargoNacional()).setScale(2, RoundingMode.HALF_UP);
		
		BigDecimal total = valor.add(cargo);
				
		listaTicketComprobanteFac.add("CBANCO DEL PACIFICO S.A");
		//listaTicketComprobanteFac.add("CGUAYAQUIL-PRINCIPAL");
		listaTicketComprobanteFac.add("D ");
		
		listaTicketComprobanteFac.add("ITRANSACCION:&D"+producto.getDescripcion());
		
		//listaTicketComprobanteFac.add("ISERVICIOS:&D"+producto.getDescripcionServicio());
						
		listaTicketComprobanteFac.add("IVALOR :&D"+String.valueOf(valor) +" "+paramRespuesta.getMonedaNacional());
		listaTicketComprobanteFac.add("ICARGO :&D"+String.valueOf(cargo)+" "+paramRespuesta.getMonedaNacional());
		listaTicketComprobanteFac.add("ITOTAL :&D"+String.valueOf(total)+" "+paramRespuesta.getMonedaNacional());
						
		listaTicketComprobanteFac.add("D ");
		listaTicketComprobanteFac.add("IFECHA:&D"+Utilitaria.formatFecha(tx.getFecha(),"yyyy-MM-dd HH:mm:ss"));
		
		return listaTicketComprobanteFac;
	
	}
	
	public String enmascararCedula(String cedula){
		String cedulaEnmascarada = "";
		for(int i=0; i<cedula.length(); i++) {
			if(cedula.length()==10)
                cedulaEnmascarada+=(i > 1 && i < 6) ?"X":cedula.charAt(i);
            else
                cedulaEnmascarada+=(i > 2 && i < 10) ?"X":cedula.charAt(i);
        }
		return cedulaEnmascarada;
	}
	
	public String ocultarNombre(String nombre){
		 String nombreOculto="";
		 String[] nombreSeparado = separarNombre(nombre);
		 int tamanio = nombreSeparado.length;
		 if(tamanio==2)
			 return nombre;
		 else if(tamanio==3 || tamanio==4)
			 return nombreSeparado[0]+" "+nombreSeparado[2];
		 return nombreOculto;
	}
	
    public String[] separarNombre(String nombre){
    	String[] nombreSeparado = {"","","",""};
        String[] nombreSeparadoTmp = nombre.split(" ");
        int i = 0;
        ArrayList<String> palabras = new ArrayList<String>();
        for(String palabraTmp: nombreSeparadoTmp) {
        	if(!palabraTmp.equals(""))
        		palabras.add(palabraTmp);
        }
        for(String palabra: palabras){
            if(esArticulo(palabra))
                nombreSeparado[i] = nombreSeparado[i]+palabra+" ";
            else{
                nombreSeparado[i] = nombreSeparado[i]+palabra;
                i++;
            }
        }
        return nombreSeparado;
    }

    public boolean esArticulo(String articulo){
        String[] articulos = {"de","los","la","el","los"};
        for(String art: articulos){
            if(art.toLowerCase().compareTo(articulo.toLowerCase())==0)
                return true;
        }
        return false;
    }  
	
}
