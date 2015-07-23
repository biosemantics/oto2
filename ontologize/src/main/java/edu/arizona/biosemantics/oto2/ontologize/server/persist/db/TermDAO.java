package edu.arizona.biosemantics.oto2.ontologize.server.persist.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;

public class TermDAO {
		
	public TermDAO() {} 
	
	public Term get(int id)  {
		Term term = null;
		try(Query query = new Query("SELECT * FROM ontologize_term WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				term = createTerm(result);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return term;
	}
	
	private Term createTerm(ResultSet result) throws SQLException {
		int id = result.getInt("id");
		String term = result.getString("term");
		String originalTerm = result.getString("original_term");
		String iri = result.getString("iri");
		String buckets = result.getString("buckets");
		String category = result.getString("category");
		boolean removed = result.getBoolean("removed");
		int collectionId = result.getInt("collection");
		return new Term(id, term, originalTerm, iri, buckets, category, removed, collectionId);
	}

	public Term insert(Term term, int collectionId)  {
		if(!term.hasId()) {
			try(Query insert = new Query("INSERT INTO `ontologize_term` (`term`, `original_term`, `iri`, `buckets`, `category`, `collection`) VALUES(?, ?, ?, ?, ?, ?)")) {
				insert.setParameter(1, term.getTerm());
				insert.setParameter(2, term.getOriginalTerm());
				insert.setParameter(3, term.getIri());
				insert.setParameter(4, term.getBuckets());
				insert.setParameter(5, term.getCategory());
				insert.setParameter(6, collectionId);
				insert.execute();
				ResultSet generatedKeys = insert.getGeneratedKeys();
				generatedKeys.next();
				int id = generatedKeys.getInt(1);
				
				term.setId(id);
			} catch(Exception e) {
				log(LogLevel.ERROR, "Query Exception", e);
			}
		}
		return term;
	}
	
	public void update(Term term, int collectionId)  {
		if(term.hasId()) {
			try(Query query = new Query("UPDATE ontologize_term SET term = ?, original_term = ?, iri = ?, buckets = ?, category = ?, removed = ?, "
					+ "collection = ? WHERE id = ?")) {
				query.setParameter(1, term.getTerm());
				query.setParameter(2, term.getOriginalTerm());
				query.setParameter(3, term.getIri());
				query.setParameter(4, term.getBuckets());
				query.setParameter(5, term.getCategory());
				query.setParameter(6, term.isRemoved());
				query.setParameter(7, collectionId);
				query.setParameter(8, term.getId());
				query.execute();
			} catch(QueryException e) {
				log(LogLevel.ERROR, "Query Exception", e);
			}
		} else {
			this.insert(term, collectionId);
		}
	}
	
	public void remove(Term term)  {
		try(Query query = new Query("DELETE FROM ontologize_term WHERE id = ?")) {
			query.setParameter(1, term.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
	}

	public List<Term> getTerms(int collectionId) {
		List<Term> terms = new LinkedList<Term>();
		try(Query query = new Query("SELECT id FROM ontologize_term WHERE collection = ?")) {
			query.setParameter(1, collectionId);
			ResultSet result = query.execute();
			while(result.next()) {
				int id = result.getInt(1);
				Term term = get(id);
				if(term != null)
					terms.add(term);
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return terms;
	}
}
