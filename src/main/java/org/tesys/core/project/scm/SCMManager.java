package org.tesys.core.project.scm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.tesys.core.db.DatabaseFacade;
import org.tesys.core.project.tracking.ProjectTracking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Path("/scm")
@Singleton
public class SCMManager {
  
  private Pattern issuePattern;
  private Pattern userPattern;
  private Matcher matcher;
  private DatabaseFacade db;
  
  @PostConstruct
  public void init() {
    issuePattern = Pattern.compile("#issue='(.*?)'"); 
    userPattern = Pattern.compile("#user='(.*?)'");
    db = new DatabaseFacade();
  }

  
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String isCommitAllowed(SvnPreCommitDataPOJO svnData) {

    //-----------Se obtine el issue del mesaje---------------
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
    //if( !pt.existIssue( issue ) ) {
    //  return "El issue: " + issue + "No coincide con un issue existente";
    //}
    //-----------Se obtuvo el issue del mesaje---------------
    
    
    //-----------Se mapea el usuario con el project tracking---------------
    //TODO esto tranquilamente puede ser un thread aparte
    //TODO este JSON se tiene que generar con JsonNode
    String query = "{ "
                      + "\"query\": { "
                      + "\"bool\": { "
                        + "\"must\": ["
                          + " { \"match\": { \"svn_user\": \""+ svnData.getAuthor() +"\" }},"
                          + " { \"match\": { \"repository\": \""+ svnData.getRepository() +"\" }}"
                        + "]"
                      + "}"
                    + "}"
                  + "}";

    String result = db.SEARCH("svn", "users", query );               
    if( result.equals("CAMBIAME") ) { //TODO
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
            //if( !pt.existUser( user ) ) {
            //  return "El user: " + user + "No coincide con un user existente";
            //}
            
            //TODO generar id
            //TODO hacer el json en un JsonNode
            String data = "{\"jira_user\":\""+user+"\",\"svn_user\":\""+svnData.getAuthor()+"\",\"repository\":\""+svnData.getRepository()+"\"}";
            db.PUT("svn", "users", "1", data);
      } else {
        return "Como es la primer vez que este usuario hace un commit debe indicar su nombre en el"
            + "poject tracking que se este usando de la forma #user='bar'";
      }
    }
    //-----------Se mapeo el usuario con el project tracking---------------

    return "1";
  }

  
  
  /**
   * Este metodo es el encargado de guardar toda la informacion importante de cada commit
   * Debe ser llamado principalmente con un hook post commit, aunque puede ser llamado en
   * cualquier momento
   * 
   * La informacion que hay que suministrale es: revision, mensaje, fecha, repo, usuario
   * Del mesaje se extrae la tarea del project tracking para almacenar, no se almacena
   * El mensaje completo.
   * 
   * La fecha debe estar en un formato predeterminado que es el siguinte:
   * yyyy-MM-dd HH:mm:ss
   * 
   * @param svnData
   * @return
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String storeCommit(SvnPostCommitDataPOJO svnData) {
    String issue;
    
    //TODO, ver como hacer para que si falla un parametro el hook avise, y validar
    
    
    matcher = issuePattern.matcher( svnData.getMessage() );
    if (matcher.find()) {
        issue = matcher.group(1);
    }
    
    //TODO generar el id (tener en cuanta revision y repositorio)
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode data = objectMapper.valueToTree(svnData);
    db.PUT("svn", "revisions", "1", data.toString());

    return "1";
  }
  
  
  public boolean doCheckout( String pathDestination, String revision, String url) {
    
    return false;
  }


}
