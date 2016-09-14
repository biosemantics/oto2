package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.HideMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.state.client.GridFilterStateHandler;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ClearEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CloseRelationsEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.OrderEdgesEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveCandidateEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.VisualizationConfigurationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.VisualizationRefreshEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell.AttachedCell;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell.DefaultMenuCreator;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell.LeadCell;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;

public class TermsGrid implements IsWidget {

	public static class Row {
		
		public class CreationComparator implements Comparator<Vertex> {
			@Override
			public int compare(Vertex o1, Vertex o2) {
				OntologyGraph g = ModelController.getCollection().getGraph();
				Date d1 = getCreationDate(o1);
				Date d2 = getCreationDate(o2);
				if(g.hasOrderedEdges(lead, type)) {
					List<Edge> orderedEdges = g.getOrderedEdges(lead, type);
					return getIndex(o1, orderedEdges) - getIndex(o2, orderedEdges);
				} else {
					return d1.compareTo(d2);
				}
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
		
		public class NameComparator implements Comparator<Vertex> {		
			@Override
			public int compare(Vertex o1, Vertex o2) {
				OntologyGraph g = ModelController.getCollection().getGraph();			
				if(g.hasOrderedEdges(lead, type)) {
					List<Edge> orderedEdges = g.getOrderedEdges(lead, type);
					return orderedEdges.indexOf(o1) - orderedEdges.indexOf(o2);
				} else {
					return o1.getValue().toLowerCase().compareTo(o2.getValue().toLowerCase());
				}
			}
		};
		
		private static int currentId = 0;
		
		private int id = currentId++;
		private Type type;
		private Vertex lead;
		private List<Edge> attached = new ArrayList<Edge>();
		
		public Row(Type type, Vertex lead) {
			this.type = type;
			this.lead = lead;
		}
		
		public Row(Type type, Vertex lead, List<Edge> attached) {
			this(type, lead);
			this.attached.addAll(attached);
		}
		
		public Vertex getLead() {
			return lead;
		}
		
		public List<Edge> getAttached() {
			return new ArrayList<Edge>(this.attached);
		}

		public int getId() {
			return id;
		}
		
		public void add(int index, Edge relation) throws Exception {
			if(contains(relation))
				throw new Exception("Edge already exists.");
			this.attached.add(index, relation);
		}

		public void add(Edge relation) throws Exception {
			//int index = attached.size();
			this.add(0, relation); //index, relation);
		}
		
		public void add(Collection<Edge> relations) throws Exception {
			for(Edge relation : relations) 
				this.add(0, relation);
		}
		
		public void remove(int i) {
			Edge relation = attached.remove(i);
		}
		
		public void remove(String attached) {
			for(Edge r : this.attached) {
				if(r.getDest().getValue().equals(attached))
					this.remove(r);
			}
		}
		
		public void remove(Edge relation) {
			for(int i=0; i < attached.size(); i++) {
				if(attached.get(i).getDest().equals(relation.getDest()))
					this.remove(i);
			}
		}
		
		public void remove(Collection<Edge> relations) {
			for(Edge relation : relations)
				this.remove(relation);
		}

		public void setLead(Vertex lead) {
			this.lead = lead;
		}

		public int getAttachedCount() {
			return this.attached.size();
		}

		public boolean hasAttacheds() {
			return !this.attached.isEmpty();
		}
		
		public boolean containsAttached(Vertex v) {
			for(Edge relation : attached) {
				if(relation.getDest().equals(v))
					return true;
			}
			return false;
		}
		
		public boolean contains(Edge relation) {
			for(Edge r : attached) {
				if(r.getDest().equals(relation.getDest())) {
					return true;
				}
			}
			return false;
		}

		
		public int size() {
			return this.getAttachedCount() + 1;
		}

		public List<Vertex> getAll() {
			List<Vertex> result = new ArrayList<Vertex>(attached.size() + 1);
			result.add(this.lead);
			for(Edge relation : this.attached)
				result.add(relation.getDest());
			return result;
		}

		public void replaceAttachedDest(Vertex dest, Vertex disambiguatedDest) {
			for(int i=0; i<attached.size(); i++) {
				Edge r = attached.get(i);
				if(r.getDest().equals(dest))
					attached.set(i, new Edge(r.getSrc(), disambiguatedDest, r.getType(), r.getOrigin()));
			}
		}
		
		@Override
		public String toString() {
			String result = this.lead.getValue();
			for(Edge a : attached)
				result += ", " + a.getDest().getValue();
			return result;
		}

		public void sort(final Comparator<Vertex> comparator, final SortDir sortDir) {
			Collections.sort(attached, new Comparator<Edge>() {
				@Override
				public int compare(Edge o1, Edge o2) {
					Vertex d1 = o1.getDest();
					Vertex d2 = o2.getDest();
					int val = comparator.compare(d1, d2);
					if(sortDir.equals(SortDir.DESC))
						val = -val;
					return val;
				}
			});
		}
	}
	
	public static interface RowProperties extends PropertyAccess<Row> {

		@Path("id")
		ModelKeyProvider<Row> key();

		@Path("id")
		ValueProvider<Row, Integer> id();

		@Path("all")
		ValueProvider<Row, List<Vertex>> all();
		
		@Path("lead")
		ValueProvider<Row, Vertex> lead();
	}
	
	protected EventBus eventBus;
	private RowProperties rowProperties = GWT.create(RowProperties.class);
	protected Map<Vertex, Row> leadRowMap = new HashMap<Vertex, Row>();
	protected Grid<Row> grid;
	protected SimpleContainer createRowContainer;
	private final int colWidth = 100;
	protected Type type;
	private VerticalLayoutContainer vlc;
	//private SimpleContainer simpleContainer;
	protected PagingLoader<PagingLoadConfig, PagingLoadResult<Row>> loader;
	protected ListStore<Row> store;
	protected AllRowStore allRowStore;
	
	public TermsGrid(final EventBus eventBus, final Type type) {
		this.eventBus = eventBus;
		this.type = type;
		
		allRowStore = new AllRowStore();
		store = new ListStore<Row>(rowProperties.key());
		store.setAutoCommit(true);
		loader = new PagingLoader<PagingLoadConfig, PagingLoadResult<Row>>(allRowStore);
		loader.setRemoteSort(true);
		loader.addLoadHandler(new LoadResultListStoreBinding<PagingLoadConfig, Row, PagingLoadResult<Row>>(store));
		loader.useLoadConfig(new FilterPagingLoadConfigBean());
		//loader.addSortInfo(new SortInfoBean(rowProperties..submissionTerm(), SortDir.ASC));
		
		this.grid = new Grid<Row>(store, createColumnModel(new LinkedList<Row>()));// createColumnModel(1));//createColumnModel(new LinkedList<Row>()));
		this.grid.setLoader(loader);
		
		createRowContainer = createCreateRowContainer();
		
		GridDragSource<Row> dndSource = new GridDragSource<Row>(grid) {
			@Override
			protected void onDragStart(DndDragStartEvent event) {
				super.onDragStart(event);
				Element element = event.getDragStartEvent().getStartElement();
				int targetRowIndex = grid.getView().findRowIndex(element);
				int targetColIndex = grid.getView().findCellIndex(element, null);
				Row row = store.get(targetRowIndex);
				if(row != null) {
					Vertex v = row.getLead();
					if(targetColIndex > 0) {
						v = row.getAttached().get(targetColIndex - 1).getDest();
					}
					
					OntologyGraph g = ModelController.getCollection().getGraph();
					List<Edge> inRelations = g.getInRelations(v, type);
					if(inRelations.size() > 1) {
						//Alerter.showAlert("Moving", "Moving of term with more than one " + 
						//		type.getSourceLabelPlural() + " is not allowed"); // at this time
						//event.setCancelled(true);
						event.setData(inRelations);
					} else if(inRelations.size() == 1)
						event.setData(inRelations.get(0));
					else {
						Alerter.showAlert("Moving", "Cannot move the root");
						event.setCancelled(true);
					}
				}
			}
		};
		
		DropTarget dropTarget = new DropTarget(grid);
		dropTarget.addDropHandler(new DndDropHandler() {
			@Override
			public void onDrop(DndDropEvent event) {
				OntologyGraph g = ModelController.getCollection().getGraph();
				Element element = event.getDragEndEvent().getNativeEvent().getEventTarget().<Element> cast();
				int targetRowIndex = grid.getView().findRowIndex(element);
				Row row = store.get(targetRowIndex);
				if(g.isClosedRelations(row.getLead(), type)) {
					Alerter.showAlert("Create Relation", "Can not create relation for a closed row.");
					return;
				}
				
				if(row != null) {
					if(event.getData() instanceof List<?>) {
						List<?> list = (List<?>)event.getData();
						for(Object item : list) {
							if(item instanceof Candidate) {
								Candidate c = (Candidate)item;
								Vertex dest = new Vertex(c.getText());
								Edge rootEdge = new Edge(g.getRoot(type), dest, type, Origin.USER);
								if(g.existsRelation(rootEdge)) {
									fire(new ReplaceRelationEvent(rootEdge, row.getLead()));
								} else {
									fire(new CreateRelationEvent(new Edge(row.getLead(), dest, type, Origin.USER)));
								}
							}
						}
					} else if(event.getData() instanceof Edge) {
						Edge r = (Edge)event.getData();
						if(row.getAttached().contains(r)) {
							Alerter.showAlert("Create Relation", "" + r.getDest() + " is already a " + 
									type.getTargetLabel() + " of " + row.getLead());
						} else if(containedInSubtree(row.getLead(), r.getDest())) {
							Alerter.showAlert("Creat Relation", 
									"Cannot make " + r.getDest() + " a "+ type.getTargetLabel() + " of " + row.getLead() + ". "
											+ "This would create a circular relationship.");
						} else {
							fire(new ReplaceRelationEvent(r, row.getLead()));
						}
					}
				}
			}
		});
		dropTarget.setOperation(Operation.COPY);
		dropTarget.setAllowSelfAsSource(true);
		
		if(createRowContainer != null) {
			DropTarget dropTargetNewRow = new DropTarget(createRowContainer);
			dropTargetNewRow.addDropHandler(new DndDropHandler() {
				@Override
				public void onDrop(DndDropEvent event) {
					if(event.getData() instanceof List<?>) {						
						List<?> list = (List<?>)event.getData();
						if(!list.isEmpty()) {
							for(Object obj : list) {
								if(obj instanceof Candidate) {
									Candidate candidate = (Candidate)obj;
									Vertex source = new Vertex(type.getRootLabel());
									Vertex target = new Vertex(candidate.getText());
									Set<Row> attachedRows = TermsGrid.this.getRowsWhereIncluded(target);
									if(attachedRows.isEmpty()) {
										Edge relation = new Edge(source, target, type, Origin.USER);
										
										if(ModelController.getCollection().getGraph().isClosedRelations(source, type)) {
											Alerter.showAlert("Create Relation", "Can not create relation for a closed row.");
											return;
										}
										CreateRelationEvent createRelationEvent = new CreateRelationEvent(relation);
										fire(createRelationEvent);
									} else {
										if(!TermsGrid.this.leadRowMap.containsKey(target))
											TermsGrid.this.addRow(new Row(type, target), true);
									}
								}
								else if(obj instanceof Edge) {
									createRowFromEdgeDrop((Edge)obj);
								}
							}
						}
					}
					if(event.getData() instanceof Edge) {
						createRowFromEdgeDrop((Edge)event.getData());
					}
				}
			});
			dropTargetNewRow.setOperation(Operation.COPY);
		}
		vlc = new VerticalLayoutContainer();
		vlc.add(grid, new VerticalLayoutData(1, 1));
		if(createRowContainer != null)
			vlc.add(createRowContainer, new VerticalLayoutData(1, -1));
		
		final PagingToolBar toolBar = new PagingToolBar(20);
	    toolBar.setBorders(false);
	    toolBar.bind(loader);
		vlc.add(toolBar, new VerticalLayoutData(1, -1));
	    
		vlc.getScrollSupport().setScrollMode(ScrollMode.AUTO);
		//simpleContainer = new SimpleContainer();
		//simpleContainer.getScrollSupport().setScrollMode(ScrollMode.NONE);
		//simpleContainer.add(vlc);
		bindEvents();
	}

	protected boolean containedInSubtree(Vertex search, Vertex source) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		
		if(search.equals(source))
			return true;
		List<Edge> out = g.getOutRelations(source, type);
		for(Edge e : out) {
			if(containedInSubtree(search, e.getDest()))
				return true;
		}
		return false;
	}

	protected void createRowFromEdgeDrop(Edge edge) {
		// TODO Auto-generated method stub
		
	}

	public void fire(GwtEvent<? extends EventHandler> e) {
		eventBus.fireEvent(e);
	}

	protected void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {			
			@Override
			public void onLoad(LoadCollectionEvent event) {
				if(!event.isEffectiveInModel()) {
					clearGrid();
					OntologyGraph g = event.getCollection().getGraph();
					TermsGrid.this.onLoad(g);
				} else {
					onLoadCollectionEffectiveInModel();
				}
			}
		}); 
		eventBus.addHandler(VisualizationRefreshEvent.TYPE, new VisualizationRefreshEvent.Handler() {
			@Override
			public void onRefresh(VisualizationRefreshEvent event) {
				grid.getView().refresh(false);
			}
		});
		eventBus.addHandler(ClearEvent.TYPE, new ClearEvent.Handler() {
			@Override
			public void onClear(ClearEvent event) {
				if(event.isEffectiveInModel()) {
					allRowStore.clear();
					leadRowMap.clear();
					loader.load(loader.getLastLoadConfig());
				}
			}
		});
		eventBus.addHandler(CreateRelationEvent.TYPE, new CreateRelationEvent.Handler() {
			@Override
			public void onCreate(CreateRelationEvent event) {
				if(!event.isEffectiveInModel())
					for(Edge r : event.getRelations())
						createRelation(r, true);
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
						removeRelation(event, r, event.isRecursive(), true);
				else 
					for(Edge r : event.getRelations())
						onRemoveRelationEffectiveInModel(event, r);
			}
		});
		eventBus.addHandler(ReplaceRelationEvent.TYPE, new ReplaceRelationEvent.Handler() {
			@Override
			public void onReplace(ReplaceRelationEvent event) {
				if(!event.isEffectiveInModel()) {
					replaceRelation(event.getOldRelation(), event.getNewSource());
				} else {
					onReplaceRelationEffectiveInModel(event.getOldRelation(), event.getNewSource());
				}
			}
		});
		/*eventBus.addHandler(RemoveCandidateEvent.TYPE, new RemoveCandidateEvent.Handler() {
			@Override
			public void onRemove(RemoveCandidateEvent event) {
				//removeCandidates(event.getCandidates());
			}
		});*/
		eventBus.addHandler(OrderEdgesEvent.TYPE, new OrderEdgesEvent.Handler() {
			@Override
			public void onOrder(OrderEdgesEvent event) {
				if(!event.isEffectiveInModel()) {
					if(event.getType().equals(type))
						orderEdges(event.getSrc(), event.getEdges());
				} else {
					onOrderEdgesEffectiveInModel(event.getSrc(), event.getEdges(), event.getType());
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

	protected void onCloseRelationsEffectiveInModel(Vertex vertex, Type type) {
		if(type.equals(this.type)) {
			for(Row row : getRowsWhereIncluded(vertex)) 
				updateRow(row);
		}
	}

	protected void onOrderEdgesEffectiveInModel(Vertex v, List<Edge> edges, Type type) {

	}

	protected void orderEdges(Vertex src, final List<Edge> edges) {
		if(leadRowMap.containsKey(src)) {
			if(edges == null || !edges.isEmpty()) {
				Row row = this.leadRowMap.get(src);
				Collections.sort(row.attached, new Comparator<Edge>() {
					@Override
					public int compare(Edge o1, Edge o2) {
						if(!edges.contains(o1))
							return Integer.MAX_VALUE;
						if(!edges.contains(o2))
							return Integer.MAX_VALUE;
						return edges.indexOf(o1) - edges.indexOf(o2);	
					}
				});
				this.updateRow(row);
			} else {
				Row row = this.leadRowMap.get(src);
				row.sort(allRowStore.getLastlastVertexComparator(), 
						allRowStore.getLastVertexSortDir());
				this.updateRow(row);
			}
		}
	}

	protected void replaceRelation(Edge oldRelation, Vertex newSource) {
		if(oldRelation.getType().equals(type)) {
			if(leadRowMap.containsKey(newSource)) {
				if(leadRowMap.containsKey(oldRelation.getSrc())) {
					Row oldRow = leadRowMap.get(oldRelation.getSrc());
					oldRow.remove(oldRelation);
					updateRow(oldRow);
				}
				Row newRow = leadRowMap.get(newSource);
				try {
					addAttached(true, newRow, new Edge(newSource, oldRelation.getDest(), oldRelation.getType(), oldRelation.getOrigin()));
				} catch (Exception e) {
					Alerter.showAlert("Failed to replace relation", "Failed to replace relation");
					return;
				}
			}
		}
	}

	protected void onReplaceRelationEffectiveInModel(Edge oldRelation, Vertex newSource) {
		// TODO Auto-generated method stub
		
	}

	protected void onRemoveRelationEffectiveInModel(GwtEvent<?> event, Edge r) {
		// TODO Auto-generated method stub
		
	}

	protected void onLoadCollectionEffectiveInModel() {
		this.allRowStore.applySort(true);
		this.loader.load();
	}

	protected void onCreateRelationEffectiveInModel(Edge r) {
		// TODO Auto-generated method stub
		
	}

	protected void clearGrid() {
		allRowStore.clear();
		leadRowMap.clear();
		loader.load(loader.getLastLoadConfig());
	}
	
	protected void onLoad(OntologyGraph g) {
		this.reconfigureForAttachedTerms(g.getMaxOutRelations(type, new HashSet<Vertex>(Arrays.asList(g.getRoot(type)))));
		Row rootRow = new Row(type, g.getRoot(type));
		addRow(rootRow, false);
		createEdges(g, g.getRoot(type), new HashSet<String>(), false);
	}
	
	protected void createEdges(OntologyGraph g, Vertex source, Set<String> createdRelations, boolean refresh) {
		for(Edge r : g.getOutRelations(source, type)) {
			String relationIdentifier = r.getSrc().getValue() + " - " + r.getDest().getValue() + " " + r.getType().toString();
			if(!createdRelations.contains(relationIdentifier)) {
				createdRelations.add(relationIdentifier);
				createRelation(r, refresh);
				createEdges(g, r.getDest(), createdRelations, refresh);
			}
		}
	}

	protected void removeRelation(GwtEvent<?> event, Edge r, boolean recursive, boolean refresh) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		if(r.getType().equals(type)) {
			if(r.getSrc().equals(g.getRoot(type))) {
				removeRow(event, r.getDest(), recursive, refresh);
			} else if(leadRowMap.containsKey(r.getSrc())) {
				Row row = leadRowMap.get(r.getSrc());
				removeAttached(event, row, r, recursive, refresh);
			} else {
				Alerter.showAlert("Failed to remove relation", "Failed to remove relation");
			}
		}
	}

	protected void createRelation(Edge r, boolean refresh) {
		if(r.getType().equals(type)) {
			Row row = null;
			if(leadRowMap.containsKey(r.getSrc())) {
				row = leadRowMap.get(r.getSrc());
			} else {
				row = new Row(type, r.getSrc());
				this.addRow(row, refresh);
			}	
			try {
				addAttached(refresh, row, r);
			} catch (Exception e) {
				Alerter.showAlert("Failed to create relation", "Failed to create relation");
				return;
			}
		}
	}

	protected void addRow(Row row, boolean refresh) {
		allRowStore.add(row);
		leadRowMap.put(row.getLead(), row);
		if(refresh)
			loader.load(loader.getLastLoadConfig());
	}
	
	public void removeRow(GwtEvent<?> event,Vertex lead, boolean recursive, boolean refresh) {
		if(leadRowMap.containsKey(lead)) {
			this.removeRow(event, this.leadRowMap.get(lead), recursive, refresh);
		}
	}
	
	public void removeRow(GwtEvent<?> event, Row row, boolean recursive, boolean refresh) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		if(recursive) {
			for(Edge relation : row.getAttached()) {
				if(g.getInRelations(relation.getDest(), type).size() == 1 && leadRowMap.containsKey(relation.getDest())) {
					Row attachedRow = leadRowMap.get(relation.getDest());
					removeRow(event, attachedRow, true, refresh);
				}
			}
		} else {
			Vertex lead = row.getLead();
			List<Edge> inRelations = g.getInRelations(lead, type);
			if(inRelations.size() == 1 && inRelations.get(0).getSrc().equals(g.getRoot(type))) {
				for(Edge e : row.getAttached())
					if(!leadRowMap.containsKey(e.getDest()))
						this.addRow(new Row(type, e.getDest()), refresh);
			} else {
				for(Edge r : g.getInRelations(lead, type)) {
					if(leadRowMap.containsKey(r.getSrc())) {
						try {
							Row targetRow = leadRowMap.get(r.getSrc());
							if(!row.getAttached().isEmpty()) {
								targetRow.add(row.getAttached());
								updateRow(targetRow);
							}
						} catch (Exception e) {
							Alerter.showAlert("Failed to reattach", "Failed to reattach");
						}
					}
				}
			}
		}
		removeRow(row, refresh);
	}

	public void removeRow(Vertex lead, boolean refresh) {
		if(leadRowMap.containsKey(lead))
			this.removeRow(leadRowMap.get(lead), refresh);
	}
	
	protected void removeRow(Row row, boolean refresh) {
		allRowStore.remove(row);
		leadRowMap.remove(row.getLead());
		if(refresh)
			loader.load(loader.getLastLoadConfig());
	}

	protected void addAttached(boolean refresh, Row row, Edge... add) throws Exception {
		row.add(Arrays.asList(add));
		if(refresh)
			updateRow(row);
		/*for(Edge r : add) {
			if(!leadRowMap.containsKey(r.getDest())) {
				Row addRow = new Row(r.getDest());
				this.addRow(addRow);
			}
		}*/
	}
	
	protected void removeAttached(GwtEvent<?> event, Row row, Edge r, boolean recursive, boolean refresh) {
		row.remove(r);
		if(refresh)
			updateRow(row);
		
		if(leadRowMap.containsKey(r.getDest())) {
			Row targetRow = leadRowMap.get(r.getDest());
			removeRow(event, targetRow, recursive, refresh);
		}
	}
	
	protected void updateRow(Row row) {
		if(row.size() <= grid.getColumnModel().getColumnCount()) {
			if(isVisible(row))
				store.update(row);
		} else {
			this.reconfigureForAttachedTerms(row.getAttachedCount());
			if(isVisible(row))
				store.update(row);
		}
	}
	
	protected void updateVertex(Vertex v) {
		for(Row row : this.getRowsWhereIncluded(v))
			if(isVisible(row))
				store.update(row);
	}
	
	public Set<Row> getRowsWhereIncluded(Vertex v) {
		Set<Row> result = new HashSet<Row>();
		if(leadRowMap.containsKey(v))
			result.add(leadRowMap.get(v));
		OntologyGraph g = ModelController.getCollection().getGraph();
		for(Edge inRelations : g.getInRelations(v, type)) {
			if(leadRowMap.containsKey(inRelations.getSrc())) {
				result.add(leadRowMap.get(inRelations.getSrc()));
			}
		}
		return result;
	}
	
	public boolean isVisible(Row row) {
		return this.store.findModel(row) != null;
	}
	
	public List<Row> getRowsWhereAttached(Vertex v) {
		List<Row> result = new LinkedList<Row>();
		OntologyGraph g = ModelController.getCollection().getGraph();
		for(Edge inRelations : g.getInRelations(v, type)) {
			if(leadRowMap.containsKey(inRelations.getSrc())) {
				result.add(leadRowMap.get(inRelations.getSrc()));
			}
		}
		return result;
	}
	
	public Row getRow(int index) {
		return store.get(index);
	}
	
	protected List<Row> getSelection() {
		return new ArrayList<Row>(grid.getSelectionModel().getSelectedItems());
	}

	protected List<Row> getAll() {
		return allRowStore.getAll();
	}

	protected void reconfigureForAttachedTerms(int attachedTermsCount) {
		grid.reconfigure(store, createColumnModel(attachedTermsCount));
	}
	
	private ColumnModel<Row> createColumnModel(int attachedTermsCount) {
		List<ColumnConfig<Row, ?>> columns = new ArrayList<ColumnConfig<Row, ?>>();
		ColumnConfig<Row, Vertex> column1 = new ColumnConfig<Row, Vertex>(new ValueProvider<Row, Vertex>() {
			@Override
			public Vertex getValue(Row object) {
				return object.getLead();
			}
			@Override
			public void setValue(Row object, Vertex value) { }
			@Override
			public String getPath() {
				return "lead";
			}
		}, colWidth, SafeHtmlUtils.fromTrustedString("<b>" + type.getSourceLabel() + "</b>"));
		column1.setCellPadding(false);
		column1.setSortable(true);
		column1.setHideable(false);
		column1.setGroupable(false);
		column1.setMenuDisabled(true);
		LeadCell cell = createLeadCell();
		column1.setCell(cell);
		columns.add(column1);
		for(int i = 1; i <= attachedTermsCount; i++)
			columns.add(createColumnI(i));
		return new ColumnModel<Row>(columns);
	}
	
	protected LeadCell createLeadCell() {
		LeadCell leadCell = new LeadCell(eventBus, this, new ValueProvider<Vertex, String>() {
			@Override
			public String getValue(Vertex object) {
				return object.getValue();
			}
			@Override
			public void setValue(Vertex object, String value) { }
			@Override
			public String getPath() {
				return "lead";
			}
		}, new DefaultMenuCreator(eventBus, this));
		return leadCell;
	}

	private ColumnModel<Row> createColumnModel(Collection<Row> rows) {	
		return createColumnModel(getMaxAttachedTermsCount(rows));
	}
	
	/**
	 * create the i-th column, its path is term-i
	 * @param i
	 * @return
	 */
	private ColumnConfig<Row, ?> createColumnI(final int i) {
		ColumnConfig<Row, Row> config = new ColumnConfig<Row, Row>(new ValueProvider<Row, Row>() {
			@Override
			public Row getValue(Row object) {
				return object;
			}
			@Override
			public void setValue(Row object, Row value) { }
			@Override
			public String getPath() {
				return "attached-" + i;
			}
		}, colWidth, SafeHtmlUtils.fromTrustedString("<b>" + type.getTargetLabel() + "-" + i + "</b>"));
		config.setCellPadding(false);
		AttachedCell cell = createAttachedCell(i - 1);
		config.setCell(cell);
		config.setSortable(false);
		config.setHideable(false);
		config.setGroupable(false);
		config.setMenuDisabled(true);
		return config;
	}
	
	protected AttachedCell createAttachedCell(int i) {
		AttachedCell attachedCell = new AttachedCell(eventBus, this, i);
		return attachedCell;
	}
		
	private int getMaxAttachedTermsCount(Collection<Row> rows) {
		if(rows.isEmpty())
			return 0;
		Row maxRow = rows.iterator().next();
		for(Row row : rows) {
			int size = row.getAttachedCount();
			if(size > maxRow.getAttachedCount())
				maxRow = row;
		}
		return maxRow.getAttachedCount();
	}

	protected SimpleContainer createCreateRowContainer() {
		createRowContainer = new SimpleContainer();
		createRowContainer.setTitle("Drop here to create new row");
		com.google.gwt.user.client.ui.Label dropLabel = new com.google.gwt.user.client.ui.Label("Drop here to create new row");
		dropLabel.getElement().getStyle().setLineHeight(30, Unit.PX);
		createRowContainer.setWidget(dropLabel);
		createRowContainer.setHeight(30);
		createRowContainer.getElement().getStyle().setBorderWidth(1, Unit.PX);
		createRowContainer.getElement().getStyle().setBorderStyle(BorderStyle.DASHED);
		createRowContainer.getElement().getStyle().setBorderColor("gray");
		createRowContainer.getElement().getStyle().setProperty("mozMorderMadius", "7px");
		createRowContainer.getElement().getStyle().setProperty("webkitBorderRadius", "7px");
		createRowContainer.getElement().getStyle().setProperty("borderRadius", "7px");
		createRowContainer.getElement().getStyle().setBackgroundColor("#ffffcc");
		return createRowContainer;
	}

	@Override
	public Widget asWidget() {
		return vlc;
	}

	public Type getType() {
		return type;
	}

	public void refreshHeader() {
        grid.getView().refresh(true);
	}
	
}
