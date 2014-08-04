package org.tesys.core.project.scm;

import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.tesys.core.db.Database;
import org.tesys.util.RESTClient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * Esta clase es la interfaz que utiliza el core para poder acceder al conector del SCM que tendria
 * la implementacion del SCM especifico que se este usando
 * 
 * POr lo tanto debe contener la localizacion del conector como informacion adicional para poder
 * comunicarse, pero no logica del modelo
 */

public class SCMFacade {

  private static final String DEFAULT_URL_SCM_CONNECTOR =
      "http://localhost:8080/core/rest/connectors/svn/"; //$NON-NLS-1$
  
  private static final Logger LOG = Logger.getLogger( SCMFacade.class.getName() );


  private RESTClient client;

  private static SCMFacade instance = null;

  private SCMFacade() {
    try {
      client = new RESTClient(DEFAULT_URL_SCM_CONNECTOR);
    } catch (MalformedURLException e) {
      LOG.log( Level.SEVERE, e.toString(), e );
    }
  }

  public static SCMFacade getInstance() {
    if (instance == null) {
      instance = new SCMFacade();
    }
    return instance;
  }



  public boolean doCheckout(String revision, String repository, File workspace) {

    JsonFactory factory = new JsonFactory();
    ObjectMapper om = new ObjectMapper(factory);
    factory.setCodec(om);
    ObjectNode data = om.createObjectNode();
    data.put("repository", repository);
    data.put("workspace", workspace.getAbsolutePath());

    if (client.PUT(revision, data.toString()).getStatus() / 100 == 2) {
      return true;
    }
    return false;
  }


}
