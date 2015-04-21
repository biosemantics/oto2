package edu.arizona.biosemantics.oto2.steps.shared.model.toontology;

import java.io.Serializable;

import edu.arizona.biosemantics.oto2.steps.shared.model.Status;

public class OntologyClassSubmissionStatus implements Serializable, Comparable<OntologyClassSubmissionStatus> {

	private int id = -1;
	private int ontologyClassSubmissionId;
	private Status status;
	private String externalId;
	
	public OntologyClassSubmissionStatus() { }
	
	public OntologyClassSubmissionStatus(int ontologyClassSubmissionId,
			Status status, String externalId) {
		super();
		this.ontologyClassSubmissionId = ontologyClassSubmissionId;
		this.status = status;
		this.externalId = externalId;
	}
	
	public OntologyClassSubmissionStatus(int id, int ontologyClassSubmissionId,
			Status status, String externalId) {
		super();
		this.id = id;
		this.ontologyClassSubmissionId = ontologyClassSubmissionId;
		this.status = status;
		this.externalId = externalId;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOntologyClassSubmissionId() {
		return ontologyClassSubmissionId;
	}
	public void setOntologyClassSubmissionId(int ontologyClassSubmissionId) {
		this.ontologyClassSubmissionId = ontologyClassSubmissionId;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatusId(Status status) {
		this.status = status;
	}
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
	public boolean hasId() {
		return id != -1;
	}

	@Override
	public int compareTo(OntologyClassSubmissionStatus o) {
		return this.getId() - o.getId();
	}
	
	
}
