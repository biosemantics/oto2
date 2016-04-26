package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.ValueProvider;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.AddSuperclassEvent;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class SuperclassesGrid extends MenuTermsGrid {

	private EventBus eventBus;
	//allowed: subclass of multiple superclasses
	//allowed: duplicates of subclasses: can be consolidated
	//not allowed: circular relationships
	//not allowed: duplicate superclasses in the same subclass

	public SuperclassesGrid(EventBus eventBus) {
		super("is-a", "subclass", "superclass", new ValueProvider<Term, String>() {
			@Override 
			public String getValue(Term object) {
				if(object.hasDisambiguator())
					return object.getValue() + " (" + object.getDisambiguator() + ")";
				return object.getValue();
			}
			@Override
			public void setValue(Term object, String value) { }
			@Override
			public String getPath() {
				return "class-term";
			}
		});
		this.eventBus = eventBus;
	}
	
	@Override
	protected void addAttachedTermsToRow(Row row, List<Term> add) throws Exception {
		boolean valid = true;
		try { 
			validAddTermsToRow(row, add);
		} catch(Exception e) {
			valid = false;
			Alerter.showAlert("Add failed.", e.getMessage());
		}
		if(valid) {
			super.addAttachedTermsToRow(row, add);
			eventBus.fireEvent(new AddSuperclassEvent(row.getLeadTerm(), add));
		}
	}
	
	private void validAddTermsToRow(Row row, List<Term> add) throws Exception { 
		//duplicate superclasses in same subclass
		Term parentTerm = row.getLeadTerm();
		Set<Term> existingSuperclasses = new HashSet<Term>();
		for(Row storeRow : store.getAll()) {
			if(storeRow.getLeadTerm().equals(parentTerm)) {
				existingSuperclasses.addAll(storeRow.getAttachedTerms());
			}
		}
		for(Term addTerm : add) {
			if(existingSuperclasses.contains(addTerm)) {
				throw new Exception("Superclass is already defined as superclass of this subclass");
			}
			existingSuperclasses.add(addTerm);
		}
		
		//circular relationship
		Map<Term, Set<Row>> leadTermRowMap = createLeadTermRowMap();
		
		for(Term addTerm : add) 
			if(createsCircularRelationship(row.getLeadTerm(), addTerm, leadTermRowMap))
				throw new Exception("Adding superclass to subclass creates a circular relationship. Not allowed.");
	}

	private Map<Term, Set<Row>> createLeadTermRowMap() {
		Map<Term, Set<Row>> leadTermRowMap = new HashMap<Term, Set<Row>>();
		Map<Term, Set<Term>> superclassToSubclassMap = new HashMap<Term, Set<Term>>();
		for(Row storeRow : this.getAll()) {
			if(!leadTermRowMap.containsKey(storeRow.getLeadTerm()))
				leadTermRowMap.put(storeRow.getLeadTerm(), new HashSet<Row>());
			leadTermRowMap.get(storeRow.getLeadTerm()).add(storeRow);
			
			for(Term superclass : storeRow.getAttachedTerms()) {
				if(!superclassToSubclassMap.containsKey(superclass))
					superclassToSubclassMap.put(superclass, new HashSet<Term>());
				superclassToSubclassMap.get(superclass).add(storeRow.getLeadTerm());
			}
		}
		return leadTermRowMap;
	}

	private boolean createsCircularRelationship(Term to, Term from, Map<Term, Set<Row>> leadTermRowMap) {
		if(leadTermRowMap.containsKey(from)) {
			for(Row row : leadTermRowMap.get(from)) {
				for(Term attachedTerm : row.getAttachedTerms()) {
					if(createsCircularRelationship(to, attachedTerm, leadTermRowMap))
						return true;
				}
			}
		}
		return false;
	}

	@Override
	protected void addRow(Row row) {
		boolean valid = true;
		try { 
			validAddRow(row, this.getAll());
		} catch(Exception e) {
			valid = false;
			Alerter.showAlert("Add failed.", e.getMessage());
		}
		if(valid) {
			super.addRow(row);	
		}
	}
	
	protected void validAddRow(Row row, List<Row> existingRows) throws Exception {
		//circular relationship
		if(row.hasAttachedTerms()) {
			Map<Term, Set<Row>> leadTermRowMap = this.createLeadTermRowMap();
			for(Term addTerm : row.getAttachedTerms()) 
				if(createsCircularRelationship(row.getLeadTerm(), addTerm, leadTermRowMap))
					throw new Exception("Adding superclass to subclass creates a circular relationship. Not allowed.");
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
			super.setRows(rows);
		}
	}
	
	@Override
	public void remove(Collection<Row> rows) {
		boolean valid = true;
		try { 
			validateRemove(rows);
		} catch(Exception e) {
			valid = false;
			Alerter.showAlert("Add failed.", e.getMessage());
		}
		if(valid) {
			super.remove(rows);
		}
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
		
	@Override
	public void consolidate() {
		super.consolidate();
		//does store manipulation, but has no impact on validation concerns
	}
	
	@Override
	protected void updateRow(Row row) {
		super.updateRow(row);
		//does store manipulation, but has no impact on validation concerns
	}
}
