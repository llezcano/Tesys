package org.tesys.core.estructures;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MetricFactory {

  
  public static void main(String[] args) {
    new MetricFactory();
    
    
  }
  
  public MetricFactory() {
    IValue mul = new Multiplicacion( new SimpleValue("lines"), new SimpleValue("bugs"));
  

    Metric lineasPorBugs = new Metric("linbug", "lineas por bugs", 
        "lineas de codigo por cantidad de bugs", "User Made", mul);
    //en db
    //getMetric(lineasPorBugs.toString());
 
    getMetric("{\"class\":\"org.tesys.core.estructures.Multiplicacion\",\"value\":{\"Multiplicacion\":[{\"class\":\"org.tesys.core.estructures.SimpleValue\", \"SimpleValue\":\"lines\"},{\"class\":\"org.tesys.core.estructures.SimpleValue\"\"SimpleValue\":\"bugs\"}]}}") ;
  
  }
  
  public Metric getMetric(String jsonFormat) {
    System.out.println(jsonFormat) ;
    ObjectMapper mapper = new ObjectMapper();
    JsonNode actualObj = null;
    
    try {

      actualObj = mapper.readTree(jsonFormat);
    } catch (IOException e) {        }
    
    System.out.println(actualObj.get( "class" )) ;
    IValue v = getValue(actualObj.get("value"));

    
    
    return null;
  }
  
  public IValue getValue( JsonNode json ) {
    
    return null;
  }
  
  
}
