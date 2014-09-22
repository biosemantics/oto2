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
import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelPortletsView;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyRemoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeMoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelCreateEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelModifyEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelRemoveEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelsMergeEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymCreationEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymRemovalEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.client.layout.OtoView.MenuView;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.LabelProperties;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermTreeNode;

public class SingleLabelView extends SimpleContainer {

	private EventBus eventBus;
	private Collection collection;
	private LabelProperties labelProperites = GWT.create(LabelProperties.class);
	private ListStore<Label> labelStore;
	private PortalLayoutContainer portalLayoutContainer;
	private Map<Term, MainTermPortlet> termPortletsMap = new HashMap<Term, MainTermPortlet>();
	private int portalColumnCount;
	protected Label currentLabel;
	private ComboBox<Label> labelComboBox;

	public SingleLabelView(final EventBus eventBus, final int portalColumnCount) {
		super();
		this.eventBus = eventBus;
		this.portalColumnCount = portalColumnCount;
		
		VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
		verticalLayoutContainer.add(createToolBar(),new VerticalLayoutData(1,-1));
		verticalLayoutContainer.add(createPortalLayoutContainer(), new VerticalLayoutData(1,1));
		this.setWidget(verticalLayoutContainer);
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(TermUncategorizeEvent.TYPE, new TermUncategorizeEvent.TermUncategorizeHandler() {
			@Override
			public void onUncategorize(TermUncategorizeEvent event) {
				for(Term term : event.getTerms()) {
					if(termPortletsMap.containsKey(term)) {
						removeMainTerm(term);
					}
				}
			}
		});
		eventBus.addHandler(CategorizeCopyRemoveTermEvent.TYPE, new CategorizeCopyRemoveTermEvent.CategorizeCopyRemoveTermHandler() {
			@Override
			public void onRemove(CategorizeCopyRemoveTermEvent event) {
				for(Term term : event.getTerms()) {
					if(termPortletsMap.containsKey(term)) {
						removeMainTerm(term);
					}
				}
			}
		});
		eventBus.addHandler(TermCategorizeEvent.TYPE, new TermCategorizeEvent.TermCategorizeHandler() {
			@Override
			public void onCategorize(TermCategorizeEvent event) {
				if(event.getLabels().contains(currentLabel)) {
					for(Term mainTerm : event.getTerms()) {
						addMainTerm(mainTerm);
					}
				}
			}
		});
		eventBus.addHandler(CategorizeCopyTermEvent.TYPE, new CategorizeCopyTermEvent.CategorizeCopyTermHandler() { 
			@Override
			public void onCategorize(CategorizeCopyTermEvent event) {
				if(event.getTargetCategories().contains(currentLabel)) {
					for(Term mainTerm : event.getTerms()) {
						addMainTerm(mainTerm);
					}
				}
			}
		});
		eventBus.addHandler(CategorizeMoveTermEvent.TYPE, new CategorizeMoveTermEvent.CategorizeMoveTermHandler() {
			@Override
			public void onCategorize(CategorizeMoveTermEvent event) {
				if(event.getTargetCategory().equals(currentLabel)) {
					for(Term mainTerm : event.getTerms()) {
						addMainTerm(mainTerm);
					}
				}
			}
		});
		eventBus.addHandler(SynonymCreationEvent.TYPE, new SynonymCreationEvent.SynonymCreationHandler() {
			@Override
			public void onSynonymCreation(SynonymCreationEvent event) {
				if(event.getLabel().equals(currentLabel)) {
					for(Term synonym : event.getSynonymTerm()) {
						removeMainTerm(synonym);
					}
				}
			}
		});
		eventBus.addHandler(SynonymRemovalEvent.TYPE, new SynonymRemovalEvent.SynonymRemovalHandler() {
			@Override
			public void onSynonymRemoval(SynonymRemovalEvent event) {
				if(event.getLabel().equals(currentLabel)) {
					for(Term term : event.getSynonyms()) {
						addMainTerm(term);
					}
				}
			}
		});
		
		eventBus.addHandler(LabelCreateEvent.TYPE, new LabelCreateEvent.CreateLabelHandler() {
			@Override
			public void onCreate(LabelCreateEvent event) {
				labelStore.add(event.getLabel());
			}
		});
		eventBus.addHandler(LabelModifyEvent.TYPE, new LabelModifyEvent.ModifyLabelHandler() {
			@Override
			public void onModify(LabelModifyEvent event) {
				labelStore.update(event.getLabel());
			}
		});
		eventBus.addHandler(LabelRemoveEvent.TYPE, new LabelRemoveEvent.RemoveLabelHandler() {
			@Override
			public void onRemove(LabelRemoveEvent event) {
				labelStore.remove(event.getLabel());
			}
		});
		eventBus.addHandler(LabelsMergeEvent.TYPE, new LabelsMergeEvent.MergeLabelsHandler() {
			@Override
			public void onMerge(LabelsMergeEvent event) {
				for(Label label : event.getSources()) {
					labelStore.remove(label);
					
					for(Term mainTerm : label.getMainTerms()) {
						addMainTerm(mainTerm);
					}
				}
			}
		});
	}

	protected void addMainTerm(Term mainTerm) {
		MainTermPortlet mainTermPortlet = new MainTermPortlet(eventBus, collection, currentLabel, mainTerm);
		
		portalLayoutContainer.add(mainTermPortlet, 0);
		termPortletsMap.put(mainTerm, mainTermPortlet);
	}

	protected void removeMainTerm(Term term) {
		MainTermPortlet portlet = termPortletsMap.remove(term);
		portalLayoutContainer.remove(portlet, portalLayoutContainer.getPortletColumn(portlet));
		portlet.removeFromParent();
	}

	private ToolBar createToolBar() {
		labelStore = new ListStore<Label>(labelProperites.key());
		labelComboBox = new ComboBox<Label>(labelStore, labelProperites.nameLabel());
		labelComboBox.setForceSelection(true);
		labelComboBox.setTriggerAction(TriggerAction.ALL);
		labelComboBox.addSelectionHandler(new SelectionHandler<Label>() {
			@Override
			public void onSelection(SelectionEvent<Label> event) {		
				currentLabel = event.getSelectedItem();
				portalLayoutContainer.clear();
				termPortletsMap.clear();
				for(Term mainTerm : currentLabel.getMainTerms()) {
					MainTermPortlet mainTermPortlet = new MainTermPortlet(eventBus, collection, currentLabel, mainTerm);
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
		labelComboBox.select(collection.getLabels().get(0));
	}
}
