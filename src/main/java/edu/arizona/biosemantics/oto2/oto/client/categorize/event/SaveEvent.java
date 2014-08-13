package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.SaveEvent.SaveHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;

public class SaveEvent extends GwtEvent<SaveHandler> {

	public interface SaveHandler extends EventHandler {
		void onSave(Collection collection);
	}
	
    public static Type<SaveHandler> TYPE = new Type<SaveHandler>();
	private Collection collection;

    public SaveEvent(Collection collection) {
    	this.collection = collection;
    }
    
	@Override
	public Type<SaveHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SaveHandler handler) {
		handler.onSave(collection);
	}
	
}
