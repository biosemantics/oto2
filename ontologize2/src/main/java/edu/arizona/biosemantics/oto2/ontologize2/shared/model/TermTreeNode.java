package edu.arizona.biosemantics.oto2.ontologize2.shared.model;

import java.util.List;

import com.sencha.gxt.data.shared.TreeStore.TreeNode;

/**
 * Assumption: Terms are unique. However they can appear multiple times in a tree (e.g. superclass tree, term has multiple superclasses, duplicate term nodes necessary)
 * @author rodenhausen
 */
public class TermTreeNode extends TextTreeNode {

	private Term term;

	public TermTreeNode(Term term) {
		this.term = term;
	}
	
	@Override
	public String getText() {
		return term.getDisambiguatedValue();
	}

	public Term getTerm() {
		return term;
	}
	
	@Override
	public String getId() {
		return "term-" + term.getDisambiguatedValue();
	}
	
}