package org.tesys.controller;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.tesys.core.analysis.Analyzer;


@Path("/analyzer")
@Singleton
public class AnalyzerController {

  private Analyzer analizer;

  @PostConstruct
  public void init() {
    analizer = Analyzer.getInstance();
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String performAnalysis() {

    return analizer.performAnalysis();
  }

}
