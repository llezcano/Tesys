package org.tesys.core.estructures;


public class Resta extends CompositeValue {

  public Resta(IValue izq, IValue der) {
    super(izq, der);
  }
  
  @Override
  public String toString() {
    return "{\"Resta\":["+izq.toString()+","+der.toString()+"]}";
  }

  @Override
  public Double evaluate(Issue issue) {
    return ( izq.evaluate(issue) - der.evaluate(issue)  );
  }

}
