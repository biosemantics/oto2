package edu.arizona.biosemantics.oto2.ontologize.server.persist.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.semanticweb.owlapi.model.IRI;

import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.ontologize.server.Configuration;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.ontologize.shared.log.TypeFromSuperclasses;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.ClassSubmissionsPagingLoadResult;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmissionStatus;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.StatusEnum;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.SubmissionType;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;

public class OntologyClassSubmissionDAO {

	private TermDAO termDAO;
	private OntologyDAO ontologyDAO;
	private OntologyClassSubmissionStatusDAO ontologyClassSubmissionStatusDAO;
	private OntologyClassSubmissionSynonymDAO ontologyClassSubmissionSynonymDAO;
	private OntologyClassSubmissionSuperclassDAO ontologyClassSubmissionSuperclassDAO;
	private OntologyClassSubmissionPartOfDAO ontologyClassSubmissionPartOfDAO;
	private OntologySynonymSubmissionDAO ontologySynonymSubmissionDAO;
	
	public OntologyClassSubmissionDAO() {} 
	
	public OntologyClassSubmission get(int id) throws Exception  {
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
	
	private OntologyClassSubmission createClassSubmission(ResultSet result) throws Exception {
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
				ontologyClassSubmissionPartOfDAO.getPartOfs(id), user, ontologyClassSubmissionStatuses);
	}

	public OntologyClassSubmission insert(OntologyClassSubmission ontologyClassSubmission) throws Exception  {
		if(ontologyClassSubmission.hasId()) 
			this.remove(ontologyClassSubmission);
		try(Query submissionIdInCollectionQuery = new Query("SELECT `id` FROM `ontologize_ontologyclasssubmission` ORDER BY id DESC LIMIT 1")) {
			submissionIdInCollectionQuery.execute();
			ResultSet resultSet = submissionIdInCollectionQuery.getResultSet();
			boolean hasRecord = resultSet.next();
			int submissionIdInCollection = 0;
			if(hasRecord) 
				submissionIdInCollection = resultSet.getInt(1) + 1;
			ontologyClassSubmission.setClassIRI(createClassIRI(ontologyClassSubmission, submissionIdInCollection));
			try(Query insert = new Query("INSERT INTO `ontologize_ontologyclasssubmission` "
					+ "(`collection`, `term`, `submission_term`, `ontology`, `class_iri`, `definition`, `source`, `sample_sentence`, "
					+ "`user`)"
					+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
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
				insert.setParameter(9, ontologyClassSubmission.getUser());
				insert.execute();
				ResultSet generatedKeys = insert.getGeneratedKeys();
				generatedKeys.next();
				int id = generatedKeys.getInt(1);
				
				ontologyClassSubmission.setId(id);
				
				for(Synonym synonym : ontologyClassSubmission.getSynonyms())
					synonym.setSubmission(id);
				for(Superclass superclass : ontologyClassSubmission.getSuperclasses())
					superclass.setOntologyClassSubmission(id);
				for(PartOf partOf : ontologyClassSubmission.getPartOfs())
					partOf.setOntologyClassSubmission(id);
				
				ontologyClassSubmissionSynonymDAO.insert(ontologyClassSubmission.getSynonyms());
				ontologyClassSubmissionSuperclassDAO.insert(ontologyClassSubmission.getSuperclasses());
				ontologyClassSubmissionPartOfDAO.insert(ontologyClassSubmission.getPartOfs());
			} catch(QueryException | SQLException e) {
				log(LogLevel.ERROR, "Query Exception", e);
				throw new QueryException(e);
			}
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
		}
		return ontologyClassSubmission;
	}
	
	private String createClassIRI(OntologyClassSubmission ontologyClassSubmission, int submissionIdInCollection) {
		if(ontologyClassSubmission.hasClassIRI())
			return ontologyClassSubmission.getClassIRI();
		if(!ontologyClassSubmission.getOntology().isBioportalOntology()) {
			return Configuration.etcOntologyBaseIRI + ontologyClassSubmission.getCollectionId() + "/" +  
					ontologyClassSubmission.getOntology().getAcronym() + "#" + submissionIdInCollection;
		} else {
			return Configuration.etcOntologyBaseIRI + ontologyClassSubmission.getCollectionId() + "/" + 
					ontologyClassSubmission.getOntology().getAcronym() + "#" + submissionIdInCollection;
		}
	}

	public void update(OntologyClassSubmission ontologyClassSubmission) throws QueryException  {		
		try(Query query = new Query("UPDATE ontologize_ontologyclasssubmission SET collection = ?,"
				+ " term = ?, submission_term = ?,"
				+ " ontology = ?, class_iri = ?, definition = ?, source = ?, sample_sentence = ?, "
				+ "user = ? WHERE id = ?")) {
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
			query.setParameter(9, ontologyClassSubmission.getUser());
			query.setParameter(10, ontologyClassSubmission.getId());
			query.execute();
			
			ontologyClassSubmissionStatusDAO.update(ontologyClassSubmission.getSubmissionStatuses());
			
			for(Synonym synonym : ontologyClassSubmission.getSynonyms())
				synonym.setSubmission(ontologyClassSubmission.getId());
			for(Superclass superclass : ontologyClassSubmission.getSuperclasses())
				superclass.setOntologyClassSubmission(ontologyClassSubmission.getId());
			for(PartOf partOf : ontologyClassSubmission.getPartOfs())
				partOf.setOntologyClassSubmission(ontologyClassSubmission.getId());
			ontologyClassSubmissionSynonymDAO.update(ontologyClassSubmission.getId(), ontologyClassSubmission.getSynonyms());			
			ontologyClassSubmissionSuperclassDAO.update(ontologyClassSubmission.getId(), ontologyClassSubmission.getSuperclasses());
			
			
			Type type = TypeFromSuperclasses.getType(ontologyClassSubmission.getSuperclasses());
			if(type != null && type.equals(Type.QUALITY))
				ontologyClassSubmissionPartOfDAO.remove(ontologyClassSubmission.getPartOfs());
			else
				ontologyClassSubmissionPartOfDAO.update(ontologyClassSubmission.getId(), ontologyClassSubmission.getPartOfs());
		} catch(QueryException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw e;
		}
	}
	
	public void remove(OntologyClassSubmission ontologyClassSubmission) throws Exception  {
		try(Query query = new Query("DELETE FROM ontologize_ontologyclasssubmission WHERE id = ?")) {
			query.setParameter(1, ontologyClassSubmission.getId());
			query.execute();
			
			ontologyClassSubmissionStatusDAO.remove(ontologyClassSubmission.getSubmissionStatuses());
			ontologyClassSubmissionSynonymDAO.remove(ontologyClassSubmission.getId());
			ontologyClassSubmissionSuperclassDAO.remove(ontologyClassSubmission);
			ontologyClassSubmissionPartOfDAO.remove(ontologyClassSubmission);
			ontologySynonymSubmissionDAO.remove(ontologyClassSubmission);
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
	
	public void setOntologySynonymSubmissionDAO(
			OntologySynonymSubmissionDAO ontologySynonymSubmissionDAO) {
		this.ontologySynonymSubmissionDAO = ontologySynonymSubmissionDAO;
	}

	public List<OntologyClassSubmission> get(Collection collection) throws Exception {
		return this.getByCollectionId(collection.getId());
	}
	
	public List<OntologyClassSubmission> getByCollectionId(int collectionId) throws Exception {
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
	
	public List<OntologyClassSubmission> get(Collection collection,	Ontology ontology) throws Exception {
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
	
	public List<OntologyClassSubmission> get(Collection collection,	java.util.Collection<Ontology> ontologies) throws Exception {
		List<OntologyClassSubmission> result = new LinkedList<OntologyClassSubmission>();
		for(Ontology ontology : ontologies) {
			result.addAll(get(collection, ontology));
		}
		return result;
	}
	
	public List<OntologyClassSubmission> get(Collection collection, StatusEnum status) throws Exception {
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

	public List<OntologyClassSubmission> get(Collection collection, StatusEnum status, String term) throws Exception {
		List<OntologyClassSubmission> result = new LinkedList<OntologyClassSubmission>();
		try(Query query = new Query("SELECT * FROM ontologize_ontologyclasssubmission s, "
				+ "ontologize_ontologyclasssubmission_status ss, ontologize_status st, ontologize_ontologyclasssubmission_synonym ssy"
				+ " WHERE "
				+ "s.collection = ? AND ss.ontologyclasssubmission = s.id AND ss.status = st.id AND st.name = ? AND ssy.ontologyclasssubmission = s.id"
				+ " AND (s.submission_term = ? OR ssy.synonym = ?)")) {
			query.setParameter(1, collection.getId());
			query.setParameter(2, status.getDisplayName());
			query.setParameter(3, term);
			query.setParameter(4, term);
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

	public List<OntologyClassSubmission> get(Collection collection, String term) throws Exception {
		List<OntologyClassSubmission> result = new LinkedList<OntologyClassSubmission>();
		try(Query query = new Query("SELECT * FROM ontologize_ontologyclasssubmission s, "
				+ "ontologize_ontologyclasssubmission_synonym ssy"
				+ " WHERE "
				+ "s.collection = ? AND ssy.ontologyclasssubmission = s.id"
				+ " AND (s.submission_term = ? OR ssy.synonym = ?)")) {
			query.setParameter(1, collection.getId());
			query.setParameter(2, term);
			query.setParameter(3, term);
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
	
	public OntologyClassSubmission getByClassIRI(Collection collection, String iri) throws Exception {
		try(Query query = new Query("SELECT * FROM ontologize_ontologyclasssubmission s"
				+ " WHERE "
				+ "s.collection = ? AND s.class_iri = ?")) {
			query.setParameter(1, collection.getId());
			query.setParameter(2, iri);
			ResultSet resultSet = query.execute();
			if(resultSet.next()) {
				return createClassSubmission(resultSet);
			}
			return null;
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
	}

	public OntologyClassSubmission getByTerm(Collection collection, String submissionTerm) throws Exception {
		try(Query query = new Query("SELECT * FROM ontologize_ontologyclasssubmission s"
				+ " WHERE "
				+ "s.collection = ? AND s.submission_term = ?")) {
			query.setParameter(1, collection.getId());
			query.setParameter(2, submissionTerm);
			ResultSet resultSet = query.execute();
			if(resultSet.next()) {
				return createClassSubmission(resultSet);
			}
			return null;
		} catch(QueryException | SQLException e) {
			log(LogLevel.ERROR, "Query Exception", e);
			throw new QueryException(e);
		}
	}

	public PagingLoadResult<OntologyClassSubmission> get(Collection collection, FilterPagingLoadConfig loadConfig, SubmissionType submissionType) throws Exception {
		int count = 0;
		List<OntologyClassSubmission> data = new LinkedList<OntologyClassSubmission>();
		
		if(collection != null) {
			String filterSQL = "";
			for(FilterConfig filter : loadConfig.getFilters()) {
				if(filter.getComparison().equals("contains") && filter.getType().equals("string")) {
					String dbField = getDBField(filter.getField());
					if(dbField != null)
						filterSQL += " AND " + dbField + " LIKE '%" + filter.getValue() + "%'";
				}
			}
			String sortSQL = "";
			for(SortInfo sortInfo : loadConfig.getSortInfo()) {
				String dbField = getDBField(sortInfo.getSortField());
				if(dbField != null)
					sortSQL += " ORDER BY " + dbField + " " + sortInfo.getSortDir().toString();
			}
			
			try(Query query = new Query("SELECT * FROM ontologize_ontologyclasssubmission s, ontologize_ontology o WHERE s.collection = ? "
					+ " AND s.ontology = o.id AND o.bioportal_ontology = ? " + filterSQL + " " + sortSQL + " LIMIT ? OFFSET ?")) {
				query.setParameter(1, collection.getId());
				query.setParameter(2, submissionType == SubmissionType.BIOPORTAL ? 1 : 0);
				query.setParameter(3, loadConfig.getLimit());
				query.setParameter(4, loadConfig.getOffset());
				ResultSet resultSet = query.execute();
				while(resultSet.next()) {
					data.add(createClassSubmission(resultSet));
				}
			} catch(QueryException | SQLException e) {
				e.printStackTrace();
				log(LogLevel.ERROR, "Query Exception", e);
				throw new QueryException(e);
			}
			
			try(Query query = new Query("SELECT COUNT(s.id) FROM ontologize_ontologyclasssubmission s, ontologize_ontology o WHERE s.collection = ? "
					+ " AND s.ontology = o.id AND o.bioportal_ontology = ? " + filterSQL)) {
				query.setParameter(1, collection.getId());
				query.setParameter(2, submissionType == SubmissionType.BIOPORTAL ? 1 : 0);
				ResultSet resultSet = query.execute();
				while(resultSet.next()) {
					count = resultSet.getInt(1);
				}
			}
		}
		
		ClassSubmissionsPagingLoadResult result = new ClassSubmissionsPagingLoadResult(data, count, loadConfig.getOffset());
		return result;
	}

	private String getDBField(String field) {      
	    switch(field) {
	    case "id":
	    	return "id";
	    case "term":
	    	return "term";
	    case "collection":
	    	return "collection";
    	case "submissionTerm":
	    	return "submission_term";
    	case "ontology":
    		return "ontology";
    	case "classIri":
    		return "class_iri";
    	case "definition":
    		return "definition";
    	case "source":
    		return "source";
    	case "sampleSentence":
    		return "sample_sentence";
    	case "user":
    		return "user";
	    default:
	    	return null;
	    }
	}


}
