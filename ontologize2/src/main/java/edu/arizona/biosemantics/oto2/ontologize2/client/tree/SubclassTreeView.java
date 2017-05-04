package edu.arizona.biosemantics.oto2.ontologize2.client.tree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent.FilterTarget;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent.RemoveMode;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CloseRelationsEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SelectTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.UserLogEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

import com.sencha.gxt.widget.core.client.menu.Item;

public class SubclassTreeView extends MenuTreeView {
	
	protected Map<GwtEvent<?>, Set<VertexTreeNode>> visiblilityCheckNodes = new HashMap<GwtEvent<?>, Set<VertexTreeNode>>();
	
	public SubclassTreeView(EventBus eventBus) {
		super(eventBus, Type.SUBCLASS_OF);
	}


	
	@Override
	protected void createRelation(SubTree subTree, Edge r) {
		if(r.getType().equals(type)) {
			if(!isVisible(subTree, r))
				return;
			
			VertexTreeNode sourceNode = null;
	 		if(subTree.getVertexNodeMap().containsKey(r.getSrc())) {
				sourceNode = subTree.getVertexNodeMap().get(r.getSrc()).iterator().next();
			} else {
				sourceNode = new VertexTreeNode(r.getSrc());
				add(subTree, null, sourceNode);
			}
	 		//create either way, to get a new id. 
	 		//In other words if edge adds destination that already exists it is a multiple inheritance
	 		//and the same term is represented by two different nodes
	 		VertexTreeNode destinationNode = new VertexTreeNode(r.getDest());
	 		add(subTree, sourceNode, destinationNode);
	 		//treeGrid.setExpanded(sourceNode, true);
	 		
	 		if(subTree.getVertexNodeMap().get(r.getDest()).size() > 1) {
				//remove child nodes below already existings
				for(VertexTreeNode n : subTree.getVertexNodeMap().get(r.getDest())) {
					removeAllChildren(subTree, n);
				}
			}
		}
		
		/*
		if(r.getType().equals(Type.PART_OF)) {
			OntologyGraph g = ModelController.getCollection().getGraph();
			Vertex dest = r.getDest();
			Vertex src = r.getSrc();
			String newValue = src + " " + dest;
			
			List<Edge> parentRelations = g.getInRelations(dest, Type.PART_OF);
			if(!parentRelations.isEmpty()) {			
				createRelation(subTree, new Edge(g.getRoot(Type.SUBCLASS_OF), dest, Type.SUBCLASS_OF, Origin.USER));
				for(Edge parentRelation : parentRelations) {
					Vertex parentSrc = parentRelation.getSrc();
					Vertex disambiguatedDest = new Vertex(parentSrc + " " + dest);
					
					createRelation(subTree, new Edge(dest, disambiguatedDest, Type.SUBCLASS_OF, Origin.USER));
				}
				createRelation(subTree, new Edge(dest, new Vertex(newValue), Type.SUBCLASS_OF, Origin.USER));
			}
		}*/

	}

	
	/**
	 * if the source of the edge is subclasses of two superclasses, the edge is invisible.
	 * @param subTree
	 * @param r
	 * @return
	 */
	private boolean isVisible(SubTree subTree, Edge r) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		Vertex currentRoot = getRoot(subTree);
		Vertex source = r.getSrc();
		if(currentRoot.equals(source))
			return true;
		if(g.getInRelations(source, type).size() > 1) 
			return false;
		for(Edge in : g.getInRelations(source, type)) {
			if(!isVisible(subTree, in))
				return false;
		}
		return true;
	}
	
	@Override
	protected void replaceRelation(SubTree subTree, ReplaceRelationEvent event, Edge oldRelation, Vertex newSource) {
		if(oldRelation.getType().equals(type)) {
			Edge newRelation = new Edge(newSource, oldRelation.getDest(), oldRelation.getType(), oldRelation.getOrigin());
			if(!isVisible(subTree, newRelation)) {
				removeRelation(subTree, event, oldRelation, RemoveMode.RECURSIVE);
				return;
			}else
				super.replaceRelation(subTree, event, oldRelation, newSource);
		}
	}

	@Override
	protected void createFromVertex(SubTree subTree, OntologyGraph g, Vertex source) {
		Vertex currentRoot = getRoot(subTree);
		if(!currentRoot.equals(source) && g.getInRelations(source, Type.SUBCLASS_OF).size() > 1) {
			return;
		} else {
			for(Edge r : g.getOutRelations(source, type)) {
				createRelation(subTree, r);
				createFromVertex(subTree, g, r.getDest());
			}
		}
	}

	/*@Override
	protected void onDoubleClick(VertexTreeNode node) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		Vertex v = node.getVertex();
		//if(g.getInRelations(v, Type.SUBCLASS_OF).size() > 1) {
			navigationStack.push(subTree);
			SubTree subTree = createNewSubTree(true);
			backButton.setEnabled(true);
			createFromRoot(subTree, g, v);
			setSubTree(subTree, false);
		//}
	}*/
	
	@Override
	protected void onCreateRelationEffectiveInModel(Edge r) {
		super.onCreateRelationEffectiveInModel(r);
		if(r.getType().equals(type)) {
			if(!isVisible(subTree, r))
				return;
			if(subTree.getVertexNodeMap().containsKey(r.getDest()))
				refreshNodes(subTree, subTree.getVertexNodeMap().get(r.getDest()));
		}
		expandRoot();
	}
	
	@Override
	protected void removeRelation(SubTree subTree, GwtEvent<?> event, Edge r, RemoveMode removeMode) {
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
					List<VertexTreeNode> visibleNodes = new LinkedList<VertexTreeNode>();
					switch(removeMode) {
					case REATTACH_TO_AVOID_LOSS:
						List<TreeNode<VertexTreeNode>> targetChildNodes = new LinkedList<TreeNode<VertexTreeNode>>();
						List<VertexTreeNode> children = subTree.getStore().getChildren(targetNode);
						if(children.size()>0){//existed children
							for(VertexTreeNode targetChild : subTree.getStore().getChildren(targetNode)) {
								List<Edge> inRelations = g.getInRelations(targetChild.getVertex(), type);
								if(inRelations.size() <= 1) 
									targetChildNodes.add(subTree.getStore().getSubTree(targetChild));
								if(inRelations.size() >= 2) 
									visibleNodes.add(targetChild);
							}
						}
						visiblilityCheckNodes.get(event).addAll(visibleNodes);
						remove(subTree, targetNode, false);
						if(children.size()==0){//hidden children
							for(Edge edge : g.getOutRelations(targetNode.getVertex(), type)) {
								if(subTree.getVertexNodeMap().containsKey(targetNode.getVertex())) {
									VertexTreeNode newSourceNode = subTree.getVertexNodeMap().get(targetNode.getVertex()).iterator().next();
									List<Edge> inRelations = g.getInRelations(targetNode.getVertex(), type);
									if(inRelations.size()<=2){
										VertexTreeNode destinationNode = new VertexTreeNode(edge.getDest());
								 		add(subTree, newSourceNode, destinationNode);
										createFromVertex(subTree, g, edge.getDest());
									}
								}
							}
						}
						if(!targetChildNodes.isEmpty())
							subTree.getStore().addSubTree(sourceNode, subTree.getStore().getChildCount(sourceNode), targetChildNodes);
						break;
					case NONE:
						remove(subTree, targetNode, false);
						break;
					case RECURSIVE:
						List<VertexTreeNode> recursiveChildren = subTree.getStore().getAllChildren(targetNode);
						for(VertexTreeNode child : recursiveChildren) 
							if(g.getInRelations(child.getVertex(), type).size() == 2)
								visibleNodes.add(child);
						visiblilityCheckNodes.get(event).addAll(visibleNodes);
						
						remove(subTree, targetNode, true);
						break;
					default:
						break;
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
					createFromVertex(subTree, g, visibleNode.getVertex());
					refreshNodes(subTree, subTree.getVertexNodeMap().get(visibleNode.getVertex()));
				}
				visiblilityCheckNodes.remove(event);
			}
		}
		expandRoot();
	}
	
	@Override
	protected void onRemoveRelationEffectiveInModel(GwtEvent<?> event, Edge r, RemoveMode removeMode) {
		super.onRemoveRelationEffectiveInModel(event, r, removeMode);
		if(r.getType().equals(type)) {
			if(!isVisible(subTree, r))
				return;
			if(subTree.getVertexNodeMap().containsKey(r.getDest()))
				refreshNodes(subTree, subTree.getVertexNodeMap().get(r.getDest()));
			
			OntologyGraph g = ModelController.getCollection().getGraph();
			if(this.visiblilityCheckNodes.containsKey(event)) {
				for(VertexTreeNode visibleNode : visiblilityCheckNodes.get(event)) {
					createFromVertex(subTree, g, visibleNode.getVertex());
					if(subTree.getVertexNodeMap().containsKey(visibleNode.getVertex()))
						refreshNodes(subTree, subTree.getVertexNodeMap().get(visibleNode.getVertex()));
				}
				visiblilityCheckNodes.remove(event);
			}
		}	
		expandRoot();	
	}
	
	@Override
	protected void onLoad(Collection collection) {
		super.onLoad(collection);
		OntologyGraph g = ModelController.getCollection().getGraph();
		for(Vertex v : g.getVertices()) {
			List<Edge> inRelations = g.getInRelations(v, type);
			if(inRelations.size() > 1) {
				for(Edge inRelation : inRelations)
					if(isVisible(subTree, inRelation)) {
						if(subTree.getVertexNodeMap().containsKey(v)) {
							refreshNodes(subTree, subTree.getVertexNodeMap().get(v));
							continue;
						}
					}
			}
		}
		
		expandRoot();
	}
	
	@Override
	protected void onEdgeOnGridDrop(final Edge dropEdge, final Element element, final VertexTreeNode targetNode, final Vertex targetVertex) {
		final OntologyGraph g = ModelController.getCollection().getGraph();
		Menu menu = new Menu();

		MenuItem createSubclass = new MenuItem("Create " + type.getTargetLabel());
		createSubclass.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				fire(new CreateRelationEvent(new Edge(targetVertex, dropEdge.getDest(), Type.SUBCLASS_OF, Origin.USER)));
			}
		});
		menu.add(createSubclass);
		
		MenuItem moveSubclass = new MenuItem("Move " + type.getTargetLabel());
		moveSubclass.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				fire(new ReplaceRelationEvent(dropEdge, targetVertex));
			}
		});
		menu.add(moveSubclass);
		
		MenuItem createPart = new MenuItem("Create " + Type.PART_OF.getTargetLabel());
		createPart.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				fire(new CreateRelationEvent(new Edge(targetVertex, dropEdge.getDest(), Type.PART_OF, Origin.USER)));
			}
		});
		menu.add(createPart);
		
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
