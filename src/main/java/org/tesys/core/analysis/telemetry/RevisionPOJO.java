package org.tesys.core.analysis.telemetry;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class RevisionPOJO implements Comparable<RevisionPOJO> {

  private Date date;
  private String scm_user;
  private String project_tracking_task;
  private String revision;
  private String repository;

  public String getRepository() {
    return repository;
  }

  public void setRepository(String repository) {
    this.repository = repository;
  }

  public RevisionPOJO() {}

  public Date getDate() {
    return date;
  }

  public String getUser() {
    return scm_user;
  }

  public String getTask() {
    return project_tracking_task;
  }

  public String getRev() {
    return revision;
  }

  public void setRev(String s) {
    revision = s;
  }

  public void setUser(String s) {
    scm_user = s;
  }

  public void setTask(String s) {
    project_tracking_task = s;
  }

  public void setDate(Date s) {
    date = s;
  }

  public int compareTo(RevisionPOJO rev) {
    if (rev.getDate().before(this.date)) {
      return 1;
    }
    return -1;
  }

}
