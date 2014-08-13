package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelsMergeEvent.MergeLabelsHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Label.AddResult;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class LabelsMergeEvent extends GwtEvent<MergeLabelsHandler> {

	public interface MergeLabelsHandler extends EventHandler {
		void onMerge(Label destination, List<Label> sources, Map<Term, AddResult> addResults);
	}
	
    public static Type<MergeLabelsHandler> TYPE = new Type<MergeLabelsHandler>();
    
    private List<Label> sources;
    private Label destination;
	private Map<Term, AddResult> addResults;
    
    public LabelsMergeEvent(Label destination, List<Label> sources, Map<Term, AddResult> addResults) {
        this.sources = sources;
        this.destination = destination;
        this.addResults = addResults;
    }
	
	@Override
	public Type<MergeLabelsHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MergeLabelsHandler handler) {
		handler.onMerge(destination, sources, addResults);
	}

	public List<Label> getSources() {
		return sources;
	}

	public Label getDestination() {
		return destination;
	}

	public Map<Term, AddResult> getAddResults() {
		return addResults;
	}
	
	

}

