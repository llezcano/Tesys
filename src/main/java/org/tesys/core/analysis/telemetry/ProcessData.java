package org.tesys.core.analysis.telemetry;

import java.util.List;

import org.tesys.core.db.ElasticsearchDao;
import org.tesys.core.estructures.Issue;
import org.tesys.core.estructures.Metric;
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

  private ElasticsearchDao<Issue> daoi;
  private ElasticsearchDao<Metric> daom;

  private static ProcessData instance = null;

  private ProcessData() {
    daoi = new ElasticsearchDao<Issue>(Issue.class, ElasticsearchDao.DEFAULT_RESOURCE_ISSUE_METRIC);
    daom = new ElasticsearchDao<Metric>(Metric.class, ElasticsearchDao.DEFAULT_RESOURCE_METRIC);
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

    for( String key : pt.getIssuesKeys() ) {
      Issue issueActual = new Issue( key );
      Issue issueFinal = aggregator.agregateMetrics(issueActual);
      daoi.create( issueFinal.getIssueId(), issueFinal);
    }
    
    /*List<Metric> metrics = aggregator.getMetricsID();
    
    for (Metric metric : metrics) {
      daom.create(metric.getKey(), metric);
    }*/
    
    //TODO crear los developers

  }

}
