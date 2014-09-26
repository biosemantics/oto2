package edu.arizona.biosemantics.oto2.oto.client.common.dnd;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class MainTermSynonymsLabelDnd extends TermLabelDnd {

	public static class MainTermSynonyms {
		private Term mainTerm;
		private List<Term> synonyms;

		public MainTermSynonyms(Term mainTerm, List<Term> synonyms) {
			this.mainTerm = mainTerm;
			this.synonyms = synonyms;
		}

		public Term getMainTerm() {
			return mainTerm;
		}

		public void setMainTerm(Term mainTerm) {
			this.mainTerm = mainTerm;
		}

		public List<Term> getSynonyms() {
			return synonyms;
		}

		public void setSynonyms(List<Term> synonyms) {
			this.synonyms = synonyms;
		}
		
	}
	
	protected List<MainTermSynonyms> mainTermSynonyms = new LinkedList<MainTermSynonyms>();
	
	public MainTermSynonymsLabelDnd(Widget source, Term mainTerm, List<Term> synonyms, Label label) {
		super(source);
		List<Term> terms = new LinkedList<Term>();
		terms.add(mainTerm);
		terms.addAll(synonyms);
		this.setTerms(terms);
		this.mainTermSynonyms.add(new MainTermSynonyms(mainTerm, synonyms));
		List<Label> labels = new LinkedList<Label>();
		labels.add(label);
		this.setLabels(labels);
	}
	
	public MainTermSynonymsLabelDnd(Widget source, List<MainTermSynonyms> mainTermSynonyms, Label label) {
		super(source);
		List<Term> terms = new LinkedList<Term>();
		for(MainTermSynonyms entry : mainTermSynonyms) {
			terms.add(entry.getMainTerm());
			terms.addAll(entry.getSynonyms());
		}
		this.setTerms(terms);
		this.mainTermSynonyms = mainTermSynonyms;
		List<Label> labels = new LinkedList<Label>();
		labels.add(label);
		this.setLabels(labels);
	}

	public List<MainTermSynonyms> getMainTermSynonyms() {
		return mainTermSynonyms;
	}

	public void setMainTerm(List<MainTermSynonyms> mainTermSynonyms) {
		this.mainTermSynonyms = mainTermSynonyms;
	}

	public List<Term> getMainTerms() {
		List<Term> mainTerms = new LinkedList<Term>();
		for(MainTermSynonyms entry : mainTermSynonyms) {
			mainTerms.add(entry.getMainTerm());
		}
		return mainTerms;
	}
	
}
