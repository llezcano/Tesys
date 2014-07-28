package org.tesys.core.project.scm;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import org.tesys.util.GenericJSONClient;
import org.tesys.util.SearchJSONClient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Path("/scm")
@Singleton
public class SCMManager {

  private static final String PROJECT_TRACKING_USER_DATA_ID = "project_tracking_user";
  private static final String INVALID_ISSUE = "#user='";
  private static final String INVALID_USER = "#issue='";
  private static final String QUERY_QL = "query";
  private static final String BOOL_QL = "bool";
  private static final String MUST_QL = "must";
  private static final String MATCH_QL = "match";
  private static final String REPOSITORY_DATA_ID = "repository";
  private static final String SCM_USER_DATA_ID = "scm_user";
  private static final String USER_REGEX = "#user='(.*?)'";
  private static final String ISSUE_REGEX = "#issue='(.*?)'";
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
  private static final String OK_CODE = "1";
  private static final String SCM_DTYPE_REVISIONS = "revisions";
  private static final String SCM_DTYPE_USERS = "users";
  private static final String SCM_DB_INDEX = "scm";
  private static final String HASH_TYPE = "MD5";

  private Pattern issuePattern;
  private Pattern userPattern;
  private Matcher matcher;
  private DatabaseFacade db;
  private MessageDigest md;
  private GenericJSONClient client;



  /**
   * Se ejecuta solo la primer vez que se usa un servicio ya que la clase en Singleton Inicializa
   * variables utiles para todos los servicios
   */
  @PostConstruct
  public void init() {
    issuePattern = Pattern.compile(ISSUE_REGEX);
    userPattern = Pattern.compile(USER_REGEX);
    db = new DatabaseFacade();
    client = new SearchJSONClient("http://localhost:8080/core/rest/connectors/svn/"); // default
                                                                                      // value

  }



  /**
   * Este metodo debe ser llamado antes de que se haga efectivo cada commit de esa forma, si el
   * commit tiene los datos validos para poder ser aceptado por el sistema este metodo devulve el
   * string 1
   * 
   * Sino devuelve un mensaje con el error que surgio
   * 
   * @param scmData Datos previos a hacer un commit (autor, mensaje y repos)
   * @return
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String isCommitAllowed(ScmPreCommitDataPOJO scmData) {
    try {
      // cada uno de estos se puede hacer con un thread aparte
      getIssue(scmData.getMessage());
      mapUser(scmData);
    } catch (Exception e) {
      return e.getMessage();
    }

    return OK_CODE;
  }



  /**
   * Este metodo es el encargado de guardar toda la informacion importante de cada commit Debe ser
   * llamado principalmente con un hook post commit, aunque puede ser llamado en cualquier momento
   * 
   * La informacion que hay que suministrale es: revision, mensaje, fecha, repo, usuario Del mesaje
   * se extrae la tarea del project tracking para almacenar, no se almacena El mensaje completo.
   * 
   * La fecha debe estar en un formato predeterminado que es el siguinte: yyyy-MM-dd HH:mm:ss
   * 
   * @param scmData
   * @return
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String storeCommit(ScmPostCommitDataPOJO scmData) {

    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    String issue = null;

    try {
      issue = getIssue(scmData.getMessage());
      dateFormat.parse(scmData.getDate());
    } catch (Exception e) {
      return e.getMessage();
    }

    // Se sobre escribe el mensaje por el issue que es lo que importa
    scmData.setMessage(issue);
    String id = generateId(scmData.getDate());

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode data = objectMapper.valueToTree(scmData);
    db.PUT(SCM_DB_INDEX, SCM_DTYPE_REVISIONS, id, data.toString());

    return OK_CODE;
  }



  /**
   * Esta clase hace un checkout de un scm en la carpeta $HOME/.tesys
   * 
   * Se debe indicar donde esta el conector del SCM que a su vez este va a tener La verdadera ruta
   * donde esta el servidor SCM
   * 
   * doCheckout("1", "myrepo"); //revision 1 de myrepo en el server que tenga el conector
   * 
   * La url final en del estilo:
   * 
   * http://localhost:8080/core/rest/connectors/svn/1
   * 
   * @param revision la revision que se quiere hacer checkout
   * @param repository, repositorio que va por ejemplo: svn://localhost/<aca>
   * @return si se pudo hacer o no
   */
  public boolean doCheckout(String revision, String repository) {

    JsonFactory factory = new JsonFactory();
    ObjectMapper om = new ObjectMapper(factory);
    factory.setCodec(om);
    ObjectNode data = om.createObjectNode();
    data.put(REPOSITORY_DATA_ID, repository);

    if (client.PUT(revision, data.toString()).equals("true")) {
      return true;
    }
    return false;
  }



  /**
   * Este metodo debe ser utlizado para setear en que url esta el connector del SCM por default es
   * en:
   * 
   * http://localhost:8080/core/rest/connectors/svn/
   * 
   * @param url
   * @return
   */
  @PUT
  @Path("/config")
  public String changeConnectorLocation(String url) {
    client.setURL(url);
    return "Done";
  }



  /**
   * Dado el mensaje de un commit obtiene el issue relacionado con el project tacking de acuerdo los
   * comados planteados.
   * 
   * El issue que retorna es valido, por esta razon cualquier error que el usuario tenga el
   * ingresarlo en el mensaje resultara en un error
   * 
   * @param message
   * @return
   * @throws Exception
   */
  private String getIssue(String message) throws Exception {
    String issue;
    matcher = issuePattern.matcher(message);
    if (matcher.find()) {
      issue = matcher.group(1);
      if (issue.contains(INVALID_ISSUE)) {
        throw new Exception("El mensaje del commit es erroneo, falta una comilla?");
      }
      if (matcher.find()) {
        throw new Exception("Solo se puede referenciar un unico issue");
      }
      // TODO preguntarle el project.tracking si existe issue en jira
      // ProjectTracking pt;
      // if( !pt.existIssue( issue ) ) {
      // throw new Exception("El issue: " + issue + "No coincide con un issue existente");
      // }
    } else {
      throw new Exception(
          "Se debe indicar el issue relacionado con el commit de la forma #issue='foo'");
    }
    return issue;
  }



  /**
   * Este metodo se encarga de mappear los usarios del scm con los del project tracking
   * 
   * Para eso saca el valor del usuario del scm de la informacion del commit y el valor del usuario
   * del project tracking del mensaje del commit
   * 
   * El mapeo de un mismo usuario ocurre solo una vez (la primer vez que hace un commit) despues ya
   * no es necesario que siga indicando el user del project tracking en el mensaje ya que este
   * metodo se fija en la base de datos si el usuario del scm ya tiene un mapeo o no
   * 
   * Ya que el mapeo se debe hacer correctamente este metod puede arrojar varias excepciones como
   * por ejemplo user no valido del project tracking o de algun problema de sintaxis
   * 
   * Ademas este metodo guarda en la base de datos los mapeos
   * 
   * Para poder buscar si el user ya esta mapeado o no utiliza el lenguaje de busqueda interno del
   * core, que es un JSON especial que se utiliza como una query, el json que utiliza es el siguinte
   * 
   * 
   * { "query": { "bool": { "must": [ { "match": { "scm_user": "<usuario>" }}, { "match": {
   * "repository": "<repositorio>" }} ] } } }
   * 
   * Dirigirse a la documentacion del lenguaje para mas informacion:
   * http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/search-request-body.html
   * 
   * @param scmData
   * @throws Exception
   */
  private void mapUser(ScmPreCommitDataPOJO scmData) throws Exception {
    // generar una query para buscar en la db
    JsonFactory factory = new JsonFactory();
    ObjectMapper om = new ObjectMapper(factory);
    factory.setCodec(om);

    ObjectNode node1 = om.createObjectNode();
    node1.put(SCM_USER_DATA_ID, scmData.getAuthor());
    ObjectNode node11 = om.createObjectNode();
    node11.put(MATCH_QL, node1);

    ObjectNode node2 = om.createObjectNode();
    node2.put(REPOSITORY_DATA_ID, scmData.getRepository());
    ObjectNode node22 = om.createObjectNode();
    node22.put(MATCH_QL, node2);

    ArrayNode an = om.createArrayNode();
    an.add(node11);
    an.add(node22);
    ObjectNode must = om.createObjectNode();
    must.put(MUST_QL, an);
    ObjectNode bool = om.createObjectNode();
    bool.put(BOOL_QL, must);
    ObjectNode query = om.createObjectNode();
    query.put(QUERY_QL, bool);
    // fin generacion de query

    // TODO analizar como va a ser el SEARCH final
    String result = db.POST(SCM_DB_INDEX, SCM_DTYPE_USERS, query.toString());
    if (result.equals("CAMBIAME")) { // TODO
      matcher = userPattern.matcher(scmData.getMessage());
      if (matcher.find()) {
        String user = matcher.group(1);
        if (user.contains(INVALID_USER)) {
          throw new Exception("El mensaje del commit es erroneo, falta una comilla?");
        }
        if (matcher.find()) {
          throw new Exception("Solo se puede referenciar un unico issue");
        }
        // TODO
        // if( !pt.existUser( user ) ) {
        // throw new Exception("El user: " + user + "No coincide con un user existente");
        // }

        String id = generateId(user + scmData.getAuthor() + scmData.getRepository());

        ObjectNode data = om.createObjectNode();
        data.put(PROJECT_TRACKING_USER_DATA_ID, user);
        data.put(SCM_USER_DATA_ID, scmData.getAuthor());
        data.put(REPOSITORY_DATA_ID, scmData.getRepository());

        db.PUT(SCM_DB_INDEX, SCM_DTYPE_USERS, id, data.toString());
      } else {
        throw new Exception(
            "Como es la primer vez que este usuario hace un commit debe indicar su nombre en el project tracking que se este usando de la forma #user='bar'");
      }
    }

  }



  /**
   * Este metodo genera el MD5 equivalente de un String dado. Si se quieren varios Strings hay que
   * pasarlos concatenados o de alguna otra forma
   * 
   * @param input
   * @return
   */
  private String generateId(String input) {
    try {
      md = MessageDigest.getInstance(HASH_TYPE);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    md.update(input.getBytes());
    byte[] digest = md.digest();
    StringBuffer sb = new StringBuffer();
    for (byte b : digest) {
      sb.append(String.format("%02x", b & 0xff));
    }
    return sb.toString();
  }


}
