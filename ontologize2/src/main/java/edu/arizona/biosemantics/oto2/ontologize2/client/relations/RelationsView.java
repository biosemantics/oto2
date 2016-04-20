package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.oto2.ontologize2.client.TermsGrid;
import edu.arizona.biosemantics.oto2.ontologize2.client.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class RelationsView extends SimpleContainer {

	private EventBus eventBus;

	public RelationsView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		final TermsGrid termsGrid = new TermsGrid();
		
		final List<Row> rows = new LinkedList<Row>();
		List<Term> terms1 = new ArrayList<Term>();
		terms1.add(new Term("test", "", "/src/test"));
		Row row1 = new Row(1, terms1);
		rows.add(row1);
		termsGrid.setRows(rows);
		

		List<Term> terms2 = new ArrayList<Term>();
		terms2.add(new Term("test1", "", "/src/test"));
		terms2.add(new Term("test2", "", "/src/test"));
		terms2.add(new Term("test3", "", "/src/test"));
		Row row2 = new Row(2, terms2);
		rows.add(row2);
		
		
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add(termsGrid);
		TextButton button = new TextButton("test");
		button.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				termsGrid.setRows(rows);
			}
		});
		vlc.add(button);
		
		this.add(vlc);
	}

}
