package edu.arizona.biosemantics.oto.oto.client.categorize;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.state.client.GridStateHandler;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermSelectEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermSelectEvent.TermSelectHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Context;
import edu.arizona.biosemantics.oto.oto.shared.model.ContextProperties;
import edu.arizona.biosemantics.oto.oto.shared.model.Location;
import edu.arizona.biosemantics.oto.oto.shared.model.Ontology;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class ContextView extends Composite {

	private static final ContextProperties contextProperties = GWT.create(ContextProperties.class);
	private ListStore<Context> store = new ListStore<Context>(contextProperties.key());
	
	private EventBus eventBus;
	
	public ContextView(EventBus eventBus) {
		this.eventBus = eventBus;
		ColumnConfig<Context, String> sourceColumn = new ColumnConfig<Context, String>(contextProperties.source(), 50, SafeHtmlUtils.fromTrustedString("<b>Source</b>"));
		ColumnConfig<Context, String> sentenceColumn = new ColumnConfig<Context, String>(contextProperties.sentence(), 100, SafeHtmlUtils.fromTrustedString("<b>Sentence</b>"));
		List<ColumnConfig<Context, ?>> columns = new ArrayList<ColumnConfig<Context, ?>>();
		columns.add(sourceColumn);
		columns.add(sentenceColumn);
		ColumnModel<Context> columnModel = new ColumnModel<Context>(columns);
		ListStore<Context> store = new ListStore<Context>(contextProperties.key());
		Grid<Context> grid = new Grid<Context>(store, columnModel);
		grid.getView().setAutoExpandColumn(sentenceColumn);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.setBorders(false);
		grid.setColumnReordering(false);
		grid.setStateful(true);
		grid.setStateId("contextsGrid");
		GridStateHandler<Context> state = new GridStateHandler<Context>(grid);
		state.loadState();
		this.initWidget(grid);
		
		bindEvents();
	}
	
	public void setContexts(List<Context> contexts) {
		store.clear();
		store.addAll(contexts);
	}

	private void bindEvents() {
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.TermSelectHandler() {
			@Override
			public void onSelect(Term term) {
				//retrieving the locations for the specific term from somewhere / server
				List<Context> contexts = new LinkedList<Context>();
				setContexts(contexts);
			}
		});
	}
}
