package org.tesys.core.estructures;


public class Multiplicacion extends CompositeValue {

  public Multiplicacion(IValue izq, IValue der) {
    super(izq, der);
  }

  @Override
  public Double getValue(Issue issue) {
    return ( izq.getValue(issue) * der.getValue(issue)  );
  }

}
