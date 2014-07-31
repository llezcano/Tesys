package org.tesys.core.analysis.telemetry.util;

import java.util.List;

import org.tesys.core.analysis.telemetry.RevisionPOJO;
import org.tesys.core.analysis.telemetry.dbutilities.DBUtilities;

import com.fasterxml.jackson.databind.JsonNode;



public class Searcher {

  /**
   * Dada toda la informacion del jira sobre un issue especifico, devuelve el analisis de sonar para
   * esa tarea.
   * 
   * @param issue
   * @param analysis
   * @return
   */
  public static JsonNode searchIssue(JsonNode issue, List<JsonNode> analysis) {

    String issueKey = issue.get(DBUtilities.KEY_TAG).asText();

    for (JsonNode jsonNode : analysis) {
      if (jsonNode.get(DBUtilities.JIRA_TASK_TAG).asText().equals(issueKey)) {
        return jsonNode;
      }
    }

    return null;
  }



  /**
   * Dado un id de revision (por ejemplo "r1") busca la instancia de clase Revision que tenga ese id
   * 
   * @param r id de la revision que se desea buscar
   * @param revisiones conjunto de revisiones
   * @return revision que coincide con ese id
   */
  public static RevisionPOJO searchRevision(String r, List<RevisionPOJO> revisiones) {
    for (RevisionPOJO revision : revisiones) {
      if (revision.getRev().equals(r)) {
        return revision;
      }
    }
    return null;
  }

  /**
   * 
   * @param t id de la tarea que se desea buscar
   * @param analisisPorTarea conjunto de revisiones
   * @return revision que coincide con ese id
   */
  public static JsonNode searchTarea(String t, List<JsonNode> analisisPorTarea) {
    for (JsonNode analisisTarea : analisisPorTarea) {
      if (analisisTarea.get(DBUtilities.JIRA_TASK_TAG).asText().equals(t)) {
        return analisisTarea;
      }
    }
    return null;
  }


  /**
   * Dado un String key busca en lo que devuelve sonar /api/metrics esa key y devuelve Toda la
   * informacion asociada a la misma
   * 
   * @param field
   * @param metrics
   * @return
   */
  public static JsonNode searchMetric(String field, List<JsonNode> metrics) {
    for (JsonNode js : metrics) {
      if (js.get(DBUtilities.KEY_TAG).asText().equals(field)) {
        return js;
      }
    }
    return null;
  }

}
