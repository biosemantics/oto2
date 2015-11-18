package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

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
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.TermTreeNode;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.TextTreeNode;

public class AddSynonymDialog extends Dialog {

	private TermsView termsView;
	private TextField termField;
	
	public AddSynonymDialog() {
		this.setHeadingText("Add Synonym");
		this.setHeight(400);
		this.setWidth(400);
		this.setClosable(true);
		this.setOnEsc(true);
		this.setModal(true);
		
		termsView = new TermsView(null);
		termField = new TextField();
		
		VerticalLayoutContainer vlp = new VerticalLayoutContainer();
		vlp.getScrollSupport().setScrollMode(ScrollMode.AUTO);
		this.setWidget(vlp);
		
		vlp.add(new FieldLabel(termsView, "Candidate Terms"), new VerticalLayoutData(1, 310));
		vlp.add(new FieldLabel(termField, "New Synonym"), new VerticalLayoutData(1, -1));
		
		bindEvents();
	}
	
	private void bindEvents() {
		termsView.addSelectionChangedHandler(new SelectionChangedHandler<TextTreeNode>() {
			@Override
			public void onSelectionChanged(SelectionChangedEvent<TextTreeNode> event) {
				TextTreeNode selection = event.getSelection().get(0);
				if(selection instanceof TermTreeNode) {
					termField.setValue(((TermTreeNode) selection).getTerm().getTerm());
				}
			}
		});
	}
	
	public String getValue() {
		return termField.getValue();
	}

	public void setCollection(Collection collection) {
		termsView.setCollection(collection);
	}
}
