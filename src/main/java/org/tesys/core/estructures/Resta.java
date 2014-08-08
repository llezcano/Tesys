package org.tesys.core.estructures;


public class Resta extends CompositeValue {

  public Resta(IValue izq, IValue der) {
    super(izq, der);
  }

  @Override
  public Double getValue(Issue issue) {
    return ( izq.getValue(issue) - der.getValue(issue)  );
  }

}
