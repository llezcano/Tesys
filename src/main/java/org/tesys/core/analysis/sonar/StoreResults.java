package org.tesys.core.analysis.sonar;

import org.tesys.core.db.DatabaseFacade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;


public class StoreResults {

  private DatabaseFacade db;

  private static StoreResults instance = null;

  private StoreResults() {
    db = new DatabaseFacade();
  }

  public static StoreResults getInstance() {
    if (instance == null) {
      instance = new StoreResults();
    }
    return instance;
  }


  public void storeMetrics(List<MetricPOJO> metrics) {

    for (MetricPOJO metric : metrics) {
      db.storeMetric( metric );
    }
  }

  public void storeAnalysis(List<AnalisisPOJO> resultados) {

    for (AnalisisPOJO analisis : resultados) {
      db.storeAnalisis( analisis );

    }


  }


}
