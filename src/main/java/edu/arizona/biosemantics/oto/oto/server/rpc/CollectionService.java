package edu.arizona.biosemantics.oto.oto.server.rpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.oto.oto.server.db.CollectionDAO;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.rpc.ICollectionService;

public class CollectionService extends RemoteServiceServlet implements ICollectionService {
	
	@Override
	public Collection get(Collection collection) throws Exception {
		return CollectionDAO.getInstance().get(collection.getId());
	}

	@Override
	public void update(Collection collection) throws Exception {
		CollectionDAO.getInstance().update(collection);
	}	

}
