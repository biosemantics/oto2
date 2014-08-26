package edu.arizona.biosemantics.oto2.oto.client.categorize.event;

import java.util.List;
import java.util.Set;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.oto.client.categorize.event.OntologiesSelectEvent.OntologiesSelectHandler;

public class OntologiesSelectEvent extends GwtEvent<OntologiesSelectHandler> {

	public interface OntologiesSelectHandler extends EventHandler {
		void onSelect(Set<Ontology> ontologies);
	}
	
    public static Type<OntologiesSelectHandler> TYPE = new Type<OntologiesSelectHandler>();
	private Set<Ontology> ontologies;

    public OntologiesSelectEvent(Set<Ontology> ontologies) {
    	this.ontologies = ontologies;
    }
    
	@Override
	public Type<OntologiesSelectHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OntologiesSelectHandler handler) {
		handler.onSelect(ontologies);
	}

}
