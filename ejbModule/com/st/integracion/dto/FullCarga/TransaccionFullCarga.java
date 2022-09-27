package com.st.integracion.dto.FullCarga;

import java.math.BigDecimal;
import java.util.Date;

import com.st.integracion.dto.Transaccion;
import com.st.integracion.util.FullCarga.TransaccionDatos;
import com.st.integracion.ws.FullCarga.ParametrosRespuesta;

public class TransaccionFullCarga extends Transaccion implements Comparable<TransaccionFullCarga>, Cloneable {

	public static final long serialVersionUID = -1209405255901660675L;

	public enum TipoProducto {
		MOVIL, FIJO, TVSATELITAL, TELEFONIA_FIJA, DTH, SERVICIO_INACTIVO
	}

	private TipoProducto tipoProducto;

	private long transacId;
	private long intentos;
	private int channelEnvio;
	private int channelRecibo;
	private String job;

	private String numeroTransaccion;
	private String tipoTransaccion;
	private String fechaTransaccion;
	private String horaTransaccion;
	private String userWs;
	private String passWs;
	private String instanceWs;

	private String codigoRetorno;
	private String mensajeRetorno;

	private String numeroTransaccionReverso;
	private Date fechaReinicioReverso;
	private Date fechaTopeReversos;
	private String parametrosXmlInTransaccion;
	private String parametrosInfoEmpresa;
	private Long idEmpresa;
	private String nombre;

	private String tramaTxRequerimiento;
	private String tramaTxRespuesta;

	// Elementos para transaccion�

	private TransaccionDatos datosPagoAsp;
	private ParametrosRespuesta parametrosRespuesta;

	private String idInstitucion;
	private String idCanal;
	private Date fechaContable;
	private Date fechaTrx;
	private String idPagoBanco;
	private String idTerminal;
	private String trxAtm;
	private String valorRecibido;

	private String tipoProductoStr;

	private BigDecimal importe;
	private String referenciaSigma;
	private String referenciaOperadora;

	@Override
	public int compareTo(TransaccionFullCarga that) {
		return (int) (this.getTransacId() - that.getTransacId());
	}

	@Override
	public TransaccionFullCarga clone() {

		TransaccionFullCarga tx = new TransaccionFullCarga();

		tx.setTipoProductoStr(this.tipoProductoStr);

		tx.setParametrosRespuesta(this.getParametrosRespuesta());

		tx.setDatosPagoAsp(this.getDatosPagoAsp());

		tx.setTipoOperacion(this.getTipoOperacion());
		tx.setCantReqRealizados(this.getCantReqRealizados());
		tx.setTransacId(this.getTransacId());
		tx.setIntentos(this.getIntentos());
		tx.setChannelEnvio(this.getChannelEnvio());
		tx.setChannelRecibo(this.getChannelRecibo());
		tx.setJob(this.getJob());

		tx.setCodigoMovimiento(this.getCodigoMovimiento());
		tx.setNumTransaccion(this.getNumTransaccion());
		tx.setFecha(this.getFecha());
		tx.setDescripcion(this.getDescripcion());
		tx.setCodigoCliente(this.getCodigoCliente());
		tx.setCodigoUsuario(this.getCodigoUsuario());
		tx.setValorContable(this.getValorContable());
		tx.setReferenciaCliente(this.getReferenciaCliente());
		tx.setMvmParametroXmlIn(this.getMvmParametroXmlIn());
		tx.setIdentificadorHost(this.getIdentificadorHost());
		tx.setReferenciaProveedor(this.getReferenciaProveedor());
		tx.setMvmParametroXmlOut(this.getMvmParametroXmlOut());
		tx.setCodigoBodega(this.getCodigoBodega());
		tx.setCodigoProveedorProducto(this.getCodigoProveedorProducto());
		tx.setCodigoEstadoConexion(this.getCodigoEstadoConexion());
		tx.setEstadoFK(this.getEstadoFK());
		tx.setParametrosXmlInTransaccion(this.getParametrosXmlInTransaccion());
		tx.setParametrosInfoEmpresa(this.getParametrosInfoEmpresa());

		tx.setFechaColaPeticion(this.getFechaColaPeticion());
		tx.setFechaColaPeticionCompraPinInternet(this.getFechaColaPeticionCompraPinInternet());
		tx.setFechaColaPeticionAnulacion(this.getFechaColaPeticionAnulacion());
		tx.setFechaEnvioRecarga(this.getFechaEnvioRecarga());
		tx.setFechaEnvioCompraPinInternet(this.getFechaEnvioCompraPinInternet());
		tx.setFechaEnvioAnulacion(this.getFechaEnvioAnulacion());
		tx.setFechaRespuestaRecarga(this.getFechaRespuestaRecarga());
		tx.setFechaRespuestaCompraPinInternet(this.getFechaRespuestaCompraPinInternet());
		tx.setFechaRespuestaAnulacion(this.getFechaRespuestaAnulacion());
		tx.setFechaEnvioConsultaRecarga(this.getFechaEnvioConsultaRecarga());
		tx.setFechaEnvioConsultaCompraPinInternet(this.getFechaEnvioConsultaCompraPinInternet());
		tx.setFechaEnvioConsultaAnulacion(this.getFechaEnvioConsultaAnulacion());
		tx.setFechaRespuestaConsultaRecarga(this.getFechaRespuestaConsultaRecarga());
		tx.setFechaRespuestaConsultaCompraPinInternet(this.getFechaRespuestaConsultaCompraPinInternet());
		tx.setFechaRespuestaConsultaAnulacion(this.getFechaRespuestaConsultaAnulacion());
		tx.setFechaRespuestaCompraPinInternet(this.getFechaRespuestaCompraPinInternet());
		tx.setFechaRespuestaAnulacionCompraPinInternet(this.getFechaRespuestaAnulacionCompraPinInternet());

		tx.setFechaEnvioAnulacionCompraPinInternet(this.getFechaEnvioAnulacionCompraPinInternet());
		tx.setFechaRespuestaAnulacionCompraPinInternet(this.getFechaRespuestaAnulacionCompraPinInternet());

		tx.setNumeroTransaccion(this.getNumeroTransaccion());
		tx.setTipoTransaccion(this.getTipoTransaccion());
		tx.setFechaTransaccion(this.getFechaTransaccion());
		tx.setHoraTransaccion(this.getHoraTransaccion());
		tx.setUserWs(this.getUserWs());
		tx.setPassWs(this.getPassWs());

		tx.setCodigoRetorno(this.getCodigoRetorno());
		tx.setMensajeRetorno(this.getMensajeRetorno());

		tx.setNumeroTransaccionReverso(this.getNumeroTransaccionReverso());
		tx.setFechaReinicioReverso(this.getFechaReinicioReverso());
		tx.setFechaTopeReversos(this.getFechaTopeReversos());
		tx.setNombre(this.getNombre());

		tx.setTramaTxRequerimiento(this.getTramaTxRequerimiento());
		tx.setTramaTxRespuesta(this.getTramaTxRespuesta());

		tx.setNumeroTransaccion(this.getNumeroTransaccion());
		tx.setTipoTransaccion(this.getTipoTransaccion());
		tx.setFechaTransaccion(this.getFechaTransaccion());
		tx.setHoraTransaccion(this.getHoraTransaccion());
		tx.setUserWs(this.getUserWs());
		tx.setPassWs(this.getPassWs());

		tx.setCodigoRetorno(this.getCodigoRetorno());
		tx.setMensajeRetorno(this.getMensajeRetorno());

		tx.setNumeroTransaccionReverso(this.getNumeroTransaccionReverso());
		tx.setFechaReinicioReverso(this.getFechaReinicioReverso());
		tx.setFechaTopeReversos(this.getFechaTopeReversos());
		tx.setNombre(this.getNombre());

		tx.setTramaTxRequerimiento(this.getTramaTxRequerimiento());
		tx.setTramaTxRespuesta(this.getTramaTxRespuesta());

		tx.setIdInstitucion(this.getIdInstitucion());
		tx.setIdCanal(this.getIdCanal());
		;
		tx.setFechaContable(this.getFechaContable());
		tx.setFechaTrx(this.getFechaTrx());
		tx.setIdPagoBanco(this.getIdPagoBanco());
		tx.setIdTerminal(this.getIdTerminal());
		tx.setTrxAtm(this.getTrxAtm());
		tx.setValorRecibido(this.getValorRecibido());
		tx.setTipoProducto(this.getTipoProducto());
		tx.setReferenciaSigma(this.getReferenciaSigma());
		tx.setImporte(this.getImporte());
		return tx;
	}
	
	public String getReferenciaOperadora() {
		return referenciaOperadora;
	}

	public void setReferenciaOperadora(String referenciaOperadora) {
		this.referenciaOperadora = referenciaOperadora;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	public String getTipoProductoStr() {
		return tipoProductoStr;
	}

	public void setTipoProductoStr(String tipoProductoStr) {
		this.tipoProductoStr = tipoProductoStr;
	}

	public ParametrosRespuesta getParametrosRespuesta() {
		return parametrosRespuesta;
	}

	public void setParametrosRespuesta(ParametrosRespuesta parametrosRespuesta) {
		this.parametrosRespuesta = parametrosRespuesta;
	}

	public TransaccionDatos getDatosPagoAsp() {
		return datosPagoAsp;
	}

	public void setDatosPagoAsp(TransaccionDatos datosPagoAsp) {
		this.datosPagoAsp = datosPagoAsp;
	}

	public long getTransacId() {
		return transacId;
	}

	public void setTransacId(long transacId) {
		this.transacId = transacId;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public long getIntentos() {
		return intentos;
	}

	public void setIntentos(long intentos) {
		this.intentos = intentos;
	}

	public int getChannelEnvio() {
		return channelEnvio;
	}

	public void setChannelEnvio(int channelEnvio) {
		this.channelEnvio = channelEnvio;
	}

	public int getChannelRecibo() {
		return channelRecibo;
	}

	public void setChannelRecibo(int channelRecibo) {
		this.channelRecibo = channelRecibo;
	}

	public String getNumeroTransaccion() {
		return numeroTransaccion;
	}

	public void setNumeroTransaccion(String numeroTransaccion) {
		this.numeroTransaccion = numeroTransaccion;
	}

	public String getTipoTransaccion() {
		return tipoTransaccion;
	}

	public void setTipoTransaccion(String tipoTransaccion) {
		this.tipoTransaccion = tipoTransaccion;
	}

	public String getFechaTransaccion() {
		return fechaTransaccion;
	}

	public void setFechaTransaccion(String fechaTransaccion) {
		this.fechaTransaccion = fechaTransaccion;
	}

	public String getHoraTransaccion() {
		return horaTransaccion;
	}

	public void setHoraTransaccion(String horaTransaccion) {
		this.horaTransaccion = horaTransaccion;
	}

	public String getCodigoRetorno() {
		return codigoRetorno;
	}

	public void setCodigoRetorno(String codigoRetorno) {
		this.codigoRetorno = codigoRetorno;
	}

	public String getMensajeRetorno() {
		return mensajeRetorno;
	}

	public void setMensajeRetorno(String mensajeRetorno) {
		this.mensajeRetorno = mensajeRetorno;
	}

	public String getNumeroTransaccionReverso() {
		return numeroTransaccionReverso;
	}

	public void setNumeroTransaccionReverso(String NumeroTransaccionReverso) {
		this.numeroTransaccionReverso = NumeroTransaccionReverso;
	}

	public String getUserWs() {
		return userWs;
	}

	public void setUserWs(String userWs) {
		this.userWs = userWs;
	}

	public String getPassWs() {
		return passWs;
	}

	public void setPassWs(String passWs) {
		this.passWs = passWs;
	}

	public Date getFechaReinicioReverso() {
		return fechaReinicioReverso;
	}

	public void setFechaReinicioReverso(Date fechaReinicioReverso) {
		this.fechaReinicioReverso = fechaReinicioReverso;
	}

	public Date getFechaTopeReversos() {
		return fechaTopeReversos;
	}

	public void setFechaTopeReversos(Date fechaTopeReversos) {
		this.fechaTopeReversos = fechaTopeReversos;
	}

	public String getParametrosXmlInTransaccion() {
		return parametrosXmlInTransaccion;
	}

	public void setParametrosXmlInTransaccion(String parametrosXmlInTransaccion) {
		this.parametrosXmlInTransaccion = parametrosXmlInTransaccion;
	}

	public String getParametrosInfoEmpresa() {
		return parametrosInfoEmpresa;
	}

	public void setParametrosInfoEmpresa(String parametrosInfoEmpresa) {
		this.parametrosInfoEmpresa = parametrosInfoEmpresa;
	}

	public Long getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Long idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTramaTxRequerimiento() {
		return tramaTxRequerimiento;
	}

	public void setTramaTxRequerimiento(String tramaTxRequerimiento) {
		this.tramaTxRequerimiento = tramaTxRequerimiento;
	}

	public String getTramaTxRespuesta() {
		return tramaTxRespuesta;
	}

	public void setTramaTxRespuesta(String tramaTxRespuesta) {
		this.tramaTxRespuesta = tramaTxRespuesta;
	}

	public String getInstanceWs() {
		return instanceWs;
	}

	public void setInstanceWs(String instanceWs) {
		this.instanceWs = instanceWs;
	}

	public String getIdInstitucion() {
		return idInstitucion;
	}

	public void setIdInstitucion(String idInstitucion) {
		this.idInstitucion = idInstitucion;
	}

	public String getIdCanal() {
		return idCanal;
	}

	public void setIdCanal(String idCanal) {
		this.idCanal = idCanal;
	}

	public Date getFechaContable() {
		return fechaContable;
	}

	public void setFechaContable(Date fechaContable) {
		this.fechaContable = fechaContable;
	}

	public Date getFechaTrx() {
		return fechaTrx;
	}

	public void setFechaTrx(Date fechaTrx) {
		this.fechaTrx = fechaTrx;
	}

	public String getIdPagoBanco() {
		return idPagoBanco;
	}

	public void setIdPagoBanco(String idPagoBanco) {
		this.idPagoBanco = idPagoBanco;
	}

	public String getIdTerminal() {
		return idTerminal;
	}

	public void setIdTerminal(String idTerminal) {
		this.idTerminal = idTerminal;
	}

	public String getTrxAtm() {
		return trxAtm;
	}

	public void setTrxAtm(String trxAtm) {
		this.trxAtm = trxAtm;
	}

	public String getValorRecibido() {
		return valorRecibido;
	}

	public void setValorRecibido(String valorRecibido) {
		this.valorRecibido = valorRecibido;
	}

	public TipoProducto getTipoProducto() {
		return tipoProducto;
	}

	public void setTipoProducto(TipoProducto tipoProducto) {
		this.tipoProducto = tipoProducto;
	}

	public String getReferenciaSigma() {
		return referenciaSigma;
	}

	public void setReferenciaSigma(String referenciaSigma) {
		this.referenciaSigma = referenciaSigma;
	}

}
