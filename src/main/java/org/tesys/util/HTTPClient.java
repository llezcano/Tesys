package org.tesys.util;

import java.net.MalformedURLException;
import java.util.Map;

import javax.ws.rs.core.Response;

/**
 * Interfaz para Cliente HTTP
 * 
 * @author rulo
 * 
 */
public interface HTTPClient {

  public String getURL();

  public void setURL(String url) throws MalformedURLException;

  public Response GET(String resource, Map<String, String> params);

  public Response GET(String resource);

  public Response PUT(String resource, String JSON);

  public Response POST(String resource, String JSON);

  public Response DELETE(String resource);

}
