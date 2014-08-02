package org.tesys.core.project.scm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.tesys.util.MD5;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RevisionPOJO implements Comparable<RevisionPOJO> {

  protected long date;
  
  @JsonProperty("scm_user")
  protected String scmUser;
  
  @JsonProperty("project_tracking_task")
  protected String projectTrackingTask;
  
  protected String revision;
  protected String repository;
  protected boolean scaned;
  
  public RevisionPOJO() {}
  
  public RevisionPOJO(long date, String scmUser, String projectTrackingTask, String revision,
      String repository) {
    this.date = date;
    this.scmUser = scmUser;
    this.projectTrackingTask = projectTrackingTask;
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

  public String getScmUser() {
    return scmUser;
  }


  public void setScmUser(String scmUser) {
    this.scmUser = scmUser;
  }

  public String getProjectTrackingTask() {
    return projectTrackingTask;
  }


  public void setprojectTrackingTask(String projectTrackingTask) {
    this.projectTrackingTask = projectTrackingTask;
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
