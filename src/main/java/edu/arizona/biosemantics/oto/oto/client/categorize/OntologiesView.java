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
import edu.arizona.biosemantics.oto.oto.shared.model.Location;
import edu.arizona.biosemantics.oto.oto.shared.model.Ontology;
import edu.arizona.biosemantics.oto.oto.shared.model.OntologyProperties;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class OntologiesView extends Composite {

	private static final OntologyProperties ontologyProperties = GWT.create(OntologyProperties.class);
	private ListStore<Ontology> store = new ListStore<Ontology>(ontologyProperties.key());

	private EventBus eventBus;
	
	public OntologiesView(EventBus eventBus) {
		this.eventBus = eventBus;
		ColumnConfig<Ontology, String> categoryColumn = new ColumnConfig<Ontology, String>(ontologyProperties.category(), 50, SafeHtmlUtils.fromTrustedString("<b>Category</b>"));
		ColumnConfig<Ontology, String> definitionColumn = new ColumnConfig<Ontology, String>(ontologyProperties.definition(), 100, SafeHtmlUtils.fromTrustedString("<b>Definition</b>"));
		List<ColumnConfig<Ontology, ?>> columns = new ArrayList<ColumnConfig<Ontology, ?>>();
		columns.add(categoryColumn);
		columns.add(definitionColumn);
		ColumnModel<Ontology> columnModel = new ColumnModel<Ontology>(columns);
		Grid<Ontology> grid = new Grid<Ontology>(store, columnModel);
		grid.getView().setAutoExpandColumn(definitionColumn);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.setBorders(false);
		grid.setColumnReordering(false);
		grid.setStateful(true);
		grid.setStateId("ontologiesGrid");
		GridStateHandler<Ontology> state = new GridStateHandler<Ontology>(grid);
		state.loadState();
		this.initWidget(grid);
		
		bindEvents();
	}
	
	public void setOntologies(List<Ontology> ontologies) {
		store.clear();
		store.addAll(ontologies);
	}
	
	private void bindEvents() {
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.TermSelectHandler() {
			@Override
			public void onSelect(Term term) {
				//retrieving the locations for the specific term from somewhere / server
				List<Ontology> ontologies = new LinkedList<Ontology>();
				setOntologies(ontologies);
			}
		});
	}
	
}
