package edu.arizona.biosemantics.oto2.oto.client.categorize.single;

import java.util.LinkedList;
import java.util.List;

import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto2.oto.client.categorize.TermMenu;
import edu.arizona.biosemantics.oto2.oto.client.common.SelectedTermsExtractor;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.SelectedTerms;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermTreeNode;

public class SynonymTermMenu extends TermMenu {

	private Tree<TermTreeNode, String> tree;
	private SelectedTermsExtractor selectedTermsExtractor = new SelectedTermsExtractor();

	public SynonymTermMenu(EventBus eventBus, Collection collection, Label label, Tree<TermTreeNode, String> tree) {
		super(eventBus, collection, label);
		this.tree = tree;
	}
	
	@Override
	public void buildMenu(List<Term> explicitSelection, SelectedTerms selectedTerms) {
		createMoveTo(explicitSelection, selectedTerms);
		createCopy(explicitSelection, selectedTerms);
		createRename(explicitSelection, selectedTerms);
		createRemove(explicitSelection, selectedTerms);
		//createAddSynonom(explicitSelection, selectedTerms);
		//createRemoveSynonym(explicitSelection, selectedTerms);
		//createRemoveAllSynonyms(explicitSelection, selectedTerms);
	}

	@Override
	public SelectedTerms getSelectedTerms() {
		return selectedTermsExtractor.getSelectedTerms(tree);
	}

	@Override
	public List<Term> getExplicitlySelectedTerms() {
		List<Term> result = new LinkedList<Term>();
		for(TermTreeNode node : tree.getSelectionModel().getSelectedItems())
			result.add(node.getTerm());
		return result;
	}

}
