package edu.arizona.biosemantics.oto2.ontologize.client.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.content.submission.EditSubmissionView;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologySynonymSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologiesEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologySynonymsSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public class ModelController {

	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);
	private EventBus eventBus;
	protected static Collection collection;
	protected static List<Ontology> ontologies = new ArrayList<Ontology>();
	protected static List<Ontology> permanentOntologies = new ArrayList<Ontology>();
	protected static List<Ontology> localOntologies = new ArrayList<Ontology>();
	protected static Map<Integer, OntologyClassSubmission> classSubmissions = new HashMap<Integer, OntologyClassSubmission>();
	protected static Map<Integer, OntologySynonymSubmission> synonymSubmissions = new HashMap<Integer, OntologySynonymSubmission>();
	
	public static List<Ontology> getOntologies() {
		return ontologies;
	}
	
	public static List<Ontology> getPermanentOntologies() {
		return permanentOntologies;
	}

	public static List<Ontology> getLocalOntologies() {
		return localOntologies;
	}
	
	public static Map<Integer, OntologyClassSubmission> getClassSubmissions() {
		return classSubmissions;
	}
	
	public static Map<Integer, OntologySynonymSubmission> getSynonymSubmissions() {
		return synonymSubmissions;
	}
	
	public static Collection getCollection() {
		return collection;
	}
	
	public ModelController(EventBus eventBus) {
		this.eventBus = eventBus;
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				collection = event.getCollection();
				
				final MessageBox boxOntologies = Alerter.startLoading();
				toOntologyService.getLocalOntologies(collection, new AsyncCallback<List<Ontology>>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.stopLoading(boxOntologies);
						Alerter.getOntologiesFailed(caught);
					}
					@Override
					public void onSuccess(List<Ontology> result) {
						Alerter.stopLoading(boxOntologies);
						ModelController.ontologies = new ArrayList<Ontology>(result);
						eventBus.fireEvent(new LoadOntologiesEvent(result));
					}
				});
				final MessageBox boxPermanentOntologies = Alerter.startLoading();
				toOntologyService.getPermanentOntologies(collection, new AsyncCallback<List<Ontology>>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.stopLoading(boxPermanentOntologies);
						Alerter.getOntologiesFailed(caught);
					}
					@Override
					public void onSuccess(List<Ontology> result) {
						Alerter.stopLoading(boxPermanentOntologies);
						ModelController.permanentOntologies = new ArrayList<Ontology>(result);
					}
				});
				final MessageBox boxLocalOntologies = Alerter.startLoading();
				toOntologyService.getLocalOntologies(collection, new AsyncCallback<List<Ontology>>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.stopLoading(boxLocalOntologies);
						Alerter.getOntologiesFailed(caught);
					}
					@Override
					public void onSuccess(List<Ontology> result) {
						Alerter.stopLoading(boxLocalOntologies);
						ModelController.localOntologies = new ArrayList<Ontology>(result);
					}
				});
				
				final MessageBox boxClassSubmissions = Alerter.startLoading();
				toOntologyService.getClassSubmissions(collection, new AsyncCallback<List<OntologyClassSubmission>>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.stopLoading(boxClassSubmissions);
						Alerter.failedToRefreshSubmissions();
					}
					@Override
					public void onSuccess(List<OntologyClassSubmission> result) {
						Alerter.stopLoading(boxClassSubmissions);
						ModelController.classSubmissions = new HashMap<Integer, OntologyClassSubmission>();
						addClassSubmissions(result);
						eventBus.fireEvent(new LoadOntologyClassSubmissionsEvent(result));
					}
				});
				final MessageBox boxSynonymSubmissions = Alerter.startLoading();
				toOntologyService.getSynonymSubmissions(collection, new AsyncCallback<List<OntologySynonymSubmission>>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.stopLoading(boxSynonymSubmissions);
						Alerter.failedToRefreshSubmissions();
					}
					@Override
					public void onSuccess(List<OntologySynonymSubmission> result) {
						Alerter.stopLoading(boxSynonymSubmissions);
						synonymSubmissions = new HashMap<Integer, OntologySynonymSubmission>();
						addSynonymSubmissions(result);
						eventBus.fireEvent(new LoadOntologySynonymSubmissionsEvent(result));
					}
				});
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
