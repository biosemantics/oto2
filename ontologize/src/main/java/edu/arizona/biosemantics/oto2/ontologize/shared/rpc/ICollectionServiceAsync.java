package edu.arizona.biosemantics.oto2.ontologize.shared.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ICollectionServiceAsync {	

	public void insert(Collection collection, AsyncCallback<Collection> callback);
	
	public void get(int id, String secret, AsyncCallback<Collection> callback);
	
	public void update(Collection collection, AsyncCallback<Void> callback);
	
		
}
