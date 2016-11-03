package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.HideMode;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent.BeforeHideHandler;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.filters.AbstractGridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.data.shared.ModelKeyProvider;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.common.TextAreaMessageBox;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ClearEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent.FilterTarget;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SortEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SortEvent.SortField;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SortEvent.SortTarget;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.MenuTreeView;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.TreeView;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

public class MenuTermsGrid extends TermsGrid {
	
	private class FilterState {
		public String filter = "";
		public FilterTarget target = FilterTarget.GRID;
	}
	
	protected ToolBar buttonBar;
	private ComboBox<String> filterGridField;
	private ComboBox<String> filterTreeField;
	private ComboBox<String> filterGridAndTreeField;
	private CheckBox filterCheckBox;
	private FilterState lastActiveFilterState = new FilterState();
	private FilterState filterState = new FilterState();
	private DelayedTask filterGridTask = new DelayedTask() {		
		@Override
		public void onExecute() {
			fire(new FilterEvent(filterGridField.getText(), FilterTarget.GRID, Type.values()));
		}
	};
	private DelayedTask filterTreeTask = new DelayedTask() {		
		@Override
		public void onExecute() {
			fire(new FilterEvent(filterTreeField.getText(), FilterTarget.TREE, Type.values()));
		}
	};
	private DelayedTask filterGridAndTreeTask = new DelayedTask() {		
		@Override
		public void onExecute() {
			fire(new FilterEvent(filterGridAndTreeField.getText(), FilterTarget.GRID_AND_TREE, Type.values()));
		}
	};
	private VerticalLayoutContainer vlc;
	private SimpleContainer simpleContainer;
	private ListStore<String> filterStore;
	
	public MenuTermsGrid(final EventBus eventBus, final Type type) {
		super(eventBus, type);
		
		buttonBar = new ToolBar();
		
		TextButton sortButton = new TextButton("Sort");
		final Menu sortMenu = new Menu();
		sortButton.setMenu(sortMenu);
		for(final SortTarget sortTarget : SortEvent.SortTarget.values()) {
			//if(!sortTarget.equals(SortTarget.TREE)) {
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
							fire(new SortEvent(sortField, SortDir.ASC, sortTarget, type));
						}
					});
					creationDescButton.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							fire(new SortEvent(sortField, SortDir.DESC, sortTarget, type));
						}
					});
				}
			//}
		}
		
		TextButton importButton = new TextButton("Import");
		importButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final TextAreaMessageBox box = new TextAreaMessageBox("Import " + type.getDisplayLabel(), "");
				box.getTextArea().setEmptyText(getDefaultImportText());
				/*box.setResizable(true);
				box.setResize(true);
				box.setMaximizable(true);*/
				box.setModal(true);
				box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						try {
							List<GwtEvent<?>> importEvents = createImportEvents(box.getValue().trim());
							fire(new CompositeModifyEvent(importEvents));
						} catch(Exception e) {
							Alerter.showAlert("Import failed", e.getMessage());
						}
						box.hide();
					}
				});
				box.show();
			}
		});
		
		TextButton exportButton = new TextButton("Export");
		exportButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final TextAreaMessageBox box = new TextAreaMessageBox("Export " + type.getDisplayLabel(), "");
				/*box.setResizable(true);
				box.setResize(true);
				box.setMaximizable(true);*/
				box.setModal(true);
				String export = createExport();
				box.getTextArea().setText(export);
				box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						box.hide();
					}
				});
				box.show();
			}
		});
		
		
		TextButton clearButton = new TextButton("Clear");
		clearButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final MessageBox box = Alerter.showConfirm("Remove All Rows", "Are you sure you want to remove all rows");
				box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						fire(new ClearEvent());
						box.hide();
					}
				});
				box.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						box.hide();
					}
				});
			}
		});
		
		/*
		TextButton consolidateButton = new TextButton("Consolidate");
		consolidateButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				try {
					consolidate();
				} catch(Exception e) {
					Alerter.showAlert("Consolidate rows", "Error occured");
				}
			}
		});
		*/
		/*TextButton removeButton = new TextButton("Remove");
		Menu removeMenu = new Menu();
		MenuItem selectedRemove = new MenuItem("Selected rows");//jin selected entry
		selectedRemove.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				List<Row> selection = getSelection();
				eventBus.fireEvent(event);
				removeRows(getSelection());
			}
		});
		/*MenuItem consolidate = new MenuItem("Consolidate");
		emptyColRemove.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				try {
					consolidate();
				} catch (Exception e) {
					Alerter.showAlert("Remove empty columns", "Error occured");
				}
			}
		});*/
		/*MenuItem allRemove = new MenuItem("All");
		allRemove.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				removeRows(getAll());
			}
		});
		removeMenu.add(selectedRemove);
		//removeMenu.add(consolidate);
		removeMenu.add(allRemove);
		removeButton.setMenu(removeMenu);*/
		
		filterCheckBox = new CheckBox();
		filterCheckBox.setBoxLabel("Filter: ");
		filterCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(!event.getValue())
	        		fire(new FilterEvent("", filterState.target, Type.values()));
	        	else {
	        		if(lastActiveFilterState.filter != null && !lastActiveFilterState.filter.isEmpty() && lastActiveFilterState.target != null)
	        			eventBus.fireEvent(new FilterEvent(lastActiveFilterState.filter, lastActiveFilterState.target, Type.values()));
	        		else
	        			Alerter.showAlert("Filter", "No filter selected");
	        	}
			}
	    });
		
		TextButton filterButton = new TextButton("Set Filter");		
		final Menu filterMenu = new Menu();
		MenuItem filterGridItem = new MenuItem("Grid");
		Menu filterGridMenu = new Menu();
		filterGridItem.setSubMenu(filterGridMenu);		
		filterStore = new ListStore<String>(new ModelKeyProvider<String>() {
			@Override
			public String getKey(String item) {
				return item;
			}
		});
		filterStore.add("modifier");
		filterGridField = new ComboBox<String>(filterStore, new StringLabelProvider<String>()) {
			protected void onKeyUp(Event event) {
				super.onKeyUp(event);
				int key = event.getKeyCode();
				if (key == KeyCodes.KEY_ENTER) {
					event.stopPropagation();
					event.preventDefault();
					filterMenu.hide(true);
					fire(new FilterEvent(filterGridField.getText(), FilterTarget.GRID, Type.values()));
				} else 
					filterGridTask.delay(500);
			}
		};
		filterGridField.addSelectionHandler(new SelectionHandler<String>() {
			@Override
			public void onSelection(SelectionEvent<String> event) {
				filterMenu.hide(true);
				fire(new FilterEvent(filterGridField.getText(), FilterTarget.GRID, Type.values()));
			}
		});
		filterGridMenu.add(filterGridField);
		filterMenu.add(filterGridItem);
		
		MenuItem filterTreeItem = new MenuItem("Tree");
		Menu filterTreeMenu = new Menu();
		filterTreeItem.setSubMenu(filterTreeMenu);
		filterTreeField = new ComboBox<String>(filterStore, new StringLabelProvider<String>()) {
			protected void onKeyUp(Event event) {
				super.onKeyUp(event);
				int key = event.getKeyCode();
				if (key == KeyCodes.KEY_ENTER) {
					event.stopPropagation();
					event.preventDefault();
					filterMenu.hide(true);
					fire(new FilterEvent(filterTreeField.getText(), FilterTarget.TREE, Type.values()));
				} else
					filterTreeTask.delay(500);
			}
		};
		filterTreeField.addSelectionHandler(new SelectionHandler<String>() {
			@Override
			public void onSelection(SelectionEvent<String> event) {
				filterMenu.hide(true);
				fire(new FilterEvent(filterTreeField.getText(), FilterTarget.TREE, Type.values()));
			}
		});
		filterTreeMenu.add(filterTreeField);
		filterMenu.add(filterTreeItem);
		
		MenuItem filterGridAndTreeItem = new MenuItem("Grid and Tree");
		Menu filterGridAndTreeMenu = new Menu();
		filterGridAndTreeItem.setSubMenu(filterGridAndTreeMenu);
		filterGridAndTreeField = new ComboBox<String>(filterStore, new StringLabelProvider<String>()) {
			protected void onKeyUp(Event event) {
				super.onKeyUp(event);
				int key = event.getKeyCode();
				if (key == KeyCodes.KEY_ENTER) {
					event.stopPropagation();
					event.preventDefault();
					filterMenu.hide(true);
					fire(new FilterEvent(filterGridAndTreeField.getText(), FilterTarget.GRID_AND_TREE, Type.values()));
				} else
					filterGridAndTreeTask.delay(500);
			}
		};
		filterGridAndTreeField.addSelectionHandler(new SelectionHandler<String>() {
			@Override
			public void onSelection(SelectionEvent<String> event) {
				filterMenu.hide(true);
				fire(new FilterEvent(filterGridAndTreeField.getText(), FilterTarget.GRID_AND_TREE, Type.values()));
			}
		});
		filterGridAndTreeMenu.add(filterGridAndTreeField);
		filterMenu.add(filterGridAndTreeItem);
		
		filterButton.setMenu(filterMenu);

		buttonBar.add(sortButton);
		buttonBar.add(importButton);
		buttonBar.add(exportButton);
		buttonBar.add(clearButton);
		//buttonBar.add(consolidateButton);
		//buttonBar.add(removeButton);
		buttonBar.add(filterButton);
		buttonBar.add(filterCheckBox);
		
		
		vlc = new VerticalLayoutContainer();
		vlc.add(buttonBar, new VerticalLayoutData(1, -1));
		vlc.add(super.asWidget(), new VerticalLayoutData(1, 1));
		simpleContainer = new SimpleContainer();
		simpleContainer.setWidget(vlc);
		simpleContainer.setHideMode(HideMode.OFFSETS); //bug https://www.sencha.com/forum/showthread.php?285982-Grid-ColumnHeader-Menu-missing
	}
	
	/*protected String getActiveFilterText() {
		if(!filterGridField.getText().isEmpty())
			return filterGridField.getText();
		if(!filterGridAndTreeField.getText().isEmpty())
			return filterGridAndTreeField.getText();
		return lastFilterText;
	}

	protected FilterTarget getActiveFilterTarget() {
		if(!filterGridField.getText().isEmpty())
			return FilterTarget.GRID;
		if(!filterGridAndTreeField.getText().isEmpty())
			return FilterTarget.GRID_AND_TREE;
		return lastFilterTarget;
	}*/

	protected String getDefaultImportText() {
		return "";	
	}
	
	@Override
	public void bindEvents() {
		super.bindEvents();

		eventBus.addHandler(FilterEvent.TYPE, new FilterEvent.Handler() {
			@Override
			public void onFilter(FilterEvent event) {
				if(event.containsType(type)) {
					String filter = event.getFilter();
					if(filterStore.findModel(filter) == null && !filter.trim().isEmpty())
						filterStore.add(filter);
					FilterTarget target = event.getFilterTarget();
					boolean activate = filterCheckBox.getValue();	
					switch(target) {
						case GRID:
							filterState.filter = filter;
							filterState.target = target;
							activate = filter != null && !filter.isEmpty();
							filterGridField.setText(filter);
							filterTreeField.setText("");
							filterGridAndTreeField.setText("");
							MenuTermsGrid.this.onFilter(filterState.filter);
							break;
						case GRID_AND_TREE:
							filterState.filter = filter;
							filterState.target = target;
							activate = filter != null && !filter.isEmpty();
							filterGridAndTreeField.setText(filterState.filter);
							filterGridField.setText("");
							filterTreeField.setText("");
							MenuTermsGrid.this.onFilter(filterState.filter);
							break;
						case TREE:
							if(filterState.target.equals(FilterTarget.GRID_AND_TREE)) {
								filterState.target = FilterTarget.GRID;
								filterGridField.setText(filterState.filter);
								filterTreeField.setText("");
								filterGridAndTreeField.setText("");
							}
							break;
					}

					if(activate) {
						filterCheckBox.setBoxLabel("Filter: " + filterState.filter + " (" + 
								filterState.target.getDisplayName() + ")");
						lastActiveFilterState.filter = filterState.filter;
						lastActiveFilterState.target = filterState.target;
					} else
						filterCheckBox.setBoxLabel("Filter: ");
					filterCheckBox.setValue(activate, false);
				}
			}
		});
		
		eventBus.addHandler(SortEvent.TYPE, new SortEvent.Handler() {
			@Override
			public void onSort(SortEvent event) {
				if(event.containsType(type)) {
					PagingLoadConfig config = loader.getLastLoadConfig();
					List<SortInfo> sortInfo = new LinkedList<SortInfo>();
					sortInfo.add(new SortInfoBean(event.getSortField().toString(), event.getSortDir()));
					config.setSortInfo(sortInfo);
					loader.load(config);
				}
			}
		});
	}

	protected void onFilter(final String filter) {
		if(filter == null || filter.isEmpty()) {
			allRowStore.removeFilters();
			allRowStore.setEnableFilters(false);
		} else {
			allRowStore.removeFilters();
			allRowStore.addFilter(new StoreFilter<Row>() {
				@Override
				public boolean select(Store<Row> store, Row parent, Row item) {
					String all = item.getLead().getValue() + " ";
					for(Edge r : item.getAttached())
						all += r.getDest().getValue() + " ";
					return all.contains(filter);
				}
			});
			allRowStore.setEnableFilters(true);
		}
		loader.load(loader.getLastLoadConfig());
	}

	protected String createExport() {
		/*OntologyGraph g = ModelController.getCollection().getGraph();
		Vertex root = g.getRoot(type);
		return getRelationsAsString(g, root);*/
		StringBuilder sb = new StringBuilder();
		for(Row row : this.getAll()) {
			//if(row.hasAttacheds()) {
				sb.append(row.getLead());
				for(Edge a : row.getAttached())
					sb.append(", " + a.getDest().getValue());
				sb.append("\n");
			//}
		}
		return sb.toString();
	}

	/*private String getRelationsAsString(OntologyGraph g, Vertex v) {
		StringBuilder sb = new StringBuilder();
		List<Relation> out = g.getOutRelations(v, type);
		if(!out.isEmpty()) {
			sb.append(v.getValue());
			for(Relation r : out) 
				sb.append(", " + r.getDestination().getValue());
			sb.append("\n");
			for(Relation r : out) 
				sb.append(getRelationsAsString(g, r.getDestination()));
		}
		return sb.toString();
	}*/

	@Override
	public Widget asWidget() {
		return simpleContainer;
	}
	
	private List<GwtEvent<?>> createImportEvents(String text) throws Exception {
		List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
		
		OntologyGraph g = ModelController.getCollection().getGraph();
		Vertex root = new Vertex(type.getRootLabel());
		String[] lines = text.split("\\n");
		Set<Vertex> alreadyAttached = new HashSet<Vertex>(g.getVertices());						
		for(String line : lines) {
			String[] terms = line.split(",");
			
			if(!terms[0].trim().isEmpty()) {
				Vertex source = new Vertex(terms[0].trim());
				if(!alreadyAttached.contains(source)) {
					Edge newEdge = new Edge(root, source, type, Origin.USER);
					result.add(new CreateRelationEvent(newEdge));
					alreadyAttached.add(source);
				}
				for(int i=1; i<terms.length; i++) {
					if(terms[i].trim().isEmpty()) 
						continue;
					Vertex target = new Vertex(terms[i].trim());
					
					if(alreadyAttached.contains(target)) {
						Edge rootEdge = new Edge(g.getRoot(type), target, type, Origin.USER);
						if(g.existsRelation(rootEdge)) {
							result.add(new ReplaceRelationEvent(rootEdge, source));
						} else {
							Edge newEdge = new Edge(source, target, type, Origin.USER);
							result.add(new CreateRelationEvent(newEdge));
						}
					} else {
						Edge newEdge = new Edge(source, target, type, Origin.USER);
						result.add(new CreateRelationEvent(newEdge));
						alreadyAttached.add(target);
					}
				}
			} else {
				throw new Exception("Malformed input");
			}
		}
		
		return result;
	}

}
