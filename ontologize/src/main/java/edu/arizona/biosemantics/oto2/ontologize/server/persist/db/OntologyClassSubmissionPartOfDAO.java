package edu.arizona.biosemantics.oto2.ontologize.server.persist.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.ontologize.server.Configuration;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.file.PermanentOntologyFileDAO;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;

public class OntologyClassSubmissionPartOfDAO {
	
	private PermanentOntologyFileDAO permanentOntologyFileDAO;
	
	public OntologyClassSubmissionPartOfDAO() {} 
	
	public List<PartOf> getPartOfs(int ontologyClassSubmissionId) throws Exception {
		List<PartOf> partOfs = new LinkedList<PartOf>();
		try(Query query = new Query("SELECT * FROM ontologize_ontologyclasssubmission_partof WHERE ontologyclasssubmission = ?")) {
			query.setParameter(1, ontologyClassSubmissionId);
			ResultSet result = query.execute();
			while(result.next()) {
				partOfs.add(createPartOf(result));
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return partOfs;
	}
	
	private PartOf createPartOf(ResultSet result) throws Exception {
		int id = result.getInt("id");
		int ontologyClassSubmission = result.getInt("ontologyclasssubmission");
		String partOf = result.getString("partof");
		String label = getLabel(partOf);
		return new PartOf(id, ontologyClassSubmission, partOf, label);
	}

	private String getLabel(String classIri) throws Exception {
		//if(classIri.startsWith(Configuration.etcOntologyBaseIRI)) {
		try(Query query = new Query("SELECT * FROM ontologize_ontologyclasssubmission s"
				+ " WHERE s.class_iri = ?")) {
			query.setParameter(1, classIri);
			ResultSet resultSet = query.execute();
			if(resultSet.next()) {
				return resultSet.getString("submission_term");
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		
		return permanentOntologyFileDAO.getClassLabel(classIri);
	}

	public PartOf insert(PartOf partOf) throws QueryException  {
		if(partOf.hasId()) 
			this.remove(partOf);
		try(Query insert = new Query("INSERT INTO `ontologize_ontologyclasssubmission_partof` (`ontologyclasssubmission`, `partof`) VALUES(?, ?)")) {
			insert.setParameter(1, partOf.getOntologyClassSubmission());
			insert.setParameter(2, partOf.getIri());
			insert.execute();
			ResultSet generatedKeys = insert.getGeneratedKeys();
			generatedKeys.next();
			int id = generatedKeys.getInt(1);
			
			partOf.setId(id);
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return partOf;
	}
	
	public void update(PartOf partOf) throws QueryException  {		
		try(Query query = new Query("UPDATE ontologize_ontologyclasssubmission_partof SET ontologyclasssubmission = ?, partof = ? WHERE id = ?")) {
			query.setParameter(1, partOf.getOntologyClassSubmission());
			query.setParameter(2, partOf.getIri());
			query.setParameter(3, partOf.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}
	
	public void remove(PartOf partOf) throws QueryException  {
		try(Query query = new Query("DELETE FROM ontologize_ontologyclasssubmission_partof WHERE id = ?")) {
			query.setParameter(1, partOf.getId());
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}

	public List<PartOf> insert(List<PartOf> partOfs) throws QueryException {
		List<PartOf> result = new LinkedList<PartOf>();
		for(PartOf partOf : partOfs) 
			result.add(insert(partOf));
		return result;
	}
	
	public void update(int ontologyClassSubmissionId, List<PartOf> partofs) throws QueryException {
		remove(ontologyClassSubmissionId);
		for(PartOf partof : partofs) {
			insert(partof);
		}
	}
	
	public void remove(int ontologyClassSubmissionId) throws QueryException {
		try(Query query = new Query("DELETE FROM ontologize_ontologyclasssubmission_partof WHERE ontologyclasssubmission = ?")) {
			query.setParameter(1, ontologyClassSubmissionId);
			query.execute();
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}
	
	public void remove(OntologyClassSubmission ontologyClassSubmission) throws QueryException {
		this.remove(ontologyClassSubmission.getId());
		if(ontologyClassSubmission.hasClassIRI()) {
			try(Query query = new Query("DELETE FROM ontologize_ontologyclasssubmission_partof WHERE partof = ?")) {
				query.setParameter(1, ontologyClassSubmission.getClassIRI());
				query.execute();
			} catch(QueryException e) {
				log(LogLevel.ERROR, "Query Exception", e);
				throw e;
			}
		}
	}

	public void remove(List<PartOf> partOfs) throws QueryException {
		for(PartOf partOf : partOfs)
			this.remove(partOf);
	}

	public void setPermanentOntologyFileDAO(PermanentOntologyFileDAO permanentOntologyFileDAO) {
		this.permanentOntologyFileDAO = permanentOntologyFileDAO;
	}


}
