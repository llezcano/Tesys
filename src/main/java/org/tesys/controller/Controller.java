package org.tesys.controller;

import java.util.LinkedList;
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
import org.tesys.core.project.tracking.IssueTypePOJO;
import org.tesys.core.recommendations.Recommendation;
import org.tesys.core.recommendations.Recommender;


@Path("/controller")
@Singleton
public class Controller {

  private static final String FAIL_CODE = "0";
  private static final String OK_CODE = "1";
  
  //Componente encargado de las tareas relacionas con el SCM
  private SCMManager scmManager;
  //Componenete encargado con las tareas de recolectar e interpretar datos
  private Analyzer analizer;
  //Componenete que realiza recomandaciones en base a los datos obtenidos
  private Recommender recommender;

  @PostConstruct
  public void init() {
    scmManager = SCMManager.getInstance();
    analizer = Analyzer.getInstance();
    recommender = new Recommender();
  }


  /**
   * Metodo que dada la informacion sobre un commit devuelve
   * Si el sistema Tesys lo puede computar o no
   */
  
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
  
  /**
   * ALmacena en el sistema la informacion relacionada con un commit
   * que debe ser previamente verificado por el metodo anterior
   * (Estos dos se encuantran separados ya que por lo general no se
   * dispone de toda la informacion necesaria para almacenar un commit
   * antes de hacerlo)
   */

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
  
  /**
   * Cuando se llama se recolectan todos los datos esparcidos a lo largo del sistema
   * Este metodo no realiza ningun tipo de calculo solo junta toda la informacion existente
   * en estructuras convenientes que luego se utilizaran para hacer recomandaciones
   */
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/analyzer")
  public String performAnalysis() {
    return analizer.performAnalysis();
  }
  
  /**
   * Devuelve todos los developers que existen en el project tracking, una ves que se
   * haya ejecutado un analisis, esta infoamcion es util para obserbar los issues
   * que tiene cada developer y para poder conocer el conjunto por el cual se recomienda
   */
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/developers")
  public List<Developer> getDevelopers() {
    ElasticsearchDao<Developer> dao = 
        new ElasticsearchDao<Developer>(Developer.class, 
            ElasticsearchDao.DEFAULT_RESOURCE_DEVELOPERS);
    return dao.readAll();
  }
  
  /**
   * Este metodo devuelve los tipos de metricas que el programa maneja, esto es
   * Las metrics que definene los programas (conocidas como simples, que son por
   * ejemplo lineas de codigo) y las metricas definidas por el usuario ( conocidas
   * como compuestas que son convinaciones de simples)
   */
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/metrics")
  public List<String> getMetrics() {
    ElasticsearchDao<Metric> dao = 
        new ElasticsearchDao<Metric>(Metric.class, 
            ElasticsearchDao.DEFAULT_RESOURCE_METRIC);
    List<Metric> metrics = dao.readAll();
    List<String> metricsJson = new LinkedList<String>();
    for (Metric m : metrics) {
      metricsJson.add( m.toString() );
    }
    return metricsJson;
    
  }
  
  
  /**
   * Devuleve los tipos de issues que existen en el project tracking, de esta
   * forma se puede saber sobre el conjuto de restricciones que se puede ejecutar
   * una recomendacion
   */
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/issuestype")
  public List<IssueTypePOJO> getIssuesTypes() {
    ElasticsearchDao<IssueTypePOJO> dao = 
        new ElasticsearchDao<IssueTypePOJO>(IssueTypePOJO.class, 
            ElasticsearchDao.DEFAULT_RESOURCE_ISSUE_TYPE);
    return dao.readAll();
  }
  
  /**
   * Define una nueva metrica compuesta, que el usaurio desee
   * por ejemplo lines por hora como productividad
   * o productividad dividido bugs como seguridad
   */
  
  //TODO implementar todos estos
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/newmetric")
  public String addMetric( String metric ) {
    //TODO return db.addMetric(m);
    return null;
  }
  
  /**
   * Elimina una metrica definida por el usuario, tambien pude eliminar una simple
   * pero en el proximo analisis se volvera a crear
   */
  
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/deletemetric")
  public String deleteMetric( String metric ) {
    // TODO return db.deleteMetric(m);
    return null;
  }
  
  /**
   * Dada una metrica particular y un tipo de issue devuelve todos los developers
   * ordenados de mejor a peor, en caso de que no se cuente con informacion 
   * el developer va a estar al final de la lista como no rankeado
   */
  
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/recommendate/{issuetype}")
  public List<Recommendation> recommendate( String metric,
      @PathParam("id") String issueType ) {
    //return recommender.recommendate(m, issueType);
    return null;
  }
  
  
  /**
   * Se utiliza para proporcionar feedback sobre una recomandacion realizada con
   * anterioridad
   */
  
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
