package org.tesys.core.analysis.telemetry;

import java.util.List;

import org.tesys.core.analysis.sonar.AnalisisPOJO;
import org.tesys.core.analysis.sonar.KeyValuePOJO;
import org.tesys.core.analysis.sonar.SonarAnalizer;
import org.tesys.core.analysis.sonar.SonarMetricPOJO;
import org.tesys.core.db.ElasticsearchDao;
import org.tesys.core.estructures.Issue;
import org.tesys.core.estructures.Metric;
import org.tesys.core.estructures.SimpleValue;

/**
 * Clase encargada de recolectar todos los datos del sonar, dado que sonar
 * almacena los analisis por tarea en la base de datos los obtiene desde ahi
 */

public class SonarAnalisisAggregator extends AggregatorDecorator {

    private ElasticsearchDao<AnalisisPOJO> dao;

    public SonarAnalisisAggregator(Aggregator aggregator) {
	super(aggregator);
	dao = new ElasticsearchDao<>(AnalisisPOJO.class,
		ElasticsearchDao.DEFAULT_RESOURCE_ANALYSIS);
    }

    /**
     * Se obtiene las metricas con las que cuenta sonar desde la api del
     * programa
     */

    @Override
    public List<Metric> getMetricsID() {
	// Se juntan la de los demas programas
	List<Metric> metrics = super.aggregator.getMetricsID();
	// se agregan las de este
	SonarAnalizer sa = SonarAnalizer.getInstance();
	for (SonarMetricPOJO metric : sa.getMetrics()) {
	    metrics.add(new Metric(metric.getKey(), metric.getName(), metric
		    .getDescription(), "SonarQube", new SimpleValue(metric
		    .getKey())));
	}

	return metrics;
    }

    /**
     * Se agregan los analisis que fueron realizados con anterioridad y estan
     * almacenados en la base de datos
     */

    @Override
    public Issue agregateMetrics(Issue issueMetrics) {
	issueMetrics = super.agregateMetrics(issueMetrics);
	String key = issueMetrics.getIssueId();
	AnalisisPOJO analisis = dao.read(key);
	if (analisis != null) {
	    for (KeyValuePOJO value : analisis.getResults()) {
		issueMetrics.addMetric(value.getKey(),
			(Double) value.getValue());
	    }
	}

	return issueMetrics;
    }

}
