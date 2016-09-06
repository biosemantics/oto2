package edu.arizona.biosemantics.oto2.ontologize2.client.tree;

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
import java.util.Random;
import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ToStringValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.HeaderDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.HeaderDoubleClickEvent.HeaderDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent.RowDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.StoreFilterField;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.candidate.TermTreeNodeIconProvider;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ClearEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.OrderEdgesEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveCandidateEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SelectTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent.FilterTarget;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SortEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SortEvent.SortField;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SortEvent.SortTarget;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.TextTreeNodeProperties;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexCell;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexTreeNodeProperties;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.oto.client.event.LoadEvent;

public class TreeView extends SimpleContainer {
	
	private static final VertexTreeNodeProperties vertexTreeNodeProperties = GWT.create(VertexTreeNodeProperties.class);
	
	protected EventBus eventBus;
	
	protected Type type;	
	
	protected TreeStore<VertexTreeNode> store;
	protected Map<Vertex, Set<VertexTreeNode>> vertexNodeMap = new HashMap<Vertex, Set<VertexTreeNode>>();
	protected TreeGrid<VertexTreeNode> treeGrid;
	
	protected ToolBar buttonBar;
	
	private CheckMenuItem checkFilterItem;
	private TextField filterGridField;
	private TextField filterTreeField;
	private TextField filterGridAndTreeField;
	private DelayedTask filterGridTask = new DelayedTask() {		
		@Override
		public void onExecute() {
			eventBus.fireEvent(new FilterEvent(filterGridField.getText(), FilterTarget.GRID, type));
		}
	};
	private DelayedTask filterTreeTask = new DelayedTask() {		
		@Override
		public void onExecute() {
			eventBus.fireEvent(new FilterEvent(filterTreeField.getText(), FilterTarget.TREE, type));
		}
	};
	private DelayedTask filterGridAndTreeTask = new DelayedTask() {		
		@Override
		public void onExecute() {
			eventBus.fireEvent(new FilterEvent(filterGridAndTreeField.getText(), FilterTarget.GRID_AND_TREE, type));
		}
	};
	
	private Comparator<VertexTreeNode> creationComparator = new Comparator<VertexTreeNode>() {
		@Override
		public int compare(VertexTreeNode o1, VertexTreeNode o2) {
			OntologyGraph g = ModelController.getCollection().getGraph();
			Date d1 = getCreationDate(o1.getVertex());
			Date d2 = getCreationDate(o2.getVertex());
			
			VertexTreeNode p1 = store.getParent(o1);
			VertexTreeNode p2 = store.getParent(o2);
			if(p1 != null && p2 != null && p1.equals(p2)) {
				if(g.hasOrderedEdges(p1.getVertex(), type)) {
					List<Edge> orderedEdges = g.getOrderedEdges(p1.getVertex(), type);
					return getIndex(o2.getVertex(), orderedEdges) - getIndex(o1.getVertex(), orderedEdges);
				} else {
					return d1.compareTo(d2);
				}
			}
			return d1.compareTo(d2);
		}
		
		private int getIndex(Vertex vertex, List<Edge> orderedEdges) {
			for(int i=0; i<orderedEdges.size(); i++) {
				Edge orderedEdge = orderedEdges.get(i);
				if(orderedEdge.getDest().equals(vertex))
					return i;
			}
			return -1;
		}

		protected Date getCreationDate(Vertex v) {
			OntologyGraph g = ModelController.getCollection().getGraph();
			Date result = new Date();
			for(Edge in : g.getInRelations(v)) {
				if(in.getCreation().compareTo(result) < 0)
					result = in.getCreation();
			}
			return result;
		}
	};
	private Comparator<VertexTreeNode> nameComparator = new Comparator<VertexTreeNode>() {
		@Override
		public int compare(VertexTreeNode o1, VertexTreeNode o2) {
			OntologyGraph g = ModelController.getCollection().getGraph();			
			VertexTreeNode p1 = store.getParent(o1);
			VertexTreeNode p2 = store.getParent(o2);
			if(p1 != null && p2 != null && p1.equals(p2)) {
				if(g.hasOrderedEdges(p1.getVertex(), type)) {
					List<Edge> orderedEdges = g.getOrderedEdges(p1.getVertex(), type);
					return orderedEdges.indexOf(o2.getVertex()) - orderedEdges.indexOf(o1.getVertex());
				} else {
					return o1.getVertex().compareTo(o2.getVertex());
				}
			}			
			return o1.getVertex().compareTo(o2.getVertex());
		}
	};

	
	public TreeView(EventBus eventBus, Type type) {
		this.eventBus = eventBus;
		this.type = type;
		
		buttonBar = new ToolBar();
		buttonBar.add(createFilterMenu());
		
		/*Label titleLabel = new Label(type.getTreeLabel());
		buttonBar.add(titleLabel);
		titleLabel.getElement().getStyle().setFontSize(11, Unit.PX);
		titleLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		titleLabel.getElement().getStyle().setPaddingLeft(3, Unit.PX);
		titleLabel.getElement().getStyle().setColor("#15428b");
		*/
		
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
		
		buttonBar.add(createSortMenu());
		
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
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add(buttonBar, new VerticalLayoutData(1, -1));
		vlc.add(treeGrid, new VerticalLayoutData(1, 1));
		this.setWidget(vlc);
		
		bindEvents();
	}

	private Widget createFilterMenu() {
		TextButton filterButton = new TextButton("Filter");
		final Menu menu = new Menu();
		checkFilterItem = new CheckMenuItem(DefaultMessages.getMessages().gridFilters_filterText());
		menu.add(checkFilterItem);
		
		final Menu filterMenu = new Menu();
		
		MenuItem filterGridItem = new MenuItem("Grid");
		Menu filterGridMenu = new Menu();
		filterGridItem.setSubMenu(filterGridMenu);
		filterGridField = new TextField() {
			protected void onKeyUp(Event event) {
				super.onKeyUp(event);
				int key = event.getKeyCode();
				if (key == KeyCodes.KEY_ENTER) {
					event.stopPropagation();
					event.preventDefault();
					filterMenu.hide(true);
					eventBus.fireEvent(new FilterEvent(filterGridField.getText(), FilterTarget.GRID, type));
				}
				filterGridTask.delay(500);
			}
		};
		filterGridMenu.add(filterGridField);
		filterMenu.add(filterGridItem);
		
		MenuItem filterTreeItem = new MenuItem("Tree");
		Menu filterTreeMenu = new Menu();
		filterTreeItem.setSubMenu(filterTreeMenu);
		filterTreeField = new TextField() {
			protected void onKeyUp(Event event) {
				super.onKeyUp(event);
				int key = event.getKeyCode();
				if (key == KeyCodes.KEY_ENTER) {
					event.stopPropagation();
					event.preventDefault();
					filterMenu.hide(true);
					eventBus.fireEvent(new FilterEvent(filterTreeField.getText(), FilterTarget.TREE, type));
				}
				filterTreeTask.delay(500);
			}
		};
		filterTreeMenu.add(filterTreeField);
		filterMenu.add(filterTreeItem);
		
		MenuItem filterGridAndTreeItem = new MenuItem("Grid and Tree");
		Menu filterGridAndTreeMenu = new Menu();
		filterGridAndTreeItem.setSubMenu(filterGridAndTreeMenu);
		filterGridAndTreeField = new TextField() {
			protected void onKeyUp(Event event) {
				super.onKeyUp(event);
				int key = event.getKeyCode();
				if (key == KeyCodes.KEY_ENTER) {
					event.stopPropagation();
					event.preventDefault();
					filterMenu.hide(true);
					eventBus.fireEvent(new FilterEvent(filterGridAndTreeField.getText(), FilterTarget.GRID_AND_TREE, type));
				}
				filterGridAndTreeTask.delay(500);
			}
		};
		filterGridAndTreeMenu.add(filterGridAndTreeField);
		filterMenu.add(filterGridAndTreeItem);
		
		
		checkFilterItem.setSubMenu(filterMenu);
		checkFilterItem.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {
	        @Override
	        public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
	        	if(event.getItem().isChecked())
	        		eventBus.fireEvent(new FilterEvent(filterTreeField.getText(), FilterTarget.TREE, type));
	        	else
	        		eventBus.fireEvent(new FilterEvent("", FilterTarget.TREE, type));
	        }
	    });
		filterButton.setMenu(menu);
		return filterButton;
	}

	private Widget createSortMenu() {
		TextButton sortButton = new TextButton("Sort");
		final Menu sortMenu = new Menu();
		sortButton.setMenu(sortMenu);
		for(final SortTarget sortTarget : SortEvent.SortTarget.values()) {
			//if(!sortTarget.equals(SortTarget.GRID)) {
				MenuItem targetItem = new MenuItem(sortTarget.getDisplayName());
				sortMenu.add(targetItem);
				Menu targetMenu = new Menu();
				targetItem.setSubMenu(targetMenu);
				for(final SortField sortField : SortEvent.SortField.values()) {
					MenuItem fieldItem = new MenuItem("By " + sortField.getDisplayName());
					targetMenu.add(fieldItem);
					Menu fieldMenu = new Menu();
					fieldItem.setSubMenu(fieldMenu);
					MenuItem creationAscButton = new MenuItem("Ascending");
					MenuItem creationDescButton = new MenuItem("Descending");
					fieldMenu.add(creationAscButton);
					fieldMenu.add(creationDescButton);
					
					creationAscButton.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							eventBus.fireEvent(new SortEvent(sortField, SortDir.ASC, sortTarget, type));
						}
					});
					creationDescButton.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							eventBus.fireEvent(new SortEvent(sortField, SortDir.DESC, sortTarget, type));
						}
					});
				}
			//}
		}
		return sortButton;
	}

	private Menu createContextMenu() {
		final Menu menu = new Menu();
		
		menu.addBeforeShowHandler(new BeforeShowHandler() {
			@Override
			public void onBeforeShow(BeforeShowEvent event) {
				menu.clear();
				if(!treeGrid.getSelectionModel().getSelectedItems().isEmpty()) {
					final MenuItem filterItem = new MenuItem("Filter: " + 
							treeGrid.getSelectionModel().getSelectedItem().getText());
					Menu filterMenu = new Menu();
					filterItem.setSubMenu(filterMenu);
					MenuItem filterGrid = new MenuItem("Grid");
					filterGrid.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							eventBus.fireEvent(new FilterEvent(treeGrid.getSelectionModel().getSelectedItem().getText(), 
									FilterTarget.GRID, type));
						}
					});
					filterMenu.add(filterGrid);
					MenuItem filterTree = new MenuItem("Tree");
					filterTree.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							eventBus.fireEvent(new FilterEvent(treeGrid.getSelectionModel().getSelectedItem().getText(), 
									FilterTarget.TREE, type));
						}
					});
					filterMenu.add(filterTree);
					MenuItem filterAll = new MenuItem("Grid + Tree");
					filterAll.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							eventBus.fireEvent(new FilterEvent(treeGrid.getSelectionModel().getSelectedItem().getText(), 
									FilterTarget.GRID_AND_TREE, type));
						}
					});
					filterMenu.add(filterAll);
					menu.add(filterItem);
					
					MenuItem context = new MenuItem("Show Term Context");
					context.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							eventBus.fireEvent(new SelectTermEvent(treeGrid.getSelectionModel().getSelectedItem().getText()));
						}
					});
					menu.add(context);
				}
			}
		});
		

		return menu;
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
		eventBus.addHandler(FilterEvent.TYPE, new FilterEvent.Handler() {
			@Override
			public void onFilter(FilterEvent event) {
				if((event.getFilterTarget().equals(FilterTarget.TREE) || 
						event.getFilterTarget().equals(FilterTarget.GRID_AND_TREE)) && event.containsType(type)) {
					TreeView.this.onFilter(event.getFilter());
				}
			}
		});
		eventBus.addHandler(SortEvent.TYPE, new SortEvent.Handler() {
			@Override
			public void onSort(SortEvent event) {
				if((event.getSortTarget().equals(SortTarget.TREE) || 
						event.getSortTarget().equals(SortTarget.GRID_AND_TREE)) && event.containsType(type)) {
					TreeView.this.onSort(event.getSortField(), event.getSortDir());
				}
			}
		});
	}
	

	protected void onSort(SortField sortField, SortDir sortDir) {
		this.sort(sortField.equals(SortField.name) ? nameComparator : creationComparator, sortDir);
	}

	protected void onFilter(final String filter) {
		boolean activate = filter != null && !filter.isEmpty();
		checkFilterItem.setChecked(activate, true);
		if(!activate) {
			filterTreeField.setText("");
			store.removeFilters();
			store.setEnableFilters(false);
		} else {
			store.removeFilters();
			store.addFilter(new StoreFilter<VertexTreeNode>() {
				@Override
				public boolean select(Store<VertexTreeNode> store, VertexTreeNode parent, VertexTreeNode item) {
					String value = item.getVertex().getValue();
		        	value = value.toLowerCase();
		        	if(value.contains(filter.toLowerCase())) {
		        		return true;
					}
		        	while(parent != null) {
		        		parent = TreeView.this.store.getParent(parent);
		        		item = TreeView.this.store.getParent(item);
		        		value = item.getVertex().getValue();
			        	value = value.toLowerCase();
			        	if(value.contains(filter.toLowerCase())) {
			        		return true;
						}
		        	}
					return false;
				}
			});
			store.setEnableFilters(true);
			filterTreeField.setText(filter);
		}
	}
	
	protected void onOrderEffectiveInModel(OrderEdgesEvent event, Vertex src, List<Edge> edges, Type type2) {
		this.sort();
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
		this.sort(creationComparator, SortDir.DESC);
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
		return treeGrid.getTreeStore().getRootItems().get(0).getVertex();
	}
	
	public void sort(final Comparator<VertexTreeNode> comparator, final SortDir sortDir) {
		store.clearSortInfo();
		store.addSortInfo(new StoreSortInfo<VertexTreeNode>(comparator, sortDir));
		treeGrid.setExpanded(store.getRootItems().get(0), true);
	}
	
	public void sort() {
		store.applySort(false);
		treeGrid.setExpanded(store.getRootItems().get(0), true);
	}
}
