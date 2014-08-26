package edu.arizona.biosemantics.oto2.oto.server.db;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.oto2.oto.server.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class TermDAO {
	
	private BucketDAO bucketDAO;
	
	protected TermDAO() { }
		
	public void setBucketDAO(BucketDAO bucketDAO) {
		this.bucketDAO = bucketDAO;
	}

	public Term get(int id)  {
		Term term = null;
		try(Query query = new Query("SELECT * FROM oto_term WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				term = createTerm(result);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}	
		return term;
	}
	
	private Term createTerm(ResultSet result) throws SQLException  {
		int id = result.getInt(1);
		//int bucketId = result.getInt(2);
		String text = result.getString(3);
		String originalTerm = result.getString(4);
		return new Term(id, text, originalTerm);
	}

	public Term insert(Term term, int bucketId)  {
		if(!term.hasId()) {
			try(Query insert = new Query("INSERT INTO `oto_term` " +
					"(`bucket`, `term`, `original_term`) VALUES (?, ?, ?)")) {
				insert.setParameter(1, bucketId);
				insert.setParameter(2, term.getTerm());
				insert.setParameter(3, term.getTerm());
				insert.execute();
				ResultSet generatedKeys = insert.getGeneratedKeys();
				generatedKeys.next();
				int id = generatedKeys.getInt(1);
				term.setId(id);
			} catch(Exception e) {
				e.printStackTrace();
			}	
		}
		return term;
	}

	public void update(Term term, int bucketId) {
		try(Query query = new Query("UPDATE oto_term SET bucket = ?, term = ? WHERE id = ?")) {
			query.setParameter(1, bucketId);
			query.setParameter(2, term.getTerm());
			//never update original_term because it contains the *original* spelling of the term
			query.setParameter(3, term.getId());	
			query.execute();
		} catch(QueryException e) {
			e.printStackTrace();
		}	
	}

	public void remove(Term term)  {
		try(Query query = new Query("DELETE FROM oto_term WHERE id = ?")) {
			query.setParameter(1, term.getId());
			query.execute();
		} catch(QueryException e) {
			e.printStackTrace();
		}	
	}
	
	public List<Term> getTerms(Bucket bucket)  {
		List<Term> terms = new LinkedList<Term>();
		try(Query query = new Query("SELECT * FROM oto_term WHERE bucket = ?")) {
			query.setParameter(1, bucket.getId());
			ResultSet result = query.execute();
			while(result.next()) {
				int id = result.getInt(1);
				terms.add(get(id));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}	
		return terms;
	}

	public List<Term> getTerms(Collection collection)  {
		List<Term> result = new LinkedList<Term>();
		List<Bucket> buckets = bucketDAO.getBuckets(collection);
		for(Bucket bucket : buckets) {
			result.addAll(this.getTerms(bucket));
		}
		return result;
	}

	public void resetTerms(Collection collection)  {
		try(Query query = new Query("UPDATE oto_term t, oto_bucket b SET t.term = t.original_term WHERE b.collection = ? AND t.bucket = b.id")) {
			query.setParameter(1, collection.getId());
			query.execute();
		} catch(QueryException e) {
			e.printStackTrace();
		}	
	}
	
}
