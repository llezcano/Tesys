package org.tesys.core.project.tracking;

import org.tesys.util.GenericJSONClient;

/**
 * Este ProjectTracking es un cliente REST para el connector del un Project Manager.
 * 
 * @author rulo
 *
 */
public class RESTClientProjectTracking implements ProjectTracking {

	public static void main( String args[] ) {
		
		GenericJSONClient client = new GenericJSONClient("http://localhost:8091/") ;
		System.out.println( client.GET("/core/rest/project/issues") );
		
	}
	public RESTClientProjectTracking() {
		// TODO setear parametros iniciales, por ejemplo URL del Project Manager Connector
	}
	
	@Override
	public Issue[] getIssues() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public User[] getUsers() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean existUser(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean existIssue(String key) {
		// TODO Auto-generated method stub
		return false;
	}

}
