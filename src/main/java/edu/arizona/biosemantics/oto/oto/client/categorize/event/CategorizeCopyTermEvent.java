package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.CategorizeCopyTermEvent.CategorizeCopyTermHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Label.AddResult;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class CategorizeCopyTermEvent extends GwtEvent<CategorizeCopyTermHandler> {

	public interface CategorizeCopyTermHandler extends EventHandler {
		void onCategorize(List<Term> terms, Label sourceCategory, List<Label> targetCategories);
	}
	
    public static Type<CategorizeCopyTermHandler> TYPE = new Type<CategorizeCopyTermHandler>();
    
    private List<Term> terms;
	private Label sourceCategory;
	private List<Label> targetCategories;
	private  Map<Term, AddResult> addResults;
	
	public CategorizeCopyTermEvent(List<Term> terms, Label sourceCategory, List<Label> targetCategories, Map<Term, AddResult> addResults) {
		this.terms = terms;
		this.sourceCategory = sourceCategory;
		this.targetCategories = targetCategories;
		this.addResults = addResults;
	}
	
	public CategorizeCopyTermEvent(Term term, Label sourceCategory, List<Label> targetCategories, Map<Term, AddResult> addResults) {
		this.terms = new LinkedList<Term>();
		terms.add(term);
		this.sourceCategory = sourceCategory;
		this.targetCategories = targetCategories;
		this.addResults = addResults;
	}
	
	public CategorizeCopyTermEvent(Term term, Label sourceCategory, Label targetCategory, Map<Term, AddResult> addResults) {
		this.terms = new LinkedList<Term>();
		terms.add(term);
		this.sourceCategory = sourceCategory;
		this.targetCategories = new LinkedList<Label>();
		targetCategories.add(targetCategory);
		this.addResults = addResults;
	}
    
    public CategorizeCopyTermEvent(List<Term> terms, Label sourceCategory, Label targetCategory, Map<Term, AddResult> addResults) {
        this.terms = terms;
        this.sourceCategory = sourceCategory;	
        this.targetCategories = new LinkedList<Label>();
		targetCategories.add(targetCategory);
		this.addResults = addResults;
    }
	
	@Override
	public Type<CategorizeCopyTermHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CategorizeCopyTermHandler handler) {
		handler.onCategorize(terms, sourceCategory, targetCategories);
	}

	public List<Term> getTerms() {
		return terms;
	}

	public Label getSourceCategory() {
		return sourceCategory;
	}

	public List<Label> getTargetCategories() {
		return targetCategories;
	}

	public Map<Term, AddResult> getAddResults() {
		return addResults;
	}
	
}
