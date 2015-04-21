package edu.arizona.biosemantics.oto2.steps.shared.model.toontology;

import java.io.Serializable;

import edu.arizona.biosemantics.oto2.steps.shared.model.Status;

public class OntologySynonymSubmissionStatus implements Serializable, Comparable<OntologySynonymSubmissionStatus> {

	private int id = -1;
	private int ontologySynonymSubmissionId;
	private Status status;
	private String externalId;
	
	public OntologySynonymSubmissionStatus() { }
	
	public OntologySynonymSubmissionStatus(int ontologySynonymSubmissionId,
			Status status, String externalId) {
		super();
		this.ontologySynonymSubmissionId = ontologySynonymSubmissionId;
		this.status = status;
		this.externalId = externalId;
	}
	
	public OntologySynonymSubmissionStatus(int id, int ontologySynonymSubmissionId,
			Status status, String externalId) {
		super();
		this.id = id;
		this.ontologySynonymSubmissionId = ontologySynonymSubmissionId;
		this.status = status;
		this.externalId = externalId;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOntologySynonymSubmissionId() {
		return ontologySynonymSubmissionId;
	}
	public void setOntologySynonymSubmissionId(int ontologySynonymSubmissionId) {
		this.ontologySynonymSubmissionId = ontologySynonymSubmissionId;
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
	public int compareTo(OntologySynonymSubmissionStatus o) {
		return this.getId() - o.getId();
	}
	
	
}
