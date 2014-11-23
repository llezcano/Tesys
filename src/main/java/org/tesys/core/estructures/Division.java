package org.tesys.core.estructures;

public class Division extends CompositeValue {

    public Division(IValue izq, IValue der) {
	super(izq, der);
    }

    @Override
    public String toString() {
	return "{\"Division\":[" + izq.toString() + "," + der.toString() + "]}";
    }

    @Override
    public Double evaluate(Issue issue) {
    	if( der.evaluate(issue) == 0.0 ){
    		return 0.0;
    	}
	return (izq.evaluate(issue) / der.evaluate(issue));
    }

}
