package edu.arizona.biosemantics.oto2.steps.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.steps.shared.model.Color;
import edu.arizona.biosemantics.oto2.steps.client.event.SetColorEvent.SetColorEventHandler;

public class SetColorEvent extends GwtEvent<SetColorEventHandler> {

	public interface SetColorEventHandler extends EventHandler {
		void onSet(SetColorEvent event);
	}
	
	public static Type<SetColorEventHandler> TYPE = new Type<SetColorEventHandler>();
	private Object object;
	private Color color;
	
	public SetColorEvent(Object object, Color color) {
		this.object = object;
		this.color = color;
	}
	
	@Override
	public GwtEvent.Type<SetColorEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SetColorEventHandler handler) {
		handler.onSet(this);
	}

	public static Type<SetColorEventHandler> getTYPE() {
		return TYPE;
	}

	public Object getObject() {
		return object;
	}

	public Color getColor() {
		return color;
	}

}
