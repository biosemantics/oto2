package edu.arizona.biosemantics.oto2.oto.server.rpc;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Set;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.oto2.oto.server.Configuration;
import edu.arizona.biosemantics.oto2.oto.server.db.DAOManager;
import edu.arizona.biosemantics.oto2.oto.shared.model.community.Categorization;
import edu.arizona.biosemantics.oto2.oto.shared.model.community.CommunityCollection;
import edu.arizona.biosemantics.oto2.oto.shared.model.community.Synonymization;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICommunityService;

public class CommunityService extends RemoteServiceServlet implements ICommunityService {

	private DAOManager daoManager;
	
	@Inject
	public CommunityService(DAOManager daoManager) {
		this.daoManager = daoManager;
	}
	
	@Override
	public CommunityCollection get(String type) throws Exception {
		ObjectInputStream objectIn = new ObjectInputStream(new FileInputStream(Configuration.files + File.separator + "CommunityDecisions." + type + ".Categorization.ser"));
		Set<Categorization> categorizations = (Set<Categorization>) objectIn.readObject();
		objectIn.close();
		objectIn = new ObjectInputStream(new FileInputStream(Configuration.files + File.separator + "CommunityDecisions." + type + ".Synonymization.ser"));
		Set<Synonymization> synonymizations = (Set<Synonymization>) objectIn.readObject();
		objectIn.close();
		return new CommunityCollection(categorizations, synonymizations);
	}

}
