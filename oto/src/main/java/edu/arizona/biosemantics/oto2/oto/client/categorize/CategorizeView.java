package edu.arizona.biosemantics.oto2.oto.client.categorize;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.NorthSouthContainer;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuBar;
import com.sencha.gxt.widget.core.client.menu.MenuBarItem;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem;

import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;

public class CategorizeView implements IsWidget {
		
	private EventBus eventBus;
	private int portalColumnCount = 6;
	private TermsView termsView;
	private LabelsView labelsView;
	private TermInfoView termInfoView;
	private Collection collection;
	private NorthSouthContainer container = new NorthSouthContainer();

	public CategorizeView(EventBus eventBus) {
		this.eventBus = eventBus;
		termsView = new TermsView(eventBus);
		labelsView = new LabelsView(eventBus, portalColumnCount);
		termInfoView = new TermInfoView(eventBus);	 		
		
		BorderLayoutContainer borderLayoutContainer = new BorderLayoutContainer();
		
		ContentPanel cp = new ContentPanel();
		cp.setHeadingText("Uncategorized Terms");
		cp.add(termsView);
		BorderLayoutData d = new BorderLayoutData(.20);
		//d.setMargins(new Margins(0, 1, 1, 1));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		borderLayoutContainer.setWestWidget(cp, d);

		cp = new ContentPanel();
		cp.setHeadingText("Categorization");
		cp.add(labelsView);
		cp.setContextMenu(labelsView.new LabelsMenu());
		d = new BorderLayoutData();
		//d.setMargins(new Margins(0, 1, 1, 0));
		borderLayoutContainer.setCenterWidget(cp, d);

		cp = new ContentPanel();
		cp.setHeadingText("Term Information");
		cp.add(termInfoView);
		d = new BorderLayoutData(.20);
		//d.setMargins(new Margins(1));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		borderLayoutContainer.setSouthWidget(cp, d);
		
		//cp = new ContentPanel();
		/*cp.setHeadingText("Search");
		d = new BorderLayoutData(.20);
		//d.setMargins(new Margins(1));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);		
		setNorthWidget(getMenu(), d);*/
		

		container.setNorthWidget(getMenu());
		container.setSouthWidget(borderLayoutContainer);
	}

	private IsWidget getMenu() {
		MenuBar bar = new MenuBar();
		bar.addStyleName(ThemeStyles.get().style().borderBottom());
		MenuBarItem item = new MenuBarItem("Search");
		bar.add(item);
		return bar;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
		termsView.setCollection(collection);
		labelsView.setCollection(collection);
	}

	@Override
	public Widget asWidget() {
		return container;
	}

}
