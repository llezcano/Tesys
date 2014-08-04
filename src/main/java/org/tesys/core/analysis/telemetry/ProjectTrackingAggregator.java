package org.tesys.core.analysis.telemetry;

/**
 * Este agregador es el obligatorio ya que es el que coloca el id de la tarea
 * COmo asi tambien el usuario
 *
 */

public class ProjectTrackingAggregator implements Aggregator {

  @Override
  public IssueMetrics agregateMetrics(IssueMetrics issueMetrics) {
    String key = issueMetrics.getIssueId();
    //TODO buscar en jira los datos y agregarlos a  issueMetrics
    return issueMetrics;
  }

}
