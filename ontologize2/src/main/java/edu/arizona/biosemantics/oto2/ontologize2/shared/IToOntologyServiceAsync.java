package edu.arizona.biosemantics.oto2.ontologize2.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;

public interface IToOntologyServiceAsync {
	public void storeLocalOntologiesToFile(Collection collection, AsyncCallback<Void> callback);
}
