package edu.arizona.biosemantics.oto2.steps.server.rpc;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.oto2.steps.server.Configuration;
import edu.arizona.biosemantics.oto2.steps.server.persist.DAOManager;
import edu.arizona.biosemantics.oto2.steps.server.persist.file.OntologyDAO;
import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmissionStatus;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmissionStatus;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.StatusEnum;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology.ClassExistsException;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology.OntologyFileException;

public class ToOntologyService extends RemoteServiceServlet implements IToOntologyService {

	private DAOManager daoManager = new DAOManager();
	private OntologyDAO ontologyDAO = new OntologyDAO();
	
	public Collection insert(Collection collection) {
		return daoManager.getCollectionDAO().insert(collection);
	}

	@Override
	public List<Ontology> getOntologies(Collection collection) {
		return daoManager.getOntologyDBDAO().getOntologiesForCollection(collection);
	}
	
	@Override
	public List<OntologyClassSubmission> getClassSubmissions(Collection collection) {
		return daoManager.getOntologyClassSubmissionDAO().get(collection);
	}

	@Override
	public List<OntologySynonymSubmission> getSynonymSubmissions(Collection collection) {
		return daoManager.getOntologySynonymSubmissionDAO().get(collection);
	}
	
	@Override
	public void createOntology(Collection collection, Ontology ontology) throws OntologyFileException {
		ontology.setIri(Configuration.etcOntologyBaseIRI +collection.getId() + "/" + ontology.getAcronym());
		daoManager.getOntologyDBDAO().insert(ontology);
		daoManager.getOntologyFileDAO().insertOntology(ontology);
	}

	@Override
	public void submitClass(OntologyClassSubmission submission) throws OntologyFileException, ClassExistsException, InterruptedException, ExecutionException {
		submission = daoManager.getOntologyClassSubmissionDAO().insert(submission);
		Collection collection = daoManager.getCollectionDAO().get(submission.getTerm().getCollectionId());
		
		if(submission.getOntology().hasCollectionId()) {
			String classIRI = daoManager.getOntologyFileDAO().insertClassSubmission(collection, submission);
			setAccepted(submission, classIRI);
		} else {
			daoManager.getOntologyBioportalDAO().insertClassSubmission(collection, submission);
			setPending(submission);
		}
	}

	@Override
	public void submitSynonym(OntologySynonymSubmission submission) throws OntologyFileException, InterruptedException, ExecutionException {
		daoManager.getOntologySynonymSubmissionDAO().insert(submission);
		Collection collection = daoManager.getCollectionDAO().get(submission.getTerm().getCollectionId());
		if(submission.getOntology().hasCollectionId()) {
			daoManager.getOntologyFileDAO().insertSynonymSubmission(collection, submission);
			//setAccepted(submission, classIRI);
		} else {
			//TODO: send to bioportal for these ontologies that are not local
			daoManager.getOntologyBioportalDAO().insertSynonymSubmission(collection, submission);
			setPending(submission);
		}
	}
	
	/**
	 * There's still open questions about this: What to do with a bioportal submission that has been accepted.
	 * Should it still be possible to edit that submission? Would that be a resubmissoin then since the original
	 * submisison is already permanently accepted?
	 */
	@Override
	public void updatehOntologyClassSubmission(Collection collection, OntologyClassSubmission submission) {
		daoManager.getOntologyClassSubmissionDAO().update(submission);
		if(submission.getOntology().hasCollectionId()) {
			daoManager.getOntologyFileDAO().updateClassSubmission(collection, submission);
		} else {
			daoManager.getOntologyBioportalDAO().updateClassSubmission(submission);
		}
	}

	@Override
	public void updatehOntologySynonymSubmission(Collection collection, OntologySynonymSubmission submission) {
		daoManager.getOntologySynonymSubmissionDAO().update(submission);
		if(submission.getOntology().hasCollectionId()) {
			daoManager.getOntologyFileDAO().updateSynonymSubmission(collection, submission);
		} else {
			daoManager.getOntologyBioportalDAO().updateSynonymSubmission(submission);
		}
	}

	@Override
	public void removeOntologyClassSubmission(Collection collection, OntologyClassSubmission submission) {
		daoManager.getOntologyClassSubmissionDAO().remove(submission);
		if(submission.getOntology().hasCollectionId()) {
			daoManager.getOntologyFileDAO().removeClassSubmission(collection, submission);
		} else {
			daoManager.getOntologyBioportalDAO().removeClassSubmission(submission);
		}
	}

	@Override
	public void removeOntologySynonymSubmission(Collection collection, OntologySynonymSubmission submission) {
		daoManager.getOntologySynonymSubmissionDAO().remove(submission);
		if(submission.getOntology().hasCollectionId()) {
			daoManager.getOntologyFileDAO().removeSynonymSubmission(collection, submission);
		} else {
			daoManager.getOntologyBioportalDAO().removeSynonymSubmission(submission);
		}
	}
	
	@Override
	public void refreshOntologySubmissionStatuses(Collection collection) {
		daoManager.getOntologyBioportalDAO().refreshStatuses(collection);
	}

	
	private void setAccepted(OntologySubmission submission, String classIRI) throws OntologyFileException {
		this.setStatus(submission, classIRI, StatusEnum.ACCEPTED);
	}
	
	private void setPending(OntologySubmission submission) throws OntologyFileException {
		this.setStatus(submission, StatusEnum.PENDING);
	}
	
	private void setRejected(OntologySubmission submission) throws OntologyFileException {
		this.setStatus(submission, StatusEnum.REJECTED);
	}
	
	private void setStatus(OntologySubmission submission, String classIRI, StatusEnum status) {
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
	
	private void setStatus(OntologySubmission submission, StatusEnum status) {
		this.setStatus(submission, "", status);
	}

}
