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
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmissionStatus;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.StatusEnum;

public class OntologyClassSubmissionDAO {

	private TermDAO termDAO;
	private OntologyDAO ontologyDAO;
	private OntologyClassSubmissionStatusDAO ontologyClassSubmissionStatusDAO;
	private OntologyClassSubmissionSynonymDAO ontologyClassSubmissionSynonymDAO;
	private OntologyClassSubmissionSuperclassDAO ontologyClassSubmissionSuperclassDAO;
	private OntologyClassSubmissionPartOfDAO ontologyClassSubmissionPartOfDAO;
	
	public OntologyClassSubmissionDAO() {} 
	
	public OntologyClassSubmission get(int id) throws QueryException  {
		OntologyClassSubmission classSubmission = null;
		try(Query query = new Query("SELECT * FROM otosteps_ontologyclasssubmission WHERE id = ?")) {
			query.setParameter(1, id);
			ResultSet result = query.execute();
			while(result.next()) {
				classSubmission = createClassSubmission(result);
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return classSubmission;
	}
	
	private OntologyClassSubmission createClassSubmission(ResultSet result) throws QueryException, SQLException {
		int id = result.getInt("id");
		int termId = result.getInt("term");
		String submission_term = result.getString("submission_term");
		int ontologyId = result.getInt("ontology");
		String classIRI = result.getString("class_iri");
		String definition = result.getString("definition");
		String source = result.getString("source");
		String sampleSentence = result.getString("sample_sentence");
		boolean entity = result.getBoolean("entity");
		boolean quality = result.getBoolean("quality");
		String user = result.getString("user");
		
		List<OntologyClassSubmissionStatus> ontologyClassSubmissionStatuses = ontologyClassSubmissionStatusDAO.getStatusOfOntologyClassSubmission(id);
		OntologyClassSubmissionStatus mostRecentStatus = ontologyClassSubmissionStatuses.get(ontologyClassSubmissionStatuses.size() - 1);
		if(mostRecentStatus != null && 
				mostRecentStatus.getStatus().getName().equals(StatusEnum.ACCEPTED.getDisplayName()) && !classIRI.equals(mostRecentStatus.getIri())) {
			classIRI = mostRecentStatus.getIri();
			
			try(Query query = new Query("UPDATE otosteps_ontologyclasssubmission SET class_iri = ? WHERE id = ?")) {
				query.setParameter(1, classIRI);
				query.setParameter(2, id);
				query.execute();
			} catch(QueryException e) {
				log(LogLevel.ERROR, "Query Exception", e);
				throw e;
			}
		}
		
		Term term = termDAO.get(termId);
		Ontology ontology = ontologyDAO.get(ontologyId);
		
		return new OntologyClassSubmission(id, term, submission_term, ontology, classIRI, ontologyClassSubmissionSuperclassDAO.getSuperclasses(id), 
				definition, ontologyClassSubmissionSynonymDAO.getSynonyms(id), source, sampleSentence,
				ontologyClassSubmissionPartOfDAO.getPartOfs(id), entity, quality, user, ontologyClassSubmissionStatuses);
	}

	public OntologyClassSubmission insert(OntologyClassSubmission ontologyClassSubmission) throws QueryException  {
		if(!ontologyClassSubmission.hasId()) {
			try(Query insert = new Query("INSERT INTO `otosteps_ontologyclasssubmission` "
					+ "(`term`, `submission_term`, `ontology`, `class_iri`, `definition`, `source`, `sample_sentence`, "
					+ "`entity`, `quality`, `user`)"
					+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
				insert.setParameter(1, ontologyClassSubmission.getTerm().getId());
				insert.setParameter(2, ontologyClassSubmission.getSubmissionTerm());
				insert.setParameter(3, ontologyClassSubmission.getOntology().getId());
				insert.setParameter(4, ontologyClassSubmission.getClassIRI());
				insert.setParameter(5, ontologyClassSubmission.getDefinition());
				insert.setParameter(6, ontologyClassSubmission.getSource());
				insert.setParameter(7, ontologyClassSubmission.getSampleSentence());
				insert.setParameter(8, ontologyClassSubmission.isEntity());
				insert.setParameter(9, ontologyClassSubmission.isQuality());
				insert.setParameter(10, ontologyClassSubmission.getUser());
				insert.execute();
				ResultSet generatedKeys = insert.getGeneratedKeys();
				generatedKeys.next();
				int id = generatedKeys.getInt(1);
				
				ontologyClassSubmission.setId(id);
				
				ontologyClassSubmissionSynonymDAO.insert(id, ontologyClassSubmission.getSynonyms());
				ontologyClassSubmissionSuperclassDAO.insert(id, ontologyClassSubmission.getSuperclassIRIs());
				ontologyClassSubmissionPartOfDAO.insert(id, ontologyClassSubmission.getPartOfIRIs());
			} catch(QueryException | SQLException e) {
				log(LogLevel.ERROR, "Query Exception", e);
				throw new QueryException(e);
			}
		}
		return ontologyClassSubmission;
	}
	
	public void update(OntologyClassSubmission ontologyClassSubmission) throws QueryException  {		
		try(Query query = new Query("UPDATE otosteps_ontologyclasssubmission SET term = ?, submission_term = ?,"
				+ " ontology = ?, class_iri = ?, definition = ?, source = ?, sample_sentence = ?, "
				+ "entity = ?, quality = ?, user = ? WHERE id = ?")) {
			query.setParameter(1, ontologyClassSubmission.getTerm().getId());
			query.setParameter(2, ontologyClassSubmission.getSubmissionTerm());
			query.setParameter(3, ontologyClassSubmission.getOntology().getId());
			query.setParameter(4, ontologyClassSubmission.getClassIRI());
			query.setParameter(5, ontologyClassSubmission.getDefinition());
			query.setParameter(6, ontologyClassSubmission.getSource());
			query.setParameter(7, ontologyClassSubmission.getSampleSentence());
			query.setParameter(8, ontologyClassSubmission.isEntity());
			query.setParameter(9, ontologyClassSubmission.isQuality());
			query.setParameter(10, ontologyClassSubmission.getUser());
			query.setParameter(11, ontologyClassSubmission.getId());
			query.execute();
			
			ontologyClassSubmissionStatusDAO.update(ontologyClassSubmission.getSubmissionStatuses());
			ontologyClassSubmissionSynonymDAO.update(ontologyClassSubmission.getId(), ontologyClassSubmission.getSynonyms());
			ontologyClassSubmissionSuperclassDAO.update(ontologyClassSubmission.getId(), ontologyClassSubmission.getSuperclassIRIs());
			ontologyClassSubmissionPartOfDAO.update(ontologyClassSubmission.getId(), ontologyClassSubmission.getPartOfIRIs());
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}
	
	public void remove(OntologyClassSubmission ontologyClassSubmission) throws QueryException  {
		try(Query query = new Query("DELETE FROM otosteps_ontologyclasssubmission WHERE id = ?")) {
			query.setParameter(1, ontologyClassSubmission.getId());
			query.execute();
			
			ontologyClassSubmissionStatusDAO.remove(ontologyClassSubmission.getSubmissionStatuses());
			ontologyClassSubmissionSynonymDAO.remove(ontologyClassSubmission.getId());
			ontologyClassSubmissionSuperclassDAO.remove(ontologyClassSubmission.getId());
			ontologyClassSubmissionPartOfDAO.remove(ontologyClassSubmission.getId());
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}

	public void setTermDAO(TermDAO termDAO) {
		this.termDAO = termDAO;
	}

	public void setOntologyClassSubmissionStatusDAO(
			OntologyClassSubmissionStatusDAO ontologyClassSubmissionStatusDAO) {
		this.ontologyClassSubmissionStatusDAO = ontologyClassSubmissionStatusDAO;
	}

	public void setOntologyDAO(OntologyDAO ontologyDAO) {
		this.ontologyDAO = ontologyDAO;
	}
	
	public void setOntologyClassSubmissionSynonymDAO(
			OntologyClassSubmissionSynonymDAO ontologyClassSubmissionSynonymDAO) {
		this.ontologyClassSubmissionSynonymDAO = ontologyClassSubmissionSynonymDAO;
	}

	public void setOntologyClassSubmissionSuperclassDAO(
			OntologyClassSubmissionSuperclassDAO ontologyClassSubmissionSuperclassDAO) {
		this.ontologyClassSubmissionSuperclassDAO = ontologyClassSubmissionSuperclassDAO;
	}

	public void setOntologyClassSubmissionPartOfDAO(
			OntologyClassSubmissionPartOfDAO ontologyClassSubmissionPartOfDAO) {
		this.ontologyClassSubmissionPartOfDAO = ontologyClassSubmissionPartOfDAO;
	}

	public List<OntologyClassSubmission> get(Collection collection) throws QueryException {
		List<OntologyClassSubmission> result = new LinkedList<OntologyClassSubmission>();
		try(Query query = new Query("SELECT * FROM otosteps_ontologyclasssubmission s, otosteps_term t WHERE s.term = t.id AND t.collection = ?")) {
			query.setParameter(1, collection.getId());
			ResultSet resultSet = query.execute();
			while(resultSet.next()) {
				result.add(createClassSubmission(resultSet));
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return result;
	}
	
	public List<OntologyClassSubmission> get(Collection collection, StatusEnum status) throws QueryException {
		List<OntologyClassSubmission> result = new LinkedList<OntologyClassSubmission>();
		try(Query query = new Query("SELECT * FROM otosteps_ontologyclasssubmission s, "
				+ "otosteps_ontologyclasssubmission_status ss, otosteps_status st,"
				+ " otosteps_term t WHERE s.term = t.id AND t.collection = ? AND ss.ontologyclasssubmission = s.id AND ss.status = st.id AND"
				+ " st.name = ?")) {
			query.setParameter(1, collection.getId());
			query.setParameter(2, status.getDisplayName());
			ResultSet resultSet = query.execute();
			while(resultSet.next()) {
				result.add(createClassSubmission(resultSet));
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
		return result;
	}	

}
