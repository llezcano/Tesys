/*
 * package org.tesys.core.analysis.telemetry;
 * 
 * 
 * import java.text.SimpleDateFormat; import java.util.ArrayList; import java.util.Collections;
 * import java.util.List;
 * 
 * import org.elasticsearch.ElasticsearchException; import
 * org.elasticsearch.action.search.SearchResponse; import org.elasticsearch.client.Client; import
 * org.tesys.core.analysis.telemetry.dbutilities.DBUtilities;
 * 
 * import com.fasterxml.jackson.core.JsonFactory; import com.fasterxml.jackson.core.JsonParser;
 * import com.fasterxml.jackson.databind.JsonNode; import
 * com.fasterxml.jackson.databind.ObjectMapper;
 * 
 * 
 * public class SvnRevisions {
 * 
 * 
 * private SearchResponse datosEnServer;
 * 
 * /** Costructor de la clase almacena los datos del servidor acerca del svn en una variable
 * 
 * @param client Un cliente de elesticsearch donde se puedan ejecutar consultas
 * 
 * public SvnRevisions(Client client) { try { datosEnServer =
 * client.prepareSearch(DBUtilities.ES_SVN_INDEX)
 * .setTypes(DBUtilities.ES_REVISIONS_DATATYPE).execute().actionGet(); } catch
 * (ElasticsearchException e) { System.err.println(Messages.getString("servernotfound") +
 * e.getMessage()); //$NON-NLS-1$ System.exit(1); }
 * 
 * }
 * 
 * public SearchResponse getData() { return datosEnServer; }
 * 
 * /** Transforma los datos recibidos del servidor en un arreglo de clases JsonNode Donde cada
 * elemento es una revision en json
 * 
 * @param data El resultado de buscar en el servidor por los datos del svn
 * 
 * @return Un arreglo con las revisiones almacenadas
 * 
 * public List<JsonNode> getDataInJsonFormat(SearchResponse data) {
 * 
 * ObjectMapper mapper = new ObjectMapper(); JsonFactory factory = mapper.getJsonFactory();
 * JsonParser jp; JsonNode jsonRevision = null; try { jp =
 * factory.createJsonParser(datosEnServer.toString()); jsonRevision = mapper.readTree(jp); } catch
 * (Exception e) { }
 * 
 * List<JsonNode> revisions = new ArrayList<JsonNode>();
 * 
 * /* Se queda con el contenido de lo que devueve cada busqueda (saca datos inicesarios) en cada
 * elemente de revisiones hay un id de rev, user, date y jira task
 * 
 * 
 * for (int i = 0; i < jsonRevision.get(DBUtilities.ES_HITS_TAG)
 * .get(DBUtilities.ES_HITS_TAG).size(); i++) { revisions
 * .add(jsonRevision.get(DBUtilities.ES_HITS_TAG) .get(DBUtilities.ES_HITS_TAG).get(i)
 * .get(DBUtilities.ES_SOURCE_TAG)); }
 * 
 * return revisions;
 * 
 * }
 * 
 * /** Mapea revisiones JSON a instancias de "Revision"
 * 
 * @param revisions Las revisiones en formato JSON
 * 
 * @return las revisiones en Clases Revision ordenadas por fecha de menor a mayor
 * 
 * 
 * public List<RevisionPOJO> getRevisions(List<JsonNode> revisions) {
 * 
 * List<RevisionPOJO> result = new ArrayList<RevisionPOJO>();
 * 
 * for (JsonNode jsonNode : revisions) {
 * 
 * ObjectMapper mapper = new ObjectMapper();
 * 
 * SimpleDateFormat dateFormat = new SimpleDateFormat(DBUtilities.SVN_DATE_FORMAT_IN_ES);
 * 
 * mapper.setDateFormat(dateFormat);
 * 
 * RevisionPOJO r = null; try { r = mapper.readValue(jsonNode, RevisionPOJO.class); } catch
 * (Exception e) { }
 * 
 * result.add(r);
 * 
 * }
 * 
 * Collections.sort(result); // se ordenan las revisiones por fecha return result;
 * 
 * }
 * 
 * }
 */
