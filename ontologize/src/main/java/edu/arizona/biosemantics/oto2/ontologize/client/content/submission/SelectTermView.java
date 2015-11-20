package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.Validator;

import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSynonymEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.UpdateOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.OntologyProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.TermProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.SynonymClass;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public class SelectTermView implements IsWidget {
	
	private TermProperties termProperties = GWT.create(TermProperties.class);
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);
	
	private EventBus eventBus;
	
	private VerticalLayoutContainer formContainer;
	private ListStore<Term> termStore = new ListStore<Term>(termProperties.key());
	private ComboBox<Term> termComboBox;
	private TextField categoryField = new TextField();
	private SelectSubmissionTypeView selectSubmissionTypeView;
	private TextField classIRIField = new TextField();
	private ComboBox<SynonymClass> classIRIComboBox;
	private FieldLabel classIRIFieldLabel;
	private FieldLabel classIRIComboBoxFieldLabel;
	private ListStore<SynonymClass> classIRIStore = new ListStore<SynonymClass>(new ModelKeyProvider<SynonymClass>() {
		@Override
		public String getKey(SynonymClass item) {
			return item.getIri();
		}
	});
	private TextField submissionTermField = new TextField();
	protected Collection collection;
	private FieldValidator fieldValidator = new FieldValidator();
	protected Ontology ontology;
	private boolean bindTermSelectEvent;
	private boolean showClassIRI;
	private SelectOntologyView selectOntologyView;
	private boolean showIRITextFieldForSynonym;
	private SelectTypeView selectTypeView;
	private boolean showDefaultSuperclasses;

	public SelectTermView(EventBus eventBus, boolean bindTermSelectEvent, boolean enabledSubmissionTermField, 
			boolean enabledClassIRIFields, boolean showClassIRI, boolean showOntology, boolean enableOntology, boolean showIRITextFieldForSynonym, 
			boolean showDefaultSuperclasses) {
		this.eventBus = eventBus;
		this.bindTermSelectEvent = bindTermSelectEvent;
		this.showClassIRI = showClassIRI;
		this.showIRITextFieldForSynonym = showIRITextFieldForSynonym;
		this.showDefaultSuperclasses = showDefaultSuperclasses;
		
	    formContainer = new VerticalLayoutContainer();
	    termComboBox = new ComboBox<Term>(termStore, termProperties.nameLabel());
	    formContainer.add(new FieldLabel(termComboBox, "Candiate Term"), new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(submissionTermField, "Term *"), new VerticalLayoutData(1, -1));
	    submissionTermField.setAllowBlank(false);
	    submissionTermField.setAutoValidate(true);
	    submissionTermField.setEnabled(enabledSubmissionTermField);
	    formContainer.add(new FieldLabel(categoryField, "Category"), new VerticalLayoutData(1, -1));
	    categoryField.setEnabled(false);
	    
	    if(showOntology) {
			selectOntologyView = new SelectOntologyView(eventBus);
		    selectOntologyView.setEnabled(enableOntology);
		    formContainer.add(new FieldLabel(selectOntologyView, "Ontology *"), new VerticalLayoutData(1, -1));
	    }
	    
	   	selectSubmissionTypeView = new SelectSubmissionTypeView(eventBus);
	   	formContainer.add(selectSubmissionTypeView);
	    classIRIComboBox = new ComboBox<SynonymClass>(classIRIStore, new LabelProvider<SynonymClass>() {
			@Override
			public String getLabel(SynonymClass synonymClass) {
				return (synonymClass.hasLabel() ? synonymClass.getLabel() : synonymClass.toString());
			}
		});
		classIRIComboBox.getListView().setCell(new AbstractCell() {
			@Override
			public void render(Context context, Object value, SafeHtmlBuilder sb) {
				if(value instanceof SynonymClass) {
					SynonymClass synonymClass = (SynonymClass)value;
					sb.append(SafeHtmlUtils.fromTrustedString("<div qtip=\"" + synonymClass.toString() + "\">" + (synonymClass.hasLabel() ? synonymClass.getLabel() : synonymClass.toString()) + "</div>"));
				}
			}
		});
	    classIRIComboBox.setAllowBlank(false);
	    classIRIComboBox.setAutoValidate(true);
		classIRIFieldLabel = new FieldLabel(classIRIField, "Class IRI");
		classIRIField.setAutoValidate(true);
		classIRIField.setValidateOnBlur(true);
		//classIRIField.setValidationDelay(1000);
		classIRIComboBox.setEnabled(enabledClassIRIFields);
		classIRIFieldLabel.setEnabled(enabledClassIRIFields);
		classIRIComboBoxFieldLabel = new FieldLabel(classIRIComboBox, "Class *");
		
		selectTypeView = new SelectTypeView(eventBus);
		
	    bindEvents();
	}
	
	private void bindEvents() {	
		if(bindTermSelectEvent)
			eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.Handler() {
				@Override
				public void onSelect(TermSelectEvent event) {
					setTerm(event.getTerm());
				}
			});
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				SelectTermView.this.collection = event.getCollection();
				
				toOntologyService.getLocalOntologies(collection, new AsyncCallback<List<Ontology>>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.getOntologiesFailed(caught);
					}
					@Override
					public void onSuccess(List<Ontology> result) {
						SelectTermView.this.ontology = result.get(0);
						fillClassIRIStore(ontology);
					}
				});
				
				refreshTerms();
			}
		});
		classIRIField.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				toOntologyService.isSupportedIRI(collection, event.getValue().trim(), new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToCheckIRI(caught);
					}
					@Override
					public void onSuccess(Boolean result) {
						if(!result) {
							classIRIField.setValue("", false);
							Alerter.unsupportedIRI();
						}
					}
				});
			}
		});
		/*classIRIField.addValidator(new Validator<String>() {
			@Override
			public List<EditorError> validate(Editor<String> editor, String value) {
				toOntologyService.isSupportedIRI(value, new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						
					}

					@Override
					public void onSuccess(Boolean result) {
						
					}
				});
			}
		});*/
		termComboBox.addValueChangeHandler(new ValueChangeHandler<Term>() {
			@Override
			public void onValueChange(ValueChangeEvent<Term> event) {
				setTerm(event.getValue());
				eventBus.fireEventFromSource(new TermSelectEvent(event.getValue()), SelectTermView.this);
			}
		});
		eventBus.addHandler(SelectSynonymEvent.TYPE, new SelectSynonymEvent.Handler() {
			@Override
			public void onSelect(SelectSynonymEvent event) {
				classIRIComboBox.setValue(new SynonymClass(event.getSubmission()), false);
			}
		});
		eventBus.addHandler(CreateOntologyClassSubmissionEvent.TYPE, new CreateOntologyClassSubmissionEvent.Handler() {
			@Override
			public void onSubmission(CreateOntologyClassSubmissionEvent event) {
				for(OntologyClassSubmission submission : event.getClassSubmissions())
					if(!submission.getOntology().isBioportalOntology())
						classIRIStore.add(new SynonymClass(submission));
			}
		});
		eventBus.addHandler(UpdateOntologyClassSubmissionsEvent.TYPE, new UpdateOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onUpdate(UpdateOntologyClassSubmissionsEvent event) {
				for(OntologyClassSubmission submission : event.getOntologyClassSubmissions())
					if(!submission.getOntology().isBioportalOntology())
						classIRIStore.update(new SynonymClass(submission));
			}
		});
		eventBus.addHandler(RemoveOntologyClassSubmissionsEvent.TYPE, new RemoveOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onRemove(RemoveOntologyClassSubmissionsEvent event) {
				for(OntologyClassSubmission submission : event.getOntologyClassSubmissions())
					if(!submission.getOntology().isBioportalOntology())
						classIRIStore.remove(new SynonymClass(submission));
			}
		});
	}
	
	protected void fillClassIRIStore(Ontology ontology) {
		final MessageBox box = Alerter.startLoading();
		toOntologyService.getClassSubmissions(collection, ontology, new AsyncCallback<List<OntologyClassSubmission>>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.stopLoading(box);
			}
			@Override
			public void onSuccess(List<OntologyClassSubmission> result) {
				classIRIStore.clear();
				for(OntologyClassSubmission submission : result)
					classIRIStore.add(new SynonymClass(submission));
				Alerter.stopLoading(box);
			}
		});
	}

	protected void refreshTerms() {
		termStore.clear();
		termStore.addAll(collection.getTerms());
	}

	protected void setTerm(Term term) {
		clear();
		termComboBox.setValue(term);
		categoryField.setValue(termComboBox.getValue().getCategory());
		submissionTermField.setValue(termComboBox.getValue().getTerm());
		if(term.hasIri()) {
			classIRIField.setValue(term.getIri());
			classIRIField.setEnabled(false);
		} else {
			classIRIField.setValue("");
			classIRIField.setEnabled(true);
		}
	}

	@Override
	public Widget asWidget() {
		return formContainer;
	}
	
	public String getSubmissionTerm() {
		return submissionTermField.getText().trim();
	}
	
	public String getClassIRI() {
		if(isSynonym() && !showIRITextFieldForSynonym)
			return this.classIRIComboBox.getValue().getIri().trim();
		return this.classIRIField.getText().trim();
	}

	public Term getTerm() {
		return termComboBox.getValue();
	}

	public boolean validate() {
		boolean result = true;
		if(selectSubmissionTypeView.asWidget().isAttached())
			result &= selectSubmissionTypeView.validate();
		if(classIRIField.isAttached()) {
			if(classIRIField.getValue().isEmpty()) 
				return true;
			else {
				if(!classIRIField.getText().startsWith("http"))
					return false;
				else {
					//can't do a async call to check for validity of iri here: Do when text field was changed with a load
				}
			}
			result &= (classIRIFieldLabel.getText().isEmpty() ? true : classIRIField.getText().startsWith("http"));
		}
		result &= fieldValidator.validate(formContainer.iterator());
		return result;
	}
	
	public boolean isClass() {
		return selectSubmissionTypeView.isClass();
	}
	
	public boolean isSynonym() {
		return selectSubmissionTypeView.isSynonym();
	}
	
	public void addSubmissionTypeHandler(ValueChangeHandler<Boolean> handler) {
		selectSubmissionTypeView.addHandler(handler);
	}

	public void setClassSubmission() {
		formContainer.remove(classIRIComboBoxFieldLabel);
		if(showClassIRI)
			formContainer.add(classIRIFieldLabel, new VerticalLayoutData(1, -1));
		if(showDefaultSuperclasses)
			formContainer.add(selectTypeView);
		formContainer.forceLayout();
	}

	public void setSynonymSubmission() {
		formContainer.remove(selectTypeView);
		formContainer.remove(classIRIFieldLabel);
		if(showClassIRI)
			formContainer.add(classIRIComboBoxFieldLabel, new VerticalLayoutData(1, -1));
		else if(showIRITextFieldForSynonym)
			formContainer.add(classIRIFieldLabel, new VerticalLayoutData(1, -1));
		formContainer.forceLayout();
	}
	
	public void setEnabledTermComboBox(boolean enabled) {
		this.termComboBox.setEnabled(false);
	}
	
	public void setEnabledSubmissionTermField(boolean enabled) {
		this.submissionTermField.setEnabled(enabled);
	}
	
	public void setEnabledSubmissionTypeField(boolean enabled) {
		this.selectSubmissionTypeView.setEnabled(enabled);
	}
	
	public void setEnabledClassIRIField(boolean enabled) {
		this.classIRIField.setEnabled(enabled);
	}
	
	public void setEnabledClassIRIComboBox(boolean enabled) {
		this.classIRIComboBox.setEnabled(enabled);
	}

	public void setOntologyClassSubmission(OntologyClassSubmission submission) {
		this.setClassSubmission();
		this.termComboBox.setValue(submission.getTerm());
		if(submission.hasTerm())
			this.categoryField.setValue(submission.getTerm().getCategory());
		this.selectSubmissionTypeView.setClass();
		this.classIRIField.setValue(submission.getClassIRI());
		this.submissionTermField.setValue(submission.getSubmissionTerm());
		this.selectTypeView.setType(submission.getType());
	}

	public void setOntologySynonymSubmission(OntologySynonymSubmission submission) {
		this.setSynonymSubmission();
		this.termComboBox.setValue(submission.getTerm());
		this.categoryField.setValue(submission.getTerm().getCategory());
		this.selectSubmissionTypeView.setSynonym();
		this.classIRIComboBox.setValue(new SynonymClass(submission.getClassIRI(), submission.getClassLabel()));
		this.submissionTermField.setValue(submission.getSubmissionTerm());
		this.selectTypeView.setType(submission.getType());
	}

	public void clear() {
		this.selectTypeView.clear();
		this.formContainer.remove(this.selectTypeView);
		this.formContainer.remove(this.classIRIComboBoxFieldLabel);
		this.formContainer.remove(this.classIRIFieldLabel);
		this.termComboBox.setValue(null);
		this.categoryField.setValue("");
		this.selectSubmissionTypeView.clear();
		this.classIRIField.setValue("");
		this.classIRIComboBox.setValue(null);
	}

	public Ontology getOntology() {
		return selectOntologyView.getOntology();
	}
	
	public void addSelectTypeHandler(ValueChangeHandler<Boolean> handler) {
		selectTypeView.addHandler(handler);
	}

	public Type getType() {
		return selectTypeView.getType();
	}
}
