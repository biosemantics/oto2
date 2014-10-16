package edu.arizona.biosemantics.oto2.oto.server.db;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.arizona.biosemantics.oto2.oto.server.db.CommunityDAO.LabelCount;
import edu.arizona.biosemantics.oto2.oto.shared.log.LogLevel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class HistoricInitializer {

	private DAOManager daoManager;

	public HistoricInitializer(DAOManager daoManager) {
		this.daoManager = daoManager;
	}
	
	public void initialize(Collection collection) {
		initializeTerm(collection);
		initializeLabeling(collection);
		initializeSynonym(collection);
	}
	
	public void initializeTerm(Collection collection) {
		TermDAO termDAO = daoManager.getTermDAO();
		BucketDAO bucketDAO = daoManager.getBucketDAO();
		termDAO.resetTerms(collection);
		try {
			List<Term> terms = termDAO.getTerms(collection);
			for(Term term : terms) {
				Bucket bucket = bucketDAO.get(term);
				term.setTerm(daoManager.getCommunityDAO().getSpelling(collection, term));
				term.setUseless(daoManager.getCommunityDAO().getUseless(collection, term));
				termDAO.update(term, bucket.getId());
			}	
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
	}

	public void initializeLabeling(Collection collection) {
		LabelingDAO labelingDAO = daoManager.getLabelingDAO();
		labelingDAO.remove(collection);
		
		Set<Term> structureTerms = new HashSet<Term>();
		for(Bucket bucket : collection.getBuckets())
			if(bucket.getName().equalsIgnoreCase("structures")) {
				structureTerms.addAll(bucket.getTerms());
			}
		Label structureLabel = null;
		for(Label label : collection.getLabels()) {
			if(label.getName().equalsIgnoreCase("structure")) {
				structureLabel = label;
			}
		}
		
		if(structureLabel != null) {
			initializeStructureTerms(collection, structureTerms, structureLabel);
			initializeRemainingTerms(collection, structureTerms);
		} else 
			initializeRemainingTerms(collection, new HashSet<Term>());
	}
	
	private void initializeStructureTerms(Collection collection, Set<Term> structureTerms, Label structureLabel) {
		LabelingDAO labelingDAO = daoManager.getLabelingDAO();
		for(Term structureTerm : structureTerms) 
			labelingDAO.insert(structureTerm, structureLabel);
	}

	private void initializeRemainingTerms(Collection collection, Set<Term> structureTerms) {
		TermDAO termDAO = daoManager.getTermDAO();
		LabelingDAO labelingDAO = daoManager.getLabelingDAO();
		try {
			Map<String, Label> labelNameMap = new HashMap<String, Label>();
			for(Label label : collection.getLabels())
				labelNameMap.put(label.getName(), label);
			
			List<Term> terms = termDAO.getTerms(collection);
			for(Term term : terms) {
				if(!structureTerms.contains(term)) {
					List<LabelCount> labelCounts = daoManager.getCommunityDAO().getLabelCounts(collection, term);
					Set<String> labels = daoManager.getCommunityDAO().determineLabels(labelCounts);
					for(String label : labels) {
						if(labelNameMap.containsKey(label))
							labelingDAO.insert(term, labelNameMap.get(label));
					}
				}
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
	}
	
	public void initializeSynonym(Collection collection) {
		SynonymDAO synonymDAO = daoManager.getSynonymDAO();
		synonymDAO.remove(collection);
				
		Map<Label, Map<Term, Map<Term, Integer>>> synonymGroups = daoManager.getCommunityDAO().createSynonymGroupsFromHistory(collection);
		
		Set<Term> visitedTerms = new HashSet<Term>();
		for(Label label : synonymGroups.keySet()) {
			Map<Term, Map<Term, Integer>> termGroups = synonymGroups.get(label);
			for(Term term : termGroups.keySet()) {
				if(!visitedTerms.contains(term)) {
					Map<Term, Integer> group = termGroups.get(term);
					visitedTerms.addAll(group.keySet());
					Term mainTerm = daoManager.getCommunityDAO().getHistoricMainTerm(group);
					for(Term groupTerm : group.keySet()) 
						if(!groupTerm.equals(mainTerm))
							synonymDAO.insert(label, mainTerm, groupTerm);
				}
			}
		}
	}
	
}
