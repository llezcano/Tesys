package org.tesys.core.analysis.sonar.metricsdatatypes;


public class FLOAT implements Metrics {

  Double actual, anterior;

  public FLOAT(String actual, String anterior) {
    this.actual = Double.valueOf(actual);
    if ("null".equals(anterior)) {
      this.anterior = 0.0;
    } else {
      this.anterior = Double.valueOf(anterior);
    }
  }

  public String getDifferenceBetweenAnalysis() {
    return String.valueOf(actual - anterior);
  }

  public String getNewAnalysisPerTask() {
    return String.valueOf(actual + anterior);
  }

}
