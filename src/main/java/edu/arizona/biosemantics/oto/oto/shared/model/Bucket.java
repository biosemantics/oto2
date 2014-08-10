package edu.arizona.biosemantics.oto.oto.shared.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class Bucket implements Serializable {

	private int id = -1;
	private int collectionId;
	private String name;
	private String description;
	private List<Term> terms = new LinkedList<Term>();
	
	public Bucket() { }
	
	public Bucket(int id, int collectionId, String text, String description) {
		super();
		this.id = id;
		this.collectionId = collectionId;
		this.name = text;
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
		//for(Term term : terms) 
		//	term.setBucket(this.getId());
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
		//term.setBucket(this);
		terms.add(term);
	}
	
	public int getId() {
		return id;
	}
	
	public boolean hasId() {
		return id != -1;
	}
	
	public int getCollectionId() {
		return collectionId;
	}

	public void setCollection(int collectionId) {
		this.collectionId = collectionId;
	}
	
	public void setId(int id) {
		this.id = id;
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
		Bucket other = (Bucket) obj;
		if (id != other.id)
			return false;
		return true;
	}	
	
}