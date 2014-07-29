package org.tesys.connectors.tracking.jira;
/*

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.HashMap;
import org.glassfish.jersey.client
import org.codehaus.jettison.json.JSONException;
*/

import java.util.HashMap;
import java.util.Map;

import org.tesys.util.GenericJSONClient;


public class JiraRESTClient {
		
	private static final String API = "rest/api/2/" ;

	//resources from Jira API
	private static final String RESOURCE_GROUP = "group" ;
	private static final String RESOURCE_SEARCH = "search" ;
	private static final String RESOURCE_USER = "user" ;	
	//parameters for each resource
	// group
	private static final String RGROUP_GROUPNAME = "groupname" ;
	private static final String RGROUP_EXPAND = "expand" ;
	
	// search
	private static final String RSEARCH_QUERY = "jql" ;
	private static final String RSEARCH_START = "startAt"; 
	private static final String RSEARCH_MAX = "maxResults";
	
	// group
	private static final String RUSER_USERNAME = "username" ;

	
	private GenericJSONClient client ;
	
	
	public JiraRESTClient(String url, String user, String pass) {
		client = new GenericJSONClient(url, user, pass) ;

	}
	
	
	public String path(String resource) {
		return API + resource ;
	}
	
	
	public String getUsers( String group, Integer min, Integer max ) {
		//TODO validate params ?
		Map<String, String> params = new HashMap<String, String>(); 
		params.put(RGROUP_GROUPNAME, group );
		params.put(RGROUP_EXPAND, "users[" + min.toString() + ":" + max.toString() + "]") ;
		return client.GET(this.path(RESOURCE_GROUP), params) ;
	}
	
	
	public String getUser(String userKey) {
		//TODO
		Map<String, String> params = new HashMap<String, String>(); 
		params.put(RUSER_USERNAME, userKey );
		return client.GET(this.path(RESOURCE_USER), params) ;		
	}
	
	/**
	 * Consulta las Issues que cumplen con un determinado criterio, dicho criterio esta definido
	 * en Java Query Language (JQL).
	 * 
	 * @param query	Consulta a realizar en JQL 
	 * @return		Lista de Basic Issues que satisfacen la consulta, un basic Issue tiene poca informacion del 
	 * 				issue en si, pero tiene la Key.
	 * 
	 */
	public String getIssues( String query, Integer min, Integer max ) {
		HashMap<String, String> params = new HashMap<String, String>(); 
		params.put(RSEARCH_QUERY, query ); 
		params.put(RSEARCH_START, min.toString()) ;
		params.put(RSEARCH_MAX, max.toString()) ;
		return client.GET(this.path(RESOURCE_SEARCH), params) ;
	}
	
	/**
	 * Retorna todos los issues de JIRA 
	 * @param min	
	 * @param max	
	 * @return
	 */
	public String getIssues(Integer min, Integer max) {
		return getIssues("", min, max) ;
	}
	

	
}
