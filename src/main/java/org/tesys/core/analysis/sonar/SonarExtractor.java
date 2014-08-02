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


  public static String HOST = "http://localhost:9000"; // de sonar
  public static String PROJECT_KEY = "temporal:test";

  private static final String USER = "admin";
  private static final String PASS = "admin";

  public SonarExtractor() {

    sonar = new Sonar(new HttpClient4Connector(new Host(HOST, USER, PASS)));
    metricList = sonar.findAll(MetricQuery.all());

  }

  public List< AnalisisPOJO > getResults( RevisionPOJO [] revisions ) {

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

      AnalisisPOJO resultadoDeRevision = new AnalisisPOJO();
      
      resultadoDeRevision.setDate( revisions[j].getDate() );
      resultadoDeRevision.setProject_tracking_task(revisions[j].getProject_tracking_task());
      resultadoDeRevision.setRepository(revisions[j].getRepository());
      resultadoDeRevision.setRevision(revisions[j].getRevision());
      resultadoDeRevision.setScm_user(revisions[j].getScm_user());
      

      for (int i = 0; i < tmco.length; i++) {
        KeyValuePOJO kvp = new KeyValuePOJO();
        
        try {
          kvp.key = tmco[i].getMetricKey();
          kvp.value = v[i].toString();
          resultadoDeRevision.add( kvp );
        } catch (Exception e) {
          kvp.key = tmco[i].getMetricKey();
          kvp.value = "null";
          resultadoDeRevision.add( kvp );
        }
      }

      sonar.delete(ProjectDeleteQuery.create(PROJECT_KEY));
      resultados.add(resultadoDeRevision);
    }

    return resultados;
  }


  public List<MetricPOJO> getMetrics() {

    List<MetricPOJO> metrics = new LinkedList<MetricPOJO>();
    for (Metric m : metricList) {
      metrics.add(new MetricPOJO(m.getKey(), m.getName(), m.getDescription(), m.getType(), m
          .getDomain()));
    }

    return metrics;
  }

}
