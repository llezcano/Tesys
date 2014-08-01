package org.tesys.core.analysis.sonar;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class KeyValuePOJO {
  
  @XmlAttribute
  public String key;
  
  @XmlValue
  public String value;

}
