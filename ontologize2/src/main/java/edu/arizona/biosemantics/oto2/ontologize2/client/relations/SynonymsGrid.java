package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.ValueProvider;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.AddSynonymEvent;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class SynonymsGrid extends MenuTermsGrid {
	
	//allowed: duplicates of preferred terms: can be consolidated
	//not allowed: same synonym term for different preferred terms
	//not allowed: a term that's preferred term and synonym anywhere at the same time
	private Set<Term> synonymTerms = new HashSet<Term>();
	private Set<Term> preferredTerms = new HashSet<Term>();
	private EventBus eventBus;
	
	public SynonymsGrid(EventBus eventBus) {
		super("synonym", "preferred term", "synonym", new ValueProvider<Term, String>() {
			@Override 
			public String getValue(Term object) {
				if(object.hasDisambiguator())
					return object.getDisambiguator() + " " + object.getValue();
				return object.getValue();
			}
			@Override
			public void setValue(Term object, String value) { }
			@Override
			public String getPath() {
				return "synonym-term";
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
			synonymTerms.addAll(add);
			eventBus.fireEvent(new AddSynonymEvent(row.getLeadTerm(), add));
		}
	}
	
	private void validAddTermsToRow(Row row, List<Term> add) throws Exception { 
		for(Term term : add) {
			if(synonymTerms.contains(term))
				throw new Exception("Synonym term is already defined as a synonym of another term.");
			if(preferredTerms.contains(term))
				throw new Exception("Synonym term is already defined as a preferred term");
		}
	}

	@Override
	protected void addRow(Row row) {
		boolean valid = true;
		try { 
			validAddRow(row, this.preferredTerms, this.synonymTerms);
		} catch(Exception e) {
			valid = false;
			Alerter.showAlert("Add failed.", e.getMessage());
		}
		if(valid) {
			super.addRow(row);	
			this.preferredTerms.add(row.getLeadTerm());
			this.synonymTerms.addAll(row.getAttachedTerms());
		}
	}
	
	protected void validAddRow(Row row, Set<Term> preferredTerms, Set<Term> synonymTerms) throws Exception {
		if(synonymTerms.contains(row.getLeadTerm()))
			throw new Exception("Preferred term is already defined as a synonym of another term.");
		for(Term term : row.getAttachedTerms()) {
			if(synonymTerms.contains(term))
				throw new Exception("Synonym term is already defined as a synonym of another term.");
			if(preferredTerms.contains(term))
				throw new Exception("Synonym term is already defined as a preferred term");
		}
	}
	
	@Override
	protected void setRows(List<Row> rows) {
		boolean valid = true;
		Set<Term> preferredTerms = new HashSet<Term>();
		Set<Term> synonymTerms = new HashSet<Term>();
		try { 
			validSetRows(rows, preferredTerms, synonymTerms);
		} catch(Exception e) {
			valid = false;
			Alerter.showAlert("Add failed.", e.getMessage());
		}
		
		if(valid) {
			super.setRows(rows);	
			this.preferredTerms = preferredTerms;
			this.synonymTerms = synonymTerms;
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
			initializePreferredAndSynonymTerms();
		}
	}

	private void initializePreferredAndSynonymTerms() {
		this.preferredTerms = new HashSet<Term>();
		this.synonymTerms = new HashSet<Term>();
		for(Row row : store.getAll()) {
			preferredTerms.add(row.getLeadTerm());
			synonymTerms.addAll(row.getAttachedTerms());
		}
	}

	private void validateRemove(Collection<Row> rows) throws Exception {
		for(Row row : rows) {
			if(store.findModel(row) == null) {
				throw new Exception("Row does not exist");
			}
		}
	}

	private void validSetRows(List<Row> rows, Set<Term> preferredTerms2, Set<Term> synonymTerms2) throws Exception {
		for(Row row : rows) {
			validAddRow(row, preferredTerms, synonymTerms);
			preferredTerms.add(row.getLeadTerm());
			synonymTerms.addAll(row.getAttachedTerms());
		}
	}
		
	@Override
	public void consolidate() {
		super.consolidate();
		//does store manipulation, but preferred terms set and synonymterms set should stay the same
	}
	
	@Override
	protected void updateRow(Row row) {
		super.updateRow(row);
		//does store manipulation, but preferred terms set and synonymterms set should stay the same
	}
}
