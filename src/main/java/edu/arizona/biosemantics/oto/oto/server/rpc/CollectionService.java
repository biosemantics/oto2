package edu.arizona.biosemantics.oto.oto.server.rpc;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.oto.oto.server.db.BucketDAO;
import edu.arizona.biosemantics.oto.oto.server.db.CollectionDAO;
import edu.arizona.biosemantics.oto.oto.server.db.ContextDAO;
import edu.arizona.biosemantics.oto.oto.server.db.DAOManager;
import edu.arizona.biosemantics.oto.oto.server.db.LabelDAO;
import edu.arizona.biosemantics.oto.oto.server.db.TermDAO;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Context;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Location;
import edu.arizona.biosemantics.oto.oto.shared.model.Ontology;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;
import edu.arizona.biosemantics.oto.oto.shared.model.rpc.ICollectionService;

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
	public void update(Collection collection) throws Exception {
		try {
		daoManager.getCollectionDAO().update(collection);
		}catch(Exception e) {
			e.printStackTrace();
		}
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
