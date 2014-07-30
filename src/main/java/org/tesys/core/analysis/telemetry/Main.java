package org.tesys.core.analysis.telemetry;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.tesys.core.analysis.telemetry.dbutilities.DBUtilities;
import org.tesys.core.analysis.telemetry.util.Formatter;
import org.tesys.core.analysis.telemetry.util.Searcher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;



public class Main {

  private static final int NUM_OF_PARAM = 7;
  private static final String DB_HOST_PAR = Messages.getString("Main.dbhost"); //$NON-NLS-1$
  private static final String DB_PORT_PAR = Messages.getString("Main.dbport"); //$NON-NLS-1$
  private static final String SENDER_HOST_PAR = Messages.getString("Main.senderhost"); //$NON-NLS-1$
  private static final String SENDER_PORT_PAR = Messages.getString("Main.senderport"); //$NON-NLS-1$
  private static final String SENDER_PATH_PAR = Messages.getString("Main.senderpath"); //$NON-NLS-1$
  private static final String SENDER_APP_PAR = Messages.getString("Main.senderapp"); //$NON-NLS-1$
  private static final String SENDER_DATA_PAR = Messages.getString("Main.senderdata"); //$NON-NLS-1$

  private static final String PROGRAM_USAGE = Messages.getString("Main.exampleofuse"); //$NON-NLS-1$

  private static String dbhost;
  private static int dbport;
  private static String senderhost;
  private static int senderport;
  private static String senderpath;
  private static String senderapp;
  private static String senderdata;

  public static void main(String[] args) {

    validateParameters(args);

    Client client =
        new TransportClient().addTransportAddress(new InetSocketTransportAddress(dbhost, dbport));

    SvnRevisions svn = new SvnRevisions(client);
    // Se obtiene todos los datos de las revisiones
    List<Revision> revisiones = svn.getRevisions(svn.getDataInJsonFormat(svn.getData()));

    SonarMetrics sm = new SonarMetrics(client);

    // Se obtienen todas las metricas que existen en sonar, con tipo y demas
    List<JsonNode> metrics = sm.getMetrics(sm.getResponse());

    SonarAnalisis sonarAnalisis = new SonarAnalisis(client, revisiones, metrics);

    // Se obtienen los analisis por commit acumulado
    List<JsonNode> analisisJson = sonarAnalisis.getDataJsonFormat(sonarAnalisis.getAnalisis());

    // Se obtienen los analisis por commit individual
    List<JsonNode> analisisJsonPorCommit = sonarAnalisis.getAnalisisPorCommit(analisisJson);

    // Se obtienen todas las tareas que existen de jira
    JiraIssues jira = new JiraIssues(client);
    List<JsonNode> issuesID = jira.getIssuesId();

    // se obtienen los analisis por tarea
    List<JsonNode> analisisJsonPorTarea =
        sonarAnalisis.getAnalisisPorTarea(analisisJsonPorCommit, revisiones, issuesID);

    List<JsonNode> issues = jira.getIssues();

    StoreInElasticSearch store =
        new StoreInElasticSearch(senderhost, senderport, senderpath, senderapp, senderdata);

    for (JsonNode issue : issues) {
      JsonNode analisis = Searcher.searchIssue(issue, analisisJsonPorTarea);
      analisisJsonPorTarea.remove(analisis);
      ObjectNode objectAnalisis = (ObjectNode) analisis;
      objectAnalisis.remove(DBUtilities.JIRA_TASK_TAG);
      analisis = (JsonNode) objectAnalisis;

      Formatter.joinJson(issue, analisis);

      store.send(issue.get(DBUtilities.KEY_TAG).asText(), issue.toString());
    }

    client.close();
  }


  private static void validateParameters(String[] args) {

    if (args.length != NUM_OF_PARAM * 2) {
      System.err.println(Messages.getString("Main.errorcantparameters")); //$NON-NLS-1$
      System.err.println(PROGRAM_USAGE);
      System.exit(1);
    }

    List<String> parametersFlags = new ArrayList<String>(NUM_OF_PARAM);

    parametersFlags.add(DB_HOST_PAR);
    parametersFlags.add(DB_PORT_PAR);
    parametersFlags.add(SENDER_HOST_PAR);
    parametersFlags.add(SENDER_PORT_PAR);
    parametersFlags.add(SENDER_PATH_PAR);
    parametersFlags.add(SENDER_APP_PAR);
    parametersFlags.add(SENDER_DATA_PAR);

    for (String flag : parametersFlags) {
      boolean esta = false;
      for (int i = 0; i < args.length; i++) {
        if (args[i].equals(flag)) {
          esta = true;
          break;
        }
      }
      if (esta == false) {
        System.err.println(Messages.getString("Main.errorflag")); //$NON-NLS-1$
        System.err.println(PROGRAM_USAGE);
        System.exit(1);
      }

    }

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals(DB_HOST_PAR)) {
        dbhost = args[i + 1];
      } else if (args[i].equals(DB_PORT_PAR)) {
        try {
          dbport = Integer.parseInt(args[i + 1]);
        } catch (NumberFormatException e) {
          System.err.println(Messages.getString("Main.errordbport")); //$NON-NLS-1$
          System.exit(1);
        }
      } else if (args[i].equals(SENDER_HOST_PAR)) {
        senderhost = args[i + 1];
      } else if (args[i].equals(SENDER_PORT_PAR)) {
        try {
          senderport = Integer.parseInt(args[i + 1]);
        } catch (NumberFormatException e) {
          System.err.println(Messages.getString("Main.errorsenderport")); //$NON-NLS-1$
          System.exit(1);
        }
      } else if (args[i].equals(SENDER_PATH_PAR)) {
        senderpath = args[i + 1];
      } else if (args[i].equals(SENDER_APP_PAR)) {
        senderapp = args[i + 1];
      } else if (args[i].equals(SENDER_DATA_PAR)) {
        senderdata = args[i + 1];
      }

    }

  }
}
