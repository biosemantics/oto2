package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.AddSynonymEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class AddSynonymEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onCreate(AddSynonymEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private Term preferredTerm;
	private Term[] synonyms = new Term[] { };

    public AddSynonymEvent(Term preferredTerm, Term... synonyms) {
    	this.preferredTerm = preferredTerm;
    	this.synonyms = synonyms;
    }
    
	public AddSynonymEvent(Term leadTerm, List<Term> synonyms) {
		this.preferredTerm = preferredTerm;
		this.synonyms = synonyms.toArray(this.synonyms);
	}

	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onCreate(this);
	}

	public Term getPreferredTerm() {
		return preferredTerm;
	}

	public Term[] getSynonyms() {
		return synonyms;
	}	
}
