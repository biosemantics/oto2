package edu.arizona.biosemantics.oto2.ontologize2.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;

public interface IUserLogServiceAsync {

	public void insertEdgeLog(String userid, String sessionid, String ontology ,String operation,
			Edge edge, AsyncCallback<Void> callback);

	public void insertLog(String userid, String sessionid, String ontology, String operation,
			String term, AsyncCallback<Void> callback);

}
