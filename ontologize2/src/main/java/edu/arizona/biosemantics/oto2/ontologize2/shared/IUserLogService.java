package edu.arizona.biosemantics.oto2.ontologize2.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;

@RemoteServiceRelativePath("ontologize2_userlog")
public interface IUserLogService extends RemoteService{
	
	public void insertEdgeLog(String userid, String sessionid, String ontology, String operation, Edge edge) throws Exception;
	public void insertLog(String userid, String sessionid, String ontology, String operation, String term)  throws Exception;
}
