package org.tesys.core.db;

import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.fasterxml.jackson.databind.JsonNode;


@Path("/db")
@Singleton
public class DatabaseFacade {   
  
  @PUT
  @Path("{index}/{dtype}/{id}")
  public String store(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("dtype") String id, JsonNode data) {
    
    return "Supuestamente hubiera insertado el dato";
  }

  @DELETE
  @Path("{index}/{dtype}/{id}")
  public String delete(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("dtype") String id) {

    return "Supuestamente hubiera borrado el dato";
  }

  
  @GET
  @Path("{index}/{dtype}/{id}")
  public String get(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("dtype") String id) {
    
    return "Supuestamente hubiera devuelto el dato";
  }
  
  



}
