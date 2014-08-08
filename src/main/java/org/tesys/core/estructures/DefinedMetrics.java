package org.tesys.core.estructures;

import java.util.List;

public class DefinedMetrics {
  private List<Metric> metrics;
  
  public DefinedMetrics(List<Metric> metrics) {
    super();
    this.metrics = metrics;
  }

  public List<Metric> getMetrics() {
    return metrics;
  }

  public void setMetrics(List<Metric> metrics) {
    this.metrics = metrics;
  }

}
