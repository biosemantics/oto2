package edu.arizona.biosemantics.oto2.ontologize2.shared.model;

import java.io.Serializable;
import java.util.UUID;

public class Term implements Serializable {

	private String value;
	private String partDisambiguator;
	private String classDisambiguator;
	
	private Term() { }
	
	public Term(String value) {
		this.value = value;
		this.partDisambiguator = "";
		this.classDisambiguator = "";
	}
	
	public Term(String value, String partDisambiguator, String classDisambiguator) {
		this.value = value;
		this.partDisambiguator = partDisambiguator;
		this.classDisambiguator = classDisambiguator;
	}
	
	public String getPartDisambiguator() {
		return partDisambiguator;
	}

	public String getClassDisambiguator() {
		return classDisambiguator;
	}

	public String getDisambiguatedValue() {
		String result = value;
		if(this.hasPartDisambiguator())
			result = this.partDisambiguator + " " + result;
		if(this.hasClassDisambiguator())
			result = result + " (" + this.classDisambiguator + ")";
		return result;
	}

	public String getValue() {
		return value;
	}

	/*@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getDisambiguatedValue() == null) ? 0 : this.getDisambiguatedValue().hashCode());
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
		if (this.getDisambiguatedValue() == null) {
			if (other.getDisambiguatedValue() != null)
				return false;
		} else if (!this.getDisambiguatedValue().equals(other.getDisambiguatedValue()))
			return false;
		return true;
	}*/

	@Override
	public String toString() {
		return this.getDisambiguatedValue();
	}

	public boolean hasPartDisambiguator() {
		return partDisambiguator != null && !partDisambiguator.isEmpty();
	}
	
	public boolean hasClassDisambiguator() {
		return classDisambiguator != null && !classDisambiguator.isEmpty();
	}
}

