package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

import java.io.Serializable;

public class Synonym implements Serializable {

	private int id = -1;
	private int submission = 1;
	private String synonym;
	
	public Synonym() { }

	public Synonym(int id, int submission, String synonym) {
		super();
		this.id = id;
		this.submission = submission;
		this.synonym = synonym;
	}

	public Synonym(int submission, String synonym) {
		super();
		this.submission = submission;
		this.synonym = synonym;
	}
	
	public Synonym(String synonym) {
		this.synonym = synonym;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSubmission() {
		return submission;
	}

	public void setSubmission(int submission) {
		this.submission = submission;
	}

	public String getSynonym() {
		return synonym;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}

	public boolean hasId() {
		return id != -1;
	}

}
