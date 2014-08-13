package edu.arizona.biosemantics.oto.oto.shared.model;

import java.io.Serializable;

public class Term implements Serializable {

	private int id = -1;
	private String term;

	public Term() { }
	
	public Term(String term) {
		this.term = term;
	}
	
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

	public int getId() {
		return id;
	}

	public boolean hasId() {
		return id != -1;
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
		Term other = (Term) obj;
		if (id != other.id)
			return false;
		return true;
	}	
	
}
