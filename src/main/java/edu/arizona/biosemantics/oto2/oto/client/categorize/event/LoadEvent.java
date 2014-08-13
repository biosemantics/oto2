package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.LoadEvent.LoadHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;

public class LoadEvent extends GwtEvent<LoadHandler> {

	public interface LoadHandler extends EventHandler {
		void onLoad(Collection collection);
	}
	
    public static Type<LoadHandler> TYPE = new Type<LoadHandler>();
	private Collection collection;

    public LoadEvent(Collection collection) {
    	this.collection = collection;
    }
    
	@Override
	public Type<LoadHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadHandler handler) {
		handler.onLoad(collection);
	}
	
}
