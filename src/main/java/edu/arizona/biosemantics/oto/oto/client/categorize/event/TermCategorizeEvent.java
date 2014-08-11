package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermCategorizeEvent.TermCategorizeHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class TermCategorizeEvent extends GwtEvent<TermCategorizeHandler> {

	public interface TermCategorizeHandler extends EventHandler {
		void onCategorize(LinkedHashSet<Term> terms, Set<Label> categories);
	}
	
    public static Type<TermCategorizeHandler> TYPE = new Type<TermCategorizeHandler>();
    
    private LinkedHashSet<Term> terms;
	private LinkedHashSet<Label> categories;
    
	public TermCategorizeEvent(LinkedHashSet<Term> terms, LinkedHashSet<Label> categories) {
		this.terms = terms;
		this.categories = categories;
	}
	
    public TermCategorizeEvent(LinkedHashSet<Term> terms, Label category) {
        this.terms = terms;
        this.categories = new LinkedHashSet<Label>();
        this.categories.add(category);
    }
	
	@Override
	public Type<TermCategorizeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TermCategorizeHandler handler) {
		handler.onCategorize(terms, categories);
	}

	public LinkedHashSet<Term> getTerms() {
		return terms;
	}

	public LinkedHashSet<Label> getCategories() {
		return categories;
	}

	
	
}

