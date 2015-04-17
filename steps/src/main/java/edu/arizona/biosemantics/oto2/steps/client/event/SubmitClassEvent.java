package edu.arizona.biosemantics.oto2.steps.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.steps.client.event.SubmitClassEvent.Handler;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;

public class SubmitClassEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onSubmission(SubmitClassEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private OntologyClassSubmission classSubmission;

    public SubmitClassEvent(OntologyClassSubmission classSubmission) {
    	this.classSubmission = classSubmission;
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onSubmission(this);
	}

	public OntologyClassSubmission getClassSubmission() {
		return classSubmission;
	}
}
