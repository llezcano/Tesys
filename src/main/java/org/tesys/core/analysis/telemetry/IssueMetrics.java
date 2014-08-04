package org.tesys.core.analysis.telemetry;

import java.util.List;

import org.tesys.core.analysis.sonar.KeyValuePOJO;


public class IssueMetrics {

  private String issueId;
  private String user;
  
  List< KeyValuePOJO > metrics;
  
  public IssueMetrics() {
    //for jackson
  }
  
  public IssueMetrics( String issueId ) {
    super();
    this.issueId = issueId;
  }

  public String getIssueId() {
    return issueId;
  }

  public void setIssueId(String issueId) {
    this.issueId = issueId;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public List<KeyValuePOJO> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<KeyValuePOJO> metrics) {
    this.metrics = metrics;
  }
  
  public void addMetric(KeyValuePOJO k) {
    this.metrics.add(k);
  }

}
