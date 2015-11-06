package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

import java.io.Serializable;

public class PartOf implements Serializable {

	private int id = -1;
	private int ontologyClassSubmission;
	private String iri;
	
	public PartOf() { }

	public PartOf(int id, int ontologyClassSubmission, String iri) {
		super();
		this.id = id;
		this.ontologyClassSubmission = ontologyClassSubmission;
		this.iri = iri;
	}

	public PartOf(int ontologyClassSubmission, String iri) {
		super();
		this.ontologyClassSubmission = ontologyClassSubmission;
		this.iri = iri;
	}
	
	public PartOf(String iri) {
		this.iri = iri;
	}

	public PartOf(OntologyClassSubmission submission) {
		this.iri = submission.getClassIRI() + " (" + submission.getSubmissionTerm() + ")";
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

}
