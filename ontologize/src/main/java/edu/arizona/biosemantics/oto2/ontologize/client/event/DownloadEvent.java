package edu.arizona.biosemantics.oto2.ontologize.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize.client.event.DownloadEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;

public class DownloadEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onDownload(DownloadEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private Collection collection;

    public DownloadEvent(Collection collection) {
    	this.collection = collection;
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onDownload(this);
	}

	public Collection getCollection() {
		return collection;
	}

}
