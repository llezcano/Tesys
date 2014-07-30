package org.tesys.core.db;


import java.net.MalformedURLException;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.tesys.rest.RESTClient;


import com.fasterxml.jackson.databind.JsonNode;


@Path("/db")
@Singleton
public class DatabaseFacade {

  private static final String DEFAULT_LOCATION_CONNECTOR = "http://localhost:8080/core/rest/connectors/elasticsearch"; //$NON-NLS-1$
  RESTClient client;

  @PostConstruct
  public void init() {
    try {
      client = new RESTClient(DEFAULT_LOCATION_CONNECTOR);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * El constructor debe estar para cuando se llama la aplicacion desde el core y no
   * desde rest
   */
  public DatabaseFacade() {
    try {
      client = new RESTClient(DEFAULT_LOCATION_CONNECTOR);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  @PUT
  @Path("/config")
  public String changeConnectorLocation(String url) {
    try {
      client.setURL(url);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return Messages.getString("DatabaseFacade.urlchanged"); //$NON-NLS-1$
  }


  @PUT
  @Path("{index}/{dtype}/{id}")
  public String PUT(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("id") String id, String data) {
    
    Response response = client.PUT(index + "/" + dtype + "/" + id, data); //$NON-NLS-1$ //$NON-NLS-2$

    return response.readEntity( String.class );
  }

  @DELETE
  @Path("{index}/{dtype}/{id}")
  public String DELETE(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("id") String id) {
    
    Response response = client.DELETE(index + "/" + dtype + "/" + id); //$NON-NLS-1$ //$NON-NLS-2$

    return response.readEntity( String.class );
  }


  @GET
  @Path("{index}/{dtype}/{id}")
  public String GET(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("id") String id) {
    
    Response response = client.GET(index + "/" + dtype + "/" + id); //$NON-NLS-1$ //$NON-NLS-2$

    return response.readEntity( String.class );
  }


  @POST
  @Path("{index}/{dtype}")
  public String POST(@PathParam("index") String index, @PathParam("dtype") String dtype,
      String query) {
    
    Response response = client.POST(index + "/" + dtype , query); //$NON-NLS-1$
    
    return response.readEntity( String.class );
  }

}
