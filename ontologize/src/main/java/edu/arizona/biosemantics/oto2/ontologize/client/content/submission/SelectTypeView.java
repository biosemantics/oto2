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

public class SelectTypeView implements IsWidget {
	
	private VerticalLayoutContainer formContainer;
	private Radio isEntityRadio = new Radio();
	private Radio isQualityRadio = new Radio();
	private ToggleGroup entityQualityGroup;
	private EventBus eventBus;

	public SelectTypeView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		formContainer = new VerticalLayoutContainer();
		isEntityRadio.setBoxLabel("Is material anatomical entity");
	    isQualityRadio.setBoxLabel("Is quality");
	    HorizontalPanel hp = new HorizontalPanel();
	    hp.add(isEntityRadio);
	    hp.add(isQualityRadio);
	    entityQualityGroup = new ToggleGroup();
	    entityQualityGroup.add(isEntityRadio);
	    entityQualityGroup.add(isQualityRadio);
	    formContainer.add(new FieldLabel(hp, "Type *"), new VerticalLayoutData(1, -1));
	    
	    bindEvents();
	}
	
	private void bindEvents() {
		
	}

	public boolean isEntity() {
		return isEntityRadio.getValue();
	}
	
	public boolean isQuality() {
		return !isEntityRadio.getValue();
	}
	
	@Override
	public Widget asWidget() {
		return formContainer;
	}

	public void addHandler(ValueChangeHandler<Boolean> handler) {
		isEntityRadio.addValueChangeHandler(handler);
		isQualityRadio.addValueChangeHandler(handler);
	}

	public Type getType() {
		if(isEntity())
			return Type.ENTITY;
		return Type.QUALITY;
	}
	
	public boolean validate() {
		return isEntityRadio.getValue() || isQualityRadio.getValue();
	}

	public void setType(Type type) {
		if(type != null) {
			if(type.equals(Type.ENTITY)) {
				setEntity();
			} else if(type.equals(Type.QUALITY)) {
				setQuality();
			}
		}
	}

	public void setQuality() {
		isEntityRadio.setValue(false, true);
		isQualityRadio.setValue(true, true);
	}

	public void setEntity() {
		isEntityRadio.setValue(true, true);
		isQualityRadio.setValue(false, true);
	}

	public void clear() {
		isEntityRadio.setValue(false);
		isQualityRadio.setValue(false);
	}

}
