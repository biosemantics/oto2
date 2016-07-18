package edu.arizona.biosemantics.oto2.ontologize2.shared.model.toontology;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Type;


public interface OntologySubmission {
	
	public Ontology getOntology();

	public void setOntology(Ontology ontology);
	
	public String getClassIRI();
	
	public Type getType();
	
}
