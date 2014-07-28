package org.tesys.connectors.tracking.jira;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.DefaultValue; 
import javax.ws.rs.QueryParam;

import org.tesys.core.project.tracking.Issue;
import org.tesys.core.project.tracking.IssuePOJO;
import org.tesys.core.project.tracking.User;
import org.tesys.core.project.tracking.UserPOJO;

@Path("/project")
public class JiraConnector {
	
    @GET
    @Path("/issues")
    @Produces(MediaType.APPLICATION_JSON)
    public Issue[] getIssues() {
    	//TODO
    	Issue [] allIssues = new Issue[1] ;
    	IssuePOJO issue = new IssuePOJO() ;
    	issue.setKey(1) ;
    	allIssues[0] = issue ;
    	
    	return allIssues;
    			
    }
    

    @GET
    @Path("/exist-issue")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean existIssue( @QueryParam("key") Integer key ) {
  
    	boolean result = false ; //result = exist(Issue)
    	return result ;
    		
    }
    
    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public User[] getUsers() {
    	//TODO
    	User [] allUsers = new User[1] ;
    	UserPOJO user = new UserPOJO() ;
    	user.setName("pojo");
    	allUsers[0] = user ;
    	
    	return allUsers;
    			
    }
     

    @GET
    @Path("/exist-user")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean existUser( @QueryParam("key") String key ) {
  
    	boolean result = true ; //result = exist(Issue)
    	return result ;
    		
    }
    
    
}

