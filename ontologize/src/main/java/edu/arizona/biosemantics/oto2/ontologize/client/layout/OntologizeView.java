package edu.arizona.biosemantics.oto2.ontologize.client.layout;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

public class OntologizeView extends SimpleLayoutPanel {
	
	private EventBus eventBus;

	private MenuView menuView;
	private ContentView categorizeView;
	private VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();

	public OntologizeView(EventBus eventBus) {
		this.eventBus = eventBus;
		categorizeView = new ContentView(eventBus);
		menuView = new MenuView(eventBus);

		verticalLayoutContainer.add(menuView,new VerticalLayoutData(1,-1));
		verticalLayoutContainer.add(categorizeView,new VerticalLayoutData(1,1));
		this.setWidget(verticalLayoutContainer);
	}
}

