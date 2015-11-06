package edu.arizona.biosemantics.oto2.ontologize.server.persist.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;

public class OntologyClassSubmissionSuperclassDAO {
	
	public OntologyClassSubmissionSuperclassDAO() {} 
	
	public Superclass get(int id) throws QueryException  {
		Superclass superclass = null;
		try(Query query = new Query("SELECT * FROM ontologize_ontologyclasssubmission_superclass WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				superclass = createSuperclass(result);
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return superclass;
	}
	
	public List<Superclass> getSuperclasses(int ontologyClassSubmissionId) throws QueryException {
		List<Superclass> superclasses = new LinkedList<Superclass>();
		try(Query query = new Query("SELECT * FROM ontologize_ontologyclasssubmission_superclass WHERE ontologyclasssubmission = ?")) {
			query.setParameter(1, ontologyClassSubmissionId);
			ResultSet result = query.execute();
			while(result.next()) {
				superclasses.add(createSuperclass(result));
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return superclasses;
	}
	
	private Superclass createSuperclass(ResultSet result) throws SQLException {
		int id = result.getInt("id");
		int ontologyClassSubmission = result.getInt("ontologyclasssubmission");
		String superclass = result.getString("superclass");
		return new Superclass(id, ontologyClassSubmission, superclass);
	}

	public Superclass insert(Superclass superclass) throws QueryException  {
		if(!superclass.hasId()) {
			try(Query insert = new Query("INSERT INTO `ontologize_ontologyclasssubmission_superclass` (`ontologyclasssubmission`, `superclass`) VALUES(?, ?)")) {
				insert.setParameter(1, superclass.getOntologyClassSubmission());
				insert.setParameter(2, superclass.getIri());
				insert.execute();
				ResultSet generatedKeys = insert.getGeneratedKeys();
				generatedKeys.next();
				int id = generatedKeys.getInt(1);
				
				superclass.setId(id);
			} catch(QueryException | SQLException e) {
				log(LogLevel.ERROR, "Query Exception", e);
				throw new QueryException(e);
			}
		}
		return superclass;
	}
	
	public void update(Superclass superclass) throws QueryException  {		
		try(Query query = new Query("UPDATE ontologize_ontologyclasssubmission_superclass SET ontologyclasssubmission = ?, superclass = ? WHERE id = ?")) {
			query.setParameter(1, superclass.getOntologyClassSubmission());
			query.setParameter(2, superclass.getIri());
			query.setParameter(3, superclass.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}
	
	public void remove(Superclass superclass) throws QueryException  {
		try(Query query = new Query("DELETE FROM ontologize_ontologyclasssubmission_superclass WHERE id = ?")) {
			query.setParameter(1, superclass.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}

	public List<Superclass> insert(List<Superclass> superclasses) throws QueryException {
		List<Superclass> result = new LinkedList<Superclass>();
		for(Superclass superclass : superclasses)
			result.add(insert(superclass));
		return result;
	}

	public void update(int ontologyClassSubmissionId, List<Superclass> superclasses) throws QueryException {
		remove(ontologyClassSubmissionId);
		for(Superclass superclass : superclasses)
			insert(superclass);
	}
	
	public void remove(int ontologyClassSubmissionId) throws QueryException {
		try(Query query = new Query("DELETE FROM ontologize_ontologyclasssubmission_superclass WHERE ontologyclasssubmission = ?")) {
			query.setParameter(1, ontologyClassSubmissionId);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}

	public void remove(List<Superclass> superclasses) throws QueryException {
		for(Superclass superclass : superclasses)
			this.remove(superclass);
	}
	
}
