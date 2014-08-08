package org.tesys.core.estructures;


public class Division extends CompositeValue {

  public Division(IValue izq, IValue der) {
    super(izq, der);
  }

  @Override
  public Double getValue(Issue issue) {
    return ( izq.getValue(issue) / der.getValue(issue)  );
  }

}
