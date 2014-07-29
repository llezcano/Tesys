package org.tesys.connectors.db.elasticsearch;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.tesys.util.GenericJSONClient;


@Path("/connectors/elasticsearch")
@Singleton
public class ElasticSearch {

  GenericJSONClient client;

  @PostConstruct
  public void init() {
    client = new GenericJSONClient("http://localhost:9200/");
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
      @PathParam("id") String id) {
    
    return client.GET(index + "/" + dtype + "/" + id);
  }


  /**
   * Usada solamente para busquedas, se debe utilizar PUT para updates o implementar
   * El otro metodo
   * 
   * TODO cuando se hace un search en un lugar que no existe devolver no encontrado
   * 
   * @param index
   * @param dtype
   * @param query
   * @return
   */
  @POST
  @Path("{index}/{dtype}")
  public String POST(@PathParam("index") String index, @PathParam("dtype") String dtype,
      String query) {

    return client.POST(index + "/" + dtype + "/_search", query);
  }

}
