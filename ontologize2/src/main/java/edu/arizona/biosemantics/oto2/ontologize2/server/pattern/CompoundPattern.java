package edu.arizona.biosemantics.oto2.ontologize2.server.pattern;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

/**
 * in: {compound candidate}
 * out: {part} -> {candidate}; for all subsets of consecutive parts 
 * 
 * e.g. 
 * candidate: plant stem apex
 * out: apex -> plant stem apex
 * out: stem apex -> plant stem apex
 * out: stem -> plant stem
 * 
 * similar for part relations if given as material anatomical entity
 * e.g. 
 * candidate: plant stem apex
 * out: plant -> stem apex
 * out: plant stem -> apex
 * out: stem -> apex
 * @author rodenhausen
 */
public class CompoundPattern implements CandidatePattern {

	@Override
	public boolean matches(Collection collection, Candidate candidate) {
		String[] parts = candidate.getText().split(" ");
		return parts.length > 1;
	}
	
	@Override
	public List<Edge> getRelations(Collection collection, Candidate c) {
		OntologyGraph g = collection.getGraph();
		
		List<Edge> result = new LinkedList<Edge>();
		String[] parts = c.getText().split(" ");
		if(parts.length > 1) {
			for(int superclassStart = 1; superclassStart < parts.length; superclassStart++) {
				for(int subclassStart = 0; subclassStart < superclassStart; subclassStart++) {
					String superclass = createTerm(parts, superclassStart, parts.length);
					String subclass = createTerm(parts, subclassStart, parts.length);
					Edge e = new Edge(new Vertex(superclass), new Vertex(subclass), Type.SUBCLASS_OF, Origin.USER);
					result.add(e);
				}
			}
			
			if(c.getPath().contains("/material anatomical entity/")) {
				for(int partStart = 1; partStart < parts.length; partStart++) {
					for(int parentStart = 0; parentStart < partStart; parentStart++) {
						String parent = createTerm(parts, parentStart, partStart);
						String part = createTerm(parts, partStart, parts.length);
						Edge e = new Edge(new Vertex(parent), new Vertex(part), Type.PART_OF, Origin.USER);
						result.add(e);
					}
				}
			}
		}
		return result;
	}
	
	private String createTerm(String[] parts, int start, int end) {
		StringBuilder sb = new StringBuilder();
		for(int i=start; i<end; i++) {
			sb.append(parts[i] + " ");
		}
		return sb.toString().trim();
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

}
