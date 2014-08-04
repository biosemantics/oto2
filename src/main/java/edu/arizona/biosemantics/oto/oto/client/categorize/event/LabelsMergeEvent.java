package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelsMergeEvent.MergeLabelsHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;

public class LabelsMergeEvent extends GwtEvent<MergeLabelsHandler> {

	public interface MergeLabelsHandler extends EventHandler {
		void onMerge(Label source, Label destination);
	}
	
    public static Type<MergeLabelsHandler> TYPE = new Type<MergeLabelsHandler>();
    
    private Label source;
    private Label destination;
    
    public LabelsMergeEvent(Label source, Label destination) {
        this.source = source;
        this.destination = destination;
    }
	
	@Override
	public Type<MergeLabelsHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MergeLabelsHandler handler) {
		handler.onMerge(source, destination);
	}

}

