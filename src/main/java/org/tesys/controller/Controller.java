package org.tesys.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.tesys.core.analysis.Analyzer;
import org.tesys.core.db.ElasticsearchDao;
import org.tesys.core.estructures.Developer;
import org.tesys.core.estructures.Metric;
import org.tesys.core.project.scm.InvalidCommitException;
import org.tesys.core.project.scm.SCMManager;
import org.tesys.core.project.scm.ScmPostCommitDataPOJO;
import org.tesys.core.project.scm.ScmPreCommitDataPOJO;
import org.tesys.core.recommendations.Recommendation;
import org.tesys.core.recommendations.Recommender;


@Path("/controller")
@Singleton
public class Controller {

  private static final String FAIL_CODE = "0";
  private static final String OK_CODE = "1";
  
  private SCMManager scmManager;
  private Analyzer analizer;
  private Recommender recommender;

  @PostConstruct
  public void init() {
    scmManager = SCMManager.getInstance();
    analizer = Analyzer.getInstance();
    recommender = new Recommender();
  }


  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/scm")
  public String isCommitAllowed(ScmPreCommitDataPOJO scmData) {

    try {
      if( scmManager.isCommitAllowed(scmData) ) {
        return OK_CODE;
      }
    } catch (InvalidCommitException e) {
      return e.getMessage();
    }
    return FAIL_CODE;
  }

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/scm")
  public String storeCommit(ScmPostCommitDataPOJO scmData) {

    try {
      if (scmManager.storeCommit(scmData) ) {
        return OK_CODE;
      }
    } catch (RuntimeException e) {
      return e.getMessage();
    }
    return FAIL_CODE;
  }
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/analyzer")
  public String performAnalysis() {
    return analizer.performAnalysis();
  }
  
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/developers")
  public List<Developer> getDevelopers() {
    ElasticsearchDao<Developer> dao = 
        new ElasticsearchDao<>(Developer.class, ElasticsearchDao.DEFAULT_RESOURCE_DEVELOPERS);
    return dao.readAll();
  }
  
  //TODO implementar todos estos
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/metrics")
  public List<String> getMetrics() {
    //return db.getMetrics();
    return null;
  }
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/issuestype")
  public List<String> getIssuesTypes() {
    //return db.getIssuesTypes();
    return null;
  }
  
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/newmetric")
  public String addMetric( String metric ) {
    //TODO return db.addMetric(m);
    return null;
  }
  
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/deletemetric")
  public String deleteMetric( String metric ) {
    // TODO return db.deleteMetric(m);
    return null;
  }
  
  
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/recommendate/{issuetype}")
  public List<Recommendation> recommendate( String metric,
      @PathParam("id") String issueType ) {
    //return recommender.recommendate(m, issueType);
    return null;
  }
  
  
  //TODO hacer por tipo de issue??
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/puntrecommendation/{punt}")
  public String puntRecommendation( Developer d, @PathParam("punt") Boolean b  ) {
    //TODO getDeveloper, penalizar y guardar
    return null;
  }
  
  
  

}
