package org.tesys.core.project.tracking;

import java.net.MalformedURLException;


import org.tesys.rest.RESTClient ;


/**
 * Este ProjectTracking es un cliente REST para el Connector de un Project Manager.
 * 
 * @author rulo
 *
 */
public class ProjectTrackingRESTClient implements ProjectTracking {

	private RESTClient client;
	
	private static String RESOURCE_ISSUES = "core/rest/project/issues/" ;
	private static String RESOURCE_USERS = "core/rest/project/users/" ;
	
	
	/**
	 * Main for testing.
	 * 
	 * @param args
	 * @throws MalformedURLException
	 */
	/*
	public static void main( String args[] ) throws MalformedURLException {
		
		ProjectTrackingRESTClient project = new ProjectTrackingRESTClient("http://localhost:8091/") ;
	
		//Query for user exists
		System.out.println("Homero J. Simpson is a developer ? "  + project.existUser("homerojsimpson") ) ;
		//Getting all users
		User[] users = project.getUsers() ;
		System.out.println("Users List: ") ;
		System.out.println(Arrays.toString(users)) ;
		System.out.println("TOTAL = " + users.length) ;

		//Query for issue exists
		System.out.println("ADA-1 is a issue ? "  + project.existUser("ADA-1") ) ;
		//Getting all issues
		Issue[] issues = project.getIssues() ;
		System.out.println("Issues List: ") ;
		System.out.println(Arrays.toString(issues)) ;
		System.out.println("TOTAL = " + issues.length) ;
		
	}
	*/
	
	
	public ProjectTrackingRESTClient( String url ) throws MalformedURLException {
		client = new RESTClient(url);
		//TODO RESEARCH Discovery Resources from Connector
	}
	
	@Override
	public Issue[] getIssues() {
		IssuePOJO[] issues = client.GET(RESOURCE_ISSUES).readEntity(IssuePOJO[].class);
		return issues;
	}

	@Override
	public User[] getUsers() {
		UserPOJO[] users = client.GET(RESOURCE_USERS).readEntity(UserPOJO[].class);
		return users;
	}


	@Override
	public boolean existUser(String key) {
		UserPOJO u = client.GET(RESOURCE_USERS + key ).readEntity(UserPOJO.class);
		return (u != null);
	}

	@Override
	public boolean existIssue(String key) {
		IssuePOJO i = client.GET(RESOURCE_ISSUES + key ).readEntity(IssuePOJO.class) ;
		return (i != null);
	}

}
