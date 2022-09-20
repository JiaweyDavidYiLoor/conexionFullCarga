package com.st.integracion.data.FullCarga;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.st.integracion.dto.FullCarga.ConfiguracionCorreo;
import com.st.integracion.dto.FullCarga.DatosConfiguracion;
import com.st.integracion.dto.FullCarga.DatosConfiguracionWsdl;
import com.st.integracion.dto.FullCarga.Empresa;
import com.st.integracion.dto.FullCarga.EmpresaClienteRoll;
import com.st.integracion.dto.FullCarga.Producto;
import com.st.integracion.dto.FullCarga.Productos;
import com.st.integracion.dto.FullCarga.Transaccional;
import com.st.integracion.servicios.PropiedadesProveedorProducto;


public class Datos {

	public  boolean insertarTransaccional (Connection Conn,	Long codigoMovimiento,Long numTransaccion , java.util.Date fecha ,
			java.util.Date fechaRecibidaColaPeticion, String descripcion , Long codigoCliente , Long codigoUsuario , BigDecimal valorContable ,
			String referenciaCliente, String mvmParametroXmlIn , String identificadorHost , String referenciaProveedor ,
			String mvmParametroXmlOut , Long codigoBodega, Long codigoProveedorProducto, int codigoEstado , int codigoProceso)  throws Exception
	{
		boolean ban = false;
		PreparedStatement  sentenciaSql3;		

		String cadena3 = "INSERT INTO public.conexion( " +
		"cnx_codigo_movimiento, " + //1
		"cnx_num_transaccion, " + //2
		"cnx_fecha, " +     //3
		"cnx_fecha_recibida_cola_peticion_cmp_pin_internet, " + //4
		"cnx_descripcion, " + //5
		"cnx_numero_cliente, " + //6
		"cnx_codigo_usuario, " +  //7
		"cnx_valor, " +  //8
		"cnx_referencia_cliente, " +  //9
		"cnx_parametro_xml_in, " +  //10
		"cnx_identificador_host, " +  //11
		"cnx_referencia_proveedor, " +  //12
		"cnx_parametro_xml_out, " +  //13
		"cnx_bodega, " +   //14
		"cnx_proveedor_producto, " +  //15
		"cnx_estado_fk, " +   //16
		"cnx_proceso_fk ) " +  //17
		"VALUES( " +
		"?, " +
		"?, " +
		"?, " +
		"?, " +
		"?, " +
		"?, " +
		"?, " +
		"?, " +
		"?, " +
		"?, " +
		"?, " +
		"?, " +
		"?, " +
		"?, " +
		"?, " +
		"?, " +
		"?)"; 
		Timestamp tstFecha =  new Timestamp(fecha.getTime()); 
		Timestamp tstFechaRecibidaColaPeticion =  new Timestamp(fechaRecibidaColaPeticion.getTime());
		sentenciaSql3 = Conn.prepareStatement(cadena3);
		sentenciaSql3.setLong(1,codigoMovimiento);
		sentenciaSql3.setLong(2, numTransaccion);
		sentenciaSql3.setTimestamp(3, tstFecha);
		sentenciaSql3.setTimestamp(4, tstFechaRecibidaColaPeticion);
		sentenciaSql3.setString(5, descripcion);
		sentenciaSql3.setLong(6, codigoCliente);
		sentenciaSql3.setLong(7, codigoUsuario);
		sentenciaSql3.setDouble(8, valorContable.doubleValue());
		sentenciaSql3.setString(9, referenciaCliente);
		sentenciaSql3.setString(10, mvmParametroXmlIn);
		sentenciaSql3.setString(11, identificadorHost);
		sentenciaSql3.setString(12, referenciaProveedor);
		sentenciaSql3.setString(13, mvmParametroXmlOut);
		sentenciaSql3.setLong(14, codigoBodega);
		sentenciaSql3.setLong(15, codigoProveedorProducto );
		sentenciaSql3.setLong(16,codigoEstado);
		sentenciaSql3.setLong(17, codigoProceso);
		sentenciaSql3.executeUpdate();
		ban = true;
		return ban;
	}
	


	public boolean actualizarTransaccionalEnvioCompraPinIntenet(Connection Conn, Date fechaEnvioCompraPinInternet,
			int codigoEstado, int codigoProceso, Long transacId, Long numeroTicket) throws Exception
	{
		boolean ban = false;
		String cadena3 = "UPDATE public.conexion  SET " +
		"cnx_fecha_envio_cmp_pin_internet = ?, " +
		"cnx_estado_fk = ?, " +
		"cnx_proceso_fk = ?, " +
		"cnx_transac_id = ? " +
		"WHERE " +
		"(cnx_num_transaccion = ?)";
		Timestamp tstFechaEnvioCompraPinInternet = null;
		if(fechaEnvioCompraPinInternet != null)
			tstFechaEnvioCompraPinInternet = new Timestamp(fechaEnvioCompraPinInternet.getTime());

		PreparedStatement sentenciaSql3 = Conn.prepareStatement(cadena3);
		sentenciaSql3.setTimestamp(1, tstFechaEnvioCompraPinInternet);
		sentenciaSql3.setLong(2, codigoEstado);
		sentenciaSql3.setLong(3, codigoProceso);
		sentenciaSql3.setLong(4, transacId);
		sentenciaSql3.setLong(5, numeroTicket);
		sentenciaSql3.executeUpdate();
		ban = true;
		return ban;
	}
	
	public boolean insertarTransaccionalDetalleEnvioCompraPinInternet(Connection Conn,
			Long codigoMovimiento,
			Long numTransaccion,
			Long transacId,
			String numeroTransaccion,
			Date fechaTransaccion,
			BigDecimal valor,
			String fechaConciliacionStr
			)
	throws Exception
	{
		boolean ban = false;

		String cadena3 = "INSERT INTO  public.conexion_detalle ( " +
		"cnx_det_codigo_movimiento, " +			//1  
		"cnx_det_num_transaccion, " +			//2
		"cnx_det_transac_id, " +				//3
		"cnx_det_numero_transaccion, " +		//4
		"cnx_det_fecha_transaccion, " +			//5
		"cnx_det_valor) " +						//6
		
		"VALUES( " +
		"?, " +									//1
		"?, " +									//2
		"?, " +									//3
		"?, " +									//4
		"?, " +									//5
		"?)";									//6  

		Timestamp tstFechaTransaccion = null;
		
		if(fechaTransaccion!=null)
			tstFechaTransaccion = new Timestamp(fechaTransaccion.getTime());		

		PreparedStatement sentenciaSql3 = Conn.prepareStatement(cadena3);
		sentenciaSql3.setLong(1, codigoMovimiento);
		sentenciaSql3.setLong(2, numTransaccion);
		sentenciaSql3.setLong(3, transacId);
		sentenciaSql3.setString(4, numeroTransaccion);
		sentenciaSql3.setTimestamp(5, tstFechaTransaccion);
		sentenciaSql3.setBigDecimal(6, valor);
				
		sentenciaSql3.executeUpdate();
		ban = true;
		return ban;
	}
	
	//corregir
	public boolean insertarTransaccionalDetalleEnvioAnulacion(Connection Conn,
			Long codigoMovimiento,
			Long numeroTransaccion,
			Date fechaEnvio,
			Long transacId,
			String valor)
	throws Exception
	{
		boolean ban = false;

		String cadena3 = "INSERT INTO  public.conexion_detalle ( " +
		"cnx_det_codigo_movimiento, " +
		"cnx_det_num_transaccion, " +
		"cnx_det_transac_id, " +
		"cnx_det_numero_transaccion, " +	
		"cnx_det_fecha_transaccion, " +
		"cnx_det_valor) " +
		"VALUES( " +
		"?, " +
		"?, " +
		"?, " +
		"?, " +
		"?, " +
		"?) ";


		Timestamp tstFechaEnvio = null;
		if(fechaEnvio!=null)
			tstFechaEnvio = new Timestamp(fechaEnvio.getTime());
		PreparedStatement sentenciaSql3 = Conn.prepareStatement(cadena3);
		sentenciaSql3.setLong(1, codigoMovimiento);
		sentenciaSql3.setLong(2, numeroTransaccion);
		sentenciaSql3.setLong(3, transacId);
		sentenciaSql3.setLong(4, numeroTransaccion);
		sentenciaSql3.setTimestamp(5, tstFechaEnvio);
		sentenciaSql3.setString(6, valor);
		sentenciaSql3.executeUpdate();
		ban = true;
		return ban;
	}		

	public Map<Long,Producto> consultarProductos (Connection Conn) throws Exception 
	{		
		PreparedStatement sentenciaSql;
		Map<Long,Producto> hm = new HashMap<Long,Producto> ();
		ResultSet rs=null;
		//prd_tipo
		String cadena = " select pdr_full_carga, prd_codigo, prd_descripcion, prd_estado_fk, prd_producto, prd_empresa_st_codigo, "+		   				
		   				"prd_empresa_st_descripcion from producto " ;
		sentenciaSql=Conn.prepareStatement(cadena);
		rs=sentenciaSql.executeQuery();
		while(rs.next())
		{
			Producto prd = new Producto();
			int prdFC = rs.getInt("pdr_full_carga");
			Long codigo = rs.getLong("prd_codigo");
			String prdDescripcion = rs.getString("prd_descripcion");
			Long prdEstadoFk = rs.getLong("prd_estado_fk");
			Long prdProducto = rs.getLong("prd_producto");
			Long prdCodigoEmpresa = rs.getLong("prd_empresa_st_codigo");
			String prdDescripcionEmpresa = rs.getString("prd_empresa_st_descripcion");
			//String prdTipo=rs.getString("prd_tipo");

			prd.setIdfc(prdFC);
			prd.setCodigo(codigo);
			prd.setDescripcion(prdDescripcion);
			prd.setEstadoFk(prdEstadoFk);
			prd.setCodigoEmpresa(prdCodigoEmpresa);
			prd.setProducto(prdProducto);		
			prd.setDescripcionEmpresa(prdDescripcionEmpresa);
			//prd.setTipoProducto(prdTipo);
		
			hm.put(prdProducto,prd);
		}	    
		return hm;
	}
	
	public boolean actualizarTransaccionalRespuesta(Connection Conn,
			int codigoEstado,
			int codigoProceso,
			Date fechaRespuesta ,
			String referencia ,
			String descripcionProveedor,
			Long numeroTransaccion, 
			Long codigoMovimiento,
			Long transacId,
			String codigoRespuesta)  throws Exception
			{
		boolean ban = false;
		PreparedStatement  sentenciaSql3;		

		String cadena3 = "UPDATE " +
		"public.conexion " +
		"SET " +
		"cnx_estado_fk = ?, " +
		"cnx_proceso_fk = ?, " +
		"cnx_referencia_proveedor = ?, " +
		"cnx_descripcion_proveedor = ?, "+
		"cnx_fecha_respuesta_comprar_pin_internet = ?, " +
		"cnx_codigo_respuesta = ? " +
		"WHERE " +
		"(cnx_codigo_movimiento = ?)AND "+
		"(cnx_num_transaccion = ?)";

		Timestamp tstFechaRespuestaRecarga =  null; 
		if (fechaRespuesta!= null)
		{
			tstFechaRespuestaRecarga =  new Timestamp(fechaRespuesta.getTime());
		}

		sentenciaSql3 = Conn.prepareStatement(cadena3);
		sentenciaSql3.setInt(1, codigoEstado);
		sentenciaSql3.setInt(2, codigoProceso);		
		sentenciaSql3.setString(3, referencia);
		sentenciaSql3.setString(4, descripcionProveedor);
		sentenciaSql3.setTimestamp(5,tstFechaRespuestaRecarga);
		sentenciaSql3.setString(6,codigoRespuesta);
		sentenciaSql3.setLong(7,codigoMovimiento);
		sentenciaSql3.setLong(8,numeroTransaccion);
		sentenciaSql3.executeUpdate();
		ban = true;		
		return ban;

			}

	public List<String> consultarTramasPorNumeroTransaccion(Connection Conn,
			Long numeroTransaccion)throws Exception{
		List<String> retorno = new ArrayList<String>();
		List<String> reversoReq = new ArrayList<String>();
		List<String> reversoRes = new ArrayList<String>();
		
		PreparedStatement sentenciaSql;
		ResultSet rs = null;
		
		String cadena = "Select " +
				"trn.cnx_det_transac_id," +
				"trn.cnx_det_etapa_trn_st, " +
				"trn.cnx_det_trn_requerimiento, " +
				"trn.cnx_det_trn_respuesta " +
				/*"trn.cnx_det_reverso_requerimiento, " +
				"trn.cnx_det_reverso_respuesta " +*/
				"FROM " +
				"public.conexion_detalle as trn " +
				"WHERE " +
				"(trn.cnx_det_num_transaccion = ?)" +
				"ORDER BY trn.cnx_det_transac_id ASC";
		
		sentenciaSql = Conn.prepareStatement(cadena);
		sentenciaSql.setLong(1, numeroTransaccion);
		
		rs = sentenciaSql.executeQuery();
		
		while(rs.next())
		{
			String etapaTrn = rs.getString("cnx_det_etapa_trn_st");
			String tramaRequerimiento = rs.getString("cnx_det_trn_requerimiento");
			String tramaRespuesta = rs.getString("cnx_det_trn_respuesta");
			//String tramaReversoRequerimiento = rs.getString("cnx_det_reverso_requerimiento");
			//String tramaReversoRespuesto = rs.getString("cnx_det_reverso_respuesta");
			if(etapaTrn!=null){
				if(etapaTrn.compareTo("TX")==0){
					//retorno.add(etapaTrn);
					if(tramaRequerimiento!=null)
						retorno.add(tramaRequerimiento);
					else
						retorno.add("");
					if(tramaRespuesta!=null)
						retorno.add(tramaRespuesta);
					else
						retorno.add("No hubo Respuesta");
				}else if(etapaTrn.compareTo("TX_REVERSO")==0){
					if(tramaRequerimiento!=null)
						reversoReq.add(tramaRequerimiento);
					else
						reversoReq.add("");
					if(tramaRespuesta!=null)
						reversoRes.add(tramaRespuesta);
					else
						reversoRes.add("No hubo Respuesta");
				}			
			}else{
				retorno.add("No existen datos para presentar");
				return retorno;
			}
		}
		//retorno.add("TX_REVERSO");
		int size = reversoReq.size();
		if(size==2){
			retorno.add(reversoReq.get(0));
			retorno.add(reversoRes.get(0));
		}else if(size>2){
			retorno.add(reversoReq.get(size-2));		
			retorno.add(reversoRes.get(size-2));
		}
		return retorno;
	}
	
	public boolean insertarTramaTransaccionRequerimiento(Connection Conn,
			Long numeroTransaccion, 
    		Long codigoMovimiento, 
    		Long transacId,
    		String tramaTrnRequerimiento) throws Exception{
		
		boolean ban = false;
		
		PreparedStatement  sentenciaSql3;		

		String cadena3 = "UPDATE " +
		"public.conexion_detalle " +
		"SET " +
		"cnx_det_trn_requerimiento = ? " +
		"WHERE " +	
		"(cnx_det_num_transaccion = ?) AND " +  			
		"(cnx_det_codigo_movimiento = ?) AND " +  			
		"(cnx_det_transac_id = ?)"; 
		
		sentenciaSql3 = Conn.prepareStatement(cadena3);		
		sentenciaSql3.setString(1,tramaTrnRequerimiento);
		sentenciaSql3.setLong(2,numeroTransaccion);
		sentenciaSql3.setLong(3,codigoMovimiento);
		sentenciaSql3.setLong(4,transacId);
		
		sentenciaSql3.executeUpdate();
		
		ban = true;
		return ban;
	}
	public boolean insertarTramaTransaccionRespuesta(Connection Conn,
			Long numeroTransaccion, 
    		Long codigoMovimiento, 
    		Long transacId,
    		String tramaTrnRespuesta) throws Exception{
		
		boolean ban = false;
		
		PreparedStatement  sentenciaSql3;		

		String cadena3 = "UPDATE " +
		"public.conexion_detalle " +
		"SET " +
		"cnx_det_trn_respuesta = ?" +
		"WHERE " +	
		"(cnx_det_num_transaccion = ?) AND " +  			
		"(cnx_det_codigo_movimiento = ?) AND " +  			
		"(cnx_det_transac_id = ?)"; 
		
		sentenciaSql3 = Conn.prepareStatement(cadena3);		
		sentenciaSql3.setString(1,tramaTrnRespuesta);
		sentenciaSql3.setLong(2,numeroTransaccion);
		sentenciaSql3.setLong(3,codigoMovimiento);
		sentenciaSql3.setLong(4,transacId);
		
		sentenciaSql3.executeUpdate();
		
		ban = true;
		return ban;
	}

	public boolean actualizarTransaccionalDetalleRespuesta(Connection Conn,
			Long numeroTransaccion, 
    		Long codigoMovimiento, 
    		Long transacId,
    		String codigoRetorno,
    		String mensajeRetorno,
    		String tramaReq,
    		String tramaResp, 
    		String tipoProducto)
	throws Exception
	{
		boolean ban = false;
		PreparedStatement  sentenciaSql3;		

		String cadena3 = "UPDATE " +
		"public.conexion_detalle " +
		"SET " +	
		"cnx_det_codigo_retorno = ? ," +				//1
		"cnx_det_mensaje_retorno = ? ," +				//2
		"cnx_det_trama_requerimiento = ?, " +					//3
		"cnx_det_trama_respùesta = ?, " +					//4
		"cnx_det_tipo_producto = ? " +	 //5
		"WHERE " +	
		"(cnx_det_num_transaccion = ?) AND " +  		//6
		"(cnx_det_codigo_movimiento = ?) AND " +  		//7
		"(cnx_det_transac_id = ?)";                		//8

		sentenciaSql3 = Conn.prepareStatement(cadena3);	 		

		sentenciaSql3.setString(1,codigoRetorno);
		sentenciaSql3.setString(2,mensajeRetorno);
		sentenciaSql3.setString(3, tramaReq);
		sentenciaSql3.setString(4, tramaResp);
		sentenciaSql3.setString(5, tipoProducto);
		sentenciaSql3.setLong(6,numeroTransaccion);
		sentenciaSql3.setLong(7,codigoMovimiento);
		sentenciaSql3.setLong(8,transacId);
		
		sentenciaSql3.executeUpdate();
		ban = true;		
		return ban;		
	}

	public boolean actualizarTransaccionalEstadoPorNumeroticketFinal(Connection Conn,						
			int codigoEstado,
			int codigoProceso,
			String referencia,
			Long numTransaccion,
			Long codigoMovimiento,
			String descripcionProveedor )  throws Exception
			{
		boolean ban = false;
		PreparedStatement  sentenciaSql3;		

		String cadena3 = "UPDATE " +
		"public.conexion  " +
		"SET " +
		"cnx_estado_fk = ? ," +
		"cnx_proceso_fk = ? ," +
		"cnx_referencia_proveedor = ? ," +
		"cnx_descripcion_proveedor = ? " +
		"WHERE " +
		"(cnx_num_transaccion = ?)AND "+
		"(cnx_codigo_movimiento = ?)"; 		


		sentenciaSql3 = Conn.prepareStatement(cadena3);

		sentenciaSql3.setLong(1,codigoEstado);
		sentenciaSql3.setLong(2,codigoProceso);
		sentenciaSql3.setString(3,referencia);
		sentenciaSql3.setString(4,descripcionProveedor);
		sentenciaSql3.setLong(5,numTransaccion);
		sentenciaSql3.setLong(6,codigoMovimiento);
		
		sentenciaSql3.executeUpdate();
		ban = true;		
		return ban;

			}
	
	
	
	public boolean actualizarTransaccionAnulacionEstadoPorNumeroticketFinal(Connection Conn,						
			int codigoEstado,
			int codigoProceso,			
			Long numTransaccion,
			Long codigoMovimiento,
			String descripcionProveedor )  throws Exception
			{
		boolean ban = false;
		PreparedStatement  sentenciaSql3;		

		String cadena3 = "UPDATE " +
		"public.conexion  " +
		"SET " +
		"cnx_estado_fk = ? ," + //1
		"cnx_proceso_fk = ? ," +  //2
		"cnx_descripcion_proveedor = ? " +
		"WHERE " +
		"(cnx_num_transaccion = ?)AND "+
		"(cnx_codigo_movimiento = ?)"; 		


		sentenciaSql3 = Conn.prepareStatement(cadena3);

		sentenciaSql3.setLong(1,codigoEstado);
		sentenciaSql3.setLong(2,codigoProceso);

		sentenciaSql3.setString(3,descripcionProveedor);
		sentenciaSql3.setLong(4,numTransaccion);
		sentenciaSql3.setLong(5,codigoMovimiento);
		
		sentenciaSql3.executeUpdate();
		ban = true;		
		return ban;
			}

	public long[] consultarProductosProveedor (Connection Conn) throws Exception 
	{
		PreparedStatement sentenciaSql;
		ResultSet rs=null;
		ResultSet rsC=null;

		String cadenaPrevia = " SELECT count(distinct prd_codigo) as cantidad from producto ";
		sentenciaSql=Conn.prepareStatement(cadenaPrevia);
		rsC=sentenciaSql.executeQuery();
		int cantidad=0;
		while(rsC.next())
		{
			cantidad=rsC.getInt("cantidad");
		}
		String cadena =" SELECT distinct prd_codigo as prd_codigo from producto ";
		sentenciaSql=Conn.prepareStatement(cadena);
		rs=sentenciaSql.executeQuery();
		long []proveedores = new long[cantidad];
		while(rs.next())
		{
			Long codigoProveedorProducto = rs.getLong("prd_codigo");
			proveedores[rs.getRow()-1]=codigoProveedorProducto;
		}	    
		return proveedores;
	}

	public Map<Long, PropiedadesProveedorProducto> consultarPropiedadesProductosProveedor (Connection Conn) throws Exception 
	{
		PreparedStatement sentenciaSql;
		ResultSet rs=null;
		String cadena =" SELECT distinct prd_codigo, prd_estado_fk, prd_descripcion from producto ";
		sentenciaSql=Conn.prepareStatement(cadena);
		rs=sentenciaSql.executeQuery();
		Map<Long,PropiedadesProveedorProducto> hm = new HashMap<Long,PropiedadesProveedorProducto> ();
		while(rs.next())
		{
			Long codigoProveedorProducto = rs.getLong("prd_codigo");
			Long codigoEstado = rs.getLong("prd_estado_fk");
			String descripcionEstado = rs.getString("prd_descripcion");
			PropiedadesProveedorProducto propiedadesProductoProveedor = new PropiedadesProveedorProducto();
			propiedadesProductoProveedor.setCodigoEstadoFk(codigoEstado);
			propiedadesProductoProveedor.setDescripcionEstadoFk(descripcionEstado);
			hm.put(codigoProveedorProducto, propiedadesProductoProveedor);
		}	    
		return hm;
	}
	


	public boolean actualizarTransaccionalEnvioAnulacion(Connection Conn,			

			int codigoEstado,
			int codigoProceso,
			long transacId,
			Long codigoMovimiento,
			Long numTransaccion,
			Date fechaEnvioAnulacionCompraPinInternet)  throws Exception
			{
		boolean ban = false;
		PreparedStatement  sentenciaSql3;		

		String cadena3 = "UPDATE " +
		"public.conexion " +
		"SET " +							
		"cnx_estado_fk = ?, " + //1
		"cnx_proceso_fk = ?, " + //2
		"cnx_transac_id = ?, " + //3
		"cnx_fecha_envio_anulacion_cmp_pin_internet = ? " + //4
		"WHERE " +
		"(cnx_codigo_movimiento = ?) AND " + //5
		"(cnx_num_transaccion = ?) ";  //6

        
		Timestamp tstfechaEnvioAnulacionCompraPinInternet =  null; 
		if (fechaEnvioAnulacionCompraPinInternet!= null)
		{
			tstfechaEnvioAnulacionCompraPinInternet =  new Timestamp(fechaEnvioAnulacionCompraPinInternet.getTime());
		}
		
		sentenciaSql3 = Conn.prepareStatement(cadena3);


		sentenciaSql3.setLong(1,codigoEstado);
		sentenciaSql3.setLong(2,codigoProceso);
		sentenciaSql3.setLong(3,transacId);
		sentenciaSql3.setTimestamp(4,tstfechaEnvioAnulacionCompraPinInternet);
		sentenciaSql3.setLong(5,codigoMovimiento);
		sentenciaSql3.setLong(6,numTransaccion);
		sentenciaSql3.executeUpdate();
		ban = true;		
		return ban;

			}

	
	public boolean actualizarAnulacionNoRealizada(Connection Conn,			

			int codigoEstado,
			int codigoProceso,
			long transacId,
			Long codigoMovimiento,
			Long numTransaccion,
			Date fechaRespuestaAnulacionPinInternet,
			String codigoRespuesta ,
			String descripcioError)  throws Exception
			{
		boolean ban = false;
		PreparedStatement  sentenciaSql3;		

		String cadena3 = "UPDATE " +
		"public.conexion " +
		"SET " +							
		"cnx_estado_fk = ?, " + //1
		"cnx_proceso_fk = ?, " + //2
		"cnx_transac_id = ?, " + //3
		"cnx_fecha_respuesta_anulacion_cmp_pin_internet = ?, " + //4
		"cnx_codigo_respuesta = ?, " + //5
		"cnx_descripcion_proveedor = ? " + //6
		"WHERE " +
		"(cnx_codigo_movimiento = ?) AND " + //7
		"(cnx_num_transaccion = ?) ";  //8

        
		Timestamp tstfechaEnvioAnulacionCompraPinInternet =  null; 
		if (fechaRespuestaAnulacionPinInternet!= null)
		{
			tstfechaEnvioAnulacionCompraPinInternet =  new Timestamp(fechaRespuestaAnulacionPinInternet.getTime());
		}
		
		sentenciaSql3 = Conn.prepareStatement(cadena3);


		sentenciaSql3.setLong(1,codigoEstado);
		sentenciaSql3.setLong(2,codigoProceso);
		sentenciaSql3.setLong(3,transacId);
		sentenciaSql3.setTimestamp(4,tstfechaEnvioAnulacionCompraPinInternet);
		sentenciaSql3.setString(5,codigoRespuesta);
		sentenciaSql3.setString(6,descripcioError);
		sentenciaSql3.setLong(7,codigoMovimiento);
		sentenciaSql3.setLong(8,numTransaccion);
		sentenciaSql3.executeUpdate();
		ban = true;		
		return ban;

			}

	public List<Transaccional>  consultarTransaccionalPendientes (Connection Conn) throws Exception 
	{
		int estado1 = 1;//Recarga en proceso
		int estado2 = 4;//Anulacion en proceso
		int estado3 = 7;//Recibida del MDB para proceso de recarga
		//int estado4 = 8;//Recibida del MDB para proceso de consulta de recarga
		int estado4 = 9;//Recibida del MDB para proceso de Anulacion
		//int estado6 = 10;//Recibida del MDB para proceso de consulta de Anulacion
		//int estado7 = 71;//Compra de pin de internet en proceso
		//int estado8 = 77;//Recibida del MDB para proceso de compra de pin de internet 
		//int estado9 = 78;//Recibida del MDB para proceso de consulta de la compra de pin de internet
		//int estado5 = 57;//Compra de pin  en proceso
		//int estado6 = 60;//Anulacion de la compra de pin  en proceso
		//int estado7 = 63;//Recibida del MDB para proceso de compra de pin
		//int estado8 = 65;//Recibida del MDB para proceso de Anulacion de la compra de pin

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
		"cnx_fecha_recibida_cola_peticion_cmp_pin_internet, " + //18  
		"cnx_fecha_envio_recarga, " + //19
		"cnx_fecha_respuesta_recarga, " +  //20
		"cnx_fecha_recibida_cola_peticion_anulacion, " + //24 
		"cnx_fecha_envio_anulacion, " + //25
		"cnx_fecha_respuesta_anulacion, " +  //26
		"cnx_descripcion_proveedor " + //30
		"FROM " +
		"public.conexion as trn " +
		"WHERE " +
		"((trn.cnx_estado_fk = ?) OR " +//1
		"(trn.cnx_estado_fk = ?) OR " +//2
		"(trn.cnx_estado_fk = ?) OR " +//3
		"(trn.cnx_estado_fk = ?)) AND " +
		"(trn.cnx_fecha_recibida_cola_peticion_cmp_pin_internet between now()::timestamp - '24 hours'::interval and now()) ";//4

		sentenciaSql = Conn.prepareStatement(cadena);
		sentenciaSql.setInt(1, estado1);
		sentenciaSql.setInt(2, estado2);
		sentenciaSql.setInt(3, estado3);
		sentenciaSql.setInt(4, estado4);



		rs = sentenciaSql.executeQuery();
		List<Transaccional> lTrn = new ArrayList<Transaccional>();

		while(rs.next())
		{
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
			Long codigoBodega = rs.getLong("cnx_bodega"); //13
			Long numeroProveedor = rs.getLong("cnx_proveedor_producto"); //14
			Integer codigoEstado = rs.getInt("cnx_estado_fk"); //15
			Integer codigoProceso = rs.getInt("cnx_proceso_fk");  //16
			Long trannsacId = rs.getLong("cnx_transac_id"); //17    	

			Date fechaRecibidaColaPeticionRecarga =	rs.getTimestamp("cnx_fecha_recibida_cola_peticion_cmp_pin_internet"); //18
			Date fechaEnvioRecarga = rs.getTimestamp("cnx_fecha_envio_cmp_pin_internet" ); //19
			Date fechaRespuestaRecarga = rs.getTimestamp("cnx_fecha_respuesta_comprar_pin_internet"); //20
			Date fechaRecibidaColaPeticionAnulacion =rs.getTimestamp( "cnx_fecha_recibida_cola_peticion_anulacion" ); //21
			Date fechaEnvioAnulacion =	 rs.getTimestamp("cnx_fecha_envio_anulacion" ); //22
			Date fechaRespuestaAnulacion = rs.getTimestamp("cnx_fecha_respuesta_anulacion" ); //23
			String	descripcionProveedor = rs.getString("cnx_descripcion_proveedor" ); //24

			Transaccional trn = new Transaccional ();

			trn.setCodigoMovimiento(codigoMovimiento); 
			trn.setNumTransaccion(numeroTransaccion); 
			trn.setFecha(fecha);
			trn.setDescripcion(descripcion); 
			trn.setNumeroCliente(numeroCliente); 
			trn.setCodigoUsuario(codigoUsuario); 
			trn.setValorContable(importe);
			trn.setReferenciaCliente(nicNpe);
			trn.setParametroXmlIn(parametroXmlIn); 
			trn.setIdentificadorHost(identificadorClienteHost); 
			trn.setReferenciaProveedor(referenciaProveedor); 
			trn.setParametroXmlOut(parmetroXmlOut);
			trn.setBodega(codigoBodega);
			trn.setProveedorProducto(numeroProveedor); 
			trn.setEstadoFk(codigoEstado);
			trn.setProcesoFk(codigoProceso);//16
			trn.setTransacId(trannsacId);//17
			trn.setFechaRecibidaColaPeticionRecarga(fechaRecibidaColaPeticionRecarga); //18
			trn.setFechaEnvioRecarga(fechaEnvioRecarga);//19
			trn.setFechaRespuestaRecarga(fechaRespuestaRecarga); //20
			trn.setFechaRecibidaColaPeticionAnulacion(fechaRecibidaColaPeticionAnulacion);//24
			trn.setFechaEnvioAnulacion(fechaEnvioAnulacion);//25
			trn.setFechaRespuestaAnulacion(fechaRespuestaAnulacion);//26
			trn.setDescripcionProveedor(descripcionProveedor); //30

			lTrn.add(trn);
		}
		return lTrn;
	}

	public void corregirTransaccion(Connection Conn,long codigoEstado,long codigoProceso ,String referenciaProveedor,
            String descripcionProveeedor, long codigoMovimiento ,long numTransaccion) throws Exception 
{
	//boolean ban = false;
	PreparedStatement  sentenciaSql3;		
	
	String cadena3 = "UPDATE " +
	"public.conexion  " +
		"SET " +
		"cnx_estado_fk = ?, " + //1
		"cnx_proceso_fk= ?, " + //2							
		"cnx_referencia_proveedor= ?, " + //3
		"cnx_descripcion_proveedor= ? " + //4							
	"WHERE " +
		"(cnx_codigo_movimiento = ?) and " + //5
		"(cnx_num_transaccion = ? )"; 	//6	
	
	sentenciaSql3 = Conn.prepareStatement(cadena3);
	
	sentenciaSql3.setLong(1,codigoEstado);
	sentenciaSql3.setLong(2,codigoProceso);	    
	sentenciaSql3.setString(3,referenciaProveedor);
	sentenciaSql3.setString(4,descripcionProveeedor);
	sentenciaSql3.setLong(5,codigoMovimiento);
	sentenciaSql3.setLong(6,numTransaccion);
	sentenciaSql3.executeUpdate();		
	return ;
}

	
	public Map<Long,DatosConfiguracion> consultarConfiguracion(Connection Conn) throws Exception 
	{

        PreparedStatement sentenciaSql;
		ResultSet rs=null;
		
		 
		String cadena ="Select cnf_codigo, cnf_cod_empresa, cnf_nombre_empresa, cnf_path_url_ws, "+
		"cnf_puerto_ws, cnf_ip_ws, cnf_cod_pais_origen, cnf_cod_pais_destino, "+
		"cnf_ip_admin_consulta_pais from configuracion ";
		sentenciaSql=Conn.prepareStatement(cadena);
		rs=sentenciaSql.executeQuery();
	 
		Map<Long,DatosConfiguracion> mapaDatosConfiguracion = new HashMap<Long, DatosConfiguracion>(); 
		
		while(rs.next())
		{
			DatosConfiguracion datosConf = new DatosConfiguracion ();
			 Long codigo =  rs.getLong("cnf_codigo");
			 Long codigoEmpresa = rs.getLong("cnf_cod_empresa");
			 String nombreEmpresa = rs.getString("cnf_nombre_empresa");
			 String pathUrlWs = rs.getString("cnf_path_url_ws");
			 Long puertoWs = rs.getLong("cnf_puerto_ws");
			 String ipWs = rs.getString("cnf_ip_ws");
			 Long codigoPaisOrigen = rs.getLong("cnf_cod_pais_origen");
			 Long codigoPaisDestino = rs.getLong("cnf_cod_pais_destino");
			 String ipAdminConsultaPais = rs.getString("cnf_ip_admin_consulta_pais");
			 			 			 		 		    		    			 
			 datosConf.setCodigo(codigo);
			 datosConf.setEmpresa(codigoEmpresa);
			 datosConf.setNombreEmpresa(nombreEmpresa);
			 datosConf.setPathUrlWs(pathUrlWs);
			 datosConf.setPuertoWs(puertoWs);
			 datosConf.setIpWs(ipWs);
			 datosConf.setCodPaisOrigen(codigoPaisOrigen);
			 datosConf.setCodPaisDestino(codigoPaisDestino);
			 datosConf.setIpAdminConsultaPais(ipAdminConsultaPais);
			 					 
			 mapaDatosConfiguracion.put(codigo, datosConf);
		}	
		
		return mapaDatosConfiguracion;				
	}
	
	
	public Map<Long,DatosConfiguracionWsdl> consultarConfiguracionWsdl(Connection Conn) throws Exception 
	{

        PreparedStatement sentenciaSql;
		ResultSet rs=null;
	
		String cadena ="Select cfw_id, cfw_canal, cfw_terminal, cfw_id_institucion from configuracion_wsdl ";
		sentenciaSql=Conn.prepareStatement(cadena);
		rs=sentenciaSql.executeQuery();
	 
		Map<Long,DatosConfiguracionWsdl> mapaDatosConfiguracion = new HashMap<Long, DatosConfiguracionWsdl>(); 
		
		while(rs.next())
		{
			 DatosConfiguracionWsdl datosConfWsd = new DatosConfiguracionWsdl ();
			 Long id =  rs.getLong("cfw_id");
			 String canal = rs.getString("cfw_canal");
			 String terminal = rs.getString("cfw_terminal");
			 String idInstitucion = rs.getString("cfw_id_institucion");
			 			 			 			 		 		    		    			
			 datosConfWsd.setId(id);
			 datosConfWsd.setCanal(canal);
			 datosConfWsd.setTerminal(terminal);
			 datosConfWsd.setIdInstitucion(idInstitucion);
			 
			 mapaDatosConfiguracion.put(id, datosConfWsd);
		}	
		
		return mapaDatosConfiguracion;
		
		
	}
	
	public List<Productos> getProductosProovedor(Connection Conn) throws Exception 
	{

        PreparedStatement sentenciaSql;
		ResultSet rs=null;
	    				
		String cadena ="Select codigo, id, descripcion from public.\"productosFullCarga\" ";
		sentenciaSql=Conn.prepareStatement(cadena);
		rs=sentenciaSql.executeQuery();
	 
		List<Productos> listaClienteRoll= new ArrayList<Productos>(); 
		
		while(rs.next())
		{
			Productos datosConfWsd = new Productos ();
			 int codigo =  rs.getInt("codigo");
			 String id = rs.getString("id");
			 String descrp = rs.getString("descripcion");
    			
			 datosConfWsd.setCodigo(codigo);
			 datosConfWsd.setId(id);
			 datosConfWsd.setDescripcion(descrp);

			 listaClienteRoll.add(datosConfWsd);
		}	
		
		return listaClienteRoll;				
	}
	
	public List<EmpresaClienteRoll> consultarEmpresaClienteRoll(Connection Conn) throws Exception 
	{

        PreparedStatement sentenciaSql;
		ResultSet rs=null;
	    				
		String cadena ="Select ecr_codigo, ecr_empresa_fk, ecr_cliente_roll_fk, ecr_codigo_configuracion_fk,  ecr_codigo_cliente_fk from empresa_cliente_roll ";
		sentenciaSql=Conn.prepareStatement(cadena);
		rs=sentenciaSql.executeQuery();
	 
		List<EmpresaClienteRoll> listaClienteRoll= new ArrayList<EmpresaClienteRoll>(); 
		
		while(rs.next())
		{
			EmpresaClienteRoll datosConfWsd = new EmpresaClienteRoll ();
			 Long codigo =  rs.getLong("ecr_codigo");
			 Long empresaFk = rs.getLong("ecr_empresa_fk");
			 Long clienteRollFk = rs.getLong("ecr_cliente_roll_fk");
			 Long codigoConfiguracionFk = rs.getLong("ecr_codigo_configuracion_fk");
			 Long codigoClienteFk = rs.getLong("ecr_codigo_cliente_fk");
			 			 			 			 		 		    		    			
			 datosConfWsd.setCodigo(codigo);
			 datosConfWsd.setEmpresaFk(empresaFk);
			 datosConfWsd.setClienteRollFk(clienteRollFk);
			 datosConfWsd.setCodigoConfiguracionFk(codigoConfiguracionFk);
			 datosConfWsd.setCodigoClienteFk(codigoClienteFk);
			 
			 listaClienteRoll.add( datosConfWsd);
		}	
		
		return listaClienteRoll;				
	}

	
	public Long actualizarNumConsulta(Connection Conn , Long numConsulta, Long codEmpresa) throws Exception 
	{
		  PreparedStatement sentenciaSql;
		 Long numeroTransConsulta = numConsulta;
			
			String cadena3 = "UPDATE " +
			" public.configuracion  " +
				"SET " +
				"cnf_num_transaccion_consulta = ? " + //1
				" WHERE cnf_cod_empresa = ?" ; //2						
			
			sentenciaSql = Conn.prepareStatement(cadena3);			
			sentenciaSql.setLong(1,numConsulta);
			sentenciaSql.setLong(2,codEmpresa);
	        sentenciaSql.executeUpdate();	   
			return numeroTransConsulta;
	}
	
	
	public Long obtenerNumConsulta(Connection Conn , Long codEmpresa ) throws Exception 
	{
		  PreparedStatement sentenciaSql;
			ResultSet rs=null;
			Long numeroTransConsulta = 0L;
			
			String cadena =" UPDATE public.configuracion  SET cnf_num_transaccion_consulta =(SELECT cnf_num_transaccion_consulta  From configuracion WHERE cnf_cod_empresa = ?) +1 " +
						   " WHERE cnf_cod_empresa = ? "+
                           " RETURNING  cnf_num_transaccion_consulta "; 
			sentenciaSql=Conn.prepareStatement(cadena);
			sentenciaSql.setLong(1,codEmpresa);
			sentenciaSql.setLong(2,codEmpresa);
			rs=sentenciaSql.executeQuery();
		
			while(rs.next())
			{
				 numeroTransConsulta =  rs.getLong("cnf_num_transaccion_consulta");
			}	    
			return numeroTransConsulta;
	}
	
	public Long consultarUltimoTransacid(Connection Conn) throws Exception 
	{
		Long transacId = null;
		PreparedStatement  sentenciaSql;	    
	    ResultSet rs=null;
	    String cadena = "SELECT " +
	    					"MAX(tdt.cnx_det_transac_id) AS transac_id " +
	    				"FROM " +
	    				"public.conexion_detalle as tdt";
	    
	    sentenciaSql = Conn.prepareStatement(cadena);
	    rs = sentenciaSql.executeQuery();
	    
	    while(rs.next())
	    {	    	
	    	transacId= rs.getLong("transac_id");
	    	if (transacId == null)
	    	{
	    		transacId = 0L;
	    	}
		}
		return transacId;
	}
	
	
	
	public Date consultarUltimaFechaConciliacion(Connection Conn) throws Exception 
	{
		Date fechaUltimaConciliacion = new Date();
		
		PreparedStatement  sentenciaSql;	    
	    ResultSet rs=null;
	    String cadena = "SELECT "+
	                    "MAX(tdt.cnd_fecha_conciliacion) AS fecha_conciliacion " +  
	                    "FROM public.conciliacion_deposito as tdt ";
	    
	    sentenciaSql = Conn.prepareStatement(cadena);
	    rs = sentenciaSql.executeQuery();
	    
	    while(rs.next())
	    {	    	
	    	fechaUltimaConciliacion= rs.getTimestamp("fecha_conciliacion");
	    	
		}
		return fechaUltimaConciliacion;
	}
	

 
 public boolean insertarRegistroDeConcilidacion(Connection Conn,
			String fecha ,String nombreArchivo,Long transaccionConsilidadas,
			Long transaccionNoConcilidada)
throws Exception
{
	boolean ban = false;			
	
	String cadena3 = "INSERT INTO  public.conciliacion ( " +		
	"ccc_fecha, " +  //1
	"ccc_nombre_archivo, " + //2
	"ccc_numero_transacciones_conciliadas, " + //3
	"ccc_numero_transacciones_no_conciliadas) " + //4
	"VALUES( " +		
	"?, " + //1
	"?, " + //2
	"?, " + //3				
	"?)";  //4
	
	PreparedStatement sentenciaSql3 = Conn.prepareStatement(cadena3);
	sentenciaSql3.setString(1, fecha);
	sentenciaSql3.setString(2, nombreArchivo);
	sentenciaSql3.setLong(3, transaccionConsilidadas);
	sentenciaSql3.setLong(4, transaccionNoConcilidada);	
	
	sentenciaSql3.executeUpdate();
	ban = true;
	return ban;
}
	
 
 public boolean borrarRegistroDeConcilidacion(Connection Conn,String nombreArchivo)
throws Exception
{
	boolean ban = false;			
	
	String cadena3 =  "DELETE from transacciones_no_conciliadas "+
	                  "WHERE tcn_nombre_archivo_conciliacion='"+nombreArchivo+"' ";
	
	PreparedStatement sentenciaSql3 = Conn.prepareStatement(cadena3);
	
	sentenciaSql3.executeUpdate();
	ban = true;
	return ban;
}
 
 
 public Map<Long, Empresa> listaEmpresa( Connection Conn) throws Exception
  {
	    Map<Long,Empresa> hm = new HashMap<Long,Empresa> ();

		PreparedStatement  sentenciaSql;	    
		ResultSet rs=null;

		String cadena = 
		"SELECT  id_empresa from empresa ";
	
		sentenciaSql = Conn.prepareStatement(cadena);				
		rs=sentenciaSql.executeQuery();
		
		while(rs.next())
		{
			Empresa emp = new Empresa();			
					
			Long  idEmpresa = rs.getLong("id_empresa");
			emp.setIdEmpresa(idEmpresa);
											
		    hm.put(idEmpresa, emp);			
       }	    

     return hm;

  }
 
 
 public ConfiguracionCorreo listarConfiguracionCorreo( Connection Conn) throws Exception
 {
	 ConfiguracionCorreo cfc = new ConfiguracionCorreo();
		PreparedStatement  sentenciaSql;	    
		ResultSet rs=null;
		String cadena = "SELECT " +
		"cfc_user_correo,  cfc_password_correo, cfc_correos_destinatarios, cfc_correos_copia  " + 
		"FROM " +
		"public.configuracion_correo " ;

		sentenciaSql = Conn.prepareStatement(cadena);
		rs = sentenciaSql.executeQuery();
	
		if(rs.next())
		{
			String userCorreo = rs.getString("cfc_user_correo");
			cfc.setUserCorreo(userCorreo);
			
			String passwordCorreo = rs.getString("cfc_password_correo");
			cfc.setPasswordCorreo(passwordCorreo);
			
			String correosDestinatarios = rs.getString("cfc_correos_destinatarios");
			cfc.setDestinatarios(correosDestinatarios);
			
			String correosCopia = rs.getString("cfc_correos_copia");
			cfc.setDestinatariosCopia(correosCopia);								
			
		}
		return cfc;

 }
 
 public static void obtenerAleatorio(List<Object> lista) {
     
     Random aleatorio = new Random();
     int r = (int) lista.get(aleatorio.nextInt(lista.size()));
     System.out.println(r);
 }
 
 
}
