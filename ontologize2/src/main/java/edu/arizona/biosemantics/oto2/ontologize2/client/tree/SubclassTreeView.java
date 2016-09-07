package edu.arizona.biosemantics.oto2.ontologize2.client.tree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

import com.sencha.gxt.widget.core.client.menu.Item;

public class SubclassTreeView extends MenuTreeView {

	
	private SubTree resetSubTree;
	private Stack<SubTree> navigationStack = new Stack<SubTree>();
	private MenuItem backButton;
	protected Map<GwtEvent<?>, Set<VertexTreeNode>> visiblilityCheckNodes = new HashMap<GwtEvent<?>, Set<VertexTreeNode>>();
	
	public SubclassTreeView(EventBus eventBus) {
		super(eventBus, Type.SUBCLASS_OF);
		
		Menu menu = new Menu();
		MenuItem resetButton = new MenuItem("Reset");
		resetButton.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				MessageBox box = Alerter.startLoading();
				OntologyGraph g = ModelController.getCollection().getGraph();
				setSubTree(resetSubTree, true);
				navigationStack.removeAllElements();
				backButton.setEnabled(false);
				SubclassTreeView.this.expandRoot();
				Alerter.stopLoading(box);
			}
		});
		backButton = new MenuItem("Back");
		backButton.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				MessageBox box = Alerter.startLoading();
				OntologyGraph g = ModelController.getCollection().getGraph();
				SubTree subTree = navigationStack.pop();
				setSubTree(subTree, true);
				System.out.println();
				if(navigationStack.isEmpty())
					backButton.setEnabled(false);
				SubclassTreeView.this.expandRoot();
				Alerter.stopLoading(box);
			}
		});
		menu.add(backButton);
		menu.add(resetButton);
		
		TextButton menuButton = new TextButton("Navigate");
		menuButton.setMenu(menu);
		
		buttonBar.add(menuButton);
	}

	@Override
	protected void createRelation(Edge r) {
		if(r.getType().equals(type)) {
			if(!isVisible(r))
				return;
			
			VertexTreeNode sourceNode = null;
	 		if(subTree.getVertexNodeMap().containsKey(r.getSrc())) {
				sourceNode = subTree.getVertexNodeMap().get(r.getSrc()).iterator().next();
			} else {
				sourceNode = new VertexTreeNode(r.getSrc());
				add(null, sourceNode);
			}
	 		//create either way, to get a new id
	 		VertexTreeNode destinationNode = new VertexTreeNode(r.getDest());
	 		add(sourceNode, destinationNode);
	 		//treeGrid.setExpanded(sourceNode, true);
			
	 		if(subTree.getVertexNodeMap().get(r.getDest()).size() > 1) {
				//remove child nodes below already existings
				for(VertexTreeNode n : subTree.getVertexNodeMap().get(r.getDest())) {
					removeAllChildren(n);
				}
			}
		}
		
		if(r.getType().equals(Type.PART_OF)) {
			OntologyGraph g = ModelController.getCollection().getGraph();
			Vertex dest = r.getDest();
			Vertex src = r.getSrc();
			String newValue = src + " " + dest;
			
			List<Edge> parentRelations = g.getInRelations(dest, Type.PART_OF);
			if(!parentRelations.isEmpty()) {			
				createRelation(new Edge(g.getRoot(Type.SUBCLASS_OF), dest, Type.SUBCLASS_OF, Origin.USER));
				for(Edge parentRelation : parentRelations) {
					Vertex parentSrc = parentRelation.getSrc();
					Vertex disambiguatedDest = new Vertex(parentSrc + " " + dest);
					
					createRelation(new Edge(dest, disambiguatedDest, Type.SUBCLASS_OF, Origin.USER));
				}
				createRelation(new Edge(dest, new Vertex(newValue), Type.SUBCLASS_OF, Origin.USER));
			}
		}
	}

	private boolean isVisible(Edge r) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		Vertex currentRoot = getRoot();
		Vertex source = r.getSrc();
		if(currentRoot.equals(source))
			return true;
		if(g.getInRelations(source, type).size() > 1) 
			return false;
		for(Edge in : g.getInRelations(r.getSrc(), type)) {
			if(!isVisible(in))
				return false;
		}
		return true;
	}
	
	@Override
	protected void replaceRelation(ReplaceRelationEvent event, Edge oldRelation, Vertex newSource) {
		if(oldRelation.getType().equals(type)) {
			Edge newRelation = new Edge(newSource, oldRelation.getDest(), oldRelation.getType(), oldRelation.getOrigin());
			if(!isVisible(newRelation)) {
				removeRelation(event, oldRelation, true);
				return;
			}else
				super.replaceRelation(event, oldRelation, newSource);
		}
	}

	private void refreshNodes(Set<VertexTreeNode> nodes) {
		for(VertexTreeNode n : nodes) {
			if(subTree.getStore().findModel(n) != null)
				subTree.getStore().update(n);
		}
	}

	@Override
	protected void createFromVertex(OntologyGraph g, Vertex source) {
		Vertex currentRoot = getRoot();
		if(!currentRoot.equals(source) && g.getInRelations(source, Type.SUBCLASS_OF).size() > 1) {
			return;
		} else {
			for(Edge r : g.getOutRelations(source, type)) {
				createRelation(r);
				createFromVertex(g, r.getDest());
			}
		}
	}

	@Override
	protected void onDoubleClick(VertexTreeNode node) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		Vertex v = node.getVertex();
		if(g.getInRelations(v, Type.SUBCLASS_OF).size() > 1) {
			navigationStack.push(subTree);
			subTree = createNewSubTree();
			backButton.setEnabled(true);
			this.createFromRoot(g, v);
			this.setSubTree(subTree, false);
		}
	}
	
	@Override
	protected void onCreateRelationEffectiveInModel(Edge r) {
		super.onCreateRelationEffectiveInModel(r);
		if(r.getType().equals(type)) {
			if(!isVisible(r))
				return;
			if(subTree.getVertexNodeMap().containsKey(r.getDest()))
				refreshNodes(subTree.getVertexNodeMap().get(r.getDest()));
		}
	}
	
	@Override
	protected void removeRelation(GwtEvent<?> event, Edge r, boolean recursive) {
		if(!this.visiblilityCheckNodes.containsKey(event))
			this.visiblilityCheckNodes.put(event, new HashSet<VertexTreeNode>());
		OntologyGraph g = ModelController.getCollection().getGraph();
		if(r.getType().equals(type)) {
			if(subTree.getVertexNodeMap().containsKey(r.getSrc()) && subTree.getVertexNodeMap().containsKey(r.getDest())) {
				VertexTreeNode sourceNode = subTree.getVertexNodeMap().get(r.getSrc()).iterator().next();
				Set<VertexTreeNode> targetCandidates = subTree.getVertexNodeMap().get(r.getDest());
				VertexTreeNode targetNode = null;
				for(VertexTreeNode child : subTree.getStore().getChildren(sourceNode)) 
					if(targetCandidates.contains(child)) 
						targetNode = child;
				
				if(targetNode != null) {
					if(recursive) {
						List<VertexTreeNode> visibleNodes = new LinkedList<VertexTreeNode>();
						List<VertexTreeNode> recursiveChildren = subTree.getStore().getAllChildren(targetNode);
						for(VertexTreeNode child : recursiveChildren) 
							if(g.getInRelations(child.getVertex(), type).size() == 2)
								visibleNodes.add(child);
						visiblilityCheckNodes.get(event).addAll(visibleNodes);
						
						remove(targetNode, true);
					} else {
						List<TreeNode<VertexTreeNode>> targetChildNodes = new LinkedList<TreeNode<VertexTreeNode>>();
						List<VertexTreeNode> visibleNodes = new LinkedList<VertexTreeNode>();
						for(VertexTreeNode targetChild : subTree.getStore().getChildren(targetNode)) {
							List<Edge> inRelations = g.getInRelations(targetChild.getVertex(), type);
							if(inRelations.size() <= 1) 
								targetChildNodes.add(subTree.getStore().getSubTree(targetChild));
							if(inRelations.size() == 2) 
								visibleNodes.add(targetChild);
						}
						visiblilityCheckNodes.get(event).addAll(visibleNodes);
						
						remove(targetNode, false);
						subTree.getStore().addSubTree(sourceNode, subTree.getStore().getChildCount(sourceNode), targetChildNodes);
					}
				}
			}
		}
	}	

	@Override
	protected void onReplaceRelationEffectiveInModel(GwtEvent<?> event, Edge r, Vertex vertex) {
		super.onReplaceRelationEffectiveInModel(event, r, vertex);
		if(r.getType().equals(type)) {
			OntologyGraph g = ModelController.getCollection().getGraph();
			if(this.visiblilityCheckNodes.containsKey(event)) {
				for(VertexTreeNode visibleNode : visiblilityCheckNodes.get(event)) {
					createFromVertex(g, visibleNode.getVertex());
					refreshNodes(subTree.getVertexNodeMap().get(visibleNode.getVertex()));
				}
				visiblilityCheckNodes.remove(event);
			}
		}
	}
	
	@Override
	protected void onRemoveRelationEffectiveInModel(GwtEvent<?> event, Edge r, boolean recursive) {
		super.onRemoveRelationEffectiveInModel(event, r, recursive);
		if(r.getType().equals(type)) {
			if(!isVisible(r))
				return;
			if(subTree.getVertexNodeMap().containsKey(r.getDest()))
				refreshNodes(subTree.getVertexNodeMap().get(r.getDest()));
			
			OntologyGraph g = ModelController.getCollection().getGraph();
			if(this.visiblilityCheckNodes.containsKey(event)) {
				for(VertexTreeNode visibleNode : visiblilityCheckNodes.get(event)) {
					createFromVertex(g, visibleNode.getVertex());
					if(subTree.getVertexNodeMap().containsKey(visibleNode.getVertex()))
						refreshNodes(subTree.getVertexNodeMap().get(visibleNode.getVertex()));
				}
				visiblilityCheckNodes.remove(event);
			}
		}		
	}
	
	@Override
	protected void onLoadCollectionEffectiveInModel() {
		super.onLoadCollectionEffectiveInModel();
		OntologyGraph g = ModelController.getCollection().getGraph();
		for(Vertex v : g.getVertices()) {
			List<Edge> inRelations = g.getInRelations(v, type);
			if(inRelations.size() > 1) {
				refreshNodes(subTree.getVertexNodeMap().get(v));
			}
		}

		resetSubTree = subTree;
	}
}
