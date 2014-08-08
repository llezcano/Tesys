package org.tesys.core.project.scm;


import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tesys.core.db.ElasticsearchDao;
import org.tesys.core.db.ValidDeveloperQuery;
import org.tesys.core.messages.Messages;
import org.tesys.core.project.tracking.ProjectTracking;
import org.tesys.core.project.tracking.ProjectTrackingRESTClient;



/**
 * Esta clase es la principal encargada de llevar a cabo todas las tareas relacioanasdas con el SCM
 * que se utilice en el proyecto.
 * 
 * Tiene dos funciones principales.
 * 
 * Una es como ofrecedor de servicios para:
 * 
 * - Saber si un commit que se esta realizando es valido. Este servicio debe ser consumido antes de
 * que se realice el commit, lo que en la mayoria de los SCM se conoce como hook pre commit, y se le
 * deben proporcionar los datos que sirvan para validar el commit.
 * 
 * - Guardar un commit. En este caso se almacena la informacion relacionada a que se realizo con
 * exito un commit, no los archivos que se commitearon, los datos se especifican en el metodo y
 * basicamente este servicio se utiliza dentro del sistema para luego saber que revision corresponde
 * a que tarea del project tracking. Este servicio esta pensado para ser consumido por el hook post
 * commit Pero se puede usar de forma "asincrona" tranquilamente guardando los commits cada tanto
 * usando algun sistemas de log del SCM o lo que sea que se disponga.
 * 
 * Hay que mencionar que estos dos servicios van a ser utilizados quizas por scripts ya que es la
 * unica forma de hacer hooks en la mayoria de los SCMs, y estos scrips deben estar en alguna
 * locacion especifica, por lo que son imposibles de gestionar dentro del core, pero aun asi
 * fundamentales para el sistema.
 * 
 * La Segunda funcionalidad es desde el sistema poder hacer "checkouts" de un SCM. En este caso,
 * esta clase, actua como cliente de un servidor conocido como "conector SCM" Que debe existir
 * dependiendo del tipo de SCM que se este utilizando. Dicho conector sera el que tenga la
 * implementacion de como realizar un checkout y debera guardar los archivos en una ruta predefinida
 * que luego el sistema analizara.
 * 

 * 
 */

public class SCMManager {

  private static final String SCM_MANAGER_FORMATOFECHAINVALIDO = "SCMManager.formatofechainvalido";
  private static final String SCM_MANAGER_ISSUEINVALIDO = "SCMManager.issueinvalido";
  private static final String SYNTAXERRORISSUE = "SCMManager.syntaxerrorissue";
  private static final String SYTAXERRORMULTIPLECOMMANDS = "SCMManager.sytaxerrormultiplecommands";
  private static final String SCM_MANAGER_USERINVALIDO = "SCMManager.userinvalido";
  private static final String SYNTAXERRORUSER = "SCMManager.syntaxerroruser";
  private static final String SCM_MANAGER_BASEDEDATOSCAIDA = "SCMManager.basededatoscaida";

  private static final String INVALID_ISSUE = "#user='"; //$NON-NLS-1$
  private static final String INVALID_USER = "#issue='"; //$NON-NLS-1$
  private static final String USER_REGEX = "#user='(.*?)'"; //$NON-NLS-1$
  private static final String ISSUE_REGEX = "#issue='(.*?)'"; //$NON-NLS-1$
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"; //$NON-NLS-1$

  private Pattern issuePattern;
  private Pattern userPattern;
  private Matcher matcher;
  private SCMFacade scmFacade;
  
  private static SCMManager instance = null;
  
  private SCMManager() {
    issuePattern = Pattern.compile(ISSUE_REGEX);
    userPattern = Pattern.compile(USER_REGEX);
    scmFacade = SCMFacade.getInstance();
  }

  public static SCMManager getInstance() {
    if (instance == null) {
      instance = new SCMManager();
    }
    return instance;
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
  public boolean isCommitAllowed(ScmPreCommitDataPOJO scmData) throws InvalidCommitException {
    try {
      //cada uno de estos se puede hacer con un thread aparte
      getIssue(scmData.getMessage());
      mapUser(scmData);
    } catch (InvalidCommitException e) {
      throw e;
    }

    return true;
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
  public boolean storeCommit(ScmPostCommitDataPOJO scmData) throws InvalidCommitException {

    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    String issue = null;
    Date formatDate = null;

    try {
      
      issue = getIssue(scmData.getMessage());
      formatDate = dateFormat.parse(scmData.getDate());
      
    } catch (InvalidCommitException e) {
      throw e;
    } catch (ParseException e1) {
      throw new InvalidCommitException(Messages.getString(SCM_MANAGER_FORMATOFECHAINVALIDO)); //$NON-NLS-1$
    }

    RevisionPOJO revision = new RevisionPOJO(formatDate.getTime(), scmData.getAuthor(), issue,
        scmData.getRevision(), scmData.getRepository());

    ElasticsearchDao<RevisionPOJO> dao = new ElasticsearchDao<RevisionPOJO>(
        RevisionPOJO.class, 
        ElasticsearchDao.DEFAULT_RESOURCE_REVISION  );
    
    dao.create(revision.getID(), revision);

    return true;
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
  public boolean doCheckout(String revision, String repository, File workspace) {
    return scmFacade.doCheckout(revision, repository, workspace);
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
  private String getIssue(String message) throws InvalidCommitException {
    String issue;
    matcher = issuePattern.matcher(message);
    if (matcher.find()) {
      issue = matcher.group(1);
      if (issue.contains(INVALID_ISSUE)) {
        throw new InvalidCommitException(Messages.getString(SYNTAXERRORISSUE)); //$NON-NLS-1$
      }
      if (matcher.find()) {
        throw new InvalidCommitException(Messages.getString(SYTAXERRORMULTIPLECOMMANDS)); //$NON-NLS-1$
      }
      ProjectTracking pt = new ProjectTrackingRESTClient();
      if (!pt.existIssue(issue)) {
        throw new InvalidCommitException(Messages.getString(SCM_MANAGER_ISSUEINVALIDO)); //$NON-NLS-1$
      }
    } else {
      throw new InvalidCommitException(Messages.getString(SYNTAXERRORISSUE));  //$NON-NLS-1$
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
   * 
   * @param scmData
   * @throws Exception
   */
  private void mapUser(ScmPreCommitDataPOJO scmData) throws InvalidCommitException {

    boolean existeMapeo;

    ValidDeveloperQuery query = new ValidDeveloperQuery( scmData.getAuthor(), scmData.getRepository());
    
    try {
      existeMapeo = query.execute();
    } catch (Exception e) {
      throw new InvalidCommitException(Messages.getString(SCM_MANAGER_BASEDEDATOSCAIDA), e);
    }


    if ( !existeMapeo ) {
      matcher = userPattern.matcher(scmData.getMessage());
      if (matcher.find()) {
        String user = matcher.group(1);
        
        if (user.contains(INVALID_USER)) {
          throw new InvalidCommitException(Messages.getString(SYNTAXERRORUSER));
        }
        
        if (matcher.find()) {
          throw new InvalidCommitException(Messages.getString(SYTAXERRORMULTIPLECOMMANDS));
        }
        
        ProjectTracking pt = new ProjectTrackingRESTClient();
        if (!pt.existUser(user)) {
          throw new InvalidCommitException(Messages.getString(SCM_MANAGER_USERINVALIDO));
        }


        MappingPOJO mp = new MappingPOJO(user, scmData.getAuthor(), scmData.getRepository());

        ElasticsearchDao<MappingPOJO> dao = new ElasticsearchDao<MappingPOJO>(
            MappingPOJO.class, 
            ElasticsearchDao.DEFAULT_RESOURCE_MAPPING  );
        
        dao.create(mp.getID(), mp);

      } else {
        throw new InvalidCommitException(Messages.getString(SYNTAXERRORUSER));
      }
    }

  }

}
