package edu.arizona.biosemantics.oto2.oto.client.categorize.single;

import java.util.LinkedList;
import java.util.List;

import com.google.web.bindery.event.shared.EventBus;

import edu.arizona.biosemantics.oto2.oto.client.categorize.TermMenu;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class MainTermMenu extends TermMenu {

	private Term mainTerm;

	public MainTermMenu(EventBus eventBus, Collection collection, Label label, Term mainTerm) {
		super(eventBus, collection, label);
		this.mainTerm = mainTerm;
	}

	@Override
	public void builtMenu(List<Term> terms) {
		createMoveTo(terms);
		//createCopy(terms);
		createRename(terms);
		createRemove(terms);
		createAddSynonom(terms);
		createRemoveSynonym(terms);
		createRemoveAllSynonyms(terms);
	}
	
	@Override
	public List<Term> getTerms() {
		List<Term> terms = new LinkedList<Term>();
		terms.add(mainTerm);
		return terms;
	}

}
