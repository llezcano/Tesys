package org.tesys.core.db;


import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.tesys.util.SearchJSONClient;


@Path("/db")
@Singleton
public class DatabaseFacade {

  SearchJSONClient client;

  @PostConstruct
  public void init() {
    client = new SearchJSONClient("http://localhost:8080/core/");
  }

  @PUT
  @Path("/config")
  // TODO un response
  public String changeConnectorLocation(String url) {
    client.setURL(url);
    return "Supuestamentese cambio la direccion del conector";
  }


  @PUT
  @Path("{index}/{dtype}/{id}")
  public String PUT(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("dtype") String id, String data) {

    return client.PUT(index + "/" + dtype + "/" + id, data);
  }

  @DELETE
  @Path("{index}/{dtype}/{id}")
  public String DELETE(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("dtype") String id) {

    return client.DELETE(index + "/" + dtype + "/" + id);
  }


  @GET
  @Path("{index}/{dtype}/{id}")
  public String GET(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("dtype") String id) {

    return client.GET(index + "/" + dtype + "/" + id);
  }


  @POST
  @Path("{index}/{dtype}")
  public String SEARCH(@PathParam("index") String index, @PathParam("dtype") String dtype,
      String query) {

    return client.SEARCH(index + "/" + dtype + "/", query);
  }

}
