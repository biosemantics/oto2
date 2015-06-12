package edu.arizona.biosemantics.oto2.ontologize.client.toontology.submit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.TabPanel.TabPanelAppearance;
import com.sencha.gxt.widget.core.client.TabPanel.TabPanelBottomAppearance;

import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectPartOfEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSuperclassEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSynonymEvent;

public class CreateSubmissionView implements IsWidget {

	private SubmitLocalView submitLocalView;
	private SubmitBioportalView submitBioportalView;
	private TabPanel tabPanel;
	private EventBus eventBus;

	public CreateSubmissionView(EventBus eventBus) {
		this.eventBus = eventBus;
		//tabPanel = new TabPanel(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
		tabPanel = new TabPanel();
		
		submitLocalView = new SubmitLocalView(eventBus);
		submitBioportalView = new SubmitBioportalView(eventBus);
		
		tabPanel.add(submitLocalView, "Local");
		tabPanel.add(submitBioportalView, "Bioportal");
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(SelectSynonymEvent.TYPE, new SelectSynonymEvent.Handler() {
			@Override
			public void onSelect(SelectSynonymEvent event) {
				if(event.getSubmission().getOntology().isBioportalOntology()) {
					tabPanel.setActiveWidget(submitBioportalView);
				} else {
					tabPanel.setActiveWidget(submitLocalView);
				}
			}
		});
		eventBus.addHandler(SelectPartOfEvent.TYPE, new SelectPartOfEvent.Handler() {
			@Override
			public void onSelect(SelectPartOfEvent event) {
				tabPanel.setActiveWidget(submitLocalView);
			}
		});
		eventBus.addHandler(SelectSuperclassEvent.TYPE, new SelectSuperclassEvent.Handler() {
			@Override
			public void onSelect(SelectSuperclassEvent event) {
				if(event.getSubmission().getOntology().isBioportalOntology()) {
					tabPanel.setActiveWidget(submitBioportalView);
				} else {
					tabPanel.setActiveWidget(submitLocalView);
				}
			}
		});
	}

	@Override
	public Widget asWidget() {
		return tabPanel;
	}

}
