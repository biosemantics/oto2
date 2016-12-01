package edu.arizona.biosemantics.oto2.ontologize2.server.pattern;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
 * in: female ventral surface (and exists similarly elsewhere)
 * in: material anatomical entity: surface 
 * out: non-specific -> surface
 * @author rodenhausen
 */
public class ReverseCompoundNonSpecificStructurePattern implements CandidatePattern {

	@Override
	public boolean matches(Collection collection, Candidate candidate) {
		String[] parts = candidate.getText().split("[-\\s]");
		return parts.length > 1;
	}

	@Override
	public List<Edge> getRelations(Collection collection, Candidate c) {
		List<Edge> edges = new LinkedList<Edge>();
		String[] parts = c.getText().split("[-\\s]");
		
		OntologyGraph g = collection.getGraph();
		if(parts.length > 1) {
			String lastPart = parts[parts.length - 1];
			if(g.getAllSources(new Vertex(lastPart), Type.SUBCLASS_OF).contains(new Vertex("material anatomical entity"))) {
				if(existsOtherCandidateWith(lastPart, collection, c)) {
					Edge e = new Edge(new Vertex("non-specific material anatomical entity"), new Vertex(lastPart), 
							Type.SUBCLASS_OF, Origin.USER);		
					edges.add(e);
				}
			}			
		}
		return edges;
	}

	private boolean existsOtherCandidateWith(String lastPart, Collection collection, Candidate c) {
		for(Candidate otherC : collection.getCandidates()) {
			if(!otherC.equals(c)) {
				String[] otherParts = otherC.getText().split("[-\\s]");
				if(otherParts.length > 1 && otherParts[otherParts.length - 1].equals(lastPart)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getName() {
		return "Candidate Non-specific Structure Pattern";
	}


}
