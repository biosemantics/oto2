package edu.arizona.biosemantics.oto2.ontologize.client.toontology;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.data.shared.ListStore;

import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;

public class LocalClassSubmissionsGrid extends ClassSubmissionsGrid {

	public LocalClassSubmissionsGrid(EventBus eventBus,
			ListStore<OntologyClassSubmission> classSubmissionStore) {
		super(eventBus, classSubmissionStore);
	}

	@Override
	protected void setHiddenColumns() {
		termCol.setHidden(true);
		categoryCol.setHidden(true);
		iriCol.setHidden(true);
		superClassCol.setHidden(true);
		partOfCol.setHidden(true);
		definitionCol.setHidden(true);
		sampleCol.setHidden(true);
		sourceCol.setHidden(true);
		synonymsCol.setHidden(true);
		userCol.setHidden(true);
		statusCol.setHidden(true);
	}

}
