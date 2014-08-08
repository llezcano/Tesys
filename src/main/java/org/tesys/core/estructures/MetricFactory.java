package org.tesys.core.estructures;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MetricFactory {

  
  /*public static void main(String[] args) {
    new MetricFactory();
  }
  
  public MetricFactory() {
    IValue mul = 
        new Division(
        new Multiplicacion( new SimpleValue("lines"), new SimpleValue("bugs")),
        
        new Multiplicacion( new SimpleValue("lines"), new SimpleValue("bugs")));
  
    
    Metric lineasPorBugs = new Metric("linbug", "lineas por bugs", 
        "lineas de codigo por cantidad de bugs", "User Made", mul);
    
    System.out.println(  getMetric(lineasPorBugs.toString()) );
    
  }*/
  
  public Metric getMetric(String jsonFormat) {
    
    ObjectMapper mapper = new ObjectMapper();
    JsonNode o = null;
    try {
      o = mapper.readTree(jsonFormat);
    } catch (IOException e) {}
    
    IValue v = getValue(o.get("value"));
    
    return new Metric(o.get("key").asText(), o.get("nombre").asText(), o.get("descripcion").asText(), 
        o.get("procedencia").asText(), v);
    
    
  }
  
  
  
  public IValue getValue( JsonNode json ) {

       if (json.isContainerNode()) {
         JsonNode j = json.elements().next();
         if( j.isArray() ) {
           //nodo
           IValue v1 = getValue( j.get(0) );
           IValue v2 = getValue( j.get(1) );
           try {
           return (IValue) Class.forName(
               "org.tesys.core.estructures." + json.fieldNames().next().toString() 
               ).getConstructors()[0].newInstance(v1,v2
           );
           } catch (Exception e) {}

         } else if (!json.isArray()) {
           //hoja
           try {
            return (IValue) Class.forName(
                 "org.tesys.core.estructures." + json.fieldNames().next().toString() 
                 ).getConstructors()[0].newInstance(json.elements().next().asText()
             );
          } catch (Exception e) {}
    
         }
         
       }

    
    return null;
  }
  
  
}
