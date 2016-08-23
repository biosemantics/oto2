package edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

public class SynonymMenuCreator extends DefaultMenuCreator implements LeadCell.MenuCreator {

	protected Item removeRowItem;

	public SynonymMenuCreator(EventBus eventBus, TermsGrid termsGrid) {
		super(eventBus, termsGrid);
	}
	
	@Override
	public Menu create(int rowIndex) {
		Menu menu = super.create(rowIndex);
		menu.clear();
		
		final OntologyGraph g = ModelController.getCollection().getGraph();
		final Row row = termsGrid.getRow(rowIndex);
		removeRowItem = new MenuItem("Remove row");
		removeRowItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				OntologyGraph g = ModelController.getCollection().getGraph();
				Vertex targetVertex = row.getLead();
				eventBus.fireEvent(new RemoveRelationEvent(true, 
						g.getInRelations(targetVertex, termsGrid.getType()).iterator().next()));
			}
		});
		
		menu.add(addItem);
		menu.add(removeRowItem);
		menu.add(removeAllItem);
		menu.add(context);
		
		return menu;
	}
}
