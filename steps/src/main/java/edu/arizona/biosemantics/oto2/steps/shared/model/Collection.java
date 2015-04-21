package edu.arizona.biosemantics.oto2.steps.shared.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.common.biology.TaxonGroup;

public class Collection implements Serializable, Comparable<Collection> {

	private int id = -1;
	private String name = "";
	private TaxonGroup taxonGroup;
	private String secret = "";
	private List<Term> terms = new LinkedList<Term>();

	public Collection() { }
	
	public Collection(String name, TaxonGroup taxonGroup, String secret, List<Term> terms) {
		this.name = name;
		this.taxonGroup = taxonGroup;
		this.secret = secret;
		this.setTerms(terms);
	}
	
	public Collection(int id, String name, TaxonGroup taxonGroup, String secret, List<Term> terms) {
		super();
		this.id = id;
		this.name = name;
		this.taxonGroup = taxonGroup;
		this.secret = secret;
		this.setTerms(terms);
	}

	public int getId() {
		return id;
	}
	
	public void setTerms(List<Term> terms) {
		this.terms = terms;
		for(Term term : terms)
			term.setCollectionId(id);
	}

	public boolean hasId() {
		return id != -1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public List<Term> getTerms() {
		return terms;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Collection other = (Collection) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public TaxonGroup getTaxonGroup() {
		return taxonGroup;
	}

	public void setTaxonGroup(TaxonGroup taxonGroup) {
		this.taxonGroup = taxonGroup;
	}

	@Override
	public int compareTo(Collection o) {
		return this.getId() - o.getId();
	}	
}
