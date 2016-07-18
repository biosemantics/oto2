package edu.arizona.biosemantics.oto2.ontologize2.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreatePartEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateSubclassEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemovePartEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveSubclassEvent;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TermDisambiguator;

public class CollectionDisambiguator {

	public static void disambiguateAllClasses() {
		edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection collection = ModelController.getCollection();
		List<List<Boolean>> incidenceMatrix = new ArrayList<List<Boolean>>(collection.getTerms().size());
		Set<String> roots = new HashSet<String>();
		Map<String, Integer> indexMap = new HashMap<String, Integer>();
		Map<Integer, String> reverseIndexMap = new HashMap<Integer, String>();
		populateDataStructuresFromRelationMap(collection.getSubclasses(), incidenceMatrix, roots, indexMap, reverseIndexMap);
		
		Set<String> disambiguated = new HashSet<String>();
		for(String root : roots) 
			for(Term subclassTerm : collection.getSubclasses(collection.getTerm(root)))
				disambiguateClass(subclassTerm, incidenceMatrix, indexMap, reverseIndexMap, disambiguated);
	}
	
	private static List<GwtEvent<?>> disambiguateClass(Term subclassTerm, List<List<Boolean>> incidenceMatrix, Map<String, Integer> indexMap,
			Map<Integer, String> reverseIndexMap, Set<String> disambiguated) {
		List<GwtEvent<?>> result = new ArrayList<GwtEvent<?>>();
		if(disambiguated.contains(subclassTerm.getDisambiguatedValue()))
			return result;
		else
			disambiguated.add(subclassTerm.getDisambiguatedValue());
		
		edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection collection = ModelController.getCollection();
		List<Term> superclassTerms = getParents(subclassTerm, incidenceMatrix, indexMap, reverseIndexMap);
		if(superclassTerms.size() > 1) {
			List<Term> newlyCreatedClasses = new ArrayList<Term>(superclassTerms.size());
			List<Term> subsubclassTerms = collection.getSubclasses(subclassTerm);
			for(Term superclassTerm : superclassTerms) {
				disambiguateClass(superclassTerm, incidenceMatrix, indexMap, reverseIndexMap, disambiguated);
				
				Term disambiguatedTerm = TermDisambiguator.disambiguateClass(subclassTerm, superclassTerm);
				adaptDataStructuresForNewTerm(disambiguatedTerm, incidenceMatrix, indexMap, reverseIndexMap);
				disambiguated.add(disambiguatedTerm.getDisambiguatedValue());
				collection.createTerm(disambiguatedTerm);
				
				collection.removeSubclass(superclassTerm, subclassTerm);
				incidenceMatrix.get(indexMap.get(superclassTerm.getDisambiguatedValue())).set(indexMap.get(subclassTerm.getDisambiguatedValue()), false);
				result.add(new RemoveSubclassEvent(superclassTerm, subclassTerm));
				collection.removeSubclass(subclassTerm, subsubclassTerms);
				for(Term subsubclassTerm : subsubclassTerms)
					incidenceMatrix.get(indexMap.get(subclassTerm.getDisambiguatedValue())).set(indexMap.get(subsubclassTerm.getDisambiguatedValue()), false);
				result.add(new RemoveSubclassEvent(subclassTerm, subsubclassTerms));
				collection.createSubclass(superclassTerm, disambiguatedTerm);
				newlyCreatedClasses.add(disambiguatedTerm);
				incidenceMatrix.get(indexMap.get(superclassTerm.getDisambiguatedValue())).set(indexMap.get(disambiguatedTerm.getDisambiguatedValue()), true);
				result.add(new CreateSubclassEvent(superclassTerm, disambiguatedTerm));
				collection.createSubclass(disambiguatedTerm, subsubclassTerms);
				for(Term subsubclassTerm : subsubclassTerms)
					incidenceMatrix.get(indexMap.get(disambiguatedTerm.getDisambiguatedValue())).set(indexMap.get(subsubclassTerm.getDisambiguatedValue()), true);
				result.add(new CreateSubclassEvent(disambiguatedTerm, subsubclassTerms));
			}
			for(Term newlyCreatedClass : newlyCreatedClasses) 
				for(Term subclass : collection.getSubclasses(newlyCreatedClass)) 
					disambiguateClass(subclass, incidenceMatrix, indexMap, reverseIndexMap, disambiguated);
		}
		for(Term subclass : collection.getSubclasses(subclassTerm)) 
			disambiguateClass(subclass, incidenceMatrix, indexMap, reverseIndexMap, disambiguated);
		return result;
	}

	public static void diambiguateAllParts() {
		edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection collection = ModelController.getCollection();
		List<List<Boolean>> incidenceMatrix = new ArrayList<List<Boolean>>(collection.getTerms().size());
		Set<String> roots = new HashSet<String>();
		Map<String, Integer> indexMap = new HashMap<String, Integer>();
		Map<Integer, String> reverseIndexMap = new HashMap<Integer, String>();
		populateDataStructuresFromRelationMap(collection.getParts(), incidenceMatrix, roots, indexMap, reverseIndexMap);
		
		Set<String> disambiguated = new HashSet<String>();
		for(String root : roots) 
			for(Term partTerm : collection.getParts(collection.getTerm(root))) {
				disambiguatePart(partTerm, incidenceMatrix, indexMap, reverseIndexMap, disambiguated);
			}
	}
	
	private static List<GwtEvent<?>> disambiguatePart(Term partTerm, List<List<Boolean>> incidenceMatrix, Map<String, Integer> indexMap, Map<Integer, String> reverseIndexMap, Set<String> disambiguated) {
		List<GwtEvent<?>> result = new ArrayList<GwtEvent<?>>();
		if(disambiguated.contains(partTerm.getDisambiguatedValue()))
			return result;
		else
			disambiguated.add(partTerm.getDisambiguatedValue());
		
		edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection collection = ModelController.getCollection();
		List<Term> parentTerms = getParents(partTerm, incidenceMatrix, indexMap, reverseIndexMap);
		if(parentTerms.size() > 1) {
			List<Term> newlyCreatedClasses = new ArrayList<Term>(parentTerms.size());
			for(Term parentTerm : parentTerms) {
				disambiguatePart(parentTerm, incidenceMatrix, indexMap, reverseIndexMap, disambiguated);
				
				Term disambiguatedTerm = TermDisambiguator.disambiguatePart(partTerm, parentTerm);
				adaptDataStructuresForNewTerm(disambiguatedTerm, incidenceMatrix, indexMap, reverseIndexMap);
				disambiguated.add(disambiguatedTerm.getDisambiguatedValue());
				collection.createTerm(disambiguatedTerm);
				
				collection.createPart(parentTerm, disambiguatedTerm);
				result.add(new CreatePartEvent(parentTerm, disambiguatedTerm));
				newlyCreatedClasses.add(disambiguatedTerm);
				incidenceMatrix.get(indexMap.get(parentTerm.getDisambiguatedValue())).set(indexMap.get(disambiguatedTerm.getDisambiguatedValue()), true);
				collection.createSubclass(partTerm, disambiguatedTerm);
				result.add(new CreateSubclassEvent(partTerm, disambiguatedTerm));
				collection.removePart(parentTerm, partTerm);
				incidenceMatrix.get(indexMap.get(parentTerm.getDisambiguatedValue())).set(indexMap.get(partTerm.getDisambiguatedValue()), false);
				result.add(new RemovePartEvent(parentTerm, partTerm));
			}
			for(Term newlyCreatedClass : newlyCreatedClasses) 
				for(Term subclass : collection.getParts(newlyCreatedClass)) 
					disambiguatePart(subclass, incidenceMatrix, indexMap, reverseIndexMap, disambiguated);
		}
		for(Term partsPartTerm : collection.getParts(partTerm)) 
			disambiguatePart(partsPartTerm, incidenceMatrix, indexMap, reverseIndexMap, disambiguated);
		return result;
	}
	
	private static void adaptDataStructuresForNewTerm(Term disambiguatedTerm, List<List<Boolean>> incidenceMatrix, Map<String, Integer> indexMap,
			Map<Integer, String> reverseIndexMap) {
		int index = incidenceMatrix.size();
		indexMap.put(disambiguatedTerm.getDisambiguatedValue(), index);
		reverseIndexMap.put(index, disambiguatedTerm.getDisambiguatedValue());
		List<Boolean> column = new ArrayList<Boolean>(incidenceMatrix.size() + 1);
		incidenceMatrix.add(index, column);
		for(int j=0; j<incidenceMatrix.size(); j++)
			column.add(false);
		for(int i=0; i<incidenceMatrix.size(); i++)
			incidenceMatrix.get(i).add(false);
	}

	private static void removeParents(Term partTerm, List<List<Boolean>> incidenceMatrix, Map<String, Integer> indexMap,
			Map<Integer, String> reverseIndexMap) {
		edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection collection = ModelController.getCollection();
		List<Term> parentTerms = getParents(partTerm, incidenceMatrix, indexMap, reverseIndexMap);
		for(Term parentTerm : parentTerms) {
			collection.removePart(parentTerm, partTerm);
		}
	}
	
	private static void populateDataStructuresFromRelationMap(Map<String, List<String>> relationsMap, List<List<Boolean>> incidenceMatrix, Set<String> roots, 
			Map<String, Integer> indexMap, Map<Integer, String> reverseIndexMap) {
		edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection collection = ModelController.getCollection();
		List<String> terms = new ArrayList<String>(collection.getTerms().size());
		
		for(Term term : collection.getTerms()) {
			terms.add(term.getDisambiguatedValue());
			if(roots != null)
				roots.add(term.getDisambiguatedValue());
		}
		for(int i=0; i<terms.size(); i++)
			indexMap.put(terms.get(i), i);
		for(int i=0; i<terms.size(); i++)
			reverseIndexMap.put(i, terms.get(i));
		
		
		for(int i=0; i<terms.size(); i++) {
			List<Boolean> column = new ArrayList<Boolean>(terms.size());
			for(int j=0; j<terms.size(); j++) 
				column.add(false);
			incidenceMatrix.add(i, column);
			String superclass = terms.get(i);
			if(relationsMap.containsKey(superclass)) {
				for(String subclass : relationsMap.get(superclass)) {
					if(roots != null) 
						roots.remove(subclass);
					incidenceMatrix.get(i).set(indexMap.get(subclass), true);
				}
			}
		}
	}

	private static List<Term> getParents(Term childTerm, List<List<Boolean>> incidenceMatrix, Map<String, Integer> indexMap, Map<Integer, String> reverseIndexMap) {
		edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection collection = ModelController.getCollection();
		int index = indexMap.get(childTerm.getDisambiguatedValue());
		List<Term> parents = new LinkedList<Term>();
		for(int i=0; i<incidenceMatrix.size(); i++) 
			if(incidenceMatrix.get(i).get(index))
				parents.add(collection.getTerm(reverseIndexMap.get(i)));
		return parents;
	}

	public static void disambiguateAll() {
		diambiguateAllParts();
		disambiguateAllClasses();
	}

	public static List<GwtEvent<?>> disambiguateParts(java.util.Collection<Term> partTerms) {
		List<GwtEvent<?>> results = new ArrayList<GwtEvent<?>>(partTerms.size());
		for(Term partTerm : partTerms) {
			edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection collection = ModelController.getCollection();
			List<List<Boolean>> incidenceMatrix = new ArrayList<List<Boolean>>(collection.getTerms().size());
			Map<String, Integer> indexMap = new HashMap<String, Integer>();
			Map<Integer, String> reverseIndexMap = new HashMap<Integer, String>();
			populateDataStructuresFromRelationMap(collection.getSubclasses(), incidenceMatrix, null, indexMap, reverseIndexMap);
			
			Set<String> disambiguated = new HashSet<String>();
			results.addAll(disambiguatePart(partTerm, incidenceMatrix, indexMap, reverseIndexMap, disambiguated));
		}
		return results;
	}
	
	public static List<GwtEvent<?>> disambiguateClasses(java.util.Collection<Term> classTerms) {
		List<GwtEvent<?>> results = new ArrayList<GwtEvent<?>>(classTerms.size());
		for(Term classTerm : classTerms) {
			edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection collection = ModelController.getCollection();
			List<List<Boolean>> incidenceMatrix = new ArrayList<List<Boolean>>(collection.getTerms().size());
			Map<String, Integer> indexMap = new HashMap<String, Integer>();
			Map<Integer, String> reverseIndexMap = new HashMap<Integer, String>();
			populateDataStructuresFromRelationMap(collection.getSubclasses(), incidenceMatrix, null, indexMap, reverseIndexMap);
			
			Set<String> disambiguated = new HashSet<String>();
			results.addAll(disambiguateClass(classTerm, incidenceMatrix, indexMap, reverseIndexMap, disambiguated));
		}
		return results;
	}
	
}
