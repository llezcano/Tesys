package org.tesys.core.analysis.sonar;

import org.tesys.core.db.Database;

import java.util.List;



public class StoreResults {

  private Database db;

  private static StoreResults instance = null;

  private StoreResults() {
    db = new Database();
  }

  public static StoreResults getInstance() {
    if (instance == null) {
      instance = new StoreResults();
    }
    return instance;
  }


  public void storeMetrics(List<MetricPOJO> metrics) {

    for (MetricPOJO metric : metrics) {
      db.store( metric.getID(), metric );
    }
  }

  public void storeAnalysis(List<AnalisisPOJO> resultados) {

    for (AnalisisPOJO analisis : resultados) {
      db.store( analisis.getID() ,analisis );
    }


  }


}
