package org.tesys.core.db;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.UriBuilder;

import org.tesys.util.RESTClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;


/**
 * Esta clase tiene como objectivo suministrar una interfaz común entre la
 * aplicación y el componente de almacenamiento de datos
 * 
 * La misma implementa un Data Access Object (DAO).
 * 
 * @author leandro
 * 
 * @param <T>
 */
public class ElasticsearchDao<T extends Object> implements GenericDao<T> {

    // Constantes para Response.getStatus del Elasticsearch
    protected static final int RESP_OK = 200;

    protected static final Logger LOG = Logger.getLogger( ElasticsearchDao.class.getName() );

    protected static final String SOURCE = "/_source";
    protected static final String QUERY = "/_search";
    protected static final String SCROLL = "/scroll";
    
    //Default resources
    protected static final String DEFAULT_RESOURCE_MAPPING = "/scm/user" ;
    protected static final String DEFAULT_RESOURCE_REVISION = "/scm/revision" ;
    protected static final String DEFAULT_RESOURCE_ANALYSIS = "/analyzer/analysis" ;
    protected static final String DEFAULT_RESOURCE_METRIC = "/analyzer/metric";
    protected static final String DEFAULT_RESOURCE_ISSUE_METRIC = "/analyzer/issuemetric" ;

    protected static final String ES_URL = "http://localhost:9200/";

    protected String resource;

    protected RESTClient client;

    protected Class<T> inferedClass;
    
    /**
     * Debo pasar la clase como parametro debido a que los tipos genericos
     * desaparacen en tiempo de compilacion (Se usan como casteos implicitos).
     * Pero Java no guarda la informacion de tipos generado en tiempo de
     * compilacion.
     * 
     * Type erasure:
     * http://docs.oracle.com/javase/tutorial/java/generics/erasure.html
     * 
     * @param inferedClass
     */
    public ElasticsearchDao( Class<T> inferedClass, String resource ) {
        this.inferedClass = inferedClass;
        this.resource = resource;
        try {
            client = new RESTClient( ES_URL );
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void create( String id, T object ) {
        try {
            client.PUT( UriBuilder.fromPath( resource ).path( id ).toString(), object );
        } catch ( Exception e ) {
            LOG.log( Level.SEVERE, e.toString(), e );
        } 
    }

    @Override
    public void update( String id, T object ) {
        try {
            client.POST( UriBuilder.fromPath( resource ).path( id ).toString(), object );
        } catch ( Exception e ) {
            LOG.log( Level.SEVERE, e.toString(), e );
        }
    }

    @Override
    public T read( String id ) {
        try {
            return client.GET( UriBuilder.fromPath( resource ).path( id ).path( SOURCE ).toString() )
                            .readEntity( inferedClass );
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

    public List<T> readAll() {
        String scrollId = scan();
        Map<String, String> param = new HashMap<String, String>();
        param.put( "scroll", "1m" );
        param.put( "scroll_id", scrollId );
        try {
            ArrayNode jsonResponse = (ArrayNode) client.GET( UriBuilder.fromPath( QUERY ).path( SCROLL ).toString(), param )
                                                         .readEntity( JsonNode.class ).get( "hits" ).get( "hits" );
            return arrayJsonToList( jsonResponse );
        } catch (Exception e) {
            LOG.log( Level.SEVERE, e.toString(), e );
            return new ArrayList<T>();
        }
    }

    public List<String> readAllKeys() {
        String scrollId = scan();
        Map<String, String> param = new HashMap<String, String>();
        param.put( "scroll", "1m" );
        param.put( "scroll_id", scrollId );
        param.put( "fields", "" );
        try {
            ArrayNode jsonResponse = (ArrayNode) client.GET( UriBuilder.fromPath( QUERY ).path( SCROLL ).toString(), param )
                                                         .readEntity( JsonNode.class ).get( "hits" ).get( "hits" );
            return extractKeys( jsonResponse );
        } catch (Exception e) {
            LOG.log( Level.SEVERE, e.toString(), e );
            return null;
        }
    }

    public boolean exists( String id ) {
        try {
            return (client.GET( UriBuilder.fromPath( resource ).path( id ).path( SOURCE ).toString() ).getStatus() == RESP_OK);
        } catch (Exception e) {
            LOG.log( Level.SEVERE, e.toString(), e );
            return false;
        }
    }
    
    public List<T> search( String query ){
        try {
             ArrayNode arrayNode = (ArrayNode) client.POST( UriBuilder.fromPath( resource ).path( QUERY ).toString(), query )
                                                       .readEntity( JsonNode.class).get( "hits" ).get( "hits" ) ;
             return arrayJsonToList( arrayNode );
        } catch (Exception e) {
            LOG.log( Level.SEVERE, e.toString(), e );
            return new ArrayList<T>();
        }
    }
    

    public int getSize() {
        // Se buscan todos los datos sin cuerpo para que devuelva cuantos hay en
        // total
        Map<String, String> param = new HashMap<String, String>();
        param.put( "search_type", "count" );
        try {
            JsonNode jsonResponse = client.GET( UriBuilder.fromPath( resource ).path( QUERY ).toString(), param )
                                           .readEntity( JsonNode.class );
            return jsonResponse.get( "hits" ).get( "total" ).asInt();
        } catch (Exception e) {
            LOG.log( Level.SEVERE, e.toString(), e );
            return 0;
        }
    }

    private String scan() {
        Map<String, String> param = new HashMap<String, String>();
        param.put( "search_type", "scan" );
        param.put( "scroll", "1m" );
        try {
            JsonNode jsonResponse = client.GET( UriBuilder.fromPath( resource ).path( QUERY ).toString(), param )
                                            .readEntity( JsonNode.class );
            return jsonResponse.get( "_scroll_id" ).asText();
        } catch (Exception e) {
            LOG.log( Level.SEVERE, e.toString(), e );
            return null;
        }
    }

    private List<String> extractKeys( ArrayNode arrayNode ) {
        Iterator<JsonNode> it = null;
        List<String> keys = new ArrayList<String>();

        try {
            it = arrayNode.elements();
        } catch (Exception e) {
            LOG.log( Level.SEVERE, e.toString(), e );
            return null;
        }

        while (it.hasNext()) {
            try {
                keys.add( ((JsonNode) it.next()).get( "_id" ).asText() );
            } catch (Exception e) {
                LOG.log( Level.SEVERE, e.toString(), e );
                return null;
            }
            it.remove();
        }
        return keys;
    }

    /**
     * Cambia la representacion desde JSON a List<T>. Dado un Json que
     * represente un Array. Devuelve todos los elementos del Array en una lista
     * parametrizada. Este metodo funcionara siempre y cuando el Json este bien
     * formado y los elementos del Array puedan mapearse a la clase
     * parametrizada.
     * 
     * @param arrayNode
     * @return lista parametrizada que representa el JSON ingresado como
     *         parametro.
     */
    private List<T> arrayJsonToList( ArrayNode arrayNode ) {
        ObjectMapper mapper = new ObjectMapper();
        Iterator<JsonNode> it = null;
        List<T> elements = new ArrayList<T>();

        try {
            it = arrayNode.elements();
        } catch (Exception e) {
            LOG.log( Level.SEVERE, e.toString(), e );
            return new ArrayList<T>();
        }

        while (it.hasNext()) {
            JsonNode j = ((JsonNode) it.next()).get( "_source" );
            try {
                T elem = mapper.readValue( j.toString(), inferedClass );
                elements.add( elem );
            } catch (Exception e) {
                LOG.log( Level.SEVERE, e.toString(), e );
                return new ArrayList<T>();
            }
            it.remove();
        }
        return elements;
    }

}