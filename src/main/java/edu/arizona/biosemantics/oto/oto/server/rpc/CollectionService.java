package edu.arizona.biosemantics.oto.oto.server.rpc;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.oto.oto.server.db.CollectionDAO;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.rpc.ICollectionService;

public class CollectionService extends RemoteServiceServlet implements ICollectionService {

	  @Override
	  public void init(ServletConfig config) throws ServletException {
	    super.init(config);
	    System.out.println(config.getServletContext().getContextPath());
	  }
	
	@Override
	public Collection get(Collection collection) throws Exception {
		return CollectionDAO.getInstance().get(collection.getId());
	}

}
