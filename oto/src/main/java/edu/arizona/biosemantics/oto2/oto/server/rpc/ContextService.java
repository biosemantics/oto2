package edu.arizona.biosemantics.oto2.oto.server.rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import edu.arizona.biosemantics.oto2.oto.server.db.DAOManager;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.common.context.shared.Context;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TypedContext;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.IContextService;

public class ContextService extends RemoteServiceServlet implements IContextService {

	private DAOManager daoManager;

	@Inject
	public ContextService(DAOManager daoManager) {
		this.daoManager = daoManager;
	}
	
	@Override
	public List<TypedContext> getContexts(Collection collection, Term term) throws ParseException, IOException {
		return daoManager.getContextDAO().get(collection, term);
	}
	
	@Override
	public List<Context> insert(int collectionId, String secret, List<Context> contexts) throws IOException {
		if(daoManager.getCollectionDAO().isValidSecret(collectionId, secret)) {
			/*List<Context> result = new ArrayList<Context>(contexts.size());
			for(Context context : contexts) {
				daoManager.getContextDAO().insert(collectionId, context);
				//context = daoManager.getContextDAO().insert(context);
				result.add(context);
			}
			return result;*/
			daoManager.getContextDAO().insert(collectionId, contexts);
			return contexts;
		}
		return null;
	}

}
