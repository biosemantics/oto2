package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.SynonymCreationEvent.SynonymCreationHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class SynonymCreationEvent extends GwtEvent<SynonymCreationHandler> {

	public interface SynonymCreationHandler extends EventHandler {
		void onSynonymCreation(Term term, Term mainTerm);
	}
	
    public static Type<SynonymCreationHandler> TYPE = new Type<SynonymCreationHandler>();
    
    private Term term;

	private Term mainTerm;
    
    public SynonymCreationEvent(Term term, Term mainTerm) {
        this.term = term;
        this.mainTerm = mainTerm;
    }
	
	@Override
	public Type<SynonymCreationHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SynonymCreationHandler handler) {
		handler.onSynonymCreation(term, mainTerm);
	}

	public Term getTerm() {
		return term;
	}

	public Term getMainTerm() {
		return mainTerm;
	}

	
	
}