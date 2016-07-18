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
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateSynonymEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.HasRowId;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemovePartEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveSynonymEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class SynonymsGrid extends MenuTermsGrid {
	
	//allowed: duplicates of preferred terms: can be consolidated
	//not allowed: same synonym term for different preferred terms
	//not allowed: a term that's preferred term and synonym anywhere at the same time
	private edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection collection;
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	
	private Map<String, Integer> synonymTerms = new HashMap<String, Integer>();
	private Map<String, Integer> preferredTerms = new HashMap<String, Integer>();
	
	public SynonymsGrid(EventBus eventBus) {
		super(eventBus, "synonym", "preferred term", "synonym", new ValueProvider<Term, String>() {
			@Override 
			public String getValue(Term object) {
				return object.getDisambiguatedValue();
			}
			@Override
			public void setValue(Term object, String value) { }
			@Override
			public String getPath() {
				return "synonym-term";
			}
		});
	}
	
	private void addPreferredTerm(Term term, Map<String, Integer> preferredTerms) {
		if(!preferredTerms.containsKey(term.getDisambiguatedValue()))
			preferredTerms.put(term.getDisambiguatedValue(), 0);
		preferredTerms.put(term.getDisambiguatedValue(), preferredTerms.get(term.getDisambiguatedValue()) + 1);
	}
	
	private void addSynonyms(List<Term> terms, Map<String, Integer> synonymTerms) {
		for(Term term : terms)
			addSynonym(term, synonymTerms);
	}
	
	private void addSynonym(Term term, Map<String, Integer> synonymTerms) {
		if(!synonymTerms.containsKey(term.getDisambiguatedValue()))
			synonymTerms.put(term.getDisambiguatedValue(), 0);
		synonymTerms.put(term.getDisambiguatedValue(), synonymTerms.get(term.getDisambiguatedValue()) + 1);
	}
	
	private void removePreferredTerm(Term term, Map<String, Integer> preferredTerms) {
		if(!preferredTerms.containsKey(term.getDisambiguatedValue()))
			return;
		int count = preferredTerms.get(term.getDisambiguatedValue()) - 1;
		if(count <= 0)
			preferredTerms.remove(term.getDisambiguatedValue());
		else
			preferredTerms.put(term.getDisambiguatedValue(), count);
	}
	
	private void removeSynonyms(Term[] terms, Map<String, Integer> synonymTerms) {
		for(Term term : terms)
			removeSynonym(term, synonymTerms);
	}
	
	private void removeSynonym(Term term, Map<String, Integer> synonymTerms) {
		if(!synonymTerms.containsKey(term.getDisambiguatedValue()))
			return;
		int count = synonymTerms.get(term.getDisambiguatedValue()) - 1;
		if(count <= 0)
			synonymTerms.remove(term.getDisambiguatedValue());
		else
			synonymTerms.put(term.getDisambiguatedValue(), count);
	}
	
	@Override
	public void bindEvents() {
		super.bindEvents();
		
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				collection = event.getCollection();
				store.clear();
				for(String preferred : collection.getSynonyms().keySet()) {
					Term preferredTerm = collection.getTerm(preferred);
					eventBus.fireEvent(new CreateSynonymEvent(preferredTerm, collection.getSynonyms(preferredTerm)));
				}
			}
		}); 
		eventBus.addHandler(CreateSynonymEvent.TYPE, new CreateSynonymEvent.Handler() {
			@Override
			public void onCreate(CreateSynonymEvent event) {
				List<Row> rows = SynonymsGrid.this.getLeadTermsRows(event.getPreferredTerm(), true);
				Row row = null;
				if(event.hasRowId()) 
					row = getRowWithId(rows, event.getRowId()); 
				else {
					row = new Row(event.getPreferredTerm());
					SynonymsGrid.super.addRow(row);
				}
				if(row != null)
					try {
						addPreferredTerm(event.getPreferredTerm(), preferredTerms);
						addSynonyms(Arrays.asList(event.getSynonyms()), synonymTerms);
						SynonymsGrid.super.addAttachedTermsToRow(row, Arrays.asList(event.getSynonyms()));
					} catch (Exception e) {
						Alerter.showAlert("Create Synonym", "Create synonym failed");
					}
			}
		});
		eventBus.addHandler(RemoveSynonymEvent.TYPE, new RemoveSynonymEvent.Handler() {
			@Override
			public void onRemove(RemoveSynonymEvent event) {
				List<Row> rows = SynonymsGrid.this.getLeadTermsRows(event.getPreferredTerm(), true);
				if(event.hasRowId()) {
					Row idRow = getRowWithId(rows, event.getRowId());
					if(idRow != null) {
						rows = new LinkedList<Row>();
						rows.add(idRow);
					}
				}
				//Alerter.showAlert("rows.isEmpty()", rows.isEmpty()+"");
				if(!rows.isEmpty()) {
					//Alerter.showAlert("event.hasSynonyms()", event.hasSynonyms()+"");
					if(!event.hasSynonyms()) {
						//Alerter.showAlert("remove preferredTerms and synonyms", event.getPreferredTerm().getValue());
						removePreferredTerm(event.getPreferredTerm(), preferredTerms);
						SynonymsGrid.super.removeRows(rows);
					} else {
						try {
							//Alerter.showAlert("remove synonyms", event.getPreferredTerm().getValue());
							removeSynonyms(event.getSynonyms(), synonymTerms);
							SynonymsGrid.super.removeAttachedTermsFromRows(rows, Arrays.asList(event.getSynonyms()));
						} catch (Exception e) {
							Alerter.showAlert("Remove Synonym", "Remove synonym failed");
						}
					}
				}
					
			}
		});
	}
	
	@Override
	public void addRow(Row row) {
		boolean valid = true;
		try { 
			validAddRow(row.getLeadTerm(), this.preferredTerms, this.synonymTerms);
		} catch(Exception e) {
			valid = false;
			Alerter.showAlert("Add failed.", e.getMessage());
		}
		if(valid) {
			collectionService.createSynonym(collection.getId(), collection.getSecret(), 
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
	public void setRows(List<Row> rows) {
		boolean valid = true;
		try { 
			validSetRows(rows);
		} catch(Exception e) {
			valid = false;
			Alerter.showAlert("Add failed.", e.getMessage());
		}
		
		if(valid) {
			for(final Row row : rows)
				collectionService.createSynonym(collection.getId(), collection.getSecret(), 
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
	public void removeRows(Collection<Row> rows) {
		boolean valid = true;
		try { 
			validateRemove(rows);
		} catch(Exception e) {
			valid = false;
			Alerter.showAlert("Remove failed.", e.getMessage());
		}
		if(valid) {
			for(final Row row : rows)
				collectionService.removeSynonym(collection.getId(), collection.getSecret(), 
						row.getLeadTerm(), row.getAttachedTerms(), new AsyncCallback<List<GwtEvent<?>>>() {
					@Override
					public void onFailure(Throwable caught) {
						
					}
					@Override
					public void onSuccess(List<GwtEvent<?>> result) {
						//Alerter.showAlert("Remove failed 1", "");
						((RemoveSynonymEvent)result.get(0)).setSynonyms(null);//in order to indicate this event is removeRows
						//Alerter.showAlert("Remove failed 2", "");
						fireEvents(result, row);
					}
				});
		}
	}
	
	@Override
	public void addAttachedTermsToRow(final Row row, List<Term> add) throws Exception {
		boolean valid = true;
		try { 
			validAddTermsToRow(row, add, preferredTerms, synonymTerms);
		} catch(Exception e) {
			valid = false;
			Alerter.showAlert("Add failed.", e.getMessage());
		}
		if(valid) {
			collectionService.createSynonym(collection.getId(), collection.getSecret(), 
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
	public void removeAttachedTermsFromRow(final Row row, List<Term> terms) {
		collectionService.removeSynonym(collection.getId(), collection.getSecret(), 
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
	
	private void validAddTermsToRow(Row row, List<Term> add, Map<String, Integer> preferredTerms, Map<String, Integer> synonymTerms) throws Exception { 
		for(Term term : add) {
			if(synonymTerms.containsKey(term.getDisambiguatedValue()))
				throw new Exception("Synonym term is already defined as a synonym of another term.");
			if(preferredTerms.containsKey(term.getDisambiguatedValue()))
				throw new Exception("Synonym term is already defined as a preferred term");
		}
	}
	
	private void validAddRow(Term preferredTerm, Map<String, Integer> preferredTerms, Map<String, Integer> synonymTerms) throws Exception {
		if(synonymTerms.containsKey(preferredTerm.getDisambiguatedValue()))
			throw new Exception("Preferred term is already defined as a synonym of another term.");
		/*for(Term term : row.getAttachedTerms()) {
			if(synonymTerms.contains(term))
				throw new Exception("Synonym term is already defined as a synonym of another term.");
			if(preferredTerms.contains(term))
				throw new Exception("Synonym term is already defined as a preferred term");
		}*/
	}
	
	private void validSetRows(List<Row> rows) throws Exception {
		Map<String, Integer> preferredTerms = new HashMap<String, Integer>();
		Map<String, Integer> synonymTerms = new HashMap<String, Integer>();
		for(Row row : rows) {
			validAddRow(row.getLeadTerm(), preferredTerms, synonymTerms);
			Row hypotheticRow = new Row(row.getLeadTerm());
			this.addPreferredTerm(row.getLeadTerm(), preferredTerms);
			validAddTermsToRow(hypotheticRow, row.getAttachedTerms(), preferredTerms, synonymTerms);
			this.addSynonyms(row.getAttachedTerms(), synonymTerms);
		}
	}
	
	private void validateRemove(Collection<Row> rows) throws Exception {
		for(Row row : rows) {
			if(store.findModel(row) == null) {
				throw new Exception("Row does not exist");
			}
		}
	}
}
