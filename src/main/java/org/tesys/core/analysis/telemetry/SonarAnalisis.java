package org.tesys.core.analysis.telemetry;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import org.tesys.core.analysis.sonar.AnalisisPOJO;
import org.tesys.core.analysis.sonar.KeyValuePOJO;
import org.tesys.core.analysis.sonar.MetricPOJO;
import org.tesys.core.analysis.sonar.metricsdatatypes.Metrics;

import org.tesys.core.analysis.telemetry.util.Searcher;


public class SonarAnalisis {

  private List<MetricPOJO> metrics;

  public SonarAnalisis( List<MetricPOJO> metrics ) {
    this.metrics = metrics;
  }

  public List<AnalisisPOJO> getAnalisisPorCommit(List<AnalisisPOJO> analisisAcumulados) {

    List<AnalisisPOJO> analisisPorCommit = new LinkedList<AnalisisPOJO>();

    for (int i = 1; i < analisisAcumulados.size(); i++) {
      AnalisisPOJO analisisAcumuladoPrevio = analisisAcumulados.get(i - 1);
      AnalisisPOJO analisisAcumuladoActual = analisisAcumulados.get(i);
      
      if( !(analisisAcumuladoPrevio.isScaned() && analisisAcumuladoActual.isScaned())) {
          
          
        AnalisisPOJO nuevoAnalisisPorCommit = new AnalisisPOJO();

        List<KeyValuePOJO> resultadosPrevios = analisisAcumuladoPrevio.getResults();
        List<KeyValuePOJO> resultadosActuales = analisisAcumuladoActual.getResults();

        for (int j=0; j<resultadosActuales.size(); j++) {
          
          Metrics metricHandler = null;
          String metricName = resultadosActuales.get(j).getKey();
          String valorActual = resultadosActuales.get(j).getValue();
          String valorPrevio = resultadosPrevios.get(j).getValue();

          if (!valorActual.equals("null")) {

            MetricPOJO metric = Searcher.searchMetric(metricName, metrics);
            String metricType = metric.getType();

            Object object = null;

            try {
              object =
                  Class.forName("org.tesys.core.analysis.sonar.metricdatatypes"+ "." + metricType)
                      .getConstructors()[0].newInstance(valorActual, valorPrevio);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | SecurityException | InvocationTargetException | ClassNotFoundException e) {
              System.err.println( "reflection gone wrong"
                  + e.getMessage());
            }


            metricHandler = (Metrics) object;

            if (metricHandler != null) {

              nuevoAnalisisPorCommit.add( new KeyValuePOJO(metricName, metricHandler.getDifferenceBetweenAnalysis() ) );
            }

          }

        }
        analisisPorCommit.add(nuevoAnalisisPorCommit);
        
      }
      
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
  public List<AnalisisPOJO> getAnalisisPorTarea( List<AnalisisPOJO> analisisPorCommit ) {
    
    List<AnalisisPOJO> result = new LinkedList<AnalisisPOJO>();
   
    for (AnalisisPOJO commitAnalisis : analisisPorCommit) {
      AnalisisPOJO guardado = Searcher.searchIssue(result, commitAnalisis.getRevision().getProjectTrackingTask());
      if( guardado == null ) {
        result.add( commitAnalisis );
      } else {
        
        
        AnalisisPOJO nuevoAnalisisPorTarea = new AnalisisPOJO(guardado.getRevision());
        

        List<KeyValuePOJO> resultadosPrevios = guardado.getResults();
        List<KeyValuePOJO> resultadosActuales = commitAnalisis.getResults();
        
        result.remove(guardado);

        for (int j=0; j<resultadosActuales.size(); j++) {
          
          Metrics metricHandler = null;
          String metricName = resultadosActuales.get(j).getKey();
          String valorActual = resultadosActuales.get(j).getValue();
          String valorPrevio = resultadosPrevios.get(j).getValue();

          MetricPOJO metric = Searcher.searchMetric(metricName, metrics);
          String metricType = metric.getType();

          Object object = null;

          try {
            object =
                Class.forName( "org.tesys.core.analysis.sonar.metricdatatypes" + "." + metricType)
                    .getConstructors()[0].newInstance(valorActual, valorPrevio);
          } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
              | SecurityException | InvocationTargetException | ClassNotFoundException e) {
            System.err.println( "reflection gone wrong"
                + e.getMessage());
          }


          metricHandler = (Metrics) object;

          if (metricHandler != null) {

            nuevoAnalisisPorTarea.add( new KeyValuePOJO(metricName, metricHandler.getNewAnalysisPerTask()) );
          }



        }
        result.add(nuevoAnalisisPorTarea);
        
        
        
        
      }
      
    }
    
    return result;
  }
  
  
  
  
  
  
  

}