package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;

public class CreateRelationEvent extends GwtEvent<Handler> implements Serializable, HasIsRemoteEvent {

	public interface Handler extends EventHandler {
		void onCreate(CreateRelationEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private Edge[] relations = new Edge[] { };
	private boolean isEffectiveInModel = false;
	private boolean isRemote = true;
	
	private CreateRelationEvent() { }
	
    public CreateRelationEvent(Edge... relations) { 
    	this.relations = relations;
    }
    
	public CreateRelationEvent(List<Edge> relations) {
		this.relations = relations.toArray(this.relations);
	}

	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onCreate(this);
	}

	public Edge[] getRelations() {
		return relations;
	}

	public boolean isEffectiveInModel() {
		return isEffectiveInModel;
	}
	
	public void setIsRemote(boolean isRemote) {
		this.isRemote = isRemote;
	}

	public boolean isRemote() {
		return isRemote;
	}

	public void setEffectiveInModel(boolean isEffectiveInModel) {
		this.isEffectiveInModel = isEffectiveInModel;
	}
}
