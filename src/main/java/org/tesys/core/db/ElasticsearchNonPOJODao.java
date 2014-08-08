package org.tesys.core.db;

import java.util.logging.Level;

import javax.ws.rs.core.UriBuilder;

import com.fasterxml.jackson.databind.JsonNode;

public class ElasticsearchNonPOJODao<T> extends ElasticsearchDao<T> {

    public ElasticsearchNonPOJODao( Class<T> inferedClass, String resource ) {
        super( inferedClass, resource );
    }
    
    @Override
    public void create( String id, T object ) {
        try {
            client.PUT( UriBuilder.fromPath( resource ).path( id ).toString(), object.toString() );
        } catch ( Exception e ) {
            LOG.log( Level.SEVERE, e.toString(), e );
        } 
    }

    @Override
    public void update( String id, T object ) {
        try {
            client.POST( UriBuilder.fromPath( resource ).path( id ).toString(), object.toString() );
        } catch ( Exception e ) {
            LOG.log( Level.SEVERE, e.toString(), e );
        }
    }

    @Override
    public T read( String id ) {
        try {
             client.GET( UriBuilder.fromPath( resource ).path( id ).path( SOURCE ).toString() )
                            .readEntity( JsonNode.class );
            //inferedClass
             return null ;
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


}
