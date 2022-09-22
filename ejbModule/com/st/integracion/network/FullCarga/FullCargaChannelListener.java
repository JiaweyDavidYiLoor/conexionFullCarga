package com.st.integracion.network.FullCarga;

import java.util.EventListener;

import com.st.integracion.dto.FullCarga.TransaccionFullCarga;

public interface FullCargaChannelListener extends EventListener {
	
	public void canalConectado();
		
	public void recargaRespondida(TransaccionFullCarga respuesta);
		
	public void canalDesconectado();
}
