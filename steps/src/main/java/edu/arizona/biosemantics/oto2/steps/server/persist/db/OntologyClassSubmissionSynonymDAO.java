package edu.arizona.biosemantics.oto2.steps.server.persist.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.Status;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.Synonym;

public class OntologyClassSubmissionSynonymDAO {
	
	public OntologyClassSubmissionSynonymDAO() {} 
	
	public Synonym get(int id) throws QueryException  {
		Synonym synonym = null;
		try(Query query = new Query("SELECT * FROM otosteps_ontologyclasssubmission_synonym WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				synonym = createSynonym(result);
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return synonym;
	}
	
	public List<String> getSynonyms(int ontologyClassSubmissionId) throws QueryException {
		List<String> synonyms = new LinkedList<String>();
		try(Query query = new Query("SELECT * FROM otosteps_ontologyclasssubmission_synonym WHERE ontologyclasssubmission = ?")) {
			query.setParameter(1, ontologyClassSubmissionId);
			ResultSet result = query.execute();
			while(result.next()) {
				synonyms.add(result.getString("synonym"));
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return synonyms;
	}
	
	private Synonym createSynonym(ResultSet result) throws SQLException {
		int id = result.getInt("id");
		int ontologyClassSubmission = result.getInt("ontologyclasssubmission");
		String synonym = result.getString("synonym");
		return new Synonym(id, ontologyClassSubmission, synonym);
	}

	public Synonym insert(Synonym synonym) throws QueryException  {
		if(!synonym.hasId()) {
			try(Query insert = new Query("INSERT INTO `otosteps_ontologyclasssubmission_synonym` (`ontologyclasssubmission`, `synonym`) VALUES(?, ?)")) {
				insert.setParameter(1, synonym.getSubmission());
				insert.setParameter(2, synonym.getSynonym());
				insert.execute();
				ResultSet generatedKeys = insert.getGeneratedKeys();
				generatedKeys.next();
				int id = generatedKeys.getInt(1);
				
				synonym.setId(id);
			} catch(QueryException | SQLException e) {
				log(LogLevel.ERROR, "Query Exception", e);
				throw new QueryException(e);
			}
		}
		return synonym;
	}
	
	public List<Synonym> insert(int ontologyClassSubmissionId, List<String> synonyms) throws QueryException {
		List<Synonym> result = new LinkedList<Synonym>();
		for(String synonym : synonyms)
			result.add(insert(new Synonym(ontologyClassSubmissionId, synonym)));
		return result;
	}
	
	public void update(Synonym synonym) throws QueryException  {		
		try(Query query = new Query("UPDATE otosteps_ontologyclasssubmission_synonym SET ontologyclasssubmission = ?, synonym = ? WHERE id = ?")) {
			query.setParameter(1, synonym.getSubmission());
			query.setParameter(2, synonym.getSynonym());
			query.setParameter(3, synonym.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}
	
	public void update(int ontologyClassSubmissionId, List<String> synonyms) throws QueryException {
		remove(ontologyClassSubmissionId);
		for(String synonym : synonyms)
			insert(new Synonym(ontologyClassSubmissionId, synonym));
	}
	
	public void remove(int ontologyClassSubmissionId) throws QueryException {
		try(Query query = new Query("DELETE FROM otosteps_ontologyclasssubmission_synonym WHERE ontologyclasssubmission = ?")) {
			query.setParameter(1, ontologyClassSubmissionId);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}

	public void remove(Synonym synonym) throws QueryException  {
		try(Query query = new Query("DELETE FROM otosteps_ontologyclasssubmission_synonym WHERE id = ?")) {
			query.setParameter(1, synonym.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}

	public void remove(List<Synonym> synonyms) throws QueryException {
		for(Synonym synonym : synonyms)
			this.remove(synonym);
	}

	
}
