package edu.arizona.biosemantics.oto.oto.client.categorize;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.TabPanel;

public class TermInfoView extends TabPanel {
	private LocationsView locationsView;
	private ContextView contextView;
	private OntologiesView ontologiesView;

	public TermInfoView(EventBus eventBus) {
		super(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
		locationsView = new LocationsView(eventBus);
		contextView = new ContextView(eventBus);
		ontologiesView = new OntologiesView(eventBus);
		add(locationsView, "Locations");
		add(contextView, "Context");
		add(ontologiesView, "Ontologies");
	}
}