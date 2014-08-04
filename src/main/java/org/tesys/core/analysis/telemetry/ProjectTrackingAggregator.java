package org.tesys.core.analysis.telemetry;

import org.tesys.core.analysis.sonar.KeyValuePOJO;
import org.tesys.core.project.tracking.Issue;
import org.tesys.core.project.tracking.IssuePOJO;
import org.tesys.core.project.tracking.ProjectTracking;
import org.tesys.core.project.tracking.ProjectTrackingRESTClient;

/**
 * Este agregador es el obligatorio ya que es el que coloca el id de la tarea
 * COmo asi tambien el usuario
 *
 */

public class ProjectTrackingAggregator implements Aggregator {

  @Override
  public IssueMetrics agregateMetrics(IssueMetrics issueMetrics) {
    String key = issueMetrics.getIssueId();
    ProjectTracking proj = new ProjectTrackingRESTClient() ;
    Issue i = proj.getIssue( key ) ;
    issueMetrics.setUser( i.getAssignee() );
    KeyValuePOJO horasTrabajadas = new KeyValuePOJO ("progress",i.getProgress().getProgress()) ;
    KeyValuePOJO horasTotal = new KeyValuePOJO ("estimated",i.getProgress().getTotal()) ;
    issueMetrics.addMetric(horasTrabajadas) ;
    issueMetrics.addMetric(horasTotal) ; 
    return issueMetrics;
  }

}
