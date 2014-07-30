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
import org.sonar.wsclient.services.TimeMachine;
import org.sonar.wsclient.services.TimeMachineCell;
import org.sonar.wsclient.services.TimeMachineColumn;
import org.sonar.wsclient.services.TimeMachineQuery;

class SonarExtractor {

  TimeMachineCell[] tmc;
  TimeMachineColumn[] tmco;
  List<String> revisions;

  public SonarExtractor(String sonarHost, String proyectKey, List<String> revisions) {
    this.revisions = revisions;

    Sonar sonar = new Sonar(new HttpClient4Connector(new Host(sonarHost)));
    List<Metric> lm = sonar.findAll(MetricQuery.all());
    String[] met = new String[lm.size()];
    int index = 0;
    for (Metric metric : lm) {
      met[index++] = metric.getKey();
    }

    TimeMachine struts = sonar.find(TimeMachineQuery.createForMetrics(proyectKey, met));
    tmc = struts.getCells();
    tmco = struts.getColumns();

  }


  // devuelve una lista en la que cada elemento es un hashmap que tiene los resultados
  // del sonar para dicha revision
  public List<Map<String, String>> getResults() {

    List<Map<String, String>> resultados = new LinkedList<Map<String, String>>();


    for (int j = 0; j < tmc.length; j++) {

      Object[] v = tmc[j].getValues();

      Map<String, String> resultadoDeRevision = new HashMap<String, String>();
      resultadoDeRevision.put("revision", revisions.get(j));

      for (int i = 0; i < tmco.length; i++) {
        try {
          resultadoDeRevision.put(tmco[i].getMetricKey(), v[i].toString());
        } catch (Exception e) {
          resultadoDeRevision.put(tmco[i].getMetricKey(), "null");
        }
      }

      resultados.add(resultadoDeRevision);
    }

    return resultados;
  }

}
