package org.tesys.core.analysis.telemetry;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.tesys.core.analysis.sonar.AnalisisPOJO;
import org.tesys.core.analysis.sonar.MetricPOJO;
import org.tesys.core.analysis.sonar.metricsdatatypes.Metrics;
import org.tesys.core.analysis.telemetry.dbutilities.DBUtilities;
import org.tesys.core.analysis.telemetry.util.Searcher;
import org.tesys.core.project.scm.RevisionPOJO;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SonarAnalisis {



  public SonarAnalisis(List<RevisionPOJO> revisions, List<MetricPOJO> metrics) {
    
  }


  public List<AnalisisPOJO> getAnalisisSonar() {


  }



  /**
   * Dada una lista de analisis del sonar, resta los datos de cada uno con los del anterior Para asi
   * obtener los metricas de ese unico commit y no de todo el proyecto entero
   * 
   * @param analisisAcumuladosJson Las analisis ejecutados por el sonar
   * @return los mismos analisis pero con la informacion relativa (o no acumulada)
   */
  public List<JsonNode> getAnalisisPorCommit(List<JsonNode> analisisAcumuladosJson) {

    List<JsonNode> analisisPorCommit = new LinkedList<JsonNode>();
    ObjectMapper mapper = new ObjectMapper();

    for (int i = 1; i < analisisAcumuladosJson.size(); i++) {
      JsonNode analisisAcumuladoPrevio = analisisAcumuladosJson.get(i - 1);
      JsonNode analisisAcumuladoActual = analisisAcumuladosJson.get(i);

      ObjectNode nuevoAnalisisPorCommit = mapper.createObjectNode();

      Iterator<JsonNode> anteriorValues = analisisAcumuladoPrevio.getElements();
      Iterator<JsonNode> actualValues = analisisAcumuladoActual.getElements();
      Iterator<String> actualMetrics = analisisAcumuladoActual.getFieldNames();


      while (actualMetrics.hasNext()) {

        Metrics metricHandler = null;
        String metricName = actualMetrics.next().toString();
        JsonNode valorAnalisisAcumuladoActual = actualValues.next();
        JsonNode valorAnalisisAcumuladoPrevio = anteriorValues.next();

        if (metricName.equals(DBUtilities.REVISION_TAG)) {
          nuevoAnalisisPorCommit.put(metricName, valorAnalisisAcumuladoActual.asText());
        } else {

          if (!valorAnalisisAcumuladoActual.asText().equals(DBUtilities.NULL_VALUE_JSON)) {

            JsonNode metricDescription = Searcher.searchMetric(metricName, metricsList);
            String metricType = metricDescription.get(DBUtilities.VAL_TYPE_TAG).asText();

            Object object = null;

            try {
              object =
                  Class.forName(DBUtilities.METRICS_DATA_TYPES_PATH + "." + metricType) //$NON-NLS-1$
                      .getConstructors()[0].newInstance(valorAnalisisAcumuladoActual,
                      valorAnalisisAcumuladoPrevio);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | SecurityException | InvocationTargetException | ClassNotFoundException e) {
              System.err.println(Messages.getString("sonardatatypeerrorcommits") //$NON-NLS-1$
                  + e.getMessage());
            }


            metricHandler = (Metrics) object;

            if (metricHandler != null) {
              nuevoAnalisisPorCommit.put(metricName, metricHandler.getDifferenceBetweenAnalysis());
            }

          }

        }

      }
      analisisPorCommit.add(nuevoAnalisisPorCommit);

    }

    return analisisPorCommit;
  }

  /**
   * Junta las metricas de uno o varios commits correspondientes a la misma tarea de jira
   * 
   * @param analisisJsonPorCommit analisis por commit generador por esta misma clase
   * @param revisiones los datos de las revisiones generados por la clase svnrevisions
   * @return analisis por tarea de jira
   */
  public List<JsonNode> getAnalisisPorTarea(List<JsonNode> analisisJsonPorCommit,
      List<RevisionPOJO> revisiones, List<JsonNode> issuesID) {

    List<JsonNode> analisisPorTarea = new LinkedList<JsonNode>(issuesID);

    for (JsonNode commitAnalisis : analisisJsonPorCommit) {

      String revisionID = commitAnalisis.get(DBUtilities.REVISION_TAG).asText();
      RevisionPOJO revision = Searcher.searchRevision(revisionID, revisiones);

      JsonNode tareaActual = Searcher.searchTarea(revision.getTask(), analisisPorTarea);

      // al analisis por tarea correspondiente sumarle el analisis por commit

      analisisPorTarea.remove(tareaActual);

      ObjectNode tareaNueva = (ObjectNode) tareaActual;

      Iterator<String> metrics = commitAnalisis.getFieldNames();
      Iterator<JsonNode> values = commitAnalisis.getElements();

      while (metrics.hasNext()) {
        String metric = metrics.next();
        JsonNode value = values.next();
        Metrics metricHandler = null;

        // la revision del commit no es un valor que queda en el analisis por tarea
        if (!metric.equals(DBUtilities.REVISION_TAG)) {

          JsonNode metricDescription = Searcher.searchMetric(metric, metricsList);
          String metricType = metricDescription.get(DBUtilities.VAL_TYPE_TAG).asText();
          JsonNode oldValue = tareaActual.get(metric);

          Object object = null;
          try {
            object = Class.forName(DBUtilities.METRICS_DATA_TYPES_PATH + "." + metricType) //$NON-NLS-1$
                .getConstructors()[0].newInstance(value, oldValue);
          } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
              | SecurityException | InvocationTargetException | ClassNotFoundException e) {
            System.err.println(Messages.getString("sonardatatypeerrorjoincommit") //$NON-NLS-1$
                + e.getMessage());
          }

          metricHandler = (Metrics) object;

          if (metricHandler != null) {
            tareaNueva.put(metric, metricHandler.getNewAnalysisPerTask());
          }

        }
      }

      analisisPorTarea.add(tareaNueva);

    }

    return analisisPorTarea;
  }

}
