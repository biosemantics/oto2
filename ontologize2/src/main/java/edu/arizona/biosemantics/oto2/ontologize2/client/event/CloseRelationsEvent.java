package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import java.io.Serializable;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.CloseRelationsEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

public class CloseRelationsEvent extends GwtEvent<Handler> implements Serializable {

	public interface Handler extends EventHandler {
		void onClose(CloseRelationsEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private Vertex vertex;
	private boolean close;
	private Edge.Type type;
	private boolean isEffectiveInModel = false;
	
	private CloseRelationsEvent() { }

    public CloseRelationsEvent(Vertex vertex, Edge.Type type, boolean close) {
    	this.vertex = vertex;
    	this.type = type;
    	this.close = close;
    }
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onClose(this);
	}

	public Vertex getVertex() {
		return vertex;
	}

	public boolean isClose() {
		return close;
	}

	public Edge.Type getType() {
		return type;
	}

	public boolean isEffectiveInModel() {
		return isEffectiveInModel ;
	}

	public void setEffectiveInModel(boolean isEffectiveInModel) {
		this.isEffectiveInModel = isEffectiveInModel;
	}	
}
