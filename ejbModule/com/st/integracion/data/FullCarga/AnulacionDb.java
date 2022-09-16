package com.st.integracion.data.FullCarga;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.st.integracion.dto.Transaccion;
import com.st.integracion.dto.FullCarga.ReversoAnulacionObj;
import com.st.integracion.dto.FullCarga.TransaccionFullCarga;





public class AnulacionDb {
	
	public long consultarTxPorNumeroTrnTablaConexion(Connection Conn , long numeroTrn) throws Exception 
	{
		long codigo = 0L;
		PreparedStatement  sentenciaSql;	    
	    ResultSet rs=null;
	    String cadena = "SELECT  cnx_codigo " +	    					
	    				"FROM public.conexion "+
	    				"Where cnx_num_transaccion = ? ";
	    
	    sentenciaSql = Conn.prepareStatement(cadena);
	    sentenciaSql.setLong(1,numeroTrn);
	    rs=sentenciaSql.executeQuery();
	    
	    if(rs.next())
	    {	    	
	    	codigo= rs.getLong("cnx_codigo");	    	    		    			    	
		}
		return codigo;
	}
	
	public long consultarEstdoTxPorNumeroTrnTablaConexion(Connection Conn , long numeroTrn) throws Exception 
	{
		long estado = 0L;
		PreparedStatement  sentenciaSql;	    
	    ResultSet rs=null;
	    String cadena = "SELECT  cnx_estado_fk " +	    					
	    				"FROM public.conexion "+
	    				"Where cnx_num_transaccion = ? ";
	    
	    sentenciaSql = Conn.prepareStatement(cadena);
	    sentenciaSql.setLong(1,numeroTrn);
	    rs=sentenciaSql.executeQuery();
	    
	    if(rs.next())
	    {	    	
	    	estado= rs.getLong("cnx_estado_fk");	    	    		    			    	
		}
		return estado;
	}
	
	
	public long consultarTxPorNumeroTrnTablaReverso(Connection Conn , long numeroTrn) throws SQLException
	{		
		long codigo = 0L;
		PreparedStatement  sentenciaSql;	    
	    ResultSet rs=null;
	    String cadena = "SELECT  rvs_codigo " +	    					
	    				"FROM public.anulaciones_reverso "+
	    				"Where rvs_num_transaccion = ? ";
	    
	    sentenciaSql = Conn.prepareStatement(cadena);
	    sentenciaSql.setLong(1,numeroTrn);
	    rs=sentenciaSql.executeQuery();
	    
	    if(rs.next())
	    {	    	
	    	codigo= rs.getLong("rvs_codigo");	    	    		    			    	
		}
		return codigo;
		
	}
	
	public boolean addRegistroTblReverso(Connection Conn ,long numTrn, long estado , Date fechaPeticion,long numIntentos, long numPeticiones , 
			                             long codProvProd, BigDecimal valorTx, String xmlInfoEmpresa, String XmlParamInTransaccion ) throws SQLException
	{
		boolean ban = false;		
		String cadena3 = "INSERT INTO public.anulaciones_reverso ( " +	
		"rvs_num_transaccion, " +  //1
		"rvs_estado, " + //2
		"rvs_fecha_peticion, " + //3		
		"rvs_numeros_intentos, "+  //4				
		"rvs_numero_peticiones, " +  //5
		"rvs_cod_proveedor_producto, " +  //6
		"rvs_valor_transaccion, "+ //7 
        "rvs_xml_info_empresa, "+ //8
        "rvs_xml_parametro_in_transaccion ) " + //9
		"VALUES( " +
		"?, " +//1
		"?, " +//2
		"?, " +//3
		"?, " +//4
		"?, " +//5
		"?, " +//6
		"?, " +//7
		"?, " +//8
		"?)";  //9 
		
		Timestamp tstFechaPeticion =  null; 
		if (fechaPeticion!= null)
		{
			tstFechaPeticion =  new Timestamp(fechaPeticion.getTime());
		}
		
		PreparedStatement sentenciaSql3 = Conn.prepareStatement(cadena3);
		sentenciaSql3.setLong(1, numTrn);
		sentenciaSql3.setLong(2, estado);
		sentenciaSql3.setTimestamp(3, tstFechaPeticion);
		sentenciaSql3.setLong(4, numIntentos);
		sentenciaSql3.setLong(5, numPeticiones);
		sentenciaSql3.setLong(6, codProvProd);		
		
		sentenciaSql3.setBigDecimal(7, valorTx);
		sentenciaSql3.setString(8, xmlInfoEmpresa);
		sentenciaSql3.setString(9, XmlParamInTransaccion);
		
		sentenciaSql3.executeUpdate();
		ban = true;
		return ban;
		
	}
	
	public boolean actualizarEnvioRegistroTblReverso(Connection Conn, long estado, Date fechaEnvio, long numIntentos , long numTrn) throws SQLException
	{
		boolean ban = false;
		String cadena3 = "UPDATE public.anulaciones_reverso  SET " +
		"rvs_estado = ?, " + //1
		"rvs_fecha_envio = ?, " + //2
		"rvs_numeros_intentos = ? " +  //3	
		"WHERE " +
		"(rvs_num_transaccion = ?)";  //4
		
		Timestamp tstFechaEnvio = null;
		if(fechaEnvio != null)
			tstFechaEnvio = new Timestamp(fechaEnvio.getTime());

		PreparedStatement sentenciaSql3 = Conn.prepareStatement(cadena3);
		
		sentenciaSql3.setLong(1, estado);
		sentenciaSql3.setTimestamp(2, tstFechaEnvio);
		sentenciaSql3.setLong(3, numIntentos);
		sentenciaSql3.setLong(4, numTrn);
		sentenciaSql3.executeUpdate();
		ban = true;
		return ban;				
	}
	
	public boolean actualizarRespuestaTblReverso(Connection Conn, long estado, Date fechaRespuesta , long numTrn) throws SQLException
	{
	    boolean ban = false;
		String cadena3 = "UPDATE public.anulaciones_reverso  SET " +
		"rvs_estado = ?, " + //1
		"rvs_fecha_respuesta = ? " + //2		
		"WHERE " +
		"(rvs_num_transaccion = ?)";  //3
		
		Timestamp tstFechaRespuesta = null;
		if(fechaRespuesta != null)
			tstFechaRespuesta = new Timestamp(fechaRespuesta.getTime());

		PreparedStatement sentenciaSql3 = Conn.prepareStatement(cadena3);
		
		sentenciaSql3.setLong(1, estado);
		sentenciaSql3.setTimestamp(2, tstFechaRespuesta);	
		sentenciaSql3.setLong(3, numTrn);
		sentenciaSql3.executeUpdate();
		ban = true;
		return ban;					
	}
	
	public boolean actualizarNumPeticionesTblReverso(Connection Conn, long numTrn) throws SQLException
	{ 
		boolean ban = false;
		PreparedStatement sentenciaSql;
		ResultSet rs=null;	
		
		String cadena =" UPDATE public.anulaciones_reverso  SET rvs_estado = 1 , rvs_numero_peticiones =(SELECT rvs_numero_peticiones  From anulaciones_reverso WHERE rvs_num_transaccion = ?) +1 " +
					   " WHERE rvs_num_transaccion = ? "+
                       " RETURNING  rvs_numero_peticiones "; 
		sentenciaSql=Conn.prepareStatement(cadena);
		sentenciaSql.setLong(1,numTrn);
		sentenciaSql.setLong(2,numTrn);
		rs=sentenciaSql.executeQuery();
	
		ban = true;
		return ban;				
	}
	
public List <ReversoAnulacionObj> listaTransaccionReversoAnulacion( Connection Conn,String fechaInicial , 
	      String fechaFinal , long codigoEstado) throws Exception
{
  List <ReversoAnulacionObj> listaTransaccionesRever= new ArrayList<ReversoAnulacionObj>();

	PreparedStatement  sentenciaSql;	    
	ResultSet rs=null;

	 
	String cadena = "SELECT rvs_codigo, rvs_num_transaccion, rvs_estado, "+
    "rvs_fecha_peticion, rvs_fecha_envio, "+ 
    "rvs_fecha_respuesta,rvs_numeros_intentos, rvs_numero_peticiones, "+  
    "rvs_cod_proveedor_producto, rvs_valor_transaccion, rvs_xml_info_empresa, rvs_xml_parametro_in_transaccion "+     		         
    "from  public.anulaciones_reverso  "+  
    "WHERE rvs_estado = ?  AND rvs_fecha_peticion "+ 
    "BETWEEN '"+fechaInicial+ "'  AND  '" +fechaFinal +"' ";	
	
	sentenciaSql = Conn.prepareStatement(cadena);
	sentenciaSql.setLong(1, codigoEstado);	
	rs=sentenciaSql.executeQuery();
	
	while(rs.next())
	{
		ReversoAnulacionObj objAnu = new ReversoAnulacionObj();			
		
		long codigo = rs.getLong("rvs_codigo"); 
		objAnu.setCodigo(codigo);
		
		long numTransaccion = rs.getLong("rvs_num_transaccion"); 
		objAnu.setNumTransaccion(numTransaccion);	  
		
		long estado = rs.getLong("rvs_estado");
		objAnu.setEstado(estado);
		
		Date fechaPeticion = rs.getTimestamp("rvs_fecha_peticion"); 
		objAnu.setFechaPeticion(fechaPeticion);
		 
		Date fechaEnvio = rs.getTimestamp("rvs_fecha_envio"); 
		objAnu.setFechaEnvio(fechaEnvio);
		
		Date fechaRespuesta = rs.getTimestamp("rvs_fecha_respuesta"); 
		objAnu.setFechaRespuesta(fechaRespuesta);
		
		long numerosIntentos = rs.getLong("rvs_numeros_intentos"); 
		objAnu.setNumerosIntentos(numerosIntentos);
		    
		long numeroPeticiones = rs.getLong("rvs_numero_peticiones");
		objAnu.setNumeroPeticiones(numeroPeticiones); 
		   
		long codigoProvProd  = rs.getLong("rvs_cod_proveedor_producto"); 
		objAnu.setCodigoProvProd(codigoProvProd);
		   
		BigDecimal valorTrn = rs.getBigDecimal("rvs_valor_transaccion");
		objAnu.setValorTrn( valorTrn);
		   
		String infoEmpresa = rs.getString("rvs_xml_info_empresa");
		objAnu.setXmlParamInfoEmpresa(infoEmpresa);
		  
		String xmlParameIntransac = rs.getString("rvs_xml_parametro_in_transaccion") ;
		objAnu.setXmlParamInTransaccion(xmlParameIntransac);
					
		listaTransaccionesRever.add(objAnu);
  }	    
 
 return listaTransaccionesRever;
 
}


public TransaccionFullCarga  consultarTransaccionalPorNumeroTx (Connection Conn , long numTrn) throws Exception 
	{	
		PreparedStatement  sentenciaSql;	    
		ResultSet rs=null;
		String cadena = "SELECT " +
		"trn.cnx_codigo_movimiento, "+ //1 
		"trn.cnx_num_transaccion, " +//2
		"trn.cnx_fecha, " +//3
		"cnx_descripcion, " +//4
		"trn.cnx_numero_cliente, " + //5
		"trn.cnx_codigo_usuario, " + //6
		"trn.cnx_valor, " + //7
		"trn.cnx_referencia_cliente, " + //8 
		"trn.cnx_parametro_xml_in, " +  //9
		"trn.cnx_identificador_host, " + //10
		"trn.cnx_referencia_proveedor, " + //11
		"trn.cnx_parametro_xml_out, " + //12
		"trn.cnx_bodega, " + //13
		"trn.cnx_proveedor_producto, " + //14 
		"trn.cnx_estado_fk, " + //15
		"trn.cnx_proceso_fk, " + //16
		"trn.cnx_transac_id, " + 	//17			    				
		"cnx_descripcion_proveedor " + //18
		"FROM " +
		"public.conexion as trn " +
		"WHERE " +
		"(trn.cnx_num_transaccion = ?) " ; //1
				
		sentenciaSql = Conn.prepareStatement(cadena);
		sentenciaSql.setLong(1, numTrn);	
		rs = sentenciaSql.executeQuery();
		TransaccionFullCarga trn = null;

		if(rs.next())
		{
			trn = new TransaccionFullCarga();
			Long codigoMovimiento = rs.getLong("cnx_codigo_movimiento"); //1
			Long numeroTransaccion = rs.getLong("cnx_num_transaccion");//2
			Date fecha = rs.getTimestamp("cnx_fecha");//3
			String descripcion = rs.getString("cnx_descripcion"); //4
			Long numeroCliente = rs.getLong("cnx_numero_cliente"); 	//5
			Long codigoUsuario = rs.getLong("cnx_codigo_usuario"); //6
			BigDecimal importe = rs.getBigDecimal("cnx_valor"); //7
			String nicNpe = rs.getString("cnx_referencia_cliente");//8 
			String parametroXmlIn = rs.getString("cnx_parametro_xml_in"); //9
			String identificadorClienteHost = rs.getString("cnx_identificador_host"); //10
			String referenciaProveedor = rs.getString("cnx_referencia_proveedor"); //11
			String parmetroXmlOut = rs.getString("cnx_parametro_xml_out"); //12
			//Long codigoBodega = rs.getLong("cnx_bodega"); //13
			Long numeroProveedorProd = rs.getLong("cnx_proveedor_producto"); //14
			Integer codigoEstado = rs.getInt("cnx_estado_fk"); //15
			Integer codigoProceso = rs.getInt("cnx_proceso_fk");  //16
			Long trannsacId = rs.getLong("cnx_transac_id"); //17    				
			String	descripcionProveedor = rs.getString("cnx_descripcion_proveedor" ); //18
		
			trn.setCodigoMovimiento(codigoMovimiento); 
			trn.setNumTransaccion(numeroTransaccion); 
			trn.setFecha(fecha);
			
			trn.setDescripcion(descripcion); 
			trn.setCodigoCliente(numeroCliente); 
			trn.setCodigoUsuario(codigoUsuario); 
			trn.setValorContable(importe);
			trn.setReferenciaCliente(nicNpe);
			trn.setMvmParametroXmlIn(parametroXmlIn); 
			trn.setIdentificadorHost(identificadorClienteHost); 
			trn.setReferenciaProveedor(referenciaProveedor); 
			trn.setMvmParametroXmlOut(parmetroXmlOut);
			//trn.setBodega(codigoBodega);
			trn.setCodigoProveedorProducto(numeroProveedorProd); 
			trn.setCodigoEstado(codigoEstado);
			trn.setCodigoProceso(codigoProceso);//16
			trn.setTransacId(trannsacId);//17			
			trn.setDescripcion(descripcionProveedor); //18			
		}
		return trn;
	}	

}
