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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

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
import edu.arizona.biosemantics.oto2.ontologize2.server.pattern.CandidatePattern;
import edu.arizona.biosemantics.oto2.ontologize2.server.pattern.CandidatePatternDeducer;
import edu.arizona.biosemantics.oto2.ontologize2.shared.AddCandidateResult;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.CandidatePatternResult;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraphReducer;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.PredefinedVertex;
import edu.uci.ics.jung.graph.util.EdgeType;

public class CollectionService extends RemoteServiceServlet implements ICollectionService {

	private int currentCollectionId = 0;
	
	private String user;
	
	private UserLogService userLogService;
	@Inject
	public CollectionService(UserLogService userLogService) {
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
		
		this.userLogService = userLogService;
	}
	
	@Override
	public synchronized Collection insert(Collection collection) throws Exception {
		collection.setId(currentCollectionId++);
		initializeGraph(collection);
		serializeCollection(collection);
		return collection;
	}

	private void initializeGraph(Collection collection) {
		HashMap<Type, LinkedHashMap<String, List<PredefinedVertex>>> existingRelations = 
				new HashMap<Type, LinkedHashMap<String, List<PredefinedVertex>>>();
		ObjectMapper mapper = new ObjectMapper();
		TypeFactory typeFactory = mapper.getTypeFactory();
		MapType mapType = typeFactory.constructMapType(LinkedHashMap.class, typeFactory.constructType(String.class), 
				typeFactory.constructCollectionType(List.class, PredefinedVertex.class));
		try {
			existingRelations.put(Type.SUBCLASS_OF, (LinkedHashMap<String, List<PredefinedVertex>>)mapper.readValue(new File(Configuration.existingClassesFile), mapType));
			existingRelations.put(Type.PART_OF, (LinkedHashMap<String, List<PredefinedVertex>>)mapper.readValue(new File(Configuration.existingPartsFile), mapType));
			existingRelations.put(Type.SYNONYM_OF, (LinkedHashMap<String, List<PredefinedVertex>>)mapper.readValue(new File(Configuration.existingSynonymsFile), mapType));
			for(Type type : Type.values()) {
				if(existingRelations.containsKey(type)) {
					for(String superclass : existingRelations.get(type).keySet())  {
						for(PredefinedVertex subclass : existingRelations.get(type).get(superclass)) {
							Edge e = new Edge(new Vertex(superclass), new Vertex(subclass.getValue()), 
									type, Origin.USER);
							if(!subclass.isRequiresCandidate()) {
								TimeUnit.MILLISECONDS.sleep(2); //to avoid a random order of the edges, which per default are ordered by insertion time
								collection.getGraph().addRelation(e);
							}
						}
					}
				}
			}
		} catch(Exception e) {
			log(LogLevel.ERROR, "Could not read json", e);
		}
	}
	
	private void initializeGraphAfterClear(Collection collection) {
//		HashMap<Type, LinkedHashMap<String, List<PredefinedVertex>>> existingRelations = 
//				new HashMap<Type, LinkedHashMap<String, List<PredefinedVertex>>>();
//		ObjectMapper mapper = new ObjectMapper();
//		TypeFactory typeFactory = mapper.getTypeFactory();
//		MapType mapType = typeFactory.constructMapType(LinkedHashMap.class, typeFactory.constructType(String.class), 
//				typeFactory.constructCollectionType(List.class, PredefinedVertex.class));
		try {
//			existingRelations.put(Type.PART_OF, (LinkedHashMap<String, List<PredefinedVertex>>)mapper.readValue(new File(Configuration.existingPartsFile), mapType));
//			existingRelations.put(Type.SYNONYM_OF, (LinkedHashMap<String, List<PredefinedVertex>>)mapper.readValue(new File(Configuration.existingSynonymsFile), mapType));
//			for(Type type : Type.values()) {
//				if(existingRelations.containsKey(type)) {
//					for(String superclass : existingRelations.get(type).keySet())  {
//						for(PredefinedVertex subclass : existingRelations.get(type).get(superclass)) {
//							Edge e = new Edge(new Vertex(superclass), new Vertex(subclass.getValue()), 
//									type, Origin.USER);
//							if(!subclass.isRequiresCandidate()) {
//								TimeUnit.MILLISECONDS.sleep(2); //to avoid a random order of the edges, which per default are ordered by insertion time
//								collection.getGraph().addRelation(e);
//							}
//						}
//					}
//				}
//			}
			
			Edge c1 = new Edge(new Vertex("Thing"), new Vertex("material anatomical entity"), Type.SUBCLASS_OF, Origin.USER);
			Edge c2 = new Edge(new Vertex("material anatomical entity"), new Vertex("non-specific material anatomical entity"), Type.SUBCLASS_OF, Origin.USER);
			TimeUnit.MILLISECONDS.sleep(2);
			collection.getGraph().addRelation(c1);
			TimeUnit.MILLISECONDS.sleep(2);
			collection.getGraph().addRelation(c2);
			Edge c3 = new Edge(new Vertex("Thing"), new Vertex("quality"), Type.SUBCLASS_OF, Origin.USER);
			TimeUnit.MILLISECONDS.sleep(2);
			collection.getGraph().addRelation(c3);
		} catch(Exception e) {
			e.printStackTrace();
			log(LogLevel.ERROR, "Could not read json", e);
		}
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
			//userLogService.insertLog(this.user, "", "get_col", "");
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
		userLogService.insertEdgeLog(user, "", collectionId+"", "col_add", relation);
		return result;
	}
	
	@Override
	public synchronized void remove(int collectionId, String secret, Edge relation, RemoveMode removeMode) throws Exception {
		Collection collection = this.get(collectionId, secret);
		collection.getGraph().removeRelation(relation, removeMode);
		update(collection);
		userLogService.insertEdgeLog(user, "", collectionId+"", "col_remove", relation);
	}
	
	@Override
	public synchronized void replace(int collectionId, String secret, Edge oldRelation, Vertex newSource) throws Exception {
		Collection collection = this.get(collectionId, secret);
		collection.getGraph().replaceRelation(oldRelation, newSource);
		update(collection);
		// TODO: replace old and add new
		userLogService.insertEdgeLog(user, "", collectionId+"", "col_replace", oldRelation);
	}
	
	@Override
	public synchronized Collection clear(int collectionId, String secret) throws Exception {
		Collection collection = this.get(collectionId, secret);
		collection.getGraph().init();
		//initializeGraph(collection);
		initializeGraphAfterClear(collection);
		update(collection);
		userLogService.insertLog(user, "", collectionId+"", "clear", null);
		return collection;
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
	public synchronized void reduceGraph(int collectionId, String secret) throws Exception {
		Collection collection = this.get(collectionId, secret);
		OntologyGraphReducer reducer = new OntologyGraphReducer();
		reducer.reduce(collection.getGraph());
		update(collection);
	}

	@Override
	public synchronized void compositeModify(int id, String secret, CompositeModifyEvent event) throws Exception {
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
	
	@Override
	public synchronized Map<Candidate, List<CandidatePatternResult>> getCandidatePatternResults(int collectionId, String secret) throws Exception {
		Collection collection = this.get(collectionId, secret);
		CandidatePatternDeducer cpd = new CandidatePatternDeducer(collection);
		return cpd.deduce(collection);
	}
	
	@Override
	public synchronized List<CandidatePatternResult> getCandidatePatternResults(int collectionId, String secret, Candidate candidate) throws Exception {
		Collection collection = this.get(collectionId, secret);
		CandidatePatternDeducer cpd = new CandidatePatternDeducer(collection);
		return cpd.deduce(collection, candidate);
	}

	@Override
	public synchronized boolean add(int collectionId, String secret, Edge[] relations) throws Exception {
		boolean result = true;
		Collection collection = this.get(collectionId, secret);
		for(Edge r : relations){
			result &= collection.getGraph().addRelation(r);
			userLogService.insertEdgeLog(user, "", collectionId+"", "col_bat_add", r);
		}
			
			
		update(collection);
		return result;
	}

	@Override
	public synchronized void remove(int collectionId, String secret, Edge[] relations, RemoveMode removeMode) throws Exception {
		Collection collection = this.get(collectionId, secret);
		for(Edge r : relations)	{
			collection.getGraph().removeRelation(r, removeMode);
			userLogService.insertEdgeLog(user, "", collectionId+"", "col_bat_remove", r);
		}
		update(collection);
	}

	@Override
	public void setUser(String user) {
		this.user = user;
	}

}
