package edu.arizona.biosemantics.oto.oto.client.categorize;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.state.client.GridStateHandler;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermSelectEvent;
import edu.arizona.biosemantics.oto.oto.shared.model.Location;
import edu.arizona.biosemantics.oto.oto.shared.model.LocationProperties;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;
import edu.arizona.biosemantics.oto.oto.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto.oto.shared.rpc.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto.oto.shared.rpc.RPCCallback;

public class LocationsView extends Composite {

	private static final LocationProperties locationProperties = GWT
			.create(LocationProperties.class);
	private ListStore<Location> store = new ListStore<Location>(locationProperties.key());
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private EventBus eventBus;

	public LocationsView(EventBus eventBus) {
		this.eventBus = eventBus;
		store.setAutoCommit(true);
		ColumnConfig<Location, String> instanceColumn = new ColumnConfig<Location, String>(locationProperties.instance(), 50,SafeHtmlUtils.fromTrustedString("<b>Instance</b>"));
		ColumnConfig<Location, String> categorizationColumn = new ColumnConfig<Location, String>(locationProperties.categorization(), 100, SafeHtmlUtils.fromTrustedString("<b>Categorization</b>"));
		List<ColumnConfig<Location, ?>> columns = new ArrayList<ColumnConfig<Location, ?>>();
		columns.add(instanceColumn);
		columns.add(categorizationColumn);
		ColumnModel<Location> columnModel = new ColumnModel<Location>(columns);
		Grid<Location> grid = new Grid<Location>(store, columnModel);
		instanceColumn.setWidth(200);
		grid.getView().setAutoExpandColumn(categorizationColumn);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.setBorders(false);
		grid.setColumnReordering(false);
		grid.setStateful(true);
		grid.setStateId("locationsGrid");
		GridStateHandler<Location> state = new GridStateHandler<Location>(grid);
		state.loadState();
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
	}

}
