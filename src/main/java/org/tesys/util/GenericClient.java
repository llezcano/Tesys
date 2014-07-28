package org.tesys.util;

import java.util.Map;

/**
 * Interfaz para un cliente HTTP generico
 * 
 * @author rulo
 *
 */
public interface GenericClient {
	
	public String GET( String resource, Map<String, String> params ) ;
	
	public String GET( String resource ) ;
	
	public String PUT( String resource, String JSON ) ;
	
	public String POST( String resource, String JSON ) ;
	
	public String DELETE( String resource ) ;

}
