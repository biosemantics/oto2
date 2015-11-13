package edu.arizona.biosemantics.oto2.ontologize.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologyClassSubmissionsEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;

public class UpdateOntologyClassSubmissionsEvent  extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onUpdate(UpdateOntologyClassSubmissionsEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private List<OntologyClassSubmission> ontologyClassSubmissions;
	
	public UpdateOntologyClassSubmissionsEvent(List<OntologyClassSubmission> ontologyClassSubmissions) {
		this.ontologyClassSubmissions = ontologyClassSubmissions;
	}

	public UpdateOntologyClassSubmissionsEvent(
			OntologyClassSubmission submission) {
		ontologyClassSubmissions = new LinkedList<OntologyClassSubmission>();
		ontologyClassSubmissions.add(submission);
	}

	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onUpdate(this);
	}

	public List<OntologyClassSubmission> getOntologyClassSubmissions() {
		return ontologyClassSubmissions;
	}

}
