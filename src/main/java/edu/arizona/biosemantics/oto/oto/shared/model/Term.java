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
	private Label label;
	private Set<Term> synonyms = new LinkedHashSet<Term>();
	private List<Context> contexts = new LinkedList<Context>();
	
	public Term() { }
	
	public Term(int id, String term, Bucket bucket) {
		super();
		this.id = id;
		this.term = term;
		this.bucket = bucket;
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

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
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
	
}
