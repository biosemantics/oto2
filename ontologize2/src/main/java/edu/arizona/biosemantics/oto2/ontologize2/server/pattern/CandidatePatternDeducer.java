package edu.arizona.biosemantics.oto2.ontologize2.server.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

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
			
		Map<String, Set<Edge>> patternNameEdgesMap = new HashMap<String, Set<Edge>>();
		for(CandidatePattern p : patterns) {
			List<Edge> edges = p.getRelations(collection, c);
			if(!edges.isEmpty()) {
				/*Iterator<Edge> iterator = edges.iterator();
				while(iterator.hasNext()) {
					if(types.contains(iterator.next().getType())) {
						iterator.remove();
					}
				}*/
				if(!edges.isEmpty()) {
					if(!patternNameEdgesMap.containsKey(p.getName()))
						patternNameEdgesMap.put(p.getName(), new HashSet<Edge>());
					Set<Edge> existing = patternNameEdgesMap.get(p.getName());
					for(Edge e : edges) 
						if(!existing.contains(e))
							existing.add(e);
				}
			}
		}
		
		/**
		 * do not apply hyphen pattern, it's used for coloration only
		for(String patternName : patternNameEdgesMap.keySet()) {
			for(Edge e : new ArrayList<Edge>(patternNameEdgesMap.get(patternName))) {
				for(Vertex v : new Vertex[] {e.getSrc(), e.getDest()}) {
					String normalized = v.getValue();
					String synonym = v.getValue().replaceAll("[-_\\s]", "-");
					if(!normalized.equals(synonym)) {
						patternNameEdgesMap.get(patternName).add(new Edge(new Vertex(normalized), new Vertex(synonym), Type.SYNONYM_OF, Origin.USER));
					}
				}
			}
		}
		*/
		
		for(String name : patternNameEdgesMap.keySet()) {
			List<Edge> relations = new ArrayList<Edge>(patternNameEdgesMap.get(name));
			Collections.sort(relations, new Comparator<Edge>() {
				@Override
				public int compare(Edge o1, Edge o2) {
					String string1 = o1.getType().toString() + "#" + o1.getSrc() + "#" + o1.getDest() + "#" + o1.getOrigin().toString();
					String string2 = o2.getType().toString() + "#" + o2.getSrc() + "#" + o2.getDest() + "#" + o1.getOrigin().toString();
					return string1.compareTo(string2);
				}
			});
			result.add(new CandidatePatternResult(name, relations));
			//for(Edge patternEdge:relations) System.out.println(name+" "+patternEdge.getSrc()+" "+patternEdge.getType()+" "+patternEdge.getDest());
		}
		return result;
	}

	
	public static void main(String[] args) throws Exception {
		//CollectionService cs = new CollectionService();
		//Collection c = cs.get(0, "");
		Collection c = new Collection();
		c.add(new Candidate("leaf","/material anatomical entity"));
		c.add(new Candidate("base","/material anatomical entity"));
		c.add(new Candidate("leaf base","/material anatomical entity"));
		c.add(new Candidate("flower base","/material anatomical entity"));
		CandidatePatternDeducer rd = new CandidatePatternDeducer(c);
		rd.deduce(c);
	}
}
