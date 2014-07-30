package org.tesys.connectors.db.elasticsearch;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.tesys.util.RESTClient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Path("/connectors/elasticsearch")
@Singleton
public class ElasticSearch {

  RESTClient client;

  @PostConstruct
  public void init() {
    try {
      client = new RESTClient("http://localhost:9200/");
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  @PUT
//@Consumes(MediaType.APPLICATION_JSON)
  //@Produces(MediaType.APPLICATION_JSON)
  @Path("{index}/{dtype}/{id}")
  public Response PUT(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("id") String id, String data) {

    return client.PUT(index + "/" + dtype + "/" + id, data);
  }

  @DELETE
//@Consumes(MediaType.APPLICATION_JSON)
  //@Produces(MediaType.APPLICATION_JSON)
  @Path("{index}/{dtype}/{id}")
  public Response DELETE(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("id") String id) {
  
    return client.DELETE(index + "/" + dtype + "/" + id);
  }

  /**
   * @param index
   * @param dtype
   * @param id
   * @return
   */
  @GET
//@Consumes(MediaType.APPLICATION_JSON)
  //@Produces(MediaType.APPLICATION_JSON)
  @Path("{index}/{dtype}/{id}")
  public Response GET(@PathParam("index") String index, @PathParam("dtype") String dtype,
      @PathParam("id") String id) {

    return client.GET(index + "/" + dtype + "/" + id);
  }
  

  /**
   * Usada solamente para busquedas, se debe utilizar PUT para updates o implementar
   * El otro metodo
   * 
   * 
   * Como search nomas esta limitado a 10 por defecto lo que se hace es verificar en primer
   * lugar cuantos datos tiene el lugar donde se va buscar
   * 
   * Una vez que se sabe la cantidad de datos se hace la busqueda y se pide que se retorne
   * Esa cantidad como maximo (aunque la query puede reducir el tamaño al igual que el filtro)
   * 
   * @param index
   * @param dtype
   * @param query
   * @return
   */
  @POST
  //TODO Habria que usar POJOS en vez de strings, el tema que los pojos son medios complicados
  //@Consumes(MediaType.APPLICATION_JSON)
  //@Produces(MediaType.APPLICATION_JSON)
  @Path("{index}/{dtype}")
  public String POST(@PathParam("index") String index, @PathParam("dtype") String dtype,
      String query) {
    
    String size = getSize(index, dtype);
    
    JsonFactory factory = new JsonFactory();
    ObjectMapper mapper = new ObjectMapper(factory);
    JsonNode jsonQuery = null;
    try {
      jsonQuery = mapper.readTree( query );
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    ObjectNode ob = (ObjectNode) jsonQuery;
    ob.put("size", size);
    
    Response response = client.POST(index + "/" + dtype + "/_search", ob.toString());
    
    JsonNode jsonResponse = response.readEntity( JsonNode.class );
    
    //TODO si devuelve 0 resultados seguro se debe romper (testear todos los metodos con db vacia y cosas asi)
    
    JsonNode hits = jsonResponse.get("hits").get("hits");
 
    Iterator<JsonNode> it = hits.iterator();
    
    ArrayNode results = mapper.createArrayNode();
    
    while( it.hasNext() ) {
      results.add(it.next().get("_source"));
    }
    
    ObjectNode finalResult = mapper.createObjectNode();
    finalResult.put("results", results);
    
    return finalResult.toString();
  }
  
  /**
   * Este metodo devuelve la cantidad de elementos del indice ese para poder hacer una
   * consulta de el tamaño indicado
   * @param index
   * @param dtype
   * @return
   */
  private String getSize(String index, String dtype) {
    //Se buscan todos los datos sin cuerpo para que devuelva cuantos hay en total
    Map<String, String> param = new HashMap<String, String>();
    param.put("search_type", "count");
    Response response = client.GET(index + "/" + dtype + "/_search", param );

    JsonNode jsonResponse = response.readEntity( JsonNode.class );

    String size = jsonResponse.get("hits").get("total").asText();
    return size;
  }

}
