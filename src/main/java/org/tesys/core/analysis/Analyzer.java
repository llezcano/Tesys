package org.tesys.core.analysis;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.tesys.connectors.scm.svn.SVNConnector;
import org.tesys.core.TesysPath;
import org.tesys.core.analysis.telemetry.ProcessData;

public class Analyzer {

    
    private static final Logger LOG = Logger.getLogger(SVNConnector.class
	    .getName());
    
    static FileHandler handler;
    
    private static Analyzer instance = null;

    private Analyzer() {
	try {
	    handler = new FileHandler(TesysPath.Path +"logs/tesys-log.%u.%g.xml", 1024 * 1024, 10);
	} catch (SecurityException | IOException e) {}
	LOG.addHandler(handler);
    }

    public static Analyzer getInstance() {
	if (instance == null) {
	    instance = new Analyzer();
	}
	return instance;
    }

    public void performAnalysis() {

	LOG.log(Level.INFO, "Se programo un analisis");
	
	ProcessData pd = ProcessData.getInstance();
	pd.executeProcessor();

    }

}
