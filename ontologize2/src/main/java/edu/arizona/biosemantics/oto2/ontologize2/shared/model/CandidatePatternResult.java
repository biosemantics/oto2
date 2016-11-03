package edu.arizona.biosemantics.oto2.ontologize2.shared.model;

import java.io.Serializable;
import java.util.List;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;

public class CandidatePatternResult implements Serializable {

	private String patternName;
	private List<Edge> relations;
	
	public CandidatePatternResult() { }

	public CandidatePatternResult(String patternName, List<Edge> relations) {
		this.patternName = patternName;
		this.relations = relations;
	}

	public String getPatternName() {
		return patternName;
	}

	public void setPatternName(String patternName) {
		this.patternName = patternName;
	}

	public List<Edge> getRelations() {
		return relations;
	}

	public void setRelations(List<Edge> relations) {
		this.relations = relations;
	}
	
}
