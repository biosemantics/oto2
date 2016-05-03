package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceTermInRelationsEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class ReplaceTermInRelationsEvent extends GwtEvent<Handler> implements Serializable {

	public interface Handler extends EventHandler {
		void onDisambiguate(ReplaceTermInRelationsEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private Term oldTerm;
	private Term newTerm;

	private ReplaceTermInRelationsEvent() { }
	
    public ReplaceTermInRelationsEvent(Term oldTerm, Term newTerm) {
    	this.oldTerm = oldTerm;
    	this.newTerm = newTerm;
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onDisambiguate(this);
	}

	public Term getOldTerm() {
		return oldTerm;
	}

	public Term getNewTerm() {
		return newTerm;
	}

	
}
