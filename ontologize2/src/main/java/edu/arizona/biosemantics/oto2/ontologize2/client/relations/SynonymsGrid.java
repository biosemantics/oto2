package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.common.TextAreaMessageBox;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent.RemoveMode;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell.DefaultMenuCreator;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell.LeadCell;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell.SynonymMenuCreator;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public class SynonymsGrid extends MenuTermsGrid {

	public SynonymsGrid(EventBus eventBus) {
		super(eventBus, Type.SYNONYM_OF);
		
		TextButton addButton = new TextButton("Add Preferred Term");
		addButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final PromptMessageBox box = Alerter.showPromptMessageBox("Add Preferred Term", "Term");
				box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						Vertex source = new Vertex(type.getRootLabel());
						Vertex target = new Vertex(box.getTextField().getText());
						Edge relation = new Edge(source, target, type, Origin.USER);
						
						if(ModelController.getCollection().getGraph().isClosedRelations(source, type)) {
							Alerter.showAlert("Create Relation", "Can not create relation for a closed row.");
							return;
						}
						CreateRelationEvent createRelationEvent = new CreateRelationEvent(relation);
						fire(createRelationEvent);
					}
				});
			}
		});
		
		buttonBar.insert(addButton, 0);
	}
	
	@Override
	public void fire(GwtEvent<? extends EventHandler> e) {
		if(e instanceof CreateRelationEvent) {
			final CreateRelationEvent createRelationEvent = (CreateRelationEvent)e;
			OntologyGraph g = ModelController.getCollection().getGraph();
			for(Edge r : createRelationEvent.getRelations()) {
				try {
					g.isValidSynonym(r);
					eventBus.fireEvent(createRelationEvent);
				} catch(Exception ex) {
					final MessageBox box = Alerter.showAlert("Create synonym", ex.getMessage());
					box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							box.hide();
						}
					});
				}
			}
		} else if(e instanceof RemoveRelationEvent) {
			eventBus.fireEvent(e);
		} else {
			eventBus.fireEvent(e);
		}
	}
	
	@Override
	protected void onLoad(OntologyGraph g) {
		clearGrid();
		this.reconfigureForAttachedTerms(g.getMaxOutRelations(type, new HashSet<Vertex>(Arrays.asList(g.getRoot(type)))));
		createEdges(g, g.getRoot(type), new HashSet<String>(), false);
		allRowStore.applySort(true);
		loader.load();
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
	}
	
	@Override
	protected void addAttached(boolean refresh, Row row, Edge... add) throws Exception {
		row.add(Arrays.asList(add));
		if(refresh)
			updateRow(row);
	}
	
	@Override
	protected SimpleContainer createCreateRowContainer() {
		createRowContainer = new SimpleContainer();
		createRowContainer.setTitle("Drop here to create new preferred term");
		com.google.gwt.user.client.ui.Label dropLabel = new com.google.gwt.user.client.ui.Label("Drop here to create new preferred term");
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
		return "preferred term, synonym 1, synonym 2, ...[e.g. apex, tip, appex]"; 
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
		}, new SynonymMenuCreator(eventBus, this), highlight);
		return leadCell;
	}
	
	@Override
	protected void removeRelation(GwtEvent<?> event, Edge r, RemoveMode removeMode, boolean refresh) {
		OntologyGraph graph = ModelController.getCollection().getGraph();
		if(r.getSrc().equals(graph.getRoot(type))) {
			if(leadRowMap.containsKey(r.getDest())) {
				Row row = leadRowMap.get(r.getDest());
				this.removeRow(event, row, RemoveMode.RECURSIVE, refresh);
			}
		} else
			super.removeRelation(event, r, removeMode, refresh);
	}
	
	@Override
	protected void replaceRelation(Edge oldRelation, Vertex newSource) {
		if(oldRelation.getType().equals(type)) {	
			List<Vertex> newAttached = new LinkedList<Vertex>();
			newAttached.add(oldRelation.getDest());
			
			OntologyGraph g = ModelController.getCollection().getGraph();		
			if(oldRelation.getSrc().equals(g.getRoot(type))) {
				if(leadRowMap.containsKey(oldRelation.getDest())) {
					Row oldRow = leadRowMap.get(oldRelation.getDest());
					for(Edge relation : oldRow.getAttached()) 
						newAttached.add(relation.getDest());
					this.removeRow(oldRow, true);
				}
			} else if(leadRowMap.containsKey(oldRelation.getSrc())) {
				Row oldRow = leadRowMap.get(oldRelation.getSrc());
				oldRow.remove(oldRelation);
				updateRow(oldRow);
			}
			if(newSource.equals(g.getRoot(type))) {
				if(!leadRowMap.containsKey(oldRelation.getDest())) {
					Row newRow = new Row(type, oldRelation.getDest());
					this.addRow(newRow, true);
				}
			} else if(leadRowMap.containsKey(newSource)) {
				Row newRow = leadRowMap.get(newSource);
				try {
					for(Vertex newAttach : newAttached)
						addAttached(true, newRow, new Edge(newSource, newAttach, oldRelation.getType(), oldRelation.getOrigin()));
				} catch (Exception e) {
					Alerter.showAlert("Failed to replace relation", "Failed to replace relation");
					return;
				}
			} else {
				Alerter.showAlert("Failed to replace relation", "Failed to replace relation");
			}
		}
	}
	
	@Override
	protected void createRowFromEdgeDrop(Edge oldRelation) {
		OntologyGraph g = ModelController.getCollection().getGraph();		
		if(oldRelation.getSrc().equals(g.getRoot(type))) {
			return;
		} else {
			if(g.isClosedRelations(oldRelation.getSrc(), type)) {
				Alerter.showAlert("Create Relation", "Can not create relation for a closed row.");
				return;
			}
			fire(new ReplaceRelationEvent(oldRelation, g.getRoot(type)));
			//fire(new RemoveRelationEvent(false, oldRelation));
			//fire(new CreateRelationEvent(new Edge(g.getRoot(type), oldRelation.getDest(), type, Origin.USER)));
		}	
	}
}
