package org.tesys.core.analysis.telemetry;

public class SonarAnalisisAggregator extends AggregatorDecorator {

  public SonarAnalisisAggregator(Aggregator aggregator) {
    super(aggregator);
  }
  
  @Override
  public IssueMetrics agregateMetrics(IssueMetrics issueMetrics) {
    issueMetrics = super.agregateMetrics(issueMetrics);
    String key = issueMetrics.getIssueId();
    //TODO add sonar data to issueMetrics
    
    return issueMetrics;
  }

}
