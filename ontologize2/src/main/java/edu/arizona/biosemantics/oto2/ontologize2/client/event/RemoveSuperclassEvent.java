package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveSuperclassEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class RemoveSuperclassEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onRemove(RemoveSuperclassEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private Term subclass;
	private Term[] superclasses = new Term[] { };

    public RemoveSuperclassEvent(Term subclass, Term... superclasses) {
    	this.subclass = subclass;
    	this.superclasses = superclasses;
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onRemove(this);
	}

	public Term getSubclass() {
		return subclass;
	}

	public Term[] getSuperclasses() {
		return superclasses;
	}
	
}
