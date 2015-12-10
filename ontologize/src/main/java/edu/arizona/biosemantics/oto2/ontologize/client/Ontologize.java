package edu.arizona.biosemantics.oto2.ontologize.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.SimpleEventBus;

import edu.arizona.biosemantics.oto2.ontologize.client.layout.OntologizePresenter;
import edu.arizona.biosemantics.oto2.ontologize.client.layout.OntologizeView;

public class Ontologize {

	/*public class MyEventBus extends SimpleEventBus {

	    @Override
	    public void fireEvent( GwtEvent<?> event ) {
	    	System.out.println("event fired " + event.getClass().toString());
	        super.fireEvent( event );
	    }
	}*/
	
	public static String user = "";
	
	private SimpleEventBus eventBus;
	private OntologizeView view;
	private OntologizePresenter presenter;

	public Ontologize() {
		//this.eventBus = new MyEventBus();
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
