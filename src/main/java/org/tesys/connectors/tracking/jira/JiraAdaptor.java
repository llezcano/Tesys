package org.tesys.connectors.tracking.jira;

import java.util.List;

import org.tesys.core.analysis.sonar.MetricPOJO;
import org.tesys.core.project.tracking.Issue;
import org.tesys.core.project.tracking.User;

public interface JiraAdaptor {

    Issue[] getIssues();

    User[] getUsers();

    Issue getIssue( String key );

    User getUser( String key );

    List<MetricPOJO> getMetrics();

}
