package org.tesys.core.estructures;

public abstract class CompositeValue implements IValue {
    protected IValue izq, der;

    public CompositeValue(IValue izq, IValue der) {
	super();
	this.izq = izq;
	this.der = der;
    }

    public IValue getIzq() {
	return izq;
    }

    public void setIzq(IValue izq) {
	this.izq = izq;
    }

    public IValue getDer() {
	return der;
    }

    public void setDer(IValue der) {
	this.der = der;
    }

}
