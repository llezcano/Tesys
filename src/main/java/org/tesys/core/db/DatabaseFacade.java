package org.tesys.core.db;


import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.tesys.util.GenericJSONClient;


@Path("/db")
@Singleton
public class DatabaseFacade {

  private static final String DEFAULT_LOCATION_CONNECTOR = "http://localhost:8080/core/rest/connectors/elasticsearch"; //$NON-NLS-1$
  GenericJSONClient client;

  @PostConstruct
  public void init() {
    client = new GenericJSONClient(DEFAULT_LOCATION_CONNECTOR);
  }
  
  /**
   * El constructor debe estar para cuando se llama la aplicacion desde el core y no
   * desde rest
   */
  public DatabaseFacade() {
    client = new GenericJSONClient(DEFAULT_LOCATION_CONNECTOR);
  }

  @PUT
  @Path("/config")
  public String changeConnectorLocation(String url) {
    client.setURL(url);
    return Messages.getString("DatabaseFacade.urlchanged"); //$NON-NLS-1$
  }


  @PUT
  @Path("{index}/{dtype}/{id}")
  public String PUT(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("id") String id, String data) {

    return client.PUT(index + "/" + dtype + "/" + id, data); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @DELETE
  @Path("{index}/{dtype}/{id}")
  public String DELETE(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("id") String id) {

    return client.DELETE(index + "/" + dtype + "/" + id); //$NON-NLS-1$ //$NON-NLS-2$
  }


  @GET
  @Path("{index}/{dtype}/{id}")
  public String GET(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("id") String id) {

    return client.GET(index + "/" + dtype + "/" + id); //$NON-NLS-1$ //$NON-NLS-2$
  }


  @POST
  @Path("{index}/{dtype}")
  public String POST(@PathParam("index") String index, @PathParam("dtype") String dtype,
      String query) {
    
    return client.POST(index + "/" + dtype , query); //$NON-NLS-1$
  }

}
