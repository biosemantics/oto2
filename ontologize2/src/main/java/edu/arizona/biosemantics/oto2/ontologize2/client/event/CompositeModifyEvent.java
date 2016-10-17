package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent.Handler;

public class CompositeModifyEvent extends GwtEvent<Handler> implements Serializable, HasIsRemote {

	public interface Handler extends EventHandler {
		void onModify(CompositeModifyEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private List<GwtEvent<?>> events = new LinkedList<GwtEvent<?>>();
	private boolean isRemote = true;
	
	public CompositeModifyEvent() { }
	
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

	@Override
	public void setIsRemote(boolean isRemote) {
		this.isRemote = isRemote;
	}

	@Override
	public boolean isRemote() {
		return isRemote;
	}
}
