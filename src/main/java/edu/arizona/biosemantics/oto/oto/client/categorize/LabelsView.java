package edu.arizona.biosemantics.oto.oto.client.categorize;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.PortalLayoutContainer;
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

import edu.arizona.biosemantics.oto.oto.client.categorize.LabelPortlet.LabelInfoContainer;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelCreateEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelRemoveEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelsMergeEvent;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;
import edu.arizona.biosemantics.oto.oto.shared.model.Label.AddResult;
import edu.arizona.biosemantics.oto.oto.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto.oto.shared.rpc.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto.oto.shared.rpc.RPCCallback;

public class LabelsView extends PortalLayoutContainer {
	
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
					for(Label label : collection.getLabels()) {
						LabelPortlet portlet = labelPortletsMap.get(label);
						if(portlet != null)
							portlet.collapse();
					}
				}
			});
			
			
			MenuItem expand = new MenuItem("Expand All");
			expand.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					for(Label label : collection.getLabels()) {
						LabelPortlet portlet = labelPortletsMap.get(label);
						if(portlet != null)
							portlet.expand();
					}
				}
			});
			
			this.add(add);
			this.add(expand);
			this.add(collapse);
			
			MenuItem collapseExpand = new MenuItem("Collapse/Expand");
			Menu collapseExpandMenu = new Menu();
			VerticalPanel verticalPanel = new VerticalPanel();
			final Set<Label> collapseLabels = new HashSet<Label>();
			final Set<Label> expandLabels = new HashSet<Label>();
			final TextButton collapseExpandButton = new TextButton("Collapse/Expand");
			collapseExpandButton.setEnabled(false);			
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
					verticalPanel.add(checkBox);
				}
			}
			if(verticalPanel.getWidgetCount() > 0) {
				collapseExpandButton.addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						for(Label expandLabel : expandLabels) {
							LabelPortlet portlet = labelPortletsMap.get(expandLabel);
							if(portlet != null)
								portlet.expand();
						}
						for(Label collapseLabel : collapseLabels) {
							LabelPortlet portlet = labelPortletsMap.get(collapseLabel);
							if(portlet != null)
								portlet.collapse();
						}
						LabelsMenu.this.hide();
					}
				});
				verticalPanel.add(collapseExpandButton);
				collapseExpandMenu.add(verticalPanel);
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
							collection.addLabel(result);
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
	
	public LabelsView(EventBus eventBus, int portalColumnCount) {
		super(portalColumnCount);
		this.eventBus = eventBus;
		this.portalColumnCount = portalColumnCount;
		double portalColumnWidth = 1.0 / portalColumnCount;
		for(int i=0; i<portalColumnCount; i++) {
			this.setColumnWidth(i, portalColumnWidth);
		}
		this.getElement().getStyle().setBackgroundColor("white");
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(LabelsMergeEvent.TYPE, new LabelsMergeEvent.MergeLabelsHandler() {
			@Override
			public void onMerge(Label destination, List<Label> sources, Map<Term, AddResult> addResults) {
				for(Label source : sources) {
					LabelPortlet sourcePortlet = labelPortletsMap.remove(source);
					sourcePortlet.removeFromParent();
					//LabelsView.this.remove(sourcePortlet, LabelsView.this.getPortletColumn(sourcePortlet));
					LabelPortlet destinationPortlet = labelPortletsMap.get(destination);
					for(Term term : source.getMainTerms()) {
						if(addResults.get(term).result)
							destinationPortlet.addMainTerm(term);
						for(Term synonym : source.getSynonyms(term))
							if(addResults.get(synonym).result)
								destinationPortlet.addMainTerm(synonym);
					}
				}
			}
		});
		eventBus.addHandler(LabelCreateEvent.TYPE, new LabelCreateEvent.CreateLabelHandler() {
			@Override
			public void onCreate(Label label) {
				LabelPortlet labelPortlet = new LabelPortlet(eventBus, label, collection);
				add(labelPortlet, labelPortletsMap.size() % portalColumnCount);
				labelPortletsMap.put(label, labelPortlet);
			}
		});
		eventBus.addHandler(LabelRemoveEvent.TYPE, new LabelRemoveEvent.RemoveLabelHandler() {
			@Override
			public void onRemove(Label label) {
				LabelPortlet portlet = labelPortletsMap.remove(label);
				LabelsView.this.remove(portlet, LabelsView.this.getPortletColumn(portlet));
				portlet.removeFromParent();
			}
		});
	}

	public void setCollection(Collection collection) {
		this.collection = collection;

		clear();
		for(Label label : collection.getLabels()) {
			LabelPortlet labelPortlet = new LabelPortlet(eventBus, label, collection);
			add(labelPortlet, labelPortletsMap.size() % portalColumnCount);
			labelPortletsMap.put(label, labelPortlet);
		}
	}
}