package org.tesys.core.analysis.telemetry;

import org.tesys.core.db.Database;
import org.tesys.core.project.tracking.ProjectTracking;
import org.tesys.core.project.tracking.ProjectTrackingRESTClient;

/**
 * Esta clase, y en particular todo este pquete, es el encargado de recolectar
 * datos de todas partes del sistema y juntarlos todo en una unica unidad de trabajo
 * que va a ser identificada por el issue del project tracking y va a tener metricas
 * de todos los provedores que esten acoplados al sistema
 * 
 */


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

  
  public void executeProcessor() {
    
    ProjectTracking pt = new ProjectTrackingRESTClient();
    
    AggregatorFactory aggregatorFactory = new ConcreteAggregatorFactory();
    Aggregator aggregator = aggregatorFactory.getAggregator();
    //TODO getAnalisis(key jira)
    for( String key : pt.getIssuesKeys() ) {
      IssueMetrics issueActual = new IssueMetrics( key );
      IssueMetrics issueFinal = aggregator.agregateMetrics(issueActual);
      db.store( issueFinal.getIssueId(), issueFinal );
    }

  }

}
