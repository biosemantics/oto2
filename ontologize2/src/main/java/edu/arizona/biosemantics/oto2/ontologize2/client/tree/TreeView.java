package edu.arizona.biosemantics.oto2.ontologize2.client.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.TreeGridDragSource;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.candidate.CreateRelationsFromCandidateDialog;
import edu.arizona.biosemantics.oto2.ontologize2.client.common.BatchCreateRelationValidator;
import edu.arizona.biosemantics.oto2.ontologize2.client.common.CreateRelationValidator;
import edu.arizona.biosemantics.oto2.ontologize2.client.common.ReplaceRelationValidator;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ClearEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CloseRelationsEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.OrderEdgesEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.UserLogEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent.RemoveMode;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.VisualizationConfigurationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.VisualizationRefreshEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.CandidateTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.TextTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexCell;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexTreeNodeProperties;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

public class TreeView implements IsWidget {
	
	protected class SubTree {
		private TreeStore<VertexTreeNode> store;
		private Map<Vertex, Set<VertexTreeNode>> vertexNodeMap;
		public SubTree(TreeStore<VertexTreeNode> store, 
				Map<Vertex, Set<VertexTreeNode>> vertexNodeMap) {
			super();
			this.store = store;
			this.vertexNodeMap = vertexNodeMap;
		}
		public TreeStore<VertexTreeNode> getStore() {
			return store;
		}
		public Map<Vertex, Set<VertexTreeNode>> getVertexNodeMap() {
			return vertexNodeMap;
		}
	}
	
	protected static final VertexTreeNodeProperties vertexTreeNodeProperties = GWT.create(VertexTreeNodeProperties.class);
	
	protected EventBus eventBus;
	
	protected Type type;	
	
	protected SubTree subTree;
	protected ColumnModel<VertexTreeNode> columnModel;
	protected ColumnConfig<VertexTreeNode, Vertex> valueCol;
	protected TreeGrid<VertexTreeNode> treeGrid;

	
	public TreeView(EventBus eventBus, final Type type) {
		this.eventBus = eventBus;
		this.type = type;
				
		subTree = createNewSubTree(false);
		valueCol = new ColumnConfig<VertexTreeNode, Vertex>(vertexTreeNodeProperties.vertex(), 300, "");
		valueCol.setCell(new VertexCell(eventBus, this, type));
		valueCol.setSortable(false);
		valueCol.setMenuDisabled(true);
		valueCol.setHideable(false);
		valueCol.setGroupable(false);
		List<ColumnConfig<VertexTreeNode, ?>> list = new ArrayList<ColumnConfig<VertexTreeNode, ?>>();
		list.add(valueCol);
		columnModel = new ColumnModel<VertexTreeNode>(list);
		treeGrid = new TreeGrid<VertexTreeNode>(subTree.getStore(), columnModel, valueCol);
		//treeGrid.setIconProvider(new TermTreeNodeIconProvider());
		/*tree.setCell(new AbstractCell<PairTermTreeNode>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	PairTermTreeNode value, SafeHtmlBuilder sb) {
				sb.append(SafeHtmlUtils.fromTrustedString("<div>" + value.getText() +  "</div>"));
			}
		}); */
		treeGrid.getElement().setAttribute("source", "termsview");
		treeGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
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
		
		TreeGridDragSource<VertexTreeNode> dndSource = new TreeGridDragSource<VertexTreeNode>(treeGrid) {
			@Override
			protected void onDragStart(DndDragStartEvent event) {
				super.onDragStart(event);
				OntologyGraph g = ModelController.getCollection().getGraph();
				Tree.TreeNode<VertexTreeNode> node = treeGrid.findNode(event.getDragStartEvent().getStartElement());
				if(node != null) {
					VertexTreeNode parentNode = subTree.getStore().getParent(node.getModel());
					if(parentNode != null) {
						Vertex target = node.getModel().getVertex();	
						Vertex parent = parentNode.getVertex();
						Edge e = g.getEdge(parent, target, type);
						
						//if(inRelations.size() > 1) {
							//Alerter.showAlert("Moving", "Moving of term with more than one " + 
							//		type.getSourceLabelPlural() + " is not allowed"); // at this time
							//event.setCancelled(true);
						//	event.setData(inRelations);
						//} else if(inRelations.size() == 1)
						
						event.setData(e);
					} else {
						Alerter.showAlert("Moving", "Cannot move the root");
						event.setCancelled(true);
					}
				} else {
					event.setCancelled(true);
				}
			}
		};
		
		DropTarget dropTarget = new DropTarget(treeGrid);
		dropTarget.addDropHandler(new DndDropHandler() {
			@Override
			public void onDrop(DndDropEvent event) {
				OntologyGraph g = ModelController.getCollection().getGraph();
				Element element = event.getDragEndEvent().getNativeEvent().getEventTarget().<Element> cast();
				final Tree.TreeNode<VertexTreeNode> item = treeGrid.findNode(element);
		        if(item != null) {
		        	VertexTreeNode targetNode = item.getModel();
			        Vertex target = targetNode.getVertex();
					
		        	if(event.getData() instanceof List<?>) {
						List<?> list = (List<?>)event.getData();
						for(Object o : list) {
							if(o instanceof Candidate) {
								Candidate c = (Candidate)o;
								Vertex dest = new Vertex(c.getText());
								Edge rootEdge = new Edge(g.getRoot(type), dest, type, Origin.USER);
								if(g.existsRelation(rootEdge)) {
									fire(new ReplaceRelationEvent(rootEdge, target));
								} else {
									fire(new CreateRelationEvent(new Edge(target, dest, type, Origin.USER)));
								}
							}
						}
					} else if(event.getData() instanceof Edge) {
						Edge dropEdge = (Edge)event.getData();
						onEdgeOnGridDrop(dropEdge, element, targetNode, target);
					}
				}
			}
		});
		dropTarget.setOperation(Operation.COPY);
		dropTarget.setAllowSelfAsSource(true);
		
		bindEvents();
	}
	
	protected void onEdgeOnGridDrop(final Edge dropEdge, final Element element, final VertexTreeNode targetNode, final Vertex targetVertex) {
		
	}
	
	public void fire(GwtEvent<? extends EventHandler> e) {
		if(e instanceof CreateRelationEvent) {
			CreateRelationValidator createRelationValidator = new CreateRelationValidator(eventBus);
			createRelationValidator.validateAndFire((CreateRelationEvent)e);
		} else if(e instanceof ReplaceRelationEvent) {
			ReplaceRelationValidator replaceRelationValidator = new ReplaceRelationValidator(eventBus);
			replaceRelationValidator.validateAndFire((ReplaceRelationEvent)e);
		} else 
			eventBus.fireEvent(e);
	}
	
	protected Menu createContextMenu() {
		return new Menu();
	}

	protected void onDoubleClick(VertexTreeNode node) {

	}

	protected void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				if(event.isEffectiveInModel()) {
					TreeView.this.onLoad(event.getCollection());
				}
			}
		}); 
		eventBus.addHandler(VisualizationRefreshEvent.TYPE, new VisualizationRefreshEvent.Handler() {
			@Override
			public void onRefresh(VisualizationRefreshEvent event) {
				treeGrid.getTreeView().refresh(false);
				treeGrid.getView().refresh(false);
			}
		});
		eventBus.addHandler(ClearEvent.TYPE, new ClearEvent.Handler() {
			@Override
			public void onClear(ClearEvent event) {
				if(event.isEffectiveInModel()) {
					OntologyGraph g = ModelController.getCollection().getGraph();
					subTree = createNewSubTree(true);
					createFromRoot(subTree, g, g.getRoot(type));
					bindSubTree(subTree, false);
				}
			}
		});
		eventBus.addHandler(CreateRelationEvent.TYPE, new CreateRelationEvent.Handler() {
			@Override
			public void onCreate(CreateRelationEvent event) {
				if(!event.isEffectiveInModel())
					for(Edge r : event.getRelations()) {
						createRelation(subTree, r);
						eventBus.fireEvent(new UserLogEvent("tree_crerel_"+ type.getDisplayLabel(),r.getDest().getValue()));
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
					for(Edge r : event.getRelations()){
						removeRelation(subTree, event, r, event.getRemoveMode());
						eventBus.fireEvent(new UserLogEvent("tree_rmrel_"+ type.getDisplayLabel(),r.getDest().getValue()));
					}
				else
					for(Edge r : event.getRelations()) 
						onRemoveRelationEffectiveInModel(event, r, event.getRemoveMode());
			}
		});
		eventBus.addHandler(ReplaceRelationEvent.TYPE, new ReplaceRelationEvent.Handler() {
			@Override
			public void onReplace(ReplaceRelationEvent event) {
				if(!event.isEffectiveInModel()) {
					replaceRelation(subTree, event, event.getOldRelation(), event.getNewSource());
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
		eventBus.addHandler(CloseRelationsEvent.TYPE, new CloseRelationsEvent.Handler() {
			@Override
			public void onClose(CloseRelationsEvent event) {
				if(!event.isEffectiveInModel()) {
				} else {
					onCloseRelationsEffectiveInModel(event.getVertex(), event.getType());
				}
			}
		});
		
	}
	
	protected void onLoad(Collection collection) {
		subTree = createNewSubTree(false);
		OntologyGraph g = collection.getGraph();
		Vertex root = g.getRoot(type);
		createFromRoot(subTree, g, root);
		bindSubTree(subTree, false);
		TreeView.this.expandRoot();
	}
	
	protected void onCloseRelationsEffectiveInModel(Vertex v, Type type) {
		if(this.type.equals(type)) {
			if(subTree.getVertexNodeMap().containsKey(v)) {
				refreshNodes(subTree, subTree.getVertexNodeMap().get(v));
			}
		}
	}

	protected void onOrderEffectiveInModel(OrderEdgesEvent event, Vertex v, List<Edge> edges, Type type) {
		if(this.type.equals(type)) {
			if(subTree.getVertexNodeMap().containsKey(v)) {
				refreshNodes(subTree, subTree.getVertexNodeMap().get(v));
			}
		}
		
		expandRoot();
	}

	protected void onOrder(OrderEdgesEvent event, Vertex src, List<Edge> edges, Type type) {
		
	}
	

	
	protected void replaceRelation(SubTree subTree, ReplaceRelationEvent event, Edge oldRelation, Vertex newSource) {
		if(oldRelation.getType().equals(type)) {
			OntologyGraph g = ModelController.getCollection().getGraph();
			VertexTreeNode newSourceNode = null;
			if(subTree.getVertexNodeMap().containsKey(newSource)) {
				newSourceNode = subTree.getVertexNodeMap().get(newSource).iterator().next();
			} else {
				newSourceNode = new VertexTreeNode(newSource);
				this.add(subTree, null, newSourceNode);
			}
			
			VertexTreeNode targetNode = null;
			if(subTree.getVertexNodeMap().containsKey(oldRelation.getDest())) {
				targetNode = subTree.getVertexNodeMap().get(oldRelation.getDest()).iterator().next();
			} else {
				targetNode = new VertexTreeNode(oldRelation.getDest());
				this.add(subTree, null, targetNode);
			}
						
			if(subTree.getStore().findModel(newSourceNode) == null) {
				subTree.getStore().add(newSourceNode);
			}
			List<TreeNode<VertexTreeNode>> targetNodes = new LinkedList<TreeNode<VertexTreeNode>>();
			if(subTree.getStore().findModel(targetNode) != null) {
				targetNodes.add(subTree.getStore().getSubTree(targetNode));
				subTree.getStore().remove(targetNode);
				subTree.getStore().addSubTree(newSourceNode, subTree.getStore().getChildCount(newSourceNode), targetNodes);
			} else {
				subTree.getStore().add(newSourceNode, targetNode);
			}
		}
		
	}

	protected void onReplaceRelationEffectiveInModel(GwtEvent<?> event, Edge relation, Vertex vertex) {
		expandRoot();
	}

	protected void onRemoveRelationEffectiveInModel(GwtEvent<?> event, Edge r, RemoveMode removeMode) {
		expandRoot();
	}

	protected void onCreateRelationEffectiveInModel(Edge r) {
		expandRoot();
	}

	protected void createFromRoot(SubTree subTree, OntologyGraph g, Vertex root) {
		VertexTreeNode rootNode = new VertexTreeNode(root);
		add(subTree, null, rootNode);
		createFromVertex(subTree, g, root);
	}

	protected SubTree createNewSubTree(boolean setFilterAndSort) {
		//filter and sort info has to be maintained from previous store
		TreeStore<VertexTreeNode> store = new TreeStore<VertexTreeNode>(vertexTreeNodeProperties.key());
		store.setAutoCommit(true);
		
		if(setFilterAndSort) {
			if(this.subTree != null) {
				if(this.subTree.getStore().isFiltered())
					store.addFilter(this.subTree.getStore().getFilters().iterator().next());
				if(!this.subTree.getStore().getSortInfo().isEmpty())
					store.addSortInfo(this.subTree.getStore().getSortInfo().get(0));
			}
		}
		
		Map<Vertex, Set<VertexTreeNode>> vertexNodeMap = new HashMap<Vertex, Set<VertexTreeNode>>();
		return new SubTree(store, vertexNodeMap);
	}
	
	protected void bindSubTree(SubTree subTree, boolean setFilterAndSort) {
		if(setFilterAndSort) {
			StoreFilter<VertexTreeNode> filter = null;
			StoreSortInfo<VertexTreeNode> sortInfo = null;
			if(this.subTree != null) {
				if(this.subTree.getStore().isFiltered())
					filter = this.subTree.getStore().getFilters().iterator().next();
				if(!this.subTree.getStore().getSortInfo().isEmpty())
					sortInfo = this.subTree.getStore().getSortInfo().get(0);
			}
			this.subTree = subTree;
			this.subTree.getStore().removeFilters();
			this.subTree.getStore().clearSortInfo();
			if(filter != null) {
				this.subTree.getStore().addFilter(filter);
			} 
			if(sortInfo != null) {
				this.subTree.getStore().addSortInfo(sortInfo);
			}
		} else {
			this.subTree = subTree;
		}
		this.treeGrid.reconfigure(this.subTree.getStore(), columnModel, valueCol);
	}

	protected void createFromVertex(SubTree subTree, OntologyGraph g, Vertex source) {
		for(Edge r : g.getOutRelations(source, type)) {
			createRelation(subTree, r);
			createFromVertex(subTree, g, r.getDest());
		}
	}

	protected void removeCandidate(Candidate c) {
		/*Vertex possibleVertex = new Vertex(c.getText());
		if(subTree.getVertexNodeMap().containsKey(possibleVertex)) {
			for(VertexTreeNode node : subTree.getVertexNodeMap().get(possibleVertex))
				remove(node);
		}*/
	}

	protected void createRelation(SubTree subTree, Edge r) {
		if(r.getType().equals(type)) {
			VertexTreeNode sourceNode = null;
	 		if(subTree.getVertexNodeMap().containsKey(r.getSrc())) {
				sourceNode = subTree.getVertexNodeMap().get(r.getSrc()).iterator().next();
			} else {
				sourceNode = new VertexTreeNode(r.getSrc());
				add(subTree, null, sourceNode);
			}
			if(subTree.getVertexNodeMap().containsKey(r.getDest())) {
				Alerter.showAlert("Failed to create relation", "Failed to create relation");
				return;
			}
			
			VertexTreeNode destinationNode = new VertexTreeNode(r.getDest());
			add(subTree, sourceNode, destinationNode);
			//if(treeGrid.isRendered())
			//	treeGrid.setExpanded(sourceNode, true);
		}
	}
	
	protected void removeRelation(SubTree subTree, GwtEvent<?> event, Edge r, RemoveMode removeMode) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		if(r.getType().equals(type)) {
			if(subTree.getVertexNodeMap().containsKey(r.getSrc()) && subTree.getVertexNodeMap().containsKey(r.getDest())) {
				VertexTreeNode sourceNode = subTree.getVertexNodeMap().get(r.getSrc()).iterator().next();
				VertexTreeNode targetNode = subTree.getVertexNodeMap().get(r.getDest()).iterator().next();
				
				eventBus.fireEvent(new UserLogEvent("grid_rmrel_"+ type.getDisplayLabel(),targetNode.getText()));
				
				switch(removeMode) {
				case REATTACH_TO_AVOID_LOSS:
					List<TreeNode<VertexTreeNode>> targetChildNodes = new LinkedList<TreeNode<VertexTreeNode>>();
					for(VertexTreeNode targetChild : subTree.getStore().getChildren(targetNode)) {
						List<Edge> inRelations = g.getInRelations(targetChild.getVertex(), type);
						if(inRelations.size() <= 1) 
							targetChildNodes.add(subTree.getStore().getSubTree(targetChild));
					}
					remove(subTree, targetNode, false);
					subTree.getStore().addSubTree(sourceNode, subTree.getStore().getChildCount(sourceNode), targetChildNodes);
					break;
				case NONE:
					remove(subTree, targetNode, false);
					break;
				case RECURSIVE:
					remove(subTree, targetNode, true);
					break;
				default:
					break;
				}
			}
		}
	}

	protected void clearTree(SubTree subTree) {
		subTree.getStore().clear();
		subTree.getVertexNodeMap().clear();
	}
	
	protected void replaceNode(SubTree subTree, VertexTreeNode oldNode, VertexTreeNode newNode) {
		List<TreeNode<VertexTreeNode>> childNodes = new LinkedList<TreeNode<VertexTreeNode>>();
		for(VertexTreeNode childNode : subTree.getStore().getChildren(oldNode)) {
			childNodes.add(subTree.getStore().getSubTree(childNode));
		}
		
		VertexTreeNode parent = subTree.getStore().getParent(oldNode);
		remove(subTree, oldNode, false);
		add(subTree, parent, newNode);
		subTree.getStore().addSubTree(newNode, 0, childNodes);
	}
	
	protected void remove(SubTree subTree, VertexTreeNode node, boolean removeChildren) {
		if(removeChildren)
			removeAllChildren(subTree, node);
		subTree.getStore().remove(node);
		if(subTree.getVertexNodeMap().containsKey(node.getVertex())) {
			subTree.getVertexNodeMap().get(node.getVertex()).remove(node);
			if(subTree.getVertexNodeMap().get(node.getVertex()).isEmpty())
				subTree.getVertexNodeMap().remove(node.getVertex());
		}
	}
	
	protected void removeAllChildren(SubTree subTree, VertexTreeNode frommNode) {
		List<VertexTreeNode> allRemoves = subTree.getStore().getAllChildren(frommNode);
		for(VertexTreeNode remove : allRemoves) {
			Vertex v = remove.getVertex();
			if(subTree.getVertexNodeMap().containsKey(v)) {
				subTree.getVertexNodeMap().get(v).remove(remove);
				if(subTree.getVertexNodeMap().get(v).isEmpty()) 
					subTree.getVertexNodeMap().remove(v);
			}
		}
		subTree.getStore().removeChildren(frommNode);
	}
	
	protected void add(SubTree subTree, VertexTreeNode parent, VertexTreeNode child) {
		if(parent == null)
			subTree.getStore().add(child);
		else
			subTree.getStore().add(parent, child);
		if(!subTree.getVertexNodeMap().containsKey(child.getVertex()))
			subTree.getVertexNodeMap().put(child.getVertex(), new HashSet<VertexTreeNode>(Arrays.asList(child)));
		else {
			subTree.getVertexNodeMap().get(child.getVertex()).add(child);
		}
	}

	protected Vertex getRoot(SubTree subTree) {
		return subTree.getStore().getRootItems().get(0).getVertex();
	}

	@Override
	public Widget asWidget() {
		return treeGrid;
	}
	
	protected void expandAll() {
		/*List<VertexTreeNode> roots = treeGrid.getTreeStore().getRootItems();
		if(!roots.isEmpty())
			treeGrid.setExpanded(roots.get(0), true, true);*/
	}
	
	protected void expandRoot() {
		List<VertexTreeNode> roots = treeGrid.getTreeStore().getRootItems();
		if(!roots.isEmpty())
			treeGrid.setExpanded(roots.get(0), true, false);		
	}
	
	protected void refreshNodes(SubTree subTree, Set<VertexTreeNode> nodes) {
		for(VertexTreeNode n : nodes) {
			if(subTree.getStore().findModel(n) != null)
				subTree.getStore().update(n);
		}
	}
}
