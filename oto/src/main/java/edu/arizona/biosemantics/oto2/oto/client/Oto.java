package edu.arizona.biosemantics.oto2.oto.client;

import com.google.gwt.event.shared.SimpleEventBus;

import edu.arizona.biosemantics.oto2.oto.client.categorize.OtoPresenter;
import edu.arizona.biosemantics.oto2.oto.client.categorize.OtoView;

public class Oto {
		
	private SimpleEventBus eventBus;
	private OtoView view;
	private OtoPresenter presenter;

	public Oto(int collectionId, String secret) {
		this.eventBus = new SimpleEventBus();
		this.view = new OtoView(eventBus);
		this.presenter = new OtoPresenter(eventBus, view);
		presenter.loadCollection(collectionId, secret);
	}
	
	public OtoView getView() {
		return view;
	}
	

}
