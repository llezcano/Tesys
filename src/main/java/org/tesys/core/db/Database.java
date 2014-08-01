package org.tesys.core.db;

import java.net.MalformedURLException;
import java.util.Date;

import javax.ws.rs.core.Response;

import org.tesys.core.analysis.sonar.AnalisisPOJO;
import org.tesys.core.analysis.sonar.MetricPOJO;
import org.tesys.core.project.scm.RevisionPOJO;
import org.tesys.core.project.scm.MappingPOJO;
import org.tesys.util.RESTClient;

/**
 * El objetivo de esta clase es proveer SCRUD para el acceso a datos desde el CORE.
 * 
 * La misma actua como facade de la base de datos, e implementa un cliente para acceder 
 * al Conector de la misma.
 * 
 * 
 * @author rulo
 * 
 */
public class Database {
    // RESEARCH Singleton Pattern Java Thread-Safe
    
    // Estos RESOURCEs serian los del Connector de la DB
    private final static String RESOURCE_USER_MAPPING = "mapping/";
    private final static String RESOURCE_COMMIT = "commit/";
    private final static String RESOURCE_METRIC = "metric/";
    private final static String RESOURCE_ANALYSIS = "analysis/";   
    
    private final static String SLASH = "/";

    private final static String DEFAULT_LOCATION_CONNECTOR = "http://localhost:8080/core/rest/connectors/elasticsearch"; //$NON-NLS-1$

    public RESTClient client;

    public String getURL() {
	return DEFAULT_LOCATION_CONNECTOR;
    }
    
    public Database() {
	try {
	    client = new RESTClient(getURL());
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	}
    }
    
    public static void main(String args[]) {
	Database db = new Database() ;
	RevisionPOJO rev  = new RevisionPOJO(new Date(), "scm2000", "JIRA-1", "rev1", "repo") ;
	db.store("rev1", rev);
	
    }
    
    public boolean isValidDeveloper(String name, String repoID) {
	Response response = client.GET(RESOURCE_USER_MAPPING+name+SLASH+repoID);
	return (response.readEntity(String.class).equals("true"));
    }


    public void store(String ID, MappingPOJO mapping) {
	client.PUT(RESOURCE_USER_MAPPING+ID, mapping);	
    }

    public void store(String ID, RevisionPOJO rev) {
	client.PUT(RESOURCE_COMMIT+ID, rev);
    }

    public void store(String ID, MetricPOJO metric) {
	client.PUT(RESOURCE_METRIC+ID, metric);
    }

    public void store(String ID, AnalisisPOJO analysis) {
	client.PUT(RESOURCE_ANALYSIS+ID, analysis);

    }
    public RevisionPOJO[] getRevisions() {
	// TODO Database.getRevisions()
	return null;
    }
    

    public RevisionPOJO[] getUnscanedRevisions() {
	// TODO Auto-generated method stub
	return null;
    }

}

//TEST Probar escritura y lectura en DB, mirar este ejemplo
/*
public static void main(String args[]) {
	Database db = new Database() ;
	MappingPOJO mapping = new MappingPOJO("jira", "scm0", "repo0") ;
	db.store("TEST-1", mapping); //Esto es asincrono 
	System.out.println(db.isValidDeveloper("scm0", "repo0")) ; // Por lo tanto si instantaneamente leo el dato retorna False
	
	
	mapping = new MappingPOJO("jira", "scm2000", "repo2000") ;
	db.store("TEST-1", mapping);
	try {
	    Thread.sleep(2000);
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	System.out.println(db.isValidDeveloper("scm2000", "repo2000")) ; // Al esperar 2 segundos se llega a escribir el dato antes de consultar
	
}
 */
