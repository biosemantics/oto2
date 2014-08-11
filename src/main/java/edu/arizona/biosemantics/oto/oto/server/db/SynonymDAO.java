package edu.arizona.biosemantics.oto.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class SynonymDAO {

	private TermDAO termDAO;
	
	protected SynonymDAO() { }
	
	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}
	
	public LinkedHashMap<Term, LinkedHashSet<Term>> get(Label label) throws ClassNotFoundException, SQLException, IOException {
		LinkedHashMap<Term, LinkedHashSet<Term>> synonyms = new LinkedHashMap<Term, LinkedHashSet<Term>>();
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
					synonyms.put(mainTerm, new LinkedHashSet<Term>());
				synonyms.get(mainTerm).add(synonymTerm);
			}
		}
		query.close();
		return synonyms;
	}
	
	public void ensure(Label label, Term mainTerm, LinkedHashSet<Term> synonymTerms) throws ClassNotFoundException, SQLException, IOException {
		String notInSynonyms = "";
		for(Term synonymTerm : synonymTerms) {
			notInSynonyms += synonymTerm.getId() + ",";
		}
		
		notInSynonyms = notInSynonyms.isEmpty() ? "" : notInSynonyms.substring(0, synonymTerms.size() - 1);
		String deleteOldSynonymsQuery = notInSynonyms.isEmpty() ? "DELETE FROM `oto_synonym` WHERE label = ? AND mainTerm = ?" : 
			 "DELETE FROM `oto_synonym` WHERE label = ? AND mainTerm = ? AND NOT IN (" + notInSynonyms + ")";
		Query deleteOldSynonyms = new Query(deleteOldSynonymsQuery);
		deleteOldSynonyms.setParameter(1, label.getId());
		deleteOldSynonyms.setParameter(2, mainTerm.getId());
		deleteOldSynonyms.executeAndClose();
		
		for(Term synonymTerm : synonymTerms) {
			Query insert = new Query("INSERT INTO `oto_synonym` " +
					"(`mainTerm`, `label`, `synonymTerm`) VALUES (?, ?, ?)");			
			insert.setParameter(1, mainTerm.getId());
			insert.setParameter(2, label.getId());
			insert.setParameter(3, synonymTerm.getId());
			insert.executeAndClose();
		}
	}
	
}
