package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ClearEvent.Handler;

public class ClearEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onClear(ClearEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private boolean isEffectiveInModel = false;

    public ClearEvent() {
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onClear(this);
	}

	public boolean isEffectiveInModel() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setIsEffectiveInModel(boolean value) {
		this.isEffectiveInModel  = value;
	}

}
