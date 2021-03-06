package edu.arizona.biosemantics.oto2.ontologize2.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent.RemoveMode;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

public class OntologyGraph implements Serializable {

	private static final long serialVersionUID = 1L;

	public static class Vertex implements Serializable, Comparable<Vertex> {

		private static final long serialVersionUID = 1L;
		private String value;

		public Vertex() {
		}

		public Vertex(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Vertex other = (Vertex) obj;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

		@Override
		public int compareTo(Vertex o) {
			return this.value.compareTo(o.value);
		}
	}

	public static class Edge implements Serializable {

		public static enum Type {
			SUBCLASS_OF("category", "superclass", "superclasses", "subclass", "subclasses", "category hierarchy", "Thing"), 
			PART_OF("part", "parent", "parents", "part", "parts", "part-of hierarchy", "Whole Organism"), 
			SYNONYM_OF("synonym", "preferred term", "preferred terms", "synonym", "synonyms", "synonym-hierarchy", "Synonym-Root");

			private String displayLabel;
			private String sourceLabel;
			private String targetLabel;
			private String treeLabel;
			private String rootLabel;
			private String sourceLabelPlural;
			private String targetLabelPlural;

			private Type(String displayLabel, String sourceLabel, String sourceLabelPlural,
					String targetLabel, String targetLabelPlural, String treeLabel, String rootLabel) {
				this.displayLabel = displayLabel;
				this.sourceLabel = sourceLabel;
				this.sourceLabelPlural = sourceLabelPlural;
				this.targetLabel = targetLabel;
				this.targetLabelPlural = targetLabelPlural;
				this.treeLabel = treeLabel;
				this.rootLabel = rootLabel;
			}

			public String getDisplayLabel() {
				return displayLabel;
			}

			public String getSourceLabel() {
				return sourceLabel;
			}

			public String getTargetLabel() {
				return targetLabel;
			}

			public String getTreeLabel() {
				return treeLabel;
			}

			public String getRootLabel() {
				return rootLabel;
			}

			public String getTargetLabelPlural() {
				return targetLabelPlural;
			}

			public String getSourceLabelPlural() {
				return sourceLabelPlural;
			}
		}

		public static enum Origin {
			USER, IMPORT;
		}

		private static final long serialVersionUID = 1L;
		private Type type;
		private Origin origin;
		private Vertex src;
		private Vertex dest;
		private Date creation;

		public Edge() {
		}

		public Edge(Vertex src, Vertex dest, Type type, Origin origin) {
			this.src = src;
			this.dest = dest;
			this.type = type;
			this.origin = origin;
			this.creation = new Date();
		}

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public Origin getOrigin() {
			return origin;
		}

		@Override
		public String toString() {
			return src + " ---- " + type + " (" + origin + ") " + creation + " ----> " + dest;
		}
		
		public Vertex getSrc() {
			return src;
		}

		public Vertex getDest() {
			return dest;
		}
		
		public Date getCreation() {
			return creation;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((dest == null) ? 0 : dest.hashCode());
			result = prime * result
					+ ((origin == null) ? 0 : origin.hashCode());
			result = prime * result + ((src == null) ? 0 : src.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Edge other = (Edge) obj;
			if (dest == null) {
				if (other.dest != null)
					return false;
			} else if (!dest.equals(other.dest))
				return false;
			if (origin != other.origin)
				return false;
			if (src == null) {
				if (other.src != null)
					return false;
			} else if (!src.equals(other.src))
				return false;
			if (type != other.type)
				return false;
			return true;
		}
	}
	
	private Type[] types;
	private DirectedSparseMultigraph<Vertex, Edge> graph;
	private Map<String, Vertex> index;
	private Map<Vertex, Set<Type>> closedRelations;
	private Map<Vertex, Map<Type, List<Edge>>> orderedEdges;

	public OntologyGraph() { }
	
	public OntologyGraph(Type... types) {
		this.types = types;
		init();
	}
	
	private boolean addVertex(Vertex vertex) {
		boolean result = graph.addVertex(vertex);
		if (result)
			index.put(vertex.getValue(), vertex);
		return result;
	}

	private boolean removeVertex(Vertex vertex) {
		boolean result = graph.removeVertex(vertex);
		if (result) {
			index.remove(vertex.getValue());
			closedRelations.remove(vertex);
			orderedEdges.remove(vertex);
		}
		return result;
	}

	public boolean addRelation(Edge edge) throws Exception {
		if(isClosedRelations(edge.getSrc(), edge.getType()))
			throw new Exception("There are no new outgoing relations allowed for " + edge.getSrc());
		switch(edge.getType()) {
			case PART_OF:
				return addPartOf(edge);
			case SUBCLASS_OF:
				return addSubclass(edge);
			case SYNONYM_OF:
				return addSynonym(edge);
		}
		return false;
	}


	private boolean doAddRelation(Edge edge) throws Exception {
		if(!graph.containsVertex(edge.getSrc()))
			this.addVertex(edge.getSrc());
		if(!graph.containsVertex(edge.getDest()))
			this.addVertex(edge.getDest());
		
		Vertex src = edge.getSrc();
		Vertex dest = edge.getDest();
		Type type = edge.getType();
		Vertex root = this.getRoot(type);
		if(!src.equals(root) && this.getInRelations(src, type).size() == 0) {
			this.addRelation(new Edge(root, src, type, edge.getOrigin()));
			System.out.println("Added root: Dangling relation");
		}
		this.removeRootEdgeOnEdgeAddIfNecessary(edge);
		return graph.addEdge(edge, edge.getSrc(), edge.getDest(), EdgeType.DIRECTED);
	}
	
	//Jin update 04-20-2017
	//check circular
	public boolean isCreatesCircular(Edge potentialRelation) {
		Vertex search = potentialRelation.getSrc();
		Vertex source = potentialRelation.getDest();
		if(search.equals(source))
			return true;
		List<Edge> out = getOutRelations(source, potentialRelation.getType());
		for(Edge e : out) {
			//if(isCreatesCircular(e))
			if(e.getDest().equals(search))
				return true;
		}
		return false;
		
		//Set<Vertex> potentialCircle = new HashSet<Vertex>();
		//Set<Vertex> visited = new HashSet<Vertex>();
		//potentialCircle.add(potentialRelation.getSrc());
		//return isCreatesCircular(potentialRelation, potentialCircle, visited);
	}
	
	/**
	 * Searches for all circles. Above limits search to circle just closed by relation. More efficient
	 * @param edge
	 * @param potentialCircle
	 * @param visited
	 * @return
	 */
//	private boolean isCreatesCircular(Edge e, Set<Vertex> potentialCircle, Set<Vertex> visited) {
//		if(potentialCircle.contains(e.getDest()))
//			return true;
//		else
//			potentialCircle.add(e.getDest());
//		
//		if(visited.contains(e.getDest()))
//			return false;
//		else
//			visited.add(e.getDest());
//		
//		for(Edge next : this.getOutRelations(e.getDest(), e.getType())) {
//			boolean result = isCreatesCircular(next, potentialCircle, visited);
//			if(result)
//				return true;
//			potentialCircle.remove(next.getDest());
//		}
//		return false;
//	}
	

	/**
	 * A node is either 
	 * - root node, has indegree == 0 and outdegree >= 0
	 * - indegree == 1 && in = { root } and outdegree >= 0
	 * - indegree == 1 && in = { != root } and outdegree == 0
	 */
	public boolean isValidSynonym(Edge e, boolean checkSynonymExistence, boolean checkPreferredTermExistence) throws Exception {
		if(this.existsRelation(e))
			throw new Exception("This relation already exists");
		Vertex src = e.getSrc();
		Vertex dest = e.getDest();
		Vertex root = this.getRoot(Type.SYNONYM_OF);
		List<Edge> srcIn = this.getInRelations(src, Type.SYNONYM_OF);
		List<Edge> srcOut = this.getInRelations(src, Type.SYNONYM_OF);
		List<Edge> destIn = this.getInRelations(dest, Type.SYNONYM_OF);
		List<Edge> destOut = this.getInRelations(dest, Type.SYNONYM_OF);
		
		if(dest.equals(root))
			throw new Exception("<i>" + root + "</i> can not be used as synonym");
		if(src.equals(root) && !destIn.isEmpty() && destIn.contains(root))
			throw new Exception("<i>" + dest + "</i> is already used as preferred term");
		if(src.equals(root) && !destIn.isEmpty() && !destIn.contains(root) && checkSynonymExistence)
			throw new Exception("<i>" + dest + "</i> is already used as synonym");
		if(!src.equals(root) && srcIn.isEmpty() && checkPreferredTermExistence)
			throw new Exception("<i>" + src + "</i> is not attached to \"" + root + "\"");
		if(!src.equals(root) && !destIn.isEmpty() && destIn.contains(root))
			throw new Exception("<i>" + dest + "</i> is already used as preferred term");
		if(!src.equals(root) && !destIn.isEmpty() && !destIn.contains(root) && checkSynonymExistence)
			throw new Exception("<i>" + dest + "</i> is already used as synonym");
		
		//if(isSubclassOrPart(e))				
		//	throw new Exception("Cannot create synonym term: " + e.getDest() + ". It is already used in "
		//			+ "subclass or part relations");
		return true;
	}
	
	public boolean isSubclassOrPart(Vertex v) {
		List<Edge> subclassPartRelations = new ArrayList<Edge>();
		subclassPartRelations.addAll(getRelations(v, Type.PART_OF));
		subclassPartRelations.addAll(getRelations(v, Type.SUBCLASS_OF));
		return !subclassPartRelations.isEmpty();
	}

	private boolean addSynonym(Edge e) throws Exception {
		if(isValidSynonym(e, true, true))
			return doAddRelation(e);
		return false;
	}

	/**
	 * - No circular relationships allowed
	 */	
	public boolean isValidSubclass(Edge e) throws Exception {
		if(this.existsRelation(e))
			throw new Exception("This relation already exists");
		if(isCreatesCircular(e))
			throw new Exception("This relation would create a circular relationship");
		//TODO: change something
		this.isPreferredTerms(e);
		return true;
	}

	private boolean isPreferredTerms(Edge e) throws Exception {
		switch(e.getType()) {
			case PART_OF:
			case SUBCLASS_OF:
				for(Vertex v : new Vertex[] { e.getSrc(), e.getDest() }) {
					List<Edge> in = getInRelations(v, Type.SYNONYM_OF);
					boolean synonymRelation = false;
					for(Edge edge : in) {
						if(!edge.getSrc().equals(getRoot(Type.SYNONYM_OF))) {
							synonymRelation = true;
							break;
						}
					}
					if(synonymRelation) {
						throw new Exception("Cannot use synonym term: " + v + ". Use preferred term " + 
								in.get(0).getSrc().getValue() + " instead");
					} 
				}
				break;
			default:
				break;
		}
		return true;
	}

	/**
	 * check the source, target and type, Origin will not be checked
	 * @param r
	 * @return
	 */
	public boolean existsRelation(Edge r) {
		if(graph.containsVertex(r.getSrc()) && graph.containsVertex(r.getDest())) {
			for(Edge e : graph.getOutEdges(r.getSrc())) {
	            if(graph.getOpposite(r.getSrc(), e).equals(r.getDest()))
	            	if(e.getType().equals(r.getType()))
	            		return true;
	        }
		}
		return false;
	}

	private boolean addSubclass(Edge e) throws Exception {
		if(isValidSubclass(e))
			return doAddRelation(e);
		return false;
	}

	/**
	 * - No circular relationships allowed
	 */
	public boolean isValidPartOf(Edge e) throws Exception {
		if(this.existsRelation(e))
			throw new Exception("This relation already exists");
		if(isCreatesCircular(e))
			throw new Exception("This relation would create a circular relationship");
		isPreferredTerms(e);	
		return true;
	}
	
	/**
	 * - No multiple parents allowed, but allowed as input with permission to disambiguate as follows: 
	 * Disambiguate by parent name and create subclass relationships to original term, e.g.
	 * leaf, leaflet (part)
	 * stem, leaflet (part)
	 * =>
	 * leaf, leaf leaflet (part)
	 * stem, stem leaflet (part)
	 * leaf, leaf leaflet, stem leaflet (subclass)
	 */
	private boolean addPartOf(Edge e) throws Exception {
		if(isValidPartOf(e)) {
			Vertex src = e.getSrc();
			Vertex dest = e.getDest();
			String originalDestValue = dest.getValue();
			String newDestValue = src + " " + dest;
			removeRootEdgeOnEdgeAddIfNecessary(e);
			List<Edge> parentRelations = this.getInRelations(dest, Type.PART_OF);
			if(!parentRelations.isEmpty()) {			
				for(Edge parentRelation : parentRelations) {
					Vertex parentSrc = parentRelation.getSrc();
					String existingNewDestValue = parentSrc + " " + dest;
					Vertex disambiguatedExistingDest = new Vertex(existingNewDestValue);
					renameVertex(dest, existingNewDestValue);
					Vertex originalDest = new Vertex(originalDestValue);
					this.addVertex(originalDest);
					this.addRelation(new Edge(originalDest, disambiguatedExistingDest, Type.SUBCLASS_OF, e.getOrigin()));
				}
				e.getDest().setValue(newDestValue);
				this.addRelation(new Edge(new Vertex(originalDestValue), e.getDest(), Type.SUBCLASS_OF, e.getOrigin()));
			}
			return doAddRelation(e);
		}
		return false;
	}
		
	private void removeRootEdgeOnEdgeAddIfNecessary(Edge e) throws Exception {
		Vertex root = this.getRoot(e.getType());
		Vertex src = e.getSrc();
		Vertex dest = e.getDest();
		List<Edge> inRelations = this.getInRelations(dest, e.getType());
		if(!inRelations.isEmpty()) {
			if(src.equals(root))
				throw new Exception("Root " + root + " can not be part of multiple inheritance");
			else
				for(Edge in : inRelations) {
					if(in.getSrc().equals(root)) {
						this.removeEdge(in);
						System.out.println("Removed root: Multiple inheritance");
					}
				}
		}
	}

	public void setClosedRelation(Vertex v, Type type, boolean close) {
		if(close) {
			if(!closedRelations.containsKey(v))
				closedRelations.put(v, new HashSet<Type>());
			this.closedRelations.get(v).add(type);
		} else {
			if(closedRelations.containsKey(v)) {
				closedRelations.get(v).remove(type);
				if(closedRelations.get(v).isEmpty())
					closedRelations.remove(v);
			}
		}
	}
	
	private void setClosedRelations(Vertex v, Set<Type> closedRelations) {
		this.closedRelations.put(v, closedRelations);
	}

	private Set<Type> getClosedRelations(Vertex v) {
		if(!closedRelations.containsKey(v))
			return new HashSet<Type>();
		return closedRelations.get(v);
	}
	
	public boolean isClosedRelations(Vertex src, Type type) {
		if(closedRelations.containsKey(src)) {
			if(closedRelations.get(src).contains(type))
				return true;
		}
		return false;
	}
	
	private void renameVertex(Vertex v, String newValue) throws Exception {
		Vertex newV = new Vertex(newValue);
		List<Edge> inRelations = this.getInRelations(v);
		List<Edge> outRelations = this.getOutRelations(v);
		
		Set<Type> closedRelations = this.getClosedRelations(v);
		Map<Type, List<Edge>> orderedEdges = this.getOrderedEdges(v);
		this.removeVertex(v);
		this.addVertex(newV);
		this.setClosedRelations(newV, closedRelations);
		this.setOrderedEdges(newV, orderedEdges);
		
		for(Edge inRelation : inRelations) {
			Edge newEdge = new Edge(inRelation.getSrc(), newV, inRelation.getType(), inRelation.getOrigin());
			this.addRelation(newEdge);
		}
		for(Edge outRelation : outRelations) {
			Edge newEdge = new Edge(newV, outRelation.getDest(), outRelation.getType(), outRelation.getOrigin());
			this.addRelation(newEdge);
			Vertex dest = outRelation.getDest();
			
			//on prefix-match with old parent name, propagate rename to children
			if(dest.getValue().startsWith(v.getValue() + " ")) {
				renameVertex(dest, dest.getValue().replaceFirst(v.getValue() + " ", newV.getValue() + " "));
			}
		}
	}

	public Vertex getVertex(String value) {
		return index.get(value);
	}
	
	public Vertex getRoot(Type type) {
		return index.get(type.getRootLabel());
	}
	
	public List<Edge> getOutRelations(Vertex vertex, Type type) {
		List<Edge> result = new LinkedList<Edge>();
		if(graph.containsVertex(vertex)) {
			java.util.Collection<Edge> edges = graph.getOutEdges(vertex);
			for(Edge edge : edges) {
				if(edge.getType().equals(type))
					result.add(edge);
			}
		}
		
		if(orderedEdges.containsKey(vertex) && orderedEdges.get(vertex).containsKey(type)) {
			final List<Edge> order = orderedEdges.get(vertex).get(type);
			Collections.sort(result, new Comparator<Edge>() {
				@Override
				public int compare(Edge o1, Edge o2) {
					if(!order.contains(o1) || !order.contains(o2)) 
						return Integer.MAX_VALUE;
					return order.indexOf(o1) - order.indexOf(o2);
				}
			});
		}
		return result;
	}
	
	public List<Edge> getInRelations(Vertex vertex) {
		List<Edge> result = new LinkedList<Edge>();
		if(graph.containsVertex(vertex)) {
			java.util.Collection<Edge> edges = graph.getInEdges(vertex);
			for(Edge edge : edges) 
				result.add(edge);
		}
		return result;
	}
	
	public List<Vertex> getDestinations(final Vertex vertex) {
		List<Edge> edges = this.getOutRelations(vertex);
		List<Vertex> result = new ArrayList<Vertex>(edges.size());
		for(Edge e : edges)
			result.add(e.getDest());
		return result;
	}
	
	public List<Vertex> getDestinations(final Vertex vertex, final Type type) {
		List<Edge> edges = this.getOutRelations(vertex, type);
		List<Vertex> result = new ArrayList<Vertex>(edges.size());
		for(Edge e : edges)
			result.add(e.getDest());
		return result;
	}
	
	public List<Vertex> getSources(final Vertex vertex) {
		List<Edge> edges = this.getInRelations(vertex);
		List<Vertex> result = new ArrayList<Vertex>(edges.size());
		for(Edge e : edges)
			result.add(e.getSrc());
		return result;
	}
	
	public List<Vertex> getSources(final Vertex vertex, final Type type) {
		List<Edge> edges = this.getInRelations(vertex, type);
		List<Vertex> result = new ArrayList<Vertex>(edges.size());
		for(Edge e : edges)
			result.add(e.getSrc());
		return result;
	}
	
	public List<Edge> getOutRelations(final Vertex vertex) {
		List<Edge> result = new LinkedList<Edge>();
		if(graph.containsVertex(vertex)) {
			java.util.Collection<Edge> edges = graph.getOutEdges(vertex);
			for(Edge edge : edges) 
				result.add(edge);
		}

		if(orderedEdges.containsKey(vertex)) {
			final List<Edge> order = new LinkedList<Edge>(); 
			for(Type type : Type.values()) {
				Set<Edge> outRelations = new HashSet<Edge>(this.getOutRelations(vertex, type));
				if(orderedEdges.get(vertex).containsKey(type)) {
					outRelations.removeAll(orderedEdges.get(vertex).get(type));
					order.addAll(orderedEdges.get(vertex).get(type));	
					order.addAll(outRelations);
				} else {
					order.addAll(outRelations);
				}
			}
			Collections.sort(result, new Comparator<Edge>() {
				@Override
				public int compare(Edge o1, Edge o2) {
					if(!order.contains(o1) || !order.contains(o2)) 
						return Integer.MAX_VALUE;
					return order.indexOf(o1) - order.indexOf(o2);
				}
			});
		}
		return result;
	}
	
	public List<Edge> getInRelations(Vertex vertex, Type type) {
		List<Edge> result = new LinkedList<Edge>();
		if(graph.containsVertex(vertex)) {
			java.util.Collection<Edge> edges = graph.getInEdges(vertex);
			for(Edge edge : edges) {
				if(edge.getType().equals(type))
					result.add(edge);
			}
		}
		return result;
	}

	public void removeRelation(Edge e, RemoveMode removeMode) throws Exception {
		if(isClosedRelations(e.getSrc(), e.getType())) {
			throw new Exception("There are no outgoing relations allowed to be removed from " + e.getSrc());
		}
		Vertex dest = e.getDest();
		Vertex src = e.getSrc();
		Type type = e.getType();
		
		switch(removeMode) {
		case NONE:
			removeEdge(e);
			if(this.getInRelations(dest).isEmpty())
				this.removeVertex(dest);
			break;
		case REATTACH_TO_AVOID_LOSS:
			List<Edge> inRelations = this.getInRelations(dest, type);//quality-->coloration
			removeEdge(e);
			if(inRelations.size() == 1) {//fix this bug
				for(Edge outRelation : this.getOutRelations(dest, type)) {//coloration-->green,grey
					List<Edge> in = this.getInRelations(outRelation.getDest(), type);//coloration-->green
					in.remove(outRelation);
					//if(type.equals(Type.PART_OF)) 
						removeEdge(outRelation);
					if(in.isEmpty()) {
						try {//quality-->green
							Edge newEdge = new Edge(src, outRelation.getDest(), type, Origin.USER);
							this.addRelation(newEdge);
						} catch(Exception ex) {
							//This should never happen
							ex.printStackTrace();
						}
					}
				}
			}
			if(this.getInRelations(dest).isEmpty()) {
				this.removeVertex(dest);
			}
			break;
		case RECURSIVE:
			removeEdge(e);
			for(Edge outRelation : this.getOutRelations(dest, type)) {
				if(this.getInRelations(outRelation.getDest(), type).size() == 1) 
					this.removeRelation(outRelation, removeMode);
			}
			if(this.getInRelations(dest).isEmpty()) {
				this.removeVertex(dest);
			}
			break;
		default:
			break;
		}
	}
	
	public void removeEdge(Edge e) {
		graph.removeEdge(e);
		if(orderedEdges.containsKey(e.getSrc()) && orderedEdges.get(e.getSrc()).containsKey(e.getType()))
			orderedEdges.get(e.getSrc()).get(e.getType()).remove(e);
	}

	public OntologyGraph getSubGraph(Type... types) throws Exception {
		OntologyGraph result = new OntologyGraph(types);
		for(Type type : types) {
			Vertex root = result.getRoot(type);
			addRelationsRecursively(result, root, type);
		}
		return result;
	}

	private void addRelationsRecursively(OntologyGraph g, Vertex source, Type type) throws Exception {
		for(Edge e : this.getOutRelations(source, type)) {
			this.addRelation(e);
			this.addRelationsRecursively(g, e.getDest(), type);
		}
	}

	public Collection<Vertex> getVertices() {
		return graph.getVertices();
	}

	//only replace the source of the relation
	public void replaceRelation(Edge oldRelation, Vertex newSource) throws Exception {
		if(this.isClosedRelations(oldRelation.getSrc(), oldRelation.getType()))
			throw new Exception("There are no outgoing relations allowed to be removed from " + oldRelation.getSrc());
		if(this.isClosedRelations(newSource, oldRelation.getType()))
			throw new Exception("There are no new outgoing relations allowed for " + newSource);
		this.removeEdge(oldRelation);
		try {
			Edge newEdge = new Edge(newSource, oldRelation.getDest(), oldRelation.getType(), oldRelation.getOrigin());
			this.addRelation(newEdge);			
		} catch(Exception e) {
			this.addRelation(oldRelation);
			throw e;
		}
		
		if(oldRelation.getType().equals(Type.SYNONYM_OF)) {
			for(Edge e : this.getOutRelations(oldRelation.getDest(), Type.SYNONYM_OF)) {
				this.replaceRelation(e, newSource);
			}
		}
	}

	public void setOrderedEdges(Vertex src, List<Edge> edges, Type type) throws Exception {
		if(!edges.isEmpty()) {
			Set<Edge> existingEdges = new HashSet<Edge>(this.getOutRelations(src, type));
			for(Edge edge : edges) {
				if(existingEdges.contains(edge))
					existingEdges.remove(edge);
				else
					throw new Exception("Can not add new edges when ordering");
			}
			if(!existingEdges.isEmpty())
				throw new Exception("Can not remove edges when ordering");
			
			if(!orderedEdges.containsKey(src))
				orderedEdges.put(src, new HashMap<Type, List<Edge>>());
			orderedEdges.get(src).put(type, edges);
		} else {
			if(orderedEdges.containsKey(src)) {
				orderedEdges.get(src).remove(type);
				if(orderedEdges.get(src).isEmpty())
					orderedEdges.remove(src);
			}
		}
	}
	
	private void setOrderedEdges(Vertex v, Map<Type, List<Edge>> orderedEdges) throws Exception {
		for(Type type : orderedEdges.keySet())
			this.setOrderedEdges(v, orderedEdges.get(type), type);
	}

	private Map<Type, List<Edge>> getOrderedEdges(Vertex v) {
		if(!orderedEdges.containsKey(v))
			return new HashMap<Type, List<Edge>>();
		return orderedEdges.get(v);
	}
	
	public List<Edge> getOrderedEdges(Vertex v, Type type) {
		if(!orderedEdges.containsKey(v))
			return new LinkedList<Edge>();
		if(!orderedEdges.get(v).containsKey(type))
			return new LinkedList<Edge>();
		return orderedEdges.get(v).get(type);
	}
	
	

	public List<Vertex> getAllDestinations(Vertex src, Type type) {
		List<Vertex> result = new LinkedList<Vertex>();
		for(Edge e : this.getOutRelations(src, type)) { 
			result.add(e.getDest());
			result.addAll(getAllDestinations(e.getDest(), type));
		}
		return result;
	}
	
	public List<Vertex> getAllSources(Vertex dest, Type type) {
		List<Vertex> result = new LinkedList<Vertex>();
		for(Edge e : this.getInRelations(dest, type)) { 
			result.add(e.getSrc());
			result.addAll(getAllSources(e.getSrc(), type));
		}
		return result;
	}

	public void init() {
		graph = new DirectedSparseMultigraph<Vertex, Edge>();
		index = new HashMap<String, Vertex>();
		closedRelations = new HashMap<Vertex, Set<Type>>();
		orderedEdges = new HashMap<Vertex, Map<Type, List<Edge>>>();
		for (Type type : types)
			this.addVertex(new Vertex(type.getRootLabel()));
	}

	public boolean containsVertex(Vertex vertex) {
		return graph.containsVertex(vertex);
	}

	public int getMaxOutRelations(Type type, Set<Vertex> exclusions) {
		int max = 0;
		for(Vertex v : this.getVertices()) {
			if(!exclusions.contains(v)) {
				int size = this.getOutRelations(v, type).size();
				if(size > max)
					max = size;
			}
		}
		return max;
	}
	
	@Override
	public String toString() {
		return graph.toString();
	}

	public boolean hasOrderedEdges(Vertex v, Type type) {
		return orderedEdges.containsKey(v) && orderedEdges.get(v).containsKey(type);
	}

	public List<Vertex> getVerticesBFS(Type type) {
		List<Vertex> result = new LinkedList<Vertex>();
		Set<Vertex> collected = new HashSet<Vertex>();
		
		Vertex root = this.getRoot(type);
		List<Vertex> srcQueue = new LinkedList<Vertex>();
		srcQueue.add(root);
		while(!srcQueue.isEmpty()) {
			Vertex src = srcQueue.remove(0);
			result.add(src);
			for(Vertex dest : this.getDestinations(src, type)) {
				if(!collected.contains(dest)) {
					srcQueue.add(dest);
					collected.add(dest);
				}
			}
		}
		return result;
	}

	public List<Edge> getRelations(Vertex v, Type type) {	
		List<Edge> result = new LinkedList<Edge>();
		if(graph.containsVertex(v)) {
			java.util.Collection<Edge> edges = graph.getIncidentEdges(v);
			for(Edge edge : edges) {
				if(edge.getType().equals(type))
					result.add(edge);
			}
		}
		return result;
	}

	//v is a synonym term of some preferred term
	public boolean isSynonym(Vertex v) {
		List<Edge> synIn = this.getInRelations(v, Type.SYNONYM_OF);
		return !synIn.isEmpty() && !synIn.get(0).getSrc().equals(this.getRoot(Type.SYNONYM_OF));
	}

	public Vertex getPreferredTerm(Vertex v) {
		if(!isSynonym(v))
			return v;
		return this.getInRelations(v, Type.SYNONYM_OF).get(0).getSrc();
	}

	public Edge getEdge(Vertex source, Vertex target, Type type) {
		for(Edge e : graph.getOutEdges(source)) {
			if(graph.getOpposite(source, e).equals(target))
				if(e.getType().equals(type))
					return e;
	        }
		return null;
	}

	public void addRelations(Edge[] relations) throws Exception {
		for(Edge r : relations)
			this.addRelation(r);
	}

	public void removeRelations(Edge[] relations, RemoveMode removeMode) throws Exception {
		for(Edge r : relations)
			this.removeRelation(r, removeMode);
	}

}
