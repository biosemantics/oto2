package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.VisualizationConfigurationEvent.Handler;

public class VisualizationConfigurationEvent extends GwtEvent<Handler> {
	
	public interface Handler extends EventHandler {
		void onConfig(VisualizationConfigurationEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private boolean highlightMultipleIncomingEdges = false;
    
    public VisualizationConfigurationEvent(boolean highlightMultipleIncomingEdges) {
    	this.highlightMultipleIncomingEdges = highlightMultipleIncomingEdges;
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	public boolean isHighlightMultipleIncomingEdges() {
		return highlightMultipleIncomingEdges;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onConfig(this);
	}

}