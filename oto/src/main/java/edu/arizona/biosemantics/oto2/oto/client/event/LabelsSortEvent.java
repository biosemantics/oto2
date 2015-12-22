package edu.arizona.biosemantics.oto2.oto.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.LabelsSortEvent.SortLabelsHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;

public class LabelsSortEvent extends GwtEvent<SortLabelsHandler> {

	public interface SortLabelsHandler extends EventHandler {
		void onSort(LabelsSortEvent event);
	}
	
    public static Type<SortLabelsHandler> TYPE = new Type<SortLabelsHandler>();
    
    private Collection collection;
    
    public LabelsSortEvent(Collection collection) {
        this.collection = collection;
    }
	
	@Override
	public Type<SortLabelsHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SortLabelsHandler handler) {
		handler.onSort(this);
	}
	
	public Collection getCollection(){
		return collection;
	}

}

