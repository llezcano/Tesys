package org.tesys.connectors.db.elasticsearch;


import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.tesys.core.analysis.sonar.AnalisisPOJO;
import org.tesys.core.analysis.sonar.MetricPOJO;
import org.tesys.core.project.scm.MappingPOJO;
import org.tesys.core.project.scm.RevisionPOJO;

@Path("/connectors/elasticsearch")
@Singleton
public class ElasticSearchConnector {

    private ElasticSearch Elasticsearch ;
    @PostConstruct
    public void init() {
	Elasticsearch = new ElasticSearch() ;
	
    }


    @GET
    @Path("/mapping/{name}/{repoID}")
    public boolean isValidDeveloper(@PathParam("name") String name, @PathParam("repoID") String repoID) {
	return Elasticsearch.isValidDeveloper(name, repoID);
    }
    
    @GET
    @Path("/revisions")
    public RevisionPOJO[] getRevisions() {	
	return Elasticsearch.getRevisions() ;
    }
    
    @GET
    @Path("/revisions/unscanned")
    public RevisionPOJO[] getUnscanedRevisions() {
	return Elasticsearch.getUnscanedRevisions() ;
    }
    
    
    /**
     * Aqui se almacenan los mapeos entre los usuarios JIRA-SCM
     * 
     * @param ID
     * @param mapping
     */
    @PUT
    @Path("/mapping/{id}")
    public void store(@PathParam("id") String ID, MappingPOJO mapping) {
	Elasticsearch.store(ID, mapping);
    }
    
    /**
     * Aqui se almacenan las descripciones de cada metrica individual (Nombre,tipo,etc.)
     * 
     * @param ID
     * @param metric
     */
    @PUT
    @Path("/metric/{id}")
    public void store(@PathParam("id") String ID, MetricPOJO metric) {
	Elasticsearch.store(ID, metric);
    }
    
    /**
     * Aqui se almacenan los analisis del sonar. Los cuales con un conjunto de metricas.
     * 
     * @param ID
     * @param analisis
     */
    @PUT
    @Path("/analysis/{id}")
    public void store(@PathParam("id") String ID, AnalisisPOJO analisis) {
	Elasticsearch.store(ID, analisis);
    }


    // TODO /revisions/{repo} esto es para que funcione con distintas revisiones
    

}
