package edu.arizona.biosemantics.oto2.ontologize.client.layout;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuBar;
import com.sencha.gxt.widget.core.client.menu.MenuBarItem;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.ontologize.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.common.ColorSettingsDialog;
import edu.arizona.biosemantics.oto2.ontologize.client.common.ColorsDialog;
import edu.arizona.biosemantics.oto2.ontologize.client.common.CommentsDialog;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.DownloadEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public class MenuView extends MenuBar {

	private EventBus eventBus;

	public MenuView(EventBus eventBus) {
		this.eventBus = eventBus;
		addStyleName(ThemeStyles.get().style().borderBottom());
		
		addItems();
		
		bindEvents();
	}

	private void bindEvents() {

	}
	
	protected void addItems() {
		add(createFileItem());
		//add(createSearchItem());
		add(createOntologiesItem());
		add(createAnnotationsItem());
		//add(createViewItem());
		add(createQuestionItem());
	}
	

	private Widget createOntologiesItem() {
		Menu sub = new Menu();
		
		MenuItem browseOntologies = new MenuItem("Browse Ontologies");
		sub.add(browseOntologies);
		final Menu browseSub = new Menu();
		browseOntologies.setSubMenu(browseSub);
		
		browseSub.addBeforeShowHandler(new BeforeShowHandler() {
			@Override
			public void onBeforeShow(BeforeShowEvent event) {
				browseSub.clear();
				for(final Ontology ontology : ModelController.getPermanentOntologies()) {
					MenuItem ontologyItem = new MenuItem(ontology.getAcronym());
					ontologyItem.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							Window.open(ontology.getBrowseURL(), "_blank", "");
						}
					});
					browseSub.add(ontologyItem);
				}
			}
		});
		
		/*MenuItem selectOntologies = new MenuItem("Select Ontologies for Term Information ");
		selectOntologies.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				//Dialog dialog = new SelectOntologiesDialog(eventBus);
				//dialog.show();
			}
		});
		sub.add(selectOntologies);*/
		MenuBarItem item = new MenuBarItem("Ontologies", sub);
		return item;
	}

	private Widget createQuestionItem() {
		Menu sub = new Menu();
		MenuBarItem questionsItem = new MenuBarItem("Instructions", sub);
		MenuItem helpItem = new MenuItem("Help");
		helpItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> arg0) {
				final Dialog dialog = new Dialog();
				dialog.setBodyBorder(false);
				dialog.setHeadingText("Help");
				dialog.setHideOnButtonClick(true);
				//dialog.setWidget(new HelpView());
				dialog.setWidth(600);
				dialog.setMaximizable(true);
				//dialog.setMinimizable(true);
				dialog.setHeight(400);
				dialog.setResizable(true);
				dialog.setShadow(true);
				dialog.show();
			}
		});
		sub.add(helpItem);
		return questionsItem;
	}

	/*private Widget createViewItem() {
		Menu sub = new Menu();
		MenuItem viewOntologies = new MenuItem("Ontologies");
		sub.add(viewOntologies);
		final Menu viewOntologiesSub = new Menu();
		viewOntologies.setSubMenu(viewOntologiesSub);
		
		viewOntologiesSub.addBeforeShowHandler(new BeforeShowHandler() {
			@Override
			public void onBeforeShow(BeforeShowEvent event) {
				viewOntologiesSub.clear();
				for(final Ontology ontology : permanentOntologies) {
					MenuItem ontologyItem = new MenuItem(ontology.getAcronym());
					ontologyItem.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							
						}
					});
					viewOntologiesSub.add(ontologyItem);
				}
			}
		});
		MenuBarItem item = new MenuBarItem("View", sub);
		return item;
	}*/

	private Widget createAnnotationsItem() {
		Menu sub = new Menu();
		MenuBarItem annotationsItem = new MenuBarItem("Annotation", sub);
		sub.add(new HeaderMenuItem("Configure"));
		MenuItem colorSettingsItem = new MenuItem("Color Settings");
		colorSettingsItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> arg0) {
				ColorSettingsDialog dialog = new ColorSettingsDialog(eventBus, ModelController.getCollection());
				dialog.show();
			}
		});
		sub.add(colorSettingsItem);
		sub.add(new HeaderMenuItem("Show"));
		MenuItem colorsItem = new MenuItem("Color Use");
		colorsItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> arg0) {
				ColorsDialog dialog = new ColorsDialog(eventBus);
				dialog.show();
			}
		});
		sub.add(colorsItem);
		MenuItem commentsItem = new MenuItem("Comments");
		commentsItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> arg0) {
				CommentsDialog dialog = new CommentsDialog(eventBus);
				dialog.show();
			}
		});
		sub.add(commentsItem);
		return annotationsItem;
	}

	private Widget createSearchItem() {
		Menu sub = new Menu();
		//final ComboBox<Term> searchCombo = new ComboBox<Term>(termStore,
		//		termProperties.nameLabel());
		//searchCombo.setForceSelection(true);
		//searchCombo.setTriggerAction(TriggerAction.ALL);
		/*searchCombo.addSelectionHandler(new SelectionHandler<Term>() {
			@Override
			public void onSelection(SelectionEvent<Term> arg0) {
				eventBus.fireEvent(new TermSelectEvent(arg0
						.getSelectedItem()));
			}
		});*/
		//sub.add(searchCombo);
		MenuBarItem item = new MenuBarItem("Search", sub);
		return item;
	}

	private Widget createFileItem() {
		Menu sub = new Menu();
		MenuBarItem item = new MenuBarItem("File", sub);
		MenuItem downloadItem = new MenuItem("Download Ontology");
		downloadItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> arg0) {
				eventBus.fireEvent(new DownloadEvent(ModelController.getCollection()));
			}
		});
		sub.add(downloadItem);
		return item;
	}
}