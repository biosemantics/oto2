package edu.arizona.biosemantics.oto.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto.oto.shared.model.Context;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class TermDAO {

	private static TermDAO instance;
	
	public static TermDAO getInstance() {
		if(instance == null)
			instance = new TermDAO();
		return instance;
	}
	
	public Term getTerm(int id) throws SQLException, ClassNotFoundException, IOException {
		Term term = null;
		Query query = new Query("SELECT * FROM term WHERE id = ?");
		query.setParameter(1, id);
		ResultSet result = query.execute();
		while(result.next()) {
			term = createTerm(result);
		}
		query.close();
		return term;
	}
	
	private Term createTerm(ResultSet result) throws ClassNotFoundException, SQLException, IOException {
		int id = result.getInt(1);
		String text = result.getString(3);
		
		int bucketId = result.getInt(2);
		return new Term(id, text, BucketDAO.getInstance().getBucket(bucketId));
	}

	public Term insert(Term term) throws SQLException, ClassNotFoundException, IOException {
		if(!term.hasId()) {
			Query insert = new Query("INSERT INTO `term` " +
					"(`bucket`, `term`) VALUES (?, ?)");
			insert.setParameter(1, term.getBucket().getId());
			insert.setParameter(2, term.getTerm());
			insert.execute();
			ResultSet generatedKeys = insert.getGeneratedKeys();
			generatedKeys.next();
			int id = generatedKeys.getInt(1);
			insert.close();		
			term.setId(id);
			
			for(Context context : term.getContexts()) {
				ContextDAO.getInstance().insert(context);
			}
		}
		return term;
	}

	public void update(Term term) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("UPDATE term SET bucket = ?, term = ? WHERE id = ?");
		query.setParameter(1, term.getBucket().getId());
		query.setParameter(2, term.getTerm());
		query.setParameter(3, term.getId());
		
		for(Context context : term.getContexts()) {
			ContextDAO.getInstance().update(context);
		}
		
		query.executeAndClose();
	}

	public void remove(Term term) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("DELETE FROM term WHERE id = ?");
		query.setParameter(1, term.getId());
		query.executeAndClose();
		
		for(Context context : term.getContexts())
			ContextDAO.getInstance().remove(context);
	}

	public List<Term> getTerms(Label label) throws ClassNotFoundException, SQLException, IOException {
		List<Term> terms = new LinkedList<Term>();
		Query query = new Query("SELECT * FROM term WHERE label = ?");
		query.setParameter(1, label.getId());
		ResultSet result = query.execute();
		while(result.next()) {
			int id = result.getInt(1);
			terms.add(getTerm(id));
		}
		query.close();
		return terms;		
	}
	
	public List<Term> getTerms(Bucket bucket) throws ClassNotFoundException, SQLException, IOException {
		List<Term> terms = new LinkedList<Term>();
		Query query = new Query("SELECT * FROM term WHERE bucket = ?");
		query.setParameter(1, bucket.getId());
		ResultSet result = query.execute();
		while(result.next()) {
			int id = result.getInt(1);
			String text = result.getString(2);
			terms.add(getTerm(id));
		}
		query.close();
		return terms;
	}
	
}
