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

import org.tesys.core.project.scm.RevisionPOJO;
import org.tesys.core.project.scm.MappingPOJO;
import org.tesys.core.project.scm.ScmPostCommitDataPOJO;
import org.tesys.util.RESTClient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Path("/connectors/elasticsearch")
@Singleton
public class ElasticSearch {

    private RESTClient client;

    public static final String REPOSITORY_DATA_ID = "repository"; //$NON-NLS-1$
    public static final String SCM_DTYPE_REVISIONS = "revisions"; //$NON-NLS-1$
    public static final String SCM_DTYPE_USERS = "users"; //$NON-NLS-1$
    public static final String SCM_DB_INDEX = "scm"; //$NON-NLS-1$
    public static final String PROJECT_TRACKING_USER_DATA_ID = "project_tracking_user"; //$NON-NLS-1$
    public static final String SCM_USER_DATA_ID = "scm_user"; //$NON-NLS-1$

    @PostConstruct
    public void init() {
	try {
	    client = new RESTClient("http://192.168.0.2:9200/");
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	}
    }

    @PUT
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces(MediaType.APPLICATION_JSON)
    @Path("{index}/{dtype}/{id}")
    public Response PUT(@PathParam("index") String index,
	    @PathParam("dtype") String dtype, @PathParam("id") String id,
	    String data) {

	return client.PUT(index + "/" + dtype + "/" + id, data);
    }

    @DELETE
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces(MediaType.APPLICATION_JSON)
    @Path("{index}/{dtype}/{id}")
    public Response DELETE(@PathParam("index") String index,
	    @PathParam("dtype") String dtype, @PathParam("id") String id) {

	return client.DELETE(index + "/" + dtype + "/" + id);
    }

    /**
     * @param index
     * @param dtype
     * @param id
     * @return
     */
    @GET
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces(MediaType.APPLICATION_JSON)
    @Path("{index}/{dtype}/{id}")
    public Response GET(@PathParam("index") String index,
	    @PathParam("dtype") String dtype, @PathParam("id") String id) {

	return client.GET(index + "/" + dtype + "/" + id);
    }

    /**
     * Usada solamente para busquedas, se debe utilizar PUT para updates o
     * implementar El otro metodo
     * 
     * 
     * Como search nomas esta limitado a 10 por defecto lo que se hace es
     * verificar en primer lugar cuantos datos tiene el lugar donde se va buscar
     * 
     * Una vez que se sabe la cantidad de datos se hace la busqueda y se pide
     * que se retorne Esa cantidad como maximo (aunque la query puede reducir el
     * tamaño al igual que el filtro)
     * 
     * @param index
     * @param dtype
     * @param query
     * @return
     */
    @POST
    // TODO Habria que usar POJOS en vez de strings, el tema que los pojos son
    // medios complicados
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces(MediaType.APPLICATION_JSON)
    @Path("{index}/{dtype}")
    public String POST(@PathParam("index") String index,
	    @PathParam("dtype") String dtype, String query) {

	String size = getSize(index, dtype);

	JsonFactory factory = new JsonFactory();
	ObjectMapper mapper = new ObjectMapper(factory);
	JsonNode jsonQuery = null;
	try {
	    jsonQuery = mapper.readTree(query);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	ObjectNode ob = (ObjectNode) jsonQuery;
	ob.put("size", size);

	Response response = client.POST(index + "/" + dtype + "/_search",
		ob.toString());

	JsonNode jsonResponse = response.readEntity(JsonNode.class);

	// TODO si devuelve 0 resultados seguro se debe romper (testear todos
	// los metodos con db vacia y
	// cosas asi)

	JsonNode hits = jsonResponse.get("hits").get("hits");

	Iterator<JsonNode> it = hits.iterator();

	ArrayNode results = mapper.createArrayNode();

	while (it.hasNext()) {
	    results.add(it.next().get("_source"));
	}

	ObjectNode finalResult = mapper.createObjectNode();
	finalResult.put("results", results);

	return finalResult.toString();
    }

    /**
     * Este metodo devuelve la cantidad de elementos del indice ese para poder
     * hacer una consulta de el tamaño indicado
     * 
     * @param index
     * @param dtype
     * @return
     */
    private String getSize(String index, String dtype) {
	// Se buscan todos los datos sin cuerpo para que devuelva cuantos hay en
	// total
	Map<String, String> param = new HashMap<String, String>();
	param.put("search_type", "count");
	Response response = client.GET(index + "/" + dtype + "/_search", param);

	JsonNode jsonResponse = response.readEntity(JsonNode.class);

	String size = jsonResponse.get("hits").get("total").asText();
	return size;
    }

    //
    // //EJEMPLO
    //
    // @GET
    // //@Consumes(MediaType.APPLICATION_JSON)
    // //@Produces(MediaType.APPLICATION_JSON)
    // @Path("getSCM-JIRA-MAPPING/{index}/{dtype}")
    // public void getMapping( /*index y dtype por rest*/ ) {
    //
    // //cuantos hay
    // String size = getSize(index, dtype);
    //
    // //query para hacer la busqeuda en el elastic
    // /*
    // * { "query": { "bool": { "must": [ { "match": { "scm_user": "<usuario>"
    // }}, { "match": {
    // * "repository": "<repositorio>" }} ] } } }
    // *
    // * Dirigirse a la documentacion del lenguaje para mas informacion:
    // *
    // http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-request-body.html
    // */
    // JsonFactory factory = new JsonFactory();
    // ObjectMapper om = new ObjectMapper(factory);
    // factory.setCodec(om);
    //
    // //busca que son cada variable en SCMManager
    // ObjectNode node1 = om.createObjectNode();
    // node1.put(SCM_USER_DATA_ID, scmData.getAuthor());
    // ObjectNode node11 = om.createObjectNode();
    // node11.put(MATCH_QL, node1);
    //
    // ObjectNode node2 = om.createObjectNode();
    // node2.put(REPOSITORY_DATA_ID, scmData.getRepository());
    // ObjectNode node22 = om.createObjectNode();
    // node22.put(MATCH_QL, node2);
    //
    // ArrayNode an = om.createArrayNode();
    // an.add(node11);
    // an.add(node22);
    // ObjectNode must = om.createObjectNode();
    // must.put(MUST_QL, an);
    // ObjectNode bool = om.createObjectNode();
    // bool.put(BOOL_QL, must);
    // ObjectNode query = om.createObjectNode();
    // query.put(QUERY_QL, bool);
    //
    // //agregarle el size
    //
    // //query.put("size", size);??? ver si no rompe nada
    //
    //
    // //se llama al elastic
    // Response response = client.POST(index + "/" + dtype + "/_search",
    // query.toString());
    //
    // //Devuelve los datos con sida, asi que hay que sacarlos usando lo que se
    // usa en el metod post
    // }
    //

    // TODO hacer los siguientes metodos

    public boolean isValidDeveloper(String name, String repoID) {
	// isValidDeveloper(@PathParam("name") String name, @PathParam("repoID")
	// String repoID)
	return true;
    }

    public RevisionPOJO[] getRevisions() {
	// TODO getRevisions()
	return null;
    }

    public void store(String ID, ScmPostCommitDataPOJO data) {

    }

    public void store(String ID, MappingPOJO mapping) {

    }
    
    //db.PUT(SCM_DB_INDEX, SCM_DTYPE_REVISIONS, id, data.toString());
    //db.PUT(SCM_DB_INDEX, SCM_DTYPE_USERS, id, data.toString());

}
