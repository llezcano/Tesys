package org.tesys.core.estructures;

import java.util.LinkedList;
import java.util.List;

public class Developer {
  private String name;
  private Double penalizador; //deberia ser entre 0 y 1
  private List<Issue> issues = new LinkedList<Issue>();
  
  public Developer() {
    //jackson
  }
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public Double getPenalizador() {
    return penalizador;
  }
  public void setPenalizador(Double penalizador) {
    this.penalizador = penalizador;
  }
  public List<Issue> getIssues() {
    return issues;
  }
  public void setIssues(List<Issue> issues) {
    this.issues = issues;
  }
  
}
