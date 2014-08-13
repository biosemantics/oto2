package edu.arizona.biosemantics.oto.oto.server.rpc;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.oto.oto.server.Configuration;
import edu.arizona.biosemantics.oto.oto.server.db.DAOManager;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Context;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Location;
import edu.arizona.biosemantics.oto.oto.shared.model.Ontology;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;
import edu.arizona.biosemantics.oto.oto.shared.rpc.ICollectionService;

public class CollectionService extends RemoteServiceServlet implements ICollectionService {
	
	private DAOManager daoManager = new DAOManager();
	
	@Override
	public Collection get(Collection collection) throws Exception {
		try {
			return daoManager.getCollectionDAO().get(collection.getId());
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Collection get(int id, String secret) throws Exception {
		Collection collection = new Collection(id, secret);
		return get(collection);
	}

	@Override
	public void update(Collection collection) throws Exception {
		try {
			daoManager.getCollectionDAO().update(collection);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}	
	
	@Override
	public Term addTerm(Term term, int bucketId) throws Exception {
		return daoManager.getTermDAO().insert(term, bucketId);
	}

	@Override
	public Label addLabel(Label label, int collectionId) throws Exception {
		return daoManager.getLabelDAO().insert(label, collectionId);
	}
	

	
	@Override
	public Collection insert(Collection collection) throws ClassNotFoundException, SQLException, IOException {
		if(collection.getLabels() == null || collection.getLabels().isEmpty())
			collection.setLabels(Configuration.defaultCategories);
		if(collection.getSecret() == null || collection.getSecret().isEmpty())
			createDefaultSecret(collection);
		return daoManager.getCollectionDAO().insert(collection);
	}
	
	private void createDefaultSecret(Collection collection) {
		String secret = String.valueOf(collection.getId());// Encryptor.getInstance().encrypt(Integer.toString(collection.getId()));
		collection.setSecret(secret);
	}

	@Override
	public List<Context> getContexts(Term term) throws Exception {
		return daoManager.getContextDAO().get(term);
	}

	@Override
	public List<Location> getLocations(Term term) throws Exception {
		List<Location> result = new LinkedList<Location>();
		Set<Label> labels = daoManager.getLabelingDAO().get(term);
		if(labels.isEmpty())
			result.add(new Location(term.getTerm(), "uncategorized"));
		for(Label label : labels) {
			result.add(new Location(term.getTerm(), label.getName()));
		}
		return result;
	}

	@Override
	public List<Ontology> getOntologies(Term term) {
		return daoManager.getOntologyDAO().get(term);
	}

}
