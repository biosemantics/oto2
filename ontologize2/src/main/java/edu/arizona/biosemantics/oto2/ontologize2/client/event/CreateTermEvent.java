package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateTermEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class CreateTermEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onCreate(CreateTermEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private Term[] terms = new Term[] { };

    public CreateTermEvent(Term... terms) {
    	this.terms = terms;
    }
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onCreate(this);
	}

	public Term[] getTerms() {
		return terms;
	}
}
