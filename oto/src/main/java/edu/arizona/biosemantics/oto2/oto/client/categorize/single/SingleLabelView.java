package edu.arizona.biosemantics.oto2.oto.client.categorize.single;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.PortalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelPortlet;
import edu.arizona.biosemantics.oto2.oto.client.layout.OtoView.MenuView;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.LabelProperties;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class SingleLabelView extends SimpleContainer {

	private EventBus eventBus;
	private Collection collection;
	private LabelProperties labelProperites = GWT.create(LabelProperties.class);
	private ListStore<Label> labelStore;
	private PortalLayoutContainer portalLayoutContainer;
	private Map<Term, MainTermPortlet> termPortletsMap = new HashMap<Term, MainTermPortlet>();
	private int portalColumnCount;

	public SingleLabelView(final EventBus eventBus, final int portalColumnCount) {
		super();
		this.eventBus = eventBus;
		this.portalColumnCount = portalColumnCount;
		
		VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
		verticalLayoutContainer.add(createToolBar(),new VerticalLayoutData(1,-1));
		verticalLayoutContainer.add(createPortalLayoutContainer(), new VerticalLayoutData(1,1));
		this.setWidget(verticalLayoutContainer);
	}

	private ToolBar createToolBar() {
		labelStore = new ListStore<Label>(labelProperites.key());
		ComboBox<Label> labelComboBox = new ComboBox<Label>(labelStore, 
				labelProperites.nameLabel());
		labelComboBox.setForceSelection(true);
		labelComboBox.setTriggerAction(TriggerAction.ALL);
		labelComboBox.addSelectionHandler(new SelectionHandler<Label>() {
			@Override
			public void onSelection(SelectionEvent<Label> event) {
				Label label = event.getSelectedItem();		
				portalLayoutContainer.clear();
				termPortletsMap.clear();
				for(Term mainTerm : label.getMainTerms()) {
					MainTermPortlet mainTermPortlet = new MainTermPortlet(eventBus, collection, label, mainTerm);
					//System.out.println(termPortletsMap.size());
					//System.out.println(portalColumnCount);
					//System.out.println(termPortletsMap.size() % portalColumnCount);
					portalLayoutContainer.add(mainTermPortlet, termPortletsMap.size() % portalColumnCount);
					termPortletsMap.put(mainTerm, mainTermPortlet);
				}
				
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						((CssFloatLayoutContainer)portalLayoutContainer.getContainer()).forceLayout();
					}
				});
			}
		});
		
		ToolBar toolBar = new ToolBar();
		toolBar.add(new FillToolItem());
		toolBar.add(new com.google.gwt.user.client.ui.Label("Label:"));
		toolBar.add(labelComboBox);
		return toolBar;
	}

	private PortalLayoutContainer createPortalLayoutContainer() {
		this.portalLayoutContainer = new PortalLayoutContainer(portalColumnCount);
		double portalColumnWidth = 1.0 / portalColumnCount;
		for(int i=0; i<portalColumnCount; i++) {
			portalLayoutContainer.setColumnWidth(i, portalColumnWidth);
		}
		portalLayoutContainer.getElement().getStyle().setBackgroundColor("white");
		return portalLayoutContainer;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
		labelStore.clear();
		
		portalLayoutContainer.clear();
		for(Label label : collection.getLabels()) {
			labelStore.add(label);
		}
	}
}
