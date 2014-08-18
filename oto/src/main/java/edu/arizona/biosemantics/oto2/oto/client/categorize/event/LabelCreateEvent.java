package edu.arizona.biosemantics.oto2.oto.client.categorize.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.categorize.event.LabelCreateEvent.CreateLabelHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;

public class LabelCreateEvent extends GwtEvent<CreateLabelHandler> {

	public interface CreateLabelHandler extends EventHandler {
		void onCreate(Label label);
	}
	
    public static Type<CreateLabelHandler> TYPE = new Type<CreateLabelHandler>();
    
    private Label label;
    
    public LabelCreateEvent(Label label) {
        this.label = label;
    }
	
	@Override
	public Type<CreateLabelHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CreateLabelHandler handler) {
		handler.onCreate(label);
	}

}
