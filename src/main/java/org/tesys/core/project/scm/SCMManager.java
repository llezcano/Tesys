package org.tesys.core.project.scm;

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
import org.tesys.core.project.tracking.ProjectTrackingRESTClient;
import org.tesys.util.GenericJSONClient;
import org.tesys.util.MD5;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * Esta clase es la principal encargada de llevar a cabo todas las tareas relacioanasdas
 * con el SCM que se utilice en el proyecto.
 * 
 * Tiene dos funciones principales.
 * 
 * Una es como ofrecedor de servicios para:
 * 
 * - Saber si un commit que se esta realizando es valido. 
 * Este servicio debe ser consumido antes de que se realice el commit, lo que en la
 * mayoria de los SCM se conoce como hook pre commit, y se le deben proporcionar los
 * datos que sirvan para validar el commit.
 * 
 * - Guardar un commit.
 * En este caso se almacena la informacion relacionada a que se realizo con exito un commit,
 * no los archivos que se commitearon, los datos se especifican en el metodo y basicamente 
 * este servicio se utiliza dentro del sistema para luego saber que revision corresponde a que
 * tarea del project tracking. Este servicio esta pensado para ser consumido por el hook post commit
 * Pero se puede usar de forma "asincrona" tranquilamente guardando los commits cada tanto usando
 * algun sistemas de log del SCM o lo que sea que se disponga.
 * 
 * Hay que mencionar que estos dos servicios van a ser utilizados quizas por scripts ya que es la unica
 * forma de hacer hooks en la mayoria de los SCMs, y estos scrips deben estar en alguna locacion
 * especifica, por lo que son imposibles de gestionar dentro del core, pero aun asi fundamentales
 * para el sistema.
 * 
 * La Segunda funcionalidad es desde el sistema poder hacer "checkouts" de un SCM.
 * En este caso, esta clase, actua como cliente de un servidor conocido como "conector SCM"
 * Que debe existir dependiendo del tipo de SCM que se este utilizando.
 * Dicho conector sera el que tenga la implementacion de como realizar un checkout y debera
 * guardar los archivos en una ruta predefinida que luego el sistema analizara.
 * 
 */

@Path("/scm")
@Singleton
public class SCMManager {

  private static final String DEFAULT_URL_SCM_CONNECTOR =
      "http://localhost:8080/core/rest/connectors/svn/"; //$NON-NLS-1$
  private static final String PROJECT_TRACKING_USER_DATA_ID = "project_tracking_user"; //$NON-NLS-1$
  private static final String INVALID_ISSUE = "#user='"; //$NON-NLS-1$
  private static final String INVALID_USER = "#issue='"; //$NON-NLS-1$
  private static final String QUERY_QL = "query"; //$NON-NLS-1$
  private static final String BOOL_QL = "bool"; //$NON-NLS-1$
  private static final String MUST_QL = "must"; //$NON-NLS-1$
  private static final String MATCH_QL = "match"; //$NON-NLS-1$
  private static final String REPOSITORY_DATA_ID = "repository"; //$NON-NLS-1$
  private static final String SCM_USER_DATA_ID = "scm_user"; //$NON-NLS-1$
  private static final String USER_REGEX = "#user='(.*?)'"; //$NON-NLS-1$
  private static final String ISSUE_REGEX = "#issue='(.*?)'"; //$NON-NLS-1$
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"; //$NON-NLS-1$
  private static final String OK_CODE = "1"; //$NON-NLS-1$
  private static final String SCM_DTYPE_REVISIONS = "revisions"; //$NON-NLS-1$
  private static final String SCM_DTYPE_USERS = "users"; //$NON-NLS-1$
  private static final String SCM_DB_INDEX = "scm"; //$NON-NLS-1$


  private Pattern issuePattern;
  private Pattern userPattern;
  private Matcher matcher;
  private DatabaseFacade db;
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
  }

  public SCMManager() {
    client = new GenericJSONClient(DEFAULT_URL_SCM_CONNECTOR);
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

    generateRevisionZero(scmData.getRepository());
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
    String id = MD5.generateId(scmData.getDate());

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

    if (client.PUT(revision, data.toString()).equals("true")) { //$NON-NLS-1$
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
    return Messages.getString("SCMManager.urlchanged"); //$NON-NLS-1$
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
        throw new Exception(Messages.getString("syntaxerrorcomilla")); //$NON-NLS-1$
      }
      if (matcher.find()) {
        throw new Exception(Messages.getString("sytaxerrormultiplecommands")); //$NON-NLS-1$
      }
      ProjectTrackingRESTClient pt = new ProjectTrackingRESTClient();
      //TODO descomentar cuando se implemente eso
      /*if (!pt.existIssue(issue)) {
        throw new Exception(Messages.getString("SCMManager.issueinvalido")); //$NON-NLS-1$
      }*/
    } else {
      throw new Exception(Messages.getString("syntaxerrorissue")); //$NON-NLS-1$
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
   
    //TODO
    String result = db.POST(SCM_DB_INDEX, SCM_DTYPE_USERS, query.toString());


    if (!result.contains(scmData.getAuthor())) {
      matcher = userPattern.matcher(scmData.getMessage());
      if (matcher.find()) {
        String user = matcher.group(1);
        if (user.contains(INVALID_USER)) {
          throw new Exception(Messages.getString("syntaxerrorcomilla")); //$NON-NLS-1$
        }
        if (matcher.find()) {
          throw new Exception(Messages.getString("sytaxerrormultiplecommands")); //$NON-NLS-1$
        }
        //TODO descomentar cuando se implemente eso
        ProjectTrackingRESTClient pt = new ProjectTrackingRESTClient();
        /*if (!pt.existUser(user)) {
          throw new Exception(Messages.getString("SCMManager.userinvalido")); //$NON-NLS-1$
        }*/

        String id = MD5.generateId(user + scmData.getAuthor() + scmData.getRepository());

        ObjectNode data = om.createObjectNode();
        data.put(PROJECT_TRACKING_USER_DATA_ID, user);
        data.put(SCM_USER_DATA_ID, scmData.getAuthor());
        data.put(REPOSITORY_DATA_ID, scmData.getRepository());

        db.PUT(SCM_DB_INDEX, SCM_DTYPE_USERS, id, data.toString());
      } else {
        throw new Exception(Messages.getString("syntaxerroruser")); //$NON-NLS-1$
      }
    }

  }

  /**
   * Este metodo inserta en la base de datos la revision 0 que es cuando el proyecto esta vacio, de
   * esta forma cuando se analize la informacion se va a poder sacar metricas en relacion a otros
   * commits con este
   * 
   * Hay que guardar un unico dato por cada repositorio, pero como el costo de preguntar si esta es
   * casi igual a guardarlo se guarda directamente siempre que se hace un commit
   * 
   * @param repository
   */

  private void generateRevisionZero(String repository) {
    ScmPostCommitDataPOJO rev0 = new ScmPostCommitDataPOJO();
    rev0.setAuthor("null"); //$NON-NLS-1$
    rev0.setDate("2000-01-01 00:00:00"); //$NON-NLS-1$
    rev0.setMessage("null"); //$NON-NLS-1$
    rev0.setRepository(repository);
    rev0.setRevision("0"); //$NON-NLS-1$

    ObjectMapper rev0objectMapper = new ObjectMapper();
    JsonNode rev0data = rev0objectMapper.valueToTree(rev0);
    db.PUT(SCM_DB_INDEX, SCM_DTYPE_REVISIONS, "r0", rev0data.toString()); //$NON-NLS-1$

  }


}
