package org.tesys.util;

import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.MalformedURLException;
import java.net.URL;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 * Cliente para Servicios Web REST el cual usa Jersey.
 * 
 * @author rulo
 * 
 */
public class RESTClient implements HTTPClient {

  private MediaType type = MediaType.APPLICATION_JSON_TYPE;

  private URL URL;
  private HttpAuthenticationFeature auth;
  private Client client;

  /**
   * Main for test class and examples
   * 
   * @param args
   */
  /*
    public static void main(String args[]) {
    
        try { 
            RESTClient myClient = new RESTClient("http://localhost:9200/"); 
            UserPOJO u = new UserPOJO() ;
            u.setActive(true);
            u.setDisplayName("pepeestareloco");
            u.setEmailAddress("pepe@pepin");
            u.setName("name");

            Response response = myClient.POST("/a/b/c", u);
           
            //La case UserPOJO debe ser mappeable desde JSON. Lo cual lo logro con @XmlRootElement.
      	    
      	           
      	    System.out.println(u) ;
        
        } catch (MalformedURLException e) { // TODO Auto-generated catch block e.printStackTrace();
        System.exit(0); }
        
    }
   */
  
  /**
   * Client constructor without authorization
   * 
   * @param url
   * @throws MalformedURLException
   */
  public RESTClient(String url) throws MalformedURLException {
    URL = new URL(url);
    auth = null;
    client = ClientBuilder.newClient();
  }

  /**
   * Client constructor with authorization
   * 
   * @param url
   * @throws MalformedURLException
   */
  public RESTClient(String url, String user, String pass) throws MalformedURLException {
    URL = new URL(url);
    auth = HttpAuthenticationFeature.basic(user, pass);
    client = ClientBuilder.newClient();
    // Setting client authorization
    client.register(auth);
  }
  
  public String getURL() {
    return URL.toString();
  }

  public void setURL(String url) throws MalformedURLException {
    URL = new URL(url);
  }

  public Response GET(String resource, Map<String, String> params) {
    if (params == null)
      return this.GET(resource);

    WebTarget target = client.target(getURL()).path(resource);
    // adding parameters
    for (Map.Entry<String, String> param : params.entrySet())
      target = target.queryParam(param.getKey(), param.getValue());

    // making request
    return target.request(this.type).get();
  }

  public Response GET(String resource) {

    return client.target(this.getURL()).path(resource).request(this.type).get();

  }

  public Response PUT(String resource, Object serealizable) {

    return client.target(this.getURL()).path(resource).request(this.type)
        .put(Entity.entity(serealizable, this.type));

  }

  public Response POST(String resource, Object serealizable) {

    return client.target(this.getURL()).path(resource).request(this.type)
        .post(Entity.entity(serealizable, this.type));

  }

  public Response DELETE(String resource) {

    return client.target(this.getURL()).path(resource).request(this.type).delete();

  }

}
