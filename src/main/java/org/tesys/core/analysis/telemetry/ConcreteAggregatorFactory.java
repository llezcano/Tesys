package org.tesys.core.analysis.telemetry;

public class ConcreteAggregatorFactory implements AggregatorFactory {

  @Override
  public Aggregator getAggregator() {
    
    return new SonarAnalisisAggregator( new ProjectTrackingAggregator() );
  }

}
