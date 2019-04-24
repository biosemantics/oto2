package edu.arizona.biosemantics.oto2.oto.server.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.google.inject.Inject;

import edu.arizona.biosemantics.oto.client.oto.OTOClient;
import edu.arizona.biosemantics.oto.model.GlossaryDownload;
import edu.arizona.biosemantics.oto.model.TermCategory;
import edu.arizona.biosemantics.oto.model.TermSynonym;
import edu.arizona.biosemantics.oto2.oto.server.Configuration;
import edu.arizona.biosemantics.oto2.oto.server.db.CommunityDAO.LabelCount;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermType;

public class HistoricInitializerGlossaryOnly {

	private DAOManager daoManager;

	@Inject
	public HistoricInitializerGlossaryOnly(DAOManager daoManager) {
		this.daoManager = daoManager;
	}
	
	public void initialize(Collection collection) {
		List<TermCategory> termCategories = new LinkedList<TermCategory>();
		List<TermSynonym> termSynonyms = new LinkedList<TermSynonym>();
		
		/*try(OTOClient client = new OTOClient(Configuration.otoClientUrl)) {
			client.open();
			Future<GlossaryDownload> futureGlosaryDownload = client.getGlossaryDownload(collection.getType());
			try {
				GlossaryDownload glossaryDownload = futureGlosaryDownload.get();
				termCategories = glossaryDownload.getTermCategories();
				termSynonyms = glossaryDownload.getTermSynonyms();
			} catch (InterruptedException | ExecutionException e) {
				log(LogLevel.ERROR, "Couldn't download OTO glossary to initialize collection", e);
			}
		}*/
		
		try{
		ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream(Configuration.glossariesDownloadDirectory + File.separator +
				"GlossaryDownload." + collection.getType() + ".ser"));
		GlossaryDownload glossaryDownload = (GlossaryDownload) objectIn.readObject();
		objectIn.close();
		termCategories = glossaryDownload.getTermCategories();
		termSynonyms = glossaryDownload.getTermSynonyms();
		}catch(Exception e){
			log(LogLevel.ERROR, "Couldn't download OTO "+collection.getType()+" glossary to initialize collection", e);
		}
		initializeLabeling(collection, termCategories);
		initializeSynonym(collection, termSynonyms);
	}

	public void initializeLabeling(Collection collection, List<TermCategory> termCategories) {
		Set<Term> initializedFromGlossary = initializeLabelingFromGlossary(collection, termCategories);
		
		Set<Term> taxonNameTerms = new HashSet<Term>();
		for(Bucket bucket : collection.getBuckets()) 
			if(bucket.getName().equalsIgnoreCase("taxon names")) {
				taxonNameTerms.addAll(bucket.getTerms());
			}
		Label taxonNameLabel = null;
		for(Label label : collection.getLabels()) {
			if(label.getName().equalsIgnoreCase("taxon_name")) {
				taxonNameLabel = label;
			}
		}
		
		Set<Term> categorizedTerms = new HashSet<Term>();	
		if(taxonNameLabel != null) {
			initializeLabelingForTerms(collection, initializedFromGlossary, categorizedTerms, taxonNameTerms, taxonNameLabel);
			categorizedTerms.addAll(taxonNameTerms);
		}
	}
	
	private void initializeLabelingForTerms(Collection collection, Set<Term> initializedFromGlossary, 
			Set<Term> categorizedTerms, Set<Term> terms, Label label) {
		LabelingDAO labelingDAO = daoManager.getLabelingDAO();
		for(Term term : terms) 
			if(!initializedFromGlossary.contains(term) && !categorizedTerms.contains(term)) {
				term.setTermType(TermType.KNOWN_IN_GLOSSARY);
				daoManager.getTermDAO().update(term, daoManager.getBucketDAO().get(term).getId());
				labelingDAO.insert(term, label);
			}
	}
	
	private Set<Term> initializeLabelingFromGlossary(Collection collection, List<TermCategory> termCategories) {
		Set<Term> result = new HashSet<Term>();
		
		TermDAO termDAO = daoManager.getTermDAO();
		LabelingDAO labelingDAO = daoManager.getLabelingDAO();
			
		Map<String, Set<String>> termCategoryMap = new HashMap<String, Set<String>>();
		for(TermCategory termCategory : termCategories) {
			if(!termCategoryMap.containsKey(termCategory.getTerm()) )
				termCategoryMap.put(termCategory.getTerm(), new HashSet<String>());
			termCategoryMap.get(termCategory.getTerm()).add(termCategory.getCategory());
		}
		
		try {
			Map<String, Label> labelNameMap = new HashMap<String, Label>();
			for(Label label : collection.getLabels())
				labelNameMap.put(label.getName(), label);
			
			List<Term> terms = termDAO.getTerms(collection);
			for(Term term : terms) {
				Set<Label> existingLabels = labelingDAO.getLabels(term);
				if(termCategoryMap.containsKey(term.getTerm())) {
					for(String category : termCategoryMap.get(term.getTerm())) {
						if(labelNameMap.containsKey(category) && !existingLabels.contains(labelNameMap.get(category))) {
							term.setTermType(TermType.KNOWN_IN_GLOSSARY);
							termDAO.update(term, daoManager.getBucketDAO().get(term).getId());
							labelingDAO.insert(term, labelNameMap.get(category));
							result.add(term);
						}
					}
				}
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return result;
	}
	
	public void initializeSynonym(Collection collection, List<TermSynonym> termSynonyms) {
		initializeSynonyms(collection, termSynonyms);
	}
	
	// it is unclear from oto synonym downloads whether all the synonyms of a term use the same main term
	// in a TermSynonym object. Therefore choose one by voting of appearance as "term" in termSynonyms
	private Map<Label, Map<Term, Map<Term, Integer>>> getSynonymGroupsFromGlossary(Collection collection, List<TermSynonym> termSynonyms) {
		LabelingDAO labelingDAO = daoManager.getLabelingDAO();
		
		Map<String, Label> labelNameMap = new HashMap<String, Label>();
		for(Label label : collection.getLabels())
			labelNameMap.put(label.getName(), label);
		
		Map<String, Term> termNameMap = new HashMap<String, Term>(); 
		for(Term term : collection.getTerms()) 
			termNameMap.put(term.getTerm(), term);
		
		Map<Label, Map<Term, Map<Term, Integer>>> synonymGroups = 
				new HashMap<Label, Map<Term, Map<Term, Integer>>>();
		
		for(TermSynonym termSynonym : termSynonyms) {
			String category = termSynonym.getCategory();
			if(labelNameMap.containsKey(category) && termNameMap.containsKey(termSynonym.getTerm()) && 
					termNameMap.containsKey(termSynonym.getSynonym())) {
				Label label = labelNameMap.get(category);
				Term term  = termNameMap.get(termSynonym.getTerm());
				Term synonym = termNameMap.get(termSynonym.getSynonym());
				
				List<Term> labelMainTerms = labelingDAO.getMainTerms(label);
				if(labelMainTerms.contains(term) && labelMainTerms.contains(synonym)) {
					if(!synonymGroups.containsKey(label))
						synonymGroups.put(label, new HashMap<Term, Map<Term, Integer>>());
					Map<Term, Map<Term, Integer>> labelsGroups = synonymGroups.get(label);
					Map<Term, Integer> termGroup = labelsGroups.get(term);
					Map<Term, Integer> otherTermGroup = labelsGroups.get(synonym);
					if(termGroup == null && otherTermGroup == null) {
						Map<Term, Integer> newGroup = new HashMap<Term, Integer>();
						newGroup.put(term, 1);
						newGroup.put(synonym, 0);
						labelsGroups.put(term, newGroup);
						labelsGroups.put(synonym, newGroup);
					}
					if(termGroup != null && otherTermGroup == null) {
						termGroup.put(synonym, 0);
						labelsGroups.put(synonym, termGroup);
					}
					if(termGroup == null && otherTermGroup != null) {
						otherTermGroup.put(term, 1);
						labelsGroups.put(term, otherTermGroup);
					}
					if(termGroup != null && otherTermGroup != null && 
							!termGroup.equals(otherTermGroup)) {
						termGroup.putAll(otherTermGroup);
						termGroup.put(term, termGroup.get(term) + 1);
						for(Term otherTermGroupTerm : otherTermGroup.keySet())
							labelsGroups.put(otherTermGroupTerm, termGroup);
					}
				}
			}
		}
		return synonymGroups;
	}

	private void initializeSynonyms(Collection collection, List<TermSynonym> termSynonyms) {
		SynonymDAO synonymDAO = daoManager.getSynonymDAO();
		Map<Label, Map<Term, Map<Term, Integer>>> synonymGroupsGlossary = getSynonymGroupsFromGlossary(collection, termSynonyms);
		
		//insert to db
		Set<Map<Term, Integer>> visitedTermGroups = new HashSet<Map<Term, Integer>>();
		for(Label label : synonymGroupsGlossary.keySet()) {
			Map<Term, Map<Term, Integer>> labelsGroups = synonymGroupsGlossary.get(label);
			for(Term term : labelsGroups.keySet()) {
				Map<Term, Integer> termGroup = labelsGroups.get(term);
				if(!visitedTermGroups.contains(termGroup)) {
					visitedTermGroups.add(termGroup);
					
					Term mainTerm = term;
					for(Term groupTerm : termGroup.keySet())
						if(!groupTerm.equals(mainTerm)) {
							mainTerm.setTermType(TermType.KNOWN_IN_GLOSSARY);
							groupTerm.setTermType(TermType.KNOWN_IN_GLOSSARY);
							synonymDAO.insert(label, mainTerm, groupTerm);
						}
				}
			}
		}		
	}	
	
}
