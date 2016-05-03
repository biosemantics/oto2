package edu.arizona.biosemantics.oto2.ontologize2.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IncidenceMatrix implements Serializable {
	
	public static IncidenceMatrix fromCollection(Collection collection) {
		IncidenceMatrix incidenceMatrix = new IncidenceMatrix();
		
		Map<String, List<String>> subclassesMap = collection.getSubclasses();
		for(String superclass : subclassesMap.keySet()) {
			incidenceMatrix.createSubclasses(superclass, new ArrayList<String>(subclassesMap.get(superclass)));
		}
		
		Map<String, List<String>> partsMap = collection.getParts();
		for(String parent : partsMap.keySet()) {
			incidenceMatrix.createParts(parent, new ArrayList<String>(partsMap.get(parent)));
		}
		
		Map<String, List<String>> synonymsMap = collection.getParts();
		for(String preferredTerm : synonymsMap.keySet()) {
			incidenceMatrix.createSynonyms(preferredTerm, new ArrayList<String>(synonymsMap.get(preferredTerm)));
		}
		return incidenceMatrix;
	}

	private Map<String, List<String>> parentToPartsMap = new HashMap<String, List<String>>();
	private Map<String, List<String>> superclassToSubclassesMap = new HashMap<String, List<String>>();
	private Map<String, List<String>> preferredTermToSynonymsMap = new HashMap<String, List<String>>();
	
	private Map<String, List<String>> partToParentsMap = new HashMap<String, List<String>>();
	private Map<String, List<String>> subclassToSuperclassesMap = new HashMap<String, List<String>>();
	private Map<String, List<String>> synonymToPreferredTermsMap = new HashMap<String, List<String>>();
	
	
	public IncidenceMatrix() {
		
	}	

	public List<String> getParents(String part) {
		if(!partToParentsMap.containsKey(part)) 
			return new LinkedList<String>();
		return partToParentsMap.get(part);
	}
	
	public List<String> getParts(String parent) {
		if(!parentToPartsMap.containsKey(parent)) 
			return new LinkedList<String>();
		return parentToPartsMap.get(parent);
	}
	
	public List<String> getSuperclasses(String subclass) {
		if(!subclassToSuperclassesMap.containsKey(subclass)) 
			return new LinkedList<String>();
		return subclassToSuperclassesMap.get(subclass);
	}
	
	public List<String> getSubclasses(String superclass) {
		if(!superclassToSubclassesMap.containsKey(superclass)) 
			return new LinkedList<String>();
		return superclassToSubclassesMap.get(superclass);
	}
	
	public List<String> getPreferredTerms(String synonym) {
		if(!synonymToPreferredTermsMap.containsKey(synonym)) 
			return new LinkedList<String>();
		return synonymToPreferredTermsMap.get(synonym);
	}
	
	public List<String> getSynonyms(String preferredTerm) {
		if(!preferredTermToSynonymsMap.containsKey(preferredTerm)) 
			return new LinkedList<String>();
		return preferredTermToSynonymsMap.get(preferredTerm);
	}

	public void createPart(String parent, String part) {
		if(!partToParentsMap.containsKey(part))
			partToParentsMap.put(part, new ArrayList<String>());
		partToParentsMap.get(part).add(parent);
		
		if(!parentToPartsMap.containsKey(parent))
			parentToPartsMap.put(parent, new ArrayList<String>());
		parentToPartsMap.get(parent).add(part);
	}
	
	public void createSubclass(String superclass, String subclass) {
		if(!subclassToSuperclassesMap.containsKey(subclass))
			subclassToSuperclassesMap.put(subclass, new ArrayList<String>());
		subclassToSuperclassesMap.get(subclass).add(superclass);
		
		if(!superclassToSubclassesMap.containsKey(superclass))
			superclassToSubclassesMap.put(superclass, new ArrayList<String>());
		superclassToSubclassesMap.get(superclass).add(subclass);
	}
	
	public void createSynonym(String preferredTerm, String synonym) {
		if(!synonymToPreferredTermsMap.containsKey(synonym))
			synonymToPreferredTermsMap.put(synonym, new ArrayList<String>());
		synonymToPreferredTermsMap.get(synonym).add(preferredTerm);
		
		if(!preferredTermToSynonymsMap.containsKey(preferredTerm))
			preferredTermToSynonymsMap.put(preferredTerm, new ArrayList<String>());
		preferredTermToSynonymsMap.get(preferredTerm).add(synonym);
	}
	
	private void createSubclasses(String superclass, List<String> subclasses) {
		for(String subclass : subclasses)
			createSubclass(superclass, subclass);
	}
	
	private void createParts(String parent, List<String> parts) {
		for(String part : parts)
			createPart(parent, part);
	}
	
	private void createSynonyms(String preferredTerm, List<String> synonyms) {
		for(String synonym : synonyms)
			createSynonym(preferredTerm, synonym);
	}

}
