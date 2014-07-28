package org.tesys.core.analysis.sonar;


public class SonarAnalizer {
  /// rev0 ----------------------------------------------------------------------
  //TODO preguntar si esta o no. Igual se puede obviar porque es casi el mismo costo
  /*SvnPostCommitDataPOJO rev0 = new SvnPostCommitDataPOJO();
  rev0.setAuthor("null");
  rev0.setDate("2000-01-01 00:00:00");
  rev0.setMessage("null");
  rev0.setRepository(svnData.getRepository());
  rev0.setRevision("r0");
  
  String rev0id = rev0.getDate();
  try {
    md = MessageDigest.getInstance("MD5");
  } catch (NoSuchAlgorithmException e) {
    e.printStackTrace();
  }
  md.update(rev0id.getBytes());
  byte[] rev0digest = md.digest();
  StringBuffer rev0sb = new StringBuffer();
  for (byte b : rev0digest) {
    rev0sb.append(String.format("%02x", b & 0xff));
  }
  
  ObjectMapper rev0objectMapper = new ObjectMapper();
  JsonNode rev0data = rev0objectMapper.valueToTree(rev0);
  db.PUT("scm", "revisions", rev0sb.toString(), rev0data.toString());
  //------------------------------------------------------------------------------*/
  

}
