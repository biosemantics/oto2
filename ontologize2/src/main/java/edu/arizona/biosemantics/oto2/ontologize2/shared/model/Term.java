package edu.arizona.biosemantics.oto2.ontologize2.shared.model;

public class Term {

	protected String value;
	protected String disambiguator = "";
	protected String iri;
	protected String buckets;
	
	public Term(String value) {
		this.value = value;
	}
	
	public Term(String value, String disambiguator) {
		this.value = value;
		this.disambiguator = disambiguator;
	}
	
	public Term(String value, String iri, String buckets) {
		this(value);
		this.iri = iri;
		this.buckets = buckets;
	}

	public Term(String value, String disambiguator, String iri, String buckets) {
		this(value, disambiguator);
		this.iri = iri;
		this.buckets = buckets;
	}
		
	public String getDisambiguator() {
		return disambiguator;
	}

	public void setDisambiguator(String disambiguator) {
		if(disambiguator == null)
			disambiguator = "";
		this.disambiguator = disambiguator.trim();
	}

	public String getDisambiguatedValue() {
		return (disambiguator + " " + value).trim();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value.trim();
	}

	public String getIri() {
		return iri;
	}

	public void setIri(String iri) {
		this.iri = iri;
	}

	public String getBuckets() {
		return buckets;
	}

	public void setBuckets(String buckets) {
		this.buckets = buckets;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getDisambiguatedValue() == null) ? 0 : this.getDisambiguatedValue().hashCode());
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
		Term other = (Term) obj;
		if (this.getDisambiguatedValue() == null) {
			if (other.getDisambiguatedValue() != null)
				return false;
		} else if (!this.getDisambiguatedValue().equals(other.getDisambiguatedValue()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getDisambiguatedValue();
	}

	public boolean hasDisambiguator() {
		return this.disambiguator != null && !this.disambiguator.isEmpty();
	}
	
	public Term clone(boolean cloneIri) {
		Term clone = new Term(this.value);
		clone.disambiguator = this.disambiguator;
		if(cloneIri)
			clone.iri = this.iri;
		clone.buckets = this.buckets;
		return clone;
	}

	public void addDisambiguator(String prepend) {
		this.disambiguator = (prepend + " " + this.disambiguator).trim();
	}
		
}

