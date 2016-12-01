package edu.arizona.biosemantics.oto2.ontologize2.server.pattern;

import java.util.List;
import java.util.Set;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public interface CandidatePattern {
	
	public boolean matches(Collection collection, Candidate candidate);

	public List<Edge> getRelations(Collection collection, Candidate c);

	public String getName();
	
}
