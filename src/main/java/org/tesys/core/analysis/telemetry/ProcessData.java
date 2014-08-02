package org.tesys.core.analysis.telemetry;


import java.util.Collections;
import java.util.List;

import org.tesys.core.analysis.sonar.AnalisisPOJO;
import org.tesys.core.analysis.sonar.MetricPOJO;
import org.tesys.core.db.Database;
import org.tesys.core.project.tracking.IssuePOJO;
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
//TODO testear todo
//TODO analizar si se puede hacer una forma mas facil de agregar nuevos valores 
  //(por ejemplo si se quiere agregar la wiki, aca habria que hacer bastante)


  public void executeProcessor() {
    
    
    List<AnalisisPOJO> analisis = db.getAnalisis();
    
    List<MetricPOJO> metrics = db.getMetrics();

    Collections.sort(analisis); //TODO ver si se ordenan por fecha

    SonarAnalisis sonarAnalisis = new SonarAnalisis( metrics );

    // Se obtienen los analisis por commit individual
    List<AnalisisPOJO> analisisPorCommit = sonarAnalisis.getAnalisisPorCommit(analisis);

    //TODO guaradr en la base de datos que analisis fueron agregados a la tarea
    
    // se obtienen los analisis por tarea
    List<AnalisisPOJO> analisisPorTarea =
        sonarAnalisis.getAnalisisPorTarea( analisisPorCommit );

    ProjectTrackingRESTClient pt = new ProjectTrackingRESTClient();
    
    for (AnalisisPOJO analisisDeTarea : analisisPorTarea) {
      IssuePOJO IP = pt.getIssue( analisisDeTarea.getProject_tracking_task() );
      //TODO juntar el issuePOJO con analisisDeTarea
      
      //db.store( , ); TODO el BigPOJO
    }

  }


}
