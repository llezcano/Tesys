package org.tesys.core.estructures;


public class Multiplicacion extends CompositeValue {

  public Multiplicacion(IValue izq, IValue der) {
    super(izq, der);
  }
  
  @Override
  public String toString() {
    return "{\"Multiplicacion\":["+izq.toString()+","+der.toString()+"]}";
  }


  @Override
  public Double evaluate(Issue issue) {
    return ( izq.evaluate(issue) * der.evaluate(issue)  );
  }

}
