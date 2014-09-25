package edu.arizona.biosemantics.oto2.oto.client.categorize;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.TabPanel.TabPanelAppearance;
import com.sencha.gxt.widget.core.client.TabPanel.TabPanelBottomAppearance;

import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelPortletsView;
import edu.arizona.biosemantics.oto2.oto.client.categorize.single.SingleLabelView;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;

public class LabelsView extends TabPanel {
	
	private LabelPortletsView labelPortletsView;
	private SingleLabelView singleLabelView;

	public LabelsView(EventBus eventBus, int portalColumnCount) {
		super(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
		labelPortletsView = new LabelPortletsView(eventBus, portalColumnCount);
		singleLabelView = new SingleLabelView(eventBus, portalColumnCount);
		
		this.add(labelPortletsView, "All Labels");
		this.add(singleLabelView, "Single Label");
	}

	public void setCollection(Collection collection) {
		labelPortletsView.setCollection(collection);
		singleLabelView.setCollection(collection);
	}
}
