package org.tesys.core.project.tracking;

import java.net.MalformedURLException;

import org.tesys.util.RESTClient;


/**
 * Este ProjectTracking es un cliente REST para el Connector de un Project Manager.
 * 
 * @author rulo
 * 
 */
public class ProjectTrackingRESTClient implements ProjectTracking {

  private RESTClient client;

  //RESEARCH Discovery Resources from Connector
  private static String RESOURCE_ISSUES = "issues/";
  private static String RESOURCE_USERS = "users/";

  public ProjectTrackingRESTClient() {
    try {
	client = new RESTClient(getConnectorLocation());
    } catch (MalformedURLException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
    }
  }


  public String getConnectorLocation() {
    //RESEARCH Discovery Services
    return "http://localhost:8080/core/rest/connectors/jira";
  }

  @Override
  public Issue[] getIssues() {
    IssuePOJO[] issues = client.GET(RESOURCE_ISSUES).readEntity(IssuePOJO[].class);
    return issues;
  }

  @Override
  public User[] getUsers() {
    UserPOJO[] users = client.GET(RESOURCE_USERS).readEntity(UserPOJO[].class);
    return users;
  }


  @Override
  public boolean existUser(String key) {
    UserPOJO u = client.GET(RESOURCE_USERS + key).readEntity(UserPOJO.class);
    return (u != null);
  }

  @Override
  public boolean existIssue(String key) {
    IssuePOJO i = client.GET(RESOURCE_ISSUES + key).readEntity(IssuePOJO.class);
    return (i != null);
  }
  
  @Override
  public Issue getIssue(String key) {
    IssuePOJO i = client.GET(RESOURCE_ISSUES + key).readEntity(IssuePOJO.class);
    return i ;
  }

}

/*
 

  public static void main(String args[]) throws MalformedURLException {

    ProjectTrackingRESTClient project = new ProjectTrackingRESTClient();

    // Query for user exists
    System.out.println("Homero J. Simpson is a developer ? " + project.existUser("homerojsimpson"));
    // Getting all users
    User[] users = project.getUsers();
    System.out.println("Users List: ");
    System.out.println(java.util.Arrays.toString(users));
    System.out.println("TOTAL = " + users.length);

    // Query for issue exists
    System.out.println("ADA-1 is a issue ? " + project.existUser("ADA-1"));
    // Getting all issues
    // Issue[] issues = project.getIssues() ;
    // System.out.println("Issues List: ") ;
    // System.out.println(java.util.Arrays.toString(issues)) ;
    // System.out.println("TOTAL = " + issues.length) ;
  }
 
  
 */
