package org.tesys.controller;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.tesys.core.analysis.Analyzer;
import org.tesys.core.estructures.Developer;
import org.tesys.core.estructures.IValue;
import org.tesys.core.estructures.Issue;
import org.tesys.core.estructures.Metric;
import org.tesys.core.estructures.Multiplicacion;
import org.tesys.core.estructures.SimpleValue;


@Path("/analyzer")
@Singleton
public class AnalyzerController {

  private Analyzer analizer;

  @PostConstruct
  public void init() {
    analizer = Analyzer.getInstance();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String performAnalysis() {
    return analizer.performAnalysis();
  }
  
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Metric test(IValue is) {
    
    
    Issue issue = new Issue("Ruby-7");
    issue.setIssueType("1");
    issue.setUser("arian");
    issue.addMetric("lines", 50);
    issue.addMetric("bugs", 10);
    
   // System.out.println(  is.getKey() );
    /*System.out.println(  is.getDescripcion() );
    System.out.println(  is.getNombre() );
    System.out.println(  is.getProcedencia() );*/
    System.out.println(  is.evaluate(issue)) ;
    

    IValue mul = new Multiplicacion( new SimpleValue("lines"), new SimpleValue("bugs"));
    
    Metric lineasPorBugs = new Metric("linbug", "lineas por bugs", 
        "lineas de codigo por cantidad de bugs", "User Made", mul);

    
    return lineasPorBugs;
  }
  

}
