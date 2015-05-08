package edu.arizona.biosemantics.oto2.steps.client.toontology.submit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.TabPanel.TabPanelAppearance;
import com.sencha.gxt.widget.core.client.TabPanel.TabPanelBottomAppearance;

import edu.arizona.biosemantics.oto2.steps.client.event.SelectPartOfEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.SelectSuperclassEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.SelectSynonymEvent;

public class SubmitBioportalView implements IsWidget {

	private EventBus eventBus;
	private TabPanel tabPanel;
	private SubmitBioportalClassView submitBioportalClassView;
	private SubmitBioportalSynonymView submitBioportalSynonymView;

	public SubmitBioportalView(EventBus eventBus) {
		this.eventBus = eventBus;
		tabPanel = new TabPanel(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
		//tabPanel = new TabPanel();
		
		submitBioportalClassView = new SubmitBioportalClassView(eventBus);
		submitBioportalSynonymView = new SubmitBioportalSynonymView(eventBus);
		
		tabPanel.add(submitBioportalClassView, "Class");
		tabPanel.add(submitBioportalSynonymView, "Synonym");
		
		bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(SelectSynonymEvent.TYPE, new SelectSynonymEvent.Handler() {
			@Override
			public void onSelect(SelectSynonymEvent event) {
				tabPanel.setActiveWidget(submitBioportalSynonymView);
			}
		});
		eventBus.addHandler(SelectPartOfEvent.TYPE, new SelectPartOfEvent.Handler() {
			@Override
			public void onSelect(SelectPartOfEvent event) {
				tabPanel.setActiveWidget(submitBioportalClassView);
			}
		});
		eventBus.addHandler(SelectSuperclassEvent.TYPE, new SelectSuperclassEvent.Handler() {
			@Override
			public void onSelect(SelectSuperclassEvent event) {
				tabPanel.setActiveWidget(submitBioportalClassView);
			}
		});
	}

	@Override
	public Widget asWidget() {
		return tabPanel;
	}

}
