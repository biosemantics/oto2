package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;

public interface OntologySubmission {

	public static enum Type {
		ENTITY, QUALITY
	}
	
	public Ontology getOntology();

	public void setOntology(Ontology ontology);
	
	public Type getType();

	public String getClassIRI();
	
}
