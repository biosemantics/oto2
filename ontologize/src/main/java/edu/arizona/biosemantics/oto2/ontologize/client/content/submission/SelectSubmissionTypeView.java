package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.Radio;

import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;

public class SelectSubmissionTypeView implements IsWidget {
	
	private VerticalLayoutContainer formContainer;
	private Radio isClassRadio = new Radio();
	private Radio isSynonymRadio = new Radio();
	private ToggleGroup classSynonymGroup;
	private EventBus eventBus;

	public SelectSubmissionTypeView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		formContainer = new VerticalLayoutContainer();
		isClassRadio.setBoxLabel("Class");
		isSynonymRadio.setBoxLabel("Synonym");
	    HorizontalPanel hp = new HorizontalPanel();
	    hp.add(isClassRadio);
	    hp.add(isSynonymRadio);
	    classSynonymGroup = new ToggleGroup();
	    classSynonymGroup.add(isClassRadio);
	    classSynonymGroup.add(isSynonymRadio);
	    formContainer.add(new FieldLabel(hp, "What do you want to submit this term as *"), new VerticalLayoutData(1, -1));
	    
	    bindEvents();
	}
	
	private void bindEvents() {
		
	}

	public boolean isClass() {
		return isClassRadio.getValue();
	}
	
	public boolean isSynonym() {
		return isSynonymRadio.getValue();
	}
	
	@Override
	public Widget asWidget() {
		return formContainer;
	}

	public void addHandler(ValueChangeHandler<Boolean> handler) {
		isClassRadio.addValueChangeHandler(handler);
	}
	
	public boolean validate() {
		return isClassRadio.getValue() || isSynonymRadio.getValue();
	}

	public void setEnabled(boolean enabled) {
		isClassRadio.setEnabled(enabled);
		isSynonymRadio.setEnabled(enabled);
	}

	public void setClass() {
		isClassRadio.setValue(true);
		isSynonymRadio.setValue(false);
	}

	public void setSynonym() {
		isClassRadio.setValue(false);
		isSynonymRadio.setValue(true);
	}

	public void clear() {
		this.isClassRadio.setValue(false);
		this.isSynonymRadio.setValue(false);
	}

}
