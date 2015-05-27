package edu.arizona.biosemantics.oto2.ontologize.client.event;

import java.util.Set;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;

public class CreateOntologyEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onCreate(CreateOntologyEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private Ontology ontology;

    public CreateOntologyEvent(Ontology ontology) {
    	this.ontology = ontology;
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onCreate(this);
	}

	public Ontology getOntology() {
		return ontology;
	}
	
}
