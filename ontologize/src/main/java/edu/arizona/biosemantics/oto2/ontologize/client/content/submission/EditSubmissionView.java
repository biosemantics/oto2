package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.info.Info;

import edu.arizona.biosemantics.oto2.ontologize.client.Ontologize;
import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologySynonymSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.OntologyClassSubmissionSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.OntologySynonymSubmissionSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologySynonymsSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.layout.ModelController;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.HasLabelAndIri;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public class EditSubmissionView implements IsWidget {

	private IToOntologyServiceAsync toOntologyService = GWT
			.create(IToOntologyService.class);
	private EventBus eventBus;
	private FramedPanel container;
	private SelectTermView selectTermView;
	private CardLayoutContainer cardLayout;
	private TextButton termCardButton;
	private TextButton relationCardButton;
	private TextButton metaCardButton;
	private TextButton saveButton = new TextButton("Save");
	private SelectRelationsView selectRelationsView;
	private SelectMetadataView selectMetadataView;
	private OntologySynonymSubmission selectedSynonymSubmission;
	private OntologyClassSubmission selectedClassSubmission;

	public EditSubmissionView(EventBus eventBus, boolean showPartOfRelations, boolean showClassIRI, boolean showDefaultSuperclasses, 
			boolean showOntology, boolean enableOntology, boolean showIRITextFieldForSynonym) {
		this.eventBus = eventBus;

		termCardButton = new TextButton("Term");
		relationCardButton = new TextButton("Relations");
		metaCardButton = new TextButton("Meta-data");

		selectTermView = new SelectTermView(eventBus, false, false, false, showClassIRI, showOntology, enableOntology, showIRITextFieldForSynonym, showDefaultSuperclasses);
		selectRelationsView = new SelectRelationsView(eventBus, false, showPartOfRelations, showDefaultSuperclasses);
		selectMetadataView = new SelectMetadataView(eventBus, false);

		cardLayout = new CardLayoutContainer();
		cardLayout.add(selectTermView);
		cardLayout.add(selectRelationsView);
		cardLayout.add(selectMetadataView);
		cardLayout.setActiveWidget(selectTermView);

		container = new FramedPanel();
		container.setHeadingText("Edit Submission: ");
		container.add(cardLayout);
		// container.addButton(new LabelToolItem("Switch Cards"));
		container.addButton(termCardButton);
		container.addButton(relationCardButton);
		container.addButton(metaCardButton);
		container.addButton(saveButton);

		bindEvents();
	}

	private void bindEvents() {		
		selectTermView.addSelectTypeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				selectRelationsView.setTypeSelected(selectTermView.getType());
			}
		});
		eventBus.addHandler(OntologyClassSubmissionSelectEvent.TYPE, new OntologyClassSubmissionSelectEvent.Handler() {
			@Override
			public void onSelect(OntologyClassSubmissionSelectEvent event) {
				setOntologyClassSubmission(event.getOntologyClassSubmission());
				cardLayout.setActiveWidget(selectTermView);
			}
		});
		eventBus.addHandler(OntologySynonymSubmissionSelectEvent.TYPE, new OntologySynonymSubmissionSelectEvent.Handler() {
			@Override
			public void onSelect(OntologySynonymSubmissionSelectEvent event) {
				setOntologySynonymSubmission(event.getOntologySynonymSubmission());
				cardLayout.setActiveWidget(selectTermView);
			}
		});
		termCardButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				cardLayout.setActiveWidget(selectTermView);
			}
		});

		relationCardButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				cardLayout.setActiveWidget(selectRelationsView);
			}
		});

		metaCardButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				cardLayout.setActiveWidget(selectMetadataView);
			}
		});
		saveButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				boolean valid = validateForm();
				if (valid) {
					final MessageBox box = Alerter.startLoading();
					editSubmission(box);
				} else {
					Alerter.alertInvalidForm();
				}
			}
		});
	}

	protected void setOntologySynonymSubmission(OntologySynonymSubmission submission) {
		container.setHeadingText("Edit Submission: " + submission.getSubmissionTerm());
		selectedSynonymSubmission = submission;
		selectTermView.setEnabledTermComboBox(false);
		selectTermView.setEnabledSubmissionTypeField(false);
		selectTermView.setOntologySynonymSubmission(submission);
		selectRelationsView.setOntologySynonymSubmission(submission);
		selectMetadataView.setOntologySynonymSubmission(submission);
	}

	protected void setOntologyClassSubmission(OntologyClassSubmission submission) {
		container.setHeadingText("Edit Submission: " + submission.getSubmissionTerm());
		selectedClassSubmission = submission;
		selectTermView.setEnabledTermComboBox(false);
		selectTermView.setEnabledSubmissionTypeField(false);
		selectTermView.setOntologyClassSubmission(submission);
		selectRelationsView.setOntologyClassSubmission(submission);
		selectMetadataView.setOntologyClassSubmission(submission);
	}

	protected void editSubmission(final MessageBox box) {
		if (selectTermView.isClass()) {
			final OntologyClassSubmission submission = getClassSubmission();
			submission.setId(selectedClassSubmission.getId());
			toOntologyService.updateClassSubmission(ModelController.getCollection(), submission,
					new AsyncCallback<List<OntologyClassSubmission>>() {
						@Override
						public void onFailure(Throwable caught) {
							Alerter.stopLoading(box);
							Alerter.failedToUpdateSubmission(caught);
						}
						@Override
						public void onSuccess(List<OntologyClassSubmission> result) {
							Alerter.stopLoading(box);
							eventBus.fireEvent(new CreateOntologyClassSubmissionEvent(result));
							eventBus.fireEvent(new UpdateOntologyClassSubmissionsEvent(submission));
							String resultText = "";
							for(OntologyClassSubmission submission : result) {
								resultText += submission.getSubmissionTerm() + "<br>";
							}
							if(!result.isEmpty())
								Info.display(SafeHtmlUtils.fromSafeConstant("Class created"), SafeHtmlUtils.fromSafeConstant(resultText));
							Info.display(SafeHtmlUtils.fromSafeConstant("Class updated"), SafeHtmlUtils.fromSafeConstant(submission.getSubmissionTerm()));
						}
					});
		} else if (selectTermView.isSynonym()) {
			final OntologySynonymSubmission submission = getSynonymSubmission();
			submission.setId(selectedSynonymSubmission.getId());
			toOntologyService.updateSynonymSubmission(ModelController.getCollection(), submission,
					new AsyncCallback<OntologySynonymSubmission>() {
						@Override
						public void onFailure(Throwable caught) {
							Alerter.stopLoading(box);
							Alerter.failedToUpdateSubmission(caught);
						}
						@Override
						public void onSuccess(OntologySynonymSubmission result) {
							Alerter.stopLoading(box);
							eventBus.fireEvent(new UpdateOntologySynonymsSubmissionsEvent(result));
							Info.display(SafeHtmlUtils.fromSafeConstant("Synonym updated"), SafeHtmlUtils.fromSafeConstant(submission.getSubmissionTerm()));
						}
					});
		}
	}

	private OntologySynonymSubmission getSynonymSubmission() {
		List<Synonym> synonyms = selectRelationsView.getSynonyms();
		return new OntologySynonymSubmission(ModelController.getCollection().getId(),
				selectTermView.getTerm(), selectTermView.getSubmissionTerm(),
				ModelController.getOntologies().get(0), selectTermView.getClassIRI(), "", synonyms,
				selectMetadataView.getSource(), selectMetadataView.getSample(),
				Ontologize.user);
	}

	protected OntologyClassSubmission getClassSubmission() {
		List<PartOf> partOfs = selectRelationsView.getPartOfs();
		List<Superclass> superclasses = selectRelationsView.getSuperclasses();
		List<Synonym> synonyms = selectRelationsView.getSynonyms();
		return new OntologyClassSubmission(ModelController.getCollection().getId(),
				selectTermView.getTerm(), selectTermView.getSubmissionTerm(),
				ModelController.getOntologies().get(0), selectTermView.getClassIRI(), superclasses,
				selectMetadataView.getDefinition(), synonyms,
				selectMetadataView.getSource(), selectMetadataView.getSample(),
				partOfs, Ontologize.user);
	}

	protected boolean validateForm() {
		boolean result = true;
		result &= isCircularFreeSuperclassRelationships();
		result &= isCircularFreePartOfRelationships();
		result &= selectTermView.validate();
		result &= selectRelationsView.validate();
		result &= selectMetadataView.validate();
		return result;
	}

	private boolean isCircularFreePartOfRelationships() {
		Set<String> visited = new HashSet<String>();
		visited.add(this.selectTermView.getSubmissionTerm());
		return isCircularFreePartOfRelationships(visited, selectRelationsView.getPartOfs());
	}

	private boolean isCircularFreePartOfRelationships(Set<String> visited, List<PartOf> partOfs) {		
		boolean result = true;
		for(PartOf partOf : partOfs) {
			Set<String> newVisited = new HashSet<String>(visited);
			if(newVisited.contains(partOf.getLabelAlternativelyIri()))
				return false;
			newVisited.add(partOf.getLabelAlternativelyIri());
			OntologyClassSubmission submission = OntologyClassSubmissionRetriever.getSubmissionOfLabelOrIri(partOf, ModelController.getClassSubmissions().values());
			if(submission != null) 
				result &= isCircularFreePartOfRelationships(newVisited, submission.getPartOfs());
		}
		return result;
	}

	private boolean isCircularFreeSuperclassRelationships() {
		Set<String> visited = new HashSet<String>();
		visited.add(this.selectTermView.getSubmissionTerm());
		return isCircularFreeSuperclassRelationships(visited, selectRelationsView.getSuperclasses());
	}

	private boolean isCircularFreeSuperclassRelationships(Set<String> visited, List<Superclass> superclasses) {
		boolean result = true;
		for(Superclass superclass : superclasses) {
			Set<String> newVisited = new HashSet<String>(visited);
			if(newVisited.contains(superclass.getLabelAlternativelyIri()))
				return false;
			newVisited.add(superclass.getLabelAlternativelyIri());
			OntologyClassSubmission submission = OntologyClassSubmissionRetriever.getSubmissionOfLabelOrIri(superclass, ModelController.getClassSubmissions().values());
			if(submission != null) 
				result &= isCircularFreeSuperclassRelationships(newVisited, submission.getSuperclasses());
		}
		return result;
	}

	@Override
	public Widget asWidget() {
		return container;
	}

}
