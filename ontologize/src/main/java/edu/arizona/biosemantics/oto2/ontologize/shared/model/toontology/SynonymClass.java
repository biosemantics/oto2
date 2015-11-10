package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

public class SynonymClass {

	private String iri;
	private String label;
	
	public SynonymClass() { }
	
	public SynonymClass(String iri) {
		this.iri = iri;
	}
	
	public SynonymClass(String iri, String label) {
		this.iri = iri;
		this.label = label;
	}

	public SynonymClass(OntologyClassSubmission submission) {
		this.iri = submission.getClassIRI();
		this.label = submission.getSubmissionTerm();
	}

	public String getIri() {
		return iri;
	}

	public void setIri(String iri) {
		this.iri = iri;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((iri == null) ? 0 : iri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SynonymClass other = (SynonymClass) obj;
		if (iri == null) {
			if (other.iri != null)
				return false;
		} else if (!iri.equals(other.iri))
			return false;
		return true;
	}

	
}
