package edu.arizona.biosemantics.oto2.oto.shared.model;

import java.io.Serializable;

public class Location implements Serializable {

	private String instance;
	private String categorization;
	
	public Location() { }
	
	public Location(String instance, String categorization) {
		super();
		this.instance = instance;
		this.categorization = categorization;
	}
	
	public String getInstance() {
		return instance;
	}
	public void setInstance(String instance) {
		this.instance = instance;
	}
	public String getCategorization() {
		return categorization;
	}
	public void setCategorization(String categorization) {
		this.categorization = categorization;
	}
}
