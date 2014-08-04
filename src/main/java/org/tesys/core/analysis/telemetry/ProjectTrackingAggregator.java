package org.tesys.core.analysis.telemetry;

import java.util.List;

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

  @Override
  public List<MetricPOJO> getMetricsID() {
    // TODO Auto-generated method stub
    return null;
  }

}
