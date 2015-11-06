package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

import java.io.Serializable;

public class Superclass implements Serializable {

	private int id = -1;
	private int ontologyClassSubmission = -1;
	private String iri;
	private String term;
	
	public Superclass() { }

	public Superclass(int id, int ontologyClassSubmission, String iri) {
		super();
		this.id = id;
		this.ontologyClassSubmission = ontologyClassSubmission;
		this.iri = iri;
	}

	public Superclass(int ontologyClassSubmission, String iri) {
		super();
		this.ontologyClassSubmission = ontologyClassSubmission;
		this.iri = iri;
	}
	
	public Superclass(String iri) {
		this.iri = iri;
	}

	public Superclass(OntologyClassSubmission submission) {
		this.iri = submission.getClassIRI() + " (" + submission.getSubmissionTerm() + ")";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOntologyClassSubmission() {
		return ontologyClassSubmission;
	}

	public void setOntologyClassSubmission(int ontologyClassSubmission) {
		this.ontologyClassSubmission = ontologyClassSubmission;
	}

	public String getIri() {
		return iri;
	}

	public void setIri(String iri) {
		this.iri = iri;
	}

	public boolean hasId() {
		return id != -1;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

}