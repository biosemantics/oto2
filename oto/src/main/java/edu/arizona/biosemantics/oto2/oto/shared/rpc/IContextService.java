package edu.arizona.biosemantics.oto2.oto.shared.rpc;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.bioportal.model.Ontology;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.common.context.shared.Context;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Location;
import edu.arizona.biosemantics.oto2.oto.shared.model.OntologyEntry;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TypedContext;


/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("oto_context")
public interface IContextService extends RemoteService {
		
	public List<TypedContext> getContexts(Collection collection, Term term) throws Exception;
	
	public List<Context> insert(int collectionId, String secret, List<Context> contexts) throws IOException;
	
}
