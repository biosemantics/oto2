package edu.arizona.biosemantics.oto2.ontologize2.shared.model;

/**
 * Assumption: Terms are unique. However they can appear multiple times in a tree (e.g. superclass tree, term has multiple superclasses, duplicate term nodes necessary)
 * @author rodenhausen
 */
public class TermTreeNode extends TextTreeNode {

	private int id;
	private Term term;

	public TermTreeNode(Term term, int id) {
		this.id = id;
		this.term = term;
	}
	
	@Override
	public String getText() {
		return term.getValue();
	}

	public Term getTerm() {
		return term;
	}

	@Override
	public String getId() {
		return "term-" + term.getValue() + "_" + id;
	}
	
}