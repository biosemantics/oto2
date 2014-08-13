package edu.arizona.biosemantics.oto2.oto.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.container.Viewport;

import edu.arizona.biosemantics.oto2.oto.client.categorize.CategorizePresenter;
import edu.arizona.biosemantics.oto2.oto.client.categorize.CategorizeView;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Oto implements EntryPoint {
		
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
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		int collectionId = 3;
		String secret = "my secret";
		
		Oto oto = new Oto(collectionId, secret);
		Viewport v = new Viewport();
		v.add(oto.getView().asWidget());
		RootPanel.get().add(v);
	}
}
