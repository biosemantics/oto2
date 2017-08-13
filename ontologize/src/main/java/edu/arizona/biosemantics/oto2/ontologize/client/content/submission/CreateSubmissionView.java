package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;

import edu.arizona.biosemantics.oto2.ontologize.client.Ontologize;
import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologySynonymSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadOntologiesEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.OntologyProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.TermProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOfProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.SuperclassProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.SynonymProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public class CreateSubmissionView implements IsWidget {

	private SynonymProperties synonymProperties = GWT
			.create(SynonymProperties.class);
	private PartOfProperties partOfProperties = GWT
			.create(PartOfProperties.class);
	private SuperclassProperties superclassProperties = GWT
			.create(SuperclassProperties.class);
	private OntologyProperties ontologyProperties = GWT
			.create(OntologyProperties.class);
	private TermProperties termProperties = GWT.create(TermProperties.class);
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
	private boolean showOntology;

	public CreateSubmissionView(EventBus eventBus, boolean showClassIRI, boolean showPartOfRelations, boolean showDefaultSuperclasses, 
			boolean showOntology, boolean enableOntology, boolean showIRITextFieldForSynonym) {
		this.eventBus = eventBus;
		this.showOntology = showOntology;

		termCardButton = new TextButton("Term");
		relationCardButton = new TextButton("Relations");
		metaCardButton = new TextButton("Meta-data");

		selectTermView = new SelectTermView(eventBus, true, true, true, showClassIRI, showOntology, enableOntology, showIRITextFieldForSynonym, showDefaultSuperclasses);
		selectRelationsView = new SelectRelationsView(eventBus, true, showPartOfRelations, showDefaultSuperclasses);
		selectMetadataView = new SelectMetadataView(eventBus, true);

		cardLayout = new CardLayoutContainer();
		cardLayout.add(selectTermView);
		cardLayout.add(selectRelationsView);
		cardLayout.add(selectMetadataView);
		cardLayout.setActiveWidget(selectTermView);

		container = new FramedPanel();
		FlowLayoutContainer flowLayout = new FlowLayoutContainer();
		flowLayout.setScrollMode(ScrollMode.AUTOY);
		flowLayout.add(cardLayout);
		container.setHeadingText("Create Submission: ");
		container.add(flowLayout);
		//container.addButton(new LabelToolItem("Switch Cards"));
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
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.Handler() {
			@Override
			public void onSelect(TermSelectEvent event) {
				container.setHeadingText("Create Submission: " + event.getTerm().getTerm());
				cardLayout.setActiveWidget(selectTermView);
			}
		});
		
		selectTermView.addSubmissionTypeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(selectTermView.isClass()) {
					selectTermView.setClassSubmission();
					selectRelationsView.setClassSubmission();
					selectMetadataView.setClassSubmission();
				} else if(selectTermView.isSynonym()) {
					selectTermView.setSynonymSubmission();
					selectRelationsView.setSynonymSubmission();
					selectMetadataView.setSynonymSubmission();
				}
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
				if(validateForm()) {
					final MessageBox box = Alerter.startLoading();
					saveSubmission(box);
				} else {
					Alerter.alertInvalidForm();
				}
			}
		});
	}

	protected void saveSubmission(final MessageBox box) {
		if(selectTermView.isClass()) {
			final OntologyClassSubmission submission = getClassSubmission();
			toOntologyService.createClassSubmission(ModelController.getCollection(), submission, new AsyncCallback<List<OntologyClassSubmission>>() {
				@Override
				public void onFailure(Throwable caught) {
					Alerter.stopLoading(box);
					Alerter.failedToSubmitClass(caught);
				}
				@Override
				public void onSuccess(List<OntologyClassSubmission> result) {
					Alerter.stopLoading(box);
					eventBus.fireEvent(new CreateOntologyClassSubmissionEvent(result));
					String resultText = "";
					for(OntologyClassSubmission submission : result) {
						resultText += submission.getSubmissionTerm() + "<br>";
					}
					Info.display(SafeHtmlUtils.fromSafeConstant("Class saved"), SafeHtmlUtils.fromSafeConstant(resultText));
				}
			});
		} else if(selectTermView.isSynonym()) {
			final OntologySynonymSubmission submission = getSynonymSubmission();
			toOntologyService.createSynonymSubmission(ModelController.getCollection(), submission, new AsyncCallback<OntologySynonymSubmission>() {
				@Override
				public void onFailure(Throwable caught) {
					Alerter.stopLoading(box);
					Alerter.failedToSubmitClass(caught);
				}
				@Override
				public void onSuccess(OntologySynonymSubmission result) {
					Alerter.stopLoading(box);
					eventBus.fireEvent(new CreateOntologySynonymSubmissionEvent(result));
					Info.display("Synonym saved", result.getSubmissionTerm() + " = " + result.getClassLabel());
				}
			});
		}
		Alerter.stopLoading(box);
	}

	protected OntologySynonymSubmission getSynonymSubmission() {
		List<Synonym> synonyms = selectRelationsView.getSynonyms();
		return new OntologySynonymSubmission(ModelController.getCollection().getId(), selectTermView.getTerm(), selectTermView.getSubmissionTerm(), 
				showOntology ? selectTermView.getOntology() : ModelController.getOntologies().get(0), selectTermView.getClassIRI(), selectTermView.getClassLabel(), synonyms, selectMetadataView.getSource(), selectMetadataView.getSample(), Ontologize.user);
	}

	protected OntologyClassSubmission getClassSubmission() {
		List<PartOf> partOfs = selectRelationsView.getPartOfs();
		List<Superclass> superclasses = selectRelationsView.getSuperclasses();
		List<Synonym> synonyms = selectRelationsView.getSynonyms();
		return new OntologyClassSubmission(ModelController.getCollection().getId(), selectTermView.getTerm(), selectTermView.getSubmissionTerm(), 
				showOntology ? selectTermView.getOntology() : ModelController.getOntologies().get(0), selectTermView.getClassIRI(), superclasses,
				selectMetadataView.getDefinition(), synonyms, selectMetadataView.getSource(), 
				selectMetadataView.getSample(), partOfs, Ontologize.user);
	}

	protected boolean validateForm() {
		boolean result = true;
		result &= selectTermView.validate();
		result &= selectRelationsView.validate();
		result &= selectMetadataView.validate();
		return result;
	}

	@Override
	public Widget asWidget() {
		return container;
	}

}
