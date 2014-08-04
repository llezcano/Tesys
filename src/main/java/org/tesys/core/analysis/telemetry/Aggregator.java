package org.tesys.core.analysis.telemetry;

import java.util.List;

public interface Aggregator {
  IssueMetrics agregateMetrics(IssueMetrics i);
  List<MetricPOJO> getMetricsID();
}
