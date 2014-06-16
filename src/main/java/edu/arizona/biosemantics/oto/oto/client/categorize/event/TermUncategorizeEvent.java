package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermUncategorizeEvent.TermUncategorizeHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class TermUncategorizeEvent extends GwtEvent<TermUncategorizeHandler> {

	public interface TermUncategorizeHandler extends EventHandler {
		void onUncategorize(List<Term> terms, Label oldCategory);
	}
	
    public static Type<TermUncategorizeHandler> TYPE = new Type<TermUncategorizeHandler>();
    
    private List<Term> terms;

	private Label oldCategory;
    
    public TermUncategorizeEvent(List<Term> terms, Label oldCategory) {
        this.terms = terms;
        this.oldCategory = oldCategory;
    }
	
	@Override
	public Type<TermUncategorizeHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TermUncategorizeHandler handler) {
		handler.onUncategorize(terms, oldCategory);
	}

	public List<Term> getTerms() {
		return terms;
	}

	public Label getOldCategory() {
		return oldCategory;
	}
	
	

}
