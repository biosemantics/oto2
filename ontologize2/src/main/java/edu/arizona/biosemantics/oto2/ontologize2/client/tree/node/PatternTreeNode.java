package edu.arizona.biosemantics.oto2.ontologize2.client.tree.node;

import java.util.List;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.CandidatePatternResult;

public class PatternTreeNode extends TextTreeNode {

	private CandidatePatternResult pattern;

	public PatternTreeNode(CandidatePatternResult pattern) {
		super(pattern.getPatternName());
		this.pattern = pattern;
	}

}
