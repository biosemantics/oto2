package edu.arizona.biosemantics.oto2.oto.client;

import com.google.gwt.event.shared.SimpleEventBus;

import edu.arizona.biosemantics.oto2.oto.client.layout.OtoPresenter;
import edu.arizona.biosemantics.oto2.oto.client.layout.OtoView;

public class Oto {
		
	private SimpleEventBus eventBus;
	private OtoView view;
	private OtoPresenter presenter;

	public Oto() {
		this.eventBus = new SimpleEventBus();
		this.presenter = new OtoPresenter(eventBus);
		this.view = new OtoView(eventBus);
		presenter.setView(view);
	}
	
	public OtoView getView() {
		return view;
	}
	
	public void loadCollection(int collectionId, String secret) {
		presenter.loadCollection(collectionId, secret);
	}

}
