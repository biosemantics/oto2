package edu.arizona.biosemantics.oto.oto.client.categorize;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;

import edu.arizona.biosemantics.oto.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;

public class CategorizeView extends BorderLayoutContainer implements IsWidget {

	private int portalColumnCount = 6;
	private EventBus eventBus = new SimpleEventBus();
	private TermsView termsView = new TermsView(eventBus);
	private LabelsView categoriesView = new LabelsView(eventBus, portalColumnCount);
	private TermInfoView termInfoView = new TermInfoView(eventBus);
	private Collection collection;

	public CategorizeView() {
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
		cp.add(categoriesView);
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
	
	public void setBuckets(List<Bucket> buckets) {
		termsView.setBuckets(buckets);
	}

	public void setLabels(List<Label> labels) {
		categoriesView.setLabels(labels);
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
		this.setBuckets(collection.getBuckets());
		this.setLabels(collection.getLabels());
	}

}
