package org.tesys.util;

import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 * Jersey implementation for HTTP client
 * @author rulo
 * TODO en ves de devolver String devolver responses, cada quien va a saber que hacer con eso
 */
public class GenericJSONClient implements GenericClient {

	protected static final MediaType JSONMediaType = MediaType.APPLICATION_JSON_TYPE ;

	protected String URL ;
	private HttpAuthenticationFeature auth ;
	protected Client client ;


	public GenericJSONClient( String url ) {
		URL = url ;
		auth = null ;
		client = ClientBuilder.newClient();
	}

	public GenericJSONClient( String url, String user, String pass ) {
		URL = url ;
		auth = HttpAuthenticationFeature.basic(user, pass);
		client = ClientBuilder.newClient();
		// client autorization
		client.register(auth) ;

	}


	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	@Override
	public String GET( String resource, Map<String, String> params ) {
		if (params == null) return GET( resource ) ;

		WebTarget target = client.target(URL).path(resource);
		//adding parameters
		for ( Map.Entry<String, String> param : params.entrySet() ) 
			target = target.queryParam(param.getKey(), param.getValue()) ;

		//making request 
		Response response =	target.request(JSONMediaType).get();

		//response.toString() tiene la informacion de la respuesta de la peticion... por ejemplo si fue un 404
		// TODO excepciones para respuestas invalidas
		return response.readEntity(String.class) ;
	}


	@Override
	public String GET( String resource) {

		Response response =	client
							.target(URL)
							.path(resource)
							.request(JSONMediaType)
							.get();

		//response.toString() tiene la informacion de la respuesta de la peticion... por ejemplo si fue un 404
		// TODO excepciones para respuestas invalidas
		return response.readEntity(String.class) ;

	}

	@Override
	public String GET(String resource, String JSON) {

	    Response response = client
	    					.target(URL)
	    					.path(resource)
	    					.request(JSONMediaType)
	    					.post(Entity.entity(JSON, JSONMediaType));

	    return response.readEntity(String.class);

	}

	@Override	
	public String PUT( String resource, String JSON ) {

		Response response =	client
							.target(URL)
							.path(resource)
							.request()
							.put(Entity.entity(JSON, JSONMediaType)) ;

		return response.toString() ;

	}

	@Override
	public String POST( String resource, String JSON ) {

		Response response =	client
							.target(URL)
							.path(resource)
							.request(JSONMediaType)
							.post(Entity.entity(JSON, JSONMediaType)) ;

		return response.readEntity(String.class) ;
	}


	@Override	
	public String DELETE( String resource ) {

		Response response =	client
							.target(URL)
							.path(resource)
							.request()
							.delete() ;

		return response.toString() ;
	}




}