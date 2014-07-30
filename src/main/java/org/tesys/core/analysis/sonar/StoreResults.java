package org.tesys.core.analysis.sonar;

import java.util.List;
import java.util.Map;

class StoreResults {
  
  //private String db = "http://localhost:8080/Sender/SenderService?app=sonar&data=snapshot";

  
  public StoreResults( List<Map<String, String>> resultados) {
      
     /* for (Map<String, String> map : resultados) {
          
          String json = JSONValue.toJSONString(map);
          json = json.replace(' ', '_');

          HttpClient httpClient = new DefaultHttpClient();

          try {
              HttpPost request = new HttpPost( db );
              StringEntity params =new StringEntity( json );
              request.addHeader("content-type", "application/json");
              request.setEntity(params);
              httpClient.execute(request);

              // handle response here...
          }catch (Exception ex) {
              // handle exception here
          } finally {
              httpClient.getConnectionManager().shutdown();
          }
          
      }*/
      
  }

}