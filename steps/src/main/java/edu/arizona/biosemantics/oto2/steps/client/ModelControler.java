package edu.arizona.biosemantics.oto2.steps.client;

import com.google.gwt.event.shared.EventBus;

import edu.arizona.biosemantics.oto2.steps.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.CreateOntologySynonymSubmissionEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.RemoveOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologySynonymSubmission;

public class ModelControler {
	
	private Collection collection;
	private EventBus eventBus;
	
	public ModelControler(Collection collection, EventBus eventBus) {
		super();
		this.collection = collection;
		this.eventBus = eventBus;
		
		bindEvents();
	}

	public ModelControler(EventBus eventBus) {
		this.eventBus = eventBus;
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				collection = event.getCollection();
			}
		});
		eventBus.addHandler(CreateOntologyClassSubmissionEvent.TYPE, new CreateOntologyClassSubmissionEvent.Handler() {
			@Override
			public void onSubmission(CreateOntologyClassSubmissionEvent event) {
				if(event.getClassSubmission().hasTerm())
					collection.addUsedTerm(event.getClassSubmission().getTerm(), event.getClassSubmission());
			}
		});
		eventBus.addHandler(CreateOntologySynonymSubmissionEvent.TYPE, new CreateOntologySynonymSubmissionEvent.Handler() {
			@Override
			public void onSubmission(CreateOntologySynonymSubmissionEvent event) {
				collection.addUsedTerm(event.getSynonymSubmission().getTerm(), event.getSynonymSubmission());
			}
		});
		eventBus.addHandler(RemoveOntologyClassSubmissionsEvent.TYPE, new RemoveOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onRemove(RemoveOntologyClassSubmissionsEvent event) {
				for(OntologyClassSubmission submission : event.getOntologyClassSubmissions()) {
					collection.removeUsedTerm(submission.getTerm(), submission);
				}
			}
		});
		eventBus.addHandler(RemoveOntologySynonymSubmissionsEvent.TYPE, new RemoveOntologySynonymSubmissionsEvent.Handler() {
			@Override
			public void onRemove(RemoveOntologySynonymSubmissionsEvent event) {
				for(OntologySynonymSubmission submission : event.getOntologySynonymSubmissions()) {
					if(submission.hasTerm())
						collection.removeUsedTerm(submission.getTerm(), submission);
				}
			}
		});
	}
	
	
	

}
