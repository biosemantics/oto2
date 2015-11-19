package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

import edu.arizona.biosemantics.oto2.ontologize.client.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;

public class SelectRelationsView implements IsWidget {
	
	private VerticalLayoutContainer formContainer;
	private EventBus eventBus;
	private SelectTypeView selectTypeView;
	private SelectSuperclassView selectSuperclassView;
	private SelectSinglePartOfView selectPartOfView;
	private SelectSynonymsView selectSynonymsView;
	private FieldValidator fieldValidator = new FieldValidator();
	private boolean bindTermSelectEvent;
	
	public SelectRelationsView(EventBus eventBus, boolean bindTermSelectEvent) {
		this.eventBus = eventBus;
		this.bindTermSelectEvent = bindTermSelectEvent;
		
		formContainer = new VerticalLayoutContainer();
		selectTypeView = new SelectTypeView(eventBus);
		selectSuperclassView = new SelectSuperclassView(eventBus);
		selectPartOfView = new SelectSinglePartOfView(eventBus);
		selectSynonymsView = new SelectSynonymsView(eventBus);
	    
	    bindEvents();
	}
	
	private void bindEvents() {
		selectTypeView.addHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				setTypeSelected(selectTypeView.getType());
			}
		});
		if(bindTermSelectEvent) {
			eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.Handler() {
				@Override
				public void onSelect(TermSelectEvent event) {
					clear();
				}
			});
		}
	}
	
	public void clear() {
		selectTypeView.clear();
		selectSuperclassView.clearSuperclassesExceptHigherLevelClass();
		selectPartOfView.clear();
		selectSynonymsView.clear();
		formContainer.remove(selectSuperclassView);
		formContainer.remove(selectPartOfView);
		formContainer.remove(selectSynonymsView);
	}

	protected void setTypeSelected(Type type) {
		this.selectSuperclassView.setSubmissionType(type);
		this.selectPartOfView.setSubmissionType(type);
		this.selectSynonymsView.setSubmissionType(type);
		formContainer.add(selectSuperclassView);
		if(type.equals(Type.ENTITY)) {
			formContainer.add(selectPartOfView);
		} else {
			formContainer.remove(selectPartOfView);
		}
		formContainer.add(selectSynonymsView);
	}

	@Override
	public Widget asWidget() {
		return formContainer;
	}

	public List<PartOf> getPartOfs() {
		return selectPartOfView.getPartsOfs();
	}

	public List<Superclass> getSuperclasses() {
		return selectSuperclassView.getSuperclasses();
	}

	public Type getType() {
		return selectTypeView.getType();
	}

	public List<Synonym> getSynonyms() {
		return selectSynonymsView.getSynonyms();
	}
	
	public boolean validate() {
		boolean result = true;
		if(selectTypeView.asWidget().isAttached())
			result &= selectTypeView.validate();
		result &= fieldValidator.validate(formContainer.iterator());
		return result;
	}

	public void setClassSubmission() {
		formContainer.clear();
	    formContainer.add(selectTypeView);
		formContainer.forceLayout();
	}

	public void setSynonymSubmission() {
		formContainer.clear();
		formContainer.add(selectSynonymsView);
		formContainer.forceLayout();
	}

	public void setOntologySynonymSubmission(OntologySynonymSubmission submission) {
		this.setSynonymSubmission();
		this.selectSynonymsView.setSynonyms(submission.getSynonyms());
	}

	public void setOntologyClassSubmission(OntologyClassSubmission submission) {
		this.setClassSubmission();
		this.selectTypeView.setType(submission.getType());
		this.setTypeSelected(submission.getType());
		this.selectSuperclassView.setSuperclasses(submission.getSuperclasses());
		this.selectPartOfView.setPartOfs(submission.getPartOfs());
		this.selectSynonymsView.setSynonyms(submission.getSynonyms());
	}
	
}