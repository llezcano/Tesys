package org.tesys.core.analysis.telemetry;


import java.util.Collections;
import java.util.List;

import org.tesys.core.analysis.sonar.AnalisisPOJO;
import org.tesys.core.analysis.sonar.MetricPOJO;
import org.tesys.core.db.Database;
import org.tesys.core.project.tracking.Issue;
import org.tesys.core.project.tracking.ProjectTrackingRESTClient;



public class ProcessData {

  private Database db;

  private static ProcessData instance = null;

  private ProcessData() {
    db = new Database();
  }

  public static ProcessData getInstance() {
    if (instance == null) {
      instance = new ProcessData();
    }
    return instance;
  }

//TODO agregar los datos de cuanto trabajo por hora

//TODO analizar si se puede hacer una forma mas facil de agregar nuevos valores 
//(por ejemplo si se quiere agregar la wiki, aca habria que hacer bastante)
  


  public void executeProcessor() {
    
    /*ProjectTrackingRESTClient pt = new ProjectTrackingRESTClient();
    
    for (AnalisisPOJO analisisDeTarea : analisisPorTarea) {
      Issue issue = pt.getIssue( analisisDeTarea.getRevision().getProjectTrackingTask() );
      //TODO juntar el issuePOJO con analisisDeTarea
      
      //db.store( , ); TODO el BigPOJO
    }*/

  }


}
