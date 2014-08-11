package edu.arizona.biosemantics.oto.oto.shared.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class Label implements Serializable {

	private int id = - 1;
	private String name;
	private int collectionId;
	private String description;
	private LinkedHashSet<Term> mainTerms = new LinkedHashSet<Term>();
	private LinkedHashMap<Term, LinkedHashSet<Term>> mainTermSynonymsMap = new LinkedHashMap<Term, LinkedHashSet<Term>>();
	
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

	public LinkedHashSet<Term> getMainTerms() {
		return new LinkedHashSet<Term>(mainTerms);
	}

	public void setMainTerms(LinkedHashSet<Term> mainTerms) {
		mainTerms.clear();
		mainTermSynonymsMap.clear();
		this.addMainTerms(mainTerms);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void removeMainTerm(Term term) {
		if(mainTermSynonymsMap.containsKey(term)) {
			LinkedHashSet<Term> oldSynonyms = mainTermSynonymsMap.get(term);
			for(Term oldSynonym : oldSynonyms) {
				this.addMainTerm(oldSynonym);
			}
			mainTermSynonymsMap.remove(term);
		}
		mainTerms.remove(term);
	}
	
	public void addMainTerm(Term term) {
		mainTerms.add(term);
		mainTermSynonymsMap.put(term, new LinkedHashSet<Term>());
	}
	
	public void addMainTerms(LinkedHashSet<Term> mainTerms) {
		for(Term mainTerm : mainTerms) {
			this.addMainTerm(mainTerm);
		}
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

	public void removeMainTerms(LinkedHashSet<Term> mainTerms) {
		for(Term mainTerm : mainTerms)
			this.removeMainTerm(mainTerm);
	}

	public void setSynonyms(LinkedHashMap<Term, LinkedHashSet<Term>> mainTermSynonymsMap) {
		this.mainTermSynonymsMap = mainTermSynonymsMap;
	}
	
	public LinkedHashSet<Term> getSynonyms(Term mainTerm) {
		if(!mainTermSynonymsMap.containsKey(mainTerm))
			return new LinkedHashSet<Term>();
		return mainTermSynonymsMap.get(mainTerm);
	}

	public void addSynonym(Term mainTerm, Term synonymTerm) {
		removeMainTerm(synonymTerm);
		
		if(!mainTermSynonymsMap.containsKey(mainTerm)) 
			mainTermSynonymsMap.put(mainTerm, new LinkedHashSet<Term>());
		mainTermSynonymsMap.get(mainTerm).add(synonymTerm);
	}
	
	public void addSynonyms(Term mainTerm, Set<Term> synonymTerms) {
		for(Term synonym : synonymTerms)
			addSynonym(mainTerm, synonym);
	}

	public void setSynonyms(Term mainLabelTerm, LinkedHashSet<Term> synonymTerms) {
		for(Term synonymTerm : synonymTerms)
			removeMainTerm(synonymTerm);
		mainTermSynonymsMap.put(mainLabelTerm, synonymTerms);
	}

}