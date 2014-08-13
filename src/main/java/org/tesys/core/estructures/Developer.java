package org.tesys.core.estructures;

import java.util.LinkedList;
import java.util.List;

public class Developer {
  private String name;
  private String DisplayName;
  private List<Issue> issues = new LinkedList<Issue>();
  
  public Developer() {
    //jackson
  }
  
  public String getDisplayName() {
    return DisplayName;
  }

  public void setDisplayName(String displayName) {
    DisplayName = displayName;
  }
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public List<Issue> getIssues() {
    return issues;
  }
  public void setIssues(List<Issue> issues) {
    this.issues = issues;
  }
  
}
