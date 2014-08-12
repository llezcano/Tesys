package org.tesys.core.analysis;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

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



  public Response performAnalysis() {

    ProcessData pd = ProcessData.getInstance();
    pd.executeProcessor();

    ResponseBuilder response = Response.ok("{\"analysis\":\"finished\"}");
    return response.build();
  }

}
