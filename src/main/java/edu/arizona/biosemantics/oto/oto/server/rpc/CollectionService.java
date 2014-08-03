package edu.arizona.biosemantics.oto.oto.server.rpc;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.oto.oto.server.db.CollectionDAO;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
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
	
	public void createDefaultSecret(Collection collection) {
		String secret = String.valueOf(collection.getId());// Encryptor.getInstance().encrypt(Integer.toString(collection.getId()));
		collection.setSecret(secret);
	}

	public List<Label> createDefaultLabels() {
		List<Label> result = new LinkedList<Label>();
		Label abc = new Label("abc", "d");
		result.add(abc);
		result.add(new Label("bcd", "d"));
		result.add(new Label("efg", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("efg", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("efg", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("efg", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("efg", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("efg", "d"));
		result.add(new Label("bcd", "d"));
		return result;
	}

}
