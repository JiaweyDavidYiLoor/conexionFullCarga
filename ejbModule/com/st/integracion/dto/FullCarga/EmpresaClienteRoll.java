package com.st.integracion.dto.FullCarga;

import java.util.List;

import com.st.integracion.servicios.FullCarga.VariablesFullCargaLocal;

public class EmpresaClienteRoll {
	
	private long  codigo;
	private long  empresaFk;
	private long  clienteRollFk;
    private	long  codigoConfiguracionFk;
	private long  codigoClienteFk;
	private static List<EmpresaClienteRoll> listaEmpresaClienteRoll ;
	
	public  EmpresaClienteRoll( ) {};
	
	public EmpresaClienteRoll(VariablesFullCargaLocal varFullCargaLocal)
	{
		this.listaEmpresaClienteRoll = varFullCargaLocal.getListaEmpresaClienteRoll();
	}
	
	public long getCodigo() {
		return codigo;
	}
	public void setCodigo(long codigo) {
		this.codigo = codigo;
	}
	public long getEmpresaFk() {
		return empresaFk;
	}
	public void setEmpresaFk(long empresaFk) {
		this.empresaFk = empresaFk;
	}
	public long getClienteRollFk() {
		return clienteRollFk;
	}
	public void setClienteRollFk(long clienteRollFk) {
		this.clienteRollFk = clienteRollFk;
	}
	public long getCodigoConfiguracionFk() {
		return codigoConfiguracionFk;
	}
	public void setCodigoConfiguracionFk(long codigoConfiguracionFk) {
		this.codigoConfiguracionFk = codigoConfiguracionFk;
	}
	public long getCodigoClienteFk() {
		return codigoClienteFk;
	}
	public void setCodigoClienteFk(long codigoClienteFk) {
		this.codigoClienteFk = codigoClienteFk;
	}
	
	@Override
	public String toString() {
		return "EmpresaClienteRoll [codigo=" + codigo + ", empresaFk=" + empresaFk + ", clienteRollFk=" + clienteRollFk
				+ ", codigoConfiguracionFk=" + codigoConfiguracionFk + ", codigoClienteFk=" + codigoClienteFk + "]";
	}
	
	public static EmpresaClienteRoll obtenerRegistroEmpresaCliente( long codigoEmpresa, long codigoCliente, long codigoClienteRoll)
	{
		EmpresaClienteRoll retorno= null ;
		if(listaEmpresaClienteRoll != null && !listaEmpresaClienteRoll.isEmpty())
		{
			for(EmpresaClienteRoll empresaClieRoll : listaEmpresaClienteRoll)
			{
				if(empresaClieRoll.getEmpresaFk() == codigoEmpresa && empresaClieRoll.getCodigoClienteFk() ==codigoCliente &&
						empresaClieRoll.getClienteRollFk() == codigoClienteRoll)
				{
					retorno = empresaClieRoll;
				}
			}
		}
		return retorno;
		
	}
	
		
	  
}
