package edu.arizona.biosemantics.oto2.ontologize.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

import edu.arizona.biosemantics.oto2.ontologize.client.layout.OntologizePresenter;
import edu.arizona.biosemantics.oto2.ontologize.client.layout.OntologizeView;

public class Ontologize {

	public static String user = "";
	
	private SimpleEventBus eventBus;
	private OntologizeView view;
	private OntologizePresenter presenter;

	public Ontologize() {
		this.eventBus = new SimpleEventBus();
		this.presenter = new OntologizePresenter(eventBus);
		this.view = new OntologizeView(eventBus);
		presenter.setView(view);
	}
	
	public OntologizeView getView() {
		return view;
	}
	
	public void loadCollection(int collectionId, String secret) {
		presenter.loadCollection(collectionId, secret);
	}
	
	public EventBus getEventBus() {
		return eventBus;
	}
	
	public void setUser(String user) {
		Ontologize.user = user;
		//eventBus.fireEvent(new SetUserEvent(user));
	}
	
}
