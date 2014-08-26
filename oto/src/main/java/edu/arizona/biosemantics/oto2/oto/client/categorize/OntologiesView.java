package edu.arizona.biosemantics.oto2.oto.client.categorize;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.state.client.GridStateHandler;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.button.Tools;
import com.sencha.gxt.widget.core.client.button.Tools.ToolStyle;
import com.sencha.gxt.widget.core.client.button.testing.ToolsTestingImpl;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import edu.arizona.biosemantics.oto2.oto.client.categorize.event.OntologiesSelectEvent;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.oto.shared.model.OntologyEntry;
import edu.arizona.biosemantics.oto2.oto.shared.model.OntologyEntryProperties;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.IOntologyService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.IOntologyServiceAsync;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.RPCCallback;

public class OntologiesView extends Composite {

	private static final OntologyEntryProperties ontologyEntryProperties = GWT.create(OntologyEntryProperties.class);
	private ListStore<OntologyEntry> store = new ListStore<OntologyEntry>(ontologyEntryProperties.key());
	private IOntologyServiceAsync ontologyService = GWT.create(IOntologyService.class);
	private List<Ontology> selectedOntologies = null;
	
	private EventBus eventBus;
	
	public OntologiesView(EventBus eventBus) {
		this.eventBus = eventBus;
		store.setAutoCommit(true);
		ToolStyle style = GWT.isClient() ? GWT.<Tools> create(Tools.class).icons() : new ToolsTestingImpl().icons();
		System.out.println("double " + style.doubleRight());
		
		ToolButton linkButton = new ToolButton(ToolButton.MINIMIZE);
		System.out.println(linkButton.getElement());
		
		linkButton = new ToolButton(ToolButton.MINIMIZE);
		System.out.println(linkButton.getElement().getInnerHTML());
		
		
		ColumnConfig<OntologyEntry, String> ontologyColumn = new ColumnConfig<OntologyEntry, String>(ontologyEntryProperties.ontology(), 20, SafeHtmlUtils.fromTrustedString("<b>Ontology</b>"));
		ColumnConfig<OntologyEntry, String> categoryColumn = new ColumnConfig<OntologyEntry, String>(ontologyEntryProperties.label(), 50, SafeHtmlUtils.fromTrustedString("<b>Label</b>"));
		ColumnConfig<OntologyEntry, String> definitionColumn = new ColumnConfig<OntologyEntry, String>(ontologyEntryProperties.definition(), 100, SafeHtmlUtils.fromTrustedString("<b>Definition</b>"));
		ColumnConfig<OntologyEntry, String> urlColumn = new ColumnConfig<OntologyEntry, String>(ontologyEntryProperties.url(), 10, SafeHtmlUtils.fromTrustedString(""));//"<b>Hyperlink</b>"));
		urlColumn.setCell(new AbstractCell<String>() {
			@Override
		    public void render(Context context, String value, SafeHtmlBuilder sb) {
				ToolButton linkButton = new ToolButton(ToolButton.DOUBLERIGHT);
			    SafeHtml safeHtml = SafeHtmlUtils.fromTrustedString("<a target=\"_blank\" href=\"" + value + "\">" + linkButton.getElement().toString() + "</a>");
			    sb.append(safeHtml);
		    }
		});
		List<ColumnConfig<OntologyEntry, ?>> columns = new ArrayList<ColumnConfig<OntologyEntry, ?>>();
		columns.add(ontologyColumn);
		columns.add(categoryColumn);
		columns.add(definitionColumn);
		columns.add(urlColumn);
		ColumnModel<OntologyEntry> columnModel = new ColumnModel<OntologyEntry>(columns);
		Grid<OntologyEntry> grid = new Grid<OntologyEntry>(store, columnModel);
		categoryColumn.setWidth(200);
		grid.getView().setAutoExpandColumn(definitionColumn);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.setBorders(false);
		grid.setColumnReordering(false);
		grid.setStateful(true);
		grid.setStateId("ontologiesGrid");
		GridStateHandler<OntologyEntry> state = new GridStateHandler<OntologyEntry>(grid);
		state.loadState();
		this.initWidget(grid);
		
		bindEvents();
	}
	
	public void setOntologyEntries(List<OntologyEntry> ontologyEntries) {
		store.clear();
		store.addAll(ontologyEntries);
	}
	
	private void bindEvents() {
		eventBus.addHandler(OntologiesSelectEvent.TYPE, new OntologiesSelectEvent.OntologiesSelectHandler() {
			@Override
			public void onSelect(List<Ontology> ontologies) {
				selectedOntologies = ontologies;
			}
		});
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.TermSelectHandler() {
			@Override
			public void onSelect(Term term) {
				if(selectedOntologies == null) {
					ontologyService.getOntologyEntries(term, new RPCCallback<List<OntologyEntry>>() {
						@Override
						public void onSuccess(List<OntologyEntry> ontologyEntries) {
							setOntologyEntries(ontologyEntries);
						}
					});
				} else {
					ontologyService.getOntologyEntries(term, selectedOntologies, new RPCCallback<List<OntologyEntry>>() {
						@Override
						public void onSuccess(List<OntologyEntry> ontologyEntries) {
							setOntologyEntries(ontologyEntries);
						}
					});
				}
			}
		});
	}
	
}
