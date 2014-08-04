package org.tesys.core.analysis.telemetry;

public class AggregatorDecorator implements Aggregator {
  protected Aggregator aggregator;

  public AggregatorDecorator (Aggregator aggregator) {
      this.aggregator = aggregator;
  }

  @Override
  public IssueMetrics agregateMetrics(IssueMetrics i) {
    
    return aggregator.agregateMetrics(i);
  }
  
  
  
  
}
