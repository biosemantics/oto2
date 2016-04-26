package edu.arizona.biosemantics.oto2.ontologize2.client;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.google.gwt.event.shared.EventBus;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class StandAlone implements EntryPoint {

	@Override
	public void onModuleLoad() {
		Ontologize ontologize = new Ontologize();
		Viewport v = new Viewport();
		v.add(ontologize);
		RootPanel.get().add(v);
		
		EventBus eventBus = ontologize.getEventBus();
		List<Term> terms = new LinkedList<Term>();
		terms.add(new Term("stem", "iri", "/bucket"));
		terms.add(new Term("leaf", "iri", "/bucket"));
		terms.add(new Term("fruit", "iri", "/bucket"));
		terms.add(new Term("leaflet", "iri", "/bucket"));
		terms.add(new Term("trunk", "iri", "/bucket"));
		terms.add(new Term("apex", "iri", "/bucket"));
		terms.add(new Term("node", "iri", "/bucket"));
		terms.add(new Term("bark", "iri", "/bucket"));
		terms.add(new Term("petal", "iri", "/bucket"));
		terms.add(new Term("stamen", "iri", "/bucket"));
		Collection collection = new Collection("name", TaxonGroup.PLANT, "secret", terms);
		eventBus.fireEvent(new LoadCollectionEvent(collection));
		
	}

}
