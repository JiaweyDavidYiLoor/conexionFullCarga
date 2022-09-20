package com.st.integracion.network.FullCarga;

import java.util.Date;
import java.util.TimerTask;

import javax.mail.MessagingException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.google.common.base.MoreObjects;

import com.st.integracion.beans.AdminTransaccionLocal;
import com.st.integracion.beans.FullCarga.AdminFullCargaLocal;
import com.st.integracion.dto.FullCarga.TransaccionFullCarga;
import com.st.integracion.dto.Transaccion.TipoOperacion;
import com.st.integracion.servicios.RegistroServicios;
import com.st.integracion.servicios.FullCarga.ServicioFullCarga;
import com.st.integracion.util.FullCarga.Utilitaria;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class ClaroTrbListener extends TimerTask implements ClaroChannelListener, ChannelFutureListener {
	
	private static final Logger log=Logger.getLogger(ClaroTrbListener.class);
	
	private final ServicioFullCarga servicio;
	private final TransaccionFullCarga tx;
	
	public ClaroTrbListener(ServicioFullCarga servicio, TransaccionFullCarga tx) {
		super();
		this.servicio = servicio;
		this.tx = tx;
	}
	
	public ServicioFullCarga getServicio() {
		return servicio;
	}
	public TransaccionFullCarga getTx() { 
		return tx;
	}
	
	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		if(this.tx.getTipoOperacion()==null)
			return;
		
		if(future.isSuccess()){
			log.info("trama enviada al proveedor: "+this.tx);
			this.servicio.transaccionEnviadaProveedor(this);
			this.tx.setTramaTxRequerimiento(this.tx.getTramaTxRequerimiento());
		} else {
			log.info(this.tx+"trama NO fue enviada al proveedor:");
			AdminTransaccionLocal adminTran=RegistroServicios.registroInstance().getAdminTransaccion(this.tx.getCodigoProveedorProducto());
			adminTran.transaccionNoEnviada(this.tx.clone());
		}
	}
			
	public void recargaRespondida(TransaccionFullCarga respuesta)
	{
		if(respuesta.getChannelRecibo()==this.tx.getChannelEnvio())
		{
			String descripcion = " Listener: recargaRespondida";
			if(this.tx.getTipoOperacion().compareTo(TipoOperacion.COMPRAR_PIN_INTERNET)==0)
			{
				this.tx.setFechaRespuestaCompraPinInternet(new Date());
				this.tx.setTramaTxRespuesta(fixXML(respuesta.getTramaTxRespuesta()));
				log.info("TxInteraguaListener-recargaRespondida: " + this.tx+descripcion);
			}
			else if(this.tx.getTipoOperacion().compareTo(TipoOperacion.ANULACION_CMP_PIN_INTERNET)==0)
			{
				this.tx.setFechaRespuestaAnulacionCompraPinInternet(new Date());
				log.info("TxInteraguaListener-anulacionRespondida: " + this.tx+descripcion);
			}
			//cambio 19/09/2022
			else if(this.tx.getTipoOperacion().compareTo(TipoOperacion.RECARGA)==0)
			{
				this.tx.setFechaRespuestaRecarga(new Date());
				//this.tx.setReferenciaProveedor(respuesta.getReferenciaSigma());
				log.info("TxInteraguaListener-recarga-respondida: " + this.tx+descripcion);
			}	
				
				this.tx.setCodigoRetorno(respuesta.getCodigoRetorno());
				this.tx.setMensajeRetorno(respuesta.getMensajeRetorno());								
				this.tx.setTramaTxRespuesta(respuesta.getTramaTxRespuesta());							
				this.servicio.respuestaRecibidaProveedor(this);		
		}
	}
	
	private String fixXML(String xml) {
		String x=null;
		x=xml.replace("&lt;", "<");
		x=x.replace("&gt;", ">");
		return x;
	}
	
	
	@Override
	public void canalConectado() {}
	@Override
	public void canalDesconectado() {}
	
	@Override
	public void run()
	{
		TipoOperacion tipo = this.tx.getTipoOperacion();
		switch (tipo) 
		{
		case COMPRAR_PIN_INTERNET:
				this.servicio.comprarPinInternet(this.tx);	
			break;
		case ANULACION_CMP_PIN_INTERNET:
			if(tx.getCantReqRealizados()<=10)
			{	
				this.servicio.anularCompraPinInternet(this.tx);
			}
			else
			{	
				log.info("Numero de reintentos agotados de Anulacion trn quedara marcada como Anulacion no Realizada |"+tx.toString());
				AdminFullCargaLocal adminTransacionLocal = (AdminFullCargaLocal)RegistroServicios.registroInstance().getAdminTransaccion(this.tx.getCodigoProveedorProducto());
				this.tx.setCodigoEstado(76); //Anualcion no realizada
				this.tx.setCodigoProceso(54); // respuesta anulacion  no realizada
				this.tx.setCodigoRetorno("4567");
				this.tx.setMensajeRetorno("Intentos de Anulacion Agotados");
				
				adminTransacionLocal.actualizarAAnulacionNoRealizada(this.tx);
			}
			break;
		default:
			return;
		}
	}
	
	private void tareaReverso()
	{
		this.servicio.anularCompraPinInternet(this.tx);
	}
	
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("tx", this.tx).toString();
	}

}
