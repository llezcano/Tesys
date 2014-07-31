package org.tesys.core.project.scm;

import javax.xml.bind.annotation.XmlRootElement;

import org.tesys.util.MD5;

@XmlRootElement
public class RevisionPOJO implements Comparable<RevisionPOJO> {

  public long date;
  public String scm_user;
  public String project_tracking_task;
  public String revision;
  public String repository;
  public boolean scaned;
  

  public RevisionPOJO(long date, String scm_user, String project_tracking_task, String revision,
      String repository) {
    this.date = date;
    this.scm_user = scm_user;
    this.project_tracking_task = project_tracking_task;
    this.revision = revision;
    this.repository = repository;
    this.scaned = false;
  }

  
  public String getID() {
    return MD5.generateId( String.valueOf(date) );
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


  public boolean isScaned() {
    return scaned;
  }


  public void setScaned(boolean scaned) {
    this.scaned = scaned;
  }

  
  public int compareTo(RevisionPOJO rev) {
    if ( rev.getDate() < this.date ) {
      return 1;
    }
    return -1;
  }

}
