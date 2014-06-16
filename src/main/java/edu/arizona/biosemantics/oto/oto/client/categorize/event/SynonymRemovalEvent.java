package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.SynonymRemovalEvent.SynonymRemovalHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class SynonymRemovalEvent extends GwtEvent<SynonymRemovalHandler> {

	public interface SynonymRemovalHandler extends EventHandler {
		void onSynonymCreation(Term term, Term oldMainTerm);
	}
	
    public static Type<SynonymRemovalHandler> TYPE = new Type<SynonymRemovalHandler>();
    
    private Term term;

	private Term oldMainTerm;
    
    public SynonymRemovalEvent(Term term, Term oldMainTerm) {
        this.term = term;
        this.oldMainTerm = oldMainTerm;
    }
	
	public Term getOldMainTerm() {
		return oldMainTerm;
	}

	@Override
	public Type<SynonymRemovalHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SynonymRemovalHandler handler) {
		handler.onSynonymCreation(term, oldMainTerm);
	}

	public Term getTerm() {
		return term;
	}
	
	

}
