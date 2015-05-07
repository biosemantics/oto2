package edu.arizona.biosemantics.oto2.steps.server.persist.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.Synonym;

public class OntologyClassSubmissionPartOfDAO {
	
	public OntologyClassSubmissionPartOfDAO() {} 
	
	public PartOf get(int id) throws QueryException  {
		PartOf partOf = null;
		try(Query query = new Query("SELECT * FROM otosteps_ontologyclasssubmission_partof WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				partOf = createPartOf(result);
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return partOf;
	}
	
	public List<String> getPartOfs(int ontologyClassSubmissionId) throws QueryException {
		List<String> partOfs = new LinkedList<String>();
		try(Query query = new Query("SELECT * FROM otosteps_ontologyclasssubmission_partof WHERE ontologyclasssubmission = ?")) {
			query.setParameter(1, ontologyClassSubmissionId);
			ResultSet result = query.execute();
			while(result.next()) {
				partOfs.add(result.getString("partof"));
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return partOfs;
	}
	
	private PartOf createPartOf(ResultSet result) throws SQLException {
		int id = result.getInt("id");
		int ontologyClassSubmission = result.getInt("ontologyclasssubmission");
		String partOf = result.getString("partof");
		return new PartOf(id, ontologyClassSubmission, partOf);
	}

	public PartOf insert(PartOf partOf) throws QueryException  {
		if(!partOf.hasId()) {
			try(Query insert = new Query("INSERT INTO `otosteps_ontologyclasssubmission_partof` (`ontologyclasssubmission`, `partof`) VALUES(?, ?)")) {
				insert.setParameter(1, partOf.getOntologyClassSubmission());
				insert.setParameter(2, partOf.getPartOf());
				insert.execute();
				ResultSet generatedKeys = insert.getGeneratedKeys();
				generatedKeys.next();
				int id = generatedKeys.getInt(1);
				
				partOf.setId(id);
			} catch(QueryException | SQLException e) {
				log(LogLevel.ERROR, "Query Exception", e);
				throw new QueryException(e);
			}
		}
		return partOf;
	}
	
	public void update(PartOf partOf) throws QueryException  {		
		try(Query query = new Query("UPDATE otosteps_ontologyclasssubmission_partof SET ontologyclasssubmission = ?, partof = ? WHERE id = ?")) {
			query.setParameter(1, partOf.getOntologyClassSubmission());
			query.setParameter(2, partOf.getPartOf());
			query.setParameter(3, partOf.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}
	
	public void remove(PartOf partOf) throws QueryException  {
		try(Query query = new Query("DELETE FROM otosteps_ontologyclasssubmission_partof WHERE id = ?")) {
			query.setParameter(1, partOf.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}

	public List<PartOf> insert(int ontologyClassSubmissionId, List<String> partOfs) throws QueryException {
		List<PartOf> result = new LinkedList<PartOf>();
		for(String partOf : partOfs)
			result.add(insert(new PartOf(ontologyClassSubmissionId, partOf)));
		return result;
	}
	
	public void update(int ontologyClassSubmissionId, List<String> partofs) throws QueryException {
		remove(ontologyClassSubmissionId);
		for(String partof : partofs)
			insert(new PartOf(ontologyClassSubmissionId, partof));
	}
	
	public void remove(int ontologyClassSubmissionId) throws QueryException {
		try(Query query = new Query("DELETE FROM otosteps_ontologyclasssubmission_partof WHERE ontologyclasssubmission = ?")) {
			query.setParameter(1, ontologyClassSubmissionId);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}

	public void remove(List<PartOf> partOfs) throws QueryException {
		for(PartOf partOf : partOfs)
			this.remove(partOf);
	}
	
}
