package edu.arizona.biosemantics.oto2.oto.client.categorize;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.Style.HideMode;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.state.client.GridStateHandler;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.RowNumberer;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

import edu.arizona.biosemantics.oto2.oto.client.categorize.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Location;
import edu.arizona.biosemantics.oto2.oto.shared.model.LocationProperties;
import edu.arizona.biosemantics.oto2.oto.shared.model.OntologyEntry;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TypedContext;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.RPCCallback;

public class LocationsView extends Composite {

	private static final LocationProperties locationProperties = GWT
			.create(LocationProperties.class);
	private ListStore<Location> store = new ListStore<Location>(locationProperties.key());
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private EventBus eventBus;
	private Grid<Location> grid;

	public LocationsView(EventBus eventBus) {
		this.eventBus = eventBus;
		store.setAutoCommit(true);
		RowNumberer<Location> numberer = new RowNumberer<Location>();
		ColumnConfig<Location, String> instanceColumn = new ColumnConfig<Location, String>(locationProperties.instance(), 50,SafeHtmlUtils.fromTrustedString("<b>Instance</b>"));
		ColumnConfig<Location, Label> categorizationColumn = new ColumnConfig<Location, Label>(locationProperties.categorization(), 100, SafeHtmlUtils.fromTrustedString("<b>Categorization</b>"));
		categorizationColumn.setCell(new AbstractCell<Label>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	Label value, SafeHtmlBuilder sb) {
				if(value != null)
					sb.appendHtmlConstant("<span qtitle='Description of " + value.getName() + "' " +
							"qtip='" + value.getDescription() + "'>" + value.getName() + "</span>");
				else
					sb.appendHtmlConstant("<span qtitle='Uncategorized' " +
							"qtip='No category assigned to this term.'>uncategorized</span>");
			}
		});
		instanceColumn.setToolTip(SafeHtmlUtils.fromTrustedString("The selected terml"));
		categorizationColumn.setToolTip(SafeHtmlUtils.fromTrustedString("The current categorization of the term"));
		categorizationColumn.setMenuDisabled(false);
		instanceColumn.setMenuDisabled(false);
		List<ColumnConfig<Location, ?>> columns = new ArrayList<ColumnConfig<Location, ?>>();
		columns.add(numberer);
		columns.add(instanceColumn);
		columns.add(categorizationColumn);
		ColumnModel<Location> columnModel = new ColumnModel<Location>(columns);
		grid = new Grid<Location>(store, columnModel);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		QuickTip quickTip = new QuickTip(grid);
		//instanceColumn.setWidth(200);
		grid.getView().setAutoExpandColumn(categorizationColumn);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setForceFit(true);
		grid.setBorders(false);
		grid.setAllowTextSelection(true);
		grid.setColumnReordering(true);
		/*grid.setStateful(true);
		grid.setStateId("locationsGrid");
		GridStateHandler<Location> state = new GridStateHandler<Location>(grid);
		state.loadState();*/
		
		StringFilter<Location> instanceFilter = new StringFilter<Location>(locationProperties.instance());
		StringFilter<Location> categorizationFilter = new StringFilter<Location>(new ValueProvider<Location, String>() {
			@Override
			public String getValue(Location object) {
				return object.getCategorization().getName();
			}
			@Override
			public void setValue(Location object, String value) {
				object.getCategorization().setName(value);
			}
			@Override
			public String getPath() {
				// has to return same path as valueprovider used in columnmodel for filter to work; ideally use same valueprovider 
				// but not possible here
				return "categorization";
			}
		});
		GridFilters<Location> filters = new GridFilters<Location>();
	    filters.setLocal(true);
	    filters.addFilter(instanceFilter);
	    filters.addFilter(categorizationFilter);
	    filters.initPlugin(grid);
	    numberer.initPlugin(grid);
		
		this.initWidget(grid);
		
		bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.TermSelectHandler() {
			@Override
			public void onSelect(Term term) {
				collectionService.getLocations(term, new RPCCallback<List<Location>>() {
					@Override
					public void onSuccess(List<Location> locations) {
						setLocations(locations);
					}
				});
			}
		});
	}

	public void setLocations(List<Location> locations) {
		store.clear();
		store.addAll(locations);
		
		//bug: http://www.sencha.com/forum/showthread.php?285982-Grid-ColumnHeader-Menu-missing
		grid.getView().refresh(true);
	}

}
