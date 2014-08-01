package org.tesys.connectors.db.elasticsearch;


import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes; 
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.tesys.core.analysis.sonar.AnalisisPOJO;
import org.tesys.core.analysis.sonar.MetricPOJO;
import org.tesys.core.project.scm.MappingPOJO;
import org.tesys.core.project.scm.RevisionPOJO;

/**
 * El objetivo de esta clase es proveer una Capa de interoperabilidad con la base de datos usada.
 * Ya sea para independizar ubicacion fisica y el lenguaje con el cual se accederan a los datos.
 * 
 * Provee un Web Service REST el cual ofrece un conjunto de metodos para acceder y persistir informacion 
 * relacionada con el CORE, el cual no debe tener acceso directo a la base de datos.
 * 
 * Esta clase interactua directamente con un Facade cliente de la base de datos.
 * 
 * @author rulo
 *
 */
@Path("/connectors/elasticsearch")
@Singleton
public class ElasticSearchConnector {
    
    private ElasticSearch elasticSearch ;
    
    @PostConstruct
    public void init() {
	elasticSearch = new ElasticSearch() ;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/mapping/{name}")
    public boolean isValidDeveloper(@PathParam("name") String name, @QueryParam("repo") String repoID) {
      return elasticSearch.isValidDeveloper(name, repoID);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/revisions")
    public RevisionPOJO[] getRevisions() {	
	return elasticSearch.getRevisions() ;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/revisions/unscanned")
    public RevisionPOJO[] getUnscanedRevisions() {
	return elasticSearch.getUnscanedRevisions() ;
    }
    
    
    /**
     * Aqui se almacenan los mapeos entre los usuarios JIRA-SCM
     * 
     * @param ID
     * @param mapping
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/mapping/{id}")
    public void store(@PathParam("id") String ID, MappingPOJO mapping) {
	elasticSearch.store(ID, mapping);
    }
    
    /**
     * Aqui se almacenan las descripciones de cada metrica individual (Nombre,tipo,etc.)
     * 
     * @param ID
     * @param metric
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/metric/{id}")
    public void store(@PathParam("id") String ID, MetricPOJO metric) {
	elasticSearch.store(ID, metric);
    }
    
    /**
     * Aqui se almacenan los analisis del sonar. Los cuales con un conjunto de metricas.
     * 
     * @param ID
     * @param analisis
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/analysis/{id}")
    public void store(@PathParam("id") String ID, AnalisisPOJO analisis) {
	elasticSearch.store(ID, analisis);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/commit/{id}")
    public void store(@PathParam("id") String ID, RevisionPOJO revision) {
      elasticSearch.store(ID, revision);
    }


    // TODO /revisions/{repo} esto es para que funcione con distintas revisiones
    

}
