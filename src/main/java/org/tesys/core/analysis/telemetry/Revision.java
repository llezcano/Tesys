package org.tesys.core.analysis.telemetry;

import java.util.Date;

public class Revision implements Comparable<Revision> {

  /* public para que puedan ser mapeadas */
  public Date date;
  public String svn_user;
  public String jira_task;
  public String revision;

  @Override
  public String toString() {
    String ret = getRev() + " " + getUser() + " " + getDate().toString() + " " + getTask();
    return ret;
  }

  public Revision() {}

  public Date getDate() {
    return date;
  }

  public String getUser() {
    return svn_user;
  }

  public String getTask() {
    return jira_task;
  }

  public String getRev() {
    return revision;
  }

  public void setRev(String s) {
    revision = s;
  }

  public void setUser(String s) {
    svn_user = s;
  }

  public void setTask(String s) {
    jira_task = s;
  }

  public void setDate(Date s) {
    date = s;
  }

  public int compareTo(Revision rev) {
    if (rev.getDate().before(this.date)) {
      return 1;
    }
    return -1;
  }

}
