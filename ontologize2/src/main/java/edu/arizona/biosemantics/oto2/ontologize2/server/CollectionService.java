package edu.arizona.biosemantics.oto2.ontologize2.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent.RemoveMode;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.server.owl.OWLWriter;
import edu.arizona.biosemantics.oto2.ontologize2.shared.AddCandidateResult;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraphReducer;
import edu.uci.ics.jung.graph.util.EdgeType;

public class CollectionService extends RemoteServiceServlet implements ICollectionService {

	private int currentCollectionId = 0;
	
	public CollectionService() {
		File file = new File(Configuration.collectionsDirectory);
		if(!file.exists())
			file.mkdirs();
		
		for(File collectionFile : file.listFiles()) {
			try {
				int id = Integer.parseInt(collectionFile.getName());
				if(id >= currentCollectionId)
					currentCollectionId = id + 1;
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public synchronized Collection insert(Collection collection) throws Exception {
		collection.setId(currentCollectionId++);
		serializeCollection(collection);
		return collection;
	}

	private synchronized void serializeCollection(Collection collection) {
		File collectionDirectory = new File(Configuration.collectionsDirectory + File.separator + collection.getId());
		if(!collectionDirectory.exists())
			collectionDirectory.mkdir();
		File owlDirectory = new File(Configuration.collectionsDirectory + File.separator + collection.getId() + 
				File.separator + "owl");
		if(!owlDirectory.exists())
			owlDirectory.mkdir();
		
		try(ObjectOutputStream collectionOutput = new ObjectOutputStream(new FileOutputStream(
				Configuration.collectionsDirectory + File.separator + collection.getId() + File.separator + "collection.ser"))) {
			collectionOutput.writeObject(collection);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized Collection get(int collectionId, String secret) throws Exception {
		try(ObjectInputStream is = new ObjectInputStream(new FileInputStream(
				Configuration.collectionsDirectory + File.separator + collectionId + File.separator + "collection.ser"))) {
			Object object = is.readObject();
			if(object instanceof Collection) {
				Collection collection = (Collection)object;
				if(collection.getSecret().equals(secret))
					return collection;
			}
		}
		throw new Exception("Could not read collection");
	}

	@Override
	public synchronized void update(Collection collection) throws Exception {
		Collection storedCollection = this.get(collection.getId(), collection.getSecret());
		if(storedCollection.getSecret().equals(collection.getSecret())) {
			serializeCollection(collection);
		}
	}

	@Override
	public synchronized boolean add(int collectionId, String secret, Edge relation)	throws Exception {
		Collection collection = this.get(collectionId, secret);
		boolean result = collection.getGraph().addRelation(relation);
		update(collection);
		return result;
	}
	
	@Override
	public synchronized void remove(int collectionId, String secret, Edge relation, RemoveMode removeMode) throws Exception {
		try {
			Collection collection = this.get(collectionId, secret);
			collection.getGraph().removeRelation(relation, removeMode);
			update(collection);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public synchronized void replace(int collectionId, String secret, Edge oldRelation, Vertex newSource) throws Exception {
		Collection collection = this.get(collectionId, secret);
		collection.getGraph().replaceRelation(oldRelation, newSource);
		update(collection);
	}
	
	@Override
	public synchronized void clear(int collectionId, String secret) throws Exception {
		Collection collection = this.get(collectionId, secret);
		collection.getGraph().init();
		update(collection);
	}

	@Override
	public synchronized AddCandidateResult add(int collectionId, String secret, List<Candidate> candidates) throws Exception {
		Collection collection = this.get(collectionId, secret);
		List<Candidate> successfully = new LinkedList<Candidate>();
		List<Candidate> unsuccessfully = new LinkedList<Candidate>();
		for(Candidate candidate : candidates) {
			if(!collection.contains(candidate.getText())) {
				successfully.add(candidate);
				collection.add(candidate);
			} else {
				unsuccessfully.add(candidate);
			}
		}
		
		AddCandidateResult result = new AddCandidateResult(successfully, unsuccessfully);
		update(collection);
		return result;
	}

	@Override
	public synchronized void remove(int collectionId, String secret, List<Candidate> candidates) throws Exception {
		Collection collection = this.get(collectionId, secret);
		for(Candidate candidate : candidates) 
			collection.getCandidates().remove(candidate.getText());
		update(collection);
	}

	@Override
	public synchronized String[][] getOWL(int collectionId, String secret) throws Exception {
		try {
			Collection c = this.get(collectionId, secret);
			OWLWriter ow = new OWLWriter(c);
			ow.write();
			
			File[] children = new File(Configuration.collectionsDirectory + File.separator + c.getId() + File.separator + "owl").listFiles();
			String[][] result = new String[children.length][2];
			for(int i=0; i<children.length; i++) {
				result[i][0] = getFileContent(children[i]);
				result[i][1] = children[i].getName();
			}	
			return result;
		} catch(Exception e) {
			log(LogLevel.ERROR, "Could not create OWL", e);
			e.printStackTrace();
			throw e;
		}
	}

	private String getFileContent(File file) throws IOException {
		 byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		  return new String(encoded, "UTF8");
	}

	@Override
	public synchronized void close(int collectionId, String secret, Vertex vertex, Type type, boolean close) throws Exception {
		Collection collection = this.get(collectionId, secret);
		collection.getGraph().setClosedRelation(vertex, type, close);
		update(collection);
	}

	@Override
	public synchronized void order(int collectionId, String secret, Vertex src, List<Edge> edges, Type type) throws Exception {
		Collection collection = this.get(collectionId, secret);
		collection.getGraph().setOrderedEdges(src, edges, type);
		update(collection);
	}

	@Override
	public void reduceGraph(int collectionId, String secret) throws Exception {
		Collection collection = this.get(collectionId, secret);
		OntologyGraphReducer reducer = new OntologyGraphReducer();
		reducer.reduce(collection.getGraph());
		update(collection);
	}

	@Override
	public void compositeModify(int id, String secret, CompositeModifyEvent event) throws Exception {
		for(GwtEvent<?> e : event.getEvents()) {
			if(e instanceof CreateRelationEvent) {
				for(Edge r : ((CreateRelationEvent)e).getRelations()) {
					this.add(id, secret, r);
				}
			} else if(e instanceof RemoveRelationEvent) {
				RemoveMode removeMode = ((RemoveRelationEvent)e).getRemoveMode();
				for(Edge r : ((RemoveRelationEvent)e).getRelations()) {
					this.remove(id, secret, r, removeMode);
				}
			} else if(e instanceof ReplaceRelationEvent) {
				Edge oldRelation = ((ReplaceRelationEvent)e).getOldRelation();
				Vertex newSource = ((ReplaceRelationEvent)e).getNewSource();
				this.replace(id, secret, oldRelation, newSource);
			} else if(e instanceof CompositeModifyEvent) {
				CompositeModifyEvent c = (CompositeModifyEvent)e;
				this.compositeModify(id, secret, c);
			}
		}
	}

}
