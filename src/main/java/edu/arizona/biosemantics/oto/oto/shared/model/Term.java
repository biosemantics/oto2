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
	private int bucketId;
	private int labelId;
	//private Bucket bucket; would cause circular reference
	//private Label label; would cause circular reference
	private Set<Term> synonyms = new LinkedHashSet<Term>();
	private List<Context> contexts = new LinkedList<Context>();
	
	public Term() { }
	
	public Term(int id, String term, int bucketId/*Bucket bucket*/) {
		super();
		this.id = id;
		this.term = term;
		//.bucket = bucket;
		this.bucketId = bucketId;
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

	
	
	/*public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}*/

	/*
	public Bucket getBucket() {
		return bucket;
	}

	public void setBucket(Bucket bucket) {
		this.bucket = bucket;
	}
	*/
	

	public int getLabelId() {
		return labelId;
	}

	public void setLabelId(int labelId) {
		this.labelId = labelId;
	}

	public void removeSynonym(Term term) {
		synonyms.remove(term);
	}

	public int getBucketId() {
		return bucketId;
	}

	public void setBucketId(int bucketId) {
		this.bucketId = bucketId;
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
