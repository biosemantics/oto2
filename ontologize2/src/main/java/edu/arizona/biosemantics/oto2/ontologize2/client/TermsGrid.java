package edu.arizona.biosemantics.oto2.ontologize2.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class TermsGrid extends SimpleContainer {

	public static class Row {
		
		public int id = 0;
		public List<Term> terms;
		
		public Row(int id, List<Term> terms) {
			this.id = id;
			this.terms = terms;
		}		
	}
	
	public static interface RowProperties extends PropertyAccess<Row> {

		@Path("id")
		ModelKeyProvider<Row> key();

		@Path("id")
		ValueProvider<Row, Integer> id();

		@Path("terms")
		ValueProvider<Row, List<Term>> terms();
	}

	private RowProperties rowProperties = GWT.create(RowProperties.class);
	private ListStore<Row> store;
	private Grid<Row> grid;
	
	public TermsGrid() {
		store = new ListStore<Row>(rowProperties.key());
		refreshOrCreateGrid(new LinkedList<Row>());
		this.add(grid);
	}
	
	private void refreshOrCreateGrid(Collection<Row> rows) {
		store.clear();
		store.addAll(rows);
		
		
		int maxTerms = 1;
		for(Row row : rows) {
			int size = row.terms.size();
			if(size > maxTerms)
				maxTerms = size;
		}
		
		List<ColumnConfig<Row, ?>> columns = new ArrayList<ColumnConfig<Row, ?>>();
		ColumnConfig<Row, String> column1 = new ColumnConfig<Row, String>(new ValueProvider<Row, String>() {
			@Override
			public String getValue(Row object) {
				if(object.terms.size() > 0)
					return object.terms.get(0).getValue();
				return "";
			}
			@Override
			public void setValue(Row object, String value) {
				if(object.terms.size() > 0)
					object.terms.set(0, new Term(value));
			}
			@Override
			public String getPath() {
				return "term-0";
			}
		});
		columns.add(column1);
		for(int i = 1; i < maxTerms; i++) {
			final int j = i;
			ColumnConfig<Row, String> columnI = new ColumnConfig<Row, String>(new ValueProvider<Row, String>() {
				@Override
				public String getValue(Row object) {
					if(object.terms.size() > j)
						return object.terms.get(j).getValue();
					return "";
				}
				@Override
				public void setValue(Row object, String value) {
					if(object.terms.size() > j)
						object.terms.set(j, new Term(value));
				}
				@Override
				public String getPath() {
					return "term-" + j;
				}
			});
			columns.add(columnI);
		}

		ColumnModel<Row> columnModel = new ColumnModel<Row>(columns);
		if(grid == null)
			 grid = new Grid<Row>(store, columnModel);
		else
			grid.reconfigure(store, columnModel);
	}
	
	public void setRows(Collection<Row> rows) {
		this.refreshOrCreateGrid(rows);
	}
	
}
