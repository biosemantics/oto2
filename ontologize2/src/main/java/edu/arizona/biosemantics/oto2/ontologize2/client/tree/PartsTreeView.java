package edu.arizona.biosemantics.oto2.ontologize2.client.tree;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public class PartsTreeView extends MenuTreeView {

	public PartsTreeView(EventBus eventBus) {
		super(eventBus, Type.PART_OF);
	}
	
	protected void createRelation(SubTree subTree, Edge r) {		
		/*if(r.getType().equals(Type.PART_OF)) {
			OntologyGraph g = ModelController.getCollection().getGraph();
			Vertex dest = r.getDest();
			Vertex src = r.getSrc();
			String newValue = src + " " + dest;
			
			List<Edge> parentRelations = g.getInRelations(dest, Type.PART_OF);
			parentRelations.remove(r);
			if(!parentRelations.isEmpty()) {
				for(Edge parentRelation : parentRelations) {
					Vertex parentSrc = parentRelation.getSrc();
					if(!parentSrc.equals(r.getSrc())) {
						Vertex disambiguatedDest = new Vertex(parentSrc + " " + dest);
						replace(subTree, parentSrc, dest, disambiguatedDest);
					}
				}
				
				super.createRelation(subTree, new Edge(src, new Vertex(newValue), r.getType(), r.getOrigin()));
			} else {
				super.createRelation(subTree, r);
			}
		}*/
		super.createRelation(subTree, r);
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
	
	@Override
	protected void onEdgeOnGridDrop(final Edge dropEdge, final Element element, final VertexTreeNode targetNode, final Vertex targetVertex) {
		final OntologyGraph g = ModelController.getCollection().getGraph();
		Menu menu = new Menu();

		MenuItem category = new MenuItem("Create " + Type.SUBCLASS_OF.getTargetLabel());
		category.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				fire(new CreateRelationEvent(new Edge(targetVertex, dropEdge.getDest(),Type.SUBCLASS_OF, Origin.USER)));
			}
		});
		menu.add(category);
		
		MenuItem createPart = new MenuItem("Create " + type.getTargetLabel());
		createPart.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				fire(new CreateRelationEvent(new Edge(targetVertex, dropEdge.getDest(), type, Origin.USER)));
			}
		});
		menu.add(createPart);
		
		MenuItem movePart = new MenuItem("Move " + type.getTargetLabel());
		movePart.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				fire(new ReplaceRelationEvent(dropEdge, targetVertex));
			}
		});
		menu.add(movePart);
		
		MenuItem synonym = new MenuItem("Create " + Type.SYNONYM_OF.getTargetLabel());
		synonym.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				fire(new CreateRelationEvent(new Edge(targetVertex, dropEdge.getDest(), Type.SYNONYM_OF, Origin.USER)));
			}
		});
		menu.add(synonym);
		menu.show(element, new AnchorAlignment(Anchor.TOP_LEFT, Anchor.BOTTOM_LEFT, true));
	}


}
