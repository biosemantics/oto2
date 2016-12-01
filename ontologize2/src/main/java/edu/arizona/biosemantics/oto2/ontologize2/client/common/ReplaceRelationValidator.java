package edu.arizona.biosemantics.oto2.ontologize2.client.common;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.Images;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;

public class ReplaceRelationValidator {

	private EventBus eventBus;
	private Images images = GWT.create(Images.class);

	public ReplaceRelationValidator(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	public void validateAndFire(ReplaceRelationEvent event) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		if(g.isClosedRelations(event.getOldRelation().getSrc(), event.getOldRelation().getType()) || g.isClosedRelations(event.getNewSource(), event.getOldRelation().getType())) {
			Alerter.showAlert("Create Relation", "Can not create relation for a closed row.");
			return;
		}
		switch(event.getOldRelation().getType()) {
		case PART_OF:
			validatePartOfAndFire(event);
			break;
		case SUBCLASS_OF:
			validateSubclassAndFire(event);
			break;
		case SYNONYM_OF:
			validateSynonymAndFire(event);
			break;
		}
	}

	private void validateSynonymAndFire(final ReplaceRelationEvent e) {
		final CompositeModifyEventForSynonymCreator compositeModifyEventForSynonymCreator = new CompositeModifyEventForSynonymCreator();		
		OntologyGraph g = ModelController.getCollection().getGraph();
		try {
			g.isValidSynonym(new Edge(e.getNewSource(), e.getOldRelation().getDest(), Type.PART_OF, Origin.USER), false, true);
			eventBus.fireEvent(e);
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

	private void validateSubclassAndFire(final ReplaceRelationEvent e) {
		OntologyGraph g = ModelController.getCollection().getGraph();		
		try {
			g.isValidSubclass(new Edge(e.getNewSource(), e.getOldRelation().getDest(), Type.PART_OF, Origin.USER));
			eventBus.fireEvent(e);
		} catch(Exception ex) {
			final MessageBox box = Alerter.showAlert("Create subclass", ex.getMessage());
			box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					box.hide();
				}
			});
		}
	}

	private void validatePartOfAndFire(final ReplaceRelationEvent e) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		try {
			g.isValidPartOf(new Edge(e.getNewSource(), e.getOldRelation().getDest(), Type.PART_OF, Origin.USER));
			eventBus.fireEvent(e);
		} catch(Exception ex) {
			final MessageBox box = Alerter.showAlert("Create part", ex.getMessage());
			box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					box.hide();
				}
			});
		}		
	}
	
}
