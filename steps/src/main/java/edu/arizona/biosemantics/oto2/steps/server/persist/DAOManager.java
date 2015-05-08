package edu.arizona.biosemantics.oto2.steps.server.persist;

import edu.arizona.biosemantics.oto2.steps.server.persist.bioportal.OntologyBioportalDAO;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.CollectionDAO;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.ContextDAO;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.OntologyClassSubmissionDAO;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.OntologyClassSubmissionPartOfDAO;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.OntologyClassSubmissionStatusDAO;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.OntologyClassSubmissionSuperclassDAO;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.OntologyClassSubmissionSynonymDAO;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.OntologyDAO;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.OntologySynonymSubmissionDAO;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.OntologySynonymSubmissionStatusDAO;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.OntologySynonymSubmissionSynonymDAO;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.StatusDAO;
import edu.arizona.biosemantics.oto2.steps.server.persist.db.TermDAO;
import edu.arizona.biosemantics.oto2.steps.server.persist.file.OntologyDAO2;
import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology.OntologyFileException;

public class DAOManager {

	private CollectionDAO collectionDAO;
	private TermDAO termDAO;
	private OntologyDAO ontologyDBDAO;
	private OntologyClassSubmissionDAO ontologyClassSubmissionDAO;
	private OntologySynonymSubmissionDAO ontologySynonymSubmissionDAO;
	private OntologyClassSubmissionStatusDAO ontologyClassSubmissionStatusDAO;
	private OntologySynonymSubmissionStatusDAO ontologySynonymSubmissionStatusDAO;
	private ContextDAO contextDAO;
	private StatusDAO statusDAO;
	private OntologyBioportalDAO ontologyBioportalDAO;
	private OntologyClassSubmissionSynonymDAO ontologyClassSubmissionSynonymDAO;
	private OntologySynonymSubmissionSynonymDAO ontologySynonymSubmissionSynonymDAO;
	private OntologyClassSubmissionSuperclassDAO ontologyClassSubmissionSuperclassDAO;
	private OntologyClassSubmissionPartOfDAO ontologyClassSubmissionPartOfDAO;
	
	public DAOManager() {
		collectionDAO = new CollectionDAO();
		termDAO = new TermDAO();
		ontologyDBDAO = new OntologyDAO();
		ontologyClassSubmissionDAO = new OntologyClassSubmissionDAO();
		ontologySynonymSubmissionDAO = new OntologySynonymSubmissionDAO();
		ontologyClassSubmissionStatusDAO = new OntologyClassSubmissionStatusDAO();
		ontologySynonymSubmissionStatusDAO = new OntologySynonymSubmissionStatusDAO();
		ontologyClassSubmissionSynonymDAO = new OntologyClassSubmissionSynonymDAO();
		ontologySynonymSubmissionSynonymDAO = new OntologySynonymSubmissionSynonymDAO();
		ontologyClassSubmissionSuperclassDAO = new OntologyClassSubmissionSuperclassDAO();
		ontologyClassSubmissionPartOfDAO = new OntologyClassSubmissionPartOfDAO();
		contextDAO = new ContextDAO();
		statusDAO = new StatusDAO();
		ontologyBioportalDAO = new OntologyBioportalDAO();
		
		collectionDAO.setTermDAO(termDAO);
		
		ontologyClassSubmissionDAO.setTermDAO(termDAO);
		ontologyClassSubmissionDAO.setOntologyDAO(ontologyDBDAO);
		ontologyClassSubmissionDAO.setOntologyClassSubmissionStatusDAO(ontologyClassSubmissionStatusDAO);
		ontologyClassSubmissionDAO.setOntologyClassSubmissionSynonymDAO(ontologyClassSubmissionSynonymDAO);
		ontologyClassSubmissionDAO.setOntologyClassSubmissionPartOfDAO(ontologyClassSubmissionPartOfDAO);
		ontologyClassSubmissionDAO.setOntologyClassSubmissionSuperclassDAO(ontologyClassSubmissionSuperclassDAO);
		ontologySynonymSubmissionDAO.setTermDAO(termDAO);
		ontologySynonymSubmissionDAO.setOntologyDAO(ontologyDBDAO);
		ontologySynonymSubmissionDAO.setOntologySynonymSubmissionStatusDAO(ontologySynonymSubmissionStatusDAO);
		ontologySynonymSubmissionDAO.setOntologySynonymSubmissionSynonymDAO(ontologySynonymSubmissionSynonymDAO);
		ontologySynonymSubmissionStatusDAO.setStatusDAO(statusDAO);
		ontologyClassSubmissionStatusDAO.setStatusDAO(statusDAO);
		//ontologyFileDAO.setOntologyDAO(ontologyDBDAO);
		//ontologyFileDAO.setCollectionDAO(collectionDAO);
		ontologyBioportalDAO.setOntologyClassSubmissionDAO(ontologyClassSubmissionDAO);
		ontologyBioportalDAO.setOntologySynonymSubmissionDAO(ontologySynonymSubmissionDAO);
	}

	public CollectionDAO getCollectionDAO() {
		return collectionDAO;
	}

	public TermDAO getTermDAO() {
		return termDAO;
	}

	public OntologyDAO getOntologyDBDAO() {
		return ontologyDBDAO;
	}
	
	public OntologyDAO2 getOntologyFileDAO(Collection collection) throws OntologyFileException {
		return new OntologyDAO2(collection, ontologyDBDAO);
	}

	public OntologyClassSubmissionDAO getOntologyClassSubmissionDAO() {
		return ontologyClassSubmissionDAO;
	}

	public OntologySynonymSubmissionDAO getOntologySynonymSubmissionDAO() {
		return ontologySynonymSubmissionDAO;
	}

	public OntologyClassSubmissionStatusDAO getOntologyClassSubmissionStatusDAO() {
		return ontologyClassSubmissionStatusDAO;
	}

	public OntologySynonymSubmissionStatusDAO getOntologySynonymSubmissionStatusDAO() {
		return ontologySynonymSubmissionStatusDAO;
	}

	public ContextDAO getContextDAO() {
		return contextDAO;
	}

	public StatusDAO getStatusDAO() {
		return statusDAO;
	}

	public OntologyBioportalDAO getOntologyBioportalDAO() {
		return ontologyBioportalDAO;
	}
	
	
}
