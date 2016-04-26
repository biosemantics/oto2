package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.AddPartEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class AddPartEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onCreate(AddPartEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private Term parent;
	private Term[] parts = new Term[] { };
    
    public AddPartEvent(Term parent, Term... parts) {
    	this.parent = parent;
    	this.parts = parts;
    }
    
	public AddPartEvent(Term parent, List<Term> parts) {
		this.parent = parent;
		this.parts = parts.toArray(this.parts);
	}

	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onCreate(this);
	}

	public Term getParent() {
		return parent;
	}

	public Term[] getParts() {
		return parts;
	}	
}
