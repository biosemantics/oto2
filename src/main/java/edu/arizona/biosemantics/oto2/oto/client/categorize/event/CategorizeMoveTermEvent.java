package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.CategorizeMoveTermEvent.CategorizeMoveTermHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Label.AddResult;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class CategorizeMoveTermEvent extends GwtEvent<CategorizeMoveTermHandler> {

	public interface CategorizeMoveTermHandler extends EventHandler {
		void onCategorize(List<Term> terms, Label sourceCategory, Label targetCategory);
	}
	
    public static Type<CategorizeMoveTermHandler> TYPE = new Type<CategorizeMoveTermHandler>();
    
    private List<Term> terms;
	private Label sourceCategory;
	private Label targetCategory;
	private Map<Term, AddResult> addResults;
	
    public CategorizeMoveTermEvent(Term term, Label sourceCategory, Label targetCategory, Map<Term, AddResult> addResults) {
        this.terms = new LinkedList<Term>();
        terms.add(term);
        this.sourceCategory = sourceCategory;
        this.targetCategory = targetCategory;
        this.addResults = addResults;
    }
    
    public CategorizeMoveTermEvent(List<Term> terms, Label sourceCategory, Label targetCategory, Map<Term, AddResult> addResults) {
        this.terms = terms;
        this.sourceCategory = sourceCategory;
        this.targetCategory = targetCategory;
        this.addResults = addResults;
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

	public Map<Term, AddResult> getAddResults() {
		return addResults;
	}
	
	
	
}
