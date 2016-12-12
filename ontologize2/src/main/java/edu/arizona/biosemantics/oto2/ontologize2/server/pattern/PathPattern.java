package edu.arizona.biosemantics.oto2.ontologize2.server.pattern;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

/**
 * in: {candidate with a path}
 * out: {path-part-parent} -> {path-part-child}; for all consecutive part pairs including the terminal candidate
 * 
 * e.g. 
 * candidate: /quality/architecture_or_shape/lobed
 * out: quality -> architecture
 * out: quality -> shape
 * out: architecture -> lobed
 * out: shape -> lobed
 * @author rodenhausen
 */
public class PathPattern implements CandidatePattern {

	@Override
	public List<Edge> getRelations(Collection collection, Candidate c) {
		List<Edge> result = new LinkedList<Edge>();
		if(c.getPath().equals("/Other") || c.getPath().equals("/OTHER") || c.getPath().equals("/other"))
			return result;
		OntologyGraph g = collection.getGraph();
		String normalizedFull = c.getText().replaceAll("[-_\\s]", " ");
		
		List<Vertex> superclasses = new LinkedList<Vertex>();
		Vertex superclass = g.getRoot(Type.SUBCLASS_OF);
		superclasses.add(superclass);
		if(c.getPath() != null && !c.getPath().trim().isEmpty()) {
			for(String p : c.getPath().split("/")) {
				if(!p.trim().isEmpty()) {
					List<Vertex> subclasses = new LinkedList<Vertex>();
					for(String part : p.split("_or_")) {
						part = part.replaceAll("[-_\\s]", " ");
						Vertex subclass = new Vertex(part);
						subclasses.add(subclass);
						if(!part.trim().isEmpty()) {
							for(Vertex superclazz : superclasses) {
								Edge e = new Edge(superclazz, subclass, Type.SUBCLASS_OF, Origin.USER);
								result.add(e);
							}
						}
					}
					superclasses = subclasses;
				}
			}
			for(Vertex superclazz : superclasses) {
				Edge e = new Edge(superclazz, new Vertex(normalizedFull), Type.SUBCLASS_OF, Origin.USER);
				result.add(e);
			}
		}
		return result;
	}

	@Override
	public String getName() {
		return "Ontology Path Pattern";
	}

}
