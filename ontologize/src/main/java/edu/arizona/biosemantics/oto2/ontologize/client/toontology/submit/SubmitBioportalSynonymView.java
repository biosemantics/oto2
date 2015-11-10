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
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
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
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.SynonymProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.ClassExistsException;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.OntologyNotFoundException;

public class SubmitBioportalSynonymView implements IsWidget {
	
	private SynonymProperties synonymProperties = GWT.create(SynonymProperties.class);
	private OntologyProperties ontologyProperties = GWT.create(OntologyProperties.class);
	private TermProperties termProperties = GWT.create(TermProperties.class);
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);
	private EventBus eventBus;
	private Collection collection;
	
	private ListStore<Ontology> ontologiesStore = new ListStore<Ontology>(ontologyProperties.key());
	private ListStore<Term> termStore = new ListStore<Term>(termProperties.key());
	private TextButton editButton = new TextButton("Edit");
	private TextButton submitButton = new TextButton("Save as New");
	private TextButton obsoleteSubmitButton = new TextButton("Cancel and Save as New");
	private TextField submissionTermField = new TextField();
	private TextField categoryField = new TextField();
	private ComboBox<Ontology> ontologyComboBox;
	private TextField classIRIField = new TextField();
	private TextField sourceField = new TextField();
	private TextArea sampleArea = new TextArea();
	
	private ListView<Synonym, String> synonymsListView;
	private ListStore<Synonym> synonymsStore;
	private TextButton addSynonymButton = new TextButton("Add");
	private TextButton removeSynonymButton = new TextButton("Remove");
	private TextButton clearSynonymButton = new TextButton("Clear");
	
	private ComboBox<Term> termComboBox;
	private VerticalLayoutContainer vlc;
	private OntologySynonymSubmission selectedSubmission;
	private VerticalLayoutContainer formContainer;
	
	public SubmitBioportalSynonymView(EventBus eventBus) {
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
	    
	    formContainer = new VerticalLayoutContainer();
	    formContainer.add(new FieldLabel(termComboBox, "Candiate Term"), new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(submissionTermField, "Term *"), new VerticalLayoutData(1, -1));
	    submissionTermField.setAllowBlank(false);
	    submissionTermField.setAutoValidate(true);
	    formContainer.add(new FieldLabel(categoryField, "Category"), new VerticalLayoutData(1, -1));
	    VerticalLayoutContainer ontologyVlc = new VerticalLayoutContainer();
	    ontologyVlc.add(ontologyComboBox, new VerticalLayoutData(1, -1));
	    ontologyComboBox.setAllowBlank(false);
	    ontologyComboBox.setAutoValidate(true);
	    formContainer.add(new FieldLabel(ontologyVlc, "Ontology *"), new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(classIRIField, "Class IRI *"), new VerticalLayoutData(1, -1));
	    classIRIField.setAllowBlank(false);
	    classIRIField.setAutoValidate(true);
	    
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
		if(!selectedSubmission.getClassIRI().equals(classIRIField.getValue())) {
			Alerter.alertCantModify("class IRI");
			return false;
		}
		if(!selectedSubmission.getSubmissionTerm().equals(submissionTermField.getValue())) {
			Alerter.alertCantModify("submission term");
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
				classIRIField.setValue(event.getSubmission().getClassIRI(), false);
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
				SubmitBioportalSynonymView.this.collection = event.getCollection();
				
				initCollection();
			}
		});
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.Handler() {
			@Override
			public void onSelect(TermSelectEvent event) {
				if(!event.getSource().equals(SubmitBioportalSynonymView.this)) {
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
				eventBus.fireEventFromSource(new TermSelectEvent(event.getValue()), SubmitBioportalSynonymView.this);
			}
		});		
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
					toOntologyService.removeSynonymSubmission(collection, selectedSubmission, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Alerter.failedToRemoveOntologyClassSubmission();
							Alerter.stopLoading(box);
						}
						@Override
						public void onSuccess(Void result) {
							eventBus.fireEvent(new RemoveOntologySynonymSubmissionsEvent(selectedSubmission));
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
						toOntologyService.updateSynonymSubmission(collection, submission, new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Alerter.stopLoading(box);
								Alerter.failedToEditClass(caught);
							}
							@Override
							public void onSuccess(Void result) {
								Alerter.stopLoading(box);
								eventBus.fireEvent(new UpdateOntologySynonymsSubmissionsEvent(submission));
							}
						});
					}
				} else {
					Alerter.alertInvalidForm();
				}
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
		this.classIRIField.setValue("", false);
		this.synonymsStore.clear();
		this.sourceField.setValue("", false);
		this.sampleArea.setValue("", false);
	}

	protected void setOntologySynonymSubmission(OntologySynonymSubmission ontologySynonymSubmission) {
		this.selectedSubmission = ontologySynonymSubmission;
		this.termComboBox.setValue(ontologySynonymSubmission.getTerm());
		this.submissionTermField.setValue(ontologySynonymSubmission.getSubmissionTerm()); 
		if(ontologySynonymSubmission.hasOntology())
			this.ontologyComboBox.setValue(ontologySynonymSubmission.getOntology());
		this.classIRIField.setValue(ontologySynonymSubmission.getClassIRI());
		this.synonymsStore.clear();
		this.synonymsStore.addAll(ontologySynonymSubmission.getSynonyms());
		this.sourceField.setValue(ontologySynonymSubmission.getSource());
		this.sampleArea.setValue(ontologySynonymSubmission.getSampleSentence());
	}

	protected OntologySynonymSubmission getSynonymSubmission() {
		return new OntologySynonymSubmission(collection.getId(), termComboBox.getValue(), submissionTermField.getValue(), 
				ontologyComboBox.getValue(), classIRIField.getValue(),
				new LinkedList<Synonym>(synonymsStore.getAll()),
				sourceField.getValue(), sampleArea.getValue(), 
				Ontologize.user);
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
		toOntologyService.getPermanentOntologies(collection, new AsyncCallback<List<Ontology>>() {
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
