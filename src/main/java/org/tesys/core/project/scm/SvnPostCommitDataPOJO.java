package org.tesys.core.project.scm;

public class SvnPostCommitDataPOJO {
  private String author;
  private String message;
  private String repository;
  private String revision;

  public String getRevision() {
    return revision;
  }

  public void setRevision(String revision) {
    this.revision = revision;
  }

  @Override
  public String toString() {
    return "SvnPostCommitDataPOJO [author=" + author + ", message=" + message + ", repository="
        + repository + ", revision=" + revision + "]";
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
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
