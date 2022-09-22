package com.st.integracion.network.FullCarga;

import java.util.Date;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.google.common.base.MoreObjects;
import com.st.integracion.beans.AdminTransaccionLocal;
import com.st.integracion.beans.FullCarga.AdminFullCargaLocal;
import com.st.integracion.dto.Transaccion.TipoOperacion;
import com.st.integracion.dto.FullCarga.TransaccionFullCarga;
import com.st.integracion.servicios.RegistroServicios;
import com.st.integracion.servicios.FullCarga.ServicioFullCarga;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class FullCargaTrbListener extends TimerTask implements FullCargaChannelListener, ChannelFutureListener {

	private static final Logger log = Logger.getLogger(FullCargaTrbListener.class);

	private final ServicioFullCarga servicio;
	private final TransaccionFullCarga tx;

	public FullCargaTrbListener(ServicioFullCarga servicio, TransaccionFullCarga tx) {
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
		if (this.tx.getTipoOperacion() == null)
			return;

		if (future.isSuccess()) {
			log.info("trama enviada al proveedor: " + this.tx);
			this.servicio.transaccionEnviadaProveedor(this);
			this.tx.setTramaTxRequerimiento(this.tx.getTramaTxRequerimiento());
		} else {
			log.info(this.tx + "trama NO fue enviada al proveedor:");
			AdminTransaccionLocal adminTran = RegistroServicios.registroInstance()
					.getAdminTransaccion(this.tx.getCodigoProveedorProducto());
			adminTran.transaccionNoEnviada(this.tx.clone());
		}
	}

	public void recargaRespondida(TransaccionFullCarga respuesta) {
		if (respuesta.getChannelRecibo() == this.tx.getChannelEnvio()) {
			String descripcion = " Listener: recargaRespondida";

			if (this.tx.getTipoOperacion().compareTo(TipoOperacion.RECARGA) == 0) {
				this.tx.setFechaRespuestaRecarga(new Date());
				this.tx.setReferenciaProveedor(respuesta.getReferenciaSigma());
				log.info("TxInteraguaListener-recarga-respondida: " + this.tx + descripcion);
			}
			this.tx.setCodigoRetorno(respuesta.getCodigoRetorno());
			this.tx.setMensajeRetorno(respuesta.getMensajeRetorno());
			this.tx.setTramaTxRespuesta(respuesta.getTramaTxRespuesta());
			this.servicio.respuestaRecibidaProveedor(this);
		}
	}

	@Override
	public void canalConectado() {
	}

	@Override
	public void canalDesconectado() {
	}

	@Override
	public void run() {
		TipoOperacion tipo = this.tx.getTipoOperacion();
		switch (tipo) {
		case COMPRAR_PIN_INTERNET:
			this.servicio.comprarPinInternet(this.tx);
			break;
		case ANULACION_CMP_PIN_INTERNET:
			if (tx.getCantReqRealizados() <= 10) {
				this.servicio.anularCompraPinInternet(this.tx);
			} else {
				log.info("Numero de reintentos agotados de Anulacion trn quedara marcada como Anulacion no Realizada |"
						+ tx.toString());
				AdminFullCargaLocal adminTransacionLocal = (AdminFullCargaLocal) RegistroServicios.registroInstance()
						.getAdminTransaccion(this.tx.getCodigoProveedorProducto());
				this.tx.setCodigoEstado(76); // Anualcion no realizada
				this.tx.setCodigoProceso(54); // respuesta anulacion no realizada
				this.tx.setCodigoRetorno("4567");
				this.tx.setMensajeRetorno("Intentos de Anulacion Agotados");

				adminTransacionLocal.actualizarAAnulacionNoRealizada(this.tx);
			}
			break;
		default:
			return;
		}
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("tx", this.tx).toString();
	}

}
