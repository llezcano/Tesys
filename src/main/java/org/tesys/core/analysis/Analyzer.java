package org.tesys.core.analysis;

import org.tesys.core.analysis.sonar.SonarAnalizer;
import org.tesys.core.analysis.telemetry.ProcessData;
import org.tesys.core.project.tracking.StoreProjectTrackingData;

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

    // TODO thread 1
    StoreProjectTrackingData st = new StoreProjectTrackingData(); // TODO

    // TODO thread 2
    SonarAnalizer sn = SonarAnalizer.getInstance();
    sn.storeMetrics();
    sn.executeSonarAnalysis();

    // Aca se juntan los Thread

    ProcessData pd = ProcessData.getInstance();
    pd.executeProcessor();

    return "Analisis terminado";
  }

}
