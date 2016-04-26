package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TermTreeNode;

public class TermsGrid implements IsWidget {

	public static class Row {
		
		private static int currentId = 0;
		
		private int id = currentId++;
		private Term leadTerm;
		private Set<Term> attachedTermSet = new HashSet<Term>();
		private List<Term> attachedTermsList = new ArrayList<Term>();
		
		public Row(Term leadTerm) {
			this.leadTerm = leadTerm;
		}
		
		public Row(Term leadTerm, List<Term> terms) {
			this.leadTerm = leadTerm;
			this.attachedTermsList.addAll(terms);
			this.attachedTermSet.addAll(terms);
		}
		
		public Term getLeadTerm() {
			return leadTerm;
		}
		
		public List<Term> getAttachedTerms() {
			return new ArrayList<Term>(this.attachedTermsList);
		}

		public int getId() {
			return id;
		}

		public void addAttachedTerm(Term term) throws Exception {
			if(attachedTermSet.contains(term))
				throw new Exception("Term already exists.");
			attachedTermsList.add(term);
			attachedTermSet.add(term);
		}

		public void setLeadTerm(Term leadTerm) {
			this.leadTerm = leadTerm;
		}

		public int getAttachedCount() {
			return this.attachedTermSet.size();
		}

		public boolean hasAttachedTerms() {
			return !this.attachedTermSet.isEmpty();
		}

		public void adAttachedTerms(Collection<Term> terms) throws Exception {
			for(Term term : terms) 
				this.addAttachedTerm(term);
		}

		public int getTermCount() {
			return this.getAttachedCount() + 1;
		}

		public boolean containsAttachedTerms(Term term, boolean disambiguated) {
			if(disambiguated)
				return this.attachedTermSetDisambiguated.contains(term);
			else
				return this.attachedTermSet.contains(term);
		}

		public void replaceAttachedTerm(Term oldTerm, Term newTerm) {
			attachedTermSet.remove(oldTerm);
			attachedTermSet.add(newTerm);
			int index = attachedTermsList.indexOf(oldTerm);
			attachedTermsList.remove(index);
			attachedTermsList.add(index, newTerm);
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
	protected ListStore<Row> store;
	protected Grid<Row> grid;
	private SimpleContainer createRowContainer;
	private final int colWidth = 100;
	private String firstColName;
	private String nColName;
	protected ValueProvider<Term, String> valueProvider;
	private Map<Term, Row> leadTermMap = new HashMap<Term, Row>();
	
	public TermsGrid(String firstColName, String nColName, ValueProvider<Term, String> valueProvider) {
		this.firstColName = firstColName;
		this.nColName = nColName;
		this.valueProvider = valueProvider;
		store = new ListStore<Row>(rowProperties.key());
		store.setAutoCommit(true);
		this.grid = new Grid<Row>(store, createColumnModel(new LinkedList<Row>()));
		createCreateRowContainer();

		DropTarget dropTargetGrid = new DropTarget(grid);
		dropTargetGrid.addDropHandler(new DndDropHandler() {
			@Override
			public void onDrop(DndDropEvent event) {
				Element element = event.getDragEndEvent().getNativeEvent().getEventTarget().<Element> cast();
				int targetRowIndex = grid.getView().findRowIndex(element);
				Row row = store.get(targetRowIndex);
				
				if(event.getData() instanceof List<?>) {
					List<Term> add = new LinkedList<Term>();
					List<?> list = (List<?>)event.getData();
					for(Object item : list) {
						if(item instanceof Term) {
							add.add((Term)item);
						}
					}
					try {
						addAttachedTermsToRow(row, add);
					} catch(Exception e) {
						Alerter.showAlert("Term add failed", e.getMessage(), e);
					}
				}
			}
		});
		dropTargetGrid.setOperation(Operation.COPY);
		DropTarget dropTargetNewRow = new DropTarget(createRowContainer);
		dropTargetNewRow.addDropHandler(new DndDropHandler() {
			@Override
			public void onDrop(DndDropEvent event) {
				Term term = null;
				if(event.getData() instanceof List<?>) {
					List<?> list = (List<?>)event.getData();
					if(list.size() == 1) {
						Object item = list.get(0);
						if(item instanceof Term)
							term = (Term)item;
					}
				}
				
				if(term != null) {
					Row row = new Row(term);
					addRow(row);
				} else {
					//display an error
				}
			}
		});
		dropTargetNewRow.setOperation(Operation.COPY);
	}
	
	protected void addAttachedTermsToRow(Row row, List<Term> add) throws Exception {
		row.adAttachedTerms(add);
		updateRow(row);
	}
	
	protected void addRow(Row row) {
		store.add(row);
		leadTermMap.put(row.getLeadTerm(), row);
		if(row.getTermCount() > grid.getColumnModel().getColumnCount()) {
			List<ColumnConfig<Row, ?>> columns = new ArrayList<ColumnConfig<Row, ?>>(grid.getColumnModel().getColumns());
			for(int i = columns.size(); i <= row.getAttachedCount(); i++) {
				columns.add(createColumnI(i));
			}
			grid.reconfigure(store, new ColumnModel<Row>(columns));
		} 
	}

	private void createCreateRowContainer() {
		createRowContainer = new SimpleContainer();
		createRowContainer.setTitle("Drop here to create new entry");
		com.google.gwt.user.client.ui.Label dropLabel = new com.google.gwt.user.client.ui.Label("Drop here to create new entry");
		dropLabel.getElement().getStyle().setLineHeight(30, Unit.PX);
		createRowContainer.setWidget(dropLabel);
		createRowContainer.setHeight(30);
		createRowContainer.getElement().getStyle().setBorderWidth(1, Unit.PX);
		createRowContainer.getElement().getStyle().setBorderStyle(BorderStyle.DASHED);
		createRowContainer.getElement().getStyle().setBorderColor("gray");
		createRowContainer.getElement().getStyle().setProperty("mozMorderMadius", "7px");
		createRowContainer.getElement().getStyle().setProperty("webkitBorderRadius", "7px");
		createRowContainer.getElement().getStyle().setProperty("borderRadius", "7px");
		createRowContainer.getElement().getStyle().setBackgroundColor("#ffffcc");
	}

	protected void updateRow(Row row) {
		if(row.getTermCount() <= grid.getColumnModel().getColumnCount()) 
			store.update(row);
		else {
			this.reconfigureForAttachedTerms(row.getAttachedCount());
			store.update(row);
		}
	}

	private void reconfigureForAttachedTerms(int attachedTermsCount) {
		grid.reconfigure(store, createColumnModel(attachedTermsCount));
	}

	protected void setRows(List<Row> rows) {
		store.clear();
		store.addAll(rows);
		grid.reconfigure(store, createColumnModel(rows));
	}
	
	protected ColumnModel<Row> createColumnModel(int attachedTermsCount) {
		List<ColumnConfig<Row, ?>> columns = new ArrayList<ColumnConfig<Row, ?>>();
		ColumnConfig<Row, String> column1 = new ColumnConfig<Row, String>(new ValueProvider<Row, String>() {
			@Override
			public String getValue(Row object) {
				return valueProvider.getValue(object.getLeadTerm());
			}
			@Override
			public void setValue(Row object, String value) { }
			@Override
			public String getPath() {
				return "term-0";
			}
		}, colWidth, firstColName);
		columns.add(column1);
		for(int i = 1; i <= attachedTermsCount; i++)
			columns.add(createColumnI(i));
		return new ColumnModel<Row>(columns);
	}
	
	protected ColumnModel<Row> createColumnModel(Collection<Row> rows) {	
		return createColumnModel(getMaxAttachedTermsCount(rows));
	}
	
	private ColumnConfig<Row, ?> createColumnI(final int i) {
		return new ColumnConfig<Row, String>(new ValueProvider<Row, String>() {
			@Override
			public String getValue(Row object) {
				if(object.getAttachedCount() >= i)
					return valueProvider.getValue(object.getAttachedTerms().get(i - 1));
				return "";
			}
			@Override
			public void setValue(Row object, String value) { }
			@Override
			public String getPath() {
				return "term-" + i;
			}
		}, colWidth, nColName + "-" + i);
	}

	public void consolidate() {
		List<Row> targetRows = new LinkedList<Row>();
		Map<Term, List<Row>> entries = new HashMap<Term, List<Row>>();
		for(Row row : this.store.getAll()) {
			Term firstTerm = row.getLeadTerm();
			if(!entries.containsKey(firstTerm)) 
				entries.put(firstTerm, new LinkedList<Row>());
			entries.get(firstTerm).add(row);
		}
		
		for(Term term : entries.keySet()) {
			if(entries.get(term).size() > 1) {
				List<Row> rows = entries.get(term);
				Row targetRow = rows.get(0);
				Set<Term> terms = new HashSet<Term>();
				terms.addAll(targetRow.getAttachedTerms());
				List<Term> add = new LinkedList<Term>();
				for(int i=1; i<rows.size(); i++) {
					Row row = rows.get(i);
					for(Term attachedTerm : row.getAttachedTerms()) {
						if(!terms.contains(attachedTerm)) {
							add.add(attachedTerm);
							terms.add(attachedTerm);
						}
					}
					store.remove(row);
				}
				targetRow.getAttachedTerms().addAll(add);
				targetRows.add(targetRow);
			} else if(entries.get(term).size() == 1) {
				targetRows.add(entries.get(term).get(0));
			}
		}
		if(!targetRows.isEmpty())
			this.reconfigureForAttachedTerms(getMaxAttachedTermsCount(targetRows));
	}
		
	private int getMaxAttachedTermsCount(Collection<Row> rows) {
		if(rows.isEmpty())
			return 0;
		Row maxRow = rows.iterator().next();
		for(Row row : rows) {
			int size = row.getTermCount();
			if(size > maxRow.getTermCount())
				maxRow = row;
		}
		return maxRow.getAttachedCount();
	}

	public void remove(Collection<Row> rows) {
		for(Row row : rows) {
			store.remove(row);
		}
	}

	public List<Row> getSelection() {
		return new ArrayList<Row>(grid.getSelectionModel().getSelectedItems());
	}

	public List<Row> getAll() {
		return new ArrayList<Row>(grid.getStore().getAll());
	}

	@Override
	public Widget asWidget() {
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add(grid);
		vlc.add(createRowContainer);
		SimpleContainer simpleContainer = new SimpleContainer();
		simpleContainer.add(vlc);
		return simpleContainer;
	}
	
	protected List<Row> getAttachedTermsRows(Term attachedTerm, boolean disambiguated) {
		List<Row> result = new LinkedList<Row>();
		for(Row row : this.store.getAll()) {
			if(row.containsAttachedTerms(attachedTerm, disambiguated))
				result.add(row);
		}
		return result;
	}
	
	protected List<Row> getLeadTermsRows(Term leadTerm, boolean disambiguated) {
		List<Row> result = new LinkedList<Row>();
		for(Row row : this.store.getAll()) {
			if(disambiguated) {
				if(row.getLeadTerm().getValue().equals(leadTerm.getValue()))
					result.add(row);
			} else {
				if(row.getLeadTerm().getDisambiguatedValue().equals(leadTerm.getDisambiguatedValue()))
					result.add(row);
			}
		}
		return result;
	}
	
}
