package org.tesys.core.estructures;

public class Suma extends CompositeValue {

    public Suma(IValue izq, IValue der) {
	super(izq, der);
    }

    @Override
    public String toString() {
	return "{\"Suma\":[" + izq.toString() + "," + der.toString() + "]}";
    }

    @Override
    public Double evaluate(Issue issue) {
	return (izq.evaluate(issue) + der.evaluate(issue));
    }

}
