package edu.arizona.biosemantics.oto2.ontologize2.server.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.CandidatePatternResult;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public class CandidatePatternDeducer {

	private Collection collection;
	private LinkedList<CandidatePattern> patterns;

	public CandidatePatternDeducer(Collection collection) {
		this.collection = collection;
		
		patterns = new LinkedList<CandidatePattern>();
		patterns.add(new CompoundColorPattern());
		patterns.add(new PathPattern());
		patterns.add(new CompoundPattern());
		patterns.add(new PredefinedRelationsPattern());
		patterns.add(new CompoundNonSpecificStructurePattern());
		patterns.add(new ReverseCompoundNonSpecificStructurePattern());
	}

	public Map<Candidate, List<CandidatePatternResult>> deduce(Collection collection) {
		Map<Candidate, List<CandidatePatternResult>> result = new HashMap<Candidate, List<CandidatePatternResult>>();
		
		for(Candidate c : collection.getCandidates()) {
			result.put(c, deduce(collection, c));
		}
		return result;
	}
	
	public List<CandidatePatternResult> deduce(Collection collection, Candidate c) {
		List<CandidatePatternResult> result = new LinkedList<CandidatePatternResult>();
		/*Set<Type> types = new HashSet<Type>(Arrays.asList(Type.values()));
		if(!c.getPath().equals("/Other") || c.getPath().equals("/OTHER") || c.getPath().equals("/other")) {
			types.remove(Type.SUBCLASS_OF);
		}*/
			
		Map<String, List<Edge>> patternNameEdgesMap = new HashMap<String, List<Edge>>();
		for(CandidatePattern p : patterns) {
			if(p.matches(collection, c)) {
				List<Edge> edges = p.getRelations(collection, c);
				/*Iterator<Edge> iterator = edges.iterator();
				while(iterator.hasNext()) {
					if(types.contains(iterator.next().getType())) {
						iterator.remove();
					}
				}*/
				if(!edges.isEmpty()) {
					if(!patternNameEdgesMap.containsKey(p.getName()))
						patternNameEdgesMap.put(p.getName(), new ArrayList<Edge>());
					List<Edge> existing = patternNameEdgesMap.get(p.getName());
					for(Edge e : edges) 
						if(!existing.contains(e))
							existing.add(e);
				}
			}
		}
		for(String name : patternNameEdgesMap.keySet()) {
			result.add(new CandidatePatternResult(name, patternNameEdgesMap.get(name)));
		}
		return result;
	}

	
	public static void main(String[] args) throws Exception {
		//CollectionService cs = new CollectionService();
		//Collection c = cs.get(0, "");
		Collection c = new Collection();
		c.add(new Candidate("stem fruit shell color"));
		CandidatePatternDeducer rd = new CandidatePatternDeducer(c);
		rd.deduce(null);
	}
}
