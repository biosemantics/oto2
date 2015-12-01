package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

import java.io.Serializable;

public class PartOf implements HasLabelAndIri, Serializable {

	private int id = -1;
	private int ontologyClassSubmission;
	private String iri;
	private String label;
	
	public PartOf() { }

	public PartOf(int id, int ontologyClassSubmission, String iri, String label) {
		super();
		this.id = id;
		this.ontologyClassSubmission = ontologyClassSubmission;
		this.iri = iri;
		this.label = label;
	}

	public PartOf(int ontologyClassSubmission, String iri) {
		super();
		this.ontologyClassSubmission = ontologyClassSubmission;
		this.iri = iri;
	}
	
	public PartOf(int ontologyClassSubmission, String iri, String label) {
		super();
		this.ontologyClassSubmission = ontologyClassSubmission;
		this.iri = iri;
		this.label = label;
	}
	
	public PartOf(String iri) {
		this.iri = iri;
	}
	
	public PartOf(String iri, String label) {
		this.iri = iri;
		this.label = label;
	}

	public PartOf(OntologyClassSubmission submission) {
		this.iri = submission.getClassIRI();
		this.label = submission.getSubmissionTerm();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIri() {
		return iri;
	}

	public void setIri(String iri) {
		this.iri = iri;
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public boolean hasLabel() {
		return this.label != null && !label.isEmpty();
	}
	
	public boolean hasIri() {
		return this.iri != null && !iri.isEmpty();
	}
	
	public String getValue() {
		return this.toString();
	}
	
	@Override
	public String toString() {
		if(hasIri() && hasLabel()) 
			return this.iri + " (" + this.label + ")";
		if(hasIri() && !hasLabel())
			return this.iri;
		if(!hasIri() && hasLabel())
			return this.label;
		return "";
	}
	
	@Override
	public String getLabelAlternativelyIri() {
		if(hasLabel())
			return label;
		if(hasIri())
			return iri;
		return "";
	}
	
}
