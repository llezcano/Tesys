package org.tesys.core.analysis.telemetry;

import java.util.List;

import org.tesys.core.analysis.sonar.AnalisisPOJO;
import org.tesys.core.analysis.sonar.KeyValuePOJO;
import org.tesys.core.analysis.sonar.SonarAnalizer;
import org.tesys.core.analysis.sonar.SonarMetricPOJO;
import org.tesys.core.db.Database;
import org.tesys.core.estructures.Issue;
import org.tesys.core.estructures.Metric;
import org.tesys.core.estructures.SimpleValue;

public class SonarAnalisisAggregator extends AggregatorDecorator {

  Database db;
  
  public SonarAnalisisAggregator(Aggregator aggregator) {
    super(aggregator);
    db = new Database();
  }
  
  @Override
  public List<Metric> getMetricsID() {
    //Se juntan la de los demas programas
    List<Metric> metrics = super.aggregator.getMetricsID();
    //se agregan las de este
    SonarAnalizer sa = SonarAnalizer.getInstance();
    for (SonarMetricPOJO metric : sa.getMetrics()) {
      metrics.add( new Metric( metric.getKey(), metric.getName(), 
          metric.getDescription(), "SonarQube", new SimpleValue(metric.getKey())));
    }
    
    return metrics;
  }
  
  @Override
  public Issue agregateMetrics(Issue issueMetrics) {
    issueMetrics = super.agregateMetrics(issueMetrics);
    String key = issueMetrics.getIssueId();
    AnalisisPOJO analisis = db.getAnalisis( key );
    
    for (KeyValuePOJO value : analisis.getResults()) {
      
      issueMetrics.addMetric( value.getKey(), (Double)value.getValue() );
    }

    return issueMetrics;
  }

}
