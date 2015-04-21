package edu.arizona.biosemantics.oto2.steps.server.rpc;

import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.oto2.steps.server.persist.DAOManager;
import edu.arizona.biosemantics.oto2.steps.server.persist.file.OntologyDAO;
import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.model.Context;
import edu.arizona.biosemantics.oto2.steps.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.steps.shared.model.Term;
import edu.arizona.biosemantics.oto2.steps.shared.model.TypedContext;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.IContextService;
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
		daoManager.getOntologyDBDAO().insert(ontology);
		daoManager.getOntologyFileDAO().insertOntology(ontology);
	}

	@Override
	public void submitClass(OntologyClassSubmission submission) throws OntologyFileException {
		daoManager.getOntologyClassSubmissionDAO().insert(submission);
		if(submission.getOntology().hasCollectionId()) {
			Collection collection = daoManager.getCollectionDAO().get(submission.getTerm().getCollectionId());
			daoManager.getOntologyFileDAO().insertClassSubmission(collection, submission);
		} else {
			//TODO: send to bioportal for these ontologies that are not local
		}
	}

	@Override
	public void submitSynonym(OntologySynonymSubmission submission) throws OntologyFileException {
		daoManager.getOntologySynonymSubmissionDAO().insert(submission);
		if(submission.getOntology().hasCollectionId()) {
			Collection collection = daoManager.getCollectionDAO().get(submission.getTerm().getCollectionId());
			daoManager.getOntologyFileDAO().insertSynonymSubmission(collection, submission);
		} else {
			//TODO: send to bioportal for these ontologies that are not local
		}
	}

}
