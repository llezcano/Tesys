package org.tesys.core.analysis.telemetry;

import java.util.List;

import org.tesys.core.estructures.Issue;
import org.tesys.core.estructures.Metric;

public abstract class AggregatorDecorator implements Aggregator {
  protected Aggregator aggregator;

  public AggregatorDecorator (Aggregator aggregator) {
      this.aggregator = aggregator;
  }

  @Override
  public Issue agregateMetrics(Issue i) {
    
    return aggregator.agregateMetrics(i);
  }

  @Override
  public List<Metric> getMetricsID() {
    return aggregator.getMetricsID();
  }
  
  
  
  
}
