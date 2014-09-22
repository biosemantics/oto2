package edu.arizona.biosemantics.oto2.oto.client.layout;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;

import com.gargoylesoftware.htmlunit.javascript.host.Selection;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
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

import edu.arizona.biosemantics.oto2.oto.client.common.Alerter;
import edu.arizona.biosemantics.oto2.oto.client.common.DndDropEventExtractor;
import edu.arizona.biosemantics.oto2.oto.client.common.UncategorizeDialog;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelRemoveEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermRenameEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermProperties;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermTreeNode;
import edu.arizona.biosemantics.oto2.oto.shared.model.TextTreeNode;
import edu.arizona.biosemantics.oto2.oto.shared.model.TextTreeNodeProperties;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label.AddResult;

public class TermsView extends TabPanel {
	
	private class TermMenu extends Menu implements BeforeShowHandler {
		
		public TermMenu() {
			this.addBeforeShowHandler(this);
			this.setWidth(140);
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
							eventBus.fireEvent(new TermCategorizeEvent(terms, categorizeLabels));
							TermMenu.this.hide();
						}
					});
					verticalPanel.add(categorizeButton);
					categorizeMenu.add(verticalPanel);
					MenuItem categorize = new MenuItem("Categorize to");
					categorize.setSubMenu(categorizeMenu);
					this.add(categorize);
				}
				
				if(selected.size() == 1) {
					final Term term = selected.get(0);
					MenuItem rename = new MenuItem("Rename");
					rename.addSelectionHandler(new SelectionHandler<Item>() {
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
									eventBus.fireEvent(new TermRenameEvent(term, newName));
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
		treeStore.addSortInfo(new StoreSortInfo<TextTreeNode>(new Comparator<TextTreeNode>() {
			@Override
			public int compare(TextTreeNode o1, TextTreeNode o2) {
				return o1.getText().compareTo(o2.getText());
			}
		}, SortDir.ASC));
		listStore = new ListStore<Term>(termProperties.key());
		listStore.setAutoCommit(true);
		listStore.addSortInfo(new StoreSortInfo<Term>(new Term.TermComparator(), SortDir.ASC));
		listView = new ListView<Term, String>(listStore, termProperties.term());
		listView.getElement().setAttribute("source", "termsview");
		listView.setContextMenu(new TermMenu());
		termTree = new Tree<TextTreeNode, String>(treeStore, textTreeNodeProperties.text());
		termTree.getElement().setAttribute("source", "termsview");
		termTree.setContextMenu(new TermMenu());
		add(termTree, "tree");
		//add(listView, "list");
		
		bindEvents();
		setupDnD();
	}
	
	private void bindEvents() {
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.TermSelectHandler() {
			@Override
			public void onSelect(TermSelectEvent event) {
				Term term = event.getTerm();
				if(!listView.getSelectionModel().isSelected(term)) {
					List<Term> selection = new LinkedList<Term>();
					selection.add(term);
					listView.getSelectionModel().setSelection(selection);
				}
				
				TermTreeNode termTreeNode = termTermTreeNodeMap.get(term);
				if(termTreeNode != null && treeStore.findModel(termTreeNode) != null && !termTree.getSelectionModel().isSelected(termTreeNode)) {
					List<TextTreeNode> selectionTree = new LinkedList<TextTreeNode>();
					selectionTree.add(termTreeNode);
					termTree.getSelectionModel().setSelection(selectionTree);
				}
				
			}
		});
		eventBus.addHandler(LabelRemoveEvent.TYPE, new LabelRemoveEvent.RemoveLabelHandler() {
			@Override
			public void onRemove(LabelRemoveEvent event) {
				Label label = event.getLabel();
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
			public void onUncategorize(TermUncategorizeEvent event) {
				List<Term> terms = event.getTerms();
				for(Term term : terms) {
					BucketTreeNode bucketTreeNode = bucketBucketTreeNodeMap.get(termBucketMap.get(term));
					TermTreeNode node = termTermTreeNodeMap.get(term);
					treeStore.add(bucketBucketTreeNodeMap.get(termBucketMap.get(term)), termTermTreeNodeMap.get(term));
					listStore.add(term);
				}
			}
		});
		eventBus.addHandler(TermCategorizeEvent.TYPE, new TermCategorizeEvent.TermCategorizeHandler() {
			@Override
			public void onCategorize(TermCategorizeEvent event) {
				List<Term> terms = event.getTerms();
				for(Term term : terms) {
					treeStore.remove(termTermTreeNodeMap.get(term));
					listStore.remove(term);
				}
			}
		});
		eventBus.addHandler(TermRenameEvent.TYPE, new TermRenameEvent.RenameTermHandler() {
			@Override
			public void onRename(TermRenameEvent event) {
				Term term = event.getTerm();
				if(listStore.getAll().contains(term)) {
					listStore.update(term);
				}
				if(treeStore.getAll().contains(termTermTreeNodeMap.get(term))) {
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
		dropTarget.setAllowSelfAsSource(false);
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
							label.uncategorizeTerm(term);
							eventBus.fireEvent(new TermUncategorizeEvent(term, label));
						}
					}
				}
			}
		});
	}
	
	public void setCollection(Collection collection) {
		this.collection = collection;
		
		termBucketMap = new HashMap<Term, Bucket>();
		bucketBucketTreeNodeMap = new HashMap<Bucket, BucketTreeNode>();
		termTermTreeNodeMap = new HashMap<Term, TermTreeNode>();
		treeStore.clear();
		listStore.clear();
					
		for(Bucket bucket : collection.getBuckets()) {
			BucketTreeNode bucketTreeNode = new BucketTreeNode(bucket);
			bucketBucketTreeNodeMap.put(bucket, bucketTreeNode);
			treeStore.add(bucketTreeNode);
			for(Term term : bucket.getTerms()) {
				TermTreeNode termTreeNode = new TermTreeNode(term);
				termBucketMap.put(term, bucket);
				termTermTreeNodeMap.put(term, termTreeNode);
				if(collection.getLabels(term).isEmpty()) {
					treeStore.add(bucketTreeNode, termTreeNode);
					listStore.add(term);
				}
			}
		}
	}

}