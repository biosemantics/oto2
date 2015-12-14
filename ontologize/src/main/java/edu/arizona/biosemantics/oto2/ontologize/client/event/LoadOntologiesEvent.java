package edu.arizona.biosemantics.oto2.ontologize.client.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologiesEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;

public class LoadOntologiesEvent extends GwtEvent<Handler> {

	public interface Handler extends EventHandler {
		void onLoad(LoadOntologiesEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private List<Ontology> ontologies;

    public LoadOntologiesEvent(List<Ontology> ontologies) {
    	this.ontologies = ontologies;
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onLoad(this);
	}

	public List<Ontology> getOntologies() {
		return ontologies;
	}

}
