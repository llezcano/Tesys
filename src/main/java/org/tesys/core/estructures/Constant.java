package org.tesys.core.estructures;


public class Constant implements IValue {
  private Double value;

  public Constant(Double value) {
    super();
    this.value = value;
  }
  
  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "{\"Constant\":\"" +value+"\"}" ;
  }

  @Override
  public Double evaluate(Issue issue) {
    return this.getValue();
  }
  
  
  
}
