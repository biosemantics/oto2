package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveSynonymEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class RemoveSynonymEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onRemove(RemoveSynonymEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private Term preferredTerm;
	private Term[] synonyms = new Term[] { };

    public RemoveSynonymEvent(Term preferredTerm, Term... synonyms) {
    	this.preferredTerm = preferredTerm;
    	this.synonyms = synonyms;
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onRemove(this);
	}

	public Term getPreferredTerm() {
		return preferredTerm;
	}

	public Term[] getSynonyms() {
		return synonyms;
	}


}
