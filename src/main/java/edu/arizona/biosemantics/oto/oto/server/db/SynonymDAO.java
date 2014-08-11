package edu.arizona.biosemantics.oto.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

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
	
}
