package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import java.util.LinkedHashSet;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.CategorizeCopyRemoveTermEvent.CategorizeCopyRemoveTermHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class CategorizeCopyRemoveTermEvent extends GwtEvent<CategorizeCopyRemoveTermHandler> {

	public interface CategorizeCopyRemoveTermHandler extends EventHandler {
		void onRemove(LinkedHashSet<Term> terms, Label label);
	}
	
    public static Type<CategorizeCopyRemoveTermHandler> TYPE = new Type<CategorizeCopyRemoveTermHandler>();
    
    private LinkedHashSet<Term> terms;
	private Label label;
    
	public CategorizeCopyRemoveTermEvent(Term term, Label label) {
		this.terms = new LinkedHashSet<Term>();
		this.terms.add(term);
		this.label = label;
	}
	
    public CategorizeCopyRemoveTermEvent(LinkedHashSet<Term> terms, Label label) {
        this.terms = terms;
        this.label = label;
    }
	
	@Override
	public Type<CategorizeCopyRemoveTermHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CategorizeCopyRemoveTermHandler handler) {
		handler.onRemove(terms, label);
	}

	public LinkedHashSet<Term> getTerms() {
		return terms;
	}

	public Label getLabel() {
		return label;
	}

}
