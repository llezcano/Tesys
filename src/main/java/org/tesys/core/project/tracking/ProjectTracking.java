package org.tesys.core.project.tracking;

/**
 *  Interface para un facade de Project Tracking. El cual sera el proovedor de 
 *  Issues (tareas) y Users (desarrolladores) de un proyecto.
 *  
 * @author rulo
 *
 */

public interface ProjectTracking {
	
	public Issue[] getIssues() ;
	
	public boolean existUser( String key ) ;
	
	public User[] getUsers() ;
	
	public boolean existIssue( String key ) ;
	
}
