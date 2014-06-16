package edu.arizona.biosemantics.oto.oto.shared.model;

import java.io.Serializable;

public class Context implements Serializable {

	private int id = -1;
	private Term term;
	private String source;
	private String sentence;
	
	public Context() { }

	public Context(int id, Term term, String source, String sentence) {
		super();
		this.term = term;
		this.source = source;
		this.sentence = sentence;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public Term getTerm() {
		return term;
	}
	public void setTerm(Term term) {
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

}
