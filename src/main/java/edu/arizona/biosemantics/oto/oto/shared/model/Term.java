package edu.arizona.biosemantics.oto.oto.shared.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Term implements Serializable {

	private int id = -1;
	private String term;
	private Bucket bucket; 
	private Set<Label> labels = new HashSet<Label>();
	private Set<Term> synonyms = new LinkedHashSet<Term>();
	private List<Context> contexts = new LinkedList<Context>();
	
	public Term() { }
	
	public Term(int id, String term) {
		super();
		this.id = id;
		this.term = term;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Set<Term> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(Set<Term> synonyms) {
		this.synonyms = synonyms;
	}
	
	public Set<Label> getLabels() {
		return labels;
	}

	public void setLabels(Set<Label> labels) {
		this.labels = labels;
	}

	public Bucket getBucket() {
		return bucket;
	}

	public void setBucket(Bucket bucket) {
		this.bucket = bucket;
	}	

	public void removeSynonym(Term term) {
		synonyms.remove(term);
	}

	public void addSynonym(Term term) {
		synonyms.add(term);
	}

	public int getId() {
		return id;
	}

	public boolean hasId() {
		return id != -1;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public void addContext(Context context) {
		context.setTerm(this);
		contexts.add(context);
	}	
	
	public void setContexts(List<Context> contexts) {
		this.contexts = contexts;
	}
	
	public List<Context> getContexts() {
		return contexts;
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
		Term other = (Term) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
	
}
