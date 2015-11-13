package edu.arizona.biosemantics.oto2.ontologize.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;

public class CreateOntologyClassSubmissionEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onSubmission(CreateOntologyClassSubmissionEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private List<OntologyClassSubmission> classSubmissions;

    public CreateOntologyClassSubmissionEvent(List<OntologyClassSubmission> classSubmissions) {
    	this.classSubmissions = classSubmissions;
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onSubmission(this);
	}

	public List<OntologyClassSubmission> getClassSubmissions() {
		return classSubmissions;
	}
}
