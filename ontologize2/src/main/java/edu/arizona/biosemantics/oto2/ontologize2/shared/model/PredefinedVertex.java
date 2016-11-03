package edu.arizona.biosemantics.oto2.ontologize2.shared.model;

import java.io.Serializable;

public class PredefinedVertex implements Serializable {
	
	private String value;
	private boolean requiresCandidate = true;
	
	public PredefinedVertex() { }
	
	public PredefinedVertex(String value) {
		this.value = value;
	}
	
	public PredefinedVertex(String value, boolean requiresCandidate) {
		this.value = value;
		this.requiresCandidate = requiresCandidate;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isRequiresCandidate() {
		return requiresCandidate;
	}

	public void setRequiresCandidate(boolean requiresCandidate) {
		this.requiresCandidate = requiresCandidate;
	}
	
	

}
