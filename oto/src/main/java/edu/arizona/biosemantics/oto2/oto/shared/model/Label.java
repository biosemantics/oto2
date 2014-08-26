package edu.arizona.biosemantics.oto2.oto.shared.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Label implements Serializable {

	public static class AddResult {
		public boolean result;
		public Term parent;
		public AddResult(boolean result, Term parent) {
			this.result = result;
			this.parent = parent;
		}
	}
	
	private int id = - 1;
	private String name;
	private int collectionId;
	private String description = "";
	private List<Term> mainTerms = new LinkedList<Term>();
	private Map<Term, List<Term>> mainTermSynonymsMap = new HashMap<Term, List<Term>>();
	
	public Label() { }
	
	public Label(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public Label(int collectionId, String name, String description) {
		this.collectionId = collectionId;
		this.name = name;
		this.description = description;
	}
	
	public Label(int id, int collectionId, String name, String description) {
		super();
		this.id = id;
		this.collectionId = collectionId;
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String text) {
		this.name = text;
	}

	public List<Term> getMainTerms() {
		return new LinkedList<Term>(mainTerms);
	}

	public void setMainTerms(List<Term> mainTerms) {
		this.mainTerms.clear();
		this.mainTermSynonymsMap.clear();
		this.addMainTerms(mainTerms);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void uncategorizeMainTerm(Term term) {
		if(mainTermSynonymsMap.containsKey(term)) {
			List<Term> oldSynonyms = mainTermSynonymsMap.remove(term);
			for(Term oldSynonym : oldSynonyms) {
				this.addMainTerm(oldSynonym);
			}
		}
		mainTerms.remove(term);
	}
	
	/**
	 * @param term
	 * @return true if added as main term, false if was already specified as synonym for another mainTerm
	 */
	public AddResult addMainTerm(Term term) {
		for(Term mainTerm : mainTerms) {
			if(mainTermSynonymsMap.get(mainTerm).contains(term)) {				
				return new AddResult(false, mainTerm);
			}
		}
		if(mainTerms.contains(term))
			return new AddResult(false, null);
		mainTerms.add(term);
		mainTermSynonymsMap.put(term, new LinkedList<Term>());
		return new AddResult(true, null);
	}
	
	public Map<Term, AddResult> addMainTerms(List<Term> mainTerms) {
		Map<Term, AddResult> result = new HashMap<Term, AddResult>();
		for(Term mainTerm : mainTerms) {
			result.put(mainTerm, this.addMainTerm(mainTerm));
		}
		return result;
	}	
	
	public int getId() {
		return id;
	}
	
	public boolean hasId() {
		return id != -1;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getCollectionId() {
		return collectionId;
	}

	public void setCollection(int collectionId) {
		this.collectionId = collectionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Label other = (Label) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public void uncategorizeMainTerms(List<Term> mainTerms) {
		for(Term mainTerm : mainTerms)
			this.uncategorizeMainTerm(mainTerm);
	}

	public void setMainTermSynonymsMap(Map<Term, List<Term>> mainTermSynonymsMap) {
		this.mainTermSynonymsMap = mainTermSynonymsMap;
	}
	
	public List<Term> getSynonyms(Term mainTerm) {
		if(!mainTermSynonymsMap.containsKey(mainTerm))
			return new LinkedList<Term>();
		return mainTermSynonymsMap.get(mainTerm);
	}

	public void addSynonym(Term mainTerm, Term synonymTerm) {
		uncategorizeMainTerm(synonymTerm);
		
		if(!mainTermSynonymsMap.containsKey(mainTerm)) 
			mainTermSynonymsMap.put(mainTerm, new LinkedList<Term>());
		mainTermSynonymsMap.get(mainTerm).add(synonymTerm);
	}
	
	public void addSynonymy(Term mainTerm, List<Term> synonymTerms) {
		for(Term synonym : synonymTerms)
			addSynonym(mainTerm, synonym);
	}

	public void setSynonymy(Term mainLabelTerm, List<Term> synonymTerms) {
		for(Term synonymTerm : synonymTerms)
			uncategorizeMainTerm(synonymTerm);
		mainTermSynonymsMap.put(mainLabelTerm, synonymTerms);
	}

	public Map<Term, List<Term>> getMainTermSynonymsMap() {
		Map<Term, List<Term>> copy = new HashMap<Term, List<Term>>();
		for(Term key : mainTermSynonymsMap.keySet())
			copy.put(key, new LinkedList<Term>(mainTermSynonymsMap.get(key)));
		return copy;
	}

	public void removeSynonymy(Term mainTerm, List<Term> synonyms) {
		this.addMainTerms(synonyms);
		this.setSynonymy(mainTerm, new LinkedList<Term>());
	}

	public void uncategorizeTerm(Term term) {
		this.uncategorizeMainTerm(term);
		this.uncategorizeSynonymTerm(term);
	}

	private void uncategorizeSynonymTerm(Term term) {
		for(Term mainTerm : this.mainTerms)
			this.mainTermSynonymsMap.get(mainTerm).remove(term);
	}

	public boolean containsTerm(Term term) {
		for(Term mainTerm : mainTerms) {
			if(mainTerm.equals(term))
				return true;
			for(Term synonym : mainTermSynonymsMap.get(mainTerm)) {
				if(synonym.equals(term))
					return true;
			}
		}
		return false;
	}
}