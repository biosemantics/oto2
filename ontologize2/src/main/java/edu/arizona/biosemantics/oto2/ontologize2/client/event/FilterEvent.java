package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;

public class FilterEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onFilter(FilterEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();

    private OntologyGraph.Edge.Type[] types;
    private String filter;
    private boolean tree;
    private boolean grid;
    
    public FilterEvent(String filter, boolean grid, boolean tree, OntologyGraph.Edge.Type... types) {
    	this.filter = filter == null ? "" : filter.trim();
    	this.grid = grid;
    	this.tree = tree;
    	this.types = types;
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onFilter(this);
	}

	public OntologyGraph.Edge.Type[] getTypes() {
		return types;
	}

	public String getFilter() {
		return filter;
	}

	public boolean isTree() {
		return tree;
	}

	public boolean isGrid() {
		return grid;
	}

	public boolean containsType(OntologyGraph.Edge.Type t) {
		for(OntologyGraph.Edge.Type type : OntologyGraph.Edge.Type.values()) 
			if(type.equals(t))
				return true;
		return false;
	}
	
	
}
