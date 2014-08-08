package org.tesys.core.analysis.telemetry;

import java.util.List;

import org.tesys.core.estructures.Issue;
import org.tesys.core.estructures.Metric;

public interface Aggregator {
  Issue agregateMetrics(Issue i);
  List<Metric> getMetricsID();
}
