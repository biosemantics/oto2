package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

public class PartOfTreeNode extends TextTreeNode {

	private PartOf partOf;

	public PartOfTreeNode(PartOf partOf) {
		this.partOf = partOf;
	}

	@Override
	public String getText() {
		return partOf.getLabelAlternativelyIri();
	}

	@Override
	public String getId() {
		return partOf.getLabelAlternativelyIri();
	}

	public PartOf getPartOf() {
		return partOf;
	}

}
