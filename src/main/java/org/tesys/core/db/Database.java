package org.tesys.core.db;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.tesys.core.analysis.sonar.AnalisisPOJO;
import org.tesys.core.analysis.sonar.MetricPOJO;
import org.tesys.core.analysis.telemetry.IssueMetrics;
import org.tesys.core.project.scm.RevisionPOJO;
import org.tesys.core.project.scm.MappingPOJO;
import org.tesys.util.RESTClient;

/**
 * El objetivo de esta clase es proveer SCRUD para el acceso a datos desde el
 * CORE.
 * 
 * La misma actua como facade de la base de datos, e implementa un cliente para
 * acceder al Conector de la misma.
 * 
 * 
 * @author rulo
 * 
 */
public class Database {
<<<<<<< HEAD
    // RESEARCH Singleton Pattern Java Thread-Safe

    // Estos RESOURCEs serian los del Connector de la DB
    private static final String RESOURCE_USER_MAPPING = "mapping/";
    private static final String RESOURCE_COMMIT = "commit/";
    private static final String RESOURCE_METRIC = "metric/";
    private static final String RESOURCE_ANALYSIS = "analysis/";
    private static final String RESOURCE_UNSCANNED_REVISIONS = "revision/";
    private static final String RESOURCE_ISSUE_METRIC = "issuemetric/";

    private final static String REPO_PARAM_ID = "repo";

    private final static String DEFAULT_LOCATION_CONNECTOR = "http://localhost:8080/core/rest/connectors/elasticsearch/"; //$NON-NLS-1$

    private RESTClient client;

    public String getURL() {
        return DEFAULT_LOCATION_CONNECTOR;
    }

    public Database() {
        try {
            client = new RESTClient( DEFAULT_LOCATION_CONNECTOR );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public boolean isValidDeveloper( String name, String repoId ) {
        Map<String, String> param = new HashMap<String, String>();
        param.put( REPO_PARAM_ID, repoId );
        Response response = client.GET( RESOURCE_USER_MAPPING + name, param );
        return "true".equals( response.readEntity( String.class ).toString() );
    }

    public void store( String id, MappingPOJO mapping ) {
        client.PUT( RESOURCE_USER_MAPPING + id, mapping );
    }

    public void store( String id, IssueMetrics issueMetrics ) {
        client.PUT( RESOURCE_ISSUE_METRIC + id, issueMetrics );
    }

    public void store( String id, RevisionPOJO rev ) {
        client.PUT( RESOURCE_COMMIT + id, rev );
    }

    public void store( String id, MetricPOJO metric ) {
        client.PUT( RESOURCE_METRIC + id, metric );
    }

    public void store( String id, AnalisisPOJO analysis ) {
        client.PUT( RESOURCE_ANALYSIS + id, analysis );
    }

    // TODO FIXME en todos los getter si el connector devuelve un response de
    // error, entonces devolver null
    public List<RevisionPOJO> getRevisions() {
=======
	// RESEARCH Singleton Pattern Java Thread-Safe

	// Estos RESOURCEs serian los del Connector de la DB
	private static final String RESOURCE_USER_MAPPING = "mapping/";
	private static final String RESOURCE_COMMIT = "commit/";
	private static final String RESOURCE_METRIC = "metric/";
	private static final String RESOURCE_ANALYSIS = "analysis/";
	private static final String RESOURCE_UNSCANNED_REVISIONS = "revision/";
	
	private static final Logger LOG = Logger.getLogger( Database.class.getName() );

	private final static String REPO_PARAM_ID = "repo";

	private final static String DEFAULT_LOCATION_CONNECTOR = "http://localhost:8080/core/rest/connectors/elasticsearch/"; //$NON-NLS-1$

	private RESTClient client;

	public String getURL() {
		return DEFAULT_LOCATION_CONNECTOR;
	}

	public Database() {
		try {
			client = new RESTClient(DEFAULT_LOCATION_CONNECTOR);
		} catch (MalformedURLException e) {
		  LOG.log( Level.SEVERE, e.toString(), e );
		}
	}

	public boolean isValidDeveloper(String name, String repoId) {
		Map<String, String> param = new HashMap<String, String>();
		param.put(REPO_PARAM_ID, repoId);
		Response response = client.GET(RESOURCE_USER_MAPPING + name, param);
		return "true".equals(response.readEntity(String.class).toString());
	}

	public void store(String id, MappingPOJO mapping) {
		client.PUT(RESOURCE_USER_MAPPING + id, mapping);
	}

	public void store(String id, RevisionPOJO rev) {
		client.PUT(RESOURCE_COMMIT + id, rev);
	}

	public void store(String id, MetricPOJO metric) {
		client.PUT(RESOURCE_METRIC + id, metric);
	}

	public void store(String id, AnalisisPOJO analysis) {
		client.PUT(RESOURCE_ANALYSIS + id, analysis);
	}
	
	//TODO FIXME en todos los getter si el connector devuelve un response de error, entonces devolver null
	public List<RevisionPOJO> getRevisions() {	
	    try {
	        return client.GET(RESOURCE_UNSCANNED_REVISIONS).readEntity(new GenericType<List<RevisionPOJO>>(){});
	    } catch (Exception e) {
	        LOG.log( Level.SEVERE, e.toString(), e );
	        return new ArrayList<RevisionPOJO>();
	    }
	}

	public List<AnalisisPOJO> getAnalisis() {		
>>>>>>> 938a33bd5cd3771d9f53e482b7f2c15e318a58a1
        try {
            return client.GET( RESOURCE_UNSCANNED_REVISIONS ).readEntity( new GenericType<List<RevisionPOJO>>() {
            } );
        } catch (Exception e) {
            return new ArrayList<RevisionPOJO>();
        }
    }

    public List<AnalisisPOJO> getAnalisis() {
        try {
            return client.GET( RESOURCE_ANALYSIS ).readEntity( new GenericType<List<AnalisisPOJO>>() {
            } );
        } catch (Exception e) {
            LOG.log( Level.SEVERE, e.toString(), e );
            return new ArrayList<AnalisisPOJO>();
        }
    }

    public List<MetricPOJO> getMetrics() {
        try {
            return client.GET( RESOURCE_METRIC ).readEntity( new GenericType<List<MetricPOJO>>() {
            } );
        } catch (Exception e) {
            LOG.log( Level.SEVERE, e.toString(), e );
            return new ArrayList<MetricPOJO>();
        }
    }

}