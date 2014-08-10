package edu.arizona.biosemantics.oto.oto.server.db;

import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto.oto.shared.model.Ontology;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class OntologyDAO {
	
	protected OntologyDAO() {}

	public List<Ontology> get(Term term) {
		List<Ontology> result = new LinkedList<Ontology>();
		result.add(new Ontology("category1", "definition1"));
		result.add(new Ontology("category2", "definition2"));
		result.add(new Ontology("category3", "definition3"));
		return result;
	}
	
}
