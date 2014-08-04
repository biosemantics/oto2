package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelRenameEvent.RenameLabelHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;

public class LabelRenameEvent extends GwtEvent<RenameLabelHandler> {

	public interface RenameLabelHandler extends EventHandler {
		void onRename(Label label);
	}
	
    public static Type<RenameLabelHandler> TYPE = new Type<RenameLabelHandler>();
    
    private Label label;
    
    public LabelRenameEvent(Label label) {
        this.label = label;
    }
	
	@Override
	public Type<RenameLabelHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RenameLabelHandler handler) {
		handler.onRename(label);
	}

}
