package edu.arizona.biosemantics.oto2.ontologize2.client.relations;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
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
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class MenuTermsGrid extends TermsGrid {

	private ToolBar buttonBar;

	public MenuTermsGrid(final EventBus eventBus, final String title, String firstColName, String nColName, ValueProvider<Term, String> valueProvider) {
		super(eventBus, firstColName, nColName, valueProvider);
		
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
						List<Row> rows = new ArrayList<Row>(lines.length);
						for(String line : lines) {
							String[] terms = line.split(",");
							if(!terms[0].trim().isEmpty()) {
								//TODO: Register in server first as term
								/*Term leadTerm = new Term(terms[0]);
								Row row = new Row(leadTerm);
								MenuTermsGrid.this.addRow(row);
								List<Term> attachedTerms = new ArrayList<Term>(terms.length - 1);
								for(int i=1; i<terms.length; i++) {
									if(terms[i].trim().isEmpty()) 
										continue;
								}
								try { 
									MenuTermsGrid.this.addAttachedTermsToRow(row, attachedTerms);
								} catch(Exception e) {
									Alerter.showAlert("Term add failed", e.getMessage(), e);
								}*/
							} else {
								Alerter.showAlert("Import", "Malformed input");
							}
						}
					}
				});
				box.show();
			}
		});
		/*
		TextButton consolidateButton = new TextButton("Consolidate");
		consolidateButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				try {
					consolidate();
				} catch(Exception e) {
					Alerter.showAlert("Consolidate rows", "Error occured");
				}
			}
		});
		*/
		TextButton removeButton = new TextButton("Remove");
		Menu removeMenu = new Menu();
		MenuItem selectedRemove = new MenuItem("Selected rows");//jin selected entry
		selectedRemove.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				removeRows(getSelection());
			}
		});
		MenuItem emptyColRemove = new MenuItem("Empty colums");//jin selected entry
		emptyColRemove.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				try {
					consolidate();
				} catch (Exception e) {
					Alerter.showAlert("Remove empty columns", "Error occured");
				}
			}
		});
		MenuItem allRemove = new MenuItem("All");
		allRemove.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				removeRows(getAll());
			}
		});
		removeMenu.add(selectedRemove);
		removeMenu.add(emptyColRemove);
		removeMenu.add(allRemove);
		removeButton.setMenu(removeMenu);
		
		buttonBar.add(importButton);
		//buttonBar.add(consolidateButton);
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
