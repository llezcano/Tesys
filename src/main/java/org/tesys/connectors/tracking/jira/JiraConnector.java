package org.tesys.connectors.tracking.jira;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;

import org.tesys.core.project.tracking.Issue;
import org.tesys.core.project.tracking.User;
import org.tesys.util.Strings;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Web Service REST ofrecido por el conector de Jira. 
 * Tiene como objetivo daptar la interface de Jira a un Project Tracking.
 * 
 * @author rulo
 *
 */

//TODO definir con un Schema REST la definicion con la cual debe cumplir dicho Servicio.

@Path("/project")
@Singleton
public class JiraConnector implements JiraAdaptor {
	private static String ISSUE_SCHEMA_PATH = "WEB-INF/jira-connector/issueSchema" ;
	private static String USER_SCHEMA_PATH = "WEB-INF/jira-connector/userSchema";

	private JiraAdaptation jira ;
	
	@Context ServletContext servletContext;
	
	@PostConstruct
	public void Init() {
		JiraRESTClient client = new JiraRESTClient("http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/", "grodriguez", "654321") ;

		String issueSchema = Strings.convertStreamToString(servletContext.getResourceAsStream(ISSUE_SCHEMA_PATH)) ;
		String userSchema =  Strings.convertStreamToString(servletContext.getResourceAsStream(USER_SCHEMA_PATH)) ;
		
		try {
			jira = new JiraAdaptation(client, userSchema, issueSchema) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		
	}
	   

    @GET
    @Path("/issues/{issue}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Issue getIssue( @PathParam("issue") String key ) {
    	try {
        	return jira.getIssue(key) ;    		
    	} catch (Exception e) {
    		System.err.println( e.getMessage() );
    	}
    	return null ;
    }
    

    @GET
    @Path("/users/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public User getUser( @PathParam("user") String name ) {
    	try {
			return jira.getUser(name) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			return jira.getAllIssues() ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
    }
     
    
    // TODO analizar si realmente se necesita esta informacion en el modelo  
    
    //TODO http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/rest/api/2/priority
    
    //TODO http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/rest/api/2/project 
    
    //TODO http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/rest/api/2/status 
    
    //TODO http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/rest/api/2/issuetype
}

