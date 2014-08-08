package org.tesys.core.estructures;

import java.util.HashMap;
import java.util.Map;


/**
 * Esta clase representa un Issue final.
 * Es decir, tiene un par de atributos para poder identificarlo
 * y un mapa con cad valor que los programas suministraron sobre ese issue
 * el paquete telemetry es el encargado de generar instancias de esta clase
 * en base a todos los datos que hay de cada programa
 */
public class Issue {

  private String issueId;
  private String user;
  private String issueType;
  
  Map< String, Double > measures;
  
  public Issue() {
    //for jackson
  }
  
  public Issue( String issueId ) {
    super();
    this.issueId = issueId;
    measures = new HashMap<String, Double>();
  }

  public String getIssueId() {
    return issueId;
  }

  public void setIssueId(String issueId) {
    this.issueId = issueId;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getIssueType() {
    return issueType;
  }

  public void setIssueType(String issueType) {
    this.issueType = issueType;
  }

  public Map<String, Double> getMetrics() {
    return measures;
  }

  public void setMetrics(Map<String, Double> metrics) {
    this.measures = metrics;
  }

  public void addMetric(String k, Double v) {
    this.measures.put(k,v);
  }
  
  public void addMetric(String k, Integer v) {
    this.addMetric(k, v.doubleValue());
  }
  

}
