package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;

public class AddSuperclassDialog extends Dialog {
	
	private SuperclassesView superclassesView;

	public AddSuperclassDialog(EventBus eventBus) {
		this.setHeadingText("Add Superclass");
		this.setHeight(800);
		this.setWidth(800);
		this.setClosable(true);
		this.setOnEsc(true);
		this.setModal(true);
		
		superclassesView = new SuperclassesView(eventBus);
		
		VerticalLayoutContainer vlp = new VerticalLayoutContainer();
		vlp.getScrollSupport().setScrollMode(ScrollMode.AUTO);
		this.setWidget(vlp);
		
		vlp.add(superclassesView, new VerticalLayoutData(780, 740));
		
		bindEvents();
	}
	
	private void bindEvents() {
	}
	
	public String getValue() {
		return superclassesView.getValue();
	}
}