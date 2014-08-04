package edu.arizona.biosemantics.oto.oto.shared.model.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Context;
import edu.arizona.biosemantics.oto.oto.shared.model.Location;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ICollectionServiceAsync {
	
	public void get(Collection collection, AsyncCallback<Collection> callback);

	public void update(Collection collection, AsyncCallback<Void> callback);

	public void getContexts(Term term, AsyncCallback<List<Context>> callback); 
	
	public void getLocations(Term term, AsyncCallback<List<Location>> callback);
		
}
