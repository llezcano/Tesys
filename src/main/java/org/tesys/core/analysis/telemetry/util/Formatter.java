package org.tesys.core.analysis.telemetry.util;

import org.tesys.core.analysis.telemetry.dbutilities.DBUtilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;



public class Formatter {


  public static JsonNode joinJson(JsonNode main, JsonNode update) {

    ObjectNode objectMain = (ObjectNode) main;

    objectMain.put(DBUtilities.CODE_METRIC_TAG, update);

    return (JsonNode) objectMain;
  }


}
