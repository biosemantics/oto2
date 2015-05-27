package edu.arizona.biosemantics.oto2.ontologize.client.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize.client.event.RefreshOntologySynonymSubmissionsEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;

public class RefreshOntologySynonymSubmissionsEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onSelect(RefreshOntologySynonymSubmissionsEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private List<OntologySynonymSubmission> ontologySynonymSubmissions;
    
    public RefreshOntologySynonymSubmissionsEvent(List<OntologySynonymSubmission> ontologySynonymSubmissions) {
		this.ontologySynonymSubmissions = ontologySynonymSubmissions;
    }
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onSelect(this);
	}

	public List<OntologySynonymSubmission> getOntologySynonymSubmissions() {
		return ontologySynonymSubmissions;
	}
	
	

}
