package org.tesys.core.analysis;

import org.tesys.core.analysis.sonar.SonarAnalizer;
import org.tesys.core.analysis.telemetry.ProcessData;

public class Analyzer {

  private static Analyzer instance = null;

  private Analyzer() {

  }

  public static Analyzer getInstance() {
    if (instance == null) {
      instance = new Analyzer();
    }
    return instance;
  }



  public String performAnalysis() {
    
    SonarAnalizer sn = SonarAnalizer.getInstance();
    sn.storeMetrics();
    sn.executeSonarAnalysis();

    ProcessData pd = ProcessData.getInstance();
    pd.executeProcessor();

    return "Analisis terminado";
  }

}
