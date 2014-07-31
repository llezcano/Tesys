package org.tesys.connectors.tracking.jira;

import org.tesys.core.project.tracking.Issue;
import org.tesys.core.project.tracking.User;


public interface JiraAdaptor {

  public Issue[] getIssues();

  public User[] getUsers();

  public Issue getIssue(String key);

  public User getUser(String key);

}
