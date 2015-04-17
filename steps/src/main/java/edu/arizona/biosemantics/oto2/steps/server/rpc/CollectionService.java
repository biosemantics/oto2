package edu.arizona.biosemantics.oto2.steps.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.oto2.steps.server.persist.DAOManager;
import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.ICollectionService;

public class CollectionService extends RemoteServiceServlet implements ICollectionService {

	private DAOManager daoManager = new DAOManager();
	
	public Collection insert(Collection collection) {
		return daoManager.getCollectionDAO().insert(collection);
	}

	public Collection get(int id, String secret) {
		if(daoManager.getCollectionDAO().isValidSecret(id, secret))
			return daoManager.getCollectionDAO().get(id);
		return null;
	}

}
