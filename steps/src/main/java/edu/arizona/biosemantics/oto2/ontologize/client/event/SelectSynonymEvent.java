package edu.arizona.biosemantics.oto2.ontologize.client.event;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSynonymEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;

public class SelectSynonymEvent extends GwtEvent<Handler> {
	
	public interface Handler extends EventHandler {
		void onSelect(SelectSynonymEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private OntologyClassSubmission submission;
    

	public SelectSynonymEvent(OntologyClassSubmission submission) {
		this.submission = submission;
	}
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onSelect(this);
	}

	public OntologyClassSubmission getSubmission() {
		return submission;
	}

}
