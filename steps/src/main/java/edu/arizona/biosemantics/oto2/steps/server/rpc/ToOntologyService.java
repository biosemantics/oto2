package edu.arizona.biosemantics.oto2.steps.server.rpc;

import java.util.List;

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
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.Status;
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
	public void submitClass(OntologyClassSubmission submission) throws OntologyFileException, ClassExistsException {
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
	public void submitSynonym(OntologySynonymSubmission submission) throws OntologyFileException {
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

	
	private void setAccepted(OntologySubmission submission, String classIRI) throws OntologyFileException {
		this.setStatus(submission, classIRI, Status.ACCEPTED);
	}
	
	private void setPending(OntologySubmission submission) throws OntologyFileException {
		this.setStatus(submission, Status.PENDING);
	}
	
	private void setRejected(OntologySubmission submission) throws OntologyFileException {
		this.setStatus(submission, Status.REJECTED);
	}
	
	private void setStatus(OntologySubmission submission, String classIRI, Status status) {
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
	
	private void setStatus(OntologySubmission submission, Status status) {
		this.setStatus(submission, "", status);
	}
}
