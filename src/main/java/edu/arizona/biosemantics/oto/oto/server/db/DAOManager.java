package edu.arizona.biosemantics.oto.oto.server.db;

public class DAOManager {

	private BucketDAO bucketDAO;
	private CollectionDAO collectionDAO;
	private TermDAO termDAO;
	private LabelDAO labelDAO;
	private LabelingDAO labelingDAO;
	private ContextDAO contextDAO;
	private SynonymDAO synonymDAO;
	
	public DAOManager() {
		bucketDAO = new BucketDAO();
		collectionDAO = new CollectionDAO();
		termDAO = new TermDAO();
		labelDAO = new LabelDAO();
		labelingDAO = new LabelingDAO();
		contextDAO = new ContextDAO();
		synonymDAO = new SynonymDAO();
		
		bucketDAO.setLabelingDAO(labelingDAO);
		bucketDAO.setTermDAO(termDAO);
		collectionDAO.setBucketDAO(bucketDAO);
		collectionDAO.setLabelDAO(labelDAO);
		collectionDAO.setLabelingDAO(labelingDAO);
		collectionDAO.setTermDAO(termDAO);
		termDAO.setBucketDAO(bucketDAO);
		termDAO.setContextDAO(contextDAO);
		labelDAO.setTermDAO(termDAO);
		contextDAO.setTermDAO(termDAO);
	}
	
	public BucketDAO getBucketDAO() {
		return bucketDAO;
	}

	public CollectionDAO getCollectionDAO() {
		return collectionDAO;
	}

	public TermDAO getTermDAO() {
		return termDAO;
	}

	public LabelDAO getLabelDAO() {
		return labelDAO;
	}

	public ContextDAO getContextDAO() {
		return contextDAO;
	}

	public SynonymDAO getSynonymDAO() {
		return synonymDAO;
	}

	public LabelingDAO getLabelingDAO() {
		return labelingDAO;
	}

}
