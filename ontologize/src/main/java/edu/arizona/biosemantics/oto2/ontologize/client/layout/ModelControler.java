package edu.arizona.biosemantics.oto2.ontologize.client.layout;

import com.google.web.bindery.event.shared.EventBus;

import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologySynonymSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;

public class ModelControler {
	
	private Collection collection;
	private EventBus eventBus;
	
	public ModelControler(Collection collection, EventBus eventBus) {
		super();
		this.collection = collection;
		this.eventBus = eventBus;
		
		bindEvents();
	}

	public ModelControler(com.google.web.bindery.event.shared.EventBus eventBus2) {
		this.eventBus = eventBus2;
		
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
				for(OntologyClassSubmission submission : event.getClassSubmissions())
					if(submission.hasTerm())
						collection.addUsedTerm(submission.getTerm(), submission);
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
