package org.tesys.core.project.scm;


public class SvnDataPOJO {

  private String user;
  private String message;
  private String repository;

  @Override
  public String toString() {
    return "SvnData [user=" + user + ", message=" + message + ", repository=" + repository + "]";
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getRepository() {
    return repository;
  }

  public void setRepository(String repository) {
    this.repository = repository;
  }

}
