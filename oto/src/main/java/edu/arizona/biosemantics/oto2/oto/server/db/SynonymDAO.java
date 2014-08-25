package edu.arizona.biosemantics.oto2.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class SynonymDAO {

	private TermDAO termDAO;
	
	protected SynonymDAO() { }
	
	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}
	
	public Map<Term, List<Term>> get(Label label) throws ClassNotFoundException, SQLException, IOException {
		Map<Term, List<Term>> synonyms = new HashMap<Term, List<Term>>();
		Query query = new Query("SELECT * FROM oto_synonym WHERE label = ?");
		query.setParameter(1, label.getId());
		ResultSet result = query.execute();
		while(result.next()) {
			int mainTermId = result.getInt(1);
			int synonymTermId = result.getInt(3);
			Term mainTerm = termDAO.get(mainTermId);
			Term synonymTerm = termDAO.get(synonymTermId);
			if(mainTerm != null && synonymTerm != null) {
				if(!synonyms.containsKey(mainTerm))
					synonyms.put(mainTerm, new LinkedList<Term>());
				synonyms.get(mainTerm).add(synonymTerm);
			}
		}
		query.close();
		return synonyms;
	}
	
	public void ensure(Label label, List<Term> mainTerms) throws ClassNotFoundException, SQLException, IOException {
		String notInMainTerms = "";
		for(Term mainTerm : mainTerms) {
			notInMainTerms += mainTerm.getId() + ",";
		}
		notInMainTerms = notInMainTerms.isEmpty() ? "" : notInMainTerms.substring(0, notInMainTerms.length() - 1);
		String deleteOldSynonymsQuery = notInMainTerms.isEmpty() ? "DELETE FROM `oto_synonym` WHERE label = ?" : 
			 "DELETE FROM `oto_synonym` WHERE label = ? AND mainTerm NOT IN (" + notInMainTerms + ")";
		Query deleteOldSynonyms = new Query(deleteOldSynonymsQuery);
		deleteOldSynonyms.setParameter(1, label.getId());
		deleteOldSynonyms.executeAndClose();
		
		for(Term mainTerm : mainTerms) {
			List<Term> synonymTerms = label.getSynonyms(mainTerm);
			
			String notInSynonyms = "";
			for(Term synonymTerm : synonymTerms) {
				notInSynonyms += synonymTerm.getId() + ",";
			}
			
			notInSynonyms = notInSynonyms.isEmpty() ? "" : notInSynonyms.substring(0, notInSynonyms.length() - 1);
			deleteOldSynonymsQuery = notInSynonyms.isEmpty() ? "DELETE FROM `oto_synonym` WHERE label = ? AND mainTerm = ?" : 
				 "DELETE FROM `oto_synonym` WHERE label = ? AND mainTerm = ? AND synonymTerm NOT IN (" + notInSynonyms + ")";
			deleteOldSynonyms = new Query(deleteOldSynonymsQuery);
			deleteOldSynonyms.setParameter(1, label.getId());
			deleteOldSynonyms.setParameter(2, mainTerm.getId());
			deleteOldSynonyms.executeAndClose();
			
			for(Term synonymTerm : synonymTerms) {
				Query insert = new Query("INSERT IGNORE INTO `oto_synonym` " +
						"(`mainTerm`, `label`, `synonymTerm`) VALUES (?, ?, ?)");			
				insert.setParameter(1, mainTerm.getId());
				insert.setParameter(2, label.getId());
				insert.setParameter(3, synonymTerm.getId());
				insert.executeAndClose();
			}
		}
	}

	public void remove(Collection collection) throws ClassNotFoundException, SQLException, IOException {
		Query query = new Query("DELETE FROM oto_synonym s, oto_term t, oto_bucket b WHERE b.collection = ? AND t.bucket = b.id AND s.mainTerm = t.id");
		query.setParameter(1, collection.getId());
		query.executeAndClose();
	}
	
}
