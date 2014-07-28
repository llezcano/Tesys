package org.tesys.core.db;


import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.tesys.util.SearchJSONClient;


@Path("/db")
@Singleton
public class DatabaseFacade {

  SearchJSONClient client;
  //TODO hacer todos los return piola

  @PostConstruct
  public void init() {
    client = new SearchJSONClient("http://localhost:8080/core/");
  }

  @PUT
  @Path("/config")
  public String changeConnectorLocation(String url) {
    client.setURL(url);
    return "Supuestamentese cambio la direccion del conector";
  }


  @PUT
  @Path("{index}/{dtype}/{id}")
  public String PUT(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("dtype") String id, String data) {

    client.PUT(index + "/" + dtype + "/" + id, data);
    return "Supuestamente hubiera insertado el dato";
  }

  @DELETE
  @Path("{index}/{dtype}/{id}")
  public String DELETE(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("dtype") String id) {

    client.DELETE( index + "/" + dtype + "/" + id );
    return "Supuestamente hubiera borrado el dato";
  }


  @GET
  @Path("{index}/{dtype}/{id}")
  public String GET(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("dtype") String id) {

    client.GET( index + "/" + dtype + "/" + id );
    return "Supuestamente hubiera devuelto el dato";
  }
  
  
  @GET
  @Path("{index}/{dtype}")
  public String SEARCH(@PathParam("index") String index, @PathParam("dtype") String dtype, String query) {

    client.SEARCH( index + "/" + dtype + "/", query );
    return "Supuestamente hubiera devuelto el dato";
  }

}
