package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.CategorizeCopyTermEvent.CategorizeCopyTermHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class CategorizeCopyTermEvent extends GwtEvent<CategorizeCopyTermHandler> {

	public interface CategorizeCopyTermHandler extends EventHandler {
		void onCategorize(LinkedHashSet<Term> terms, Label sourceCategory, LinkedHashSet<Label> targetCategories);
	}
	
    public static Type<CategorizeCopyTermHandler> TYPE = new Type<CategorizeCopyTermHandler>();
    
    private LinkedHashSet<Term> terms;
	private Label sourceCategory;
	private LinkedHashSet<Label> targetCategories;
	
	public CategorizeCopyTermEvent(LinkedHashSet<Term> terms, Label sourceCategory, LinkedHashSet<Label> targetCategories) {
		this.terms = terms;
		this.sourceCategory = sourceCategory;
		this.targetCategories = targetCategories;
	}
	
	public CategorizeCopyTermEvent(Term term, Label sourceCategory, LinkedHashSet<Label> targetCategories) {
		this.terms = new LinkedHashSet<Term>();
		terms.add(term);
		this.sourceCategory = sourceCategory;
		this.targetCategories = targetCategories;
	}
	
	public CategorizeCopyTermEvent(Term term, Label sourceCategory, Label targetCategory) {
		this.terms = new LinkedHashSet<Term>();
		terms.add(term);
		this.sourceCategory = sourceCategory;
		this.targetCategories = new LinkedHashSet<Label>();
		targetCategories.add(targetCategory);
	}
    
    public CategorizeCopyTermEvent(LinkedHashSet<Term> terms, Label sourceCategory, Label targetCategory) {
        this.terms = terms;
        this.sourceCategory = sourceCategory;	
        this.targetCategories = new LinkedHashSet<Label>();
		targetCategories.add(targetCategory);
    }
	
	@Override
	public Type<CategorizeCopyTermHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CategorizeCopyTermHandler handler) {
		handler.onCategorize(terms, sourceCategory, targetCategories);
	}

	public LinkedHashSet<Term> getTerms() {
		return terms;
	}

	public Label getSourceCategory() {
		return sourceCategory;
	}

	public Set<Label> getTargetCategories() {
		return targetCategories;
	}

}
