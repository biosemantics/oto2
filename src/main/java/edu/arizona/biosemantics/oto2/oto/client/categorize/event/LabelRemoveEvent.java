package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelRemoveEvent.RemoveLabelHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;

public class LabelRemoveEvent extends GwtEvent<RemoveLabelHandler> {

	public interface RemoveLabelHandler extends EventHandler {
		void onRemove(Label label);
	}
	
    public static Type<RemoveLabelHandler> TYPE = new Type<RemoveLabelHandler>();
    
    private Label label;
    
    public LabelRemoveEvent(Label label) {
        this.label = label;
    }
	
	@Override
	public Type<RemoveLabelHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RemoveLabelHandler handler) {
		handler.onRemove(label);
	}

}
