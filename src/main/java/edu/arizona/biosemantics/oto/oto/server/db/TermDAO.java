package edu.arizona.biosemantics.oto.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Context;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class TermDAO {
	
	private ContextDAO contextDAO;
	private BucketDAO bucketDAO;
		
	public void setContextDAO(ContextDAO contextDAO) {
		this.contextDAO = contextDAO;
	}

	public void setBucketDAO(BucketDAO bucketDAO) {
		this.bucketDAO = bucketDAO;
	}

	public Term get(int id) throws SQLException, ClassNotFoundException, IOException {
		Term term = null;
		Query query = new Query("SELECT * FROM oto_term WHERE id = ?");
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
		int bucketId = result.getInt(2);
		String text = result.getString(3);
		return new Term(id, text);
	}

	public Term insert(Term term) throws SQLException, ClassNotFoundException, IOException {
		if(!term.hasId()) {
			Query insert = new Query("INSERT INTO `oto_term` " +
					"(`bucket`, `term`) VALUES (?, ?)");
			insert.setParameter(1, term.getBucket().getId()); //term.getBucket().getId());
			insert.setParameter(2, term.getTerm());
			insert.execute();
			ResultSet generatedKeys = insert.getGeneratedKeys();
			generatedKeys.next();
			int id = generatedKeys.getInt(1);
			insert.close();		
			term.setId(id);
			
			for(Context context : term.getContexts()) {
				contextDAO.insert(context);
			}
		}
		return term;
	}

	public void update(Term term) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("UPDATE oto_term SET bucket = ?, term = ? WHERE id = ?");
		query.setParameter(1, term.getBucket().getId()); //term.getBucket().getId());
		query.setParameter(2, term.getTerm());
		query.setParameter(3, term.getId());
		
		for(Context context : term.getContexts()) {
			contextDAO.update(context);
		}
		
		query.executeAndClose();
	}

	public void remove(Term term) throws SQLException, ClassNotFoundException, IOException {
		Query query = new Query("DELETE FROM oto_term WHERE id = ?");
		query.setParameter(1, term.getId());
		query.executeAndClose();
		
		for(Context context : term.getContexts())
			contextDAO.remove(context);
	}

	public List<Term> getTerms(Label label) throws ClassNotFoundException, SQLException, IOException {
		List<Term> terms = new LinkedList<Term>();
		Query query = new Query("SELECT * FROM oto_labeling WHERE label = ?");
		query.setParameter(1, label.getId());
		ResultSet result = query.execute();
		while(result.next()) {
			int id = result.getInt(1);
			terms.add(get(id));
		}
		query.close();
		return terms;		
	}
	
	public List<Term> getTerms(Bucket bucket) throws ClassNotFoundException, SQLException, IOException {
		List<Term> terms = new LinkedList<Term>();
		Query query = new Query("SELECT * FROM oto_term WHERE bucket = ?");
		query.setParameter(1, bucket.getId());
		ResultSet result = query.execute();
		while(result.next()) {
			int id = result.getInt(1);
			String text = result.getString(2);
			terms.add(get(id));
		}
		query.close();
		return terms;
	}

	public List<Term> getTerms(Collection collection) throws ClassNotFoundException, SQLException, IOException {
		List<Term> result = new LinkedList<Term>();
		List<Bucket> buckets = bucketDAO.getBuckets(collection);
		for(Bucket bucket : buckets) {
			result.addAll(this.getTerms(bucket));
		}
		return result;
	}
	
}
