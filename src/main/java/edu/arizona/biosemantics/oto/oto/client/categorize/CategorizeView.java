package edu.arizona.biosemantics.oto.oto.client.categorize;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto.oto.client.categorize.LabelPortlet.LabelInfoContainer;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelCreateEvent;
import edu.arizona.biosemantics.oto.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;

public class CategorizeView extends BorderLayoutContainer implements IsWidget {

	public class LabelsMenu extends Menu {
		public LabelsMenu() {
			this.setWidth(140);
			MenuItem add = new MenuItem("Add Category");
			add.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					LabelAddDialog labelAddDialog = new LabelAddDialog();
					labelAddDialog.show();
				}
			});
			this.add(add);
		}
	}
	
	public class LabelAddDialog extends Dialog {
		
		public LabelAddDialog() {
			this.setHeadingText("Add Category");
			LabelInfoContainer labelInfoContainer = new LabelInfoContainer("", "");
		    this.add(labelInfoContainer);
		 
		    final TextField labelName = labelInfoContainer.getLabelName();
		    final TextArea labelDescription = labelInfoContainer.getLabelDescription();
		    
		    getButtonBar().clear();
		    TextButton add = new TextButton("Add");
		    add.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					if(!labelName.validate()) {
						AlertMessageBox alert = new AlertMessageBox("Category Name", "A category name is required");
						alert.show();
						return;
					}
					
					Label newLabel = new Label(labelName.getText(), labelDescription.getText());
					collection.addLabel(newLabel);
					eventBus.fireEvent(new LabelCreateEvent(newLabel));
					LabelAddDialog.this.hide();
				}
		    });
		    TextButton cancel =  new TextButton("Cancel");
		    cancel.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					LabelAddDialog.this.hide();
				}
		    });
		    addButton(add);
		    addButton(cancel);
		}
	}
	
	private EventBus eventBus;
	private int portalColumnCount = 6;
	private TermsView termsView;
	private LabelsView categoriesView;
	private TermInfoView termInfoView;
	private Collection collection;

	public CategorizeView(EventBus eventBus) {
		this.eventBus = eventBus;
		termsView = new TermsView(eventBus);
		categoriesView = new LabelsView(eventBus, portalColumnCount);
		termInfoView = new TermInfoView(eventBus);
		ContentPanel cp = new ContentPanel();
		cp.setHeadingText("West");

		cp.add(termsView);
		BorderLayoutData d = new BorderLayoutData(.20);
		d.setMargins(new Margins(0, 5, 5, 5));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		setWestWidget(cp, d);

		cp = new ContentPanel();
		cp.setHeadingText("Center");
		cp.add(categoriesView);
		cp.setContextMenu(new LabelsMenu());
		d = new BorderLayoutData();
		d.setMargins(new Margins(0, 5, 5, 0));
		setCenterWidget(cp, d);

		cp = new ContentPanel();
		cp.setHeadingText("South");
		cp.add(termInfoView);
		d = new BorderLayoutData(.20);
		d.setMargins(new Margins(5));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		setSouthWidget(cp, d);
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
		termsView.setCollection(collection);
		categoriesView.setCollection(collection);
	}

}
