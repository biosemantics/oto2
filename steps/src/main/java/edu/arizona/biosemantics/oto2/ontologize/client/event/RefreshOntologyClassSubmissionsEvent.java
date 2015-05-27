package edu.arizona.biosemantics.oto2.ontologize.client.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize.client.event.RefreshOntologyClassSubmissionsEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;

public class RefreshOntologyClassSubmissionsEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onSelect(RefreshOntologyClassSubmissionsEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private List<OntologyClassSubmission> ontologyClassSubmissions;
    
    public RefreshOntologyClassSubmissionsEvent(List<OntologyClassSubmission> ontologyClassSubmissions) {
		this.ontologyClassSubmissions = ontologyClassSubmissions;
    }
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onSelect(this);
	}

	public List<OntologyClassSubmission> getOntologyClassSubmissions() {
		return ontologyClassSubmissions;
	}
	
}
