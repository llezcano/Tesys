package org.tesys.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {


  private static MessageDigest md;



  /**
   * Este metodo genera el MD5 equivalente de un String dado. Si se quieren varios Strings hay que
   * pasarlos concatenados o de alguna otra forma
   * 
   * @param input
   * @return
   */
  public static String generateId(String input) {
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    md.update(input.getBytes());
    byte[] digest = md.digest();
    StringBuffer sb = new StringBuffer();
    for (byte b : digest) {
      sb.append(String.format("%02x", b & 0xff));
    }
    return sb.toString();
  }

}
