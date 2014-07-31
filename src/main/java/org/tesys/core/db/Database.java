package org.tesys.core.db;

import java.net.MalformedURLException;

import org.tesys.util.RESTClient;


/**
 * SCRUD for data persistence. 
 * @author rulo
 *
 */
public class Database {
    
    public RESTClient client ;
   
    public void main( String args[] ) {
	Database d = new Database() ;
	d.foo() ;
    }
    
    public String getURL() {
	return "http://localhost:8091/" ; 
    } 
    
    public Database() {
	try {
	    client = new RESTClient(getURL()) ;
	} catch (MalformedURLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
    
    public void foo() {
	
    }
    
}
