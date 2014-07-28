package org.tesys.connectors.tracking.jira;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class GenericJSONClient {
	
	private static final MediaType JSONMediaType = MediaType.APPLICATION_JSON_TYPE ;
	
	private String URL ;

	public static void main( String args[] ) {
		System.out.println("hello world ");
		
		GenericJSONClient client = new GenericJSONClient("http://ing.exa.unicen.edu.ar:8086/") ;
		System.out.println(client.GET("/atlassian-jira-6.0/rest/api/2/"));
	}
	
	public GenericJSONClient() {}
	
	public GenericJSONClient( String url ) {
		URL = url ;
	}
	
	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}
	
	public String GET( String resource /* TODO faltan params*/) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(URL).path(resource);
		 
		Form form = new Form();
		form.param("x", "foo");
		form.param("y", "bar");
		
		//comentario 
		Response response =	target
								.request(JSONMediaType)
								.get();
				
		return response.toString() ;
		
	}
	
	
	public void PUT( String resource, String Json ) {
		//TODO PUT
	}

	public void POST( String resource, String Json ) {
		//TODO POST
	}
	
	public void DELETE( String resource ) {
		//TODO 
	}
	
		
	
	
}
