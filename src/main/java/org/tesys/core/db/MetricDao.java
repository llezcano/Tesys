package org.tesys.core.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.ws.rs.core.UriBuilder;

import org.tesys.core.estructures.*;

import org.tesys.core.estructures.Metric;
import org.tesys.core.estructures.MetricFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class MetricDao extends ElasticsearchDao<Metric> {

    protected MetricFactory factory = new MetricFactory() ;
    
    public MetricDao( Class inferedClass, String resource ) {
        super( inferedClass, resource );
        // TODO Auto-generated constructor stub
    }


    @Override
    public void create( String id, Metric object ) {
        try {
            client.PUT( UriBuilder.fromPath( resource ).path( id ).toString(), object.toString() );
        } catch ( Exception e ) {
            LOG.log( Level.SEVERE, e.toString(), e );
        } 
    }

    @Override
    public void update( String id, Metric object ) {
        try {
            client.POST( UriBuilder.fromPath( resource ).path( id ).toString(), object.toString() );
        } catch ( Exception e ) {
            LOG.log( Level.SEVERE, e.toString(), e );
        }
    }

    @Override
    public Metric read( String id ) {
        try {
            JsonNode json = client.GET( UriBuilder.fromPath( resource ).path( id ).path( SOURCE ).toString() )
                                     .readEntity( JsonNode.class );
            return factory.getMetric( json ) ;
        } catch (Exception e) {
            LOG.log( Level.SEVERE, e.toString(), e );
            return null;
        }
    }

    @Override
    public void delete( String id ) {
        try {
            client.DELETE( UriBuilder.fromPath( id ).toString() );
        } catch ( Exception e ) {
            LOG.log( Level.SEVERE, e.toString(), e );
        }
    }

     @Override
    protected List<Metric> arrayJsonToList( ArrayNode arrayNode ) {
        Iterator<JsonNode> it = null;
        List<Metric> elements = new ArrayList<Metric>();

        try {
            it = arrayNode.elements();
        } catch (Exception e) {
            LOG.log( Level.SEVERE, e.toString(), e );
            return new ArrayList<Metric>();
        }

        while (it.hasNext()) {
            JsonNode j = ((JsonNode) it.next()).get( "_source" );
            try {
                Metric elem = factory.getMetric( j ) ;
                elements.add( elem );
            } catch (Exception e) {
                LOG.log( Level.SEVERE, e.toString(), e );
                return new ArrayList<Metric>();
            }
            it.remove();
        }
        return elements;
    }


}
