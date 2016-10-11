package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;

/**
 * Removes edge + destination node
 * if recursive: remove everything below destination node too
 * else: remove only edge + destination node. Add edges from source to all of destinations child nodes.
 *  * @author rodenhausen
 */
public class RemoveRelationEvent extends GwtEvent<Handler> implements Serializable, HasIsRemote {

	public interface Handler extends EventHandler {
		void onRemove(RemoveRelationEvent event);
	}
	public enum RemoveMode { RECURSIVE, REATTACH_TO_AVOID_LOSS, NONE }
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private Edge[] relations = new Edge[] { };
	private boolean isEffectiveInModel = false;
	private boolean isRemote = true;
	private RemoveMode removeMode;
    
	public RemoveRelationEvent() { }
	
    public RemoveRelationEvent(RemoveMode removeMode, Edge... relations) { 
    	this.relations = relations;
    	this.removeMode = removeMode;
    }
    
	public RemoveRelationEvent(RemoveMode removeMode, List<Edge> relations) {
		this.relations = relations.toArray(this.relations);
		this.removeMode = removeMode;
	}

	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onRemove(this);
	}

	public Edge[] getRelations() {
		return relations;
	}

	public RemoveMode getRemoveMode() {
		return removeMode;
	}

	public boolean isEffectiveInModel() {
		return isEffectiveInModel;
	}

	public void setEffectiveInModel(boolean isEffectiveInModel) {
		this.isEffectiveInModel = isEffectiveInModel;
	}

	@Override
	public void setIsRemote(boolean isRemote) {
		this.isRemote = isRemote;
	}

	@Override
	public boolean isRemote() {
		return isRemote;
	}

	/*public boolean isRecursive() {
		return removeMode.equals(RemoveMode.RECURSIVE);
	}*/	
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for(Edge r : relations)
			b.append("remove " + r.toString());
		return b.toString();
	}
}
