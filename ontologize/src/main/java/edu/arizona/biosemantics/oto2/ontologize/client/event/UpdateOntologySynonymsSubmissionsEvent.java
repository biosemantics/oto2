package edu.arizona.biosemantics.oto2.ontologize.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologySynonymsSubmissionsEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;

public class UpdateOntologySynonymsSubmissionsEvent  extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onUpdate(UpdateOntologySynonymsSubmissionsEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private List<OntologySynonymSubmission> ontologySynonymSubmissions;
	
	public UpdateOntologySynonymsSubmissionsEvent(List<OntologySynonymSubmission> ontologySynonymSubmissions) {
		this.ontologySynonymSubmissions = ontologySynonymSubmissions;
	}

	public UpdateOntologySynonymsSubmissionsEvent(
			OntologySynonymSubmission submission) {
		ontologySynonymSubmissions = new LinkedList<OntologySynonymSubmission>();
		ontologySynonymSubmissions.add(submission);
	}

	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onUpdate(this);
	}

	public List<OntologySynonymSubmission> getOntologySynonymSubmissions() {
		return ontologySynonymSubmissions;
	}

}
