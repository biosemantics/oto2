package edu.arizona.biosemantics.oto2.ontologize.client.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.web.bindery.event.shared.EventBus;

import edu.arizona.biosemantics.oto2.ontologize.client.content.submission.EditSubmissionView;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologySynonymSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologySynonymsSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;

public class ModelControler {
	
	private Collection collection;
	private EventBus eventBus;
	protected Map<Integer, OntologyClassSubmission> classSubmissions = new HashMap<Integer, OntologyClassSubmission>();
	protected Map<Integer, OntologySynonymSubmission> synonymSubmissions = new HashMap<Integer, OntologySynonymSubmission>();
	
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
		
		
		eventBus.addHandler(LoadOntologyClassSubmissionsEvent.TYPE, new LoadOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onSelect(LoadOntologyClassSubmissionsEvent event) {
				classSubmissions = new HashMap<Integer, OntologyClassSubmission>();
				addClassSubmissions(event.getOntologyClassSubmissions());
			}
		});
		eventBus.addHandler(CreateOntologyClassSubmissionEvent.TYPE, new CreateOntologyClassSubmissionEvent.Handler() {
			@Override
			public void onSubmission(CreateOntologyClassSubmissionEvent event) {
				addClassSubmissions(event.getClassSubmissions());
			}
		});
		eventBus.addHandler(RemoveOntologyClassSubmissionsEvent.TYPE, new RemoveOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onRemove(RemoveOntologyClassSubmissionsEvent event) {
				removeClassSubmissions(event.getOntologyClassSubmissions());
			}
		});
		eventBus.addHandler(LoadOntologySynonymSubmissionsEvent.TYPE, new LoadOntologySynonymSubmissionsEvent.Handler() {
			@Override
			public void onSelect(LoadOntologySynonymSubmissionsEvent event) {
				synonymSubmissions = new HashMap<Integer, OntologySynonymSubmission>();
				addSynonymSubmissions(event.getOntologySynonymSubmissions());
			}
		});
		eventBus.addHandler(CreateOntologySynonymSubmissionEvent.TYPE, new CreateOntologySynonymSubmissionEvent.Handler() {
			@Override
			public void onSubmission(CreateOntologySynonymSubmissionEvent event) {
				synonymSubmissions.put(event.getSynonymSubmission().getId(), event.getSynonymSubmission());
			}
		});
		eventBus.addHandler(RemoveOntologySynonymSubmissionsEvent.TYPE, new RemoveOntologySynonymSubmissionsEvent.Handler() {
			@Override
			public void onRemove(RemoveOntologySynonymSubmissionsEvent event) {
				removeSynonymSubmissions(event.getOntologySynonymSubmissions());
			}
		});
		
		eventBus.addHandler(UpdateOntologyClassSubmissionsEvent.TYPE, new UpdateOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onUpdate(UpdateOntologyClassSubmissionsEvent event) {
				for(OntologyClassSubmission submission : event.getOntologyClassSubmissions()) {
					update(classSubmissions.get(submission.getId()), submission);
				}
			}
		});
		eventBus.addHandler(UpdateOntologySynonymsSubmissionsEvent.TYPE, new UpdateOntologySynonymsSubmissionsEvent.Handler() {
			@Override
			public void onUpdate(UpdateOntologySynonymsSubmissionsEvent event) {
				for(OntologySynonymSubmission submission : event.getOntologySynonymSubmissions()) {
					update(synonymSubmissions.get(submission.getId()), submission);
				}
			}
		});
	}

	protected void removeClassSubmissions(List<OntologyClassSubmission> ontologyClassSubmissions) {
		for(OntologyClassSubmission submission : ontologyClassSubmissions) {
			classSubmissions.remove(submission.getId());
		}
	}
	
	protected void removeSynonymSubmissions(List<OntologySynonymSubmission> ontologySynonymSubmissions) {
		for(OntologySynonymSubmission submission : ontologySynonymSubmissions) {
			synonymSubmissions.remove(submission.getId());
		}
	}

	protected void addSynonymSubmissions(List<OntologySynonymSubmission> ontologySynonymSubmissions) {
		for(OntologySynonymSubmission submission : ontologySynonymSubmissions) {
			synonymSubmissions.put(submission.getId(), submission);
		}
	}

	protected void addClassSubmissions(List<OntologyClassSubmission> ontologyClassSubmissions) {
		for(OntologyClassSubmission submission : ontologyClassSubmissions) {
			classSubmissions.put(submission.getId(), submission);
		}
	}

	protected void update(OntologySynonymSubmission target, OntologySynonymSubmission source) {
		target.setUser(source.getUser());
		target.setType(source.getType());
		target.setTerm(source.getTerm());
		target.setSubmissionTerm(source.getSubmissionTerm());
		target.setSubmissionStatuses(source.getSubmissionStatuses());
		target.setSource(source.getSource());
		target.setSampleSentence(source.getSampleSentence());
		target.setOntology(source.getOntology());
		target.setClassIRI(source.getClassIRI());
		target.setSynonyms(source.getSynonyms());
		target.setClassLabel(source.getClassLabel());
	}

	protected void update(OntologyClassSubmission target, OntologyClassSubmission source) {
		target.setUser(source.getUser());
		target.setTerm(source.getTerm());
		target.setSubmissionTerm(source.getSubmissionTerm());
		target.setSubmissionStatuses(source.getSubmissionStatuses());
		target.setSource(source.getSource());
		target.setSampleSentence(source.getSampleSentence());
		target.setOntology(source.getOntology());
		target.setDefinition(source.getDefinition());
		target.setClassIRI(source.getClassIRI());
		target.setSynonyms(source.getSynonyms());
		target.setSuperclasses(source.getSuperclasses());
		target.setPartOfs(source.getPartOfs());
	}	

}
