package edu.arizona.biosemantics.oto.oto.shared.model.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.oto.oto.shared.model.Collection;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ICollectionServiceAsync {
	
	public void get(Collection collection, AsyncCallback<Collection> callback);

	public void update(Collection collection, AsyncCallback<Void> callback); 
		
}
