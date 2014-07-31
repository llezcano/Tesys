package org.tesys.core.analysis.sonar;

public class MetricPOJO {

  private String key;
  private String name;
  private String description;
  private String type;
  private String domain;

  public MetricPOJO(String key, String name, String description, String type, String domain) {
    this.key = key;
    this.name = name;
    this.description = description;
    this.type = type;
    this.domain = domain;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

}
