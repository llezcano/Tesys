package org.tesys.core.analysis.sonar.metricsdatatypes;


public class WORK_DUR implements Metrics {

  Integer actual, anterior;

  public WORK_DUR(String actual, String anterior) {
    this.actual = Integer.valueOf(actual);
    if (anterior == null) {
      this.anterior = 0;
    } else {
      this.anterior = Integer.valueOf(anterior);
    }
  }

  public String getDifferenceBetweenAnalysis() {
    return String.valueOf(actual - anterior);
  }

  public String getNewAnalysisPerTask() {
    return String.valueOf(actual + anterior);
  }


}
