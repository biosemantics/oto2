package edu.arizona.biosemantics.oto2.oto.server.db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
				try(Query query = new Query("SELECT t.* FROM oto_term t, oto_bucket b, " +
						"oto_collection c WHERE t.bucket = b.id AND b.collection = c.id AND c.id != ? AND" +
						" c.type = ? AND t.original_term = ?")) {
					query.setParameter(1, collection.getId());
					query.setParameter(2, collection.getType());
					query.setParameter(3, term.getOriginalTerm());
					ResultSet result = query.execute();
					Map<String, Integer> spellingMap = new HashMap<String, Integer>();
					while(result.next()) {
						Term historyTerm = termDAO.createTerm(result);
						String spelling = historyTerm.getTerm();
						if(!spellingMap.containsKey(spelling))
							spellingMap.put(spelling, 0);
						spellingMap.put(spelling, spellingMap.get(spelling) + 1);
					}
					int maxVotes = 0;
					String useSpelling = term.getOriginalTerm();
					for(String spelling : spellingMap.keySet()) {
						int votes = spellingMap.get(spelling);
						if(votes > maxVotes) {
							maxVotes = votes;
							useSpelling = spelling;
						}
					}
					term.setTerm(useSpelling);
				}
				try(Query query = new Query("SELECT t.* FROM oto_term t, oto_bucket b, " +
						"oto_collection c WHERE t.bucket = b.id AND b.collection = c.id AND c.id != ? AND" +
						" c.type = ? AND t.term = ?")) {
					query.setParameter(1, collection.getId());
					query.setParameter(2, collection.getType());
					query.setParameter(3, term.getTerm());
					ResultSet result = query.execute();
					Map<Boolean, Integer> uselessMap = new HashMap<Boolean, Integer>();
					while(result.next()) {
						Term historyTerm = termDAO.createTerm(result);
						Boolean useless = historyTerm.getUseless();
						if(!uselessMap.containsKey(useless))
							uselessMap.put(useless, 0);
						uselessMap.put(useless, uselessMap.get(useless) + 1);
					}
					int maxVotes = 0;
					Boolean useless = term.getUseless();
					for(Boolean value : uselessMap.keySet()) {
						int votes = uselessMap.get(value);
						if(votes > maxVotes) {
							maxVotes = votes;
							useless = value;
						}
					}
					term.setUseless(useless);
				}
				termDAO.update(term, bucket.getId());
			}	
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void initializeLabeling(Collection collection) {
		Set<Term> structureTerms = new HashSet<Term>();
		for(Bucket bucket : collection.getBuckets())
			if(bucket.getName().equalsIgnoreCase("structure")) {
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
		labelingDAO.remove(collection);
		try {
			Map<String, Label> labelNameMap = new HashMap<String, Label>();
			for(Label label : collection.getLabels())
				labelNameMap.put(label.getName(), label);
			
			List<Term> terms = termDAO.getTerms(collection);
			for(Term term : terms) {
				if(structureTerms.contains(term)) {
					try(Query query = new Query("SELECT l.name FROM oto_labeling x, oto_label l, " +
							"oto_term t, oto_collection c, oto_synonym s " +
							"WHERE (x.label = l.id AND l.collection = c.id AND c.id != ? AND c.type = ? AND " +
							"x.term = t.id AND t.term = ?) OR " +
							"(s.label = l.id AND l.collection = c.id AND c.id != ? AND c.type = ? AND " + 
							"s.synonymterm = t.id AND t.term = ?)")) {
						query.setParameter(1, collection.getId());
						query.setParameter(2, collection.getType());
						query.setParameter(3, term.getTerm());
						query.setParameter(4, collection.getId());
						query.setParameter(5, collection.getType());
						query.setParameter(6, term.getTerm());
						ResultSet result = query.execute();
						Map<String, Integer> labelingMap = new HashMap<String, Integer>();
						while(result.next()) {
							String labelName = result.getString(1);
							if(!labelingMap.containsKey(labelName))
								labelingMap.put(labelName, 0);
							labelingMap.put(labelName, labelingMap.get(labelName) + 1);
						}
						
						class LabelCount implements Comparable<LabelCount> {
							public String labelName;
							public int count;
							public LabelCount(String labelName, int count) {
								this.labelName = labelName;
								this.count = count;
							}
							@Override
							public int compareTo(LabelCount o) {
								return o.count - this.count;
							}
						}
						List<LabelCount> labelCounts = new ArrayList<LabelCount>();
						for(String labelName : labelingMap.keySet()) {
							labelCounts.add(new LabelCount(labelName, labelingMap.get(labelName)));
						}
						Collections.sort(labelCounts);
						
						for(LabelCount labelCount : labelCounts) {
							if(labelNameMap.containsKey(labelCount.labelName)) {
								labelingDAO.insert(term, labelNameMap.get(labelCount.labelName));
								break;
							}
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void initializeSynonym(Collection collection) {
		SynonymDAO synonymDAO = daoManager.getSynonymDAO();
		synonymDAO.remove(collection);
		Map<Label, Map<Term, Map<Term, Integer>>> synonymGroups = createSynonymGroupsFromHistory(collection);
		
		Set<Term> visitedTerms = new HashSet<Term>();
		for(Label label : synonymGroups.keySet()) {
			Map<Term, Map<Term, Integer>> termGroups = synonymGroups.get(label);
			for(Term term : termGroups.keySet()) {
				if(!visitedTerms.contains(term)) {
					Map<Term, Integer> group = termGroups.get(term);
					visitedTerms.addAll(group.keySet());
					Term mainTerm = getHistoricMainTerm(group);
					for(Term groupTerm : group.keySet()) 
						if(!groupTerm.equals(mainTerm))
							synonymDAO.insert(label, mainTerm, groupTerm);
				}
			}
		}
	}

	private Term getHistoricMainTerm(Map<Term, Integer> group) {
		Term mainTerm = null;
		int maxVotes = -1;
		for(Term term : group.keySet())
			if(group.get(term) > maxVotes) {
				mainTerm = term;
				maxVotes = group.get(term);
			}	
		return mainTerm;
	}
	
	private Map<Label, Map<Term, Map<Term, Integer>>> createSynonymGroupsFromHistory(Collection collection) {
		LabelingDAO labelingDAO = daoManager.getLabelingDAO();
		Map<Label, Map<Term, Map<Term, Integer>>> synonymGroups = 
				new HashMap<Label, Map<Term, Map<Term, Integer>>>();
		
		Set<HistoricSynonymCalculation> calculations = new HashSet<HistoricSynonymCalculation>();
		for(Term term : collection.getTerms()) {
			Set<Label> labels = labelingDAO.getLabels(term);
			for(Label label : labels) {
				for(Term otherTerm : labelingDAO.getAllTerms(label)) {
					if(!term.equals(otherTerm)) {
						HistoricSynonymCalculation calculation = new HistoricSynonymCalculation(label, term, otherTerm);
						HistoricSynonymCalculation reverseCalculation = new HistoricSynonymCalculation(label, otherTerm, term);
						if(!calculations.contains(calculation) && !calculations.contains(reverseCalculation)) {
							calculations.add(calculation);
							calculations.add(reverseCalculation);
							HistoricSynonymResult historicSynonymResult = isHistoricSynonym(collection, calculation);
							if(historicSynonymResult.result) {
								if(!synonymGroups.containsKey(label))
									synonymGroups.put(label, new HashMap<Term, Map<Term, Integer>>());
								Map<Term, Map<Term, Integer>> labelsGroups = synonymGroups.get(label);
								Map<Term, Integer> termGroup = labelsGroups.get(term);
								Map<Term, Integer> otherTermGroup = labelsGroups.get(otherTerm);
								if(termGroup == null && otherTermGroup == null) {
									Map<Term, Integer> newGroup = new HashMap<Term, Integer>();
									newGroup.put(term, historicSynonymResult.mainTerm == term ? 1 : 0);
									newGroup.put(otherTerm, historicSynonymResult.mainTerm == otherTerm ? 1 : 0);
									labelsGroups.put(term, newGroup);
									labelsGroups.put(otherTerm, newGroup);
								}
								if(termGroup != null && otherTermGroup == null) {
									termGroup.put(otherTerm, historicSynonymResult.mainTerm == otherTerm ? 1 : 0);
									labelsGroups.put(otherTerm, termGroup);
								}
								if(termGroup == null && otherTermGroup != null) {
									otherTermGroup.put(term, historicSynonymResult.mainTerm == term ? 1 : 0);
									labelsGroups.put(term, otherTermGroup);
								}
								if(termGroup != null && otherTermGroup != null && 
										!termGroup.equals(otherTermGroup)) {
									termGroup.putAll(otherTermGroup);
									if(historicSynonymResult.mainTerm == otherTerm)
										termGroup.put(otherTerm, termGroup.get(otherTerm) + 1);
									for(Term otherTermGroupTerms : otherTermGroup.keySet())
										labelsGroups.put(otherTermGroupTerms, termGroup);
								}
							}
						}
					}
				}
			}
		}
		return synonymGroups;
	}
	
	private class HistoricSynonymCalculation {
		public Label label;
		public Term term;
		public Term otherTerm;
		public HistoricSynonymCalculation(Label label, Term term, Term otherTerm) {
			this.label = label;
			this.term = term;
			this.otherTerm = otherTerm;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((label == null) ? 0 : label.hashCode());
			result = prime * result
					+ ((otherTerm == null) ? 0 : otherTerm.hashCode());
			result = prime * result + ((term == null) ? 0 : term.hashCode());
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
			HistoricSynonymCalculation other = (HistoricSynonymCalculation) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (label == null) {
				if (other.label != null)
					return false;
			} else if (!label.equals(other.label))
				return false;
			if (otherTerm == null) {
				if (other.otherTerm != null)
					return false;
			} else if (!otherTerm.equals(other.otherTerm))
				return false;
			if (term == null) {
				if (other.term != null)
					return false;
			} else if (!term.equals(other.term))
				return false;
			return true;
		}
		private HistoricInitializer getOuterType() {
			return HistoricInitializer.this;
		}
	}
	
	private class HistoricSynonymResult {
		public boolean result = false;
		public Term mainTerm = null;
	}

	private HistoricSynonymResult isHistoricSynonym(Collection collection, HistoricSynonymCalculation calculation) {		
		int bothMainTermsCount = 0;
		int synonymsCount = 0;
		int termMainTermCount = 0;
		int otherTermMainTermCount = 0;
		try(Query query = new Query("SELECT c.id, t.term FROM oto_collection c," +
				" oto_labeling x, oto_term t, oto_label l" +
				" WHERE " +
				" c.id != ? AND c.id = l.collection AND l.name = ? AND l.id = x.label AND t.id = x.term")) {
			query.setParameter(1, collection.getId());
			query.setParameter(2, calculation.label.getName());
			ResultSet result = query.execute();
			HashMap<Integer, Set<String>> collectionLabeldTerms = new HashMap<Integer, Set<String>>();
			while(result.next()) {
				int collectionId = result.getInt(1);
				String labeledTerm = result.getString(2);
				if(!collectionLabeldTerms.containsKey(collectionId))
					collectionLabeldTerms.put(collectionId, new HashSet<String>());
				collectionLabeldTerms.get(collectionId).add(labeledTerm);
			}
			for(Integer collectionId : collectionLabeldTerms.keySet()) {
				Set<String> labeledTerms = collectionLabeldTerms.get(collectionId);
				if(labeledTerms.contains(calculation.term.getTerm()) && labeledTerms.contains(calculation.otherTerm.getTerm()))
					bothMainTermsCount++;
				else {
					try(Query synonymQuery = new Query("SELECT * FROM " +
							"oto_collection c, oto_synonym s, oto_label l, " +
							"oto_term t1, oto_term t2 WHERE " +
							"c.id = ? AND l.collection = c.id AND l.name = ? AND " +
							"s.label = l.id AND s.mainterm = t1.id AND s.synonymterm = t2.id " +
							"AND t1.term = ? AND t2.term = ?")) {
						synonymQuery.setParameter(1, collectionId);
						synonymQuery.setParameter(2, calculation.label.getName());
						synonymQuery.setParameter(3, calculation.term.getTerm());
						synonymQuery.setParameter(4, calculation.otherTerm.getTerm());
						ResultSet synonymResult = synonymQuery.execute();
						while(synonymResult.next()) {
							synonymsCount++;
							termMainTermCount++;
						}
					}
					try(Query synonymQuery = new Query("SELECT * FROM " +
							"oto_collection c, oto_synonym s, oto_label l, " +
							"oto_term t1, oto_term t2 WHERE " +
							"c.id = ? AND l.collection = c.id AND l.name = ? AND " +
							"s.label = l.id AND s.mainterm = t1.id AND s.synonymterm = t2.id " +
							"AND t1.term = ? AND t2.term = ?")) {
						synonymQuery.setParameter(1, collectionId);
						synonymQuery.setParameter(2, calculation.label.getName());
						synonymQuery.setParameter(3, calculation.otherTerm.getTerm());
						synonymQuery.setParameter(4, calculation.term.getTerm());
						ResultSet synonymResult = synonymQuery.execute();
						while(synonymResult.next()) {
							synonymsCount++;
							otherTermMainTermCount++;
						}
					}
				}	
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		HistoricSynonymResult result = new HistoricSynonymResult();
		result.result = synonymsCount > bothMainTermsCount;
		result.mainTerm = termMainTermCount >= otherTermMainTermCount ? calculation.term : calculation.otherTerm;
		return result;
	}
	
}
