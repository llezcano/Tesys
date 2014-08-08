package org.tesys.core.estructures;


public class SimpleValue implements IValue  {

  private String key;
  
  public SimpleValue(String key) {
    super();
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public String toString() {
    return "{\"SimpleValue\":\"" +key+"\"}" ;
  }

  @Override
  public Double evaluate(Issue issue) {
    return issue.getMetrics().get(key);
  }
  
}
