package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DragSource;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.GridDragSource.GridDragSourceMessages;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox.MessageBoxAppearance;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer.AccordionLayoutAppearance;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
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
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ShowRelationsEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.TextTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

public class PartsGrid extends MenuTermsGrid {

	private Images images = GWT.create(Images.class);
	private AccordionLayoutAppearance appearance = GWT.<AccordionLayoutAppearance> create(AccordionLayoutAppearance.class);
	
	public PartsGrid(EventBus eventBus) {
		super(eventBus, Type.PART_OF);
	}
	
	@Override
	protected void onLoad(final OntologyGraph g) {
		clearGrid();
		final int maxOutRelations = g.getMaxOutRelations(type, new HashSet<Vertex>(Arrays.asList(g.getRoot(type))));
		Timer timer = new Timer() {
			@Override
			public void run() {
				reconfigureForAttachedTerms(maxOutRelations);
				createEdges(g, g.getRoot(type), new HashSet<String>(), false);
				allRowStore.applySort(true);
				loader.load();
			}
		};
		timer.schedule(100);
		
	}

	@Override
	protected void createRelation(Edge r, boolean refresh) {		
		if(r.getType().equals(type)) {
			OntologyGraph g = ModelController.getCollection().getGraph();
			Vertex dest = r.getDest();
			Vertex src = r.getSrc();
			String newValue = src + " " + dest;
			
			List<Edge> parentRelations = g.getInRelations(dest, Type.PART_OF);
			parentRelations.remove(r);
			if(!parentRelations.isEmpty()) {			
				for(Edge parentRelation : parentRelations) {
					Vertex parentSrc = parentRelation.getSrc();
					Vertex disambiguatedDest = new Vertex(parentSrc + " " + dest);
					
					replace(parentSrc, dest, disambiguatedDest);
				}
				
				r = new Edge(src, new Vertex(newValue), r.getType(), r.getOrigin());
				if(r.getSrc().equals(g.getRoot(type))) {
					if(!leadRowMap.containsKey(r.getDest()))
						this.addRow(new Row(type, r.getDest()), refresh);
				} else {
					super.createRelation(r, refresh);
				}
			} else {
				if(r.getSrc().equals(g.getRoot(type))) {
					if(!leadRowMap.containsKey(r.getDest()))
						this.addRow(new Row(type, r.getDest()), refresh);
				} else {
					super.createRelation(r, refresh);
				}
			}
		}
	}

	private void replace(Vertex src, Vertex dest, Vertex newDest) {
		leadRowMap.get(src).replaceAttachedDest(dest, newDest);
		updateRow(leadRowMap.get(src));
		
		if(leadRowMap.containsKey(dest)) {
			leadRowMap.get(dest).setLead(newDest);
			leadRowMap.put(newDest, leadRowMap.get(dest));
			leadRowMap.remove(dest);
			updateRow(leadRowMap.get(newDest));
			
			for(Edge r : leadRowMap.get(newDest).getAttached()) {
				if(r.getDest().getValue().startsWith(dest.getValue())) {
					replace(dest, r.getDest(), new Vertex(newDest.getValue() + " " + r.getDest().getValue()));
				}
			}
		}
	}
	
	@Override
	protected SimpleContainer createCreateRowContainer() {
		createRowContainer = new SimpleContainer();
		createRowContainer.setTitle("Drop here to create new parent");
		com.google.gwt.user.client.ui.Label dropLabel = new com.google.gwt.user.client.ui.Label("Drop here to create new parent");
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
		return "parent, part 1, part 2, ...[e.g. flower, calyx, corolla]"; 
	}
	
	@Override
	protected void createRowFromEdgeDrop(Edge edge) {
		if(!leadRowMap.containsKey(edge.getDest()))
			this.addRow(new Row(type, edge.getDest()), true);
	}
	
	@Override
	protected void onEdgeOnGridDrop(final Edge dropEdge, Element element, final Row row, final Vertex targetVertex) {
		final OntologyGraph g = ModelController.getCollection().getGraph();
		Menu menu = new Menu();

		MenuItem category = new MenuItem("Create " + Type.SUBCLASS_OF.getTargetLabel());
		category.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				fire(new CreateRelationEvent(new Edge(targetVertex, dropEdge.getDest(),Type.SUBCLASS_OF, Origin.USER)));
			}
		});
		menu.add(category);
		
		Edge existingRelation = new Edge(targetVertex, dropEdge.getDest(), type, Origin.USER);
		Edge reverseExistingRelation = new Edge(dropEdge.getDest(), targetVertex, type, Origin.USER);
		if(!g.existsRelation(existingRelation) && !g.existsRelation(reverseExistingRelation)) { 
			MenuItem createPart = new MenuItem("Create " + type.getTargetLabel());
			createPart.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					fire(new CreateRelationEvent(new Edge(targetVertex, dropEdge.getDest(), type, Origin.USER)));
				}
			});
			menu.add(createPart);
			
			MenuItem movePart = new MenuItem("Move " + type.getTargetLabel());
			movePart.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					fire(new ReplaceRelationEvent(dropEdge, targetVertex));
				}
			});
			menu.add(movePart);
		}
		
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
