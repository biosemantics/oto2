package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

import java.io.Serializable;

public class PartOf implements Serializable {

	private int id = -1;
	private int ontologyClassSubmission;
	private String partOf;
	
	public PartOf() { }

	public PartOf(int id, int ontologyClassSubmission, String partOf) {
		super();
		this.id = id;
		this.ontologyClassSubmission = ontologyClassSubmission;
		this.partOf = partOf;
	}

	public PartOf(int ontologyClassSubmission, String partOf) {
		super();
		this.ontologyClassSubmission = ontologyClassSubmission;
		this.partOf = partOf;
	}
	
	public PartOf(String partOf) {
		this.partOf = partOf;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPartOf() {
		return partOf;
	}

	public void setPartOf(String partOf) {
		this.partOf = partOf;
	}

	public int getOntologyClassSubmission() {
		return ontologyClassSubmission;
	}

	public void setOntologyClassSubmission(int ontologyClassSubmission) {
		this.ontologyClassSubmission = ontologyClassSubmission;
	}

	public boolean hasId() {
		return id != -1;
	}

}
