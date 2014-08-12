package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermCategorizeEvent.TermCategorizeHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class TermCategorizeEvent extends GwtEvent<TermCategorizeHandler> {

	public interface TermCategorizeHandler extends EventHandler {
		void onCategorize(List<Term> terms, List<Label> categories);
	}
	
    public static Type<TermCategorizeHandler> TYPE = new Type<TermCategorizeHandler>();
    
    private List<Term> terms;
	private List<Label> categories;
    
	public TermCategorizeEvent(List<Term> terms, List<Label> categories) {
		this.terms = terms;
		this.categories = categories;
	}
	
    public TermCategorizeEvent(List<Term> terms, Label category) {
        this.terms = terms;
        this.categories = new LinkedList<Label>();
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

	public List<Term> getTerms() {
		return terms;
	}

	public List<Label> getCategories() {
		return categories;
	}

	
	
}

