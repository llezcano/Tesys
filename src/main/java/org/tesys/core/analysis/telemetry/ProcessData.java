package org.tesys.core.analysis.telemetry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tesys.core.analysis.sonar.MetricPOJO;
import org.tesys.core.analysis.sonar.SonarAnalizer;
import org.tesys.core.analysis.sonar.StoreResults;
import org.tesys.core.analysis.telemetry.dbutilities.DBUtilities;
import org.tesys.core.analysis.telemetry.util.Formatter;
import org.tesys.core.analysis.telemetry.util.Searcher;
import org.tesys.core.db.DatabaseFacade;
import org.tesys.core.project.scm.RevisionPOJO;
import org.tesys.core.project.scm.SCMManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;



public class ProcessData {

  private DatabaseFacade db;

  private static ProcessData instance = null;

  private ProcessData() {
    db = new DatabaseFacade();
  }

  public static ProcessData getInstance() {
    if (instance == null) {
      instance = new ProcessData();
    }
    return instance;
  }



  public String executeProcessor() {

    // Se obtiene todos los datos de las revisiones
    List<RevisionPOJO> revisions = db.getSCMRevisions();
    Collections.sort(revisions); // se ordenan por fecha

    // Se obtienen todas las metricas que existen en sonar, con tipo y demas
    List<MetricPOJO> metrics = db.getMetrics();

    SonarAnalisis sonarAnalisis = new SonarAnalisis( revisions, metrics);

    // Se obtienen los analisis por commit acumulado
    List<JsonNode> analisisJson = sonarAnalisis.getDataJsonFormat(sonarAnalisis.getAnalisis());

    // Se obtienen los analisis por commit individual
    List<JsonNode> analisisJsonPorCommit = sonarAnalisis.getAnalisisPorCommit(analisisJson);

    // Se obtienen todas las tareas que existen de jira
    JiraIssues jira = new JiraIssues(client);
    List<JsonNode> issuesID = jira.getIssuesId();

    // se obtienen los analisis por tarea
    List<JsonNode> analisisJsonPorTarea =
        sonarAnalisis.getAnalisisPorTarea(analisisJsonPorCommit, revisiones, issuesID);

    List<JsonNode> issues = jira.getIssues();

    StoreInElasticSearch store =
        new StoreInElasticSearch(senderhost, senderport, senderpath, senderapp, senderdata);

    for (JsonNode issue : issues) {
      JsonNode analisis = Searcher.searchIssue(issue, analisisJsonPorTarea);
      analisisJsonPorTarea.remove(analisis);
      ObjectNode objectAnalisis = (ObjectNode) analisis;
      objectAnalisis.remove(DBUtilities.JIRA_TASK_TAG);
      analisis = (JsonNode) objectAnalisis;

      Formatter.joinJson(issue, analisis);

      store.send(issue.get(DBUtilities.KEY_TAG).asText(), issue.toString());
    }

  }


}
