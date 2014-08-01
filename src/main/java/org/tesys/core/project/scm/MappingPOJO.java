package org.tesys.core.project.scm;

import javax.xml.bind.annotation.XmlRootElement;

import org.tesys.util.MD5;

/**
 * Esta clase es para el mapeo SCM - Jira
 * 
 */
@XmlRootElement
public class MappingPOJO {

  private String projectTrackingUser;
  private String scmUser;
  private String repository;


  public MappingPOJO(String project_tracking_user, String scm_user, String repository) {
    this.projectTrackingUser = project_tracking_user;
    this.scmUser = scm_user;
    this.repository = repository;
  }
  
  public String getID() {
    return MD5.generateId(projectTrackingUser + scmUser + repository);
  }

  public MappingPOJO() {}
  
  @Override
  public String toString() {
    return "MappingPOJO [project_tracking_user=" + projectTrackingUser + ", scm_user="
        + scmUser + ", repository=" + repository + "]";
  }

  public String getProjectTrackingUser() {
    return projectTrackingUser;
  }

  public void setProjectTrackingUser(String project_tracking_user) {
    this.projectTrackingUser = project_tracking_user;
  }

  public String getScmUser() {
    return scmUser;
  }

  public void setScmUser(String scm_user) {
    this.scmUser = scm_user;
  }

  public String getRepository() {
    return repository;
  }

  public void setRepository(String repository) {
    this.repository = repository;
  }

}
