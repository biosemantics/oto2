package edu.arizona.biosemantics.oto2.ontologize.server.rpc;

import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.ontologize.server.Configuration;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.DAOManager;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.file.OntologyFileDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.file.PermanentOntologyFileDAO;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmissionStatus;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmissionStatus;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.StatusEnum;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.ClassExistsException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.CreateClassSubmissionException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.CreateOntologyException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.CreateSynonymSubmissionException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.OntologyBioportalException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.OntologyExistsException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.OntologyFileException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.OntologyNotFoundException;
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

	private DAOManager daoManager;

	@Inject
	public ToOntologyService(DAOManager daoManager) {
		this.daoManager = daoManager;
	}
	
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
	public List<OntologyClassSubmission> getClassSubmissions(Collection collection) throws Exception {
		
		
		//getClassLabel
		return daoManager.getOntologyClassSubmissionDAO().get(collection);
	}

	@Override
	public List<OntologyClassSubmission> getClassSubmissions(Collection collection, Ontology ontology) throws Exception {
		return daoManager.getOntologyClassSubmissionDAO().get(collection, ontology);
	}
	
	@Override
	public List<OntologyClassSubmission> getClassSubmissions(Collection collection, java.util.Collection<Ontology> ontologies) throws Exception {
		return daoManager.getOntologyClassSubmissionDAO().get(collection, ontologies);
	}

	@Override
	public List<OntologySynonymSubmission> getSynonymSubmissions(Collection collection) throws Exception {
		return daoManager.getOntologySynonymSubmissionDAO().get(collection);
	}
	
	@Override
	public Ontology createOntology(Collection collection, Ontology ontology, boolean createFile) throws Exception {
		ontology.setCreatedInCollectionId(collection.getId());
		ontology.setIri(Configuration.etcOntologyBaseIRI + collection.getId() + "/" + ontology.getAcronym());		
		ontology = daoManager.getOntologyDBDAO().insert(ontology);
		return ontology;
	}

	@Override
	public List<OntologyClassSubmission> createClassSubmission(Collection collection, OntologyClassSubmission submission) throws Exception {
		List<OntologyClassSubmission> submissions = new LinkedList<OntologyClassSubmission>();
		addIRIsToPlainTerms(collection, submission, submissions);
		submission = daoManager.getOntologyClassSubmissionDAO().insert(submission);
		this.setPending(submission);
		submissions.add(0, submission);
		return submissions;
	}

	private void addIRIsToPlainTerms(Collection collection, OntologyClassSubmission submission, List<OntologyClassSubmission> submissions) throws Exception {
		for(Superclass superclass : submission.getSuperclasses())
			if(!superclass.hasIri())
				superclass.setIri(getIRI(collection, submission, superclass.getLabel(), submissions));
		for(PartOf partOf : submission.getPartOfs())
			if(!partOf.hasIri())
				partOf.setIri(getIRI(collection, submission, partOf.getLabel(), submissions));
	}

	private String getIRI(Collection collection, OntologyClassSubmission submission, String value, List<OntologyClassSubmission> submissions) throws Exception {
		List<OntologyClassSubmission> valueSubmissions = daoManager.getOntologyClassSubmissionDAO().get(collection, value);
		if(valueSubmissions.isEmpty()) {
			List<Superclass> superclasses = new LinkedList<Superclass>();
			superclasses.add(new Superclass(submission.getType().getIRI()));
			Term usedTerm = null;
			for(Term term : collection.getTerms()) {
				if(term.getTerm().equals(value) || term.getOriginalTerm().equals(value)) {
					usedTerm = term;
					break;
				}
			}
			OntologyClassSubmission newSubmission = new OntologyClassSubmission(collection.getId(), usedTerm, value, submission.getOntology(), 
					"", superclasses, "", new LinkedList<Synonym>(), "", "", new LinkedList<PartOf>(), submission.getUser());
			//create? wouldnt know if there are other superclasses, but user could still edit later anyway?
			List<OntologyClassSubmission> newSubmissions = this.createClassSubmission(collection, newSubmission);
			submissions.add(newSubmissions.get(0));
			return newSubmissions.get(0).getClassIRI();
		} else {
			if(valueSubmissions.size() == 1) {
				return valueSubmissions.get(0).getClassIRI();
			}
		}
		throw new Exception("Term not unique");
	}

	@Override
	public OntologySynonymSubmission createSynonymSubmission(Collection collection, OntologySynonymSubmission submission) throws Exception {
		submission = daoManager.getOntologySynonymSubmissionDAO().insert(submission);
		this.setPending(submission);
		return submission;
	}
	
	/**
	 * There's still open questions about this: What to do with a bioportal submission that has been accepted.
	 * Should it still be possible to edit that submission? Would that be a resubmissoin then since the original
	 * submisison is already permanently accepted?
	 */
	@Override
	public List<OntologyClassSubmission> updateClassSubmission(Collection collection, OntologyClassSubmission submission) throws Exception {
		List<OntologyClassSubmission> submissions = new LinkedList<OntologyClassSubmission>();
		this.addIRIsToPlainTerms(collection, submission, submissions);
		daoManager.getOntologyClassSubmissionDAO().update(submission);
		return submissions;
	}

	@Override
	public void updateSynonymSubmission(Collection collection, OntologySynonymSubmission submission) throws Exception {
		daoManager.getOntologySynonymSubmissionDAO().update(submission);
	}

	@Override
	public void removeClassSubmission(Collection collection, OntologyClassSubmission submission) throws Exception {
		daoManager.getOntologyClassSubmissionDAO().remove(submission);
	}

	@Override
	public void removeSynonymSubmission(Collection collection, OntologySynonymSubmission submission) throws Exception {
		daoManager.getOntologySynonymSubmissionDAO().remove(submission);
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

	@Override
	public String getClassLabel(Collection collection, String iri) throws OntologyFileException, OntologyNotFoundException, QueryException {
		if(iri.startsWith(Configuration.etcOntologyBaseIRI)) {
			return daoManager.getOntologyClassSubmissionDAO().getFromIRI(collection, iri).getSubmissionTerm();
		} else {
			PermanentOntologyFileDAO ontologyFileDAO = daoManager.getPermanentOntologyFileDAO();
			return ontologyFileDAO.getClassLabel(iri);
		}
	}
	
	@Override
	public void storeLocalOntologiesToFile(Collection collection) throws Exception {
		for(Ontology ontology : daoManager.getOntologyDBDAO().getLocalOntologiesForCollection(collection)) {	
			daoManager.getOntologyFileDAO(collection).insertOntology(ontology, true);
		}
		List<OntologyClassSubmission> classSubmissions = daoManager.getOntologyClassSubmissionDAO().get(collection);
		for(OntologyClassSubmission classSubmission : classSubmissions) {
			if(!classSubmission.getOntology().isBioportalOntology()) {
				String classIRI = daoManager.getOntologyFileDAO(collection).insertClassSubmission(classSubmission);
			}
		}
		List<OntologySynonymSubmission> synonymSubmissions = daoManager.getOntologySynonymSubmissionDAO().get(collection);
		for(OntologySynonymSubmission synonymSubmission : synonymSubmissions) {
			if(!synonymSubmission.getOntology().isBioportalOntology()) {
				String classIRI = daoManager.getOntologyFileDAO(collection).insertSynonymSubmission(synonymSubmission);
			}
		}
	}
	
	@Override
	public void sendBioportalSubmissions(Collection collection) throws Exception {
		List<OntologyClassSubmission> classSubmissions = daoManager.getOntologyClassSubmissionDAO().get(collection);
		for(OntologyClassSubmission classSubmission : classSubmissions) {
			if(classSubmission.getOntology().isBioportalOntology()) {
				String classIRI = daoManager.getOntologyFileDAO(collection).insertClassSubmission(classSubmission);
			}
		}
		List<OntologySynonymSubmission> synonymSubmissions = daoManager.getOntologySynonymSubmissionDAO().get(collection);
		for(OntologySynonymSubmission synonymSubmission : synonymSubmissions) {
			if(synonymSubmission.getOntology().isBioportalOntology()) {
				String classIRI = daoManager.getOntologyFileDAO(collection).insertSynonymSubmission(synonymSubmission);
			}
		}
	}
}
