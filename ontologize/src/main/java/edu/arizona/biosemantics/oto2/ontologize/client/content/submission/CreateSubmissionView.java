package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
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
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.ListView.ListViewAppearance;
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
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

import edu.arizona.biosemantics.oto2.ontologize.client.Ontologize;
import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.common.CreateOntologyDialog;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.OntologyClassSubmissionSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectOntologyEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectPartOfEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSampleEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSourceEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSuperclassEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.OntologyProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.TermProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOfProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.SuperclassProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.SynonymProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.ClassExistsException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.OntologyNotFoundException;

public class CreateSubmissionView implements IsWidget {

	private SynonymProperties synonymProperties = GWT.create(SynonymProperties.class);
	private PartOfProperties partOfProperties = GWT.create(PartOfProperties.class);
	private SuperclassProperties superclassProperties = GWT.create(SuperclassProperties.class);
	private OntologyProperties ontologyProperties = GWT.create(OntologyProperties.class);
	private TermProperties termProperties = GWT.create(TermProperties.class);
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);
	private EventBus eventBus;
	private Collection collection;
	
	private ListStore<Ontology> ontologiesStore = new ListStore<Ontology>(ontologyProperties.key());
	private ListStore<Term> termStore = new ListStore<Term>(termProperties.key());
	private TextButton editButton = new TextButton("Edit");
	private TextButton submitButton = new TextButton("Save as New");
	private TextButton obsoleteSubmitButton = new TextButton("Remove and Save as New");

	private ComboBox<Term> termComboBox;
	private TextField submissionTermField = new TextField();
	private TextField categoryField = new TextField();
	//private TextButton createOntologyButton = new TextButton("Create New Ontology");
	private ComboBox<Ontology> ontologyComboBox;
	private TextField classIRIField = new TextField();
	
	private ListView<Superclass, Superclass> superclassListView;
	private ListStore<Superclass> superclassStore;
	private TextButton addSuperclassButton = new TextButton("Add");
	private TextButton removeSuperclassButton = new TextButton("Remove");
	private TextButton clearSuperclassButton = new TextButton("Clear");
	
	private ListView<PartOf, PartOf> partOfListView;
	private ListStore<PartOf> partOfStore;
	private TextButton addPartOfButton = new TextButton("Add");
	private TextButton removePartOfButton = new TextButton("Remove");
	private TextButton clearPartOfButton = new TextButton("Clear");

	private Radio isEntityRadio = new Radio();
	private Radio isQualityRadio = new Radio();

	private TextArea definitionArea = new TextArea();
	private TextArea sampleArea = new TextArea();
	private TextField sourceField = new TextField();
	

	private ListView<Synonym, String> synonymsListView;
	private ListStore<Synonym> synonymsStore;
	private TextButton addSynonymButton = new TextButton("Add");
	private TextButton removeSynonymButton = new TextButton("Remove");
	private TextButton clearSynonymButton = new TextButton("Clear");
	
	private OntologyClassSubmission selectedSubmission;
	private VerticalLayoutContainer formContainer;
	private VerticalLayoutContainer vlc;
	private FieldLabel partOfFieldLabel;
	private ToggleGroup entityQualityGroup;
	
	public CreateSubmissionView(EventBus eventBus) {
		this.eventBus = eventBus;
		
	    ontologyComboBox = new ComboBox<Ontology>(ontologiesStore, ontologyProperties.prefixLabel());
	    ontologyComboBox.setAllowBlank(false);
	    ontologyComboBox.setAutoValidate(true);
	    ontologyComboBox.setForceSelection(false);
	    ontologyComboBox.setTriggerAction(TriggerAction.ALL);

	    termComboBox = new ComboBox<Term>(termStore, termProperties.nameLabel());
	    categoryField.setEnabled(false);
	    
	    synonymsStore = new ListStore<Synonym>(new ModelKeyProvider<Synonym>() {
			@Override
			public String getKey(Synonym item) {
				return item.getSynonym();
			}
	    });
	    synonymsListView = new ListView<Synonym, String>(synonymsStore, synonymProperties.synonym());
	    superclassStore = new ListStore<Superclass>(new ModelKeyProvider<Superclass>() {
			@Override
			public String getKey(Superclass item) {
				return item.getIri();
			}
	    });
	    superclassListView = new ListView<Superclass, Superclass>(superclassStore, new IdentityValueProvider<Superclass>());	    
	    superclassListView.setCell(new AbstractCell<Superclass>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	Superclass value, SafeHtmlBuilder sb) {
				sb.append(SafeHtmlUtils.fromTrustedString("<div qtip=\"" + value.toString() + "\">" + (value.hasLabel() ? value.getLabel() : value.toString()) + "</div>"));
			}
	    });
	    partOfStore = new ListStore<PartOf>(new ModelKeyProvider<PartOf>() {
			@Override
			public String getKey(PartOf item) {
				return item.getIri();
			}
	    });
	    partOfListView = new ListView<PartOf, PartOf>(partOfStore, new IdentityValueProvider<PartOf>());
	    partOfListView.setCell(new AbstractCell<PartOf>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	PartOf value, SafeHtmlBuilder sb) {
				sb.append(SafeHtmlUtils.fromTrustedString("<div qtip=\"" + value.toString() + "\">" + (value.hasLabel() ? value.getLabel() : value.toString()) + "</div>"));
			}
	    });
	    
	    formContainer = new VerticalLayoutContainer();
	    formContainer.add(new FieldLabel(termComboBox, "Candiate Term"), new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(submissionTermField, "Term *"), new VerticalLayoutData(1, -1));
	    submissionTermField.setAllowBlank(false);
	    submissionTermField.setAutoValidate(true);
	    formContainer.add(new FieldLabel(categoryField, "Category"), new VerticalLayoutData(1, -1));
	    VerticalLayoutContainer ontologyVlc = new VerticalLayoutContainer();
	  //  ontologyVlc.add(createOntologyButton, new VerticalLayoutData(1, -1));
	    ontologyVlc.add(ontologyComboBox, new VerticalLayoutData(1, -1));
	    //ontologyVlc.add(browseOntologiesButton, new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(ontologyVlc, "Ontology *"), new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(classIRIField, "Class IRI"), new VerticalLayoutData(1, -1));
	    
	    isEntityRadio.setBoxLabel("Is Entity");
	    isEntityRadio.setValue(true);
	    this.addTypeToSupreclassStore(Type.ENTITY);
	    isQualityRadio.setBoxLabel("Is Quality");
	    HorizontalPanel hp = new HorizontalPanel();
	    hp.add(isEntityRadio);
	    hp.add(isQualityRadio);
	    entityQualityGroup = new ToggleGroup();
	    entityQualityGroup.add(isEntityRadio);
	    entityQualityGroup.add(isQualityRadio);
	    formContainer.add(new FieldLabel(hp, "Type"), new VerticalLayoutData(1, -1));
	    
	    HorizontalLayoutContainer superclassHorizontal = new HorizontalLayoutContainer();
	    superclassHorizontal.add(superclassListView, new HorizontalLayoutData(0.7, 75));
	    VerticalLayoutContainer superclassVertical = new VerticalLayoutContainer();
	    superclassHorizontal.add(superclassVertical, new HorizontalLayoutData(0.3, -1));
	    superclassVertical.add(addSuperclassButton, new VerticalLayoutData(1, -1));
	    superclassVertical.add(removeSuperclassButton, new VerticalLayoutData(1, -1));
	    superclassVertical.add(clearSuperclassButton, new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(superclassHorizontal, "Superclass (IRI or term)"), new VerticalLayoutData(1, 75));
	    
	    HorizontalLayoutContainer partOfHorizontal = new HorizontalLayoutContainer();
	    partOfHorizontal.add(partOfListView, new HorizontalLayoutData(0.7, 75));
	    VerticalLayoutContainer partOfVertical = new VerticalLayoutContainer();
	    partOfHorizontal.add(partOfVertical, new HorizontalLayoutData(0.3, -1));
	    partOfVertical.add(addPartOfButton, new VerticalLayoutData(1, -1));
	    partOfVertical.add(removePartOfButton, new VerticalLayoutData(1, -1));
	    partOfVertical.add(clearPartOfButton, new VerticalLayoutData(1, -1));
	    partOfFieldLabel = new FieldLabel(partOfHorizontal, "Part-of (IRI or term)");
	    formContainer.add(partOfFieldLabel, new VerticalLayoutData(1, 75));	    
	    
	    formContainer.add(new FieldLabel(definitionArea, "Definition"), new VerticalLayoutData(1, 50));
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
	    formContainer.setScrollMode(ScrollMode.AUTOY);
	    formContainer.setAdjustForScroll(true);
	    
	    vlc = new VerticalLayoutContainer();
	    vlc.add(new Label("* marks requried fields"), new VerticalLayoutData(1, -1));
	    vlc.add(formContainer, new VerticalLayoutData(1, 1));
	    HorizontalLayoutContainer hlc = new HorizontalLayoutContainer();
	    hlc.add(editButton, new HorizontalLayoutData(0.33, -1));
	    hlc.add(submitButton, new HorizontalLayoutData(0.33, -1));
	    hlc.add(obsoleteSubmitButton, new HorizontalLayoutData(0.33, -1));
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
			boolean result = true;
			while(it.hasNext()) {
				result &= validate(it.next());
				if(!result)
					return false;
			}
		}
		return true;
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
		/*if(selectedSubmission.getType().equals(Type.ENTITY) && !isEntityRadio.getValue()) {
			Alerter.alertCantModify("is entity");
			return false;
		}
		if(selectedSubmission.getType().equals(Type.QUALITY) && !isQualityRadio.getValue()) {
			Alerter.alertCantModify("is quality");
			return false;
		}*/
		if(!selectedSubmission.getOntology().equals(ontologyComboBox.getValue())) {
			Alerter.alertCantModify("ontology");
			return false;
		}
		if((!selectedSubmission.hasTerm() && termComboBox.getValue() != null) ||
				(selectedSubmission.hasTerm() && !selectedSubmission.getTerm().equals(termComboBox.getValue()))) {
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
				addSuperClassToStore(event.getSubmission());
				ontologyComboBox.setValue(event.getSubmission().getOntology());
			}
		});
		eventBus.addHandler(SelectPartOfEvent.TYPE, new SelectPartOfEvent.Handler() {
			@Override
			public void onSelect(SelectPartOfEvent event) {
				setSelectedSubmission(null);
				clearFields(false);
				partOfStore.clear();
				addToPartOfStore(event.getSubmission());
				ontologyComboBox.setValue(event.getSubmission().getOntology());
			}
		});
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				CreateSubmissionView.this.collection = event.getCollection();
				initCollection();
			}
		});
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.Handler() {
			@Override
			public void onSelect(TermSelectEvent event) {
				if(!event.getSource().equals(CreateSubmissionView.this)) {
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
		
		isEntityRadio.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				partOfFieldLabel.setVisible(event.getValue());
				if(event.getValue()) {
					removeSuperclass(Type.QUALITY);
					addTypeToSupreclassStore(Type.ENTITY);
					//superclassStore.remove(Type.QUALITY.getIRI());
					//superclassStore.add(Type.ENTITY.getIRI());
				} else {
					removeSuperclass(Type.ENTITY);
					addTypeToSupreclassStore(Type.QUALITY);
					//superclassStore.remove(Type.ENTITY.getIRI());
					//superclassStore.add(Type.QUALITY.getIRI());
				}
			}
		});
		
		termComboBox.addValueChangeHandler(new ValueChangeHandler<Term>() {
			@Override
			public void onValueChange(ValueChangeEvent<Term> event) {
				setTerm(event.getValue());
				eventBus.fireEventFromSource(new TermSelectEvent(event.getValue()), CreateSubmissionView.this);
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
						ontology.setCreatedInCollectionId(collection.getId());
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
								eventBus.fireEvent(new CreateOntologyEvent(result));
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
					final OntologyClassSubmission submission = getClassSubmission();
					toOntologyService.createClassSubmission(collection, submission, new AsyncCallback<List<OntologyClassSubmission>>() {
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
						public void onSuccess(List<OntologyClassSubmission> result) {
							Alerter.stopLoading(box);
							eventBus.fireEvent(new CreateOntologyClassSubmissionEvent(result));
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
					final OntologyClassSubmission newSubmission = getClassSubmission();
					toOntologyService.removeClassSubmission(collection, selectedSubmission, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Alerter.failedToRemoveOntologyClassSubmission();
							Alerter.stopLoading(box);
						}
						@Override
						public void onSuccess(Void result) {
							eventBus.fireEvent(new RemoveOntologyClassSubmissionsEvent(selectedSubmission));
							toOntologyService.createClassSubmission(collection, newSubmission, new AsyncCallback<List<OntologyClassSubmission>>() {
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
								public void onSuccess(List<OntologyClassSubmission> result) {
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
						toOntologyService.updateClassSubmission(collection, submission, new AsyncCallback<List<OntologyClassSubmission>>() {
							@Override
							public void onFailure(Throwable caught) {
								Alerter.stopLoading(box);
								Alerter.failedToEditClass(caught);
							}
							@Override
							public void onSuccess(List<OntologyClassSubmission> result) {
								Alerter.stopLoading(box);
								eventBus.fireEvent(new UpdateOntologyClassSubmissionsEvent(submission));
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
						synonymsStore.add(new Synonym(box.getValue()));
					}
				});
			}
		});
		removeSynonymButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				for(Synonym remove : synonymsListView.getSelectionModel().getSelectedItems())
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
						Superclass superclass = new Superclass();
						if(box.getValue().startsWith("http")) {
							superclass.setIri(box.getValue());
						} else {
							superclass.setLabel(box.getValue());
						}
						addSuperClassToStore(superclass);
					}
				});
			}
		});
		removeSuperclassButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				for(Superclass remove : superclassListView.getSelectionModel().getSelectedItems()) {
					if((remove.getIri().equals(Type.ENTITY.getIRI()) && isEntityRadio.getValue())
							|| (remove.getIri().equals(Type.QUALITY.getIRI()) && !isEntityRadio.getValue())) {
						Alerter.cannotRemoveEntityOrQualitySuperclass();
					} else {
						superclassStore.remove(remove);
					}
				}
			}
		});
		clearSuperclassButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				clearSuperclassesExceptHigherLevelClass();
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
						PartOf partOf = new PartOf();
						if(box.getValue().startsWith("http")) {
							partOf.setIri(box.getValue());
						} else {
							partOf.setLabel(box.getValue());
						}
						addToPartOfStore(partOf);
					}
				});
			}
		});
		removePartOfButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				for(PartOf remove : partOfListView.getSelectionModel().getSelectedItems())
					partOfStore.remove(remove);
			}
		});
		clearPartOfButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				partOfStore.clear();
			}
		});
		
		ontologyComboBox.addValueChangeHandler(new ValueChangeHandler<Ontology>() {
			@Override
			public void onValueChange(ValueChangeEvent<Ontology> event) {
				eventBus.fireEvent(new SelectOntologyEvent(event.getValue()));
			}
		});
	}
	
	protected void addToPartOfStore(OntologyClassSubmission submission) {
		partOfStore.add(new PartOf(submission));
	}

	protected void addToPartOfStore(final PartOf partOf) {
		if(!partOf.hasIri())
			partOfStore.add(partOf);
		else if(!partOf.hasLabel()){
			final MessageBox box = Alerter.startLoading();
			toOntologyService.getClassLabel(collection, partOf.getIri(), new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToGetClassLabel();
					Alerter.stopLoading(box);
				}
				@Override
				public void onSuccess(String result) {
					partOf.setLabel(result);
					partOfStore.add(partOf);
					Alerter.stopLoading(box);
				}
			});
		} else {
			partOfStore.add(partOf);
		}
	}

	protected void addSuperClassToStore(OntologyClassSubmission submission) {
		superclassStore.add(new Superclass(submission));
	}

	private void addSuperClassToStore(final Superclass superclass) {
		if(!superclass.hasIri())
			superclassStore.add(superclass);
		else if(!superclass.hasLabel()) {
			final MessageBox box = Alerter.startLoading();
			toOntologyService.getClassLabel(collection, superclass.getIri(), new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {
					Alerter.failedToGetClassLabel();
					Alerter.stopLoading(box);
				}
				@Override
				public void onSuccess(String result) {
					superclass.setLabel(result);
					superclassStore.add(superclass);
					Alerter.stopLoading(box);
				}
			});
		} else {
			superclassStore.add(superclass);
		}
	}

	protected void addTypeToSupreclassStore(Type type) {
		superclassStore.add(new Superclass(type.getIRI(), type.getLabel()));
	}

	protected void removeSuperclass(Type type) {
		for(Superclass superclass : new LinkedList<Superclass>(superclassStore.getAll())) {
			if(superclass.getIri().equals(type.getIRI())) {
				superclassStore.remove(superclass);
			}
		}
	}

	protected void clearSuperclassesExceptHigherLevelClass() {
		superclassStore.clear();
		if(isEntityRadio.getValue())
			addTypeToSupreclassStore(Type.ENTITY);
		else
			addTypeToSupreclassStore(Type.QUALITY);
	}

	protected void setTerm(Term term) {
		termComboBox.setValue(term);
		categoryField.setValue(termComboBox.getValue().getCategory());
		submissionTermField.setValue(termComboBox.getValue().getTerm());
		if(term.hasIri())
			classIRIField.setValue(term.getIri());
	}

	protected void setSelectedSubmission(OntologyClassSubmission ontologyClassSubmission) {
		this.selectedSubmission = ontologyClassSubmission;
		if(selectedSubmission == null) {
			this.editButton.setEnabled(false);
			this.submitButton.setEnabled(true);
			this.obsoleteSubmitButton.setEnabled(false);
		} else {
			this.editButton.setEnabled(true);
			this.submitButton.setEnabled(false);
			this.obsoleteSubmitButton.setEnabled(true);
		}
	}
	
	protected void clearFields(boolean fireEvents) {
		this.termComboBox.setValue(null, false);
		this.submissionTermField.setValue("", false); 
		//this.ontologyComboBox.setValue(null, false);
		this.classIRIField.setValue("", false);
		this.clearSuperclassesExceptHigherLevelClass();
		this.definitionArea.setValue("", false);
		this.synonymsStore.clear();
		this.sourceField.setValue("", false);
		this.sampleArea.setValue("", false);
		this.partOfStore.clear();
	}

	protected void setOntologyClassSubmission(OntologyClassSubmission ontologyClassSubmission) {
		this.setSelectedSubmission(ontologyClassSubmission);
		this.termComboBox.setValue(ontologyClassSubmission.getTerm());
		this.submissionTermField.setValue(ontologyClassSubmission.getSubmissionTerm()); 
		if(ontologyClassSubmission.hasOntology())
			this.ontologyComboBox.setValue(ontologyClassSubmission.getOntology());
		this.classIRIField.setValue(ontologyClassSubmission.getClassIRI());
		this.definitionArea.setValue(ontologyClassSubmission.getDefinition());
		this.synonymsStore.clear();
		this.synonymsStore.addAll(ontologyClassSubmission.getSynonyms());
		this.sourceField.setValue(ontologyClassSubmission.getSource());
		this.sampleArea.setValue(ontologyClassSubmission.getSampleSentence());
		this.partOfStore.clear();
		for(PartOf partOf : ontologyClassSubmission.getPartOfs())
			this.addToPartOfStore(partOf);
		if(ontologyClassSubmission.getType() != null) {
			this.isEntityRadio.setValue(ontologyClassSubmission.getType().equals(Type.ENTITY));
			this.isQualityRadio.setValue(ontologyClassSubmission.getType().equals(Type.QUALITY));
		}
		superclassStore.clear();
		for(Superclass superclass : ontologyClassSubmission.getSuperclasses())
			this.addSuperClassToStore(superclass);
	}
	
	protected OntologyClassSubmission getClassSubmission() {
		LinkedList<PartOf> partOfs = new LinkedList<PartOf>(partOfStore.getAll());
		LinkedList<Superclass> superclasses = new LinkedList<Superclass>(superclassStore.getAll());		
		if(isQualityRadio.getValue()) 
			partOfs = new LinkedList<PartOf>();
		Type type = isEntityRadio.getValue() ? Type.ENTITY : Type.QUALITY;
		return new OntologyClassSubmission(collection.getId(), termComboBox.getValue(), submissionTermField.getValue(), 
				ontologyComboBox.getValue(), classIRIField.getValue(), superclasses,
				definitionArea.getValue(), new LinkedList<Synonym>(synonymsStore.getAll()), sourceField.getValue(), 
				sampleArea.getValue(), partOfs, Ontologize.user);
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
				Ontology select = ontologyToSelect;
				ontologiesStore.clear();
				ontologiesStore.addAll(result);
				if(select == null && ontologiesStore.size() == 1)
					select = ontologiesStore.get(0);
				if(select != null)
					ontologyComboBox.setValue(select);
			}
		});
	}

	@Override
	public Widget asWidget() {
		return vlc;
	}

}
