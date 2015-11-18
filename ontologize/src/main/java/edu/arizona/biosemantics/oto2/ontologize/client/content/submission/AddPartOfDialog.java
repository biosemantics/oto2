package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import edu.arizona.biosemantics.oto2.ontologize.client.content.candidates.TermsView;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.TermTreeNode;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.TextTreeNode;

public class AddPartOfDialog extends Dialog {
	private PartsView partsView;

	public AddPartOfDialog(EventBus eventBus) {
		this.setHeadingText("Add Part Of");
		this.setHeight(800);
		this.setWidth(800);
		this.setClosable(true);
		this.setOnEsc(true);
		this.setModal(true);
		
		partsView = new PartsView(eventBus);
		
		VerticalLayoutContainer vlp = new VerticalLayoutContainer();
		vlp.getScrollSupport().setScrollMode(ScrollMode.AUTO);
		this.setWidget(vlp);
		
		vlp.add(partsView, new VerticalLayoutData(780, 740));
		
		bindEvents();
	}
	
	private void bindEvents() {
	}
	
	public PartOf getValue() {
		return partsView.getValue();
	}
}
