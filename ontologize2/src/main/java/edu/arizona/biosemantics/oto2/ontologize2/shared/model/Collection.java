package edu.arizona.biosemantics.oto2.ontologize2.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

import edu.arizona.biosemantics.common.biology.TaxonGroup;

public class Collection implements Serializable, Comparable<Collection> {

	private static final long serialVersionUID = 1L;
	private int id = -1;
	private String name = "";
	private TaxonGroup taxonGroup;
	private String secret = "";
	
	private String defaultBucket = null;
	private Map<String, Term> disambiguatedTermsMap = new HashMap<String, Term>();
	private Map<String, String> termBucketMap = new HashMap<String, String>();
	private Map<String, String> termIRIMap = new HashMap<String, String>();
	private Map<String, List<String>> partsMap = new HashMap<String, List<String>>();
	private Map<String, List<String>> subclassesMap = new HashMap<String, List<String>>();
	private Map<String, List<String>> synonymsMap = new HashMap<String, List<String>>();
	
	public Collection() { }
	
	public Collection(String name, TaxonGroup taxonGroup, String secret, List<String> terms, String defaultBucket) {
		this.name = name;
		this.taxonGroup = taxonGroup;
		this.secret = secret;
		this.defaultBucket = defaultBucket;
		
		for(String term : terms) {
			disambiguatedTermsMap.put(term, new Term(term));
		}
	}
	
	public void createTerm(Term... terms) {
		this.createTerm(Arrays.asList(terms));
	}
	
	public void createTerm(java.util.Collection<Term> terms) {
		for(Term term : terms)
			this.disambiguatedTermsMap.put(term.getDisambiguatedValue(), term);
	}
	
	public void removeTerms(Term... terms) {
		this.removeTerms(Arrays.asList(terms));
	}
	
	public void removeTerms(java.util.Collection<Term> terms) {
		for(Term term : terms) {
			disambiguatedTermsMap.remove(term.getDisambiguatedValue());
			partsMap.remove(term.getDisambiguatedValue());
			subclassesMap.remove(term.getDisambiguatedValue());
			synonymsMap.remove(term.getDisambiguatedValue());
			for(String parent : partsMap.keySet()) {
				for(String part : partsMap.get(parent)) {
					partsMap.get(parent).remove(part);
				}
			}
			for(String parent : partsMap.keySet()) 
				partsMap.get(parent).remove(term.getDisambiguatedValue());
			for(String subclass : subclassesMap.keySet()) 
				subclassesMap.get(subclass).remove(term.getDisambiguatedValue());
			for(String preferredTerm : synonymsMap.keySet()) 
				synonymsMap.get(preferredTerm).remove(term.getDisambiguatedValue());
		}
	}

	public List<Term> getTerms() {
		return new ArrayList<Term>(this.disambiguatedTermsMap.values());
	}
	
	public boolean hasTerm(String disambiguatedValue) {
		return disambiguatedTermsMap.containsKey(disambiguatedValue);
	}
	
	public Term getTerm(String disambiguatedValue) {
		return disambiguatedTermsMap.get(disambiguatedValue);
	}
	
	public void replaceTermInRelations(Term oldTerm, Term newTerm) {		
		for(String parent : new ArrayList<String>(partsMap.keySet())) {
			List<String> parts = partsMap.get(parent);
			for(String part : parts) {
				if(part.equals(oldTerm.getDisambiguatedValue())) {
					parts.set(parts.indexOf(part), newTerm.getDisambiguatedValue());
				}
			}
			
			if(parent.equals(oldTerm.getDisambiguatedValue())) {
				parts = partsMap.remove(parent);
				partsMap.put(newTerm.getDisambiguatedValue(), parts);
			} 
		}
		
		for(String superclass : new ArrayList<String>(subclassesMap.keySet())) {
			List<String> subclasses = subclassesMap.get(superclass);
			for(String subclass : subclasses) {
				if(subclass.equals(oldTerm.getDisambiguatedValue())) {
					subclasses.set(subclasses.indexOf(subclass), newTerm.getDisambiguatedValue());
				}
			}
			
			if(superclass.equals(oldTerm.getDisambiguatedValue())) {
				subclasses = subclassesMap.remove(superclass);
				subclassesMap.put(newTerm.getDisambiguatedValue(), subclasses);
			}
		}
		for(String preferredTerms : new ArrayList<String>(synonymsMap.keySet())) {
			List<String> synonyms = synonymsMap.get(preferredTerms);
			for(String synonym : synonyms) {
				if(synonym.equals(oldTerm.getDisambiguatedValue())) {
					synonyms.set(synonyms.indexOf(synonym), newTerm.getDisambiguatedValue());
				}
			}
			
			if(preferredTerms.equals(oldTerm.getDisambiguatedValue())) {
				synonyms = synonymsMap.remove(preferredTerms);
				synonymsMap.put(newTerm.getDisambiguatedValue(), synonyms);
			}
		}
	}	
	
	public Map<String, List<String>> getParts() {
		return partsMap;
	}
	
	public void createPart(Term parent, Term... parts) {
		this.createPart(parent, Arrays.asList(parts));
	}
	
	public void createPart(Term parent, java.util.Collection<Term> parts) {
		if(!parts.isEmpty()) {
			if(!partsMap.containsKey(parent.getDisambiguatedValue()))
				partsMap.put(parent.getDisambiguatedValue(), new LinkedList<String>());
			for(Term part : parts) 
				partsMap.get(parent.getDisambiguatedValue()).add(part.getDisambiguatedValue());
		}
	}
	
	public List<Term> getParts(Term parent) {
		if(!partsMap.containsKey(parent.getDisambiguatedValue()))
			return new LinkedList<Term>();
		List<String> parts = partsMap.get(parent.getDisambiguatedValue());
		List<Term> partTerms = new ArrayList<Term>(parts.size());
		for(String part : parts) 
			partTerms.add(disambiguatedTermsMap.get(part));
		return partTerms;
	}
	
	public void removePart(Term parent, Term... parts) {
		this.removePart(parent, Arrays.asList(parts));
	}

	public void removePart(Term parent, java.util.Collection<Term> parts) {
		if(partsMap.containsKey(parent.getDisambiguatedValue())) {
			for(Term part : parts)
				partsMap.get(parent.getDisambiguatedValue()).remove(part.getDisambiguatedValue());
			if(partsMap.get(parent.getDisambiguatedValue()).isEmpty())
				partsMap.remove(parent.getDisambiguatedValue());
		}
	}
	
	public Map<String, List<String>> getSubclasses() {
		return subclassesMap;
	}
	
	public void createSubclass(Term superclass, Term... subclasses) {
		this.createSubclass(superclass, Arrays.asList(subclasses));
	}
	
	public void createSubclass(Term superclass, java.util.Collection<Term> subclasses) {
		if(!subclasses.isEmpty()) {
			if(!subclassesMap.containsKey(superclass.getDisambiguatedValue()))
				subclassesMap.put(superclass.getDisambiguatedValue(), new LinkedList<String>());
			for(Term subclass : subclasses) 
				subclassesMap.get(superclass.getDisambiguatedValue()).add(subclass.getDisambiguatedValue());
		}
	}
	
	public List<Term> getSubclasses(Term superclass) {
		if(!subclassesMap.containsKey(superclass.getDisambiguatedValue()))
			return new LinkedList<Term>();
		List<String> subclasses = subclassesMap.get(superclass.getDisambiguatedValue());
		List<Term> subclassTerms = new ArrayList<Term>(subclasses.size());
		for(String subclass : subclasses) 
			subclassTerms.add(disambiguatedTermsMap.get(subclass));
		return subclassTerms;
	}

	public void removeSubclass(Term superclass, Term... subclasses) {
		this.removeSubclass(superclass, Arrays.asList(subclasses));
	}
	
	public void removeSubclass(Term superclass, java.util.Collection<Term> subclasses) {
		if(subclassesMap.containsKey(superclass.getDisambiguatedValue())) {
			for(Term subclass : subclasses)
				subclassesMap.get(superclass.getDisambiguatedValue()).remove(subclass.getDisambiguatedValue());
			if(subclassesMap.get(superclass.getDisambiguatedValue()).isEmpty())
				subclassesMap.remove(superclass.getDisambiguatedValue());
		}
	}	

	public Map<String, List<String>> getSynonyms() {
		return synonymsMap;
	}
	
	public void createSynonym(Term preferredTerm, Term... synonyms) {
		this.createSynonym(preferredTerm, Arrays.asList(synonyms));
	}
	
	public void createSynonym(Term preferredTerm, java.util.Collection<Term> synonyms) {
		if(!synonyms.isEmpty()) {
			if(!synonymsMap.containsKey(preferredTerm.getDisambiguatedValue()))
				synonymsMap.put(preferredTerm.getDisambiguatedValue(), new LinkedList<String>());
			for(Term synonym : synonyms) 
				synonymsMap.get(preferredTerm.getDisambiguatedValue()).add(synonym.getDisambiguatedValue());
		}
	}
	
	public List<Term> getSynonyms(Term preferredTerm) {
		if(!synonymsMap.containsKey(preferredTerm.getDisambiguatedValue()))
			return new LinkedList<Term>();
		List<String> synonyms = synonymsMap.get(preferredTerm.getDisambiguatedValue());
		List<Term> synonymTerms = new ArrayList<Term>(synonyms.size());
		for(String synonym : synonyms) 
			synonymTerms.add(disambiguatedTermsMap.get(synonym));
		return synonymTerms;
	}
	
	public void removeSynonym(Term preferredTerm, Term... syonyms) {
		this.removeSynonym(preferredTerm, Arrays.asList(syonyms));
	}
	
	public void removeSynonym(Term preferredTerm, java.util.Collection<Term> synonyms) {
		if(synonymsMap.containsKey(preferredTerm.getDisambiguatedValue())) {
			for(Term synonym : synonyms)
				synonymsMap.get(preferredTerm.getDisambiguatedValue()).remove(synonym.getDisambiguatedValue());
			if(synonymsMap.get(preferredTerm.getDisambiguatedValue()).isEmpty())
				synonymsMap.remove(preferredTerm.getDisambiguatedValue());
		}
	}
	
	public void addTermBucketMapping(String term, String bucket) {
		termBucketMap.put(term, bucket);
	}
	
	public void removeTermBucketMapping(String term) {
		termBucketMap.remove(term);
	}
	
	public boolean hasBucket(String term) {
		return termBucketMap.containsKey(term);
	}
	
	public String getBucket(String term) {
		if(this.hasBucket(term))
			return termBucketMap.get(term);
		else 
			return defaultBucket;
	}
	
	public void addTermIRIMapping(String term, String iri) {
		termIRIMap.put(term, iri);
	}
	
	public void removeTermIRIMapping(String term) {
		termIRIMap.remove(term);
	}
	
	public boolean hasIRI(String term) {
		return termIRIMap.containsKey(term);
	}
	
	public String getIRI(String term) {
		return termIRIMap.get(term);
	}
	
	public int getId() {
		return id;
	}

	public boolean hasId() {
		return id != -1;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public TaxonGroup getTaxonGroup() {
		return taxonGroup;
	}

	public void setTaxonGroup(TaxonGroup taxonGroup) {
		this.taxonGroup = taxonGroup;
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
		Collection other = (Collection) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(Collection o) {
		return this.getId() - o.getId();
	}

	
}
