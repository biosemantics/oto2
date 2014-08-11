package edu.arizona.biosemantics.oto.oto.client.categorize;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;

import edu.arizona.biosemantics.oto.oto.shared.model.Collection;

public class CategorizeView extends BorderLayoutContainer implements IsWidget {
		
	private EventBus eventBus;
	private int portalColumnCount = 6;
	private TermsView termsView;
	private LabelsView labelsView;
	private TermInfoView termInfoView;
	private Collection collection;

	public CategorizeView(EventBus eventBus) {
		this.eventBus = eventBus;
		termsView = new TermsView(eventBus);
		labelsView = new LabelsView(eventBus, portalColumnCount);
		termInfoView = new TermInfoView(eventBus);
		ContentPanel cp = new ContentPanel();
		cp.setHeadingText("West");

		cp.add(termsView);
		BorderLayoutData d = new BorderLayoutData(.20);
		d.setMargins(new Margins(0, 5, 5, 5));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		setWestWidget(cp, d);

		cp = new ContentPanel();
		cp.setHeadingText("Center");
		cp.add(labelsView);
		cp.setContextMenu(labelsView.new LabelsMenu());
		d = new BorderLayoutData();
		d.setMargins(new Margins(0, 5, 5, 0));
		setCenterWidget(cp, d);

		cp = new ContentPanel();
		cp.setHeadingText("South");
		cp.add(termInfoView);
		d = new BorderLayoutData(.20);
		d.setMargins(new Margins(5));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		setSouthWidget(cp, d);
	}

	public void setCollection(Collection collection, boolean refreshUI) {
		this.collection = collection;
		termsView.setCollection(collection, refreshUI);
		labelsView.setCollection(collection, refreshUI);
	}

}
