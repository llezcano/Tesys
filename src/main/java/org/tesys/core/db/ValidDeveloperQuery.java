package org.tesys.core.db;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.tesys.core.project.scm.MappingPOJO;

public class ValidDeveloperQuery implements GenericQuery<Boolean> {
    
    private String repository = "" ;
    private String scmUser = "" ;
    private static final Logger LOG = Logger.getLogger( ValidDeveloperQuery.class.getName() );
    private ElasticsearchDao<MappingPOJO> dao ;  

    public ValidDeveloperQuery( ElasticsearchDao<MappingPOJO> dao, String scmUser, String repository ) {
        this.dao = dao ;
        this.scmUser = scmUser ;
        this.repository = repository ;
    }
    
    public ValidDeveloperQuery( ElasticsearchDao<MappingPOJO> dao) {
        this.dao = dao ;
    }
    
    @Override
    public Boolean execute() {
        String query = "{ \"query\": { \"bool\": { \"must\": [ { \"match\": { \"scmUser\": \"" + scmUser + "\" }}, { \"match\": {\"repository\": \"" + repository + "\" }} ] } } }";
        try {            
            return ! dao.search( query ).isEmpty() ;
        } catch (Exception e) {
            LOG.log( Level.SEVERE, e.toString(), e );
            return false;            
        }
    }


    public String getRepository() {
        return repository;
    }

    public void setRepository( String repository ) {
        this.repository = repository;
    }

    public String getScmUser() {
        return scmUser;
    }

    public void setScmUser( String scmUser ) {
        this.scmUser = scmUser;
    }
}