package edu.arizona.biosemantics.oto2.oto.shared.model;

import java.io.Serializable;

public class Ontology implements Serializable {

	private String acronym;
	private String name;
	private String id;
	
	public Ontology() { }
	
	public Ontology(String id, String acronym, String name) {
		super();
		this.acronym = acronym;
		this.name = name;
		this.id = id;
	}
	
	public String getAcronym() {
		return acronym;
	}
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
