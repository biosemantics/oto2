package edu.arizona.biosemantics.oto2.oto.client.categorize;

import java.util.Comparator;

import com.sencha.gxt.widget.core.client.menu.Item;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.NorthSouthContainer;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuBar;
import com.sencha.gxt.widget.core.client.menu.MenuBarItem;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.oto.client.categorize.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermProperties;

public class OtoView implements IsWidget {

	public static class MenuView extends MenuBar {

		private ListStore<Term> termStore = new ListStore<Term>(
				termProperties.key());
		private EventBus eventBus;

		public MenuView(final EventBus eventBus) {
			this.eventBus = eventBus;
			addStyleName(ThemeStyles.get().style().borderBottom());

			Menu sub = new Menu();
			MenuBarItem item = new MenuBarItem("Collection", sub);
			MenuItem resetItem = new MenuItem("Reset");
			MenuItem saveItem = new MenuItem("Save");
			sub.add(resetItem);
			sub.add(saveItem);

			sub = new Menu();
			final ComboBox<Term> searchCombo = new ComboBox<Term>(termStore,
					termProperties.nameLabel());
			searchCombo.setForceSelection(true);
			searchCombo.setTriggerAction(TriggerAction.ALL);
			searchCombo.addSelectionHandler(new SelectionHandler<Term>() {
				@Override
				public void onSelection(SelectionEvent<Term> arg0) {
					eventBus.fireEvent(new TermSelectEvent(arg0
							.getSelectedItem()));
				}
			});
			sub.add(searchCombo);
			item = new MenuBarItem("Search", sub);
			add(item);

			sub = new Menu();
			MenuBarItem questionsItem = new MenuBarItem("?", sub);
			MenuItem helpItem = new MenuItem("Help");
			helpItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> arg0) {
					final Dialog dialog = new Dialog();
					dialog.setBodyBorder(false);
					dialog.setHeadingText("Help");
					dialog.setHideOnButtonClick(true);
					dialog.setWidget(new HelpView());
					dialog.setWidth(400);
					dialog.setHeight(225);
					dialog.setResizable(false);
					dialog.setShadow(true);
					dialog.show();
				}
			});
			sub.add(helpItem);
			add(questionsItem);
		}

		public void setCollection(Collection collection) {
			termStore.clear();
			termStore.addAll(collection.getTerms());
			termStore.addSortInfo(new StoreSortInfo<Term>(
					new Term.TermComparator(), SortDir.ASC));
		}
	}

	public static class CategorizeView extends BorderLayoutContainer {

		private int portalColumnCount = 6;
		private TermsView termsView;
		private LabelsView labelsView;
		private TermInfoView termInfoView;

		public CategorizeView(EventBus eventBus) {
			termsView = new TermsView(eventBus);
			labelsView = new LabelsView(eventBus, portalColumnCount);
			termInfoView = new TermInfoView(eventBus);

			ContentPanel cp = new ContentPanel();
			cp.setHeadingText("Uncategorized Terms");
			cp.add(termsView);
			BorderLayoutData d = new BorderLayoutData(.20);
			// d.setMargins(new Margins(0, 1, 1, 1));
			d.setCollapsible(true);
			d.setSplit(true);
			d.setCollapseMini(true);
			setWestWidget(cp, d);

			cp = new ContentPanel();
			cp.setHeadingText("Categorization");
			cp.add(labelsView);
			cp.setContextMenu(labelsView.new LabelsMenu());
			d = new BorderLayoutData();
			// d.setMargins(new Margins(0, 1, 1, 0));
			setCenterWidget(cp, d);

			cp = new ContentPanel();
			cp.setHeadingText("Term Information");
			cp.add(termInfoView);
			d = new BorderLayoutData(.20);
			d.setMargins(new Margins(0, 0, 20, 0));
			d.setCollapsible(true);
			d.setSplit(true);
			d.setCollapseMini(true);
			setSouthWidget(cp, d);

			// cp = new ContentPanel();
			/*
			 * cp.setHeadingText("Search"); d = new BorderLayoutData(.20);
			 * //d.setMargins(new Margins(1)); d.setCollapsible(true);
			 * d.setSplit(true); d.setCollapseMini(true);
			 * setNorthWidget(getMenu(), d);
			 */
		}

		public void setCollection(Collection collection) {
			termsView.setCollection(collection);
			labelsView.setCollection(collection);
			termInfoView.setCollection(collection);
		}

	}

	private EventBus eventBus;

	private MenuView menuView;
	private CategorizeView categorizeView;
	private Collection collection;
	private NorthSouthContainer container = new NorthSouthContainer();
	private static final TermProperties termProperties = GWT
			.create(TermProperties.class);

	public OtoView(EventBus eventBus) {
		this.eventBus = eventBus;
		categorizeView = new CategorizeView(eventBus);
		menuView = new MenuView(eventBus);

		container.setNorthWidget(menuView);
		Viewport southViewport = new Viewport();
		southViewport.add(categorizeView);
		container.setSouthWidget(southViewport);

	}

	public void setCollection(Collection collection) {
		this.collection = collection;
		categorizeView.setCollection(collection);
		menuView.setCollection(collection);
	}

	@Override
	public Widget asWidget() {
		return container;
	}

}
