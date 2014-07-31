package org.tesys.connectors.tracking.jira;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.PathParam;

import org.tesys.core.project.tracking.Issue;
import org.tesys.core.project.tracking.User;
import org.tesys.util.Strings;


/**
 * Web Service REST ofrecido por el conector de Jira. Tiene como objetivo daptar la interface de
 * Jira a un Project Tracking.
 * 
 * @author rulo
 * 
 */

// TODO definir con un Schema REST la definicion con la cual debe cumplir dicho Servicio.

@Path("/connectors/jira")
@Singleton
public class JiraConnector implements JiraAdaptor {
  private static String ISSUE_SCHEMA_PATH = "WEB-INF/jira-connector/issueSchema";
  private static String USER_SCHEMA_PATH = "WEB-INF/jira-connector/userSchema";

  private static String JIRA_CONNECTOR_PROPERTIES_FILE =
      "WEB-INF/jira-connector/jira-connector.properties";

  private static String PROP_URL = "host";
  private static String PROP_USER = "username";
  private static String PROP_PASS = "password";

  private JiraAdaptation jira;

  private String issueSchema;
  private String userSchema;

  private String URL;
  private String user;
  private String pass;

  @Context
  ServletContext servletContext;

  /**
   * Load URL, user and pass from properties file
   * 
   * @throws IOException
   */
  public void loadProperties() throws IOException {
    Properties prop = new Properties();
    prop.load(servletContext.getResourceAsStream(JIRA_CONNECTOR_PROPERTIES_FILE));
    URL = prop.getProperty(PROP_URL);
    user = prop.getProperty(PROP_USER);
    pass = prop.getProperty(PROP_PASS);
    issueSchema =
        Strings.convertStreamToString(servletContext.getResourceAsStream(ISSUE_SCHEMA_PATH));
    userSchema =
        Strings.convertStreamToString(servletContext.getResourceAsStream(USER_SCHEMA_PATH));
  }

  @PostConstruct
  public void Init() {

    try {
      loadProperties();
    } catch (IOException e1) {
      System.err.println("Jira Connector: Properties file not found");
      System.exit(0);
    }

    JiraRESTClient client = new JiraRESTClient(URL, user, pass);

    try {
      jira = new JiraAdaptation(client, userSchema, issueSchema);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(0);
    }

  }


  @GET
  @Path("/issues/{issue}")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Issue getIssue(@PathParam("issue") String key) {
    try {
      return jira.getIssue(key);
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
    return null;
  }


  @GET
  @Path("/users/{user}")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public User getUser(@PathParam("user") String name) {
    try {
      return jira.getUser(name);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;

  }


  @GET
  @Path("/users")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public User[] getUsers() {
    try {
      return jira.getAllUsers();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }


  @GET
  @Path("/issues")
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Issue[] getIssues() {
    try {
      return jira.getAllIssues();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }


  // TODO analizar si realmente se necesita esta informacion en el modelo

  // TODO http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/rest/api/2/priority

  // TODO http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/rest/api/2/project

  // TODO http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/rest/api/2/status

  // TODO http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/rest/api/2/issuetype
}
