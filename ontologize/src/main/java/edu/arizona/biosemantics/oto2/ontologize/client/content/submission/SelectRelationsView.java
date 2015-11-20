package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

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
	private boolean showPartOfRelations;
	private boolean showDefaultSuperclasses;
	
	public SelectRelationsView(EventBus eventBus, boolean bindTermSelectEvent, boolean showPartOfRelations, boolean showDefaultSuperclasses) {
		this.eventBus = eventBus;
		this.bindTermSelectEvent = bindTermSelectEvent;
		this.showPartOfRelations = showPartOfRelations;
		this.showDefaultSuperclasses = showDefaultSuperclasses;
		
		formContainer = new VerticalLayoutContainer();
		selectTypeView = new SelectTypeView(eventBus);
		selectSuperclassView = new SelectSuperclassView(eventBus, showDefaultSuperclasses);
		selectPartOfView = new SelectSinglePartOfView(eventBus);
		selectSynonymsView = new SelectSynonymsView(eventBus);
	    
		if(!showDefaultSuperclasses)
			addTypeDependentFormElements(null);
		
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
		if(showDefaultSuperclasses) {
			formContainer.remove(selectSuperclassView);
			formContainer.remove(selectPartOfView);
			formContainer.remove(selectSynonymsView);
		}
	}

	protected void setTypeSelected(Type type) {
		if(showDefaultSuperclasses)
			this.selectSuperclassView.setSubmissionType(type);
		this.selectPartOfView.setSubmissionType(type);
		this.selectSynonymsView.setSubmissionType(type);
		addTypeDependentFormElements(type);
	}

	private void addTypeDependentFormElements(Type type) {
		formContainer.add(selectSuperclassView);
		if(type != null) {
			if(type.equals(Type.ENTITY)) {
				if(showPartOfRelations)
					formContainer.add(selectPartOfView);
			} else {
				formContainer.remove(selectPartOfView);
			}
		} else {
			if(showPartOfRelations)
				formContainer.add(selectPartOfView);
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
		if(showDefaultSuperclasses) {
			formContainer.clear();
			formContainer.add(selectTypeView);
			formContainer.forceLayout();
		}
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