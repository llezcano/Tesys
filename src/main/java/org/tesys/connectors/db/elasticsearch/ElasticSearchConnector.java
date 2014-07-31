package org.tesys.connectors.db.elasticsearch;


import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.tesys.core.project.scm.MappingPOJO;
import org.tesys.core.project.scm.RevisionPOJO;
import org.tesys.core.project.scm.ScmPostCommitDataPOJO;

@Path("/connectors/elasticsearch")
@Singleton
public class ElasticSearchConnector {

    @PostConstruct
    public void init() {
	System.out.println("JiraConnector") ;
    }


    @GET
    @Path("/mapping/{name}/{repoID}")
    public boolean isValidDeveloper(@PathParam("name") String name, @PathParam("repoID") String repoID) {
	//isValidDeveloper(@PathParam("name") String name, @PathParam("repoID") String repoID)
	return true;
    }
    
    @GET
    @Path("/revisions")
    public RevisionPOJO[] getRevisions() {
	// TODO getRevisions()
	return null;
    }
    
    @PUT
    @Path("/revisions/{id}")
    public void store(@PathParam("id") String ID, ScmPostCommitDataPOJO data) {
	
	//TODO store(String ID, ScmPostCommitDataPOJO data)
    }
    
    @PUT
    @Path("/mapping/{id}")
    public void store(@PathParam("id") String ID, MappingPOJO mapping) {
	//TODO store(String ID, SCMProjectMappingPOJO mapping)
	System.out.println(mapping);
    }
    
    // TODO /revisions/{repo} esto es para que funcione con distintas revisiones
    

}
