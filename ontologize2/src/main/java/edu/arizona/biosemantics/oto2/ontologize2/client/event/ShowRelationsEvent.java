package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.ShowRelationsEvent.Handler;

public class ShowRelationsEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onShow(ShowRelationsEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type type;

    public ShowRelationsEvent(edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type type) {
    	this.type = type;
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onShow(this);
	}

	public edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type getType() {
		return type;
	}
	
}
