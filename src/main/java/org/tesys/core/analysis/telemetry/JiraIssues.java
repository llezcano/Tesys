package org.tesys.core.analysis.telemetry;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.tesys.core.analysis.telemetry.dbutilities.DBUtilities;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * 
 * La mayoria de esta clase usa la API java de elasticsearch scroll, y el codigo es practicamente el
 * que ellos sugieren que se use para traer datos muy grandes desde la db
 * 
 */
public class JiraIssues {

  private static final String SERVER_ERROR = Messages.getString("servercantconsulted"); //$NON-NLS-1$
  private List<JsonNode> issues;
  private List<JsonNode> issuesId;

  public JiraIssues(Client client) {

    SearchResponse response = null;
    issues = new LinkedList<JsonNode>();
    issuesId = new LinkedList<JsonNode>();

    try {
      response =
          client.prepareSearch(DBUtilities.ES_JIRA_INDEX).setTypes(DBUtilities.ES_ISSUES_DATATYPE)
              .setSearchType(SearchType.SCAN).setScroll(new TimeValue(60000)).setSize(100)
              .execute().actionGet();
    } catch (ElasticsearchException e) {
      System.err.println(SERVER_ERROR + e.getMessage());
    }

    ObjectMapper mapper = new ObjectMapper();

    while (true) {

      try {
        response =
            client.prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(600000))
                .execute().actionGet();
      } catch (ElasticsearchException e) {
        System.err.println(SERVER_ERROR + e.getMessage());
      }


      for (SearchHit hit : response.getHits()) {

        JsonFactory factory = mapper.getJsonFactory();
        JsonParser jp;
        JsonNode objectIssue = null;
        ObjectNode objectIssueId = mapper.createObjectNode();

        objectIssueId.put(DBUtilities.JIRA_TASK_TAG, hit.getId().toString());

        // se guarda pero con barras de escape
        // objectIssue.put(ElasticSearchUtilities.JIRA_TASK_TAG, hit.getSourceAsString());

        try {
          jp = factory.createJsonParser(hit.getSourceAsString());
          objectIssue = mapper.readTree(jp);
        } catch (IOException e) {
          e.printStackTrace();
        }

        issues.add(objectIssue);
        issuesId.add(objectIssueId);
      }

      // Break condition: No hits are returned
      if (response.getHits().getHits().length == 0) {
        break;
      }
    }

  }

  public List<JsonNode> getIssuesId() {
    return issuesId;
  }

  public List<JsonNode> getIssues() {
    return issues;
  }


}
