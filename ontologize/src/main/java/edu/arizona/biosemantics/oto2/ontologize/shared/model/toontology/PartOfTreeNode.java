package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

public class PartOfTreeNode extends TextTreeNode {

	private PartOf partOf;

	public PartOfTreeNode(PartOf partOf) {
		this.partOf = partOf;
	}

	@Override
	public String getText() {
		return partOf.getLabel();
	}

	@Override
	public String getId() {
		return String.valueOf(partOf.toString());
	}

	public PartOf getPartOf() {
		return partOf;
	}

}
