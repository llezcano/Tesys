package org.tesys.core.db;

import java.net.MalformedURLException;

import javax.ws.rs.core.Response;

import org.tesys.core.analysis.telemetry.RevisionPOJO;
import org.tesys.core.project.scm.ScmPostCommitDataPOJO;
import org.tesys.util.RESTClient;


/**
 * SCRUD for data persistence. 
 * @author rulo
 *
 */
public class Database { 

    // Estos RESOURCEs serian los del Connector de la DB
    private static String RESOURCE_USER_MAPPING = "/mapping/" ;
    private static String RESOURCE_COMMIT = "/commit/" ;
    private static String RESOURCE_CONCAT_CHR = "/" ;
    
    
    
    private static final String DEFAULT_LOCATION_CONNECTOR =
        "http://localhost:8080/core/rest/connectors/elasticsearch"; //$NON-NLS-1$
 

    public RESTClient client ;
   
    
    public String getURL() {
	return DEFAULT_LOCATION_CONNECTOR ; 
    } 
    
    public Database() {
	try {
	    client = new RESTClient(getURL()) ;
	} catch (MalformedURLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
    
    
    public boolean isValidDeveloper( String name, String repoID) {
	Response response = client.GET( RESOURCE_USER_MAPPING + name + RESOURCE_CONCAT_CHR  + repoID) ;
	return ( response.readEntity(String.class).equals("true") )  ;
    }
    
    
    public RevisionPOJO[] getRevisions() {
	//TODO Database.getRevisions()
	return null ;
    }
    
   
    public void store( String ID, ScmPostCommitDataPOJO data) {
	client.PUT(RESOURCE_COMMIT + ID, data) ;        
    }
    
    public void store( String ID, SCMProjectMappingPOJO mapping) {
	client.PUT(RESOURCE_USER_MAPPING + ID, mapping) ;
    }
    
      
    
}
