package edu.arizona.biosemantics.oto.oto.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.container.Viewport;

import edu.arizona.biosemantics.oto.oto.client.categorize.CategorizePresenter;
import edu.arizona.biosemantics.oto.oto.client.categorize.CategorizeView;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Oto implements EntryPoint {
		
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		EventBus eventBus = new SimpleEventBus();
		CategorizeView view = new CategorizeView(eventBus);
		CategorizePresenter presenter = new CategorizePresenter(eventBus, view);
		
		Viewport v = new Viewport();
		v.add(view.asWidget());
		RootPanel.get().add(v);
	}
}
