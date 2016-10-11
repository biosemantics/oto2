package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent.Handler;

public class CompositeModifyEvent extends GwtEvent<Handler> implements Serializable {

	public interface Handler extends EventHandler {
		void onModify(CompositeModifyEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private List<GwtEvent<?>> events;
	
	public CompositeModifyEvent(List<GwtEvent<?>> events) { 
		this.events = events;
	}
   
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onModify(this);
	}

	public List<GwtEvent<?>> getEvents() {
		return events;
	}
}
