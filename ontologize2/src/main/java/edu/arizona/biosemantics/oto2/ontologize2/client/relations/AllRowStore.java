package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.RowProperties;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

public class AllRowStore implements DataProxy<PagingLoadConfig, PagingLoadResult<Row>> {

	public static class MyPagingLoadResult extends PagingLoadResultBean<Row> {

		private static final long serialVersionUID = 1L;
		
		protected MyPagingLoadResult() {
		}

		public MyPagingLoadResult(List<Row> list, int totalLength, int offset) {
			super(list, totalLength, offset);
		}
	}
	
	private StoreSortInfo<Row> defaultSortInfo = new StoreSortInfo<Row>(new Comparator<Row>() {
		@Override
		public int compare(Row o1, Row o2) {
			Date d1 = getCreationDate(o1.getLead());
			Date d2 = getCreationDate(o2.getLead());
			return d1.compareTo(d2);
		}
	}, SortDir.DESC);
	private RowProperties rowProperties = GWT.create(RowProperties.class);
	private ListStore<Row> store;
	
	public AllRowStore() {
		store = new ListStore<Row>(rowProperties.key());
		store.setAutoCommit(true);
		setDefaultSortInfo();
	}
	
	private void setDefaultSortInfo() {
		if(store.getSortInfo().size() == 1 && 
				store.getSortInfo().get(0).equals(defaultSortInfo))
			return;
		else {
			store.clearSortInfo();
			store.addSortInfo(defaultSortInfo);
		}
	}

	public void applySort(boolean fireEvent) {
		store.applySort(fireEvent);
	}
	
	protected Date getCreationDate(Vertex v) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		Date result = new Date();
		for(Edge in : g.getInRelations(v)) {
			if(in.getCreation().compareTo(result) < 0)
				result = in.getCreation();
		}
		return result;
	}

	@Override
	public void load(PagingLoadConfig loadConfig, Callback<PagingLoadResult<Row>, Throwable> callback) {
		if(loadConfig instanceof FilterPagingLoadConfig) {
			List<FilterConfig> filters = ((FilterPagingLoadConfig)loadConfig).getFilters();
		}
		int offset = loadConfig.getOffset();
		int limit = loadConfig.getLimit();
		List<? extends SortInfo> sortInfos = loadConfig.getSortInfo();
		if(sortInfos.isEmpty())
			this.setDefaultSortInfo();
		else {
			store.clearSortInfo();
			for(SortInfo sortInfo : sortInfos) {
				switch(sortInfo.getSortField()) {
					case "lead":
						store.clearSortInfo();
						store.addSortInfo(new StoreSortInfo<Row>(new Comparator<Row>() {
							@Override
							public int compare(Row o1, Row o2) {
								return o1.getLead().getValue().compareTo(o2.getLead().getValue());
							}
						}, sortInfo.getSortDir()));
						break;
					default:
						this.setDefaultSortInfo();	
				}
			}
		}
		List<Row> data = new LinkedList<Row>();
		for(int i=offset; i<offset+limit; i++) {
			Row row = store.get(i);
			if(row != null)
				data.add(row);
		}
		MyPagingLoadResult result = new MyPagingLoadResult(data, store.size(), loadConfig.getOffset());
		callback.onSuccess(result);
	}

	public void clear() {
		store.clear();
	}

	public void add(Row row) {
		store.add(row);
	}

	public void remove(Row row) {
		store.remove(row);
	}

	public void removeFilters() {
		store.removeFilters();
	}

	public void setEnableFilters(boolean b) {
		store.setEnableFilters(b);
	}

	public void addFilter(StoreFilter<Row> storeFilter) {
		store.addFilter(storeFilter);
	}

	public List<Row> getAll() {
		return new ArrayList<Row>(store.getAll());
	}

}
