package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelsMergeEvent.MergeLabelsHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;

public class LabelsMergeEvent extends GwtEvent<MergeLabelsHandler> {

	public interface MergeLabelsHandler extends EventHandler {
		void onMerge(Label destination, List<Label> sources);
	}
	
    public static Type<MergeLabelsHandler> TYPE = new Type<MergeLabelsHandler>();
    
    private List<Label> sources;
    private Label destination;
    
    public LabelsMergeEvent(Label destination, List<Label> sources) {
        this.sources = sources;
        this.destination = destination;
    }
	
	@Override
	public Type<MergeLabelsHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MergeLabelsHandler handler) {
		handler.onMerge(destination, sources);
	}

}

