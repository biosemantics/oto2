package edu.arizona.biosemantics.oto2.oto.shared.model;

import java.io.Serializable;

public class OntologyEntry implements Serializable {

	private String id;
	private String label;
	private String definition;
	private String url;
	
	public OntologyEntry() { }
	
	public OntologyEntry(String id, String label, String definition, String url) {
		super();
		this.id = id;
		this.label = label;
		this.definition = definition;
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
	
}
