package edu.arizona.biosemantics.oto2.ontologize2.shared.model;

public class TermDisambiguator {

	public static Term disambiguatePart(Term term, Term disambiguationTerm) {
		Term result = new Term(term.getValue(), 
				(disambiguationTerm.getDisambiguatedValue() + " " + term.getClassDisambiguator()).trim(), 
				term.getClassDisambiguator());
		return result;
	}
	
	public static Term disambiguateClass(Term term, Term disambiguationTerm) {
		Term result = null;
		if(term.hasClassDisambiguator()) 
			result = new Term(term.getValue(), 
					term.getPartDisambiguator(),
					(term.getClassDisambiguator() + " (" + disambiguationTerm.getDisambiguatedValue() + ")").trim());
		else
			result = new Term(term.getValue(), 
					term.getPartDisambiguator(),
					disambiguationTerm.getDisambiguatedValue());
		return result;
	}
	
}
