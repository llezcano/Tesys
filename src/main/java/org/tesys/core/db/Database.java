package org.tesys.core.db;

import java.net.MalformedURLException;

import org.tesys.util.RESTClient;


/**
 * SCRUD for data persistence.
 * 
 * @author rulo
 * 
 */
public class Database {
<<<<<<< HEAD
    
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
    
=======

  public void main(String args[]) {
    Database d = new Database();
    d.foo();
  }

  public Database() {}

  public void foo() {

  }

>>>>>>> 7803100da418cca539277ec86b6d32182af00727
}
