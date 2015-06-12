package edu.arizona.biosemantics.oto2.ontologize.server.rpc;

import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.ontologize.server.Configuration;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.DAOManager;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmissionStatus;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmissionStatus;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.StatusEnum;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.ClassExistsException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.CreateClassSubmissionException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.CreateOntologyException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.CreateSynonymSubmissionException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.OntologyBioportalException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.OntologyExistsException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.OntologyFileException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.RemoveClassSubmissionException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.RemoveSynonymSubmissionException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.UpdateClassSubmissionException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.UpdateSynonymSubmissionException;

/**
 * Note: Not trivial to guarantee consistency between all three data stores (DB, OWL File, Bioportal), e.g. could end up in a situation where 
 * where DB throws exception, want to revert bioportal changes, bioportal fails aswell.
 * Thus, only a best effort.
 * @author rodenhausen
 *
 */
public class ToOntologyService extends RemoteServiceServlet implements IToOntologyService {

	private DAOManager daoManager = new DAOManager();
	
	@Override
	public List<Ontology> getOntologies(Collection collection) throws Exception {
		return daoManager.getOntologyDBDAO().getRelevantOntologiesForCollection(collection);
	}
	
	@Override
	public List<Ontology> getPermanentOntologies(Collection collection) throws Exception {
		return daoManager.getOntologyDBDAO().getBioportalOntologiesForCollection(collection);
	}
	
	@Override
	public List<Ontology> getLocalOntologies(Collection collection) throws Exception {
		return daoManager.getOntologyDBDAO().getLocalOntologiesForCollection(collection);
	}
	
	@Override
	public List<OntologyClassSubmission> getClassSubmissions(Collection collection, boolean includeLinkedCollections) throws Exception {
		List<OntologyClassSubmission> result = daoManager.getOntologyClassSubmissionDAO().get(collection);
		if(includeLinkedCollections) {
			for(Collection linkedCollection : getLinkedCollectionsRecursively(collection)) {
				result.addAll(daoManager.getOntologyClassSubmissionDAO().get(linkedCollection));
			}
		}
		return result;
	}
	
	private List<Collection> getLinkedCollectionsRecursively(Collection collection) {
		List<Collection> result = new LinkedList<Collection>();
		for(Collection linkedCollection : collection.getLinkedCollections()) {
			result.add(linkedCollection);
			result.addAll(getLinkedCollectionsRecursively(linkedCollection));
		}
		return result;
	}

	@Override
	public List<OntologyClassSubmission> getClassSubmissions(Collection collection, Ontology ontology, boolean includeLinkedCollections) throws Exception {
		List<OntologyClassSubmission> result =  daoManager.getOntologyClassSubmissionDAO().get(collection, ontology);
		if(includeLinkedCollections) {
			for(Collection linkedCollection : getLinkedCollectionsRecursively(collection)) {
				result.addAll(daoManager.getOntologyClassSubmissionDAO().get(linkedCollection, ontology));
			}
		}
		return result;
	}
	
	@Override
	public List<OntologyClassSubmission> getClassSubmissions(Collection collection, java.util.Collection<Ontology> ontologies, boolean includeLinkedCollections) throws Exception {
		List<OntologyClassSubmission> result =  daoManager.getOntologyClassSubmissionDAO().get(collection, ontologies);
		if(includeLinkedCollections) {
			for(Collection linkedCollection : getLinkedCollectionsRecursively(collection)) {
				result.addAll(daoManager.getOntologyClassSubmissionDAO().get(linkedCollection, ontologies));
			}
		}
		return result;
	}

	@Override
	public List<OntologySynonymSubmission> getSynonymSubmissions(Collection collection) throws Exception {
		return daoManager.getOntologySynonymSubmissionDAO().get(collection);
	}
	
	@Override
	public Ontology createOntology(Collection collection, Ontology ontology) throws CreateOntologyException {
		ontology.setIri(Configuration.etcOntologyBaseIRI +collection.getId() + "/" + ontology.getAcronym());
		try {
			daoManager.getOntologyFileDAO(collection).insertOntology(ontology);
		} catch (OntologyFileException e) {
			try {
				daoManager.getOntologyFileDAO(collection).removeOntology(ontology);
			} catch (OntologyFileException e1) {
				log(LogLevel.ERROR, "Couldn't remove ontology from file where creation of ontology overall failed!", e);
			}
			throw new CreateOntologyException(e);
		}
		
		try {
			ontology = daoManager.getOntologyDBDAO().insert(ontology);
		} catch (QueryException e) {
			try {
				daoManager.getOntologyDBDAO().remove(ontology);
			} catch (QueryException re) {
				log(LogLevel.ERROR, "Couldn't remove ontology from DB where creation of ontology overall failed!", e);
			}
			try {
				daoManager.getOntologyFileDAO(collection).removeOntology(ontology);
			} catch (OntologyFileException e1) {
				log(LogLevel.ERROR, "Couldn't remove ontology from file where creation of ontology overall failed!", e);
			}
			throw new CreateOntologyException(e);
		}
		return ontology;
	}

	@Override
	public OntologyClassSubmission createClassSubmission(Collection collection, OntologyClassSubmission submission) throws CreateClassSubmissionException, OntologyBioportalException, 
			OntologyFileException, ClassExistsException {
		try {
			collection = daoManager.getCollectionDAO().get(collection.getId());
		} catch (QueryException | IOException e) {
			throw new CreateClassSubmissionException(e);
		}
		
		String classIRI = null;
		if(!submission.getOntology().isBioportalOntology()) {
			classIRI = daoManager.getOntologyFileDAO(collection).insertClassSubmission(submission);
		} else {
			try {
				daoManager.getOntologyBioportalDAO().insertClassSubmission(collection, submission);
			} catch (InterruptedException | ExecutionException e) {
				try {
					daoManager.getOntologyBioportalDAO().removeClassSubmission(submission);
				} catch (InterruptedException | ExecutionException e1) {
					log(LogLevel.ERROR, "Couldn't remove class submission from bioportal where creation of submission at bioportal failed!", e1);
				}
				log(LogLevel.ERROR, "Couldn't insert at bioportal", e);
				throw new OntologyBioportalException(e);
			}
		} 
		try {
			submission = daoManager.getOntologyClassSubmissionDAO().insert(submission);
			if(classIRI != null)	
				setAccepted(submission, classIRI);
			else 
				setPending(submission);
		} catch (QueryException e) {
			if(!submission.getOntology().isBioportalOntology()) {
				daoManager.getOntologyFileDAO(collection).depcrecateClassSubmission(submission);
			} else {
				try {
					daoManager.getOntologyBioportalDAO().removeClassSubmission(submission);
				} catch (InterruptedException | ExecutionException e1) {
					log(LogLevel.ERROR, "Couldn't remove class submission from bioportal where creation of submission in db failed!", e1);
				}
			}
			throw new CreateClassSubmissionException(e);
		}
		
		return submission;
	}

	@Override
	public OntologySynonymSubmission createSynonymSubmission(Collection collection, OntologySynonymSubmission submission) throws OntologyFileException, OntologyBioportalException, CreateSynonymSubmissionException {
		try {
			collection = daoManager.getCollectionDAO().get(collection.getId());
		} catch (QueryException | IOException e) {
			throw new CreateSynonymSubmissionException(e);
		}
		String classIRI = null;
		if(!submission.getOntology().isBioportalOntology()) {
			classIRI = daoManager.getOntologyFileDAO(collection).insertSynonymSubmission(submission);
		} else {
			try {
				daoManager.getOntologyBioportalDAO().insertSynonymSubmission(collection, submission);
			} catch (InterruptedException | ExecutionException e) {
				try {
					daoManager.getOntologyBioportalDAO().removeSynonymSubmission(submission);
				} catch (InterruptedException | ExecutionException e1) {
					log(LogLevel.ERROR, "Couldn't remove synonym submission from bioportal where creation of submission at bioportal failed!", e1);
				}
				log(LogLevel.ERROR, "Couldn't insert at bioportal", e);
				throw new OntologyBioportalException(e);
			}
		}
		try {
			daoManager.getOntologySynonymSubmissionDAO().insert(submission);
			if(classIRI != null)
				setAccepted(submission, classIRI);
			else 
				setPending(submission);
		} catch (QueryException e) {
			if(!submission.getOntology().isBioportalOntology()) {
				daoManager.getOntologyFileDAO(collection).removeSynonymSubmission(submission);
			} else {
				try {
					daoManager.getOntologyBioportalDAO().removeSynonymSubmission(submission);
				} catch (InterruptedException | ExecutionException e1) {
					log(LogLevel.ERROR, "Couldn't remove synonym submission from bioportal where creation of submission in db failed!", e1);
				}
			}
			throw new CreateSynonymSubmissionException(e);
		}		
		return submission;
	}
	
	/**
	 * There's still open questions about this: What to do with a bioportal submission that has been accepted.
	 * Should it still be possible to edit that submission? Would that be a resubmissoin then since the original
	 * submisison is already permanently accepted?
	 */
	@Override
	public void updateClassSubmissions(Collection collection, 
			java.util.Collection<OntologyClassSubmission> submissions) throws OntologyBioportalException, UpdateClassSubmissionException {
		for(OntologyClassSubmission submission : submissions) {
			if(!submission.getOntology().isBioportalOntology()) {
				try {
					daoManager.getOntologyFileDAO(collection).updateClassSubmission(submission);
				} catch (OntologyFileException e) {
					log(LogLevel.ERROR, "Couldn't update ontology file", e);
					throw new UpdateClassSubmissionException(e);
				}
			} else {
				try {
					daoManager.getOntologyBioportalDAO().updateClassSubmission(collection, submission);
				} catch (InterruptedException | ExecutionException e) {
					log(LogLevel.ERROR, "Couldn't update at bioportal", e);
					throw new OntologyBioportalException(e);
				}
			}
			try {
				daoManager.getOntologyClassSubmissionDAO().update(submission);
			} catch (QueryException e) {
				throw new UpdateClassSubmissionException(e);
			}
		}
	}

	@Override
	public void updateSynonymSubmissions(Collection collection, 
			java.util.Collection<OntologySynonymSubmission> submissions) throws OntologyBioportalException, UpdateSynonymSubmissionException {
		for(OntologySynonymSubmission submission : submissions) { 
			if(!submission.getOntology().isBioportalOntology()) {
				try {
					daoManager.getOntologyFileDAO(collection).updateSynonymSubmission(submission);
				} catch (OntologyFileException e) {
					log(LogLevel.ERROR, "Couldn't update ontology file", e);
					throw new UpdateSynonymSubmissionException(e);
				}
			} else {
				try {
					daoManager.getOntologyBioportalDAO().updateSynonymSubmission(collection, submission);
				} catch (InterruptedException | ExecutionException e) {
					log(LogLevel.ERROR, "Couldn't update at bioportal", e);
					throw new OntologyBioportalException(e);
				}
			}
			try {
				daoManager.getOntologySynonymSubmissionDAO().update(submission);
			} catch (QueryException e) {
				throw new UpdateSynonymSubmissionException(e);
			}
		}
	}

	@Override
	public void removeClassSubmissions(Collection collection, 
			java.util.Collection<OntologyClassSubmission> submissions) throws OntologyBioportalException, RemoveClassSubmissionException {
		for(OntologyClassSubmission submission : submissions) {
			if(!submission.getOntology().isBioportalOntology()) {
				try {
					daoManager.getOntologyFileDAO(collection).depcrecateClassSubmission(submission);
				} catch (OntologyFileException e) {
					log(LogLevel.ERROR, "Couldn't remove at file", e);
					throw new RemoveClassSubmissionException(e);
				}
			} else {
				try {
					daoManager.getOntologyBioportalDAO().removeClassSubmission(submission);
				} catch (InterruptedException | ExecutionException e) {
					log(LogLevel.ERROR, "Couldn't remove at bioportal", e);
					throw new OntologyBioportalException(e);
				}
			}
			try {
				daoManager.getOntologyClassSubmissionDAO().remove(submission);
			} catch (QueryException e) {
				throw new RemoveClassSubmissionException(e);
			}
		}
	}

	@Override
	public void removeSynonymSubmissions(Collection collection, 
			java.util.Collection<OntologySynonymSubmission> submissions) throws OntologyBioportalException, RemoveSynonymSubmissionException {
		for(OntologySynonymSubmission submission : submissions) {
			if(!submission.getOntology().isBioportalOntology()) {
				try {
					daoManager.getOntologyFileDAO(collection).removeSynonymSubmission(submission);
				} catch (OntologyFileException e) {
					log(LogLevel.ERROR, "Couldn't update ontology file", e);
					throw new RemoveSynonymSubmissionException(e);
				}
			} else {
				try {
					daoManager.getOntologyBioportalDAO().removeSynonymSubmission(submission);
				} catch (InterruptedException | ExecutionException e) {
					log(LogLevel.ERROR, "Couldn't remove at bioportal", e);
					throw new OntologyBioportalException(e);
				}
			}
			try {
				daoManager.getOntologySynonymSubmissionDAO().remove(submission);
			} catch (QueryException e) {
				throw new RemoveSynonymSubmissionException(e);
			}
		}
	}
	
	@Override
	public void refreshSubmissionStatuses(Collection collection) throws Exception {
		daoManager.getOntologyBioportalDAO().refreshStatuses(collection);
	}

	
	private void setAccepted(OntologySubmission submission, String classIRI) throws QueryException {
		this.setStatus(submission, classIRI, StatusEnum.ACCEPTED);
	}
	
	private void setPending(OntologySubmission submission) throws QueryException {
		this.setStatus(submission, StatusEnum.PENDING);
	}
	
	private void setRejected(OntologySubmission submission) throws QueryException {
		this.setStatus(submission, StatusEnum.REJECTED);
	}
	
	private void setStatus(OntologySubmission submission, String classIRI, StatusEnum status) throws QueryException {
		if(submission instanceof OntologyClassSubmission) {
			OntologyClassSubmission ontologyClassSubmission = (OntologyClassSubmission) submission;
			OntologyClassSubmissionStatus ontologyClassSubmissionStatus = new OntologyClassSubmissionStatus(ontologyClassSubmission.getId(), 
					daoManager.getStatusDAO().get(status.getDisplayName()), classIRI);
			daoManager.getOntologyClassSubmissionStatusDAO().insert(ontologyClassSubmissionStatus);
		}
		if(submission instanceof OntologySynonymSubmission) {
			OntologySynonymSubmission ontologySynonymSubmission = (OntologySynonymSubmission) submission;
			OntologySynonymSubmissionStatus ontologyClassSubmissionStatus = new OntologySynonymSubmissionStatus(ontologySynonymSubmission.getId(), 
					daoManager.getStatusDAO().get(status.getDisplayName()), classIRI);
			daoManager.getOntologySynonymSubmissionStatusDAO().insert(ontologyClassSubmissionStatus);
		}
	}
	
	private void setStatus(OntologySubmission submission, StatusEnum status) throws QueryException {
		this.setStatus(submission, "", status);
	}
}
