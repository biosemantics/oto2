package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveSynonymEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class RemoveSynonymEvent extends GwtEvent<Handler> implements HasRowId, Serializable {

	public interface Handler extends EventHandler {
		void onRemove(RemoveSynonymEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private Term preferredTerm;
	private Term[] synonyms = new Term[] { };
	private int rowId = -1;

	private RemoveSynonymEvent() { }

    public RemoveSynonymEvent(Term preferredTerm, Term... synonyms) {
    	this.preferredTerm = preferredTerm;
    	this.synonyms = synonyms;
    }
    
	public RemoveSynonymEvent(Term preferredTerm, List<Term> synonyms) {
		this.preferredTerm = preferredTerm;
		this.synonyms = synonyms.toArray(this.synonyms);
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
	
	public void setSynonyms(Term[] synonyms){ 
		this.synonyms = synonyms;
	}
	
	@Override
	public int getRowId() {
		return rowId;
	}

	@Override
	public void setRowId(int rowId) {
		this.rowId = rowId;
	}

	@Override
	public boolean hasRowId() {
		return rowId != -1;
	}	
	
	public boolean hasSynonyms() {
		return synonyms!=null&&synonyms.length > 0;
	}
}
