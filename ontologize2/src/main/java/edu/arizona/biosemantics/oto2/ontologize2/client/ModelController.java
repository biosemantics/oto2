package edu.arizona.biosemantics.oto2.ontologize2.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.EventBus;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreatePartEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateSubclassEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateSynonymEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveSubclassEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveSynonymEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceTermInRelationsEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemovePartEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class ModelController {
	
	private EventBus eventBus;
	private Collection collection;

	public ModelController(EventBus eventBus) {
		this.eventBus = eventBus;
		bindEvents();
	}
		
	private void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				collection = event.getCollection();
			}
		});
		eventBus.addHandler(CreateTermEvent.TYPE, new CreateTermEvent.Handler() {
			@Override
			public void onCreate(CreateTermEvent event) {
				collection.createTerm(event.getTerms());
			}
		});
		eventBus.addHandler(RemoveTermEvent.TYPE, new RemoveTermEvent.Handler() {
			@Override
			public void onCreate(RemoveTermEvent event) {
				collection.removeTerms(event.getTerms());
			}
		});
		/*eventBus.addHandler(ReplaceTermInRelationsEvent.TYPE, new ReplaceTermInRelationsEvent.Handler() {
			@Override
			public void onDisambiguate(ReplaceTermInRelationsEvent event) {
				collection.replaceTermInRelations(event.getOldTerm(), event.getNewTerm());
			}
		});*/
		
		eventBus.addHandler(CreatePartEvent.TYPE, new CreatePartEvent.Handler() {
			@Override
			public void onCreate(CreatePartEvent event) {
				collection.createPart(event.getParent(), event.getParts());
			}
		});
		eventBus.addHandler(RemovePartEvent.TYPE, new RemovePartEvent.Handler() {
			@Override
			public void onRemove(RemovePartEvent event) {
				collection.removePart(event.getParent(), event.getParts());
			}
		});
		eventBus.addHandler(CreateSubclassEvent.TYPE, new CreateSubclassEvent.Handler() {
			@Override
			public void onCreate(CreateSubclassEvent event) {
				collection.createSubclass(event.getSuperclass(), event.getSubclasses());
			}
		});
		eventBus.addHandler(RemoveSubclassEvent.TYPE, new RemoveSubclassEvent.Handler() {
			@Override
			public void onRemove(RemoveSubclassEvent event) {
				collection.removeSubclass(event.getSuperclass(), event.getSubclasses());
			}
		});
		eventBus.addHandler(CreateSynonymEvent.TYPE, new CreateSynonymEvent.Handler() {
			@Override
			public void onCreate(CreateSynonymEvent event) {
				collection.createSynonym(event.getPreferredTerm(), event.getSynonyms());
			}
		});
		eventBus.addHandler(RemoveSynonymEvent.TYPE, new RemoveSynonymEvent.Handler() {
			@Override
			public void onRemove(RemoveSynonymEvent event) {
				collection.removeSubclass(event.getPreferredTerm(), event.getSynonyms());
			}
		});

	}

}
