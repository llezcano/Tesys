package org.tesys.core.analysis;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.tesys.connectors.scm.svn.SVNConnector;
import org.tesys.core.analysis.telemetry.ProcessData;

public class Analyzer {

    
    private static final Logger LOG = Logger.getLogger(SVNConnector.class
	    .getName());
    
    static FileHandler handler;
    
    private static Analyzer instance = null;

    private Analyzer() {
	try {
	    handler = new FileHandler("tesys-log.%u.%g.txt", 1024 * 1024, 10);
	} catch (SecurityException | IOException e) {}
	LOG.addHandler(handler);
    }

    public static Analyzer getInstance() {
	if (instance == null) {
	    instance = new Analyzer();
	}
	return instance;
    }

    public Response performAnalysis() {

	LOG.log(Level.INFO, "Se programo un analisis");
	
	ProcessData pd = ProcessData.getInstance();
	pd.executeProcessor();

	ResponseBuilder response = Response.ok("{\"analysis\":\"finished\"}");
	return response.build();
    }

}
