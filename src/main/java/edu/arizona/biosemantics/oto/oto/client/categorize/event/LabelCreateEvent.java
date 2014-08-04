package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelCreateEvent.CreateLabelHandler;

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
