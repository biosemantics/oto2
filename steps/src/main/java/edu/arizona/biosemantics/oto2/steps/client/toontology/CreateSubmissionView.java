package edu.arizona.biosemantics.oto2.steps.client.toontology;

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

public class CreateSubmissionView implements IsWidget {

	private SubmitClassView submitClassView;
	private SubmitSynonymView submitSynonymView;
	private TabPanel tabPanel;
	private EventBus eventBus;

	public CreateSubmissionView(EventBus eventBus) {
		this.eventBus = eventBus;
		//tabPanel = new TabPanel(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
		tabPanel = new TabPanel();
		
		submitClassView = new SubmitClassView(eventBus);
		submitSynonymView = new SubmitSynonymView(eventBus);
		
		tabPanel.add(submitClassView, "Class");
		tabPanel.add(submitSynonymView, "Synonym");
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(SelectSynonymEvent.TYPE, new SelectSynonymEvent.Handler() {
			@Override
			public void onSelect(SelectSynonymEvent event) {
				tabPanel.setActiveWidget(submitSynonymView);
			}
		});
		eventBus.addHandler(SelectPartOfEvent.TYPE, new SelectPartOfEvent.Handler() {
			@Override
			public void onSelect(SelectPartOfEvent event) {
				tabPanel.setActiveWidget(submitClassView);
			}
		});
		eventBus.addHandler(SelectSuperclassEvent.TYPE, new SelectSuperclassEvent.Handler() {
			@Override
			public void onSelect(SelectSuperclassEvent event) {
				tabPanel.setActiveWidget(submitClassView);
			}
		});
	}

	@Override
	public Widget asWidget() {
		return tabPanel;
	}

}
