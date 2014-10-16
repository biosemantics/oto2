package edu.arizona.biosemantics.oto2.oto.shared.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ICommunityServiceAsync {
	
	public void get(AsyncCallback<Collection> callback);
		
}
