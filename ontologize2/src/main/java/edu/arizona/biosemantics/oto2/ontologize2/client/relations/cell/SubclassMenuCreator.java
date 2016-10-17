package edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell;

import java.util.LinkedList;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.SelectTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.OrderEdgesEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;

public class SubclassMenuCreator extends DefaultMenuCreator implements LeadCell.MenuCreator {

	protected MenuItem orderItem;

	public SubclassMenuCreator(EventBus eventBus, TermsGrid termsGrid) {
		super(eventBus, termsGrid);
	}
	
	@Override
	public Menu create(int rowIndex) {
		Menu menu = super.create(rowIndex);
		menu.clear();		
		final Row row = termsGrid.getRow(rowIndex);
		
		orderItem = new MenuItem("Order Subclasses");
		orderItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				Dialog dialog = new Dialog();
				dialog.setWidth(300);
				dialog.setPredefinedButtons(PredefinedButton.CANCEL, PredefinedButton.CLOSE, PredefinedButton.OK);
				final SortVertexView sortVertexView = new SortVertexView(row.getAttached());
				dialog.setWidget(sortVertexView);
				dialog.setHideOnButtonClick(true);
				dialog.show();
				
				dialog.getButton(PredefinedButton.CLOSE).setText("Remove Order");
				dialog.getButton(PredefinedButton.CLOSE).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						termsGrid.fire(new OrderEdgesEvent(row.getLead(), new LinkedList<Edge>(), termsGrid.getType()));
					}
				});
				dialog.getButton(PredefinedButton.OK).setText("Set Order");
				dialog.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						termsGrid.fire(new OrderEdgesEvent(row.getLead(), sortVertexView.getEdges(), termsGrid.getType()));
					}
				});
			}
		});
		
		menu.add(addItem);
		menu.add(removeItem);
		menu.add(removeAllItem);
		menu.add(orderItem);
		menu.add(closeItem);
		menu.add(filterItem);
		//menu.add(synonymsItem);
		menu.add(context);
		
		return menu;
	}
}
