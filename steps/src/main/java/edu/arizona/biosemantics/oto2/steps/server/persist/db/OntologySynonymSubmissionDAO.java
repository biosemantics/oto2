package edu.arizona.biosemantics.oto2.steps.server.persist.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.steps.shared.model.Term;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmissionStatus;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.StatusEnum;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySubmission.Type;

public class OntologySynonymSubmissionDAO {

	private TermDAO termDAO;
	private OntologyDAO ontologyDAO;
	private OntologySynonymSubmissionStatusDAO ontologySynonymSubmissionStatusDAO;
	private OntologySynonymSubmissionSynonymDAO ontologySynonymSubmissionSynonymDAO;
	
	public OntologySynonymSubmissionDAO() {} 
	
	public OntologySynonymSubmission get(int id) throws QueryException  {
		OntologySynonymSubmission ontologySynonymSubmission = null;
		try(Query query = new Query("SELECT * FROM otosteps_ontologysynonymsubmission WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				ontologySynonymSubmission = createSynonymSubmission(result);
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return ontologySynonymSubmission;
	}
	
	private OntologySynonymSubmission createSynonymSubmission(ResultSet result) throws SQLException, QueryException {
		int id = result.getInt("id");
		int collectionId = result.getInt("collection");
		int termId = result.getInt("term");
		Term term = null;
		if(!result.wasNull())
			term = termDAO.get(termId);
		String submission_term = result.getString("submission_term");
		int ontologyId = result.getInt("ontology");
		String classIRI = result.getString("class_iri");
		String source = result.getString("source");
		String sampleSentence = result.getString("sample_sentence");
		String typeString = result.getString("type");
		Type type = null;
		try { 
			type = Type.valueOf(typeString.toUpperCase());
		} catch(Exception e) { }
		String user = result.getString("user");
		
		Ontology ontology = ontologyDAO.get(ontologyId);
		List<OntologySynonymSubmissionStatus> ontologysynonymSubmissionStatuses = ontologySynonymSubmissionStatusDAO.getStatusOfOntologySynonymSubmission(id);
		return new OntologySynonymSubmission(id, collectionId, term, submission_term, ontology, classIRI, 
				ontologySynonymSubmissionSynonymDAO.getSynonyms(id), 
				source, sampleSentence, type,
				user, ontologysynonymSubmissionStatuses);
	}

	public OntologySynonymSubmission insert(OntologySynonymSubmission ontologySynonymSubmission) throws QueryException  {
		if(!ontologySynonymSubmission.hasId()) {
			try(Query insert = new Query("INSERT INTO `otosteps_ontologysynonymsubmission` "
					+ "(`collection`, `term`, `submission_term`, `ontology`, `class_iri`, `source`, `sample_sentence`, "
					+ "`type`, `user`)"
					+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
				insert.setParameter(1, ontologySynonymSubmission.getCollectionId());
				if(ontologySynonymSubmission.getTerm() == null)
					insert.setParameterNull(2, java.sql.Types.BIGINT);
				else
					insert.setParameter(2,ontologySynonymSubmission.getTerm().getId());
				insert.setParameter(3, ontologySynonymSubmission.getSubmissionTerm());
				insert.setParameter(4, ontologySynonymSubmission.getOntology().getId());
				insert.setParameter(5, ontologySynonymSubmission.getClassIRI());
				insert.setParameter(6, ontologySynonymSubmission.getSource());
				insert.setParameter(7, ontologySynonymSubmission.getSampleSentence());
				insert.setParameter(8, ontologySynonymSubmission.getType().toString().toUpperCase());
				insert.setParameter(9, ontologySynonymSubmission.getUser());
				insert.execute();
				ResultSet generatedKeys = insert.getGeneratedKeys();
				generatedKeys.next();
				int id = generatedKeys.getInt(1);
				
				ontologySynonymSubmission.setId(id);
				
				ontologySynonymSubmissionSynonymDAO.insert(id, ontologySynonymSubmission.getSynonyms());
			} catch(QueryException | SQLException e) {
				log(LogLevel.ERROR, "Query Exception", e);
				throw new QueryException(e);
			}
		}
		return ontologySynonymSubmission;
	}
	
	public void update(OntologySynonymSubmission ontologySynonymSubmission) throws QueryException  {		
		try(Query query = new Query("UPDATE otosteps_ontologysynonymsubmission SET collection = ?, term = ?, "
				+ "submission_term = ?, ontology = ?, class_iri = ?, source = ?, sample_sentence = ?, type = ?, "
				+ "user = ? WHERE id = ?")) {
			ontologySynonymSubmission.setCollectionId(ontologySynonymSubmission.getCollectionId());
			if(ontologySynonymSubmission.getTerm() == null)
				query.setParameterNull(2, java.sql.Types.BIGINT);
			else
				query.setParameter(2,ontologySynonymSubmission.getTerm().getId());
			query.setParameter(3, ontologySynonymSubmission.getSubmissionTerm());
			query.setParameter(4, ontologySynonymSubmission.getOntology().getId());
			query.setParameter(5, ontologySynonymSubmission.getClassIRI());
			query.setParameter(6, ontologySynonymSubmission.getSource());
			query.setParameter(7, ontologySynonymSubmission.getSampleSentence());
			query.setParameter(8, ontologySynonymSubmission.getType().toString().toUpperCase());
			query.setParameter(9, ontologySynonymSubmission.getUser());
			query.setParameter(10, ontologySynonymSubmission.getId());
			query.execute();
			
			ontologySynonymSubmissionStatusDAO.update(ontologySynonymSubmission.getSubmissionStatuses());
			ontologySynonymSubmissionSynonymDAO.update(ontologySynonymSubmission.getId(), ontologySynonymSubmission.getSynonyms());
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}
	
	public void remove(OntologySynonymSubmission ontologySynonymSubmission) throws QueryException  {
		try(Query query = new Query("DELETE FROM otosteps_ontologysynonymsubmission WHERE id = ?")) {
			query.setParameter(1, ontologySynonymSubmission.getId());
			query.execute();
			
			ontologySynonymSubmissionStatusDAO.remove(ontologySynonymSubmission.getSubmissionStatuses());
			ontologySynonymSubmissionSynonymDAO.remove(ontologySynonymSubmission.getId());
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}

	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}
	
	public void setOntologySynonymSubmissionStatusDAO(
			OntologySynonymSubmissionStatusDAO ontologySynonymSubmissionStatusDAO) {
		this.ontologySynonymSubmissionStatusDAO = ontologySynonymSubmissionStatusDAO;
	}

	public void setOntologyDAO(OntologyDAO ontologyDAO) {
		this.ontologyDAO = ontologyDAO;
	}
	
	public void setOntologySynonymSubmissionSynonymDAO(
			OntologySynonymSubmissionSynonymDAO ontologySynonymSubmissionSynonymDAO) {
		this.ontologySynonymSubmissionSynonymDAO = ontologySynonymSubmissionSynonymDAO;
	}

	public List<OntologySynonymSubmission> get(Collection collection) throws QueryException {
		return this.getByCollectionId(collection.getId());
	}
	

	public List<OntologySynonymSubmission> getByCollectionId(
			int ontologyId) throws QueryException {
		List<OntologySynonymSubmission> result = new LinkedList<OntologySynonymSubmission>();
		try(Query query = new Query("SELECT * FROM otosteps_ontologysynonymsubmission WHERE collection = ?")) {
			query.setParameter(1, ontologyId);
			ResultSet resultSet = query.execute();
			while(resultSet.next()) {
				result.add(createSynonymSubmission(resultSet));
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return result;
	}	

	public List<OntologySynonymSubmission> get(Collection collection, StatusEnum status) throws QueryException {
		List<OntologySynonymSubmission> result = new LinkedList<OntologySynonymSubmission>();
		try(Query query = new Query("SELECT * FROM otosteps_ontologyclasssubmission s, "
				+ "otosteps_ontologyclasssubmission_status ss, otosteps_status st"
				+ " WHERE s.collection = ? AND ss.ontologyclasssubmission = s.id AND ss.status = st.id AND"
				+ " st.name = ?")) {
			query.setParameter(1, collection.getId());
			query.setParameter(2, status.getDisplayName());
			ResultSet resultSet = query.execute();
			while(resultSet.next()) {
				result.add(createSynonymSubmission(resultSet));
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return result;
	}
	
}
