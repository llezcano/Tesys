package org.tesys.core.analysis.sonar;

import java.util.LinkedList;
import java.util.List;
 
import org.sonar.wsclient.Host;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.connectors.HttpClient4Connector;
import org.sonar.wsclient.services.Metric;
import org.sonar.wsclient.services.MetricQuery;
import org.sonar.wsclient.services.ProjectDeleteQuery;
import org.sonar.wsclient.services.TimeMachine;
import org.sonar.wsclient.services.TimeMachineCell;
import org.sonar.wsclient.services.TimeMachineColumn;
import org.sonar.wsclient.services.TimeMachineQuery;
import org.tesys.core.project.scm.RevisionPOJO;

public class SonarExtractor {

  private Sonar sonar;
  private List<Metric> metricList;
 

  //TODO hacer esto con variable asi se puede cambiar en vez de constantes

  public static final String HOST = "http://localhost:9000"; // de sonar
  public static final String PROJECT_KEY = "temporal:test";

  private static final String USER = "admin";
  private static final String PASS = "admin";

  public SonarExtractor() {

    sonar = new Sonar(new HttpClient4Connector(new Host(HOST, USER, PASS)));
    metricList = sonar.findAll(MetricQuery.all());

  }

  public List< AnalisisPOJO > getResults( List<RevisionPOJO> revisions ) {

    String[] met = new String[metricList.size()];
    int index = 0;
    for (Metric metric : metricList) {
      met[index++] = metric.getKey();
    }

    TimeMachine struts = sonar.find(TimeMachineQuery.createForMetrics(PROJECT_KEY, met));
    TimeMachineCell[] tmc = struts.getCells();
    TimeMachineColumn[] tmco = struts.getColumns();

    List< AnalisisPOJO > resultados = new LinkedList< AnalisisPOJO >();


    for (int j = 0; j < tmc.length; j++) {

      Object[] v = tmc[j].getValues();

      AnalisisPOJO resultadoDeRevision = new AnalisisPOJO(revisions.get(j));

      for (int i = 0; i < tmco.length; i++) {
        
        if( v[i] != null ) {
          resultadoDeRevision.add( new KeyValuePOJO(tmco[i].getMetricKey(), v[i].toString()) );
        } else {
          resultadoDeRevision.add( new KeyValuePOJO(tmco[i].getMetricKey(), "null") );
        }

      }

      try {
        sonar.delete(ProjectDeleteQuery.create(PROJECT_KEY));
      } catch(Exception e) {}
      
      resultados.add(resultadoDeRevision);
    }

    return resultados;
  }


  public List<SonarMetricPOJO> getMetrics() {

    List<SonarMetricPOJO> metrics = new LinkedList<SonarMetricPOJO>();
    for (Metric m : metricList) {
      metrics.add(new SonarMetricPOJO(m.getKey(), m.getName(), m.getDescription(), m.getType(), m
          .getDomain()));
    }

    return metrics;
  }

}
