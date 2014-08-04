package org.tesys.core.analysis.telemetry;

import java.util.List;

import org.tesys.core.analysis.sonar.AnalisisPOJO;
import org.tesys.core.db.Database;

public class SonarAnalisisAggregator extends AggregatorDecorator {

  Database db;
  
  public SonarAnalisisAggregator(Aggregator aggregator) {
    super(aggregator);
    db = new Database();
  }
  
  @Override
  public List<MetricPOJO> getMetricsID() {
    List<MetricPOJO> demas = aggregator.getMetricsID();
    //demas + mias TODO
    return null;
  }
  
  @Override
  public IssueMetrics agregateMetrics(IssueMetrics issueMetrics) {
    issueMetrics = super.agregateMetrics(issueMetrics);
    String key = issueMetrics.getIssueId();
    AnalisisPOJO analisis = db.getAnalisis( key );
    issueMetrics.getMetrics().addAll(analisis.getResults());
    //TODO hay que ver si asi anda y sino hacerlo piiola
    return issueMetrics;
  }

}
