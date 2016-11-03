package edu.arizona.biosemantics.oto2.ontologize2.shared;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent.RemoveMode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.CandidatePatternResult;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

public interface ICollectionServiceAsync {	
	
	public void insert(Collection collection, AsyncCallback<Collection> callback);
	
	public void get(int id, String secret, AsyncCallback<Collection> callback);
	
	public void update(Collection collection, AsyncCallback<Void> callback);
	
	public void add(int collectionId, String secret, Edge relation, AsyncCallback<Boolean> callback);
	
	public void remove(int collectionId, String secret, Edge relation, RemoveMode removeMode, AsyncCallback<Void> callback);
	
	public void replace(int collectionId, String secret, Edge oldRelation, Vertex newSource, AsyncCallback<Void> callback);

	public void add(int id, String secret, List<Candidate> candidates, 
			AsyncCallback<AddCandidateResult> callback);
	
	public void remove(int id, String secret, List<Candidate> candidates, AsyncCallback<Void> callback);

	public void getOWL(int id, String secret, AsyncCallback<String[][]> callback);

	public void close(int collectionId, String secret, Vertex vertex, Type type, boolean close, AsyncCallback<Void> callback);

	public void order(int id, String secret, Vertex src, List<Edge> edges, Type type, AsyncCallback<Void> callback);

	public void clear(int id, String secret, AsyncCallback<Void> callback);

	public void reduceGraph(int id, String secret, AsyncCallback<Void> callback);

	public void compositeModify(int id, String secret, CompositeModifyEvent event, AsyncCallback<Void> callback);

	public void getCandidatePatternResults(int id, String secret, AsyncCallback<Map<Candidate, List<CandidatePatternResult>>> callback);
	
	public void getCandidatePatternResults(int id, String secret, Candidate candidate, AsyncCallback<List<CandidatePatternResult>> callback);

	public void add(int id, String secret, Edge[] relations, AsyncCallback<Boolean> callback);

	public void remove(int id, String secret, Edge[] relations, RemoveMode removeMode, AsyncCallback<Void> callback);

}
