package edu.arizona.biosemantics.oto2.steps.client.event;

import java.util.Collection;
import java.util.LinkedList;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.steps.client.event.EditClassEvent.Handler;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;

public class EditClassEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onSubmission(EditClassEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private Collection<OntologyClassSubmission> classSubmissions;

    public EditClassEvent(Collection<OntologyClassSubmission> classSubmissions) {
    	this.classSubmissions = classSubmissions;
    }
	
    public EditClassEvent(OntologyClassSubmission classSubmission) {
    	this.classSubmissions = new LinkedList<OntologyClassSubmission>();
    	this.classSubmissions.add(classSubmission);
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onSubmission(this);
	}

	public Collection<OntologyClassSubmission> getClassSubmissions() {
		return classSubmissions;
	}
}
