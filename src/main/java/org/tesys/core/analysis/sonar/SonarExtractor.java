package org.tesys.core.analysis.sonar;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

public class SonarExtractor {

  private Sonar sonar;
  private List<Metric> metricList;


  public static String host = "http://localhost:9000"; // de sonar
  public static String proyectKey = "temporal:test";

  private static final String user = "admin";
  private static final String pass = "admin";

  public SonarExtractor() {

    sonar = new Sonar(new HttpClient4Connector(new Host("http://localhost:9000", user, pass)));
    metricList = sonar.findAll(MetricQuery.all());

  }

  // devuelve una lista en la que cada elemento es un hashmap que tiene los resultados
  // del sonar para dicha revision
  public List<Map<String, String>> getResults(List<String> revisions) {

    String[] met = new String[metricList.size()];
    int index = 0;
    for (Metric metric : metricList) {
      met[index++] = metric.getKey();
    }

    TimeMachine struts = sonar.find(TimeMachineQuery.createForMetrics(proyectKey, met));
    TimeMachineCell[] tmc = struts.getCells();
    TimeMachineColumn[] tmco = struts.getColumns();

    List<Map<String, String>> resultados = new LinkedList<Map<String, String>>();


    for (int j = 0; j < tmc.length; j++) {

      Object[] v = tmc[j].getValues();

      Map<String, String> resultadoDeRevision = new HashMap<String, String>();
      resultadoDeRevision.put("revision", revisions.get(j));
      // resultadoDeRevision.put("revision", revisions.get(j));


      for (int i = 0; i < tmco.length; i++) {
        try {
          resultadoDeRevision.put(tmco[i].getMetricKey(), v[i].toString());
        } catch (Exception e) {
          resultadoDeRevision.put(tmco[i].getMetricKey(), "null");
        }
      }

      sonar.delete(ProjectDeleteQuery.create(proyectKey));
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
