package org.tesys.util;

import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.MalformedURLException;
import java.net.URL;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 * Cliente para Servicios Web REST el cual usa Jersey.
 * 
 * @author rulo
 * 
 */
public class RESTClient implements HTTPClient {

    private MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;

    private URL url ;
    private HttpAuthenticationFeature auth;
    private Client client;

    /**
     * Client constructor without authorization
     * 
     * @param url
     * @throws MalformedURLException
     */
    public RESTClient( String url ) throws MalformedURLException {
        this.url = new URL( url );
        auth = null;
        client = ClientBuilder.newClient();
    }

    public RESTClient( String url, MediaType mediaType ) throws MalformedURLException {
        this.mediaType = mediaType ;
        this.url = new URL( url );
        auth = null;
        client = ClientBuilder.newClient();
    }
    
    
    /**
     * Client constructor with authorization
     * 
     * @param url
     * @throws MalformedURLException
     */
    public RESTClient( String url, String user, String pass ) throws MalformedURLException {
        this.url = new URL( url );
        auth = HttpAuthenticationFeature.basic( user, pass );
        client = ClientBuilder.newClient();
        // Setting client authorization
        client.register( auth );
    }


    public RESTClient( String url, String user, String pass, MediaType mediaType ) throws MalformedURLException {
        this.url = new URL( url );
        this.mediaType = mediaType ;
        auth = HttpAuthenticationFeature.basic( user, pass );
        client = ClientBuilder.newClient();
        // Setting client authorization
        client.register( auth );
    }
    
    //MediaType type
    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType( MediaType mediaType ) {
        this.mediaType = mediaType;
    }

    public String getURL() {
        return url.toString();
    }

    public void setURL( String url ) throws MalformedURLException {
        this.url = new URL( url );
    }

    public Response GET( String resource, Map<String, String> params ) {
        if (params == null)
            return this.GET( resource );

        WebTarget target = client.target( getURL() ).path( resource );
        // adding parameters
        for (Map.Entry<String, String> param : params.entrySet())
            target = target.queryParam( param.getKey(), param.getValue() );

        // making request
        return target.request( this.mediaType ).get();
    }

    public Response GET( String resource ) {

        return client
                .target( this.getURL() )
                .path( resource )
                .request( this.mediaType )
                .get();

    }

    public Response PUT( String resource, Object serealizable ) {

        return client
                .target( this.getURL() )
                .path( resource )
                .request( this.mediaType )
                .put( Entity.entity( serealizable, this.mediaType ) );

    }

    public Response POST( String resource, Object serealizable ) {

        return client
                .target( this.getURL() )
                .path( resource )
                .request( this.mediaType )
                .post( Entity.entity( serealizable, this.mediaType ) );

    }

    public Response DELETE( String resource ) {

        return client
                .target( this.getURL() )
                .path( resource )
                .request( this.mediaType )
                .delete();

    }

}
