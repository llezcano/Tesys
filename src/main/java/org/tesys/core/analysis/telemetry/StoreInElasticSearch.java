package org.tesys.core.analysis.telemetry;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;

public class StoreInElasticSearch {

  private static final String DATAKEY_KEY = Messages.getString("senderkey"); //$NON-NLS-1$
  private static final String DATATYPE_KEY = Messages.getString("senderdata"); //$NON-NLS-1$
  private static final String INDEX_KEY = Messages.getString("senderapp"); //$NON-NLS-1$
  private static final String PROTOCOL = "http"; //$NON-NLS-1$

  private HttpURLConnection conn;
  private URI uri;
  private String host, path, index, dataType;
  private int port;

  public StoreInElasticSearch(String host, int port, String path, String app, String dtype) {
    this.host = host;
    this.path = path;
    this.index = app;
    this.dataType = dtype;
    this.port = port;

  }

  public void send(String key, String json) {
    try {
      uri =
          new URIBuilder().setScheme(PROTOCOL).setHost(host).setPort(port).setPath(path)
              .addParameter(INDEX_KEY, index).addParameter(DATATYPE_KEY, dataType)
              .addParameter(DATAKEY_KEY, key).build();

      conn = (HttpURLConnection) uri.toURL().openConnection();

      conn.setDoOutput(true);
      conn.setRequestMethod("POST"); //$NON-NLS-1$
      conn.addRequestProperty("Content-Type", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
      conn.setRequestProperty("Accept", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
      OutputStreamWriter out;
      out = new OutputStreamWriter(conn.getOutputStream());
      out.write(json);
      out.flush();
      out.close();
      if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
        System.err.println(key + " " + HttpURLConnection.HTTP_OK);
      }

    } catch (URISyntaxException e) {
      System.err.println(Messages.getString("sendererror") + e.getMessage()); //$NON-NLS-1$
    } catch (ProtocolException e0) {
      System.err.println(Messages.getString("sendererror") + e0.getMessage()); //$NON-NLS-1$
    } catch (MalformedURLException e1) {
      System.err.println(Messages.getString("sendererror") + e1.getMessage()); //$NON-NLS-1$
    } catch (IOException e2) {
      System.err.println(Messages.getString("sendererror") + e2.getMessage()); //$NON-NLS-1$
    }

  }

}
