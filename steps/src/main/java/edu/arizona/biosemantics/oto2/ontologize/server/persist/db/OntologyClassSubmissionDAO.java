package edu.arizona.biosemantics.oto2.ontologize.server.persist.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmissionStatus;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.StatusEnum;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySubmission.Type;

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
		try(Query query = new Query("SELECT * FROM ontologize_ontologyclasssubmission WHERE id = ?")) {
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
		int collectionId = result.getInt("collection");
		int termId = result.getInt("term");
		Term term = null;
		if(!result.wasNull())
			term = termDAO.get(termId);
		String submission_term = result.getString("submission_term");
		int ontologyId = result.getInt("ontology");
		String classIRI = result.getString("class_iri");
		String definition = result.getString("definition");
		String source = result.getString("source");
		String sampleSentence = result.getString("sample_sentence");
		String typeString = result.getString("type");
		Type type = null;
		try { 
			type = Type.valueOf(typeString.toUpperCase());
		} catch(Exception e) { }
		String user = result.getString("user");
		
		List<OntologyClassSubmissionStatus> ontologyClassSubmissionStatuses = ontologyClassSubmissionStatusDAO.getStatusOfOntologyClassSubmission(id);
		OntologyClassSubmissionStatus mostRecentStatus = ontologyClassSubmissionStatuses.get(ontologyClassSubmissionStatuses.size() - 1);
		if(mostRecentStatus != null && 
				mostRecentStatus.getStatus().getName().equals(StatusEnum.ACCEPTED.getDisplayName()) && !classIRI.equals(mostRecentStatus.getIri())) {
			classIRI = mostRecentStatus.getIri();
			
			try(Query query = new Query("UPDATE ontologize_ontologyclasssubmission SET class_iri = ? WHERE id = ?")) {
				query.setParameter(1, classIRI);
				query.setParameter(2, id);
				query.execute();
			} catch(QueryException e) {
				log(LogLevel.ERROR, "Query Exception", e);
				throw e;
			}
		}
		
		Ontology ontology = ontologyDAO.get(ontologyId);
		return new OntologyClassSubmission(id, collectionId, 
				term, submission_term, ontology, classIRI, ontologyClassSubmissionSuperclassDAO.getSuperclasses(id), 
				definition, ontologyClassSubmissionSynonymDAO.getSynonyms(id), source, sampleSentence,
				ontologyClassSubmissionPartOfDAO.getPartOfs(id), type, user, ontologyClassSubmissionStatuses);
	}

	public OntologyClassSubmission insert(OntologyClassSubmission ontologyClassSubmission) throws QueryException  {
		if(!ontologyClassSubmission.hasId()) {
			try(Query insert = new Query("INSERT INTO `ontologize_ontologyclasssubmission` "
					+ "(`collection`, `term`, `submission_term`, `ontology`, `class_iri`, `definition`, `source`, `sample_sentence`, "
					+ "`type`, `user`)"
					+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
				insert.setParameter(1, ontologyClassSubmission.getCollectionId());
				if(ontologyClassSubmission.getTerm() == null)
					insert.setParameterNull(2, java.sql.Types.BIGINT);
				else
					insert.setParameter(2, ontologyClassSubmission.getTerm().getId());
				insert.setParameter(3, ontologyClassSubmission.getSubmissionTerm());
				insert.setParameter(4, ontologyClassSubmission.getOntology().getId());
				insert.setParameter(5, ontologyClassSubmission.getClassIRI());
				insert.setParameter(6, ontologyClassSubmission.getDefinition());
				insert.setParameter(7, ontologyClassSubmission.getSource());
				insert.setParameter(8, ontologyClassSubmission.getSampleSentence());
				insert.setParameter(9, ontologyClassSubmission.getType().toString());
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
		try(Query query = new Query("UPDATE ontologize_ontologyclasssubmission SET collection = ?,"
				+ " term = ?, submission_term = ?,"
				+ " ontology = ?, class_iri = ?, definition = ?, source = ?, sample_sentence = ?, "
				+ "type = ?, user = ? WHERE id = ?")) {
			query.setParameter(1, ontologyClassSubmission.getCollectionId());
			if(ontologyClassSubmission.getTerm() == null)
				query.setParameterNull(2, java.sql.Types.BIGINT);
			else
				query.setParameter(2, ontologyClassSubmission.getTerm().getId());
			query.setParameter(3, ontologyClassSubmission.getSubmissionTerm());
			query.setParameter(4, ontologyClassSubmission.getOntology().getId());
			query.setParameter(5, ontologyClassSubmission.getClassIRI());
			query.setParameter(6, ontologyClassSubmission.getDefinition());
			query.setParameter(7, ontologyClassSubmission.getSource());
			query.setParameter(8, ontologyClassSubmission.getSampleSentence());
			query.setParameter(9, ontologyClassSubmission.getType().toString().toUpperCase());
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
		try(Query query = new Query("DELETE FROM ontologize_ontologyclasssubmission WHERE id = ?")) {
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
		return this.getByCollectionId(collection.getId());
	}
	
	public List<OntologyClassSubmission> getByCollectionId(int collectionId) throws QueryException {
		List<OntologyClassSubmission> result = new LinkedList<OntologyClassSubmission>();
		try(Query query = new Query("SELECT * FROM ontologize_ontologyclasssubmission WHERE collection = ?")) {
			query.setParameter(1, collectionId);
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
	
	public List<OntologyClassSubmission> get(Collection collection,	Ontology ontology) throws QueryException {
		List<OntologyClassSubmission> result = new LinkedList<OntologyClassSubmission>();
		try(Query query = new Query("SELECT * FROM ontologize_ontologyclasssubmission WHERE collection = ? AND ontology = ?")) {
			query.setParameter(1, collection.getId());
			query.setParameter(2, ontology.getId());
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
		try(Query query = new Query("SELECT * FROM ontologize_ontologyclasssubmission s, "
				+ "ontologize_ontologyclasssubmission_status ss, ontologize_status st"
				+ " WHERE s.collection = ? AND ss.ontologyclasssubmission = s.id AND ss.status = st.id AND"
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
