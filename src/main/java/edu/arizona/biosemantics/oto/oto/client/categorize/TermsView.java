package edu.arizona.biosemantics.oto.oto.client.categorize;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.dnd.core.client.ListViewDragSource;
import com.sencha.gxt.dnd.core.client.TreeDragSource;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent.BeforeSelectHandler;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelRemoveEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermRenameEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermSelectEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;
import edu.arizona.biosemantics.oto.oto.shared.model.TermProperties;
import edu.arizona.biosemantics.oto.oto.shared.model.TermTreeNode;
import edu.arizona.biosemantics.oto.oto.shared.model.TextTreeNode;
import edu.arizona.biosemantics.oto.oto.shared.model.TextTreeNodeProperties;

public class TermsView extends TabPanel {
	
	private class TermMenu extends Menu implements BeforeShowHandler {
		
		private MenuItem categorize;
		private MenuItem rename;
		private HandlerRegistration renameRegistration;

		public TermMenu() {
			this.addBeforeShowHandler(this);
			this.setWidth(140);
			
			categorize = new MenuItem("Categorize to");
			rename = new MenuItem("Rename");
		}

		@Override
		public void onBeforeShow(BeforeShowEvent event) {
			this.clear();
			
			List<Term> selected = new LinkedList<Term>();
			if(TermsView.this.getActiveWidget().equals(TermsView.this.termTree)) {
				List<TextTreeNode> nodes = termTree.getSelectionModel().getSelectedItems();	
				for(TextTreeNode node : nodes)
					if(node instanceof TermTreeNode) {
						selected.add(((TermTreeNode)node).getTerm());
					}
			} else if(TermsView.this.getActiveWidget().equals(TermsView.this.listView)) {
				selected = listView.getSelectionModel().getSelectedItems();
			}
			if(selected == null || selected.isEmpty()) {
				event.setCancelled(true);
				this.hide();
			} else {				
				if(renameRegistration != null)
					renameRegistration.removeHandler();
				
				final List<Term> terms = new LinkedList<Term>(selected);
				
				if(!collection.getLabels().isEmpty()) {
					Menu categorizeMenu = new Menu();
					VerticalPanel verticalPanel = new VerticalPanel();
					final List<Label> categorizeLabels = new LinkedList<Label>();
					final TextButton categorizeButton = new TextButton("Categorize");
					categorizeButton.setEnabled(false);
					for(final Label collectionLabel : collection.getLabels()) {
						CheckBox checkBox = new CheckBox();
						checkBox.setBoxLabel(collectionLabel.getName());
						checkBox.setValue(false);
						checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
							@Override
							public void onValueChange(ValueChangeEvent<Boolean> event) {
								if(event.getValue())
									categorizeLabels.add(collectionLabel);
								else
									categorizeLabels.remove(collectionLabel);
								categorizeButton.setEnabled(!categorizeLabels.isEmpty());
							}
						});
						verticalPanel.add(checkBox);
					}
					categorizeButton.addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							for(Label categorizeLabel : categorizeLabels) {
								categorizeLabel.addMainTerms(terms);
							}
							eventBus.fireEvent(new TermCategorizeEvent(terms, categorizeLabels));
							TermMenu.this.hide();
						}
					});
					verticalPanel.add(categorizeButton);
					categorizeMenu.add(verticalPanel);
					categorize.setSubMenu(categorizeMenu);
					this.add(categorize);
				}
				
				if(selected.size() == 1) {
					final Term term = selected.get(0);
					renameRegistration = rename.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							final PromptMessageBox box = new PromptMessageBox(
									"Term rename", "Please input new spelling");
							box.getButton(PredefinedButton.OK).addBeforeSelectHandler(new BeforeSelectHandler() {
								@Override
								public void onBeforeSelect(BeforeSelectEvent event) {
									if(box.getTextField().getValue().trim().isEmpty()) {
										event.setCancelled(true);
										AlertMessageBox alert = new AlertMessageBox("Empty", "Empty not allowed");
										alert.show();
									}
								}
							});
							box.getTextField().setAllowBlank(false);
							box.addHideHandler(new HideHandler() {
								@Override
								public void onHide(HideEvent event) {
									String newName = box.getValue();
									term.setTerm(newName);
									eventBus.fireEvent(new TermRenameEvent(term));
								}
							});
							box.show();
						}
					});
					this.add(rename);
				}
			}
			
			if(this.getWidgetCount() == 0)
				event.setCancelled(true);
		}
	}
	
	public static class BucketTreeNode extends TextTreeNode {
		
		private Bucket bucket;

		public BucketTreeNode(Bucket bucket) {
			this.bucket = bucket;
		}

		@Override
		public String getText() {
			return bucket.getName();
		}
		
		public Bucket getBucket() {
			return bucket;
		}

		@Override
		public String getId() {
			return "bucket-" + bucket.getId();
		}
		
	}
	
	private static final TermProperties termProperties = GWT.create(TermProperties.class);
	private static final TextTreeNodeProperties textTreeNodeProperties = GWT.create(TextTreeNodeProperties.class);
	
	private TreeStore<TextTreeNode> treeStore;
	private ListStore<Term> listStore;

	private Map<Term, TermTreeNode> termTermTreeNodeMap;
	private Map<Bucket, BucketTreeNode> bucketBucketTreeNodeMap;
	private ListView<Term, String> listView;
	private Tree<TextTreeNode, String> termTree;
	private EventBus eventBus;
	private Map<Term, Bucket> termBucketMap;
	private Collection collection;
	
	public TermsView(EventBus eventBus) {
		super(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
		this.eventBus = eventBus;
		treeStore = new TreeStore<TextTreeNode>(textTreeNodeProperties.key());
		treeStore.setAutoCommit(true);
		listStore = new ListStore<Term>(termProperties.key());
		listStore.setAutoCommit(true);
		listView = new ListView<Term, String>(listStore, termProperties.term());
		listView.setContextMenu(new TermMenu());
		termTree = new Tree<TextTreeNode, String>(treeStore, textTreeNodeProperties.text());
		termTree.setContextMenu(new TermMenu());
		add(termTree, "tree");
		add(listView, "list");
		
		bindEvents();
		setupDnD();
	}
	
	private void bindEvents() {
		eventBus.addHandler(LabelRemoveEvent.TYPE, new LabelRemoveEvent.RemoveLabelHandler() {
			@Override
			public void onRemove(Label label) {
				for(Term term : label.getMainTerms()) {
					List<Label> labels = collection.getLabels(term);
					if(labels.isEmpty()) {
						treeStore.add(bucketBucketTreeNodeMap.get(termBucketMap.get(term)), 
								termTermTreeNodeMap.get(term));
						listStore.add(term);
					}
				}
			}
		});
		eventBus.addHandler(TermUncategorizeEvent.TYPE, new TermUncategorizeEvent.TermUncategorizeHandler() {
			@Override
			public void onUncategorize(List<Term> terms, List<Label> oldLabels) {
				for(Term term : terms) {
					treeStore.add(bucketBucketTreeNodeMap.get(termBucketMap.get(term)), termTermTreeNodeMap.get(term));
					listStore.add(term);
				}
			}
		});
		eventBus.addHandler(TermCategorizeEvent.TYPE, new TermCategorizeEvent.TermCategorizeHandler() {
			@Override
			public void onCategorize(List<Term> terms, List<Label> categories) {
				for(Term term : terms) {
					treeStore.remove(termTermTreeNodeMap.get(term));
					listStore.remove(term);
				}
			}
		});
		eventBus.addHandler(TermRenameEvent.TYPE, new TermRenameEvent.RenameTermHandler() {
			@Override
			public void onRename(Term term) {
				if(listStore.indexOf(term) != -1) {
					listStore.update(term);
				}
				if(treeStore.indexOf(termTermTreeNodeMap.get(term)) != -1) {
					treeStore.update(termTermTreeNodeMap.get(term));
				}
			}
		});
		
		listView.getSelectionModel().addSelectionHandler(new SelectionHandler<Term>() {
			@Override
			public void onSelection(SelectionEvent<Term> event) {
				eventBus.fireEvent(new TermSelectEvent(event.getSelectedItem()));
			}
		});
		termTree.getSelectionModel().addSelectionHandler(new SelectionHandler<TextTreeNode>() {
			@Override
			public void onSelection(SelectionEvent<TextTreeNode> event) {
				TextTreeNode node = event.getSelectedItem();
				if(node instanceof TermTreeNode) {
					TermTreeNode termTreeNode = (TermTreeNode)node;
					eventBus.fireEvent(new TermSelectEvent(termTreeNode.getTerm()));
				}
			}
		});
	}
	
	private void setupDnD() {
		ListViewDragSource<Term> dragSource = new ListViewDragSource<Term>(listView);
		TreeDragSource<TextTreeNode> treeDragSource = new TreeDragSource<TextTreeNode>(termTree);			
		DropTarget dropTarget = new DropTarget(this);
		// actual drop action is taken care of by events
		dropTarget.setOperation(Operation.COPY);
		dropTarget.addDropHandler(new DndDropHandler() {
			@Override
			public void onDrop(DndDropEvent event) {
				event.getData();
				if(DndDropEventExtractor.isSourceLabelPortlet(event)) {
					final List<Term> terms = DndDropEventExtractor.getTerms(event);
					final Label label = DndDropEventExtractor.getLabelPortletSource(event).getLabel();
					
					for(Term term : terms) {
						List<Label> labels = collection.getLabels(term);
						if(labels.size() > 1) {
							UncategorizeDialog dialog = new UncategorizeDialog(eventBus, label, 
									term, labels);
						} else {
							label.removeMainTerm(term);
							eventBus.fireEvent(new TermUncategorizeEvent(term, label));
						}
					}
				}
			}
		});
	}
	
	public void setCollection(Collection collection, boolean refreshUI) {
		this.collection = collection;
		
		if(refreshUI) {
			termBucketMap = new HashMap<Term, Bucket>();
			bucketBucketTreeNodeMap = new HashMap<Bucket, BucketTreeNode>();
			termTermTreeNodeMap = new HashMap<Term, TermTreeNode>();
			treeStore.clear();
			listStore.clear();
			
			Set<Term> categorizedTerms = new HashSet<Term>();
			for(Label label : collection.getLabels())
				for(Term term : label.getMainTerms())
					categorizedTerms.add(term);
			
			for(Bucket bucket : collection.getBuckets()) {
				BucketTreeNode bucketTreeNode = new BucketTreeNode(bucket);
				bucketBucketTreeNodeMap.put(bucket, bucketTreeNode);
				treeStore.add(bucketTreeNode);
				for(Term term : bucket.getTerms()) {
					termBucketMap.put(term, bucket);
					TermTreeNode termTreeNode = new TermTreeNode(term);
					termTermTreeNodeMap.put(term, termTreeNode);
					
					//only if not already in a label actually add them to store
					if(!categorizedTerms.contains(term)) {
						treeStore.add(bucketTreeNode, termTreeNode);
						listStore.add(term);
					}
				}
			}
		}
	}
}