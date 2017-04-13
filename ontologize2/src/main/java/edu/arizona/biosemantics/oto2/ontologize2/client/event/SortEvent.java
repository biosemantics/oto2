package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.sencha.gxt.data.shared.SortDir;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.SortEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;

public class SortEvent extends GwtEvent<Handler> {

	public static enum SortTarget {
		//GRID("Grid"), TREE("Tree"), GRID_AND_TREE("Grid + Tree");
		GRID("Table"), TREE("Tree"), GRID_AND_TREE("Table + Tree");
		private String displayName;

		private SortTarget(String displayName) {
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
	}
	
	public static enum SortField {
		name("Name"), creation("Creation Time");
		
		private String displayName;

		private SortField(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}
	}
	
	public interface Handler extends EventHandler {
		void onSort(SortEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();

    private OntologyGraph.Edge.Type[] types;
    private SortField sortField;
    private SortDir sortDir;
    private SortTarget sortTarget;
    
    public SortEvent(SortField sortField, SortDir sortDir, SortTarget sortTarget, OntologyGraph.Edge.Type... types) {
    	this.sortField = sortField;
    	this.sortDir = sortDir;
    	this.sortTarget = sortTarget;
    	this.types = types;
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onSort(this);
	}

	public OntologyGraph.Edge.Type[] getTypes() {
		return types;
	}

	public SortField getSortField() {
		return sortField;
	}
	
	public SortDir getSortDir() {
		return sortDir;
	}

	public SortTarget getSortTarget() {
		return sortTarget;
	}

	public boolean containsType(OntologyGraph.Edge.Type t) {
		for(OntologyGraph.Edge.Type type : types) 
			if(type.equals(t))
				return true;
		return false;
	}
}
