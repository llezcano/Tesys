package org.tesys.core.analysis.sonar;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

import org.tesys.util.MD5;
//TODO cambiar nombre por -> AnalisysPOJO

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AnalisisPOJO implements Comparable<AnalisisPOJO>  {
  

  public long date;
  public String scm_user;
  public String project_tracking_task;
  public String revision;
  public String repository;
  public boolean scaned = false;
  public List<KeyValuePOJO> individualResults = new ArrayList<KeyValuePOJO>();
  
  
  
  @Override
  public boolean equals(Object obj) {
    AnalisisPOJO otro = (AnalisisPOJO) obj;
    if( this.getID().equals(otro.getID()) ) {
      return true;
    }
    return false;
    
  }
  
  public AnalisisPOJO() {}
  
  public void add(KeyValuePOJO kvp) {
    individualResults.add(kvp);
  }

  @Override
  public int compareTo(AnalisisPOJO analisis) {
    if (date < analisis.getDate()) {
      return 1;
    }
    return -1;
  }

  public boolean isScaned() {
    return scaned;
  }

  public void setScaned(boolean scaned) {
    this.scaned = scaned;
  }

  public List<KeyValuePOJO> getIndividualResults() {
    return individualResults;
  }

  public void setIndividualResults(List<KeyValuePOJO> individualResults) {
    this.individualResults = individualResults;
  }

  public String getID() {
    return MD5.generateId(String.valueOf(date));
  }
  
  public long getDate() {
    return date;
  }


  public void setDate(long date) {
    this.date = date;
  }


  public String getScm_user() {
    return scm_user;
  }


  public void setScm_user(String scm_user) {
    this.scm_user = scm_user;
  }


  public String getProject_tracking_task() {
    return project_tracking_task;
  }


  public void setProject_tracking_task(String project_tracking_task) {
    this.project_tracking_task = project_tracking_task;
  }


  public String getRevision() {
    return revision;
  }


  public void setRevision(String revision) {
    this.revision = revision;
  }


  public String getRepository() {
    return repository;
  }


  public void setRepository(String repository) {
    this.repository = repository;
  }
  
  
}

