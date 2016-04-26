package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemovePartEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class RemovePartEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onRemove(RemovePartEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private Term parent;
	private Term[] parts = new Term[] { };
	
    public RemovePartEvent(Term parent, Term... parts) {
    	this.parent = parent;
    	this.parts = parts;
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onRemove(this);
	}

	public Term getParent() {
		return parent;
	}

	public Term[] getParts() {
		return parts;
	}	
}
