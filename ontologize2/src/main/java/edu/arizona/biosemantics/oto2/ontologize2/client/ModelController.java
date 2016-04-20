package edu.arizona.biosemantics.oto2.ontologize2.client;

import com.google.web.bindery.event.shared.EventBus;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;

public class ModelController {

	private static Collection collection;
	private EventBus eventBus;
	
	public ModelController(EventBus eventBus) {
		this.eventBus = eventBus;
		bindEvents();
	}

	public static Collection getCollection() {
		return collection;
	}
	
	private void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				collection = event.getCollection();
			}
		});
	}

}
