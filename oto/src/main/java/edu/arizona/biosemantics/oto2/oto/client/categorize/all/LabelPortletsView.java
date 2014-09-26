package edu.arizona.biosemantics.oto2.oto.client.categorize.all;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.PortalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.oto.client.event.LabelCreateEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelRemoveEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelsMergeEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.HighlightLabel;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label.AddResult;
import edu.arizona.biosemantics.oto2.oto.shared.model.TrashLabel;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.oto.shared.rpc.RPCCallback;

public class LabelPortletsView extends PortalLayoutContainer {
	
	public class LabelsMenu extends Menu implements BeforeShowHandler {

		public LabelsMenu() {
			this.setWidth(140);
			this.addBeforeShowHandler(this);			
		}

		@Override
		public void onBeforeShow(BeforeShowEvent event) {
			this.clear();
			
			MenuItem add = new MenuItem("Add Category");
			add.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					LabelAddDialog labelAddDialog = new LabelAddDialog();
					labelAddDialog.show();
				}
			});
			
			MenuItem collapse = new MenuItem("Collapse All");
			collapse.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					collapseAll();
				}
			});
			
			
			MenuItem expand = new MenuItem("Expand All");
			expand.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					expandAll();
				}
			});
			
			this.add(add);
			this.add(expand);
			this.add(collapse);
			
			MenuItem expandCollapseEmpty = new MenuItem("Expand Non-empty");
			expandCollapseEmpty.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					expandNonEmptyCollapseEmpty();
				}
			});
			this.add(expandCollapseEmpty);
			
			MenuItem collapseExpand = new MenuItem("Collapse/Expand");
			Menu collapseExpandMenu = new Menu();
			VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
			final Set<Label> collapseLabels = new HashSet<Label>();
			final Set<Label> expandLabels = new HashSet<Label>();
			final TextButton collapseExpandButton = new TextButton("Collapse/Expand");
			collapseExpandButton.setEnabled(false);	
			
			FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
			VerticalLayoutContainer checkBoxPanel = new VerticalLayoutContainer();
			flowLayoutContainer.add(checkBoxPanel);
			flowLayoutContainer.setScrollMode(ScrollMode.AUTOY);
			flowLayoutContainer.getElement().getStyle().setProperty("maxHeight", "150px");
			for(final Label collectionLabel : collection.getLabels()) {
				LabelPortlet portlet = labelPortletsMap.get(collectionLabel);
				if(portlet != null) {
					CheckBox checkBox = new CheckBox();
					checkBox.setBoxLabel(collectionLabel.getName());
					checkBox.setValue(portlet.isExpanded());
					checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							if(event.getValue()) {
								expandLabels.add(collectionLabel); 
								collapseLabels.remove(collectionLabel);
							}
							else {
								collapseLabels.add(collectionLabel);
								expandLabels.remove(collectionLabel); 
							}
							collapseExpandButton.setEnabled(!collapseLabels.isEmpty() || !expandLabels.isEmpty());
						}
					});
					checkBoxPanel.add(checkBox);
				}
			}
			verticalLayoutContainer.add(flowLayoutContainer);
			if(verticalLayoutContainer.getWidgetCount() > 0) {
				collapseExpandButton.addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						expand(expandLabels);
						collapse(collapseLabels);
						LabelsMenu.this.hide();
					}
				});
				verticalLayoutContainer.add(collapseExpandButton);
				collapseExpandMenu.add(verticalLayoutContainer);
				collapseExpand.setSubMenu(collapseExpandMenu);
				this.add(collapseExpand);
			}
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
					
					final Label newLabel = new Label(labelName.getText(), labelDescription.getText());
					collectionService.addLabel(newLabel, collection.getId(), new RPCCallback<Label>() {
						@Override
						public void onSuccess(Label result) {
							eventBus.fireEvent(new LabelCreateEvent(result));
							LabelAddDialog.this.hide();
						}
					});
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

	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private EventBus eventBus;
	private int portalColumnCount;
	private Map<Label, LabelPortlet> labelPortletsMap = new HashMap<Label, LabelPortlet>();
	private Collection collection;
	
	public LabelPortletsView(EventBus eventBus, int portalColumnCount) {
		super(portalColumnCount);
		this.eventBus = eventBus;
		this.portalColumnCount = portalColumnCount;
		double portalColumnWidth = 1.0 / portalColumnCount;
		for(int i=0; i<portalColumnCount; i++) {
			this.setColumnWidth(i, portalColumnWidth);
		}
		this.getElement().getStyle().setBackgroundColor("white");
		this.getContainer().setContextMenu(new LabelsMenu());
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(LabelsMergeEvent.TYPE, new LabelsMergeEvent.MergeLabelsHandler() {
			@Override
			public void onMerge(LabelsMergeEvent event) {
				List<Label> sources = event.getSources();
				Label destination = event.getDestination();
				for(Label source : sources) {
					LabelPortlet sourcePortlet = labelPortletsMap.remove(source);
					sourcePortlet.removeFromParent();
					//LabelsView.this.remove(sourcePortlet, LabelsView.this.getPortletColumn(sourcePortlet));
					LabelPortlet destinationPortlet = labelPortletsMap.get(destination);
					for(Term term : source.getMainTerms()) {
						if(destination.isMainTerm(term) && !destinationPortlet.containsMainTerm(term))
							destinationPortlet.addMainTerm(term);
						for(Term synonym : source.getSynonyms(term))
							if(destination.isMainTerm(synonym) && 
									!destinationPortlet.containsMainTerm(synonym))
								destinationPortlet.addMainTerm(synonym);
					}
				}
			}
		});
		eventBus.addHandler(LabelCreateEvent.TYPE, new LabelCreateEvent.CreateLabelHandler() {
			@Override
			public void onCreate(LabelCreateEvent event) {
				Label label = event.getLabel();
				LabelPortlet labelPortlet = createLabelPortlet(label);
				add(labelPortlet, 0);
				labelPortletsMap.put(label, labelPortlet);
			}
		});
		eventBus.addHandler(LabelRemoveEvent.TYPE, new LabelRemoveEvent.RemoveLabelHandler() {
			@Override
			public void onRemove(LabelRemoveEvent event) {
				Label label = event.getLabel();
				LabelPortlet portlet = labelPortletsMap.remove(label);
				LabelPortletsView.this.remove(portlet, LabelPortletsView.this.getPortletColumn(portlet));
				portlet.removeFromParent();
			}
		});
	}

	protected LabelPortlet createLabelPortlet(Label label) {
		//labelPortlet = new LabelPortlet(GWT.<OtoFramedPanelAppearance> create(OtoFramedPanelAppearance.class), 
		//		eventBus, label, collection);
		LabelPortlet labelPortlet = new LabelPortlet(eventBus, label, collection, this);
		if(label instanceof HighlightLabel)
			labelPortlet.setHeadingHtml("<div style='color: black'>" + label.getName() + "</div>");
		else if(label instanceof TrashLabel)
			labelPortlet.setHeadingHtml("<div style='color: gray'>" 
					+ label.getName() + "</div>");
		else 
			labelPortlet.setHeadingHtml("<div style='font-weight: normal'>" 
					+ label.getName() + "</div>");
		return labelPortlet;
	}

	public void setCollection(final Collection collection) {
		this.collection = collection;

		clear();
		labelPortletsMap.clear();
		for(Label label : collection.getLabels()) {
			LabelPortlet labelPortlet = createLabelPortlet(label);
			labelPortlet.collapse();
			add(labelPortlet, labelPortletsMap.size() % portalColumnCount);
			labelPortletsMap.put(label, labelPortlet);
		}
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				forceLayout();
				expandNonEmptyCollapseEmpty();
			}
		});
	}

	protected void expandNonEmptyCollapseEmpty() {
		for(Label label : collection.getLabels()) {
			LabelPortlet portlet = labelPortletsMap.get(label);
			if(portlet != null) {
				if(label.hasTerms()) 
					portlet.expand();
				else
					portlet.collapse();
			}
		}
	}
	
	protected void expandAll() {
		for(Label label : collection.getLabels()) {
			LabelPortlet portlet = labelPortletsMap.get(label);
			if(portlet != null)
				portlet.expand();
		}
	}
	
	protected void collapseAll() {
		for(Label label : collection.getLabels()) {
			LabelPortlet portlet = labelPortletsMap.get(label);
			if(portlet != null)
				portlet.collapse();
		}
	}
	
	protected void collapse(Set<Label> collapseLabels) {
		for(Label collapseLabel : collapseLabels) {
			LabelPortlet portlet = labelPortletsMap.get(collapseLabel);
			if(portlet != null)
				portlet.collapse();
		}
	}

	protected void expand(Set<Label> expandLabels) {
		for(Label expandLabel : expandLabels) {
			LabelPortlet portlet = labelPortletsMap.get(expandLabel);
			if(portlet != null)
				portlet.expand();
		}
	}

	public void forceLayout() {
		((CssFloatLayoutContainer)getContainer()).forceLayout();
	}
	
}