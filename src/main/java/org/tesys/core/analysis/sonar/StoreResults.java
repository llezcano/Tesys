package org.tesys.core.analysis.sonar;

import org.tesys.core.db.DatabaseFacade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;


public class StoreResults {

  private DatabaseFacade db;

  private static StoreResults instance = null;

  private StoreResults() {
    db = new DatabaseFacade();
  }

  public static StoreResults getInstance() {
    if (instance == null) {
      instance = new StoreResults();
    }
    return instance;
  }


  public void storeMetrics(List<MetricPOJO> metrics) {

    for (MetricPOJO metric : metrics) {
      // db.PUT(index, dtype, id, data) TODO
    }

  }

  public void storeAnalysis(List<Map<String, String>> resultados) {

    for (Map<String, String> map : resultados) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        String json = mapper.writeValueAsString(map);
        System.out.println(json);// TODO
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }

    }

    // db.PUT(index, dtype, id, data) TODO

  }

  // DELETE
  /*
   * public StoreResults( List<Map<String, String>> resultados) {
   * 
   * 
   * 
   * String json = JSONValue.toJSONString(map); json = json.replace(' ', '_');
   * 
   * HttpClient httpClient = new DefaultHttpClient();
   * 
   * try { HttpPost request = new HttpPost( db ); StringEntity params =new StringEntity( json );
   * request.addHeader("content-type", "application/json"); request.setEntity(params);
   * httpClient.execute(request);
   * 
   * // handle response here... }catch (Exception ex) { // handle exception here } finally {
   * httpClient.getConnectionManager().shutdown(); }
   * 
   * }
   * 
   * }
   */

}
