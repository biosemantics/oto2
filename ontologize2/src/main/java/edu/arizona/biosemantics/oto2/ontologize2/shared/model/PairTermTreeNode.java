package edu.arizona.biosemantics.oto2.ontologize2.shared.model;

import java.util.List;

import com.sencha.gxt.data.shared.TreeStore.TreeNode;

/**
 * Assumption: Terms are unique. However they can appear multiple times in a tree (e.g. superclass tree, term has multiple superclasses, duplicate term nodes necessary)
 * @author rodenhausen
 */
public class PairTermTreeNode extends TextTreeNode {

	private PairTermTreeNode superClassTermNode;
	private Term subClassTerm;

	public PairTermTreeNode(PairTermTreeNode superClassTermNode, Term subClassTerm) {
		this.superClassTermNode = superClassTermNode;
		this.subClassTerm = subClassTerm;
	}
	
	@Override
	public String getText() {
		return subClassTerm.getDisambiguatedValue();
	}

	public Term getTerm() {
		return subClassTerm;
	}
	
	public Term getSuperclass() {
		return superClassTermNode.getTerm();
	}
	
	@Override
	public String getId() {
		if(superClassTermNode==null){
			return subClassTerm.getDisambiguatedValue();
		}else{
			return superClassTermNode.getId()+"-" + subClassTerm.getDisambiguatedValue();
		}
	}
	
	
}