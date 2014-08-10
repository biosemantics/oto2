package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.CategorizeMoveTermEvent.CategorizeMoveTermHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class CategorizeMoveTermEvent extends GwtEvent<CategorizeMoveTermHandler> {

	public interface CategorizeMoveTermHandler extends EventHandler {
		void onCategorize(List<Term> terms, Label sourceCategory, Label targetCategory);
	}
	
    public static Type<CategorizeMoveTermHandler> TYPE = new Type<CategorizeMoveTermHandler>();
    
    private List<Term> terms;
	private Label sourceCategory;
	private Label targetCategory;
	
    public CategorizeMoveTermEvent(Term term, Label sourceCategory, Label targetCategory) {
        this.terms = new LinkedList<Term>();
        terms.add(term);
        this.sourceCategory = sourceCategory;
        this.targetCategory = targetCategory;
    }
    
    public CategorizeMoveTermEvent(List<Term> terms, Label sourceCategory, Label targetCategory) {
        this.terms = terms;
        this.sourceCategory = sourceCategory;
        this.targetCategory = targetCategory;
    }
	
	@Override
	public Type<CategorizeMoveTermHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CategorizeMoveTermHandler handler) {
		handler.onCategorize(terms, sourceCategory, targetCategory);
	}

	public List<Term> getTerms() {
		return terms;
	}

	public Label getSourceCategory() {
		return sourceCategory;
	}

	public Label getTargetCategory() {
		return targetCategory;
	}
	
}
