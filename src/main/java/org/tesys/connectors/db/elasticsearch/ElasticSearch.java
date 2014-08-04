package org.tesys.connectors.db.elasticsearch;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.tesys.core.analysis.sonar.AnalisisPOJO;
import org.tesys.core.analysis.sonar.MetricPOJO;
import org.tesys.core.project.scm.RevisionPOJO;
import org.tesys.core.project.scm.MappingPOJO;
import org.tesys.util.RESTClient;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
	
	
	private ArrayNode executeQuery(String index, String type) {
		ArrayNode response = (ArrayNode) client
				.GET(this.pathToStore(index, type, QUERY))
				.readEntity(JsonNode.class).get("hits").get("hits");
		return response ;
	}

	private ArrayNode executeQuery(String index, String type, String query) {
		ArrayNode response = (ArrayNode) client
				.POST(this.pathToStore(index, type, QUERY), query)
				.readEntity(JsonNode.class).get("hits").get("hits");
		return response ;
	}
	
	private List<? extends Object> extractElements(ArrayNode arrayNode, Class<? extends Object> aClass){
		ObjectMapper mapper = new ObjectMapper();

		Iterator<JsonNode> it = arrayNode.elements();
		List<Object> elements = new ArrayList<Object>();
		while (it.hasNext()) {
			JsonNode j = ((JsonNode) it.next()).get("_source");
			try {
				Object elem  = mapper.readValue(j.toString(), aClass);
				elements.add(elem);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			it.remove();
		}
		return elements ;
	}
	
	
	private List<? extends Object> get(String index, String type, Class<? extends Object> aClass) {
		ArrayNode arrayNode = executeQuery(index, type) ;
		return extractElements( arrayNode, aClass);
	}
	
	
	private List<? extends Object> get(String index, String type, String query, Class<? extends Object> aClass) {
		ArrayNode arrayNode = executeQuery(index, type, query) ;
		return extractElements( arrayNode, aClass);
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

	public void store(String id, MappingPOJO mapping) {
		client.POST(pathToStore(INDEX_SCM, DTYPE_USER, id), mapping);
	}

	public void store(String id, RevisionPOJO revision) {
		client.POST(pathToStore(INDEX_SCM, DTYPE_REVISION, id), revision);
	}

	public void store(String id, AnalisisPOJO analysis) {
		// TODO Cambiar nombre de clase de AnalisisPOJO a AnalysisPOJO
		client.POST(pathToStore(INDEX_ANALYZER, DTYPE_ANALYSIS, id), analysis);
	}

	public void store(String id, MetricPOJO metric) {
		client.POST(pathToStore(INDEX_ANALYZER, DTYPE_METRIC, id), metric);
	}

	public boolean isValidDeveloper(String name, String repoId) {
		// TODO FIXME sacar el harcodeado y calcular automaticamente (por
		// reflexion) los campos de match
		
		//TODO HIGH Si no existe entonces no devolver error, si no devolver false

		// TODO RESEARCH fijarse si aca me pueden inyectar una query y de ser
		// asi arreglarlo
		String query = "{ \"query\": { \"bool\": { \"must\": [ { \"match\": { \"scmUser\": \""
				+ name
				+ "\" }}, { \"match\": {\"repository\": \""
				+ repoId
				+ "\" }} ] } } }";
		JsonNode jsonResponse = client.POST(
				this.pathToStore(INDEX_SCM, DTYPE_USER, QUERY), query)
				.readEntity(JsonNode.class);
		return (jsonResponse.get("hits").get("total").asInt() > 0);
	}


	public List<RevisionPOJO> getRevisions() {
		return (List<RevisionPOJO>) this.get(INDEX_SCM, DTYPE_REVISION, RevisionPOJO.class);
	}
	
	public List<AnalisisPOJO> getAnalysis() {
		return (List<AnalisisPOJO>) this.get(INDEX_ANALYZER, DTYPE_ANALYSIS, AnalisisPOJO.class);
	}
	
	
	public List<MetricPOJO> getMetrics() {
		return (List<MetricPOJO>) this.get(INDEX_ANALYZER, DTYPE_METRIC, MetricPOJO.class);
	}
}
