package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermUncategorizeEvent.TermUncategorizeHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class TermUncategorizeEvent extends GwtEvent<TermUncategorizeHandler> {

	public interface TermUncategorizeHandler extends EventHandler {
		void onUncategorize(LinkedHashSet<Term> terms, Set<Label> oldLabels);
	}
	
    public static Type<TermUncategorizeHandler> TYPE = new Type<TermUncategorizeHandler>();
    
    private LinkedHashSet<Term> terms;
	private LinkedHashSet<Label> oldLabels;
	
	public TermUncategorizeEvent(Term term, Label oldLabel) {
		this.terms = new LinkedHashSet<Term>();
		terms.add(term);
		this.oldLabels = new LinkedHashSet<Label>();
		oldLabels.add(oldLabel);
	}
	
	public TermUncategorizeEvent(Term term, LinkedHashSet<Label> oldLabels) {
		this.terms = new LinkedHashSet<Term>();
		terms.add(term);
		this.oldLabels = oldLabels;
	}
    
    /*public TermUncategorizeEvent(List<Term> terms, Label oldLabel) {
        this.terms = terms;
        this.oldLabels = new HashSet<Label>(1);
        oldLabels.add(oldLabel);
    }
	
    public TermUncategorizeEvent(List<Term> terms, Set<Label> oldLabels) {
        this.terms = terms;
        this.oldLabels = oldLabels;
    }*/
	
	@Override
	public Type<TermUncategorizeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TermUncategorizeHandler handler) {
		handler.onUncategorize(terms, oldLabels);
	}

	public LinkedHashSet<Term> getTerms() {
		return terms;
	}

	public Set<Label> getOldLabels() {
		return oldLabels;
	}

}
