package edu.arizona.biosemantics.oto2.steps.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.steps.client.event.SubmitSynonymEvent.Handler;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmission;

public class SubmitSynonymEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onSubmission(SubmitSynonymEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private OntologySynonymSubmission synonymSubmission;

    public SubmitSynonymEvent(OntologySynonymSubmission synonymSubmission) {
    	this.synonymSubmission = synonymSubmission;
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onSubmission(this);
	}

	public OntologySynonymSubmission getSynonymSubmission() {
		return synonymSubmission;
	}
}
