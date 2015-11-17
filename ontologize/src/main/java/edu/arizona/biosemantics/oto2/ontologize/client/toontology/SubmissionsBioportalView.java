package edu.arizona.biosemantics.oto2.ontologize.client.toontology;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.TabPanel;

import edu.arizona.biosemantics.oto2.ontologize.client.content.submissions.BioportalClassSubmissionsGrid;
import edu.arizona.biosemantics.oto2.ontologize.client.content.submissions.BioportalSynonymSubmissionGrid;
import edu.arizona.biosemantics.oto2.ontologize.client.content.submissions.ClassSubmissionsGrid;
import edu.arizona.biosemantics.oto2.ontologize.client.content.submissions.SynonymSubmissionsGrid;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RefreshOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RefreshOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmissionProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmissionProperties;

public class SubmissionsBioportalView implements IsWidget {

	private OntologyClassSubmissionProperties ontologyClassSubmissionProperties = GWT.create(OntologyClassSubmissionProperties.class);
	private OntologySynonymSubmissionProperties ontologySynonymSubmissionProperties = GWT.create(OntologySynonymSubmissionProperties.class);
	private EventBus eventBus;
	private TabPanel tabPanel;
	private ListStore<OntologyClassSubmission> classSubmissionStore =
			new ListStore<OntologyClassSubmission>(ontologyClassSubmissionProperties.key());
	private ListStore<OntologySynonymSubmission> synonymSubmissionStore =
			new ListStore<OntologySynonymSubmission>(ontologySynonymSubmissionProperties.key());

	public SubmissionsBioportalView(EventBus eventBus) {
		this.eventBus = eventBus;
		//tabPanel = new TabPanel(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
		tabPanel = new TabPanel();
		
		tabPanel.add(createOntologyClassSubmissionGrid(), "Class");
		tabPanel.add(createOntologySynonymSubmissionGrid(), "Synonym");
		
		bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(RefreshOntologySynonymSubmissionsEvent.TYPE, new RefreshOntologySynonymSubmissionsEvent.Handler() {
			@Override
			public void onSelect(RefreshOntologySynonymSubmissionsEvent event) {
				synonymSubmissionStore.clear();
				for(OntologySynonymSubmission submission : event.getOntologySynonymSubmissions()) {
					if(submission.getOntology().isBioportalOntology())
						synonymSubmissionStore.add(submission);
				}
			}
		});
		eventBus.addHandler(RefreshOntologyClassSubmissionsEvent.TYPE, new RefreshOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onSelect(RefreshOntologyClassSubmissionsEvent event) {
				classSubmissionStore.clear();
				for(OntologyClassSubmission submission : event.getOntologyClassSubmissions()) {
					if(submission.getOntology().isBioportalOntology())
						classSubmissionStore.add(submission);
				}
			}
		});
	}
	
	private SynonymSubmissionsGrid createOntologySynonymSubmissionGrid() {
		return new BioportalSynonymSubmissionGrid(eventBus, synonymSubmissionStore);
	}

	private ClassSubmissionsGrid createOntologyClassSubmissionGrid() {
		return new BioportalClassSubmissionsGrid(eventBus, classSubmissionStore);
	}

	@Override
	public Widget asWidget() {
		return tabPanel;
	}

}
