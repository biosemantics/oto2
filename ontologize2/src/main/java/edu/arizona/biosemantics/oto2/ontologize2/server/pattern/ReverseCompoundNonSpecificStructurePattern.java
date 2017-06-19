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
	public List<Edge> getRelations(Collection collection, Candidate c) {
		List<Edge> edges = new LinkedList<Edge>();
		String[] parts = c.getText().split("[-_\\s]");
		OntologyGraph g = collection.getGraph();
		if(parts.length > 1) {
			String lastPart = parts[parts.length - 1];
			if(isMaterialEntity(lastPart, collection)) {
				if(existsOtherCandidateWith(lastPart, collection, c)) {
					Edge e = new Edge(new Vertex("non-specific material anatomical entity"), new Vertex(lastPart), 
							Type.SUBCLASS_OF, Origin.USER);		
					edges.add(e);
				}
				
				//detect PART_OF pattern
				//String woLastPart = c.getText().substring(0, c.getText().length()-lastPart.length()-1);
				//System.out.println(woLastPart+"over");
				//if(g.getAllSources(new Vertex(woLastPart), Type.SUBCLASS_OF).contains(new Vertex("material anatomical entity"))&&
				//		existsPartOtherCandidateWith(woLastPart, collection, c)) {
//				if(g.getAllSources(new Vertex(woLastPart), Type.SUBCLASS_OF).contains(new Vertex("material anatomical entity"))&&
//					 existsPartOtherCandidateWith(woLastPart, collection, c)) {
//					Edge partofRel = new Edge(new Vertex(woLastPart), new Vertex(c.getText()), 
//							Type.PART_OF, Origin.USER);		
//					edges.add(partofRel);
//				}
			}
		}else if(existsOtherCandidateSize(c.getText(), collection)>1){
			Edge e = new Edge(new Vertex("non-specific material anatomical entity"), new Vertex(c.getText()), 
					Type.SUBCLASS_OF, Origin.USER);		
			edges.add(e);
		}
		return edges;
	}

	private boolean isMaterialEntity(String term, Collection collection) {
		OntologyGraph g = collection.getGraph();
		return 
				collection.getCandidates().getCandidate(term).getPath().contains("/material anatomical entity")
				||g.getAllSources(new Vertex(term), Type.SUBCLASS_OF).contains(new Vertex("material anatomical entity"));
	}
	
	
	private boolean existsOtherCandidateWith(String lastPart, Collection collection, Candidate c) {
		for(Candidate otherC : collection.getCandidates()) {
			if(!otherC.equals(c)&&isMaterialEntity(otherC.getText(),collection)) {
				String[] otherParts = otherC.getText().split("[-_\\s]");
				if(otherParts.length > 1 && otherParts[otherParts.length - 1].equals(lastPart)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private int existsOtherCandidateSize(String term, Collection collection) {
		int size =0;
		for(Candidate otherC : collection.getCandidates()) {
			if(isMaterialEntity(otherC.getText(),collection)) {
				String[] otherParts = otherC.getText().split("[-_\\s]");
				if(otherParts.length > 1 && otherParts[otherParts.length - 1].equals(term)) {
					size++;
				}
			}
		}
		return size;
	}
	
	
	
	private boolean existsPartOtherCandidateWith(String woLastPart, Collection collection, Candidate c) {
		for(Candidate otherC : collection.getCandidates()) {
			if(!otherC.equals(c)&&otherC.getText().equals(woLastPart)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getName() {
		return "Candidate Non-specific Structure Pattern";
	}


}
