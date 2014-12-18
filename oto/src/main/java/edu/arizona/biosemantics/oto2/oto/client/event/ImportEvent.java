package edu.arizona.biosemantics.oto2.oto.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.ImportEvent.ImportHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;

public class ImportEvent extends GwtEvent<ImportHandler> {

	public interface ImportHandler extends EventHandler {
		void onImport(ImportEvent event);
	}
	
    public static Type<ImportHandler> TYPE = new Type<ImportHandler>();
	private Collection collection;

    public ImportEvent(Collection collection) {
    	this.collection = collection;
    }
    
	@Override
	public Type<ImportHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ImportHandler handler) {
		handler.onImport(this);
	}

	public Collection getCollection() {
		return collection;
	}
	
}
