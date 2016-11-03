package edu.arizona.biosemantics.oto2.ontologize2.server.pattern;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.CandidatePatternResult;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;

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
		for(CandidatePattern p : patterns) {
			if(p.matches(collection, c)) {
				result.add(new CandidatePatternResult(p.getName(), p.getRelations(collection, c)));
			}
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
