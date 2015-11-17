package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

import edu.arizona.biosemantics.oto2.ontologize.client.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.OntologyProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.TermProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOfProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.SuperclassProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.SynonymProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public class CreateSubmissionView2 implements IsWidget {

	private SynonymProperties synonymProperties = GWT.create(SynonymProperties.class);
	private PartOfProperties partOfProperties = GWT.create(PartOfProperties.class);
	private SuperclassProperties superclassProperties = GWT.create(SuperclassProperties.class);
	private OntologyProperties ontologyProperties = GWT.create(OntologyProperties.class);
	private TermProperties termProperties = GWT.create(TermProperties.class);
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);
	
	private EventBus eventBus;
	private VerticalLayoutContainer formContainer;
	private SelectTermView selectTermView;
	private SelectTypeView selectTypeView;
	private SelectSuperclassView selectSuperclassView;
	private SelectPartOfView selectPartOfView;

	public CreateSubmissionView2(EventBus eventBus) {
		this.eventBus = eventBus;
		
		selectTermView = new SelectTermView(eventBus);
		selectTypeView = new SelectTypeView(eventBus);
		selectPartOfView = new SelectPartOfView(eventBus);
		
	    formContainer = new VerticalLayoutContainer();
	    formContainer.add(selectTermView);
	    	    
	    bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.Handler() {
			@Override
			public void onSelect(TermSelectEvent event) {
				Object o = event.getSource();
				System.out.println(event.getSource());
				//if(event.getSource().equals(selectTermView)) {
					setTermSelected();
				//}
			}
		});
		
		selectTypeView.addHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				setTypeSelected(selectTypeView.getType());
			}
		});
	}

	protected void setTypeSelected(Type type) {
		if(selectSuperclassView != null)
			formContainer.remove(selectSuperclassView);
		selectSuperclassView = new SelectSuperclassView(eventBus);
		selectSuperclassView.setDefaultSuperclass(type);
		formContainer.add(selectSuperclassView);
		formContainer.add(selectPartOfView);
	}

	protected void setTermSelected() {
		formContainer.remove(selectPartOfView);
		if(selectTypeView != null)
			formContainer.remove(selectTypeView);
		if(selectSuperclassView != null)
			formContainer.remove(selectSuperclassView);
		formContainer.add(selectTypeView);
	}

	@Override
	public Widget asWidget() {
		return formContainer;
	}

}
