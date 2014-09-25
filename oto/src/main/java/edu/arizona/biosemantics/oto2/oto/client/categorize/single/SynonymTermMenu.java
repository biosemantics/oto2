package edu.arizona.biosemantics.oto2.oto.client.categorize.single;

import java.util.LinkedList;
import java.util.List;

import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto2.oto.client.categorize.TermMenu;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermTreeNode;

public class SynonymTermMenu extends TermMenu {

	private Tree<TermTreeNode, String> tree;

	public SynonymTermMenu(EventBus eventBus, Collection collection, Label label, Tree<TermTreeNode, String> tree) {
		super(eventBus, collection, label);
		this.tree = tree;
	}
	
	@Override
	public void builtMenu(List<Term> terms) {
		//createMoveTo(terms);
		//createCopy(terms);
		createRename(terms);
		createRemove(terms);
		//createAddSynonom(terms);
		//createRemoveSynonym(terms);
		//createRemoveAllSynonyms(terms);
	}

	@Override
	public List<Term> getTerms() {
		final List<TermTreeNode> selected = tree.getSelectionModel().getSelectedItems();
		final List<Term> terms = new LinkedList<Term>();
		for(TermTreeNode node : selected) 
			terms.add(node.getTerm());
		return terms;
	}

}
