package org.tesys.connectors.scm.svn;

import java.io.File;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;


@Path("/connectors/svn")
public class SVNConnector {
  
  private static final String url = "svn://localhost/";

  private long checkout(SVNURL url, SVNRevision revision, File destPath, boolean isRecursive)
      throws SVNException {
    SVNClientManager clientManager = SVNClientManager.newInstance();
    SVNUpdateClient updateClient = clientManager.getUpdateClient();
    updateClient.setIgnoreExternals(false);

    return updateClient.doCheckout(url, destPath, revision, revision, SVNDepth.INFINITY, false);
  }
  
  
  /**
   * Servicio REST para hacer checkouts del SVN 
   * 
   * El servidor svn se guarda en esta clase pero el repositorio y la version tienen
   * que pasarse como parametro
   * 
   * Cualquier checkout que se haga se hace en la carpeta home dentro de la carpeta
   * ".tesys"
   * 
   * No hay una forma facil de cambiar esto porque el servidor tiene rutas relativas
   * Asi que si se desea cambiar hay que analizar bien como implementarlo
   * 
   * @param revision se pone en la url un numero que indica la revision que se quiere hacer co
   * @param svnco Este POJO basicamente tiene el repositorio del que se quiere hacer co
   * @return
   */
  
  @PUT
  @Path("{revision}")
  public long checkout( @PathParam("revision") String revision, SvnCheckoutPOJO svnco) {
    
    SVNURL location = null;
    try {
      location = SVNURL.parseURIEncoded( url + svnco.getRepository());
    } catch (SVNException e1) {
      e1.printStackTrace();
    }

    SVNRevision svnr = SVNRevision.create( Integer.parseInt( revision ) );
    //TODO esta ruta hay que pedirsela al core
    final File destPath = new File(System.getProperty("user.home"), ".tesys/workspace");
    
    try {
      return checkout(location, svnr, destPath, false);
    } catch (SVNException e) {
      e.printStackTrace();
    }
    return 0;
  }
}
