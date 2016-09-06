package edu.arizona.biosemantics.oto2.ontologize2.client.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ClearEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent.FilterTarget;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.OrderEdgesEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SelectTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexCell;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexTreeNodeProperties;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

public class TreeView implements IsWidget {
	
	private static final VertexTreeNodeProperties vertexTreeNodeProperties = GWT.create(VertexTreeNodeProperties.class);
	
	protected EventBus eventBus;
	
	protected Type type;	
	
	protected TreeStore<VertexTreeNode> store;
	protected Map<Vertex, Set<VertexTreeNode>> vertexNodeMap = new HashMap<Vertex, Set<VertexTreeNode>>();
	protected TreeGrid<VertexTreeNode> treeGrid;
	
	public TreeView(EventBus eventBus, Type type) {
		this.eventBus = eventBus;
		this.type = type;
				
		store = new TreeStore<VertexTreeNode>(vertexTreeNodeProperties.key());
		ColumnConfig<VertexTreeNode, Vertex> valueCol = new ColumnConfig<VertexTreeNode, Vertex>(vertexTreeNodeProperties.vertex(), 300, "");
		valueCol.setCell(new VertexCell(eventBus, this, type));
		valueCol.setSortable(false);
		valueCol.setMenuDisabled(true);
		valueCol.setHideable(false);
		valueCol.setGroupable(false);
		List<ColumnConfig<VertexTreeNode, ?>> list = new ArrayList<ColumnConfig<VertexTreeNode, ?>>();
		list.add(valueCol);
		ColumnModel<VertexTreeNode> cm = new ColumnModel<VertexTreeNode>(list);
		treeGrid = new TreeGrid<VertexTreeNode>(store, cm, valueCol);
		store.setAutoCommit(true);
		//treeGrid.setIconProvider(new TermTreeNodeIconProvider());
		/*tree.setCell(new AbstractCell<PairTermTreeNode>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	PairTermTreeNode value, SafeHtmlBuilder sb) {
				sb.append(SafeHtmlUtils.fromTrustedString("<div>" + value.getText() +  "</div>"));
			}
		}); */
		treeGrid.getElement().setAttribute("source", "termsview");
		treeGrid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
		//treeGrid.setAutoExpand(true);
		treeGrid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellClick(CellDoubleClickEvent event) {
				Tree.TreeNode<VertexTreeNode> node = treeGrid.findNode(treeGrid.getTreeView().getRow(event.getRowIndex()));
				VertexTreeNode vertexNode = node.getModel();
				onDoubleClick(vertexNode);
			}
		});
		treeGrid.setContextMenu(createContextMenu());
		
		bindEvents();
	}
	
	protected Menu createContextMenu() {
		return new Menu();
	}

	protected void onDoubleClick(VertexTreeNode vertexNode) {
		// TODO Auto-generated method stub
		
	}

	protected void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				if(!event.isEffectiveInModel()) {
					OntologyGraph g = event.getCollection().getGraph();
					Vertex root = g.getRoot(type);
					createFromRoot(g, root);
				} else {
					onLoadCollectionEffectiveInModel();
				}
			}
		}); 
		eventBus.addHandler(ClearEvent.TYPE, new ClearEvent.Handler() {
			@Override
			public void onClear(ClearEvent event) {
				if(event.isEffectiveInModel()) {
					OntologyGraph g = ModelController.getCollection().getGraph();
					createFromRoot(g, g.getRoot(type));
				}
			}
		});
		eventBus.addHandler(CreateRelationEvent.TYPE, new CreateRelationEvent.Handler() {
			@Override
			public void onCreate(CreateRelationEvent event) {
				if(!event.isEffectiveInModel())
					for(Edge r : event.getRelations()) {
						createRelation(r);
					}
				else
					for(Edge r : event.getRelations())
						onCreateRelationEffectiveInModel(r);
			}
		});
		eventBus.addHandler(RemoveRelationEvent.TYPE, new RemoveRelationEvent.Handler() {
			@Override
			public void onRemove(RemoveRelationEvent event) {
				if(!event.isEffectiveInModel())
					for(Edge r : event.getRelations())
						removeRelation(event, r, event.isRecursive());
				else
					for(Edge r : event.getRelations()) 
						onRemoveRelationEffectiveInModel(event, r, event.isRecursive());
			}
		});
		eventBus.addHandler(ReplaceRelationEvent.TYPE, new ReplaceRelationEvent.Handler() {
			@Override
			public void onReplace(ReplaceRelationEvent event) {
				if(!event.isEffectiveInModel()) {
					replaceRelation(event, event.getOldRelation(), event.getNewSource());
				} else {
					onReplaceRelationEffectiveInModel(event, event.getOldRelation(), event.getNewSource());
				}
			}
		});
		/*eventBus.addHandler(RemoveCandidateEvent.TYPE, new RemoveCandidateEvent.Handler() {
			@Override
			public void onRemove(RemoveCandidateEvent event) {
				for(Candidate c : event.getCandidates()) 
					removeCandidate(c);
			}
		});*/
		eventBus.addHandler(OrderEdgesEvent.TYPE, new OrderEdgesEvent.Handler() {
			@Override
			public void onOrder(OrderEdgesEvent event) {
				if(!event.isEffectiveInModel()) {
					TreeView.this.onOrder(event, event.getSrc(), event.getEdges(), event.getType());
				} else {
					TreeView.this.onOrderEffectiveInModel(event, event.getSrc(), event.getEdges(), event.getType());
				}
			}
		});
		
	}
	
	protected void onOrderEffectiveInModel(OrderEdgesEvent event, Vertex src, List<Edge> edges, Type type) {
		
	}

	protected void onOrder(OrderEdgesEvent event, Vertex src, List<Edge> edges, Type type) {
		
	}
	
	protected void replaceRelation(ReplaceRelationEvent event, Edge oldRelation, Vertex newSource) {
		if(oldRelation.getType().equals(type)) {
			if(vertexNodeMap.containsKey(oldRelation.getDest()) && vertexNodeMap.containsKey(newSource)) {
				VertexTreeNode targetNode = vertexNodeMap.get(oldRelation.getDest()).iterator().next();
				VertexTreeNode newSourceNode = vertexNodeMap.get(newSource).iterator().next();
				
				List<TreeNode<VertexTreeNode>> targetNodes = new LinkedList<TreeNode<VertexTreeNode>>();
				targetNodes.add(store.getSubTree(targetNode));
				store.remove(targetNode);
				store.addSubTree(newSourceNode, store.getChildCount(newSourceNode), targetNodes);
			}
		}
	}

	protected void onReplaceRelationEffectiveInModel(GwtEvent<?> event, Edge relation, Vertex vertex) {
		
	}

	protected void onLoadCollectionEffectiveInModel() {
		
	}

	protected void onRemoveRelationEffectiveInModel(GwtEvent<?> event, Edge r, boolean recursive) {
		
	}

	protected void onCreateRelationEffectiveInModel(Edge r) {
		// TODO Auto-generated method stub
		
	}

	protected void createFromRoot(OntologyGraph g, Vertex root) {
		clearTree();
		VertexTreeNode rootNode = new VertexTreeNode(root);
		add(null, rootNode);
		createFromVertex(g, root);
	}
	


	protected void createFromVertex(OntologyGraph g, Vertex source) {
		for(Edge r : g.getOutRelations(source, type)) {
			createRelation(r);
			createFromVertex(g, r.getDest());
		}
	}

	protected void removeCandidate(Candidate c) {
		/*Vertex possibleVertex = new Vertex(c.getText());
		if(vertexNodeMap.containsKey(possibleVertex)) {
			for(VertexTreeNode node : vertexNodeMap.get(possibleVertex))
				remove(node);
		}*/
	}

	protected void createRelation(Edge r) {
		if(r.getType().equals(type)) {
			VertexTreeNode sourceNode = null;
	 		if(vertexNodeMap.containsKey(r.getSrc())) {
				sourceNode = vertexNodeMap.get(r.getSrc()).iterator().next();
			} else {
				sourceNode = new VertexTreeNode(r.getSrc());
				add(null, sourceNode);
			}
			if(vertexNodeMap.containsKey(r.getDest())) {
				Alerter.showAlert("Failed to create relation", "Failed to create relation");
				return;
			}
			VertexTreeNode destinationNode = new VertexTreeNode(r.getDest());
			add(sourceNode, destinationNode);
			//if(treeGrid.isRendered())
			//	treeGrid.setExpanded(sourceNode, true);
		}
	}
	
	protected void removeRelation(GwtEvent<?> event, Edge r, boolean recursive) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		if(r.getType().equals(type)) {
			if(vertexNodeMap.containsKey(r.getSrc()) && vertexNodeMap.containsKey(r.getDest())) {
				VertexTreeNode sourceNode = vertexNodeMap.get(r.getSrc()).iterator().next();
				VertexTreeNode targetNode = vertexNodeMap.get(r.getDest()).iterator().next();
				if(recursive) {
					remove(targetNode, true);
				} else {
					List<TreeNode<VertexTreeNode>> targetChildNodes = new LinkedList<TreeNode<VertexTreeNode>>();
					for(VertexTreeNode targetChild : store.getChildren(targetNode)) {
						List<Edge> inRelations = g.getInRelations(targetChild.getVertex(), type);
						if(inRelations.size() <= 1) 
							targetChildNodes.add(store.getSubTree(targetChild));
					}
					remove(targetNode, false);
					store.addSubTree(sourceNode, store.getChildCount(sourceNode), targetChildNodes);
				}
			}
		}
	}

	protected void clearTree() {
		store.clear();
		vertexNodeMap.clear();
	}
	
	protected void replaceNode(VertexTreeNode oldNode, VertexTreeNode newNode) {
		List<TreeNode<VertexTreeNode>> childNodes = new LinkedList<TreeNode<VertexTreeNode>>();
		for(VertexTreeNode childNode : store.getChildren(oldNode)) {
			childNodes.add(store.getSubTree(childNode));
		}
		
		VertexTreeNode parent = store.getParent(oldNode);
		remove(oldNode, false);
		add(parent, newNode);
		store.addSubTree(newNode, 0, childNodes);
	}
	
	protected void remove(VertexTreeNode node, boolean removeChildren) {
		if(removeChildren)
			removeAllChildren(node);
		store.remove(node);
		if(vertexNodeMap.containsKey(node.getVertex())) {
			vertexNodeMap.get(node.getVertex()).remove(node);
			if(vertexNodeMap.get(node.getVertex()).isEmpty())
				vertexNodeMap.remove(node.getVertex());
		}
	}
	
	protected void removeAllChildren(VertexTreeNode frommNode) {
		List<VertexTreeNode> allRemoves = store.getAllChildren(frommNode);
		for(VertexTreeNode remove : allRemoves) {
			Vertex v = remove.getVertex();
			if(vertexNodeMap.containsKey(v)) {
				vertexNodeMap.get(v).remove(remove);
				if(vertexNodeMap.get(v).isEmpty()) 
					vertexNodeMap.remove(v);
			}
		}
		store.removeChildren(frommNode);
	}
	
	protected void add(VertexTreeNode parent, VertexTreeNode child) {
		if(parent == null)
			store.add(child);
		else
			store.add(parent, child);
		if(!vertexNodeMap.containsKey(child.getVertex()))
			vertexNodeMap.put(child.getVertex(), new HashSet<VertexTreeNode>(Arrays.asList(child)));
		else {
			vertexNodeMap.get(child.getVertex()).add(child);
		}
	}

	protected Vertex getRoot() {
		return store.getRootItems().get(0).getVertex();
	}

	@Override
	public Widget asWidget() {
		return treeGrid;
	}
}
