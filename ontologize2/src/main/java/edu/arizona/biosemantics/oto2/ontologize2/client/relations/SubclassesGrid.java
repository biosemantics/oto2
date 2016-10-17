package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.common.CompositeModifyEventForSynonymCreator;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ShowRelationsEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent.RemoveMode;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell.LeadCell;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell.SubclassMenuCreator;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell.SynonymMenuCreator;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;

public class SubclassesGrid extends MenuTermsGrid {

	protected Map<GwtEvent<?>, Set<Vertex>> refreshNodes = new HashMap<GwtEvent<?>, Set<Vertex>>();
	
	public SubclassesGrid(EventBus eventBus) {
		super(eventBus, Type.SUBCLASS_OF);
	}
	
	@Override
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
		}, new SubclassMenuCreator(eventBus, this), highlight);
		return leadCell;
	}
	
	@Override
	protected void onLoad(OntologyGraph g) {
		clearGrid();
		this.reconfigureForAttachedTerms(g.getMaxOutRelations(type, new HashSet<Vertex>(Arrays.asList(g.getRoot(type)))));
		createEdges(g, g.getRoot(type), new HashSet<String>(), false);
		allRowStore.applySort(true);
		loader.load();
		for(Vertex v : g.getVertices()) {
			List<Edge> inRelations = g.getInRelations(v, type);
			if(inRelations.size() > 1) {
				for(Row row : getRowsWhereIncluded(v)) 
					updateRow(row);
			}
		}
	}
		
	@Override
	protected void createRelation(Edge r, boolean refresh) {
		if(r.getType().equals(type)) {
			OntologyGraph g = ModelController.getCollection().getGraph();
			if(r.getSrc().equals(g.getRoot(type))) {
				if(!leadRowMap.containsKey(r.getDest()))
					this.addRow(new Row(type, r.getDest()), refresh);
			} else {
				super.createRelation(r, refresh);
			}
		}
		
		if(r.getType().equals(Type.PART_OF)) {
			OntologyGraph g = ModelController.getCollection().getGraph();
			Vertex dest = r.getDest();
			Vertex src = r.getSrc();
			String newValue = src + " " + dest;
			
			List<Edge> parentRelations = g.getInRelations(dest, Type.PART_OF);
			if(!parentRelations.isEmpty()) {			
				//super.createRelation(new Edge(g.getRoot(Type.SUBCLASS_OF), dest, Type.SUBCLASS_OF, Origin.USER));
				for(Edge parentRelation : parentRelations) {
					Vertex parentSrc = parentRelation.getSrc();
					Vertex disambiguatedDest = new Vertex(parentSrc + " " + dest);
					
					super.createRelation(new Edge(dest, disambiguatedDest, Type.SUBCLASS_OF, Origin.USER), refresh);
				}
				super.createRelation(new Edge(dest, new Vertex(newValue), Type.SUBCLASS_OF, Origin.USER), refresh);
			}
		}
	}
	
	@Override
	protected void onCreateRelationEffectiveInModel(Edge r) {
		if(r.getType().equals(type)) {
			Vertex dest = r.getDest();
			for(Row row : getRowsWhereIncluded(dest)) 
				updateRow(row);
		}
	}
	
	@Override
	protected void onRemoveRelationEffectiveInModel(GwtEvent<?> event, Edge r) {
		if(r.getType().equals(type)) {
			Vertex dest = r.getDest();
			for(Row row : getRowsWhereIncluded(dest)) 
				updateRow(row);
			OntologyGraph g = ModelController.getCollection().getGraph();
			if(this.refreshNodes.containsKey(event)) {
				for(Vertex visibleNode : refreshNodes.get(event)) {
					this.updateVertex(visibleNode);
				}
				refreshNodes.remove(event);
			}
		}
	}
	
	@Override
	protected SimpleContainer createCreateRowContainer() {
		createRowContainer = new SimpleContainer();
		createRowContainer.setTitle("Drop here to create new superclass");
		com.google.gwt.user.client.ui.Label dropLabel = new com.google.gwt.user.client.ui.Label("Drop here to create new superclass");
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
	protected String getDefaultImportText() {
		return "superclass, subclass 1, subclass 2, ...[e.g. fruits, simple fruits, aggregate fruits, composite fruits]"; 
	}
		
	@Override
	protected void createRowFromEdgeDrop(Edge edge) {
		if(!leadRowMap.containsKey(edge.getDest()))
			this.addRow(new Row(type, edge.getDest()), true);
	}
	
	@Override
	protected void removeAttached(GwtEvent<?> event, Row row, Edge r, RemoveMode removeMode, boolean refresh) {
		row.remove(r);
		updateRow(row);
		
		OntologyGraph g = ModelController.getCollection().getGraph();
		if(leadRowMap.containsKey(r.getDest())) {
			if(g.getInRelations(r.getDest(), type).size() <= 1) {
				Row targetRow = leadRowMap.get(r.getDest());
				removeRow(event, targetRow, removeMode, refresh);
			}
		}
	}
	
	@Override
	public void removeRow(GwtEvent<?> event, Row row, RemoveMode removeMode, boolean refresh) {
		if(!this.refreshNodes.containsKey(event))
			this.refreshNodes.put(event, new HashSet<Vertex>());
		OntologyGraph g = ModelController.getCollection().getGraph();
		switch(removeMode) {
			case NONE:
				break;
			case REATTACH_TO_AVOID_LOSS:
				Vertex lead = row.getLead();
				List<Edge> inRelations = g.getInRelations(lead, type);
				if(inRelations.size() == 1 && inRelations.get(0).getSrc().equals(g.getRoot(type))) {
					List<Vertex> refreshNodes = new LinkedList<Vertex>();
					for(Edge e : row.getAttached()) {
						List<Edge> in = g.getInRelations(e.getDest(), type);
						if(in.size() == 2) 
							refreshNodes.add(e.getDest());
					}
					this.refreshNodes.get(event).addAll(refreshNodes);
				}
				break;
			case RECURSIVE:
				List<Vertex> refreshNodes = new LinkedList<Vertex>();
				List<Vertex> recursiveDestinations = g.getAllDestinations(row.getLead(), type);
				for(Vertex dest : recursiveDestinations) 
					if(g.getInRelations(dest, type).size() == 2)
						refreshNodes.add(dest);
				this.refreshNodes.get(event).addAll(refreshNodes);
				break;
			default:
				break;
		}
		super.removeRow(event, row, removeMode, refresh);
	}
	
	@Override
	protected void onEdgeOnGridDrop(final Edge dropEdge, Element element, final Row row, final Vertex targetVertex) {
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
