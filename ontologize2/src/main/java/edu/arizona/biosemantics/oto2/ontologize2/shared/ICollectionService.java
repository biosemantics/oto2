package edu.arizona.biosemantics.oto2.ontologize2.shared;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent.RemoveMode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.CandidatePatternResult;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

@RemoteServiceRelativePath("ontologize2_collection")
public interface ICollectionService extends RemoteService {
	
	public Collection insert(Collection collection) throws Exception;;
	
	public Collection get(int id, String secret) throws Exception;
	
	public void update(Collection collection) throws Exception;
	
	public boolean add(int collectionId, String secret, Edge relation) throws Exception;
	
	public void remove(int collectionId, String secret, Edge relation, RemoveMode removeMode) throws Exception;
	
	public void replace(int collectionId, String secret, Edge oldRelation, Vertex newSource) throws Exception;
		
	public AddCandidateResult add(int id, String secret, List<Candidate> candidates) throws Exception;
	
	public void remove(int id, String secret, List<Candidate> candidates) throws Exception;
	
	public String[][] getOWL(int id, String secret) throws Exception;

	public void close(int collectionId, String secret, Vertex vertex, Type type, boolean close) throws Exception;
	
	public void order(int id, String secret, Vertex src, List<Edge> edges, Type type) throws Exception;
	
	public Collection clear(int id, String secret) throws Exception;
	
	public void reduceGraph(int id, String secret) throws Exception;
	
	public void compositeModify(int id, String secret, CompositeModifyEvent event) throws Exception;

	public Map<Candidate, List<CandidatePatternResult>> getCandidatePatternResults(int id, String secret) throws Exception;

	public List<CandidatePatternResult> getCandidatePatternResults(int id, String secret, Candidate candidate) throws Exception;

	public boolean add(int id, String secret, Edge[] relations) throws Exception;

	public void remove(int id, String secret, Edge[] relations, RemoveMode removeMode) throws Exception;
	/*used for recording user logs*/
	public void setUser(String user);
}
