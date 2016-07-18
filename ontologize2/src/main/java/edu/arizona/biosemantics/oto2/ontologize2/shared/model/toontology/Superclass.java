package edu.arizona.biosemantics.oto2.ontologize2.shared.model.toontology;

import java.io.Serializable;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Type;


public class Superclass implements HasLabelAndIri, Serializable {

//	private int id = -1;
//	private int ontologyClassSubmission = -1;
	private String iri;
	private String label;
	
	public Superclass() { }

//	public Superclass(int id, int ontologyClassSubmission, String iri, String label) {
//		super();
//		this.id = id;
//		this.ontologyClassSubmission = ontologyClassSubmission;
//		this.iri = iri;
//		this.label = label;
//	}
//
//	public Superclass(int ontologyClassSubmission, String iri) {
//		super();
//		this.ontologyClassSubmission = ontologyClassSubmission;
//		this.iri = iri;
//	}
//	
//	public Superclass(int ontologyClassSubmission, String iri, String label) {
//		super();
//		this.ontologyClassSubmission = ontologyClassSubmission;
//		this.iri = iri;
//		this.label = label;
//	}
	
	public Superclass(String iri) {
		this.iri = iri;
	}
	
	public Superclass(String iri, String label) {
		this.iri = iri;
		this.label = label;
	}

	public Superclass(OntologyClassSubmission submission) {
		this.iri = submission.getClassIRI();
		this.label = submission.getSubmissionTerm();
	}

	public Superclass(Type entity) {
		this(entity.getIRI(), entity.getLabel());
	}

//	public int getId() {
//		return id;
//	}
//
//	public void setId(int id) {
//		this.id = id;
//	}
//
//	public int getOntologyClassSubmission() {
//		return ontologyClassSubmission;
//	}
//
//	public void setOntologyClassSubmission(int ontologyClassSubmission) {
//		this.ontologyClassSubmission = ontologyClassSubmission;
//	}

	public String getIri() {
		return iri;
	}

	public void setIri(String iri) {
		this.iri = iri;
	}

//	public boolean hasId() {
//		return id != -1;
//	}

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((iri == null) ? 0 : iri.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
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