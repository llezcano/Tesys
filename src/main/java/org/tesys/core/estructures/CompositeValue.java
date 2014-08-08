package org.tesys.core.estructures;


public abstract class CompositeValue implements IValue {  
  protected IValue izq, der;

  public CompositeValue(IValue izq, IValue der) {
    super();
    this.izq = izq;
    this.der = der;
  }

}
