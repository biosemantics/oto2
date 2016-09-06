package edu.arizona.biosemantics.oto2.ontologize2.client.tree;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

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

import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.OrderEdgesEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SelectTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SortEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent.FilterTarget;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SortEvent.SortField;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SortEvent.SortTarget;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.MenuTermsGrid;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public class MenuTreeView extends TreeView {
	
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

	private VerticalLayoutContainer vlc;
	
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
		
		vlc = new VerticalLayoutContainer();
		vlc.add(buttonBar, new VerticalLayoutData(1, -1));
		vlc.add(super.asWidget(), new VerticalLayoutData(1, 1));
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
							MenuTreeView.this.onFilter(filter);
							break;
						case GRID_AND_TREE:
							filterGridAndTreeField.setText(filter);
							MenuTreeView.this.onFilter(filter);
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
		this.sort(sortField.equals(SortField.name) ? nameComparator : creationComparator, sortDir);
	}
	
	protected void onFilter(final String filter) {
		if(!checkFilterItem.isChecked()) {
			store.removeFilters();
			store.setEnableFilters(false);
		} else {
			store.removeFilters();
			store.addFilter(new StoreFilter<VertexTreeNode>() {
				@Override
				public boolean select(Store<VertexTreeNode> store, VertexTreeNode parent, VertexTreeNode item) {
					OntologyGraph g = ModelController.getCollection().getGraph();
					String value = item.getVertex().getValue();
		        	value = value.toLowerCase();
		        	if(value.contains(filter.toLowerCase())) {
		        		return true;
					}
		        	while(parent != null) {
		        		parent = MenuTreeView.this.store.getParent(parent);
		        		item = MenuTreeView.this.store.getParent(item);
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
			store.setEnableFilters(true);
		}
		treeGrid.setExpanded(store.getRootItems().get(0), true);
	}
	
	@Override
	protected Vertex getRoot() {
		store.setEnableFilters(false);
		Vertex result = store.getRootItems().get(0).getVertex();
		store.setEnableFilters(true);
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
	
	@Override
	protected void onLoadCollectionEffectiveInModel() {
		this.sort(creationComparator, SortDir.DESC);
	}
	
	@Override
	protected void onOrderEffectiveInModel(OrderEdgesEvent event, Vertex src, List<Edge> edges, Type type) {
		this.sort();
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
	
	@Override
	public Widget asWidget() {
		return vlc;
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
	        	if(!event.getItem().isChecked())
	        		eventBus.fireEvent(new FilterEvent("", getActiveFilterTarget(), type));
	        	else
	        		eventBus.fireEvent(new FilterEvent(getActiveFilterText(), getActiveFilterTarget(), type));
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
