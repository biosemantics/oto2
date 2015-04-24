package edu.arizona.biosemantics.oto2.steps.client.event;

import java.util.Collection;
import java.util.LinkedList;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.steps.client.event.EditSynonymEvent.Handler;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmission;

public class EditSynonymEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onSubmission(EditSynonymEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private Collection<OntologySynonymSubmission> synonymSubmissions;

    public EditSynonymEvent(Collection<OntologySynonymSubmission> synonymSubmissions) {
    	this.synonymSubmissions = synonymSubmissions;
    }
	
    public EditSynonymEvent(OntologySynonymSubmission synonymSubmission) {
    	this.synonymSubmissions = new LinkedList<OntologySynonymSubmission>();
    	this.synonymSubmissions.add(synonymSubmission);
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onSubmission(this);
	}

	public Collection<OntologySynonymSubmission> getSynonymSubmissions() {
		return synonymSubmissions;
	}
}
