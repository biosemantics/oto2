package edu.arizona.biosemantics.oto2.ontologize2.server.pattern;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

/**
 * in: non-specific -> {last part}
 * out: {last part} -> {candidate} 
 * 
 * e.g. 
 * candidate: female ventral surface
 * in: non-specific -> surface
 * out: surface -> female ventral surface
 * @author rodenhausen
 */
public class CompoundNonSpecificStructurePattern implements CandidatePattern {

	@Override
	public List<Edge> getRelations(Collection collection, Candidate c) {
		List<Edge> edges = new LinkedList<Edge>();
		
		String[] parts = c.getText().split("[-_\\s]");
		String normalizedFull = c.getText().replaceAll("[-_\\s]", " ");
		
		OntologyGraph g = collection.getGraph();
		if(parts.length > 1) {
			Edge e = new Edge(new Vertex("non-specific material anatomical entity"), new Vertex(parts[parts.length - 1]), 
					Type.SUBCLASS_OF, Origin.USER);		
			if(g.existsRelation(e)) {
				edges.add(new Edge(new Vertex(parts[parts.length - 1]), new Vertex(normalizedFull), Type.SUBCLASS_OF, Origin.USER));
			}
		}
		return edges;
	}

	@Override
	public String getName() {
		return "Compound Pattern";
	}
}
