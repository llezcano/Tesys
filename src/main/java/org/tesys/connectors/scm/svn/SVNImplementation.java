package org.tesys.connectors.scm.svn;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.tesys.core.TesysPath;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNDiffStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNDiffStatus;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;

public class SVNImplementation {

    private static final Logger LOG = Logger.getLogger(SVNConnector.class
	    .getName());
    
    private static FileHandler handler;

    public SVNImplementation() {
	try {
	    handler = new FileHandler(TesysPath.Path +"logs/tesys-log.%u.%g.xml", 1024 * 1024, 10);
	} catch (SecurityException | IOException e) {}
	LOG.addHandler(handler);
	
    }

    public static long checkout(SVNURL url, SVNRevision revision,
	    File destPath, boolean isRecursive) throws SVNException {
	SVNClientManager clientManager = SVNClientManager.newInstance();
	SVNUpdateClient updateClient = clientManager.getUpdateClient();
	updateClient.setIgnoreExternals(false);
	
	LOG.log(Level.INFO, "Realizando checkout de "+ url.toString() + " -> " + revision.toString());
	
	return updateClient.doCheckout(url, destPath, revision, revision,
		SVNDepth.INFINITY, false);
    }

    /**
     * Devuelve una lista de paths, archivos modificados eliminados o agregados
     */
    public static List<String> diff(String url, Integer initRevision, Integer lastRevision) {
	
	final List<String> ret = new LinkedList<String>();
	
	SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
	SVNDiffClient diffClient = new SVNDiffClient(svnOperationFactory);
	
	LOG.log(Level.INFO, "Realizando diff de "+ url.toString() + " -> " +
		initRevision.toString() + ":" + lastRevision.toString() );

	try {
	diffClient.doDiffStatus(
		SVNURL.parseURIEncoded(url),
		SVNRevision.create(initRevision),
		SVNURL.parseURIEncoded(url),
		SVNRevision.create(lastRevision), SVNDepth.INFINITY, false,
		new ISVNDiffStatusHandler() {
		    public void handleDiffStatus(SVNDiffStatus diffStatus) throws SVNException {
			if (diffStatus.getKind() == SVNNodeKind.FILE) {
			    ret.add(diffStatus.getFile().getAbsolutePath());
			}
		    }
		});
	} catch(Exception e) {
	    LOG.log(Level.SEVERE, e.toString(), e);
	}

	return ret;
    }

}
