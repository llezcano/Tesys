package org.tesys.connectors.db.elasticsearch;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.tesys.core.analysis.sonar.AnalisisPOJO;
import org.tesys.core.analysis.sonar.MetricPOJO;
import org.tesys.core.project.scm.RevisionPOJO;
import org.tesys.core.project.scm.MappingPOJO;
import org.tesys.util.RESTClient;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Facade para Elastic Search, el cual se encarga de interactuar con la API REST
 * de ES.
 * 
 * En esta clase deberian ir todas las interacciones con ES, tanto como Queries
 * y Stores.
 * 
 * Quien use esta clase no deveria conocer la naturaleza de ES, como por ejemplo
 * el lenguaje de Queries o como acceder a la API.
 * 
 * 
 * @author rulo
 * 
 */
public class ElasticSearch {

    private RESTClient client;

    // QUERY
    public static final String QUERY = "_search";

    // IDEXs
    public static final String INDEX_SCM = "scm"; //$NON-NLS-1$
    public static final String INDEX_ANALYZER = "analyzer";

    // DTYPEs
    public static final String DTYPE_REVISION = "revision"; //$NON-NLS-1$
    public static final String DTYPE_USER = "user"; //$NON-NLS-1$
    public static final String DTYPE_ANALYSIS = "analysis";
    public static final String DTYPE_METRIC = "metric";

    public static final String REPOSITORY_DATA_ID = "repository"; //$NON-NLS-1$
    public static final String PROJECT_TRACKING_USER_DATA_ID = "project_tracking_user"; //$NON-NLS-1$
    public static final String SCM_USER_DATA_ID = "scm_user"; //$NON-NLS-1$

    public static final String SLASH = "/";

    public ElasticSearch() {
	try {
	    client = new RESTClient("http://localhost:9200/");
	} catch (MalformedURLException e) {
	    // TODO ver si hay que hacer un exit que baje solo este servicio
	    e.printStackTrace();
	}
    }

    /**
     * Este metodo devuelve la cantidad de elementos del indice ese para poder
     * hacer una consulta de el tamaño indicado
     * 
     * @param index
     * @param dtype
     * @return
     */
    private Integer getSize(String index, String dtype) {
	// Se buscan todos los datos sin cuerpo para que devuelva cuantos hay en
	// total
	Map<String, String> param = new HashMap<String, String>();
	param.put("search_type", "count");

	JsonNode jsonResponse = client.GET(pathToStore(index, dtype, QUERY),
		param).readEntity(JsonNode.class);

	return jsonResponse.get("hits").get("total").asInt();
    }

    /**
     * Genera la ruta del ElasticSearch donde se almacenara el dato.
     * 
     * @param index
     * @param dType
     * @param id
     * @return
     */
    private String pathToStore(String index, String dType, String id) {
	return index + SLASH + dType + SLASH + id;
    }

    public void store(String ID, MappingPOJO mapping) {
	client.POST(pathToStore(INDEX_SCM, DTYPE_USER, ID), mapping);
    }

    public void store(String ID, RevisionPOJO revision) {
	client.POST(pathToStore(INDEX_SCM, DTYPE_REVISION, ID), revision);
    }

    public void store(String ID, AnalisisPOJO analysis) {
	// TODO Cambiar nombre de clase de AnalisisPOJO a AnalysisPOJO
	client.POST(pathToStore(INDEX_ANALYZER, DTYPE_ANALYSIS, ID), analysis);
    }

    public void store(String ID, MetricPOJO metric) {
	client.POST(pathToStore(INDEX_ANALYZER, DTYPE_METRIC, ID), metric);
    }

    public boolean isValidDeveloper(String name, String repoID) {
	// TODO sacar el harcodeado y calcular automaticamente (por reflexion) los campos de match
	// RESEARCH fijarse si aca me pueden inyectar una query y de ser asi arreglarlo
	String query = "{ \"query\": { \"bool\": { \"must\": [ { \"match\": { \"scmUser\": \""
		+ name
		+ "\" }}, { \"match\": {\"repository\": \""
		+ repoID
		+ "\" }} ] } } }";
	JsonNode jsonResponse = client.POST(
		this.pathToStore(INDEX_SCM, DTYPE_USER, QUERY), query)
		.readEntity(JsonNode.class);
	return jsonResponse.get("hits").get("total").asInt() > 0;
    }

    public RevisionPOJO[] getRevisions() {
	// TODO getRevisions() GENERATE QUERY and Adapt data
	return null;
    }

    public RevisionPOJO[] getUnscanedRevisions() {
	// TODO Auto-generated method stub
	return null;
    }

}

// @GET
// @Consumes(MediaType.APPLICATION_JSON)
// @Produces(MediaType.APPLICATION_JSON)
/*
 * @Path("{index}/{dtype}/{id}") public Response GET(@PathParam("index") String
 * index,
 * 
 * @PathParam("dtype") String dtype, @PathParam("id") String id) {
 * 
 * return client.GET(index + "/" + dtype + "/" + id); }
 * 
 * /** Usada solamente para busquedas, se debe utilizar PUT para updates o
 * implementar El otro metodo
 * 
 * 
 * Como search nomas esta limitado a 10 por defecto lo que se hace es verificar
 * en primer lugar cuantos datos tiene el lugar donde se va buscar
 * 
 * Una vez que se sabe la cantidad de datos se hace la busqueda y se pide que se
 * retorne Esa cantidad como maximo (aunque la query puede reducir el tamaño al
 * igual que el filtro)
 * 
 * @param index
 * 
 * @param dtype
 * 
 * @param query
 * 
 * @return
 */
// TODO Habria que usar POJOS en vez de strings, el tema que los pojos son
// medios complicados
// @Consumes(MediaType.APPLICATION_JSON)
// @Produces(MediaType.APPLICATION_JSON)
/*
 * @POST
 * 
 * @Path("{index}/{dtype}") public String POST(@PathParam("index") String index,
 * 
 * @PathParam("dtype") String dtype, String query) {
 * 
 * String size = getSize(index, dtype);
 * 
 * JsonFactory factory = new JsonFactory(); ObjectMapper mapper = new
 * ObjectMapper(factory); JsonNode jsonQuery = null; try { jsonQuery =
 * mapper.readTree(query); } catch (IOException e) { e.printStackTrace(); }
 * 
 * ObjectNode ob = (ObjectNode) jsonQuery; ob.put("size", size);
 * 
 * Response response = client.POST(index + "/" + dtype + "/_search",
 * ob.toString());
 * 
 * JsonNode jsonResponse = response.readEntity(JsonNode.class);
 * 
 * // TODO si devuelve 0 resultados seguro se debe romper (testear todos // los
 * metodos con db vacia y // cosas asi)
 * 
 * JsonNode hits = jsonResponse.get("hits").get("hits");
 * 
 * Iterator<JsonNode> it = hits.iterator();
 * 
 * ArrayNode results = mapper.createArrayNode();
 * 
 * while (it.hasNext()) { results.add(it.next().get("_source")); }
 * 
 * ObjectNode finalResult = mapper.createObjectNode();
 * finalResult.put("results", results);
 * 
 * return finalResult.toString(); }
 * 
 * // // //EJEMPLO // // @GET // //@Consumes(MediaType.APPLICATION_JSON) //
 * //@Produces(MediaType.APPLICATION_JSON) //
 * @Path("getSCM-JIRA-MAPPING/{index}/{dtype}") // public void getMapping( )
 * /*index y dtype por rest
 */
// {
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

