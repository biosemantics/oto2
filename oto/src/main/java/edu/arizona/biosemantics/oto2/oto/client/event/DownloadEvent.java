package edu.arizona.biosemantics.oto2.oto.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.DownloadEvent.DownloadHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;

public class DownloadEvent extends GwtEvent<DownloadHandler> {

	public interface DownloadHandler extends EventHandler {
		void onDownload(DownloadEvent event);
	}
	
    public static Type<DownloadHandler> TYPE = new Type<DownloadHandler>();
	private Collection collection;

    public DownloadEvent(Collection collection) {
    	this.collection = collection;
    }
    
	@Override
	public Type<DownloadHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DownloadHandler handler) {
		handler.onDownload(this);
	}

	public Collection getCollection() {
		return collection;
	}
	
}
