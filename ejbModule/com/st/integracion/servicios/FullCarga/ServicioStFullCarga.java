package com.st.integracion.servicios.FullCarga;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import com.st.integracion.servicios.RegistroServicios;
import com.st.integracion.servicios.ServicioSt;

@Singleton
@Startup
@DependsOn(value = { "VariablesFullCarga" })
public class ServicioStFullCarga {

	private static final Logger log = Logger.getLogger(ServicioStFullCarga.class);
	private static final String nombreServicioSt = ServicioStFullCarga.class.getName();
	
	@Inject @Preferred  private  ServicioSt servicioStInteragua;
			
	@PostConstruct
	void inicio()
	{		
		log.info("Incializando "+ nombreServicioSt );		
		if(servicioStInteragua==null) 
			log.error(nombreServicioSt + " no sera registrado en conexionBase, no se inicalizo correctamente ");
		else
		{
			RegistroServicios.registroInstance().addServicio(servicioStInteragua,nombreServicioSt +" "+ servicioStInteragua.hashCode());
			log.info(nombreServicioSt+" registrado en conexionBase");
		}	
	}
	
	
	@PreDestroy
	void finalizar()
	{
		if(servicioStInteragua==null) 
			log.error(nombreServicioSt+" no existe");
		else
		{
			RegistroServicios.registroInstance().removeServicio(servicioStInteragua,nombreServicioSt +" "+ servicioStInteragua.hashCode());
			log.info(nombreServicioSt +" removido en conexionBase");
		}	
		
	}
	
	
	

	
}
