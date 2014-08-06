package edu.arizona.biosemantics.oto.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class LabelingDAO {
	
	private TermDAO termDAO;
	private LabelDAO labelDAO;

	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}
	
	public void setLabelDAO(LabelDAO labelDAO) {
		this.labelDAO = labelDAO;
	}
	
	public Set<Label> get(Term term) throws ClassNotFoundException, SQLException, IOException {
		Set<Label> labels = new HashSet<Label>();
		Query query = new Query("SELECT * FROM oto_labeling WHERE term = ?");
		query.setParameter(1, term.getId());
		ResultSet result = query.execute();
		while(result.next()) {
			int labelId = result.getInt(1);
			labels.add(labelDAO.get(labelId));
		}
		query.close();
		return labels;
	}
	
	public Set<Term> get(Label label) throws ClassNotFoundException, SQLException, IOException {
		Set<Term> terms = new HashSet<Term>();
		Query query = new Query("SELECT * FROM oto_labeling WHERE label = ?");
		query.setParameter(1, label.getId());
		ResultSet result = query.execute();
		while(result.next()) {
			int termId = result.getInt(2);
			terms.add(termDAO.get(termId));
		}
		query.close();
		return terms;
	}

	public void insert(Term term, Label label) throws ClassNotFoundException, SQLException, IOException {
		Query query = new Query("INSERT INTO `oto_labeling` " +
				"(`term`, `label`) VALUES (?, ?)");
		query.setParameter(1, term.getId());
		query.setParameter(2, label.getId());
		ResultSet result = query.execute();
		query.close();
	}
	
	public void ensure(Term term, Label label) throws ClassNotFoundException, SQLException, IOException {
		Query query = new Query("SELECT * FROM `oto_labeling` WHERE term = ? AND label = ?");
		query.setParameter(1, term.getId());
		query.setParameter(2, label.getId());
		ResultSet result = query.execute();
		if(!result.next()) 
			insert(term, label);
		query.close();
	}
	
	public void ensure(Label label, List<Term> terms) throws SQLException, ClassNotFoundException, IOException {
		String queryString = "DELETE FROM `oto_labeling` WHERE label = ?"; 
		for(Term term : terms)
			queryString += " AND term != " + term.getId();
		Query query = new Query(queryString);
		query.setParameter(1, label.getId());
		ResultSet result = query.execute();
		query.close();
		
		for(Term term : terms)
			ensure(term, label);
	}
	
	
	public void remove(Term term, Label label) throws ClassNotFoundException, SQLException, IOException {
		Query query = new Query("DELETE FROM `oto_labeling` WHERE term = ? AND label = ?");
		query.setParameter(1, term.getId());
		query.setParameter(2, label.getId());
		ResultSet result = query.execute();
		query.close();
	}

}