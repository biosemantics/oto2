package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.OrderEdgesEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

public class OrderEdgesEvent extends GwtEvent<Handler> implements Serializable {

	public interface Handler extends EventHandler {
		void onOrder(OrderEdgesEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private boolean isEffectiveInModel = false;
	
	private Vertex src;
	private List<Edge> edges;
	private Edge.Type type;
    
	public OrderEdgesEvent() { }

	public OrderEdgesEvent(Vertex src, List<Edge> edges, Edge.Type type) {
		this.src = src;
		this.edges = edges;
		this.type = type;
	}

	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onOrder(this);
	}

	public boolean isEffectiveInModel() {
		return isEffectiveInModel;
	}

	public void setEffectiveInModel(boolean isEffectiveInModel) {
		this.isEffectiveInModel = isEffectiveInModel;
	}

	public Vertex getSrc() {
		return src;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public Edge.Type getType() {
		return type;
	}
	
	
	
}
