package org.tesys.util;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;


public class SearchJSONClient extends GenericJSONClient {

  public SearchJSONClient(String url) {
    super(url);
  }

  public String SEARCH(String resource, String query) {
    Response response =
        client.target(URL).path(resource).request(JSONMediaType).post(Entity.entity(query, JSONMediaType));

    return response.readEntity(String.class);

  }
}
