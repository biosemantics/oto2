package edu.arizona.biosemantics.oto2.ontologize.server.persist;

import edu.arizona.biosemantics.oto2.ontologize.server.persist.bioportal.OntologyBioportalDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.CollectionDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.ContextDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.OntologyClassSubmissionDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.OntologyClassSubmissionPartOfDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.OntologyClassSubmissionStatusDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.OntologyClassSubmissionSuperclassDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.OntologyClassSubmissionSynonymDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.OntologyDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.OntologySynonymSubmissionDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.OntologySynonymSubmissionStatusDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.OntologySynonymSubmissionSynonymDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.StatusDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.TermDAO;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.file.OntologyFileDAO;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.OntologyFileException;

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
		collectionDAO.setOntologyClassSubmissionDAO(ontologyClassSubmissionDAO);
		collectionDAO.setOntologySynonymSubmissionDAO(ontologySynonymSubmissionDAO);
		
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
	
	public OntologyFileDAO getOntologyFileDAO(Collection collection) throws OntologyFileException {
		return new OntologyFileDAO(collection, ontologyDBDAO);
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
