package edu.arizona.biosemantics.oto2.ontologize2.client.tree;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Stack;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.OrderEdgesEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SelectTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SortEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent.FilterTarget;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SortEvent.SortField;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SortEvent.SortTarget;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.MenuTermsGrid;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.TreeView.SubTree;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public class MenuTreeView extends TreeView {
	
	protected ToolBar buttonBar;
	
	private SubTree resetSubTree;
	private Stack<SubTree> navigationStack = new Stack<SubTree>();
	private MenuItem backButton;
	
	private CheckMenuItem checkFilterItem;
	private TextField filterGridField;
	private TextField filterTreeField;
	private TextField filterGridAndTreeField;
	private DelayedTask filterGridTask = new DelayedTask() {		
		@Override
		public void onExecute() {
			eventBus.fireEvent(new FilterEvent(filterGridField.getText(), FilterTarget.GRID, Type.values()));
		}
	};
	private DelayedTask filterTreeTask = new DelayedTask() {		
		@Override
		public void onExecute() {
			eventBus.fireEvent(new FilterEvent(filterTreeField.getText(), FilterTarget.TREE, Type.values()));
		}
	};
	private DelayedTask filterGridAndTreeTask = new DelayedTask() {		
		@Override
		public void onExecute() {
			eventBus.fireEvent(new FilterEvent(filterGridAndTreeField.getText(), FilterTarget.GRID_AND_TREE, Type.values()));
		}
	};
	
	private Comparator<VertexTreeNode> creationComparator = new Comparator<VertexTreeNode>() {
		@Override
		public int compare(VertexTreeNode o1, VertexTreeNode o2) {
			OntologyGraph g = ModelController.getCollection().getGraph();
			Date d1 = getCreationDate(o1.getVertex());
			Date d2 = getCreationDate(o2.getVertex());
			
			VertexTreeNode p1 = subTree.getStore().getParent(o1);
			VertexTreeNode p2 = subTree.getStore().getParent(o2);
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
			VertexTreeNode p1 = subTree.getStore().getParent(o1);
			VertexTreeNode p2 = subTree.getStore().getParent(o2);
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

	private VerticalLayoutContainer vlc;

	private MenuItem resetButton;
	
	public MenuTreeView(EventBus eventBus, Type type) {
		super(eventBus, type);

		buttonBar = new ToolBar();
		/*Label titleLabel = new Label(type.getTreeLabel());
		buttonBar.add(titleLabel);
		titleLabel.getElement().getStyle().setFontSize(11, Unit.PX);
		titleLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		titleLabel.getElement().getStyle().setPaddingLeft(3, Unit.PX);
		titleLabel.getElement().getStyle().setColor("#15428b");
		*/
		buttonBar.add(createFilterMenu());
		buttonBar.add(createSortMenu());
		buttonBar.add(createNavigationMenu());
		
		vlc = new VerticalLayoutContainer();
		vlc.add(buttonBar, new VerticalLayoutData(1, -1));
		vlc.add(super.asWidget(), new VerticalLayoutData(1, 1));
	}
	
	private Widget createNavigationMenu() {
		Menu menu = new Menu();
		resetButton = new MenuItem("Reset");
		resetButton.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				MessageBox box = Alerter.startLoading();
				OntologyGraph g = ModelController.getCollection().getGraph();
				bindSubTree(resetSubTree, true);
				navigationStack.removeAllElements();
				backButton.setEnabled(false);
				resetButton.setEnabled(false);
				MenuTreeView.this.expandRoot();
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
				bindSubTree(subTree, true);
				if(navigationStack.isEmpty()) {
					backButton.setEnabled(false);
					resetButton.setEnabled(false);
				}
				MenuTreeView.this.expandRoot();
				Alerter.stopLoading(box);
			}
		});
		backButton.setEnabled(false);
		resetButton.setEnabled(false);
		menu.add(backButton);
		menu.add(resetButton);
		
		TextButton menuButton = new TextButton("Navigate");
		menuButton.setMenu(menu);
		return menuButton;
	}

	@Override
	public void bindEvents() {
		super.bindEvents();
		eventBus.addHandler(FilterEvent.TYPE, new FilterEvent.Handler() {
			@Override
			public void onFilter(FilterEvent event) {
				if(event.containsType(type)) {
					String filter = event.getFilter();
					boolean activate = filter != null && !filter.isEmpty();
					checkFilterItem.setChecked(activate, true);
					
					switch(event.getFilterTarget()) {
						case TREE:
							filterTreeField.setText(filter);
							MenuTreeView.this.onFilter(subTree, filter);
							break;
						case GRID_AND_TREE:
							filterGridAndTreeField.setText(filter);
							MenuTreeView.this.onFilter(subTree, filter);
							break;
						case GRID:
							filterGridField.setText(filter);
							break;
					}
				}
			}
		});
		eventBus.addHandler(SortEvent.TYPE, new SortEvent.Handler() {
			@Override
			public void onSort(SortEvent event) {
				if(event.containsType(type)) {
					if(event.getSortTarget().equals(SortTarget.TREE) || event.getSortTarget().equals(SortTarget.GRID_AND_TREE))
							MenuTreeView.this.onSort(event.getSortField(), event.getSortDir());
				}
			}
		});
	}

	protected void onSort(SortField sortField, SortDir sortDir) {
		this.sort(subTree, sortField.equals(SortField.name) ? nameComparator : creationComparator, sortDir);
	}
	
	protected void onFilter(final SubTree subTree, final String filter) {
		if(!checkFilterItem.isChecked()) {
			subTree.getStore().removeFilters();
			subTree.getStore().setEnableFilters(false);
		} else {
			subTree.getStore().removeFilters();
			subTree.getStore().addFilter(new StoreFilter<VertexTreeNode>() {
				@Override
				public boolean select(Store<VertexTreeNode> store, VertexTreeNode parent, VertexTreeNode item) {
					OntologyGraph g = ModelController.getCollection().getGraph();
					String value = item.getVertex().getValue();
		        	value = value.toLowerCase();
		        	if(value.contains(filter.toLowerCase())) {
		        		return true;
					}
		        	while(parent != null) {
		        		parent = subTree.getStore().getParent(parent);
		        		item = subTree.getStore().getParent(item);
		        		if(!item.getVertex().equals(g.getRoot(type))) {
			        		value = item.getVertex().getValue().toLowerCase();
				        	if(value.contains(filter.toLowerCase())) {
				        		return true;
							}
		        		}
		        	}
					return false;
				}
			});
			subTree.getStore().setEnableFilters(true);
		}
		this.expandRoot();
	}
	
	@Override
	protected Vertex getRoot(SubTree subTree) {
		subTree.getStore().setEnableFilters(false);
		Vertex result = subTree.getStore().getRootItems().get(0).getVertex();
		subTree.getStore().setEnableFilters(true);
		return result;
	}
	
	@Override
	protected Menu createContextMenu() {
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
									FilterTarget.GRID, Type.values()));
						}
					});
					filterMenu.add(filterGrid);
					MenuItem filterTree = new MenuItem("Tree");
					filterTree.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							eventBus.fireEvent(new FilterEvent(treeGrid.getSelectionModel().getSelectedItem().getText(), 
									FilterTarget.TREE, Type.values()));
						}
					});
					filterMenu.add(filterTree);
					MenuItem filterAll = new MenuItem("Grid + Tree");
					filterAll.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							eventBus.fireEvent(new FilterEvent(treeGrid.getSelectionModel().getSelectedItem().getText(), 
									FilterTarget.GRID_AND_TREE, Type.values()));
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
				event.setCancelled(menu.getWidgetCount() == 0);
			}
		});
		
		return menu;
	}
	
	@Override
	protected void onLoadCollectionEffectiveInModel() {
		super.onLoadCollectionEffectiveInModel();
		this.sort(subTree, creationComparator, SortDir.DESC);
		resetSubTree = subTree;
	}
	
	@Override
	protected void onOrderEffectiveInModel(OrderEdgesEvent event, Vertex src, List<Edge> edges, Type type) {
		super.onOrderEffectiveInModel(event, src, edges, type);
		this.sort(subTree);
	}
	
	public void sort(SubTree subTree, final Comparator<VertexTreeNode> comparator, final SortDir sortDir) {
		subTree.getStore().clearSortInfo();
		subTree.getStore().addSortInfo(new StoreSortInfo<VertexTreeNode>(comparator, sortDir));
		this.expandRoot();
	}
	
	public void sort(SubTree subTree) {
		subTree.getStore().applySort(false);
		this.expandRoot();
	}
	
	@Override
	public Widget asWidget() {
		return vlc;
	}
	
	@Override
	protected void onDoubleClick(VertexTreeNode node) {
		if(!node.getVertex().equals(this.getRoot(subTree))) {
			OntologyGraph g = ModelController.getCollection().getGraph();
			Vertex v = node.getVertex();
			navigationStack.push(subTree);
			subTree = createNewSubTree(true);
			backButton.setEnabled(true);
			resetButton.setEnabled(true);
			createFromRoot(subTree, g, v);
			bindSubTree(subTree, false);
		}
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
					eventBus.fireEvent(new FilterEvent(filterGridField.getText(), FilterTarget.GRID, Type.values()));
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
					eventBus.fireEvent(new FilterEvent(filterTreeField.getText(), FilterTarget.TREE, Type.values()));
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
					eventBus.fireEvent(new FilterEvent(filterGridAndTreeField.getText(), FilterTarget.GRID_AND_TREE, Type.values()));
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
	        	if(!event.getItem().isChecked())
	        		eventBus.fireEvent(new FilterEvent("", getActiveFilterTarget(), Type.values()));
	        	else
	        		eventBus.fireEvent(new FilterEvent(getActiveFilterText(), getActiveFilterTarget(), Type.values()));
	        }
	    });
		filterButton.setMenu(menu);
		return filterButton;
	}
	
	private String getActiveFilterText() {
		if(!filterGridField.getText().isEmpty())
			return filterGridField.getText();
		if(!filterTreeField.getText().isEmpty())
			return filterTreeField.getText();
		return filterGridAndTreeField.getText();
	}

	private FilterTarget getActiveFilterTarget() {
		if(!filterGridField.getText().isEmpty())
			return FilterTarget.GRID;
		if(!filterTreeField.getText().isEmpty())
			return FilterTarget.TREE;
		return FilterTarget.GRID_AND_TREE;
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

}
