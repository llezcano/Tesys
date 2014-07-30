package org.tesys.core.analysis.telemetry;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.tesys.core.analysis.telemetry.dbutilities.DBUtilities;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SonarMetrics {


  private SearchResponse response;

  public SonarMetrics(Client client) {

    try {
      response =
          client.prepareSearch(DBUtilities.ES_SONAR_INDEX)
              .setTypes(DBUtilities.ES_METRICAS_DATATYPE)
              .setSearchType(SearchType.DFS_QUERY_THEN_FETCH).execute().actionGet();

    } catch (ElasticsearchException e) {
      System.err.println(Messages.getString("servercantconsulted") + e.getMessage()); //$NON-NLS-1$
      System.exit(1);
    }

  }

  public SearchResponse getResponse() {
    return response;
  }


  public List<JsonNode> getMetrics(SearchResponse response) {

    ObjectMapper mapper = new ObjectMapper();
    JsonFactory factory = mapper.getJsonFactory();
    JsonParser jp;
    JsonNode jsonMetrics = null;

    try {
      jp = factory.createJsonParser(response.toString());
      jsonMetrics = mapper.readTree(jp);
    } catch (JsonParseException e) {
      System.err.println(Messages.getString("parsejsonerrormetrics") + e.getMessage()); //$NON-NLS-1$
      System.exit(1);
    } catch (IOException e) {
      System.err.println(Messages.getString("readjsonerrormetrics") + e.getMessage()); //$NON-NLS-1$
      System.exit(1);
    }

    /*
     * TODO: los datos sacados del sonar salen como arreglo [....] para guardarlo en la db hay que
     * anteponer {"metrics":[...]} Asi es un json valido sin ser un arreglo, porque el elasticsearch
     * no acepta arreglos pelados
     */

    List<JsonNode> metrics = new LinkedList<JsonNode>();

    JsonNode js =
        jsonMetrics.get(DBUtilities.ES_HITS_TAG).get(DBUtilities.ES_HITS_TAG)
            .get(0).get(DBUtilities.ES_SOURCE_TAG)
            .get(DBUtilities.ES_METRICS_TAG);

    if (js.isArray()) {
      for (final JsonNode objNode : js) {
        metrics.add(objNode);
      }
    }

    return metrics;
  }

}
