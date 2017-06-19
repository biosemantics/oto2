package edu.arizona.biosemantics.oto2.ontologize2.client.common;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent.RemoveMode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public class CompositeModifyEventForSynonymCreator {

	public CompositeModifyEventForSynonymCreator() {
		
	}
	
	public CompositeModifyEvent create(Vertex preferredTerm, Vertex synonym, Set<Edge> reattach) {
		List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
		OntologyGraph g = ModelController.getCollection().getGraph();
		List<Edge> in = g.getInRelations(preferredTerm, Type.SYNONYM_OF);
		if(in.isEmpty()) 
			result.add(new CreateRelationEvent(new Edge(g.getRoot(Type.SYNONYM_OF), preferredTerm, Type.SYNONYM_OF, Origin.USER)));
		
		if(reattach!=null&&reattach.size()>0){
			//whether they have parents in PART_OF relation
			boolean preferredTermAndSynonymHaveParents = isPreferredTermAndSynonymHaveParents(preferredTerm, synonym);
			if(!preferredTermAndSynonymHaveParents) {
				result.addAll(createEventsSynonymReduction(preferredTerm, synonym, reattach));
			} else {
				result.addAll(createEventsNonSpecificSynonyms(preferredTerm, synonym, reattach));
			}
		}
		result.add(new CreateRelationEvent(new Edge(preferredTerm, synonym, Type.SYNONYM_OF, Origin.USER)));	
		List<GwtEvent<?>> newResult = new LinkedList<GwtEvent<?>>();
		List<GwtEvent<?>> newCreateResult = new LinkedList<GwtEvent<?>>();
		Set<String> event = new HashSet();
		for(GwtEvent<?> e : result) {
			if(!event.contains(e.toString())){
				if(e instanceof CreateRelationEvent) newCreateResult.add(e);
				else newResult.add(e);
				event.add(e.toString());
			}
		}
		newCreateResult.addAll(newResult);//put create events first
		return new CompositeModifyEvent(newCreateResult);
	}
	
	private Collection<? extends GwtEvent<?>> createEventsNonSpecificSynonyms(Vertex preferredTerm, Vertex synonym, Set<Edge> reattach) {
		List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
		OntologyGraph g = ModelController.getCollection().getGraph();
		
		Vertex disambiguatedPreferred = new Vertex(this.getNonRootParent(preferredTerm).getValue() + " " + preferredTerm.getValue());
		Edge disambiguatePreferredEdge = new Edge(preferredTerm, 
				disambiguatedPreferred, 
				Type.SUBCLASS_OF, 
				Origin.USER);
		List<Edge> inSubclass = g.getInRelations(disambiguatedPreferred, Type.SUBCLASS_OF);
		List<Edge> inPartOf = g.getInRelations(disambiguatedPreferred, Type.PART_OF);
		Edge disambiguatedPreferredPartOfRootEdge = new Edge(g.getRoot(Type.PART_OF), disambiguatedPreferred, Type.PART_OF, Origin.USER);
		if(inPartOf.isEmpty())
			result.add(new CreateRelationEvent(disambiguatedPreferredPartOfRootEdge));
		if(inSubclass.size() == 1 && inSubclass.get(0).getSrc().equals(g.getRoot(Type.SUBCLASS_OF))) {
			result.add(new ReplaceRelationEvent(inSubclass.get(0), preferredTerm));
		} if(inSubclass.size() == 0) {
			result.add(new CreateRelationEvent(disambiguatePreferredEdge));
		} else {
			if(!g.existsRelation(disambiguatePreferredEdge)) {
				result.add(new CreateRelationEvent(disambiguatePreferredEdge));
			}
		}
		
		Vertex disambiguatedSynonym = new Vertex(this.getNonRootParent(synonym).getValue() + " " + preferredTerm.getValue());
		Edge disambiguateSynonymEdge = new Edge(preferredTerm, 
				disambiguatedSynonym, 
				Type.SUBCLASS_OF, 
				Origin.USER);
		inSubclass = g.getInRelations(disambiguatedSynonym, Type.SUBCLASS_OF);
		inPartOf = g.getInRelations(disambiguatedSynonym, Type.PART_OF);
		Edge disambiguatedSynonymPartOfRootEdge = new Edge(g.getRoot(Type.PART_OF), disambiguatedSynonym, Type.PART_OF, Origin.USER);
		if(inPartOf.isEmpty())
			result.add(new CreateRelationEvent(disambiguatedSynonymPartOfRootEdge));
		if(inSubclass.size() == 1 && inSubclass.get(0).getSrc().equals(g.getRoot(Type.SUBCLASS_OF))) {
			result.add(new ReplaceRelationEvent(inSubclass.get(0), preferredTerm));
		} if(inSubclass.size() == 0) {
			result.add(new CreateRelationEvent(disambiguateSynonymEdge));
		} else {
			if(!g.existsRelation(disambiguateSynonymEdge)) {
				result.add(new CreateRelationEvent(disambiguateSynonymEdge));
			}
		}
		
		//synonm
		//synonym subclass
		for(Edge e : g.getOutRelations(synonym, Type.SUBCLASS_OF)) {
			if(reattach.contains(e)) {
				Edge newEdge = new Edge(disambiguatedSynonym, e.getDest(), Type.SUBCLASS_OF, Origin.USER);
				if(!g.existsRelation(newEdge) && !e.getDest().equals(disambiguatedSynonym))
					result.add(new ReplaceRelationEvent(e, disambiguatedSynonym));
			} else {
				result.add(new RemoveRelationEvent(RemoveMode.NONE, e));
			}
		}
		Set<Vertex> exceptionsIn = new HashSet<Vertex>();
		exceptionsIn.add(preferredTerm);
		for(Edge e : g.getInRelations(synonym, Type.SUBCLASS_OF)) {
			if(reattach.contains(e)) {
				Edge newEdge = new Edge(e.getSrc(), disambiguatedSynonym, Type.SUBCLASS_OF, Origin.USER);
				if(!g.existsRelation(newEdge) && !exceptionsIn.contains(e.getSrc())) {
					List<Edge> in = g.getInRelations(disambiguatedSynonym);
					if(in.size() == 1 && in.get(0).getSrc().equals(g.getRoot(Type.SUBCLASS_OF))) {
						result.add(new ReplaceRelationEvent(in.get(0), e.getSrc()));
					} else {
						result.add(new CreateRelationEvent(newEdge));
					}
				}
			}
			result.add(new RemoveRelationEvent(RemoveMode.NONE, e));
		}
		
		//synonym parts
		for(Edge e : g.getOutRelations(synonym, Type.PART_OF)) {
			if(reattach.contains(e)) {
				Edge newEdge = new Edge(disambiguatedSynonym, e.getDest(), Type.PART_OF, Origin.USER);
				if(!g.existsRelation(newEdge) && !e.getDest().equals(disambiguatedSynonym))
					result.add(new ReplaceRelationEvent(e, disambiguatedSynonym));
			} else {
				result.add(new RemoveRelationEvent(RemoveMode.NONE, e));
			}
		}
		exceptionsIn = new HashSet<Vertex>();
		exceptionsIn.add(preferredTerm);
		for(Edge e : g.getInRelations(synonym, Type.PART_OF)) {
			if(reattach.contains(e)) {
				result.add(new ReplaceRelationEvent(disambiguatedSynonymPartOfRootEdge, e.getSrc()));
			}
			result.add(new RemoveRelationEvent(RemoveMode.NONE, e));
		}
		
		//preferred term
		//preferred term subclass
		Set<Vertex> exceptionsOut = new HashSet<Vertex>();
		exceptionsOut.add(synonym);
		for(Edge e : g.getOutRelations(preferredTerm, Type.SUBCLASS_OF)) {
			if(!e.equals(disambiguatePreferredEdge)) {
				if(!exceptionsOut.contains(e.getDest())) {
					Edge newEdge = new Edge(disambiguatedPreferred, e.getDest(), Type.SUBCLASS_OF, Origin.USER);
					if(!g.existsRelation(newEdge) && !e.getDest().equals(disambiguatedPreferred))
						result.add(new ReplaceRelationEvent(e, disambiguatedPreferred));
				} else {
					result.add(new RemoveRelationEvent(RemoveMode.NONE, e));
				}
			}
		}
		for(Edge e : g.getInRelations(preferredTerm, Type.SUBCLASS_OF)) {
			Edge newEdge = new Edge(e.getSrc(), disambiguatedPreferred, Type.SUBCLASS_OF, Origin.USER);
			if(!g.existsRelation(newEdge)) {
				List<Edge> in = g.getInRelations(disambiguatedPreferred);
				if(in.size() == 1 && in.get(0).getSrc().equals(g.getRoot(Type.SUBCLASS_OF))) {
					result.add(new ReplaceRelationEvent(in.get(0), e.getSrc()));
				} else {
					result.add(new CreateRelationEvent(new Edge(e.getSrc(), disambiguatedPreferred, Type.SUBCLASS_OF, Origin.USER)));
				}
			}
			result.add(new ReplaceRelationEvent(e, g.getRoot(Type.SUBCLASS_OF)));
		}
		
		//preferred term parts
		exceptionsOut = new HashSet<Vertex>();
		exceptionsOut.add(synonym);
		for(Edge e : g.getOutRelations(preferredTerm, Type.PART_OF)) {
			if(!exceptionsOut.contains(e.getDest())) {
				Edge newEdge = new Edge(disambiguatedPreferred, e.getDest(), Type.PART_OF, Origin.USER);
				if(!g.existsRelation(newEdge) && !e.getDest().equals(disambiguatedPreferred))
					result.add(new ReplaceRelationEvent(e, disambiguatedPreferred));
			} else {
				result.add(new RemoveRelationEvent(RemoveMode.NONE, e));
			}
		}
		for(Edge e : g.getInRelations(preferredTerm, Type.PART_OF)) {
			result.add(new ReplaceRelationEvent(disambiguatedPreferredPartOfRootEdge, e.getSrc()));
			result.add(new ReplaceRelationEvent(e, g.getRoot(Type.PART_OF))); //RemoveMode.NONE, e));
		}
		
		Edge synonymSubclassOfRootEdge = new Edge(g.getRoot(Type.SUBCLASS_OF), synonym, Type.SUBCLASS_OF, Origin.USER);
		Edge synonymPartOfRootEdge = new Edge(g.getRoot(Type.PART_OF), synonym, Type.PART_OF, Origin.USER);
		result.add(new RemoveRelationEvent(RemoveMode.RECURSIVE, synonymSubclassOfRootEdge));
		result.add(new RemoveRelationEvent(RemoveMode.RECURSIVE, synonymPartOfRootEdge));
		return result;
	}

	private Collection<? extends GwtEvent<?>> createEventsSynonymReduction(Vertex preferredTerm, Vertex synonym, Set<Edge> reattach) {
		List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
		OntologyGraph g = ModelController.getCollection().getGraph();
		for(Type type : new Type[] {Type.SUBCLASS_OF, Type.PART_OF}) {
			for(Edge e : g.getOutRelations(synonym, type)) {//replace out
				if(reattach.contains(e)) {
					Edge newEdge = new Edge(preferredTerm, e.getDest(), type, Origin.USER);
					if(!g.existsRelation(newEdge) && !e.getDest().equals(preferredTerm))
						result.add(new ReplaceRelationEvent(e, preferredTerm));
				} else {
					result.add(new RemoveRelationEvent(RemoveMode.RECURSIVE, e));
				}
			}
			for(Edge e : g.getInRelations(synonym, type)) {//replace in
				if(reattach.contains(e)) {
					Edge newEdge = new Edge(e.getSrc(), preferredTerm, type, Origin.USER);
					if(!g.existsRelation(newEdge) && !e.getSrc().equals(preferredTerm)) {
						List<Edge> preferredIn = g.getInRelations(preferredTerm);
						if(preferredIn.size() == 1 && preferredIn.get(0).getSrc().equals(g.getRoot(type))) {
							result.add(new ReplaceRelationEvent(preferredIn.get(0), e.getSrc()));
						} else {
							result.add(new CreateRelationEvent(new Edge(e.getSrc(), preferredTerm, type, Origin.USER)));
						}
					}
				}
				result.add(new RemoveRelationEvent(RemoveMode.RECURSIVE, e));
			}
		}
		
		Edge synonymSubclassOfRootEdge = new Edge(g.getRoot(Type.SUBCLASS_OF), synonym, Type.SUBCLASS_OF, Origin.USER);
		Edge synonymPartOfRootEdge = new Edge(g.getRoot(Type.PART_OF), synonym, Type.PART_OF, Origin.USER);
		result.add(new RemoveRelationEvent(RemoveMode.RECURSIVE, synonymSubclassOfRootEdge));//remove all synonym subclass
		result.add(new RemoveRelationEvent(RemoveMode.RECURSIVE, synonymPartOfRootEdge));//remove all synonym partof
		return result;
	}

	
	private Vertex getNonRootParent(Vertex v) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		List<Edge> in = g.getInRelations(v, Type.PART_OF);
		if(in.size() == 1 && !in.get(0).getSrc().equals(g.getRoot(Type.PART_OF)))
			return in.get(0).getSrc();
		return null;
	}
	

	private boolean isPreferredTermAndSynonymHaveParents(Vertex preferredTerm, Vertex synonym) {
		return getNonRootParent(preferredTerm) != null && getNonRootParent(synonym) != null;
	}
	
}
