package org.tesys.core.project.scm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.tesys.core.project.tracking.ProjectTracking;


@Path("/scm")
@Singleton
public class SCMManager {
  
  private Pattern issuePattern;
  private Pattern userPattern;
  private Matcher matcher;
  
  @PostConstruct
  public void init() {
    issuePattern = Pattern.compile("#issue='(.*?)'"); 
    userPattern = Pattern.compile("#user='(.*?)'");
  }


  @POST
  @Path("/hooks")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String isCommitAllowed(SvnPreCommitDataPOJO svnData) {

    String issue;
      
    matcher = issuePattern.matcher( svnData.getMessage() );
    if (matcher.find()) {
        issue = matcher.group(1);
        if( issue.contains("#user='") ) {
          return "El mensaje del commit es erroneo, falta una comilla?";
        }
        if( matcher.find() ) {
          return "Solo se puede referenciar un unico issue";
        }
    } else {
      return "Se debe indicar el issue relacionado con el commit de la forma #issue='foo'";
    }
    
    //TODO preguntarle el project.tracking si existe issue en jira
    ProjectTracking pt;
    //TODO
    /*if( !pt.existIssue( issue ) ) {
      return "El issue: " + issue + "No coincide con un issue existente";
    }*/
    
    
    //TODO preguntar a la base de datos si existe el autor mappeado
    //TODO esto tranquilamente puede ser un thread aparte
    //al nombre del jira(o el que sea)
    //svnData.getAuthor()  svnData.getRepository()
    if( true ) {
      matcher = userPattern.matcher( svnData.getMessage() );
      if (matcher.find()) {
          String user = matcher.group(1);
            if( user.contains("#issue='") ) {
              return "El mensaje del commit es erroneo, falta una comilla?";
            }
            if( matcher.find() ) {
              return "Solo se puede referenciar un unico issue";
            }
            //TODO
            /*if( !pt.existUser( user ) ) {
              return "El user: " + user + "No coincide con un user existente";
            }*/
            

            
          //TODO guardar user(jira) repo y author(svn) en base de datos
      } else {
        return "Como es la primer vez que este usuario hace un commit debe indicar su nombre en el"
            + "poject tracking que se este usando de la forma #user='bar'";
      }
    }

    /* TODO
     * Guardar todo el svnData en la base de datos
     */  
    
    
    return "asd";
  }

  @PUT
  @Path("/hooks")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String storeCommit(SvnPostCommitDataPOJO svnData) {
    
    String issue;
    
    matcher = issuePattern.matcher( svnData.getMessage() );
    if (matcher.find()) {
        issue = matcher.group(1);
    }
    
    //new SvnPostCommitDataPOJO ??
    
    //TODO Guardar los datos en la DB
    //el valor de retorno se puede usar para indicar un error pero no se
    //puede 
    return "1";
  }
  
  
  public boolean doCheckout( String pathDestination, String revision, String url) {
    
    return false;
  }


}
