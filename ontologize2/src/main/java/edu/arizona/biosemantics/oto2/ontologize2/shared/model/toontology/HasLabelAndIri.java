package edu.arizona.biosemantics.oto2.ontologize2.shared.model.toontology;

public interface HasLabelAndIri {

	public boolean hasLabel();

	public String getLabel();

	public String getIri();

	public boolean hasIri();

	String getLabelAlternativelyIri();
}
