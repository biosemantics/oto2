package edu.arizona.biosemantics.oto2.ontologize.client.toontology.submit;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

import edu.arizona.biosemantics.oto2.ontologize.client.Ontologize;
import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.common.CreateOntologyDialog;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologySynonymSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.OntologySynonymSubmissionSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSampleEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSourceEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSynonymEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologySynonymsSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.OntologyProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.TermProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySubmission.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.ClassExistsException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.OntologyNotFoundException;

public class SubmitLocalSynonymView implements IsWidget {
	
	private OntologyProperties ontologyProperties = GWT.create(OntologyProperties.class);
	private TermProperties termProperties = GWT.create(TermProperties.class);
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);
	private EventBus eventBus;
	private Collection collection;
	
	private ListStore<Ontology> ontologiesStore = new ListStore<Ontology>(ontologyProperties.key());
	private ListStore<String> classIRIStore = new ListStore<String>(new ModelKeyProvider<String>() {
		@Override
		public String getKey(String item) {
			return item;
		}
	});
	private ListStore<Term> termStore = new ListStore<Term>(termProperties.key());
	private TextButton editButton = new TextButton("Edit");
	private TextButton submitButton = new TextButton("Save as New");
	private TextButton obsoleteSubmitButton = new TextButton("Obsolete and Save as New");
	private TextField submissionTermField = new TextField();
	private TextField categoryField = new TextField();
	private ComboBox<Ontology> ontologyComboBox;
	private ComboBox<String> classIRICheckBox;
	private TextField sourceField = new TextField();
	private TextArea sampleArea = new TextArea();
	private Radio isEntityRadio = new Radio();
	private Radio isQualityRadio = new Radio();
	
	private ListView<String, String> synonymsListView;
	private ListStore<String> synonymsStore;
	private TextButton addSynonymButton = new TextButton("Add");
	private TextButton removeSynonymButton = new TextButton("Remove");
	private TextButton clearSynonymButton = new TextButton("Clear");
	
	private ComboBox<Term> termComboBox;
	private VerticalLayoutContainer vlc;
	//private TextButton createOntologyButton = new TextButton("Create New Ontology");
	private OntologySynonymSubmission selectedSubmission;
	private VerticalLayoutContainer formContainer;
	
	public SubmitLocalSynonymView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		classIRICheckBox = new ComboBox<String>(classIRIStore, new StringLabelProvider<String>());
		
	    ontologyComboBox = new ComboBox<Ontology>(ontologiesStore, ontologyProperties.prefixLabel());
	    ontologyComboBox.setAllowBlank(false);
	    ontologyComboBox.setAutoValidate(true);
	    ontologyComboBox.setForceSelection(false);
	    ontologyComboBox.setTriggerAction(TriggerAction.ALL);
	    
	    termComboBox = new ComboBox<Term>(termStore, termProperties.nameLabel());
	    categoryField.setEnabled(false);
	    
	    synonymsStore = new ListStore<String>(new ModelKeyProvider<String>() {
			@Override
			public String getKey(String item) {
				return item;
			}
	    });
	    synonymsListView = new ListView<String, String>(synonymsStore, new IdentityValueProvider<String>());
	    
	    formContainer = new VerticalLayoutContainer();
	    formContainer.add(new FieldLabel(termComboBox, "Candiate Term"), new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(submissionTermField, "Term *"), new VerticalLayoutData(1, -1));
	    submissionTermField.setAllowBlank(false);
	    submissionTermField.setAutoValidate(true);
	    formContainer.add(new FieldLabel(categoryField, "Category"), new VerticalLayoutData(1, -1));
	    VerticalLayoutContainer ontologyVlc = new VerticalLayoutContainer();
	    //ontologyVlc.add(createOntologyButton, new VerticalLayoutData(1, -1));
	    ontologyVlc.add(ontologyComboBox, new VerticalLayoutData(1, -1));
	    ontologyComboBox.setAllowBlank(false);
	    ontologyComboBox.setAutoValidate(true);
	    //ontologyVlc.add(browseOntologiesButton, new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(ontologyVlc, "Ontology *"), new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(classIRICheckBox, "Class IRI *"), new VerticalLayoutData(1, -1));
	    classIRICheckBox.setAllowBlank(false);
	    classIRICheckBox.setAutoValidate(true);
	    
	    isEntityRadio.setBoxLabel("Is Entity");
	    isEntityRadio.setValue(true);
	    isQualityRadio.setBoxLabel("Is Quality");
	    HorizontalPanel hp = new HorizontalPanel();
	    hp.add(isEntityRadio);
	    hp.add(isQualityRadio);
	    ToggleGroup group = new ToggleGroup();
	    group.add(isEntityRadio);
	    group.add(isQualityRadio);
	    formContainer.add(new FieldLabel(hp, "Type"), new VerticalLayoutData(1, -1));
	    
	    formContainer.add(new FieldLabel(sampleArea, "Sample Sentence"), new VerticalLayoutData(1, 50));
	    formContainer.add(new FieldLabel(sourceField, "Source"), new VerticalLayoutData(1, -1));
	    HorizontalLayoutContainer synonymHorizontal = new HorizontalLayoutContainer();
	    synonymHorizontal.add(synonymsListView, new HorizontalLayoutData(0.7, 75));
	    VerticalLayoutContainer synonymVertical = new VerticalLayoutContainer();
	    synonymHorizontal.add(synonymVertical, new HorizontalLayoutData(0.3, -1));
	    synonymVertical.add(addSynonymButton, new VerticalLayoutData(1, -1));
	    synonymVertical.add(removeSynonymButton, new VerticalLayoutData(1, -1));
	    synonymVertical.add(clearSynonymButton, new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(synonymHorizontal, "Synonyms"), new VerticalLayoutData(1, 75));
	    formContainer.setScrollMode(ScrollMode.AUTO);
	    formContainer.setAdjustForScroll(true);
	    
	    vlc = new VerticalLayoutContainer();
	    vlc.add(new Label("* marks requried fields"), new VerticalLayoutData(1, -1));
	    vlc.add(formContainer, new VerticalLayoutData(1, 1));
	    HorizontalLayoutContainer hlc = new HorizontalLayoutContainer();
	    hlc.add(editButton, new HorizontalLayoutData(0.33,-1));
	    hlc.add(submitButton, new HorizontalLayoutData(0.33,-1));
	    hlc.add(obsoleteSubmitButton, new HorizontalLayoutData(0.33,-1));
	    vlc.add(hlc, new VerticalLayoutData(1, 24)); //-1));
	    
	    bindEvents();		
	}
	
	private boolean validateForm() {
		boolean validateValues = true;
		Iterator<Widget> iterator = formContainer.iterator();
		while(iterator.hasNext()) {
			Widget widget = iterator.next();
			if(widget instanceof FieldLabel) {
				FieldLabel fieldLabel = (FieldLabel)widget;
				Widget fieldWidget = fieldLabel.getWidget();
				boolean result = validate(fieldWidget);
				if(!result)
					return false;
			}
		}
		return validateValues;
	}
	
	private boolean validate(Widget widget) {
		if(widget instanceof Field) {
			Field field = (Field)widget;
			boolean result = field.validate();
			if(!result)
				return false;
		}
		if(widget instanceof HasWidgets) {
			HasWidgets hasWidgets = (HasWidgets)widget;
			Iterator<Widget> it = hasWidgets.iterator();
			while(it.hasNext()) {
				return validate(it.next());
			}
		}
		return true;
	}

	private boolean validateEdit() {
		if(!selectedSubmission.getClassIRI().equals(classIRICheckBox.getValue())) {
			Alerter.alertCantModify("class IRI");
			return false;
		}
		if(!selectedSubmission.getSubmissionTerm().equals(submissionTermField.getValue())) {
			Alerter.alertCantModify("submission term");
			return false;
		}
		if(selectedSubmission.getType().equals(Type.ENTITY) && !isEntityRadio.getValue()) {
			Alerter.alertCantModify("is entity");
			return false;
		}
		if(selectedSubmission.getType().equals(Type.QUALITY) && !isQualityRadio.getValue()) {
			Alerter.alertCantModify("is quality");
			return false;
		}
		if(!selectedSubmission.getOntology().equals(ontologyComboBox.getValue())) {
			Alerter.alertCantModify("ontology");
			return false;
		}
		if((!selectedSubmission.hasTerm() && termComboBox.getValue() != null) || 
			!selectedSubmission.getTerm().equals(termComboBox.getValue())) {
			Alerter.alertCantModify("term");
			return false;
		}
		return true;
	}

	private void bindEvents() {
		eventBus.addHandler(SelectSynonymEvent.TYPE, new SelectSynonymEvent.Handler() {
			@Override
			public void onSelect(SelectSynonymEvent event) {
				setSelectedSubmission(null);
				clearFields(false);
				classIRICheckBox.setValue(event.getSubmission().getClassIRI(), false);
				ontologyComboBox.setValue(event.getSubmission().getOntology());
			}
		});
		eventBus.addHandler(SelectSourceEvent.TYPE, new SelectSourceEvent.Handler() {
			@Override
			public void onSelect(SelectSourceEvent event) {
				sourceField.setValue(event.getSource(), false);
			}
		});
		eventBus.addHandler(SelectSampleEvent.TYPE, new SelectSampleEvent.Handler() {
			@Override
			public void onSelect(SelectSampleEvent event) {
				sampleArea.setValue(event.getSample(), false);
			}
		});
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				SubmitLocalSynonymView.this.collection = event.getCollection();
				
				initCollection();
			}
		});
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.Handler() {
			@Override
			public void onSelect(TermSelectEvent event) {
				if(!event.getSource().equals(SubmitLocalSynonymView.this)) {
					clearFields(false);
					setSelectedSubmission(null);
					setTerm(event.getTerm());
				}
			}
		});
		eventBus.addHandler(OntologySynonymSubmissionSelectEvent.TYPE, new OntologySynonymSubmissionSelectEvent.Handler() {
			@Override
			public void onSelect(OntologySynonymSubmissionSelectEvent event) {
				setOntologySynonymSubmission(event.getOntologySynonymSubmission());
			}
		});
		termComboBox.addValueChangeHandler(new ValueChangeHandler<Term>() {
			@Override
			public void onValueChange(ValueChangeEvent<Term> event) {
				setTerm(event.getValue());
				eventBus.fireEventFromSource(new TermSelectEvent(event.getValue()), SubmitLocalSynonymView.this);
			}
		});
		/*createOntologyButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final CreateOntologyDialog dialog = new CreateOntologyDialog();
				dialog.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						final Ontology ontology = new Ontology();
						ontology.setName(dialog.getName());
						ontology.setAcronym(dialog.getAcronym());
						ontology.setTaxonGroups(dialog.getTaxonGroups());
						ontology.setCollectionId(collection.getId());
						
						if(!dialog.getTaxonGroups().contains(collection.getTaxonGroup())) {
							MessageBox box = Alerter.warnOntologyUnaivableForCollection();
							box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
								@Override
								public void onSelect(SelectEvent event) {
									createOntology(ontology);
								}
							});
						} else {
							createOntology(ontology);
						}
						
						createOntology(ontology);
					}

					private void createOntology(Ontology ontology) {
						final MessageBox box = Alerter.startLoading();
						toOntologyService.createOntology(collection, ontology, new AsyncCallback<Ontology>() {
							@Override
							public void onFailure(Throwable caught) {
								Alerter.failedToCreateOntology();
								Alerter.stopLoading(box);
							}
							@Override
							public void onSuccess(Ontology result) {
								Alerter.succesfulCreatedOntology();
								refreshOntologies(result);
								Alerter.stopLoading(box);
							}
						});
					}
				});
				dialog.getButton(PredefinedButton.CANCEL).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) { }
				});
				dialog.show();
			}
		});*/
		
		submitButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if(validateForm()) {
					final MessageBox box = Alerter.startLoading();
					final OntologySynonymSubmission submission = getSynonymSubmission();
					toOntologyService.createSynonymSubmission(collection, submission, new AsyncCallback<OntologySynonymSubmission>() {
						@Override
						public void onFailure(Throwable caught) {
							Alerter.stopLoading(box);
							Alerter.failedToSubmitSynonym(caught);
						}
	
						@Override
						public void onSuccess(OntologySynonymSubmission result) {
							Alerter.stopLoading(box);
							eventBus.fireEvent(new CreateOntologySynonymSubmissionEvent(result));
						} 
					});
				} else {
					Alerter.alertInvalidForm();
				}
			}
		});
		obsoleteSubmitButton.addSelectHandler(new SelectHandler() { 
			@Override
			public void onSelect(SelectEvent event) {
				if(validateForm()) {
					final MessageBox box = Alerter.startLoading();
					final OntologySynonymSubmission submission = getSynonymSubmission();
					final List<OntologySynonymSubmission> removeSubmissions = new LinkedList<OntologySynonymSubmission>();
					removeSubmissions.add(selectedSubmission);
					toOntologyService.removeSynonymSubmissions(collection, removeSubmissions, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Alerter.failedToRemoveOntologyClassSubmission();
							Alerter.stopLoading(box);
						}
						@Override
						public void onSuccess(Void result) {
							eventBus.fireEvent(new RemoveOntologySynonymSubmissionsEvent(removeSubmissions));
							toOntologyService.createSynonymSubmission(collection, submission, new AsyncCallback<OntologySynonymSubmission>() {
								@Override
								public void onFailure(Throwable caught) {
									Alerter.stopLoading(box);
									Alerter.failedToSubmitSynonym(caught);
								}
			
								@Override
								public void onSuccess(OntologySynonymSubmission result) {
									Alerter.stopLoading(box);
									eventBus.fireEvent(new CreateOntologySynonymSubmissionEvent(result));
								}
							});
						}
					});
				} else {
					Alerter.alertInvalidForm();
				}
			}
		});
		editButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if(validateForm()) {
					if(validateEdit()) {
						final MessageBox box = Alerter.startLoading();
						final OntologySynonymSubmission submission = getSynonymSubmission();
						submission.setId(selectedSubmission.getId());
						final List<OntologySynonymSubmission> submissions = new LinkedList<OntologySynonymSubmission>();
						submissions.add(submission);
						toOntologyService.updateSynonymSubmissions(collection, submissions, new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Alerter.stopLoading(box);
								Alerter.failedToEditClass(caught);
							}
							@Override
							public void onSuccess(Void result) {
								Alerter.stopLoading(box);
								eventBus.fireEvent(new UpdateOntologySynonymsSubmissionsEvent(submissions));
							}
						});
					}
				} else {
					Alerter.alertInvalidForm();
				}
			}
		});
		
		ontologyComboBox.addValueChangeHandler(new ValueChangeHandler<Ontology>() {
			@Override
			public void onValueChange(ValueChangeEvent<Ontology> event) {
				final MessageBox box = Alerter.startLoading();
				toOntologyService.getClassSubmissions(collection, event.getValue(), new AsyncCallback<List<OntologyClassSubmission>>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.stopLoading(box);
					}
					@Override
					public void onSuccess(List<OntologyClassSubmission> result) {
						classIRIStore.clear();
						for(OntologyClassSubmission submission : result)
							classIRIStore.add(submission.getClassIRI());
						Alerter.stopLoading(box);
					}
				});
			}
		});
	}

	protected void setTerm(Term term) {
		termComboBox.setValue(term);
		categoryField.setValue(termComboBox.getValue().getCategory());
		submissionTermField.setValue(termComboBox.getValue().getTerm());
	}


	protected void setSelectedSubmission(OntologySynonymSubmission ontologySynonymSubmission) {
		this.selectedSubmission = ontologySynonymSubmission;
		if(selectedSubmission == null)
			this.editButton.setEnabled(false);
		else
			this.editButton.setEnabled(true);
	}
	
	protected void clearFields(boolean fireEvents) {
		this.termComboBox.setValue(null, false);
		this.submissionTermField.setValue("", false); 
		//this.ontologyComboBox.setValue(null, false);
		this.classIRICheckBox.setValue("", false);
		this.synonymsStore.clear();
		this.sourceField.setValue("", false);
		this.sampleArea.setValue("", false);
		this.isEntityRadio.setValue(true, false);
		this.isQualityRadio.setValue(false, false);
	}

	protected void setOntologySynonymSubmission(OntologySynonymSubmission ontologySynonymSubmission) {
		this.selectedSubmission = ontologySynonymSubmission;
		this.termComboBox.setValue(ontologySynonymSubmission.getTerm());
		this.submissionTermField.setValue(ontologySynonymSubmission.getSubmissionTerm()); 
		if(ontologySynonymSubmission.hasOntology())
			this.ontologyComboBox.setValue(ontologySynonymSubmission.getOntology());
		this.classIRICheckBox.setValue(ontologySynonymSubmission.getClassIRI());
		this.synonymsStore.clear();
		this.synonymsStore.addAll(ontologySynonymSubmission.getSynonyms());
		this.sourceField.setValue(ontologySynonymSubmission.getSource());
		this.sampleArea.setValue(ontologySynonymSubmission.getSampleSentence());
		this.isEntityRadio.setValue(ontologySynonymSubmission.getType().equals(Type.ENTITY));
		this.isQualityRadio.setValue(ontologySynonymSubmission.getType().equals(Type.QUALITY));
	}

	protected OntologySynonymSubmission getSynonymSubmission() {
		Type type = isEntityRadio.getValue() ? Type.ENTITY : Type.QUALITY;
		return new OntologySynonymSubmission(collection.getId(), termComboBox.getValue(), submissionTermField.getValue(), 
				ontologyComboBox.getValue(), classIRICheckBox.getValue(),
				new LinkedList<String>(synonymsStore.getAll()),
				sourceField.getValue(), sampleArea.getValue(), 
				type, Ontologize.user);
	}

	protected void initCollection() {
		refreshOntologies(null);
		refreshTerms();
	}

	private void refreshTerms() {
		termStore.clear();
		termStore.addAll(collection.getTerms());
	}

	private void refreshOntologies(final Ontology ontologyToSelect) {
		toOntologyService.getLocalOntologies(collection, new AsyncCallback<List<Ontology>>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.getOntologiesFailed(caught);
			}

			@Override
			public void onSuccess(List<Ontology> result) {
				ontologiesStore.clear();
				ontologiesStore.addAll(result);
				if(ontologyToSelect != null)
					ontologyComboBox.setValue(ontologyToSelect);
			}
		});
	}

	@Override
	public Widget asWidget() {
		return vlc;
	}

}
