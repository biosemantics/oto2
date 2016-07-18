package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import java.util.ArrayList;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreatePartEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.HasRowId;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveSubclassEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceTermInRelationsEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemovePartEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TermDisambiguator;

public class PartsGrid extends MenuTermsGrid {
	
	//allowed: part of multiple parents: e.g. non-specific structure terms: apex can be part of leaf as well as trunk
	//allowed: duplicates of preferred terms: can be consolidated
	//not allowed: circular relationships
	//not allowed: duplicate parts in the same parent
		
	//not allowed: parts with different quantifiers for different parents. It just does not make sense for parts to have something like
	// e.g. 
	// stem, (stem) leaf
	// fruit, (fruit) leaf
	//allowed: parts with same quantifiers for different parents (duplicates). 
	// e.g. 
	// stem, apex
	// leaf, apex
	//allowed: duplicate parts 
	
	/**
	 * obsolete:
	 * //not allowed: part cannot have multiple parents when it is already disambiguated. E.g. leaf, (leaf) leaflet; we cannot define: stem, (leaf) leaflet
	 * // because (leaf) leaflet is already coupled with a leaf parent from which its disambiguator stems. Hence we can't ommit the leaf. leaf, (leaf) leaflet, stem, leaf; would be legal
	 */
	
	private edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection collection;
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	
	public PartsGrid(EventBus eventBus) {
		super(eventBus, "part", "parent", "part", new ValueProvider<Term, String>() {
			@Override 
			public String getValue(Term object) {
				return object.getDisambiguatedValue();
			}
			@Override
			public void setValue(Term object, String value) { }
			@Override
			public String getPath() {
				return "part-term";
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
				for(String parent : collection.getParts().keySet()) {
					Term parentTerm = collection.getTerm(parent);
					eventBus.fireEvent(new CreatePartEvent(parentTerm, collection.getParts(parentTerm)));
				}
			}
		}); 
		eventBus.addHandler(CreatePartEvent.TYPE, new CreatePartEvent.Handler() {
			@Override
			public void onCreate(CreatePartEvent event) {
				
				List<Row> rows = PartsGrid.this.getLeadTermsRows(event.getParent(), true);
				Row row = null;
				if(event.hasRowId()) 
					row = getRowWithId(rows, event.getRowId()); 
				else {
					row = new Row(event.getParent());
					PartsGrid.super.addRow(row);
				}
				//Alerter.showAlert("Create Part", "Create part for "+event.getParent()+" "+event.getParts()[0].getDisambiguatedValue());
				if(row != null)
					try {
						PartsGrid.super.addAttachedTermsToRow(row, Arrays.asList(event.getParts()));
					} catch (Exception e) {
						Alerter.showAlert("Create Part", "Create part failed "+e.getMessage());
					}
			}
		});
		eventBus.addHandler(RemovePartEvent.TYPE, new RemovePartEvent.Handler() {///remove a cell
			@Override
			public void onRemove(RemovePartEvent event) {
				//Alerter.showAlert("selection entry remove", "remove in PartGrid by event from "+event.getParent());
//				//remove row
//				if(event.getParent() == null) {//this is the entry, will remove the entry term
//					for(Term part : event.getParts()) {
//						List<Row> rows = PartsGrid.this.getLeadTermsRows(part, true);
//						if(event.hasRowId()) {
//							Row idRow = getRowWithId(rows, event.getRowId());
//							if(idRow != null) {
//								rows = new LinkedList<Row>();
//								rows.add(idRow);
//							}
//						}
//						if(!rows.isEmpty()) {
//							try {
//								PartsGrid.super.removeRows(rows);
//							} catch (Exception e) {
//								Alerter.showAlert("Remove Part", "Remove part failed");
//							}
//						}
//					}
//				//remove attached terms
//				} else {
//					List<Row> rows = PartsGrid.this.getLeadTermsRows(event.getParent(), true);
//					if(event.hasRowId()) {
//						Row idRow = getRowWithId(rows, event.getRowId());
//						if(idRow != null) {
//							rows = new LinkedList<Row>();
//							rows.add(idRow);
//						}
//					}
//					if(!rows.isEmpty()) {
//						try {
//							PartsGrid.super.removeAttachedTermsFromRows(rows, Arrays.asList(event.getParts()));
//						} catch (Exception e) {
//							Alerter.showAlert("Remove Part", "Remove part failed");
//						}
//					}
//				}	
				List<Row> rows = PartsGrid.this.getLeadTermsRows(event.getParent(), true);
				if(event.hasRowId()) {
					Row idRow = getRowWithId(rows, event.getRowId());//select rowId
					if(idRow != null) {
						rows = new LinkedList<Row>();
						rows.add(idRow);
					}
				}
				if(!rows.isEmpty()) {
					//if the event comes from remove row, superclass is set to null
					if(event.getParts()==null) {//if the entry has subclasses, it can not be removed.
						PartsGrid.super.removeRows(rows);
					} else {//if the entry does have any subclasses, remove its subclasses and then remove it
						try {
							PartsGrid.super.removeAttachedTermsFromRows(rows, Arrays.asList(event.getParts()));
						} catch (Exception e) {
							Alerter.showAlert("Remove Parts", e.getMessage());
						}
					}
				}
			}
		});
	}
	
	@Override
	public void addRow(final Row row) {
		boolean valid = true;
		try { 
			//validAddRow(row, this.getAll());
		} catch(Exception e) {
			valid = false;
			Alerter.showAlert("Add failed.", "Please drop in the right area!");//e.getMessage()
		}
		if(valid) {	
			doAddRow(row);
		}
	}
	
	private void doAddRow(final Row row) {
		List<Term> parts = new LinkedList<Term>();
		parts.add(row.getLeadTerm());
		collectionService.createPart(collection.getId(), collection.getSecret(), 
				null, parts, new AsyncCallback<List<GwtEvent<?>>>() {
					@Override
					public void onFailure(Throwable caught) {
						
					}
					@Override
					public void onSuccess(List<GwtEvent<?>> result) {
						fireEvents(result, null);
						collectionService.createPart(collection.getId(), collection.getSecret(), 
								row.getLeadTerm(), row.getAttachedTerms(), new AsyncCallback<List<GwtEvent<?>>>() {
							@Override
							public void onFailure(Throwable caught) {
								
							}
							@Override
							public void onSuccess(List<GwtEvent<?>> result) {
								fireEvents(result, null);
							}
						});
					}
		});
		
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
				doAddRow(row);
		}
	}
	
	@Override
	public void removeRows(Collection<Row> rows) {
		boolean valid = true;
		try { 
			validateRemove(rows);
		} catch(Exception e) {
			valid = false;
			Alerter.showAlert("Add failed.", e.getMessage());
		}
		if(valid) {
			for(final Row row : rows)
				collectionService.removePart(collection.getId(), collection.getSecret(), 
						row.getLeadTerm(), row.getAttachedTerms(), new AsyncCallback<List<GwtEvent<?>>>() {
					@Override
					public void onFailure(Throwable caught) {
						
					}
					@Override
					public void onSuccess(List<GwtEvent<?>> result) {
						((RemovePartEvent)result.get(0)).setParts(null);
						fireEvents(result, row);
//						List<Term> parts = new LinkedList<Term>();
//						parts.add(row.getLeadTerm());
//						collectionService.removePart(collection.getId(), collection.getSecret(),
//								null, parts, new AsyncCallback<List<GwtEvent<?>>>() {
//									@Override
//									public void onFailure(Throwable caught) {
//										
//									}
//									@Override
//									public void onSuccess(List<GwtEvent<?>> result) {
//										fireEvents(result, row);
//									}
//						});
					}
				});
		}
	}

	@Override
	public void addAttachedTermsToRow(final Row row, List<Term> add) throws Exception {
		boolean valid = true;
		try { 
			validAddTermsToRow(row, add);
		} catch(Exception e) {
			valid = false;
			Alerter.showAlert("Add failed.", e.getMessage());
		}
		final Term parentTerm = row.getLeadTerm();
		if(valid) {
			for(final Term term : add) {
				collectionService.getParents(collection.getId(), collection.getSecret(), term, new AsyncCallback<List<Term>>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
					@Override
					public void onSuccess(List<Term> result) {
						if(!result.isEmpty()) {
							final Term existParentTerm = result.get(0);
							MessageBox box = Alerter.showConfirm("Add parts", 
									"This term \"" + term.getDisambiguatedValue() + "\" already exists as a part of \""
										+ existParentTerm.getDisambiguatedValue()+ "\". "
										+ "Do you agree to apply the following strategy for disambiguation: "
										+ "1) add the term \""+existParentTerm.getDisambiguatedValue()+" "+term.getDisambiguatedValue()+"\" under \""+existParentTerm.getDisambiguatedValue()+"\", "
										+ "2) add the term \""+parentTerm.getDisambiguatedValue()+" "+term.getDisambiguatedValue()+"\" under \""+parentTerm.getDisambiguatedValue()+"\", "
										+ "3) remove the term \""+term.getDisambiguatedValue()+"\" under \""+existParentTerm.getDisambiguatedValue()+"\""
										+ " If NO, please create a new term then add as a part of \""+parentTerm.getDisambiguatedValue()+"\".");
							box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
								@Override
								public void onSelect(SelectEvent event) {
									///createPart(parentTerm, term, row, true);
									createTwoDisambiguateParts(existParentTerm, term, row.getLeadTerm() , row, true);
								}
							});
							box.getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
								@Override
								public void onSelect(SelectEvent event) {
									//createPart(row.getLeadTerm(), term, row, true);
								}
							});
						} else {
							createPart(row.getLeadTerm(), term, row, false);
						}
					}
				});
			}
		}
	}
	
	@Override
	public void removeAttachedTermsFromRow(final Row row, List<Term> terms) {
		collectionService.removePart(collection.getId(), collection.getSecret(), 
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
		
	private void createPart(Term partent, Term part, final Row row, boolean disambiguate) {
		///Alerter.showAlert("create part", "parent="+partent.getDisambiguatedValue()+"/"+part.getDisambiguatedValue());
		collectionService.createPart(collection.getId(), collection.getSecret(), 
				partent, part, disambiguate, new AsyncCallback<List<GwtEvent<?>>>() {
			@Override
			public void onFailure(Throwable caught) {
				//TODO
				Alerter.showAlert("Disambigate failed", caught.getMessage());
			}
			@Override
			public void onSuccess(List<GwtEvent<?>> result) {
//				for(GwtEvent ge:result){
//					if(ge.getClass().getName().indexOf("RemovePartEvent")>-1) ((RemovePartEvent)ge).setParts(null);
//				}
				fireEvents(result, row);
			}
		});
	}

	private void validateRemove(Collection<Row> rows) throws Exception {
		for(Row row : rows) {
			if(store.findModel(row) == null) {
				throw new Exception("Row does not exist");
			}
		}
	}
	
	private void validAddRow(Row row, List<Row> existingRows) throws Exception {
		//circular relationship
		/*if(row.hasAttachedTerms()) {
			Map<String, Set<Row>> leadTermRowMap = createLeadTermRowMap();
			for(Term addTerm : row.getAttachedTerms()) 
				if(createsCircularRelationship(row.getLeadTerm(), addTerm, leadTermRowMap))
					throw new Exception("Adding part to parent creates a circular relationship. Not allowed.");
		}*/
	}

	private void validSetRows(List<Row> rows) throws Exception {
		List<Row> hypotheticRows = new LinkedList<Row>();
		for(Row row : rows) {
			validAddRow(row, hypotheticRows);
			hypotheticRows.add(row);
		}
	}	

	private void validAddTermsToRow(Row row, List<Term> add) throws Exception { 
		//duplicate parts in same parent
		Term parentTerm = row.getLeadTerm();
		Set<String> existingParts = new HashSet<String>();
		for(Row storeRow : store.getAll()) {
			if(storeRow.getLeadTerm().equals(parentTerm)) {
				for(Term attachedTerm : storeRow.getAttachedTerms())
					existingParts.add(attachedTerm.getDisambiguatedValue());
			}
		}
		for(Term addTerm : add) {
			if(existingParts.contains(addTerm.getDisambiguatedValue())) {
				throw new Exception("Part is already defined as part of this parent");
			}
			existingParts.add(addTerm.getDisambiguatedValue());
		}
		
		//already disambiguated part cannot have multiple parents
		for(Term addTerm : add) {
			if(addTerm.hasPartDisambiguator() && !this.getAttachedTermsRows(addTerm, true).isEmpty()) {
				throw new Exception("Disambiguated part is already defined as part of another parent");
			}
		}
		
		//circular relationship
		Map<String, Set<Row>> leadTermRowMap = createLeadTermRowMap();
		for(Term addTerm : add) 
			if(createsCircularRelationship(row.getLeadTerm(), addTerm, leadTermRowMap))
				throw new Exception("Adding part to parent creates a circular relationship. Not allowed.");
	}
	
	private Map<String, Set<Row>> createLeadTermRowMap() {
		Map<String, Set<Row>> leadTermRowMap = new HashMap<String, Set<Row>>();
		Map<String, Set<String>> subclassToSuperclassMap = new HashMap<String, Set<String>>();
		for(Row storeRow : this.getAll()) {
			if(!leadTermRowMap.containsKey(storeRow.getLeadTerm().getDisambiguatedValue()))
				leadTermRowMap.put(storeRow.getLeadTerm().getDisambiguatedValue(), new HashSet<Row>());
			leadTermRowMap.get(storeRow.getLeadTerm().getDisambiguatedValue()).add(storeRow);
			
			for(Term subclass : storeRow.getAttachedTerms()) {
				if(!subclassToSuperclassMap.containsKey(subclass.getDisambiguatedValue()))
					subclassToSuperclassMap.put(subclass.getDisambiguatedValue(), new HashSet<String>());
				subclassToSuperclassMap.get(subclass.getDisambiguatedValue()).add(storeRow.getLeadTerm().getDisambiguatedValue());
			}
		}
		return leadTermRowMap;
	}

	private boolean createsCircularRelationship(Term to, Term from, Map<String, Set<Row>> leadTermRowMap) {
		if(to.getDisambiguatedValue().equals(from.getDisambiguatedValue()))
			return true;
		if(leadTermRowMap.containsKey(from.getDisambiguatedValue())) {
			for(Row row : leadTermRowMap.get(from.getDisambiguatedValue())) {
				for(Term attachedTerm : row.getAttachedTerms()) {
					//if(createsCircularRelationship(to, attachedTerm, leadTermRowMap))
					if(to.equals(attachedTerm))
						return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 1. change the term to Parent1+Term
	 * 2. add a new term,
	 * 3. attach the new term to the parent2 + term
	 */
	private void createTwoDisambiguateParts(
			Term parentTerm, Term term, Term leadTerm, final Row row,
			boolean disambiguate) {
		collectionService.changeAndCreateParts(collection.getId(), collection.getSecret(), 
				parentTerm, term, leadTerm, disambiguate, new AsyncCallback<List<GwtEvent<?>>>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.showAlert("Disambiguate Fail", caught.getMessage());
			}
			@Override
			public void onSuccess(List<GwtEvent<?>> result) {
				/*
				List<GwtEvent<?>> removeEvents = new LinkedList<GwtEvent<?>>();
				for(int i=0;i<result.size();){
					GwtEvent ge=result.get(i);
					Alerter.showAlert("ge.getClass().getName()", ge.getClass().getName());
					if(ge.getClass().getName().indexOf("RemovePartEvent")>-1){
						removeEvents.add(ge);
						result.remove(ge);
					}else{
						i++;
					}
				}
				//fire remove events first
				fireEvents(removeEvents, row);
				//then fire add events*/
				//fireEvents(result, row);
				Alerter.showAlert("Page Reload", "The whole page will be reloaded!");
				Window.Location.reload();
			}
		});
	}
}
