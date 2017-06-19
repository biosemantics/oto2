package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSampleEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSourceEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;

public class SelectMetadataView implements IsWidget {
	
	private VerticalLayoutContainer formContainer;
	private EventBus eventBus;
	
	private TextArea definitionArea = new TextArea();
	private TextArea sampleArea = new TextArea();
	private TextField sourceField = new TextField();
	private FieldValidator fieldValidator = new FieldValidator();
	private boolean bindTermSelectEvent;

	public SelectMetadataView(EventBus eventBus, boolean bindTermSelectEvent) {
		this.eventBus = eventBus;
		this.bindTermSelectEvent = bindTermSelectEvent;
		
		formContainer = new VerticalLayoutContainer();
	    
	    bindEvents();
	}
	
	private void bindEvents() {
		if(bindTermSelectEvent)
			eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.Handler() {
				@Override
				public void onSelect(TermSelectEvent event) {
					clear();
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
	}

	public void clear() {
		this.definitionArea.clear();
		this.sourceField.clear();
		this.sampleArea.clear();
	}

	@Override
	public Widget asWidget() {
		return formContainer;
	}
	
	public String getDefinition() {
		return definitionArea.getText().trim();
	}
	
	public String getSample() {
		return sampleArea.getText().trim();
	}
	
	public String getSource() {
		return sourceField.getText().trim();
	}

	public boolean validate() {
		return fieldValidator.validate(formContainer.iterator());
	}

	public void setClassSubmission() {
		formContainer.clear();
		formContainer.add(new FieldLabel(definitionArea, "Definition"), new VerticalLayoutData(1, 50));
	    formContainer.add(new FieldLabel(sampleArea, "Sample Sentence"), new VerticalLayoutData(1, 50));
	    formContainer.add(new FieldLabel(sourceField, "Source"), new VerticalLayoutData(1, -1));
	    formContainer.forceLayout();
	}

	public void setSynonymSubmission() {
		formContainer.clear();
	    formContainer.add(new FieldLabel(sampleArea, "Sample Sentence"), new VerticalLayoutData(1, 50));
	    formContainer.add(new FieldLabel(sourceField, "Source"), new VerticalLayoutData(1, -1));
	    formContainer.forceLayout();
	}

	public void setOntologySynonymSubmission(OntologySynonymSubmission submission) {
		this.setSynonymSubmission();
		sampleArea.setValue(submission.getSampleSentence());
		sourceField.setValue(submission.getSource());
	}

	public void setOntologyClassSubmission(OntologyClassSubmission submission) {
		this.setClassSubmission();
		definitionArea.setValue(submission.getDefinition());
		sampleArea.setValue(submission.getSampleSentence());
		sourceField.setValue(submission.getSource());
	}
}