package edu.arizona.biosemantics.oto2.ontologize.client.content.submissions;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabPanel;

import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;

public class SubmissionsView implements IsWidget {


	
	private EventBus eventBus;
	private TabPanel tabPanel;
	private Collection collection;

	public SubmissionsView(EventBus eventBus, ClassSubmissionsGrid.SubmissionsFilter classSubmissionFilter, 
			SynonymSubmissionsGrid.SubmissionsFilter synonymSubmissionFilter) {
		this.eventBus = eventBus;
		//tabPanel = new TabPanel(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
		tabPanel = new TabPanel();

		ClassSubmissionsGrid classSubmissionsGrid = new ClassSubmissionsGrid(eventBus, classSubmissionFilter);
		SynonymSubmissionsGrid localSynonymSubmissionGrid = new SynonymSubmissionsGrid(eventBus, synonymSubmissionFilter);
		
		tabPanel.add(classSubmissionsGrid, "Class");
		tabPanel.add(localSynonymSubmissionGrid, "Synonym");
		
		bindEvents();
	}

	private void bindEvents() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Widget asWidget() {
		return tabPanel;
	}
}
