package edu.arizona.biosemantics.oto2.oto.shared.model;

import java.io.Serializable;

public enum TermType implements Serializable {
	KNOWN_IN_GLOSSARY(0), UNKNOWN(1);
	
	private int id;

	private TermType() { 
	}
	
	private TermType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public static TermType valueOfId(int id) {
		switch(id) {
		case 0:
			return KNOWN_IN_GLOSSARY;
		case 1:
			return UNKNOWN;
		}
		return UNKNOWN;
	}
}
