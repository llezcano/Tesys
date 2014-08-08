package org.tesys.core.analysis.telemetry;


import java.util.LinkedList;
import java.util.List;

import org.tesys.core.estructures.Issue;
import org.tesys.core.estructures.Metric;
import org.tesys.core.estructures.SimpleValue;
import org.tesys.core.project.tracking.IssueInterface;
import org.tesys.core.project.tracking.ProjectTracking;
import org.tesys.core.project.tracking.ProjectTrackingRESTClient;


/**
 * Este agregador es el obligatorio ya que es el que coloca el id de la tarea
 * COmo asi tambien el usuario
 *
 */

public class ProjectTrackingAggregator implements Aggregator {

  @Override
  public Issue agregateMetrics(Issue issueMetrics) {
    String key = issueMetrics.getIssueId();
    ProjectTracking proj = new ProjectTrackingRESTClient() ;
    IssueInterface i = proj.getIssue( key ) ;
    issueMetrics.setUser( i.getAssignee() );
    issueMetrics.setIssueType( i.getIssuetype() );
     
    issueMetrics.addMetric("progress", Double.valueOf( i.getProgress().getProgress()));
    issueMetrics.addMetric("estimated", Double.valueOf( i.getProgress().getTotal() ));
    
    return issueMetrics;
  }

  @Override
  public List<Metric> getMetricsID() {
    ProjectTracking proj = new ProjectTrackingRESTClient() ;
    List<Metric> metrics = new LinkedList<Metric>();
    for (Metric m : proj.getMetrics()) {
      m.setValue(new SimpleValue( m.getKey() ));
      metrics.add(m);
    }
    
    return metrics;
  }

}
