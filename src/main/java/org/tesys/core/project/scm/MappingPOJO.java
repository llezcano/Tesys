package org.tesys.core.project.scm;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Esta clase es para el mapeo SCM - Jira

 * 
 */

@XmlRootElement
public class MappingPOJO {

  private String project_tracking_user;
  private String scm_user;
  private String repository;


  public MappingPOJO(String project_tracking_user, String scm_user, String repository) {
    this.project_tracking_user = project_tracking_user;
    this.scm_user = scm_user;
    this.repository = repository;
  }


  @Override
  public String toString() {
    return "MappingPOJO [project_tracking_user=" + project_tracking_user + ", scm_user="
        + scm_user + ", repository=" + repository + "]";
  }

  public String getProjectTrackingUser() {
    return project_tracking_user;
  }

  public void setProjectTrackingUser(String project_tracking_user) {
    this.project_tracking_user = project_tracking_user;
  }

  public String getScmUser() {
    return scm_user;
  }

  public void setScmUser(String scm_user) {
    this.scm_user = scm_user;
  }

  public String getRepository() {
    return repository;
  }

  public void setRepository(String repository) {
    this.repository = repository;
  }

}
