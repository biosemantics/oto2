package edu.arizona.biosemantics.oto2.oto.client;

import com.google.gwt.event.shared.SimpleEventBus;

import edu.arizona.biosemantics.oto2.oto.client.categorize.CategorizePresenter;
import edu.arizona.biosemantics.oto2.oto.client.categorize.CategorizeView;

public class Oto {
		
	private SimpleEventBus eventBus;
	private CategorizeView view;
	private CategorizePresenter presenter;

	public Oto(int collectionId, String secret) {
		this.eventBus = new SimpleEventBus();
		this.view = new CategorizeView(eventBus);
		this.presenter = new CategorizePresenter(eventBus, view);
		presenter.loadCollection(collectionId, secret);
	}
	
	public CategorizeView getView() {
		return view;
	}
	

}
