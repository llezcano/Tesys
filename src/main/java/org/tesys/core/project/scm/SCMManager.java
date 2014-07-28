package org.tesys.core.project.scm;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import org.tesys.core.db.DatabaseFacade;
import org.tesys.core.project.tracking.ProjectTracking;
import org.tesys.util.GenericJSONClient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Path("/scm")
@Singleton
public class SCMManager {
  
  private Pattern issuePattern;
  private Pattern userPattern;
  private Matcher matcher;
  private DatabaseFacade db;
  MessageDigest md;
  
  @PostConstruct
  public void init() {
    issuePattern = Pattern.compile("#issue='(.*?)'"); 
    userPattern = Pattern.compile("#user='(.*?)'");
    db = new DatabaseFacade();
    
  }
  
  @POST
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

    //Se crea una consulta en JSON para realizar busqueda
    JsonFactory factory = new JsonFactory();
    ObjectMapper om = new ObjectMapper(factory);
    factory.setCodec(om);
    
    ObjectNode node1 = om.createObjectNode();
    node1.put("svn_user", svnData.getAuthor());
    ObjectNode node11 = om.createObjectNode();
    node11.put("match", node1);
    
    ObjectNode node2 = om.createObjectNode();
    node2.put("repository", svnData.getRepository());
    ObjectNode node22 = om.createObjectNode();
    node22.put("match", node2);
    
    ArrayNode an = om.createArrayNode();
    an.add(node11);
    an.add(node22);
    ObjectNode must = om.createObjectNode();
    must.put("must", an);
    ObjectNode bool = om.createObjectNode();
    bool.put("bool", must);
    ObjectNode query = om.createObjectNode();
    query.put("query", bool);

    String result = db.SEARCH("svn", "users", query.toString() );               
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
            
            //Se genera un id con MD5 de todos los campos concatenados
            String id = user+svnData.getAuthor()+svnData.getRepository();
            
            try {
              md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
              e.printStackTrace();
            }
            md.update(id.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            
            ObjectNode data = om.createObjectNode();
            data.put("jira_user", user);
            data.put("svn_user", svnData.getAuthor());
            data.put("repository", svnData.getRepository());

            db.PUT("svn", "users", sb.toString(), data.toString());
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
    
    /* Se deberian validar todos los parametros, no solo la fecha
     * Pero de todas formas un return solo avisa que no se pudieron guardar
     * los datos y estos problemas son solo cuando se desarrolla un nuevo 
     * programa que guarda los datos, no deberian ocurrir problemas mientras se usa
     */
    
    /// rev0 ----------------------------------------------------------------------
    //TODO preguntar si esta o no. Igual se puede obviar porque es casi el mismo costo
    SvnPostCommitDataPOJO rev0 = new SvnPostCommitDataPOJO();
    rev0.setAuthor("null");
    rev0.setDate("2000-01-01 00:00:00");
    rev0.setMessage("null");
    rev0.setRepository(svnData.getRepository());
    rev0.setRevision("r0");
    
    String rev0id = rev0.getDate();
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    md.update(rev0id.getBytes());
    byte[] rev0digest = md.digest();
    StringBuffer rev0sb = new StringBuffer();
    for (byte b : rev0digest) {
      rev0sb.append(String.format("%02x", b & 0xff));
    }
    
    ObjectMapper rev0objectMapper = new ObjectMapper();
    JsonNode rev0data = rev0objectMapper.valueToTree(rev0);
    db.PUT("svn", "revisions", rev0sb.toString(), rev0data.toString());
    //------------------------------------------------------------------------------
    
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
      dateFormat.parse(svnData.getDate());
    } catch (ParseException e) {
      return "La fecha que se envio al servidor no esta en un formato valido";
    }
    
    String issue = null;
    matcher = issuePattern.matcher( svnData.getMessage() );
    if (matcher.find()) {
        issue = matcher.group(1);
    }
    
    //Se sobre escribe el mensaje por el issue que es lo que importa
    svnData.setMessage(issue);
    
    String id = svnData.getDate();
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    md.update(id.getBytes());
    byte[] digest = md.digest();
    StringBuffer sb = new StringBuffer();
    for (byte b : digest) {
        sb.append(String.format("%02x", b & 0xff));
    }
    
    
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode data = objectMapper.valueToTree(svnData);
    db.PUT("svn", "revisions", sb.toString(), data.toString());

    return "1";
  }
  
  /**
   * Esta clase hace un checkout de un scm en un lugar dado
   * 
   * 
   * @param pathDestination a donde se quieren los archivos del checkout (debe ser local)
   * @param revision la revision que se quiere hacer checkout
   * @param repository, repositorio que va por ejemplo: svn://localhost/<aca>
   * @return si se pudo hacer o no
   */
  public boolean doCheckout( String pathDestination, String revision, String repository) {
    //TODO testear que ande
    GenericJSONClient client = new GenericJSONClient( "LUGARDELCONNECTOR" );
    
    JsonFactory factory = new JsonFactory();
    ObjectMapper om = new ObjectMapper(factory);
    factory.setCodec(om);
    ObjectNode data = om.createObjectNode();
    data.put("path_destination", pathDestination);
    data.put("repository", repository);
    
    if ( client.POST( revision, data.toString()).equals("true") ) {
      return true;
    }
    return false;
  }


}
