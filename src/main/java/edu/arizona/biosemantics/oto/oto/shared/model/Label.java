package edu.arizona.biosemantics.oto.oto.shared.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Label implements Serializable {

	private int id = - 1;
	private Collection collection;
	private String name;
	private String description;
	private List<Term> terms = new LinkedList<Term>();
	
	public Label() { }
	
	public Label(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public Label(int id, Collection collection, String name, String description) {
		super();
		this.id = id;
		this.collection = collection;
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String text) {
		this.name = text;
	}

	public List<Term> getTerms() {
		return new LinkedList<Term>(terms);
	}

	public void setTerms(List<Term> terms) {
		for(Term term : terms) 
			term.setLabel(this);
		this.terms = terms;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void removeTerm(Term term) {
		terms.remove(term);
	}
	
	public void addTerm(Term term) {
		terms.add(term);
	}
	
	public int getId() {
		return id;
	}
	
	public boolean hasId() {
		return id != -1;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	public Collection getCollection() {
		return collection;
	}

	public void setId(int id) {
		this.id = id;
	}	
	
	
}