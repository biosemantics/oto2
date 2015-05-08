package edu.arizona.biosemantics.oto2.steps.client.toontology;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

import edu.arizona.biosemantics.oto2.steps.client.OtoSteps;
import edu.arizona.biosemantics.oto2.steps.client.common.Alerter;
import edu.arizona.biosemantics.oto2.steps.client.common.CreateOntologyDialog;
import edu.arizona.biosemantics.oto2.steps.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.OntologyClassSubmissionSelectEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.SelectPartOfEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.SelectSampleEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.SelectSourceEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.SelectSuperclassEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.UpdateOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.steps.shared.model.OntologyProperties;
import edu.arizona.biosemantics.oto2.steps.shared.model.Term;
import edu.arizona.biosemantics.oto2.steps.shared.model.TermProperties;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.PartOfProperties;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.SuperclassProperties;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.Synonym;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.SynonymProperties;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology.ClassExistsException;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology.IToOntologyServiceAsync;
import edu.arizona.biosemantics.oto2.steps.shared.rpc.toontology.OntologyNotFoundException;

public class SubmitClassView implements IsWidget {
	
	private OntologyProperties ontologyProperties = GWT.create(OntologyProperties.class);
	private SynonymProperties synonymProperties = GWT.create(SynonymProperties.class);
	private PartOfProperties partOfProperties = GWT.create(PartOfProperties.class);
	private SuperclassProperties superclassProperties = GWT.create(SuperclassProperties.class);
	private TermProperties termProperties = GWT.create(TermProperties.class);
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);
	private EventBus eventBus;
	private Collection collection;
	
	private ListStore<Ontology> ontologiesStore = new ListStore<Ontology>(ontologyProperties.key());
	private ListStore<Term> termStore = new ListStore<Term>(termProperties.key());
	private TextButton editButton = new TextButton("Edit");
	private TextButton submitButton = new TextButton("Save as New");
	private TextButton obsoleteSubmit = new TextButton("Obsolete and Save as New");

	private ComboBox<Term> termComboBox;
	private TextField submissionTermField = new TextField();
	private TextField categoryField = new TextField();
	private TextButton browseOntologiesButton = new TextButton("Browse Selected Ontology");
	private TextButton createOntologyButton = new TextButton("Create New Ontology");
	private ComboBox<Ontology> ontologyComboBox;
	private TextField classIRIField = new TextField();
	
	private ListView<String, String> superclassListView;
	private ListStore<String> superclassStore;
	private TextButton addSuperclassButton = new TextButton("Add");
	private TextButton removeSuperclassButton = new TextButton("Remove");
	private TextButton clearSuperclassButton = new TextButton("Clear");
	
	private ListView<String, String> partOfListView;
	private ListStore<String> partOfStore;
	private TextButton addPartOfButton = new TextButton("Add");
	private TextButton removePartOfButton = new TextButton("Remove");
	private TextButton clearPartOfButton = new TextButton("Clear");

	private CheckBox isEntityCheckBox = new CheckBox();
	private CheckBox isQualityCheckBox = new CheckBox();

	private TextArea definitionArea = new TextArea();
	private TextArea sampleArea = new TextArea();
	private TextField sourceField = new TextField();
	

	private ListView<String, String> synonymsListView;
	private ListStore<String> synonymsStore;
	private TextButton addSynonymButton = new TextButton("Add");
	private TextButton removeSynonymButton = new TextButton("Remove");
	private TextButton clearSynonymButton = new TextButton("Clear");
	
	private OntologyClassSubmission selectedSubmission;
	private VerticalLayoutContainer formContainer;
	private VerticalLayoutContainer vlc;
	
	public SubmitClassView(EventBus eventBus) {
		this.eventBus = eventBus;
		
	    ontologyComboBox = new ComboBox<Ontology>(ontologiesStore, ontologyProperties.prefixLabel());
	    ontologyComboBox.setAllowBlank(true);
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
	    superclassStore = new ListStore<String>(new ModelKeyProvider<String>() {
			@Override
			public String getKey(String item) {
				return item;
			}
	    });
	    superclassListView = new ListView<String, String>(superclassStore, new IdentityValueProvider<String>());
	    partOfStore = new ListStore<String>(new ModelKeyProvider<String>() {
			@Override
			public String getKey(String item) {
				return item;
			}
	    });
	    partOfListView = new ListView<String, String>(partOfStore, new IdentityValueProvider<String>());
	    
	    formContainer = new VerticalLayoutContainer();
	    formContainer.add(new FieldLabel(termComboBox, "Candiate Term"), new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(submissionTermField, "Term *"), new VerticalLayoutData(1, -1));
	    submissionTermField.setAllowBlank(false);
	    submissionTermField.setAutoValidate(true);
	    formContainer.add(new FieldLabel(categoryField, "Category"), new VerticalLayoutData(1, -1));
	    VerticalLayoutContainer ontologyVlc = new VerticalLayoutContainer();
	    ontologyVlc.add(createOntologyButton, new VerticalLayoutData(1, -1));
	    ontologyVlc.add(ontologyComboBox, new VerticalLayoutData(1, -1));
	    //ontologyVlc.add(browseOntologiesButton, new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(ontologyVlc, "Ontology *"), new VerticalLayoutData(1, -1));
	    ontologyComboBox.setAllowBlank(false);
	    ontologyComboBox.setAutoValidate(true);
	    formContainer.add(new FieldLabel(classIRIField, "Class IRI"), new VerticalLayoutData(1, -1));
	    
	    HorizontalLayoutContainer superclassHorizontal = new HorizontalLayoutContainer();
	    superclassHorizontal.add(superclassListView, new HorizontalLayoutData(0.7, 75));
	    VerticalLayoutContainer superclassVertical = new VerticalLayoutContainer();
	    superclassHorizontal.add(superclassVertical, new HorizontalLayoutData(0.3, -1));
	    superclassVertical.add(addSuperclassButton, new VerticalLayoutData(1, -1));
	    superclassVertical.add(removeSuperclassButton, new VerticalLayoutData(1, -1));
	    superclassVertical.add(clearSuperclassButton, new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(superclassHorizontal, "Superclass IRI"), new VerticalLayoutData(1, 75));
	    
	    HorizontalLayoutContainer partOfHorizontal = new HorizontalLayoutContainer();
	    partOfHorizontal.add(partOfListView, new HorizontalLayoutData(0.7, 75));
	    VerticalLayoutContainer partOfVertical = new VerticalLayoutContainer();
	    partOfHorizontal.add(partOfVertical, new HorizontalLayoutData(0.3, -1));
	    partOfVertical.add(addPartOfButton, new VerticalLayoutData(1, -1));
	    partOfVertical.add(removePartOfButton, new VerticalLayoutData(1, -1));
	    partOfVertical.add(clearPartOfButton, new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(partOfHorizontal, "Part of IRI"), new VerticalLayoutData(1, 75));
	    formContainer.add(new FieldLabel(isEntityCheckBox, "Is Entity"), new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(isQualityCheckBox, "Is Quality"), new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(definitionArea, "Definition"), new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(sampleArea, "Sample Sentence"), new VerticalLayoutData(1, 100));
	    formContainer.add(new FieldLabel(sourceField, "Source"), new VerticalLayoutData(1, -1));
	    
	    HorizontalLayoutContainer synonymHorizontal = new HorizontalLayoutContainer();
	    synonymHorizontal.add(synonymsListView, new HorizontalLayoutData(0.7, 75));
	    VerticalLayoutContainer synonymVertical = new VerticalLayoutContainer();
	    synonymHorizontal.add(synonymVertical, new HorizontalLayoutData(0.3, -1));
	    synonymVertical.add(addSynonymButton, new VerticalLayoutData(1, -1));
	    synonymVertical.add(removeSynonymButton, new VerticalLayoutData(1, -1));
	    synonymVertical.add(clearSynonymButton, new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(synonymHorizontal, "Synonyms"), new VerticalLayoutData(1, 75));
	    formContainer.setScrollMode(ScrollMode.AUTOY);
	    formContainer.setAdjustForScroll(true);
	    
	    vlc = new VerticalLayoutContainer();
	    vlc.add(new Label("* marks requried fields"), new VerticalLayoutData(1, -1));
	    vlc.add(formContainer, new VerticalLayoutData(1, 1));
	    HorizontalLayoutContainer hlc = new HorizontalLayoutContainer();
	    hlc.add(editButton, new HorizontalLayoutData(0.33, -1));
	    hlc.add(submitButton, new HorizontalLayoutData(0.33, -1));
	    hlc.add(obsoleteSubmit, new HorizontalLayoutData(0.33, -1));
	    vlc.add(hlc, new VerticalLayoutData(1, 24)); //for some reason won't work: -1));
	    
		bindEvents();		
	}
	
	private boolean validateForm() {
		boolean validateValues = true;
		Iterator<Widget> iterator = formContainer.iterator();
		while(iterator.hasNext()) {
			Widget widget = iterator.next();
			if(widget instanceof FieldLabel) {
				FieldLabel fieldLabel = (FieldLabel)widget;
				Widget field = fieldLabel.getWidget();
				if(field instanceof Field) {
					Field f = (Field)field;
					boolean result = f.validate();
					if(!result)
						validateValues = false;
				}
			}
		}
		return validateValues;
	}
	
	private boolean validateEdit() {
		if(!selectedSubmission.getClassIRI().equals(classIRIField.getValue())) {
			Alerter.alertCantModify("class IRI");
			return false;
		}
		if(!selectedSubmission.getSubmissionTerm().equals(submissionTermField.getValue())) {
			Alerter.alertCantModify("submission term");
			return false;
		}
		if(selectedSubmission.isEntity() != isEntityCheckBox.getValue()) {
			Alerter.alertCantModify("is entity");
			return false;
		}
		if(selectedSubmission.isQuality() != isQualityCheckBox.getValue()) {
			Alerter.alertCantModify("is quality");
			return false;
		}
		if(!selectedSubmission.getOntology().equals(ontologyComboBox.getValue())) {
			Alerter.alertCantModify("ontology");
			return false;
		}
		if(!selectedSubmission.getTerm().equals(termComboBox.getValue())) {
			Alerter.alertCantModify("term");
			return false;
		}
		return true;
	}

	private void bindEvents() {
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
		eventBus.addHandler(SelectSuperclassEvent.TYPE, new SelectSuperclassEvent.Handler() {
			@Override
			public void onSelect(SelectSuperclassEvent event) {
				setSelectedSubmission(null);
				clearFields(false);
				superclassStore.clear();
				superclassStore.add(event.getSubmission().getClassIRI());
				ontologyComboBox.setValue(event.getSubmission().getOntology());
			}
		});
		eventBus.addHandler(SelectPartOfEvent.TYPE, new SelectPartOfEvent.Handler() {
			@Override
			public void onSelect(SelectPartOfEvent event) {
				setSelectedSubmission(null);
				clearFields(false);
				partOfStore.clear();
				partOfStore.add(event.getSubmission().getClassIRI());
				ontologyComboBox.setValue(event.getSubmission().getOntology());
			}
		});
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				SubmitClassView.this.collection = event.getCollection();
				
				initCollection();
			}
		});
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.Handler() {
			@Override
			public void onSelect(TermSelectEvent event) {
				if(!event.getSource().equals(SubmitClassView.this)) {
					clearFields(false);
					setSelectedSubmission(null);
					setTerm(event.getTerm());
				}
			}
		});
		eventBus.addHandler(OntologyClassSubmissionSelectEvent.TYPE, new OntologyClassSubmissionSelectEvent.Handler() {
			@Override
			public void onSelect(OntologyClassSubmissionSelectEvent event) {
				setOntologyClassSubmission(event.getOntologyClassSubmission());
			}
		});
		termComboBox.addValueChangeHandler(new ValueChangeHandler<Term>() {
			@Override
			public void onValueChange(ValueChangeEvent<Term> event) {
				setTerm(event.getValue());
				eventBus.fireEventFromSource(new TermSelectEvent(event.getValue()), SubmitClassView.this);
			}
		});
		createOntologyButton.addSelectHandler(new SelectHandler() {
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
		});
		browseOntologiesButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if(ontologyComboBox.getValue() != null && 
						ontologyComboBox.getValue().getBrowseURL() != null && !ontologyComboBox.getValue().getBrowseURL().isEmpty())
					Window.open(ontologyComboBox.getValue().getBrowseURL(), "_blank", "");
				else
					Alerter.failedToBrowseOntology();
			} 
		});
		
		submitButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if(validateForm()) {
					final MessageBox box = Alerter.startLoading();
					final OntologyClassSubmission submission = getClassSubmission();
					toOntologyService.createClassSubmission(submission, new AsyncCallback<OntologyClassSubmission>() {
						@Override
						public void onFailure(Throwable caught) {
							Alerter.stopLoading(box);
							if(caught.getCause() != null) {
								if(caught instanceof OntologyNotFoundException) {
									Alerter.failedToSubmitClassOntologyNotFound(caught.getCause());
								}
								if(caught instanceof ClassExistsException) {
									Alerter.failedToSubmitClassExists(caught.getCause());
								} else {
									Alerter.failedToSubmitClass(caught);
								}
							}
							Alerter.failedToSubmitClass(caught);
						}
						@Override
						public void onSuccess(OntologyClassSubmission result) {
							Alerter.stopLoading(box);
							eventBus.fireEvent(new CreateOntologyClassSubmissionEvent(result));
						}
					});
				} else {
					Alerter.alertInvalidForm();
				}
			}

		
		});
		obsoleteSubmit.addSelectHandler(new SelectHandler() { 
			@Override
			public void onSelect(SelectEvent event) {
				if(validateForm()) {
					final MessageBox box = Alerter.startLoading();
					final OntologyClassSubmission newSubmission = getClassSubmission();
					final List<OntologyClassSubmission> removeSubmissions = new LinkedList<OntologyClassSubmission>();
					removeSubmissions.add(selectedSubmission);
					toOntologyService.removeClassSubmissions(collection, removeSubmissions, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Alerter.failedToRemoveOntologyClassSubmission();
							Alerter.stopLoading(box);
						}
						@Override
						public void onSuccess(Void result) {
							eventBus.fireEvent(new RemoveOntologyClassSubmissionsEvent(removeSubmissions));
							toOntologyService.createClassSubmission(newSubmission, new AsyncCallback<OntologyClassSubmission>() {
								@Override
								public void onFailure(Throwable caught) {
									Alerter.stopLoading(box);
									if(caught.getCause() != null) {
										if(caught instanceof OntologyNotFoundException) {
											Alerter.failedToSubmitClassOntologyNotFound(caught.getCause());
										}
										if(caught instanceof ClassExistsException) {
											Alerter.failedToSubmitClassExists(caught.getCause());
										} else {
											Alerter.failedToSubmitClass(caught);
										}
									}
									Alerter.failedToSubmitClass(caught);
								}
								@Override
								public void onSuccess(OntologyClassSubmission result) {
									Alerter.stopLoading(box);
									eventBus.fireEvent(new CreateOntologyClassSubmissionEvent(result));
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
						final OntologyClassSubmission submission = getClassSubmission();
						submission.setId(selectedSubmission.getId());
						final List<OntologyClassSubmission> submissions = new LinkedList<OntologyClassSubmission>();
						submissions.add(submission);
						toOntologyService.updateClassSubmissions(collection, submissions, new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Alerter.stopLoading(box);
								Alerter.failedToEditClass(caught);
							}
							@Override
							public void onSuccess(Void result) {
								Alerter.stopLoading(box);
								eventBus.fireEvent(new UpdateOntologyClassSubmissionsEvent(submissions));
							}
						});
					} 
				} else {
					Alerter.alertInvalidForm();
				}
			}
		});
		
		addSynonymButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final PromptMessageBox box = new PromptMessageBox("Add Synonym", "Add Synonym");
				box.show();
				box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						synonymsStore.add(box.getValue());
					}
				});
			}
		});
		removeSynonymButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				for(String remove : synonymsListView.getSelectionModel().getSelectedItems())
					synonymsStore.remove(remove);
			}
		});
		clearSynonymButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				synonymsStore.clear();
			}
		});
		
		addSuperclassButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final PromptMessageBox box = new PromptMessageBox("Add Super Class", "Add Super Class");
				box.show();
				box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						superclassStore.add(box.getValue());
					}
				});
			}
		});
		removeSuperclassButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				for(String remove : superclassListView.getSelectionModel().getSelectedItems())
					superclassStore.remove(remove);
			}
		});
		clearSuperclassButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				superclassStore.clear();
			}
		});
		
		addPartOfButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final PromptMessageBox box = new PromptMessageBox("Add Part Of", "Add Part Of");
				box.show();
				box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						partOfStore.add(box.getValue());
					}
				});
			}
		});
		removePartOfButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				for(String remove : partOfListView.getSelectionModel().getSelectedItems())
					partOfStore.remove(remove);
			}
		});
		clearPartOfButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				partOfStore.clear();
			}
		});
	}

	protected void setTerm(Term term) {
		termComboBox.setValue(term);
		categoryField.setValue(termComboBox.getValue().getCategory());
		submissionTermField.setValue(termComboBox.getValue().getTerm());
	}

	protected void setSelectedSubmission(OntologyClassSubmission ontologyClassSubmission) {
		this.selectedSubmission = ontologyClassSubmission;
		if(selectedSubmission == null)
			this.editButton.setEnabled(false);
		else
			this.editButton.setEnabled(true);
	}
	
	protected void clearFields(boolean fireEvents) {
		this.termComboBox.setValue(null, false);
		this.submissionTermField.setValue("", false); 
		//this.ontologyComboBox.setValue(null, false);
		this.classIRIField.setValue("", false);
		this.superclassStore.clear();
		this.definitionArea.setValue("", false);
		this.synonymsStore.clear();
		this.sourceField.setValue("", false);
		this.sampleArea.setValue("", false);
		this.partOfStore.clear();
		this.isEntityCheckBox.setValue(false, false);
		this.isQualityCheckBox.setValue(false, false);
	}

	protected void setOntologyClassSubmission(OntologyClassSubmission ontologyClassSubmission) {
		this.setSelectedSubmission(ontologyClassSubmission);
		this.termComboBox.setValue(ontologyClassSubmission.getTerm());
		this.submissionTermField.setValue(ontologyClassSubmission.getSubmissionTerm()); 
		if(ontologyClassSubmission.hasOntology())
			this.ontologyComboBox.setValue(ontologyClassSubmission.getOntology());
		this.classIRIField.setValue(ontologyClassSubmission.getClassIRI());
		this.superclassStore.clear();
		this.superclassStore.addAll(ontologyClassSubmission.getSuperclassIRIs());
		this.definitionArea.setValue(ontologyClassSubmission.getDefinition());
		this.synonymsStore.clear();
		this.synonymsStore.addAll(ontologyClassSubmission.getSynonyms());
		this.sourceField.setValue(ontologyClassSubmission.getSource());
		this.sampleArea.setValue(ontologyClassSubmission.getSampleSentence());
		this.partOfStore.clear();
		this.partOfStore.addAll(ontologyClassSubmission.getPartOfIRIs());
		this.isEntityCheckBox.setValue(ontologyClassSubmission.isEntity());
		this.isQualityCheckBox.setValue(ontologyClassSubmission.isQuality());
	}
	
	protected OntologyClassSubmission getClassSubmission() {
		boolean entity = isEntityCheckBox.getValue();
		boolean quality = isQualityCheckBox.getValue();
		String user = OtoSteps.user;
		return new OntologyClassSubmission(termComboBox.getValue(), submissionTermField.getValue(), 
				ontologyComboBox.getValue(), classIRIField.getValue(), new LinkedList<String>(superclassStore.getAll()),
				definitionArea.getValue(), new LinkedList<String>(synonymsStore.getAll()), sourceField.getValue(), 
				sampleArea.getValue(), new LinkedList<String>(partOfStore.getAll()), isEntityCheckBox.getValue(), 
				isQualityCheckBox.getValue(), OtoSteps.user);
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
		toOntologyService.getOntologies(collection, new AsyncCallback<List<Ontology>>() {
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
