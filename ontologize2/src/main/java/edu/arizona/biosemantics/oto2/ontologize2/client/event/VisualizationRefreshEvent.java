package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.VisualizationRefreshEvent.Handler;

public class VisualizationRefreshEvent extends GwtEvent<Handler> {
	
	public interface Handler extends EventHandler {
		void onRefresh(VisualizationRefreshEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
    
    public VisualizationRefreshEvent() {
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(Handler handler) {
		handler.onRefresh(this);
	}

}