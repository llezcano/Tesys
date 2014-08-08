package org.tesys.core.estructures;


public class Constant implements IValue {
  private Double value;

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public Constant(Double value) {
    super();
    this.value = value;
  }

  @Override
  public Double getValue(Issue issue) {
    return this.getValue();
  }
  
  
  
}
