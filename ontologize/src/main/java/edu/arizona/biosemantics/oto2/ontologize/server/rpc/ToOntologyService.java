package edu.arizona.biosemantics.oto2.ontologize.server.rpc;

import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.semanticweb.owlapi.model.OWLClass;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.ontologize.server.Configuration;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.DAOManager;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.file.OntologyFileDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.file.PermanentOntologyFileDAO;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmissionStatus;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmissionStatus;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.StatusEnum;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.SubmissionType;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;

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
		//make sure term is only used once
		if(daoManager.getOntologyClassSubmissionDAO().getByTerm(collection, submission.getSubmissionTerm()) != null) 
			throw new Exception("Term can only be submitted once as class.");
		
		if(!submission.getClassIRI().isEmpty()) {
			if(!this.isSupportedIRI(collection, submission.getClassIRI())) {
				throw new Exception("IRI not supported.");
			}
		}
		List<OntologyClassSubmission> submissions = new LinkedList<OntologyClassSubmission>();
		addIRIsToPlainTerms(collection, submission, submissions);
		submission = daoManager.getOntologyClassSubmissionDAO().insert(submission);
		
		if(submission.getOntology().isBioportalOntology())
		this.setNew(submission);
		submissions.add(submission);
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
		throw new Exception("Term not unique.");
	}

	@Override
	public OntologySynonymSubmission createSynonymSubmission(Collection collection, OntologySynonymSubmission submission) throws Exception {
		submission = daoManager.getOntologySynonymSubmissionDAO().insert(submission);
		this.setNew(submission);
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
	public OntologySynonymSubmission updateSynonymSubmission(Collection collection, OntologySynonymSubmission submission) throws Exception {
		daoManager.getOntologySynonymSubmissionDAO().update(submission);
		return submission;
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
	
	private void setNew(OntologySubmission submission) throws QueryException {
		this.setStatus(submission, StatusEnum.NEW);
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
	public String getClassLabel(Collection collection, String iri) throws Exception {
		iri = iri.trim();
		if(iri.equals(Type.ENTITY.getIRI()))
			return Type.ENTITY.getLabel();
		if(iri.equals(Type.QUALITY.getIRI()))
			return Type.QUALITY.getLabel();
		if(iri.startsWith(Configuration.etcOntologyBaseIRI)) {
			return daoManager.getOntologyClassSubmissionDAO().getByClassIRI(collection, iri).getSubmissionTerm();
		} else {
			PermanentOntologyFileDAO ontologyFileDAO = daoManager.getPermanentOntologyFileDAO();
			return ontologyFileDAO.getClassLabel(iri);
		}
	}
	
	@Override
	public void storeLocalOntologiesToFile(Collection collection) throws Exception {
		OntologyFileDAO ontologyFileDAO = daoManager.getOntologyFileDAO(collection);
		for(Ontology ontology : daoManager.getOntologyDBDAO().getLocalOntologiesForCollection(collection)) {	
			ontologyFileDAO.insertOntology(ontology, true);
		}
		List<OntologyClassSubmission> classSubmissions = daoManager.getOntologyClassSubmissionDAO().get(collection);
		for(OntologyClassSubmission classSubmission : classSubmissions) {
			if(!classSubmission.getOntology().isBioportalOntology()) {
				String classIRI = ontologyFileDAO.insertClassSubmission(classSubmission);
			}
		}
		List<OntologySynonymSubmission> synonymSubmissions = daoManager.getOntologySynonymSubmissionDAO().get(collection);
		for(OntologySynonymSubmission synonymSubmission : synonymSubmissions) {
			if(!synonymSubmission.getOntology().isBioportalOntology()) {
				String classIRI = ontologyFileDAO.insertSynonymSubmission(synonymSubmission);
			}
		}
	}
	
	@Override
	public void sendBioportalSubmissions(Collection collection) throws Exception {
		List<OntologyClassSubmission> classSubmissions = daoManager.getOntologyClassSubmissionDAO().get(collection);
		for(OntologyClassSubmission classSubmission : classSubmissions) {
			if(classSubmission.getOntology().isBioportalOntology()) {
				String classIRI = daoManager.getOntologyBioportalDAO().insertClassSubmission(collection, classSubmission);
			}
		}
		List<OntologySynonymSubmission> synonymSubmissions = daoManager.getOntologySynonymSubmissionDAO().get(collection);
		for(OntologySynonymSubmission synonymSubmission : synonymSubmissions) {
			if(synonymSubmission.getOntology().isBioportalOntology()) {
				String classIRI = daoManager.getOntologyBioportalDAO().insertSynonymSubmission(collection, synonymSubmission);
			}
		}
	}

	@Override
	public boolean isSupportedIRI(Collection collection, String iri) throws Exception {
		if(daoManager.getOntologyClassSubmissionDAO().getByClassIRI(collection, iri) != null)
			return true;
		else
			return daoManager.getPermanentOntologyFileDAO().contains(collection, iri);
	}
	
	@Override
	public PagingLoadResult<OntologyClassSubmission> getClassSubmissions(Collection collection, FilterPagingLoadConfig loadConfig, SubmissionType submissionType) throws Exception {
		return daoManager.getOntologyClassSubmissionDAO().get(collection, loadConfig, submissionType);
	}
}
