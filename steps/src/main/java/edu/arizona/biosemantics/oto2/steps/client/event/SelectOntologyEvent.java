package edu.arizona.biosemantics.oto2.steps.client.event;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.steps.client.event.SelectOntologyEvent.Handler;
import edu.arizona.biosemantics.oto2.steps.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.steps.shared.model.Term;

public class SelectOntologyEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onSelect(SelectOntologyEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
    
    private Ontology ontology;

    public SelectOntologyEvent(Ontology ontology) {
        this.ontology = ontology;
    }
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onSelect(this);
	}

	public Ontology getOntology() {
		return ontology;
	}

}
