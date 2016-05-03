package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.ValueProvider;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreatePartEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateSubclassEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.HasRowId;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemovePartEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveSubclassEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class SubclassesGrid extends MenuTermsGrid {
	
	//allowed: subclass of multiple superclasses
	//allowed: duplicates of subclasses: can be consolidated
	//not allowed: circular relationships
	//not allowed: duplicate superclasses in the same subclass
	private edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection collection;
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);

	public SubclassesGrid(EventBus eventBus) {
		super(eventBus, "is-a", "superclass", "subclass", new ValueProvider<Term, String>() {
			@Override 
			public String getValue(Term object) {
				return object.getDisambiguatedValue();
			}
			@Override
			public void setValue(Term object, String value) { }
			@Override
			public String getPath() {
				return "class-term";
			}
		});
	}
	
	@Override
	public void bindEvents() {
		super.bindEvents();
		
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				collection = event.getCollection();
				store.clear();
				for(String superclass : collection.getSubclasses().keySet()) {
					Term superclassTerm = collection.getTerm(superclass);
					eventBus.fireEvent(new CreateSubclassEvent(superclassTerm, collection.getSubclasses(superclassTerm)));
				}
			}
		}); 
		eventBus.addHandler(CreateSubclassEvent.TYPE, new CreateSubclassEvent.Handler() {
			@Override
			public void onCreate(CreateSubclassEvent event) {
				List<Row> rows = SubclassesGrid.this.getLeadTermsRows(event.getSuperclass(), true);
				Row row = null;
				if(event.hasRowId()) 
					row = getRowWithId(rows, event.getRowId()); 
				else {
					row = new Row(event.getSuperclass());
					SubclassesGrid.super.addRow(row);
				}
				if(row != null)
					try {
						SubclassesGrid.super.addAttachedTermsToRow(row, Arrays.asList(event.getSubclasses()));
					} catch (Exception e) {
						Alerter.showAlert("Create Part", "Create subclass failed");
					}
			}
		});
		eventBus.addHandler(RemoveSubclassEvent.TYPE, new RemoveSubclassEvent.Handler() {
			@Override
			public void onRemove(RemoveSubclassEvent event) {
				List<Row> rows = SubclassesGrid.this.getLeadTermsRows(event.getSuperclass(), true);
				if(event.hasRowId()) {
					rows = new LinkedList<Row>();
					Row idRow = getRowWithId(rows, event.getRowId());
					if(idRow != null) {
						rows.add(idRow);
					}
				}
				if(!rows.isEmpty()) {
					if(!event.hasSubclasses()) {
						SubclassesGrid.super.removeRows(rows);
					} else {
						try {
							SubclassesGrid.super.removeAttachedTermsFromRows(rows, Arrays.asList(event.getSubclasses()));
						} catch (Exception e) {
							Alerter.showAlert("Remove Subclass", "Remove subclass failed");
						}
					}
				}
					
			}
		});
	}

	@Override
	protected void addRow(Row row) {
		boolean valid = true;
		try { 
			//validAddRow(row, this.getAll());
		} catch(Exception e) {
			valid = false;
			Alerter.showAlert("Add failed.", e.getMessage());
		}
		if(valid) {
			collectionService.createSubclass(collection.getId(), collection.getSecret(), 
					row.getLeadTerm(), new LinkedList<Term>(), new AsyncCallback<List<GwtEvent<?>>>() {
				@Override
				public void onFailure(Throwable caught) {
					
				}
				@Override
				public void onSuccess(List<GwtEvent<?>> result) {
					fireEvents(result, null);
				}
			});
		}
	}
	
	@Override
	protected void setRows(List<Row> rows) {
		boolean valid = true;
		try { 
			validSetRows(rows);
		} catch(Exception e) {
			valid = false;
			Alerter.showAlert("Add failed.", e.getMessage());
		}
		if(valid) {
			for(final Row row : rows)
				collectionService.createSubclass(collection.getId(), collection.getSecret(), 
						row.getLeadTerm(), row.getAttachedTerms(), new AsyncCallback<List<GwtEvent<?>>>() {
					@Override
					public void onFailure(Throwable caught) {
						
					}
					@Override
					public void onSuccess(List<GwtEvent<?>> result) {
						fireEvents(result, row);
					}
				});
		}
	}
	
	@Override
	protected void removeRows(Collection<Row> rows) {
		boolean valid = true;
		try { 
			validateRemove(rows);
		} catch(Exception e) {
			valid = false;
			Alerter.showAlert("Add failed.", e.getMessage());
		}
		if(valid) {
			for(final Row row : rows)
				collectionService.removeSubclass(collection.getId(), collection.getSecret(), 
						row.getLeadTerm(), row.getAttachedTerms(), new AsyncCallback<List<GwtEvent<?>>>() {
					@Override
					public void onFailure(Throwable caught) {
						
					}
					@Override
					public void onSuccess(List<GwtEvent<?>> result) {
						fireEvents(result, row);
					}
				});
		}
	}
	
	@Override
	protected void addAttachedTermsToRow(final Row row, List<Term> add) throws Exception {
		boolean valid = true;
		try { 
			validAddTermsToRow(row, add);
		} catch(Exception e) {
			valid = false;
			Alerter.showAlert("Add failed.", e.getMessage());
		}
		if(valid) {
			collectionService.createSubclass(collection.getId(), collection.getSecret(), 
					row.getLeadTerm(), add, new AsyncCallback<List<GwtEvent<?>>>() {
				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
				}
				@Override
				public void onSuccess(List<GwtEvent<?>> result) {
					fireEvents(result, row);
				}
			});
		}
	}
	
	@Override
	protected void removeAttachedTermsFromRow(final Row row, List<Term> terms) {
		collectionService.removeSubclass(collection.getId(), collection.getSecret(), 
				row.getLeadTerm(), terms, new AsyncCallback<List<GwtEvent<?>>>() {
			@Override
			public void onFailure(Throwable caught) {
				
			}
			@Override
			public void onSuccess(List<GwtEvent<?>> result) {
				fireEvents(result, row);
			}
		});
	}
	
	private void validAddRow(Row row, List<Row> existingRows) throws Exception {
		//circular relationship
		/*if(row.hasAttachedTerms()) {
			Map<String, Set<Row>> leadTermRowMap = this.createLeadTermRowMap();
			for(Term addTerm : row.getAttachedTerms()) 
				if(createsCircularRelationship(row.getLeadTerm(), addTerm, leadTermRowMap))
					throw new Exception("Adding superclass to subclass creates a circular relationship. Not allowed.");
		}*/
	}

	private void validateRemove(Collection<Row> rows) throws Exception {
		for(Row row : rows) {
			if(store.findModel(row) == null) {
				throw new Exception("Row does not exist");
			}
		}
	}

	private void validSetRows(List<Row> rows) throws Exception {
		List<Row> hypotheticRows = new LinkedList<Row>();
		for(Row row : rows) {
			validAddRow(row, hypotheticRows);
			hypotheticRows.add(row);
		}
	}
		
	private void validAddTermsToRow(Row row, List<Term> add) throws Exception { 
		//duplicate superclasses in same subclass
		Term parentTerm = row.getLeadTerm();
		Set<String> existingSuperclasses = new HashSet<String>();
		for(Row storeRow : store.getAll()) {
			if(storeRow.getLeadTerm().equals(parentTerm)) {
				for(Term term : storeRow.getAttachedTerms())
					existingSuperclasses.add(term.getDisambiguatedValue());
			}
		}
		for(Term addTerm : add) {
			if(existingSuperclasses.contains(addTerm)) {
				throw new Exception("Superclass is already defined as superclass of this subclass");
			}
			existingSuperclasses.add(addTerm.getDisambiguatedValue());
		}
		
		//circular relationship
		Map<String, Set<Row>> leadTermRowMap = createLeadTermRowMap();
		
		for(Term addTerm : add) 
			if(createsCircularRelationship(row.getLeadTerm(), addTerm, leadTermRowMap))
				throw new Exception("Adding superclass to subclass creates a circular relationship. Not allowed.");
	}

	private Map<String, Set<Row>> createLeadTermRowMap() {
		Map<String, Set<Row>> leadTermRowMap = new HashMap<String, Set<Row>>();
		Map<String, Set<String>> superclassToSubclassMap = new HashMap<String, Set<String>>();
		for(Row storeRow : this.getAll()) {
			if(!leadTermRowMap.containsKey(storeRow.getLeadTerm().getDisambiguatedValue()))
				leadTermRowMap.put(storeRow.getLeadTerm().getDisambiguatedValue(), new HashSet<Row>());
			leadTermRowMap.get(storeRow.getLeadTerm().getDisambiguatedValue()).add(storeRow);
			
			for(Term superclass : storeRow.getAttachedTerms()) {
				if(!superclassToSubclassMap.containsKey(superclass.getDisambiguatedValue()))
					superclassToSubclassMap.put(superclass.getDisambiguatedValue(), new HashSet<String>());
				superclassToSubclassMap.get(superclass.getDisambiguatedValue()).add(storeRow.getLeadTerm().getDisambiguatedValue());
			}
		}
		return leadTermRowMap;
	}

	private boolean createsCircularRelationship(Term to, Term from, Map<String, Set<Row>> leadTermRowMap) {
		if(leadTermRowMap.containsKey(from.getDisambiguatedValue())) {
			for(Row row : leadTermRowMap.get(from.getDisambiguatedValue())) {
				for(Term attachedTerm : row.getAttachedTerms()) {
					if(createsCircularRelationship(to, attachedTerm, leadTermRowMap))
						return true;
				}
			}
		}
		return false;
	}
}
