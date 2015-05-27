package edu.arizona.biosemantics.oto2.ontologize.client.event;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSampleEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;

public class SelectSampleEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onSelect(SelectSampleEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
    
    private String sample;

    public SelectSampleEvent(String sample) {
        this.sample = sample;
    }
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onSelect(this);
	}

	public String getSample() {
		return sample;
	}
	
	

}
