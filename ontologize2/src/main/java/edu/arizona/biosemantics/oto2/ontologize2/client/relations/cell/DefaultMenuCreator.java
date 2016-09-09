package edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CloseRelationsEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SelectTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent.FilterTarget;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public class DefaultMenuCreator implements LeadCell.MenuCreator {

	protected EventBus eventBus;
	protected TermsGrid termsGrid;
	protected MenuItem addItem;
	protected MenuItem removeAllItem;
	protected CheckMenuItem closeItem;
	protected MenuItem context;
	protected MenuItem removeItem;
	protected MenuItem filterItem;

	public DefaultMenuCreator(EventBus eventBus, TermsGrid termsGrid) {
		this.eventBus = eventBus;
		this.termsGrid = termsGrid;
	}
	
	@Override
	public Menu create(int rowIndex) {
		Menu menu = new Menu();
		final OntologyGraph g = ModelController.getCollection().getGraph();
		final Row row = termsGrid.getRow(rowIndex);
		final boolean closed = ModelController.getCollection().getGraph().isClosedRelations(row.getLead(), termsGrid.getType());
		
		addItem = new MenuItem("Add " + termsGrid.getType().getTargetLabel());
		addItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				OntologyGraph g = ModelController.getCollection().getGraph();
				if(g.isClosedRelations(row.getLead(), termsGrid.getType())) {
					Alerter.showAlert("Create Relation", "Can not create relation for a closed row.");
					return;
				}
				
				final PromptMessageBox box = Alerter.showPromptMessageBox("Add " + termsGrid.getType().getTargetLabel(), 
						"Add " + termsGrid.getType().getTargetLabel());
				box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						String text = box.getTextField().getText().trim();
						if(text.isEmpty())
							Alerter.showAlert("Add " + termsGrid.getType().getTargetLabel(), "Cannot create empty " + termsGrid.getType().getTargetLabel());
						else
							termsGrid.fire(new CreateRelationEvent(
									new Edge(row.getLead(), new Vertex(box.getTextField().getText()), termsGrid.getType(), Origin.USER)));
					}
				});
			}
		});
		addItem.setEnabled(!closed);
		
		removeItem = new MenuItem("Remove row");
		removeItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				removeAll(row, true);
			}
		});
		
		removeAllItem = new MenuItem("Remove all " + termsGrid.getType().getTargetLabelPlural());
		removeAllItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				removeAll(row, false);
			}
		});
		removeAllItem.setEnabled(!closed);
		
		closeItem = new CheckMenuItem("Close");
		closeItem.setChecked(closed);
		closeItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				OntologyGraph g = ModelController.getCollection().getGraph();
				Vertex targetVertex = row.getLead();
				termsGrid.fire(new CloseRelationsEvent(targetVertex, termsGrid.getType(), !closed)); 
			}
		});
		
		
		/*MenuItem removeItem = new MenuItem("Remove row");
		removeItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				OntologyGraph g = ModelController.getCollection().getGraph();
				Vertex targetVertex = row.getLead();
				for(final Relation r : g.getOutRelations(targetVertex, termsGrid.getType())) {
					if(g.getInRelations(r.getDestination(), termsGrid.getType()).size() <= 1) {
						if(g.getOutRelations(r.getDestination(), termsGrid.getType()).isEmpty()) {
							termsGrid.fire(new RemoveRelationEvent(false, r));
						} else {
							doAskForRecursiveRemoval(r);
						}
					} else {
						termsGrid.fire(new RemoveRelationEvent(false, r));
					}
				} 
			}
		});*/
		
		filterItem = new MenuItem("Filter: " + row.getLead());
		Menu filterMenu = new Menu();
		filterItem.setSubMenu(filterMenu);
		for(final FilterTarget filterTarget : FilterTarget.values()) {
			MenuItem menuItem = new MenuItem(filterTarget.getDisplayName());
			menuItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					eventBus.fireEvent(new FilterEvent(row.getLead().getValue(), filterTarget, Type.values()));
				}
			});
			filterMenu.add(menuItem);
		}
		
		context = new MenuItem("Show Term Context");
		context.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				termsGrid.fire(new SelectTermEvent(row.getLead().getValue()));
			}
		});
		//menu.add(removeRowItem);
		menu.add(addItem);
		menu.add(removeItem);
		menu.add(removeAllItem);
		menu.add(closeItem);
		menu.add(filterItem);
		menu.add(context);
		
		return menu;
	}
	
	protected void removeAll(Row row, final boolean removeRowHead) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		final Vertex targetVertex = row.getLead();
		
		if(removeRowHead) {
			Edge rootEdge = new Edge(g.getRoot(termsGrid.getType()), row.getLead(), 
					termsGrid.getType(), Origin.USER);
			if(g.existsRelation(rootEdge)) {
				this.doAskForRecursiveRemoval(rootEdge);		
			} else {
				removeAllOutgoing(targetVertex);
				termsGrid.removeRow(targetVertex, true);
			}
		} else {
			removeAllOutgoing(targetVertex);
		}
	}

	private void removeAllOutgoing(Vertex targetVertex) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		for(final Edge r : g.getOutRelations(targetVertex, termsGrid.getType())) {
			if(g.getInRelations(r.getDest(), termsGrid.getType()).size() <= 1) {
				if(g.getOutRelations(r.getDest(), termsGrid.getType()).isEmpty()) {
					termsGrid.fire(new RemoveRelationEvent(false, r));
				} else {
					doAskForRecursiveRemoval(r);
				}
			} else {
				termsGrid.fire(new RemoveRelationEvent(false, r));
			}
		}
	}

	protected void doAskForRecursiveRemoval(final Edge relation) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		List<Vertex> targets = new LinkedList<Vertex>();
		for(Edge r : g.getOutRelations(relation.getDest(), termsGrid.getType())) 
			targets.add(r.getDest());
		final MessageBox box = Alerter.showYesNoCancelConfirm("Remove " + termsGrid.getType().getTargetLabel(), 
				"You are about to remove " + termsGrid.getType().getTargetLabel() + "<i>" + relation.getDest() + "</i>"
				+ " from <i>" + relation.getSrc() + "</i>.\n" +
				"Do you want to remove all " + termsGrid.getType().getTargetLabelPlural() + " of <i>" + relation.getDest() + "</i> (Yes)" +
				" or make them instead a " + termsGrid.getType().getTargetLabel() + " of <i>" + relation.getSrc() + "</i> (No)?");
		box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				termsGrid.fire(new RemoveRelationEvent(true, relation));
				/*for(GwtEvent<Handler> e : createRemoveEvents(true, relation)) {
					termsGrid.fire(e);
				}*/
				box.hide();
			}
		});
		box.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				termsGrid.fire(new RemoveRelationEvent(false, relation));
				/*for(GwtEvent<Handler> e : createRemoveEvents(false, relation)) {
					termsGrid.fire(e);
				}*/
				box.hide();
			}
		});
		box.getButton(PredefinedButton.CANCEL).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				box.hide();
			}
		});
	}

}
