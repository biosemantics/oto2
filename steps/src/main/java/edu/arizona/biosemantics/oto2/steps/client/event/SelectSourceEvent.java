package edu.arizona.biosemantics.oto2.steps.client.event;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.steps.client.event.SelectSourceEvent.Handler;
import edu.arizona.biosemantics.oto2.steps.shared.model.Term;

public class SelectSourceEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onSelect(SelectSourceEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
    
    private String source;

    public SelectSourceEvent(String source) {
        this.source = source;
    }
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onSelect(this);
	}

	public String getSource() {
		return source;
	}	

}
