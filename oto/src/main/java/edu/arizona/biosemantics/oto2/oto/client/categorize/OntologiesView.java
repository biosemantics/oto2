package edu.arizona.biosemantics.oto2.oto.client.categorize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.state.client.GridStateHandler;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.button.Tools;
import com.sencha.gxt.widget.core.client.button.Tools.ToolStyle;
import com.sencha.gxt.widget.core.client.button.testing.ToolsTestingImpl;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

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
	private Set<Ontology> selectedOntologies = null; //will search all if nothing is selected initially
	
	private EventBus eventBus;
	
	public OntologiesView(EventBus eventBus) {
		this.eventBus = eventBus;
		store.setAutoCommit(true);	
		ColumnConfig<OntologyEntry, String> ontologyColumn = new ColumnConfig<OntologyEntry, String>(ontologyEntryProperties.ontologyAcronym(), 20, SafeHtmlUtils.fromTrustedString("<b>Ontology</b>"));
		ColumnConfig<OntologyEntry, String> categoryColumn = new ColumnConfig<OntologyEntry, String>(ontologyEntryProperties.label(), 50, SafeHtmlUtils.fromTrustedString("<b>Label</b>"));
		ColumnConfig<OntologyEntry, String> definitionColumn = new ColumnConfig<OntologyEntry, String>(ontologyEntryProperties.definition(), 100, SafeHtmlUtils.fromTrustedString("<b>Definition</b>"));
		ontologyColumn.setCell(new AbstractCell<String>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
				int row = context.getIndex();
				OntologyEntry ontologyEntry = store.get(row);
				sb.appendHtmlConstant("<span qtitle='Full Ontology Name' qtip='" + ontologyEntry.getOntologyName() + "'>" + value + "</span>");
			}
		});
		definitionColumn.setCell(new AbstractCell<String>() {
			@Override
		    public void render(Context context, String value, SafeHtmlBuilder sb) {
		      SafeHtml safeHtml = SafeHtmlUtils.fromTrustedString(value);
		      sb.append(safeHtml);
		    }
		});
		ColumnConfig<OntologyEntry, String> urlColumn = new ColumnConfig<OntologyEntry, String>(ontologyEntryProperties.url(), 10, SafeHtmlUtils.fromTrustedString(""));//"<b>Hyperlink</b>"));
		urlColumn.setCell(new AbstractCell<String>() {
			@Override
		    public void render(Context context, String value, SafeHtmlBuilder sb) {
				ToolButton linkButton = new ToolButton(ToolButton.DOUBLERIGHT);
			    SafeHtml safeHtml = SafeHtmlUtils.fromTrustedString("" +
			    		"<span qtitle='Click to visit external ontology entry' qtip='" + value + "'>" + 
			    		"<a target=\"_blank\" href=\"" + value + "\">" + linkButton.getElement().toString() + "</a></span>");
			    sb.append(safeHtml);
		    }
		});
		/*TextButtonCell buttonCell = new TextButtonCell();
		buttonCell.addSelectHandler(new SelectHandler() {
	        @Override
	        public void onSelect(SelectEvent event) {
	          Context c = event.getContext();
	          int row = c.getIndex();
	          OntologyEntry ontologyEntry = store.get(row);
	          Window.open(ontologyEntry.getUrl(),"_blank","enabled");
	        }
	      });
		urlColumn.setCell(buttonCell);*/
		List<ColumnConfig<OntologyEntry, ?>> columns = new ArrayList<ColumnConfig<OntologyEntry, ?>>();
		columns.add(ontologyColumn);
		columns.add(categoryColumn);
		columns.add(definitionColumn);
		columns.add(urlColumn);
		ColumnModel<OntologyEntry> columnModel = new ColumnModel<OntologyEntry>(columns);
		Grid<OntologyEntry> grid = new Grid<OntologyEntry>(store, columnModel);
		QuickTip quickTip = new QuickTip(grid);
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
	
	public void setOntologyEntries(Collection<OntologyEntry> ontologyEntries) {
		store.clear();
		store.addAll(ontologyEntries);
	}
	
	private void bindEvents() {
		eventBus.addHandler(OntologiesSelectEvent.TYPE, new OntologiesSelectEvent.OntologiesSelectHandler() {
			@Override
			public void onSelect(Set<Ontology> ontologies) {
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
					ontologyService.getOntologyEntries(term, new LinkedList<Ontology>(selectedOntologies), 
							new RPCCallback<List<OntologyEntry>>() {
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
