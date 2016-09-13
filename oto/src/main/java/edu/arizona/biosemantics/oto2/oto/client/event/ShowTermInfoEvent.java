package edu.arizona.biosemantics.oto2.oto.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.ShowTermInfoEvent.Handler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class ShowTermInfoEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onShow(ShowTermInfoEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
    
    private Term term;
    
    public ShowTermInfoEvent(Term term) {
        this.term = term;
    }
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onShow(this);
	}
	
	public Term getTerm(){
		return term;
	}

}

