package edu.arizona.biosemantics.oto2.ontologize2.server.pattern;

import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

/**
 * in: {compound candidate}
 * out: non-spec. structure -> {last part} 
 * 
 * e.g. 
 * in: female ventral surface
 * out: non-specific -> surface
 * @author rodenhausen
 */
public class ReverseCompoundNonSpecificStructurePattern implements CandidatePattern {

	@Override
	public boolean matches(Collection collection, Candidate candidate) {
		String[] parts = candidate.getText().split(" ");
		return parts.length > 1;
	}

	@Override
	public List<Edge> getRelations(Collection collection, Candidate c) {
		List<Edge> edges = new LinkedList<Edge>();
		String[] parts = c.getText().split(" ");
		
		OntologyGraph g = collection.getGraph();
		if(parts.length > 1) {
			Edge e = new Edge(new Vertex("non-specific material anatomical entity"), new Vertex(parts[parts.length - 1]), 
					Type.SUBCLASS_OF, Origin.USER);		
			edges.add(e);
		}
		return edges;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}


}
