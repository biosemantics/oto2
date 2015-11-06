package edu.arizona.biosemantics.oto2.ontologize.client.toontology;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.data.shared.ListStore;

import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;

public class LocalSynonymSubmissionGrid extends SynonymSubmissionsGrid {

	public LocalSynonymSubmissionGrid(EventBus eventBus,
			ListStore<OntologySynonymSubmission> synonymSubmissionStore) {
		super(eventBus, synonymSubmissionStore);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void setHiddenColumns() {
		termCol.setHidden(true);
		categoryCol.setHidden(true);
		statusCol.setHidden(true);
		iriCol.setHidden(true);
		sampleCol.setHidden(true);
		sourceCol.setHidden(true);
		synonymsCol.setHidden(true);
		//userCol.setHidden(true);
	}
}
