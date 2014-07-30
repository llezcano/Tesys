package org.tesys.core.analysis.sonar.metricsdatatypes;

import com.fasterxml.jackson.databind.JsonNode;



public class PERCENT implements Metrics {

  Double actual, anterior;

  public PERCENT(JsonNode actual, JsonNode anterior) {
    this.actual = actual.asDouble();
    if (anterior == null) {
      this.anterior = 0.0;
    } else {
      this.anterior = anterior.asDouble();
    }
  }

  public String getDifferenceBetweenAnalysis() {
    return String.valueOf(actual - anterior);
  }

  public String getNewAnalysisPerTask() {
    return String.valueOf(actual + anterior);
  }

}
