package edu.arizona.biosemantics.oto2.ontologize2.shared.model;

public class Term {

	private String value;
	private String iri;
	private String buckets;
	
	public Term(String value) {
		this.value = value;
	}
	
	public Term(String value, String iri, String buckets) {
		super();
		this.value = value;
		this.iri = iri;
		this.buckets = buckets;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
	
	
}

