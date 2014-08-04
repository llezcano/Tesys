package org.tesys.core.project.tracking;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.GenericType;

import org.tesys.core.analysis.sonar.MetricPOJO;
import org.tesys.util.RESTClient;

/**
 * Este ProjectTracking es un cliente REST para el Connector de un Project
 * Manager.
 * 
 * @author rulo
 * 
 */
public class ProjectTrackingRESTClient implements ProjectTracking {

    private RESTClient client;

    private static final Logger LOG = Logger.getLogger( ProjectTrackingRESTClient.class.getName() );

    // RESEARCH Discovery Resources from Connector
    private static String RESOURCE_ISSUES = "issues/";
    private static String RESOURCE_USERS = "users/";
    private static String RESOURCE_METRIC = "metric/";

    public ProjectTrackingRESTClient() {
        try {
            client = new RESTClient( getConnectorLocation() );
        } catch (MalformedURLException e) {
            LOG.log( Level.SEVERE, e.toString(), e );
        }

    }

    public String getConnectorLocation() {
        // RESEARCH Discovery Services
        return "http://localhost:8080/core/rest/connectors/jira";
    }

    @Override
    public Issue[] getIssues() {
        IssuePOJO[] issues = client.GET( RESOURCE_ISSUES ).readEntity( IssuePOJO[].class );
        return issues;
    }

    @Override
    public User[] getUsers() {
        UserPOJO[] users = client.GET( RESOURCE_USERS ).readEntity( UserPOJO[].class );
        return users;
    }

    @Override
    public boolean existUser( String key ) {
        UserPOJO u = client.GET( RESOURCE_USERS + key ).readEntity( UserPOJO.class );
        return (u != null);
    }

    @Override
    public boolean existIssue( String key ) {
        IssuePOJO i = client.GET( RESOURCE_ISSUES + key ).readEntity( IssuePOJO.class );
        return (i != null);
    }

    @Override
    public Issue getIssue( String key ) {
        IssuePOJO i = client.GET( RESOURCE_ISSUES + key ).readEntity( IssuePOJO.class );
        return i;
    }

    @Override
    public List<MetricPOJO> getMetrics() {
        try {
            return client.GET( RESOURCE_METRIC ).readEntity( new GenericType<List<MetricPOJO>>() {
            } );
        } catch (Exception e) {
            return new ArrayList<MetricPOJO>();
        }
    }

    @Override
    public List<String> getIssuesKeys() {
        // TODO hacerlo en el Connector.
        Issue[] issues = this.getIssues();
        List<String> keys = new ArrayList<String>();
        for (int i = 0; i < issues.length; i++) {
            if (issues[i] != null && issues[i].getKey() != null) {
                keys.add( issues[i].getKey() );
            }
        }
        return keys;
    }
}
