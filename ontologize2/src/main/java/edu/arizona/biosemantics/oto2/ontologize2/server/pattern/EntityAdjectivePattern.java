package edu.arizona.biosemantics.oto2.ontologize2.server.pattern;

import java.util.List;
import java.util.Set;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

/**
 * candidate: {term}y
 * in: material anatomical entity --transitively--> {term}
 * in: {something} --partof--> {term}  
 * out: {something} -->bearsquality--> {term}y
 * 
 * e.g.
 * candidate: thorny
 * in: material anatomical entity --> thorn
 * in: stem --partof--> thorn
 * out: stem -->bearsquality --> thorny
 * 
 * and reverse; Don't have bears quality relationship (yet?)
 * @author rodenhausen
 */
public class EntityAdjectivePattern implements CandidatePattern {

	@Override
	public List<Edge> getRelations(Collection collection, Candidate c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
