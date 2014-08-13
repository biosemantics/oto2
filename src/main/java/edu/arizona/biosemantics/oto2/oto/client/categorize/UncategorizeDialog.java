package edu.arizona.biosemantics.oto.oto.client.categorize;

import java.util.List;

import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.CategorizeCopyRemoveTermEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class UncategorizeDialog extends MessageBox {
	
	public UncategorizeDialog(final EventBus eventBus, final Label sourceLabel, final Term term, 
			final List<Label> labels) {
		super("Remove all categorizations of term?", "");
		setPredefinedButtons(PredefinedButton.YES,
				PredefinedButton.NO, PredefinedButton.CANCEL);
		setIcon(MessageBox.ICONS.question());
		setMessage("You are uncategorizing a term with multiple categories. Would you like to "
				+ "remove it from all its categories?");
		getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				for(Label label : labels)
					label.uncategorizeTerm(term);
				eventBus.fireEvent(new TermUncategorizeEvent(term, labels));
			}
		});
		getButton(PredefinedButton.NO).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				sourceLabel.uncategorizeTerm(term);
				eventBus.fireEvent(new CategorizeCopyRemoveTermEvent(term, sourceLabel));
			}
		});
		getButton(PredefinedButton.CANCEL).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				hide();
			}
		});
		show();
	}
	
}
