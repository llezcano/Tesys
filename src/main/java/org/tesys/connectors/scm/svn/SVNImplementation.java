package org.tesys.connectors.scm.svn;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tesys.core.TesysPath;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNDiffStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNDiffStatus;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc2.SvnLog;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;
import org.tmatesoft.svn.core.wc2.SvnRevisionRange;
import org.tmatesoft.svn.core.wc2.SvnTarget;

public class SVNImplementation {
    
    private static String TRUNKPATTERN = "[(.*/trunk)/|(.*/trunk)]" ;
    
    private static String BRANCHPATTERN = "(.*/branches/.*?/)" ;

    private static final Logger LOG = Logger.getLogger(SVNConnector.class
	    .getName());
    
    private static FileHandler handler;
    
    private static SVNImplementation instance = null;

    private SVNImplementation() {
	try {
	    handler = new FileHandler(TesysPath.Path +"logs/tesys-log.%u.%g.xml", 1024 * 1024, 10);
	} catch (SecurityException | IOException e) {
	    LOG.log(Level.SEVERE, e.getMessage());
	    handler = null;
	}
	LOG.addHandler(handler);
	
    }
    
    public static SVNImplementation getInstance() {
        if( instance == null ) {
            instance = new SVNImplementation();
        }
        return instance;
    }

    public long checkout(SVNURL url, SVNRevision revision,
	    File destPath, boolean isRecursive) throws SVNException {
	SVNClientManager clientManager = SVNClientManager.newInstance();
	SVNUpdateClient updateClient = clientManager.getUpdateClient();
	updateClient.setIgnoreExternals(false);
	
	LOG.log(Level.INFO, "Realizando checkout de "+ url.toString() + " -> " + revision.toString());
	
	return updateClient.doCheckout(url, destPath, revision, revision,
		SVNDepth.INFINITY, false);
    }
    
    
    public static void main( String[] args ) {
        String ret = SVNImplementation.getInstance().diff( "svn://localhost", 4, 5 );
        System.out.println(ret);
    }

    /**
     * Devuelve una lista de paths, archivos modificados eliminados o agregados
     */
    public String diff(String url, Integer initRevision, Integer lastRevision) {
	
	final StringBuilder string = new StringBuilder();
	
	SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
	SVNDiffClient diffClient = new SVNDiffClient(svnOperationFactory);
	
	LOG.log(Level.INFO, "Realizando diff de "+ url.toString() + " -> " +
		initRevision.toString() + ":" + lastRevision.toString() );

	try {
	diffClient.doDiff(
		SVNURL.parseURIEncoded(url),
		SVNRevision.create(initRevision),
		SVNURL.parseURIEncoded(url),
		SVNRevision.create(lastRevision), SVNDepth.INFINITY, false,
		new OutputStream() {
	        @Override
	        public void write(int b) throws IOException {
	            string.append((char) b );
	        }
	    });
	} catch(Exception e) {
	    LOG.log(Level.SEVERE, e.toString(), e);
	}

	return string.toString();
    }
    
    /**
     * Dada una revision y la url de un repositorio, este metodo calcula el branch/trunk
     * donde se realizo el commit, para luego hacer el analisis de dicho branch y no de
     * todo el repositorio.
     * 
     * @param url 
     *              direccion del repositorio
     * @param rev 
     *              revision que genero el commit.
     * @return  
     *              url base del branch/trunk donde se hizo el commit.
     */
    public String getSvnBasePath( String url, int rev ) {
        String changedPath = "/";
        String basePath = null ;

        // from 'stackoverflow.com/questions/11029968/svn-log-using-svnkit'
        final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
        final SvnLog log = svnOperationFactory.createLog();
        try {
            log.setSingleTarget( SvnTarget.fromURL( SVNURL.parseURIEncoded( url ) ) );
            log.addRange( SvnRevisionRange.create( SVNRevision.create( rev ), SVNRevision.create( rev ) ) );
            log.setDiscoverChangedPaths( true );
            final SVNLogEntry logEntry = log.run();
            final Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
            for (Map.Entry<String, SVNLogEntryPath> entry : changedPaths.entrySet()) {
                changedPath = entry.getValue().getPath();
                break;
            }

            if (!"/".equals( changedPath )) {
                // Primero me fijo si esta en un trunk, si no machea con ningun
                // trunk entonces esta si o si en un branch.
                Pattern trunkPattern = Pattern.compile( TRUNKPATTERN );
                Matcher m = trunkPattern.matcher( changedPath );
                if (m.find()) {
                    basePath = m.group( 1 );
                } else {
                    Pattern branchPattern = Pattern.compile( BRANCHPATTERN );
                    m = branchPattern.matcher( changedPath );
                    if (m.find()) {
                        basePath = m.group( 1 );
                    }
                }
                return SVNURL.parseURIEncoded( url ).setPath( basePath, true ).toString() ;
            }

        } catch (SVNException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
       }
       return null;
               
    }

}
