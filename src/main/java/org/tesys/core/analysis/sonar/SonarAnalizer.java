package org.tesys.core.analysis.sonar;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.tesys.core.db.DatabaseFacade;
import org.tesys.core.project.scm.SCMManager;
import org.tesys.util.MD5;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Path("sonar")
public class SonarAnalizer {
  
  
  //TODO esto obliga a que cada proyecto tenga que tener un archivo build.xml en la raiz del proyecto
  private static final File buildFile = new File(System.getProperty("user.home"), ".tesys/build.xml");
  private static final File workspace = new File(System.getProperty("user.home"), ".tesys/workspace");
  
  private static String host = "http://localhost:9000"; //de sonar
  private static String proyectKey = "temporal:test";
  
  
  @GET
  public String test() {
    //TODO hay que hacerlo multirepositorio
    
    //TODO hay que descartar las que ya fueron analizadas
    DatabaseFacade db = new DatabaseFacade();
    
    
    String revisionsFromSCM = db.POST("scm", "revisions", "{ \"_source\":\"revision\"}");
    String revisionsAlredyAnalized = db.POST("sonar", "revisions", "{ \"_source\":\"revision\"}");
    //Espera algo como
    //{"results":[{"revision":"bar"},{"revision":"foo"}]}
    
    
    
    
    
    JsonFactory factory = new JsonFactory();
    ObjectMapper mapper = new ObjectMapper(factory);
    JsonNode jsonQuery = null;
    try {
      jsonQuery = mapper.readTree( revisionsFromSCM );
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    Iterator<JsonNode> it = jsonQuery.get("results").iterator();
    
    List<String> revisions = new LinkedList<>();
    
    while( it.hasNext() ) {
      revisions.add(it.next().get("revision").asText());
    }
    
    
    SCMManager scm = SCMManager.getInstance();
  
    for (String rev : revisions) {
      if( rev.equals("0") ) {
        purgeDirectory(workspace);
      } else {
        scm.doCheckout(rev, "");
      }
      analizar();
            
      String id = MD5.generateId(rev);

      ObjectNode data = mapper.createObjectNode();
      data.put("revision", rev);
      //data.put("repository", ...);

      db.PUT("sonar", "revisions", id, data.toString()); //revisiones analizadas
      
      
    }
    
    SonarExtractor se = new SonarExtractor(host, proyectKey, revisions);
    new StoreResults(se.getResults()); //TODO
    
    purgeDirectory(workspace);
    

    return "hecho";
  }
  
  
  /**
   * Executa una tarea ant ubicada en buildFile
   */
  private void analizar() {
    Project p = new Project();
    p.setUserProperty("ant.file", buildFile.getAbsolutePath());
    p.init();
    ProjectHelper helper = ProjectHelper.getProjectHelper();
    p.addReference("ant.projectHelper", helper);
    helper.parse(p, buildFile);
    p.executeTarget(p.getDefaultTarget());
  }
  
  
  /**
   * Dado un directorio elimina el contenido pero no el directorio
   */
  void purgeDirectory(File dir) {
    for (File file: dir.listFiles()) {
        if (file.isDirectory()) purgeDirectory(file);
        file.delete();
    }
}
  
  
  

}
