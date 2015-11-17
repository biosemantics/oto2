package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.TermProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public class CopyOfSelectTermView implements IsWidget {
	
	private TermProperties termProperties = GWT.create(TermProperties.class);
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);
	
	private EventBus eventBus;
	
	private VerticalLayoutContainer formContainer;
	private ListStore<Term> termStore = new ListStore<Term>(termProperties.key());
	private ComboBox<Term> termComboBox;
	private TextField categoryField = new TextField();
	private TextField classIRIField = new TextField();
	private TextField submissionTermField = new TextField();
	protected Collection collection;
	private FieldValidator fieldValidator = new FieldValidator();

	public CopyOfSelectTermView(EventBus eventBus) {
		this.eventBus = eventBus;
		
	    formContainer = new VerticalLayoutContainer();
	    termComboBox = new ComboBox<Term>(termStore, termProperties.nameLabel());
	    formContainer.add(new FieldLabel(termComboBox, "Candiate Term"), new VerticalLayoutData(1, -1));
	    formContainer.add(new FieldLabel(submissionTermField, "Term *"), new VerticalLayoutData(1, -1));
	    submissionTermField.setAllowBlank(false);
	    submissionTermField.setAutoValidate(true);
	    formContainer.add(new FieldLabel(categoryField, "Category"), new VerticalLayoutData(1, -1));
	    categoryField.setEnabled(false);
	    formContainer.add(new FieldLabel(classIRIField, "Class IRI"), new VerticalLayoutData(1, -1));	    
	    bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.Handler() {
			@Override
			public void onSelect(TermSelectEvent event) {
				setTerm(event.getTerm());
			}
		});
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				CopyOfSelectTermView.this.collection = event.getCollection();
				refreshTerms();
			}
		});
		termComboBox.addValueChangeHandler(new ValueChangeHandler<Term>() {
			@Override
			public void onValueChange(ValueChangeEvent<Term> event) {
				setTerm(event.getValue());
				eventBus.fireEventFromSource(new TermSelectEvent(event.getValue()), CopyOfSelectTermView.this);
			}
		});
	}

	protected void refreshTerms() {
		termStore.clear();
		termStore.addAll(collection.getTerms());
	}

	protected void setTerm(Term term) {
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
		return this.classIRIField.getText().trim();
	}

	public Term getTerm() {
		return termComboBox.getValue();
	}

	public boolean validate() {
		return fieldValidator.validate(formContainer.iterator());
	}

}
