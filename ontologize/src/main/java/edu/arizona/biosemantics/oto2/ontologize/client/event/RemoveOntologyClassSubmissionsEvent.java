package edu.arizona.biosemantics.oto2.ontologize.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologyClassSubmissionsEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;

public class RemoveOntologyClassSubmissionsEvent  extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onRemove(RemoveOntologyClassSubmissionsEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private List<OntologyClassSubmission> ontologyClassSubmissions;
	
	public RemoveOntologyClassSubmissionsEvent(List<OntologyClassSubmission> ontologyClassSubmissions) {
		this.ontologyClassSubmissions = ontologyClassSubmissions;
	}

	public RemoveOntologyClassSubmissionsEvent(OntologyClassSubmission selectedSubmission) {
		ontologyClassSubmissions = new LinkedList<OntologyClassSubmission>();
		ontologyClassSubmissions.add(selectedSubmission);
		
	}

	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onRemove(this);
	}

	public List<OntologyClassSubmission> getOntologyClassSubmissions() {
		return ontologyClassSubmissions;
	}

}
