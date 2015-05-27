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

public class SubmitLocalView implements IsWidget {

	private EventBus eventBus;
	private TabPanel tabPanel;
	private SubmitLocalClassView submitLocalClassView;
	private SubmitLocalSynonymView submitLocalSynonymView;

	public SubmitLocalView(EventBus eventBus) {
		this.eventBus = eventBus;
		//tabPanel = new TabPanel(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
		tabPanel = new TabPanel();
		
		submitLocalClassView = new SubmitLocalClassView(eventBus);
		submitLocalSynonymView = new SubmitLocalSynonymView(eventBus);
		
		tabPanel.add(submitLocalClassView, "Class");
		tabPanel.add(submitLocalSynonymView, "Synonym");
		
		bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(SelectSynonymEvent.TYPE, new SelectSynonymEvent.Handler() {
			@Override
			public void onSelect(SelectSynonymEvent event) {
				tabPanel.setActiveWidget(submitLocalSynonymView);
			}
		});
		eventBus.addHandler(SelectPartOfEvent.TYPE, new SelectPartOfEvent.Handler() {
			@Override
			public void onSelect(SelectPartOfEvent event) {
				tabPanel.setActiveWidget(submitLocalClassView);
			}
		});
		eventBus.addHandler(SelectSuperclassEvent.TYPE, new SelectSuperclassEvent.Handler() {
			@Override
			public void onSelect(SelectSuperclassEvent event) {
				tabPanel.setActiveWidget(submitLocalClassView);
			}
		});
	}

	@Override
	public Widget asWidget() {
		return tabPanel;
	}

}
