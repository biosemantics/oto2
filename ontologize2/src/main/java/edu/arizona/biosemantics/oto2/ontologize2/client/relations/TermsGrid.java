package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
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
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.HasRowId;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell.AttachedTermCell;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell.LeadTermCell;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell.MenuExtendedCell;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TermTreeNode;

public class TermsGrid implements IsWidget {

	public static class Row {
		
		private static int currentId = 0;
		
		private int id = currentId++;
		private Term leadTerm;
		private Map<String, Term> attachedTermDisambiguatedMap = new HashMap<String, Term>();
		//will only contain the term once even though ambiguous over multiple rows!
		private Map<String, Term> attachedTermMap = new HashMap<String, Term>();
		private List<Term> attachedTermsList = new ArrayList<Term>();
		
		public Row(Term leadTerm) {
			this.leadTerm = leadTerm;
		}
		
		public Row(Term leadTerm, List<Term> terms) {
			this(leadTerm);
			this.attachedTermsList.addAll(terms);
			for(Term term : terms) {
				this.attachedTermMap.put(term.getValue(), term);
				this.attachedTermDisambiguatedMap.put(term.getDisambiguatedValue(), term);
			}
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
		
		public void addAttachedTerm(int index, Term term) throws Exception {
			if(attachedTermDisambiguatedMap.containsKey(term.getDisambiguatedValue()))
				throw new Exception("Term already exists.");
			this.attachedTermsList.add(index, term);
			this.attachedTermDisambiguatedMap.put(term.getDisambiguatedValue(), term);
			this.attachedTermMap.put(term.getValue(), term);
		}

		public void addAttachedTerm(Term term) throws Exception {
			int index = attachedTermsList.size();
			this.addAttachedTerm(index, term);
		}

		public void setLeadTerm(Term leadTerm) {
			this.leadTerm = leadTerm;
		}

		public int getAttachedCount() {
			return this.attachedTermDisambiguatedMap.size();
		}

		public boolean hasAttachedTerms() {
			return !this.attachedTermDisambiguatedMap.isEmpty();
		}

		public void addAttachedTerms(Collection<Term> terms) throws Exception {
			for(Term term : terms) 
				this.addAttachedTerm(term);
		}

		public int getTermCount() {
			return this.getAttachedCount() + 1;
		}

		public boolean containsAttachedTerms(Term term, boolean disambiguated) {
			if(disambiguated)
				return this.attachedTermDisambiguatedMap.containsKey(term.getDisambiguatedValue());
			else
				return this.attachedTermMap.containsKey(term.getValue());
		}
		
		public void removeAttachedTerm(int i) {
			Term term = attachedTermsList.remove(i);
			attachedTermDisambiguatedMap.remove(term.getDisambiguatedValue());
			//will only contain the term once even though ambiguous over multiple rows!
			attachedTermMap.remove(term.getValue());
		}
		
		public void removeAttachedTerm(Term term) {
			this.removeAttachedTerm(attachedTermsList.indexOf(term));
		}

		public void replaceAttachedTerm(Term oldTerm, Term newTerm, boolean valueMatchSufficient) throws Exception {
			int index = attachedTermsList.indexOf(oldTerm);
			if(index == -1) {
				if(valueMatchSufficient) {
					for(int i=0; i<attachedTermsList.size(); i++) {
						Term term = attachedTermsList.get(i);
						if(term.getValue().equals(oldTerm.getValue())) {
							oldTerm = term;
							index = i;
						}
					}
				} else {
					return;
				}
			}
			this.removeAttachedTerm(index);
			this.addAttachedTerm(index, newTerm);
		}

		public Term getAttachedTerm(String value, boolean disambiguated) {
			if(disambiguated)
				return attachedTermDisambiguatedMap.get(value);
			else
				return attachedTermMap.get(value);
		}

		public List<Term> getTerms() {
			List<Term> result = new ArrayList<Term>(attachedTermsList.size() + 1);
			result.add(this.leadTerm);
			result.addAll(this.attachedTermsList);
			return result;
		}

		public void removeAttachedTerms(List<Term> terms) {
			for(Term term : terms)
				this.removeAttachedTerm(term);
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

	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	protected EventBus eventBus;
	private RowProperties rowProperties = GWT.create(RowProperties.class);
	protected ListStore<Row> store;
	protected Grid<Row> grid;
	protected SimpleContainer createRowContainer;//jin change to protected
	private final int colWidth = 100;
	private String firstColName;
	private String nColName;
	protected ValueProvider<Term, String> valueProvider;
	private edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection collection;
	
	public TermsGrid(EventBus eventBus, String firstColName, String nColName, ValueProvider<Term, String> valueProvider) {
		this.eventBus = eventBus;
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
					addRow(new Row(term));
				} else {
					//display an error
				}
			}
		});
		dropTargetNewRow.setOperation(Operation.COPY);
		
		bindEvents();
	}
	
	protected void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				collection = event.getCollection();
			}
		});
		eventBus.addHandler(RemoveTermEvent.TYPE, new RemoveTermEvent.Handler() {
			@Override
			public void onCreate(RemoveTermEvent event) {
				removeTerms(event.getTerms());
			}
		});
	}
	
	public void addRow(Row row) {
		/*if(row.getTermCount() > grid.getColumnModel().getColumnCount()) {
			List<ColumnConfig<Row, ?>> columns = new ArrayList<ColumnConfig<Row, ?>>(grid.getColumnModel().getColumns());
			for(int i = columns.size(); i <= row.getAttachedCount(); i++) {
				columns.add(createColumnI(i));
			}
			grid.reconfigure(store, new ColumnModel<Row>(columns));
		} */
		store.add(row);
		//jin add to support auto scroll
		//this.createRowContainer.getElement().scrollIntoView(this.asWidget().getElement(), true);
		createRowContainer.getElement().scrollIntoView();
	}
	
	public void removeRows(Collection<Row> rows) {
		for(Row row : rows) {
			store.remove(row);
		}
	}
	
	public void setRows(List<Row> rows) {
		store.clear();
		grid.reconfigure(store, createColumnModel(rows));
		store.addAll(rows);
	}
	
	public void removeRows(Row row) {
		List<Row> rows = new LinkedList<Row>();
		rows.add(row);
		this.removeRows(rows);
	}
	
	public void addAttachedTermsToRow(Row row, List<Term> add) throws Exception {		
		if(!add.isEmpty()) {
			row.addAttachedTerms(add);
			updateRow(row);
		}
	}
	
	public void removeAttachedTermsFromRow(Row row, List<Term> terms) {
		doRemoveAttachedTermsFromRow(row, terms);
	}
	
	protected void doRemoveAttachedTermsFromRow(Row row, List<Term> terms) {
		row.removeAttachedTerms(terms);
		updateRow(row);
	}
	
	public void removeAttachedTermsFromRows(List<Row> rows, List<Term> terms) {
		for(Row row : rows)
			doRemoveAttachedTermsFromRow(row, terms);
	}

	public void removeTerms(Term[] terms) {
		for(Term term : terms) {
			List<Row> leadTermRows = this.getLeadTermsRows(term, true);
			for(Row row : leadTermRows)
				this.removeRows(row);
			List<Row> attachedTermRows = this.getAttachedTermsRows(term, true);
			this.removeAttachedTermsFromRows(attachedTermRows, Arrays.asList(terms));
		}
	}

	public void consolidate() throws Exception {
		List<Row> targetRows = new LinkedList<Row>();
		Map<String, List<Row>> entries = new HashMap<String, List<Row>>();
		for(Row row : this.store.getAll()) {
			Term firstTerm = row.getLeadTerm();
			if(!entries.containsKey(firstTerm.getDisambiguatedValue())) 
				entries.put(firstTerm.getDisambiguatedValue(), new LinkedList<Row>());
			entries.get(firstTerm.getDisambiguatedValue()).add(row);
		}
		
		for(String term : entries.keySet()) {
			if(entries.get(term).size() > 1) {
				List<Row> rows = entries.get(term);
				Row targetRow = rows.get(0);
				Set<String> terms = new HashSet<String>();
				for(Term attachedTerm : targetRow.getAttachedTerms())
					terms.add(attachedTerm.getDisambiguatedValue());
				List<Term> add = new LinkedList<Term>();
				for(int i=1; i<rows.size(); i++) {
					Row row = rows.get(i);
					for(Term attachedTerm : row.getAttachedTerms()) {
						if(!terms.contains(attachedTerm)) {
							add.add(attachedTerm);
							terms.add(attachedTerm.getDisambiguatedValue());
						}
					}
					store.remove(row);
				}
				targetRow.addAttachedTerms(add);
				targetRows.add(targetRow);
			} else if(entries.get(term).size() == 1) {
				targetRows.add(entries.get(term).get(0));
			}
		}
		if(!targetRows.isEmpty())
			this.reconfigureForAttachedTerms(getMaxAttachedTermsCount(targetRows));
	}
	
	public void updateRow(Row row) {
		if(row.getTermCount() <= grid.getColumnModel().getColumnCount()) 
			store.update(row);
		else {
			this.reconfigureForAttachedTerms(row.getAttachedCount());
			store.update(row);
		}
	}
	
	public List<Row> getAttachedTermsRows(Term attachedTerm, boolean disambiguated) {
		List<Row> result = new LinkedList<Row>();
		for(Row row : this.store.getAll()) {
			if(row.containsAttachedTerms(attachedTerm, disambiguated))
				result.add(row);
		}
		return result;
	}
	
	public List<Row> getLeadTermsRows(Term leadTerm, boolean disambiguated) {
		List<Row> result = new LinkedList<Row>();
		for(Row row : this.store.getAll()) {
			if(disambiguated) {
				if(row.getLeadTerm().getDisambiguatedValue().equals(leadTerm.getDisambiguatedValue()))
					result.add(row);
			} else {
				if(row.getLeadTerm().getValue().equals(leadTerm.getValue()))
					result.add(row);
			}
		}
		return result;
	}
	
	public Row getRowWithId(List<Row> rows, int rowId) {
		for(Row row : rows)
			if(row.getId() == rowId)
				return row;
		return null;
	}
	
	public Row getRow(int index) {
		return store.get(index);
	}
	
	public List<Row> getSelection() {
		return new ArrayList<Row>(grid.getSelectionModel().getSelectedItems());
	}

	public List<Row> getAll() {
		return new ArrayList<Row>(grid.getStore().getAll());
	}

	private void reconfigureForAttachedTerms(int attachedTermsCount) {
		grid.reconfigure(store, createColumnModel(attachedTermsCount));
	}
	
	private ColumnModel<Row> createColumnModel(int attachedTermsCount) {
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
		LeadTermCell cell = new LeadTermCell(this);
		column1.setCell(cell);
		columns.add(column1);
		for(int i = 1; i <= attachedTermsCount; i++)
			columns.add(createColumnI(i));
		return new ColumnModel<Row>(columns);
	}
	
	private ColumnModel<Row> createColumnModel(Collection<Row> rows) {	
		return createColumnModel(getMaxAttachedTermsCount(rows));
	}
	
	/**
	 * create the i-th column, its path is term-i
	 * @param i
	 * @return
	 */
	private ColumnConfig<Row, ?> createColumnI(final int i) {
		ColumnConfig<Row, String> config = new ColumnConfig<Row, String>(new ValueProvider<Row, String>() {
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
		AttachedTermCell cell = new AttachedTermCell(this);
		config.setCell(cell);
		
		return config;
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

	private void createCreateRowContainer() {
		createRowContainer = new SimpleContainer();
		createRowContainer.setTitle("Drop here to create new row");
		com.google.gwt.user.client.ui.Label dropLabel = new com.google.gwt.user.client.ui.Label("Drop here to create new row");
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

	@Override
	public Widget asWidget() {
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add(grid);
		vlc.add(createRowContainer);
		//jin add to support auto scroll
		vlc.getScrollSupport().setScrollMode(ScrollMode.AUTO);
		//jin add to support auto scroll end
		
		SimpleContainer simpleContainer = new SimpleContainer();
		simpleContainer.add(vlc);
		return simpleContainer;
	}	
	
	protected void fireEvents(List<GwtEvent<?>> result, Row row) {	
		for(GwtEvent<?> event : result) {
				if(event instanceof HasRowId && row != null) 
					((HasRowId)event).setRowId(row.getId());
			eventBus.fireEvent(event);
		}
	}
	
	protected String collapseTermsAsString(List<Term> terms) {
		String result = "";
		int i = 0;
		for(Term term : terms) {
			result += term.getDisambiguatedValue() + ", ";
			if(i > 5)
				break;
		}
		return result.substring(0, result.length() - 2);
	}
}
