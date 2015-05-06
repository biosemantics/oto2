package edu.arizona.biosemantics.oto2.steps.client.event;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.steps.client.event.SelectPartOfEvent.Handler;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;

public class SelectPartOfEvent extends GwtEvent<Handler> {
	
	public interface Handler extends EventHandler {
		void onSelect(SelectPartOfEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private OntologyClassSubmission submission;
    

	public SelectPartOfEvent(OntologyClassSubmission submission) {
		this.submission = submission;
	}
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onSelect(this);
	}

	public OntologyClassSubmission getSubmission() {
		return submission;
	}

}
