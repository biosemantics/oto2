package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.CategorizeCopyRemoveTermEvent.CategorizeCopyRemoveTermHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class CategorizeCopyRemoveTermEvent extends GwtEvent<CategorizeCopyRemoveTermHandler> {

	public interface CategorizeCopyRemoveTermHandler extends EventHandler {
		void onRemove(List<Term> terms, Label label);
	}
	
    public static Type<CategorizeCopyRemoveTermHandler> TYPE = new Type<CategorizeCopyRemoveTermHandler>();
    
    private List<Term> terms;
	private Label label;
    
	public CategorizeCopyRemoveTermEvent(Term term, Label label) {
		this.terms = new LinkedList<Term>();
		this.terms.add(term);
		this.label = label;
	}
	
    public CategorizeCopyRemoveTermEvent(List<Term> terms, Label label) {
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

	public List<Term> getTerms() {
		return terms;
	}

	public Label getLabel() {
		return label;
	}

}
