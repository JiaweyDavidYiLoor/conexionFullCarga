package com.st.integracion.network.FullCarga;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.st.integracion.dto.FullCarga.Producto;
import com.st.integracion.dto.FullCarga.Productos;
import com.st.integracion.dto.FullCarga.TransaccionFullCarga;
import com.st.integracion.dto.Transaccion.TipoOperacion;
import com.st.integracion.servicios.FullCarga.VariablesFullCargaLocal;
import com.st.integracion.util.FullCarga.ElementosResponse;
import com.st.integracion.util.FullCarga.TransaccionDatos;
import com.st.integracion.util.FullCarga.TransaccionRespuesta;
import com.st.integracion.util.FullCarga.Utilitaria;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

public class ClaroChannelHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

	public static final String HANDLER_NAME = "TrbChannelHandler";
	private static final Logger log = Logger.getLogger(ClaroChannelHandler.class);
	private final DocumentBuilder builder;
	private Utilitaria utl = new Utilitaria();
	private static final Set<ClaroChannelListener> listeners = new CopyOnWriteArraySet<ClaroChannelListener>();

	public ClaroChannelHandler() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setCoalescing(true);
		this.builder = factory.newDocumentBuilder();
		// this.builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
		String respuestaProveedor = msg.content().toString(CharsetUtil.UTF_8);
		log.info("mensaje recibido del proveedor : " + respuestaProveedor);
		InputSource is = new InputSource(new StringReader(respuestaProveedor));
		String xmlFixed = getOnlyElement(fixXML(respuestaProveedor));
		Document root = this.builder.parse(is);
		TransaccionFullCarga tran = this.buildTransaccion(xmlFixed);
		log.info(tran.getCodigoProveedorProducto());
		if (tran == null) {
			ctx.channel().close();
			return;
		}
		tran.setTramaTxRespuesta(fixXML(respuestaProveedor));
		tran.setChannelRecibo(ctx.channel().id().hashCode());
		ctx.channel().close();
		this.fireRespuesta(tran);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	private String getOnlyElement(String xml) {
		String x = null;
		if (xml.contains("ventaResponse")) {
			x = xml.replace(
					"<?xml version='1.0' encoding='UTF-8'?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:ventaResponse xmlns:ns2=\"net.sigma.h2h.ws\">",
					"");
			x = x.replace("</ns2:ventaResponse></S:Body></S:Envelope>", "");
		}
		return x;
	}

	private String fixXML(String xml) {
		String x = null;
		x = xml.replace("&lt;", "<");
		x = x.replace("&gt;", ">");
		return x;
	}

	private TransaccionFullCarga buildTransaccion(String root) {
		TransaccionFullCarga tx = new TransaccionFullCarga();
		String respuestaProveedorXML = "";
		String f2 = root;
		try {
			respuestaProveedorXML = Utilitaria.obtenerHijoXml(f2, "response");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String codigoRetorno = null;
		String mensajeRetorno = null;
		ElementosResponse respuestaProveedor = null;

		try {
			respuestaProveedor = (ElementosResponse) Utilitaria.convertirXmlToObj(respuestaProveedorXML,
					ElementosResponse.class);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String tipoRespuesta = respuestaProveedor.getResponseCode();

		if (tipoRespuesta.compareTo("00") == 0) {
			tx.setTipoOperacion(TipoOperacion.RECARGA);
			tx.setMensajeRetorno(respuestaProveedor.getParametros().get(1).getValor());
			tx.setDescripcion(tipoRespuesta);
			tx.setCodigoRetorno(tipoRespuesta);
			tx.setFechaRespuestaRecarga(new Date());
			tx.setReferenciaProveedor(String.valueOf(respuestaProveedor.getReferenciaSigma()));
		} else if (tipoRespuesta.compareTo("Reverso Realizado Correctamente.") == 0) {
			tx.setTipoOperacion(TipoOperacion.ANULACION_CMP_PIN_INTERNET);
			tx.setMensajeRetorno("");
			tx.setDescripcion(tipoRespuesta);
			tx.setCodigoRetorno(tipoRespuesta);
		} else {
			tx.setCodigoRetorno("TRMALFOR");
			tx.setMensajeRetorno("Formato Incorrecto en Trama de Respuesta");
		}

		return tx;
	}

	public static void addChannelListener(ClaroChannelListener listener) {
		listeners.add(listener);
		if (listeners.size() > 0)
			log.info(String.format("TrbChannelHandler-addChannelListener: actualmente hay %d listeners en el handler",
					listeners.size()));
	}

	public static void removeChannelListener(ClaroChannelListener listener) {
		listeners.remove(listener);
		if (listeners.size() > 0)
			log.info(
					String.format("TrbChannelHandler-removeChannelListener: actualmente hay %d listeners en el handler",
							listeners.size()));
	}

	private void fireRespuesta(TransaccionFullCarga respuesta) {
		TipoOperacion tipoRespuesta = respuesta.getTipoOperacion();
		for (ClaroChannelListener l : listeners) {
			l.recargaRespondida(respuesta);
		}
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("hash", this.hashCode()).add("listeners", listeners.size())
				.toString();
	}

}
