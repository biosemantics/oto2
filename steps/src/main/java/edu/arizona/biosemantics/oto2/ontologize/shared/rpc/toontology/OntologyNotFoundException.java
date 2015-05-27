package edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology;

public class OntologyNotFoundException extends Exception {

	public OntologyNotFoundException(String message) {
		super(message);
	}

	public OntologyNotFoundException(String message, Throwable t) {
		super(message, t);
	}

}
