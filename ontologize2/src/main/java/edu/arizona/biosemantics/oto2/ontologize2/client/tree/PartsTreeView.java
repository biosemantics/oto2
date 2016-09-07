package edu.arizona.biosemantics.oto2.ontologize2.client.tree;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.event.shared.EventBus;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public class PartsTreeView extends MenuTreeView {

	public PartsTreeView(EventBus eventBus) {
		super(eventBus, Type.PART_OF);
	}
	
	protected void createRelation(SubTree subTree, Edge r) {		
		if(r.getType().equals(Type.PART_OF)) {
			OntologyGraph g = ModelController.getCollection().getGraph();
			Vertex dest = r.getDest();
			Vertex src = r.getSrc();
			String newValue = src + " " + dest;
			
			List<Edge> parentRelations = g.getInRelations(dest, Type.PART_OF);
			if(!parentRelations.isEmpty()) {
				boolean disambiguate = false;
				for(Edge parentRelation : parentRelations) {
					Vertex parentSrc = parentRelation.getSrc();
					if(!parentSrc.equals(r.getSrc())) {
						Vertex disambiguatedDest = new Vertex(parentSrc + " " + dest);
						replace(subTree, parentSrc, dest, disambiguatedDest);
						disambiguate = true;
					}
				}
				
				if(disambiguate)
					super.createRelation(subTree, new Edge(src, new Vertex(newValue), r.getType(), r.getOrigin()));
				else
					super.createRelation(subTree, r);
			} else {
				super.createRelation(subTree, r);
			}
		}
	}

	private void replace(SubTree subTree, Vertex parent, Vertex vertex, Vertex newVertex) {
		if(subTree.getVertexNodeMap().containsKey(vertex)) {
			VertexTreeNode destNode = subTree.getVertexNodeMap().get(vertex).iterator().next();
			VertexTreeNode newDestNode = new VertexTreeNode(newVertex);
			
			replaceNode(subTree, destNode, newDestNode);
			
			subTree.getVertexNodeMap().put(newVertex, new HashSet<VertexTreeNode>(Arrays.asList(newDestNode)));
			subTree.getVertexNodeMap().remove(vertex);
			
			for(Edge r : ModelController.getCollection().getGraph().getOutRelations(vertex, Type.PART_OF)) {
				if(r.getDest().getValue().startsWith(vertex.getValue())) {
					replace(subTree, vertex, r.getDest(), new Vertex(newVertex.getValue() + " " + r.getDest().getValue()));
				}
			}
		}
	}


}
