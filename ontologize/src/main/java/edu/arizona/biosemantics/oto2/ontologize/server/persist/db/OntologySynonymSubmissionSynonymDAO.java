package edu.arizona.biosemantics.oto2.ontologize.server.persist.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;

public class OntologySynonymSubmissionSynonymDAO {
	
	public OntologySynonymSubmissionSynonymDAO() {} 
	
	public List<Synonym> getSynonyms(int id) throws QueryException {
		List<Synonym> synonyms = new LinkedList<Synonym>();
		try(Query query = new Query("SELECT * FROM ontologize_ontologysynonymsubmission_synonym WHERE ontologysynonymsubmission = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				synonyms.add(createSynonym(result));
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return synonyms;
	}
	
	private Synonym createSynonym(ResultSet result) throws SQLException {
		int id = result.getInt("id");
		int ontologySynonymSubmission = result.getInt("ontologysynonymsubmission");
		String synonym = result.getString("synonym");
		return new Synonym(id, ontologySynonymSubmission, synonym);
	}

	public Synonym insert(Synonym synonym) throws QueryException  {
		if(synonym.hasId())
			this.remove(synonym);
		
		try(Query insert = new Query("INSERT INTO `ontologize_ontologysynonymsubmission_synonym` (`ontologysynonymsubmission`, `synonym`) VALUES(?, ?)")) {
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
		return synonym;
	}
	
	public void insert(List<Synonym> synonyms) throws QueryException {
		List<Synonym> result = new LinkedList<Synonym>();
		for(Synonym synonym : synonyms)
			result.add(insert(synonym));
	}
	
	public void update(Synonym synonym) throws QueryException  {		
		try(Query query = new Query("UPDATE ontologize_ontologysynonymsubmission_synonym SET ontologysynonymsubmission = ?, synonym = ? WHERE id = ?")) {
			query.setParameter(1, synonym.getSubmission());
			query.setParameter(2, synonym.getSynonym());
			query.setParameter(3, synonym.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}
	
	public void update(int ontologySynonymSubmissionId, List<Synonym> synonyms) throws QueryException {
		remove(ontologySynonymSubmissionId);
		for(Synonym synonym : synonyms) {
			insert(synonym);
		}
	}
	
	public void remove(int ontologySynonymSubmissionId) throws QueryException {
		try(Query query = new Query("DELETE FROM ontologize_ontologysynonymsubmission_synonym WHERE ontologysynonymsubmission = ?")) {
			query.setParameter(1, ontologySynonymSubmissionId);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}

	public void remove(Synonym synonym) throws QueryException  {
		try(Query query = new Query("DELETE FROM ontologize_ontologysynonymsubmission_synonym WHERE id = ?")) {
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
