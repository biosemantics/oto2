package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveTermEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class RemoveTermEvent extends GwtEvent<Handler> implements Serializable {

	public interface Handler extends EventHandler {
		void onCreate(RemoveTermEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private Term[] terms = new Term[] { };

	private RemoveTermEvent() { }

    public RemoveTermEvent(Term... terms) {
    	this.terms = terms;
    }
	
	public RemoveTermEvent(List<Term> terms) {
		this.terms = terms.toArray(this.terms);
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
