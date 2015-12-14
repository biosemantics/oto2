package edu.arizona.biosemantics.oto2.ontologize.client.content.submissions;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabPanel;

import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.SubmissionType;

public class SubmissionsView implements IsWidget {

	private EventBus eventBus;
	private TabPanel tabPanel;
	private ClassSubmissionsGrid classSubmissionsGrid;
	private SynonymSubmissionsGrid synonymSubmissionGrid;

	public SubmissionsView(EventBus eventBus, SubmissionType submissionType) {
		this.eventBus = eventBus;
		//tabPanel = new TabPanel(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
		tabPanel = new TabPanel();

		classSubmissionsGrid = new ClassSubmissionsGrid(eventBus, submissionType);
		synonymSubmissionGrid = new SynonymSubmissionsGrid(eventBus, submissionType);
		
		tabPanel.add(classSubmissionsGrid, "Class");
		tabPanel.add(synonymSubmissionGrid, "Synonym");
		
		//http://stackoverflow.com/questions/24915856/gxt-3-1-0-sorting-dropdown-missing-from-grid-header
		tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {
			@Override
			public void onSelection(SelectionEvent<Widget> event) {
				if(event.getSelectedItem().equals(classSubmissionsGrid.asWidget())) {
					classSubmissionsGrid.refreshHeader();
				} else if(event.getSelectedItem().equals(synonymSubmissionGrid.asWidget())) {
					synonymSubmissionGrid.refreshHeader();
				}
			}
		});
		bindEvents();
	}

	private void bindEvents() {
	}

	@Override
	public Widget asWidget() {
		return tabPanel;
	}

	//http://stackoverflow.com/questions/24915856/gxt-3-1-0-sorting-dropdown-missing-from-grid-header
	public void refreshGridHeaders() {
		classSubmissionsGrid.refreshHeader();
		synonymSubmissionGrid.refreshHeader();
	}
}
