package edu.arizona.biosemantics.oto2.oto.server.rpc;

import java.util.Set;

import edu.arizona.biosemantics.oto2.oto.server.db.DAOManager;
import edu.arizona.biosemantics.oto2.oto.shared.model.community.Categorization;
import edu.arizona.biosemantics.oto2.oto.shared.model.community.CommunityCollection;
import edu.arizona.biosemantics.oto2.oto.shared.model.community.Synonymization;

public class CommunityService {

	private DAOManager daoManager = new DAOManager();
	
	public CommunityCollection get(String type) {
		Set<Categorization> categorizations = daoManager.getCommunityDAO().getCategorizations(type);
		Set<Synonymization> synonymizations = daoManager.getCommunityDAO().getSynoymizations(type);
		return new CommunityCollection(categorizations, synonymizations);
	}

}
