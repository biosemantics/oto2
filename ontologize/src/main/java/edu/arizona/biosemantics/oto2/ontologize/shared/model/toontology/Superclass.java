package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

public class Superclass {

	private int id = -1;
	private int ontologyClassSubmission = -1;
	private String superclass;

	public Superclass(int id, int ontologyClassSubmission, String superclass) {
		super();
		this.id = id;
		this.ontologyClassSubmission = ontologyClassSubmission;
		this.superclass = superclass;
	}

	public Superclass(int ontologyClassSubmission, String superclass) {
		super();
		this.ontologyClassSubmission = ontologyClassSubmission;
		this.superclass = superclass;
	}
	
	public Superclass(String superclass) {
		this.superclass = superclass;
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

	public String getSuperclass() {
		return superclass;
	}

	public void setSuperclass(String superclass) {
		this.superclass = superclass;
	}

	public boolean hasId() {
		return id != -1;
	}

}
