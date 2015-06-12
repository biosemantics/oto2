package edu.arizona.biosemantics.oto2.ontologize.shared.rpc;

import java.io.IOException;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.oto2.ontologize.server.persist.db.Query.QueryException;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("ontologize_collection")
public interface ICollectionService extends RemoteService {
	
	public Collection insert(Collection collection) throws Exception;;
	
	public Collection get(int id, String secret) throws Exception;
	
	public void update(Collection collection) throws Exception;

	public void insertLinkedCollections(edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection collection,
			List<edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection> collections) throws Exception;

}
