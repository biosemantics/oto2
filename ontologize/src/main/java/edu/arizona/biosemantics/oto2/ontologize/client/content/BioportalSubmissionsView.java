package edu.arizona.biosemantics.oto2.ontologize.client.content;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;

import edu.arizona.biosemantics.oto2.ontologize.client.toontology.SubmissionsView;
import edu.arizona.biosemantics.oto2.ontologize.client.toontology.submit.CreateSubmissionView;

public class BioportalSubmissionsView implements IsWidget {

	private BorderLayoutContainer borderLayoutContainer;
	private SubmissionsView submissionsView;
	private CreateSubmissionView submissionView;

	public BioportalSubmissionsView(EventBus eventBus) {
		submissionsView = new SubmissionsView(eventBus);
		submissionView = new CreateSubmissionView(eventBus);
			
		borderLayoutContainer = new BorderLayoutContainer();
		ContentPanel cp = new ContentPanel();
		cp.setHeadingText("Ontology Submissions");
		cp.add(submissionsView);
		BorderLayoutData d = new BorderLayoutData(0.5);
		d.setMargins(new Margins(0, 1, 1, 1));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		borderLayoutContainer.setWestWidget(cp, d);
		
		d = new BorderLayoutData(0.5);
		d.setMargins(new Margins(0, 1, 1, 1));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		borderLayoutContainer.setCenterWidget(submissionView, d);
	}

	@Override
	public Widget asWidget() {
		return borderLayoutContainer;
	}

}
