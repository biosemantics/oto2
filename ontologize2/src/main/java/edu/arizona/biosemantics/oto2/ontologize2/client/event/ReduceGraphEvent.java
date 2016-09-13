package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReduceGraphEvent.Handler;

public class ReduceGraphEvent extends GwtEvent<Handler> {
	
	public interface Handler extends EventHandler {
		void onReduce(ReduceGraphEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
    
    public ReduceGraphEvent() {
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onReduce(this);
	}

}