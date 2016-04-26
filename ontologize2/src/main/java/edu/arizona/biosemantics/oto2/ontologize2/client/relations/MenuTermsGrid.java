package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class MenuTermsGrid extends TermsGrid {

	private ToolBar buttonBar;

	public MenuTermsGrid(final String title, String firstColName, String nColName, ValueProvider<Term, String> valueProvider) {
		super(firstColName, nColName, valueProvider);
		
		buttonBar = new ToolBar();
		TextButton importButton = new TextButton("Import");
		importButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Import " + title, "");
				box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						String input = box.getValue();
						String[] lines = input.split("\\n");
						for(String line : lines) {
							String[] terms = line.split(",");
							if(!terms[0].trim().isEmpty()) {
								Row row = new Row(new Term(terms[0]));
								for(int i=1; i<terms.length; i++) {
									if(terms[i].trim().isEmpty()) 
										continue;
									try {
										row.addAttachedTerm(new Term(terms[i]));
									} catch(Exception e) {
										Alerter.showAlert("Term add failed", e.getMessage(), e);
									}
								}
								addRow(row);
							} else {
								//report error
							}
						}
					}
				});
				box.show();
			}
		});
		TextButton consolidateButton = new TextButton("Consolidate");
		consolidateButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				consolidate();
			}
		});
		TextButton removeButton = new TextButton("Remove");
		Menu removeMenu = new Menu();
		MenuItem selectedRemove = new MenuItem("Selected");
		selectedRemove.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				remove(getSelection());
			}
		});
		MenuItem allRemove = new MenuItem("All");
		allRemove.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				remove(getAll());
			}
		});
		removeMenu.add(selectedRemove);
		removeMenu.add(allRemove);
		removeButton.setMenu(removeMenu);
		
		buttonBar.add(importButton);
		buttonBar.add(consolidateButton);
		buttonBar.add(removeButton);
		
	}
	
	@Override
	public Widget asWidget() {
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add(buttonBar, new VerticalLayoutData(1, -1));
		vlc.add(super.asWidget(), new VerticalLayoutData(1, 1));
		SimpleContainer simpleContainer = new SimpleContainer();
		simpleContainer.setWidget(vlc);
		return simpleContainer;
	}

}
