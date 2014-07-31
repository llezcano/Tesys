package org.tesys.core.db;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Esta clase es para el mapeo SCM - Jira
 * 
 * @author rulo
 *
 */

@XmlRootElement
public class SCMProjectMappingPOJO {
    
    private String project_tracking_user ;
    
    private String scm_user ;
    
    private String repository ;

    public SCMProjectMappingPOJO() {}
    
    public SCMProjectMappingPOJO( String pUser, String sUser, String repo) {
	project_tracking_user = pUser ;
	scm_user = sUser;
	repository = repo ;
    }
    
    @Override
    public String toString() {
	return "SCMProjectMappingPOJO [project_tracking_user="
		+ project_tracking_user + ", scm_user=" + scm_user
		+ ", repository=" + repository + "]";
    }

    public String getProjectTrackingUser() {
	return project_tracking_user;
    }

    public void setProjectTrackingUser(String project_tracking_user) {
	this.project_tracking_user = project_tracking_user;
    }

    public String getScmUser() {
	return scm_user;
    }

    public void setScmUser(String scm_user) {
	this.scm_user = scm_user;
    }

    public String getRepository() {
	return repository;
    }

    public void setRepository(String repository) {
	this.repository = repository;
    }

}
