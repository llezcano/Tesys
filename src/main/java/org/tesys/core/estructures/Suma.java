package org.tesys.core.estructures;


public class Suma extends CompositeValue {

  public Suma(IValue izq, IValue der) {
    super(izq, der);
  }

  @Override
  public Double getValue(Issue issue) {
    return ( izq.getValue(issue) + der.getValue(issue)  );
  }

}
