package org.tesys.util;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.tesys.core.analysis.sonar.AnalisisPOJO;
import org.tesys.core.analysis.sonar.KeyValuePOJO;
import org.tesys.core.project.scm.MappingPOJO;
import org.tesys.core.project.scm.RevisionPOJO;

/**
 * Clase de pruebas, eliminar cuando no haya que testear mas
 * @author arian
 *
 */

@Path("/sida")
public class End {
  
  @PUT
  @Path("/sida")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public AnalisisPOJO store( AnalisisPOJO d ) {
    System.out.println( d.getResults().get(0).getKey() );
    
    AnalisisPOJO a = new AnalisisPOJO( new RevisionPOJO(0,"asd","asd","asd","asd"));
    a.add( new KeyValuePOJO("aids", "re piola") );
    
    return a;
  }
  
}


class Main {
  
  public Main() {
    RESTClient client = null;
    try {
      client = new RESTClient("http://localhost:8080/core/rest/");
    } catch (MalformedURLException e) {}
    
    AnalisisPOJO a1 = new AnalisisPOJO( new RevisionPOJO(0,"CERO","asd","asd","asd"));
    a1.add( new KeyValuePOJO("sida", "re piola") );
    
    AnalisisPOJO a2 = new AnalisisPOJO( new RevisionPOJO(1,"UNO","asd","asd","asd"));
    a2.add( new KeyValuePOJO("sida", "re piola") );
    
    AnalisisPOJO a = new AnalisisPOJO( new RevisionPOJO(2,"DOS","asd","asd","asd"));
    a.add( new KeyValuePOJO("sida", "re piola") );
    
    List<AnalisisPOJO> l = new LinkedList<AnalisisPOJO>();
    l.add(a);
    l.add(a1);
    l.add(a2);
    
    for (AnalisisPOJO analisisPOJO : l) {
      System.out.println(analisisPOJO.getRevision().getScmUser());
    }
    
    Collections.sort(l);
    
    
    for (AnalisisPOJO analisisPOJO : l) {
      System.out.println(analisisPOJO.getRevision().getScmUser());
    }
    
   /* System.out.println(
        client.PUT("sida/sida", a).readEntity(AnalisisPOJO.class).getResults().get(0).getKey()
    );*/
    
  }
  
  public static void main(String[] args) {
    new Main();
  }
}
