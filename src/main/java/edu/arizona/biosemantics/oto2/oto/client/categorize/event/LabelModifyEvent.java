package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelModifyEvent.ModifyLabelHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;

public class LabelModifyEvent extends GwtEvent<ModifyLabelHandler> {

	public interface ModifyLabelHandler extends EventHandler {
		void onModify(Label label);
	}
	
    public static Type<ModifyLabelHandler> TYPE = new Type<ModifyLabelHandler>();
    
    private Label label;
    
    public LabelModifyEvent(Label label) {
        this.label = label;
    }
	
	@Override
	public Type<ModifyLabelHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ModifyLabelHandler handler) {
		handler.onModify(label);
	}

}
