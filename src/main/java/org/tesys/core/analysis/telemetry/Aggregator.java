package org.tesys.core.analysis.telemetry;

public interface Aggregator {
  IssueMetrics agregateMetrics(IssueMetrics i);
}
