package edu.arizona.biosemantics.oto2.ontologize2.client.tree.node;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;

/**
 * Assumption: Terms are unique. However they can appear multiple times in a tree (e.g. superclass tree, term has multiple superclasses, duplicate term nodes necessary)
 * @author rodenhausen
 */
public class CandidateTreeNode extends TextTreeNode {

	private Candidate candidate;

	public CandidateTreeNode(Candidate candidate) {
		super(candidate.getText());
		this.candidate = candidate;
	}
	
	public Candidate getCandidate() {
		return candidate;
	}	
}