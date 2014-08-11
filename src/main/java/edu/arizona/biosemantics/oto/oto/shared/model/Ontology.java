package edu.arizona.biosemantics.oto.oto.shared.model;

import java.io.Serializable;

public class Ontology implements Serializable {

	private String category;
	private String definition;
	
	public Ontology() { }
	
	public Ontology(String category, String definition) {
		super();
		this.category = category;
		this.definition = definition;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
}
