package edu.arizona.biosemantics.oto.oto.client.categorize;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.dnd.core.client.ListViewDragSource;
import com.sencha.gxt.dnd.core.client.TreeDragSource;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.TabPanel.TabPanelAppearance;
import com.sencha.gxt.widget.core.client.TabPanel.TabPanelBottomAppearance;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.PortalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermCategorizeEvent.TermCategorizeHandler;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermSelectEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermSelectEvent.TermSelectHandler;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermUncategorizeEvent.TermUncategorizeHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;
import edu.arizona.biosemantics.oto.oto.shared.model.TermProperties;
import edu.arizona.biosemantics.oto.oto.shared.model.TermTreeNode;
import edu.arizona.biosemantics.oto.oto.shared.model.TextTreeNode;
import edu.arizona.biosemantics.oto.oto.shared.model.TextTreeNodeProperties;
import edu.arizona.biosemantics.oto.oto.client.categorize.DndDropEventExtractor;

public class CategorizeView extends BorderLayoutContainer implements IsWidget {
	
	private static final TermProperties termProperties = GWT.create(TermProperties.class);
	private static final TextTreeNodeProperties textTreeNodeProperties = GWT.create(TextTreeNodeProperties.class);
	
	public class LabelTreeNode extends TextTreeNode {
		
		private Label label;

		public LabelTreeNode(Label label) {
			this.label = label;
		}

		@Override
		public String getText() {
			return label.getName();
		}
		
		public Label getLabel() {
			return label;
		}
		
	}
	
	public class BucketTreeNode extends TextTreeNode {
		
		private Bucket bucket;

		public BucketTreeNode(Bucket bucket) {
			this.bucket = bucket;
		}

		@Override
		public String getText() {
			return bucket.getName();
		}
		
		public Bucket getLabel() {
			return bucket;
		}
		
	}
	
	public class LabelsView extends PortalLayoutContainer {
		public LabelsView() {
			super(portalColumnCount);
			double portalColumnWidth = 1.0 / portalColumnCount;
			for(int i=0; i<portalColumnCount; i++) {
				this.setColumnWidth(i, portalColumnWidth);
			}
			this.getElement().getStyle().setBackgroundColor("white");
		}

		public void setLabels(List<Label> labels) {
			clear();
			int i = 0;
			for(Label label : labels) {
				LabelPortlet categoryPortlet = new LabelPortlet(eventBus, label);
				add(categoryPortlet, i);
				i = (i + 1) % portalColumnCount;
			}
		}
	}
	
	public class TermsView extends TabPanel {
		
		private TreeStore<TextTreeNode> treeStore;
		private ListStore<Term> listStore;

		private Map<Term, TermTreeNode> termTermTreeNodeMap;
		private Map<Bucket, BucketTreeNode> bucketBucketTreeNodeMap;
		private ListView<Term, String> listView;
		private Tree<TextTreeNode, String> termTree;
		
		public TermsView() {
			super(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
			treeStore = new TreeStore<TextTreeNode>(textTreeNodeProperties.key());
			listStore = new ListStore<Term>(termProperties.key());
			listView = new ListView<Term, String>(listStore, termProperties.term());
			termTree = new Tree<TextTreeNode, String>(treeStore, textTreeNodeProperties.text());
			add(termTree, "tree");
			add(listView, "list");
			
			bindEvents();
			setupDnD();
		}
		
		private void bindEvents() {
			eventBus.addHandler(TermUncategorizeEvent.TYPE, new TermUncategorizeEvent.TermUncategorizeHandler() {
				@Override
				public void onUncategorize(List<Term> terms, Label oldLabel) {
					for(Term term : terms) {
						treeStore.add(bucketBucketTreeNodeMap.get(term.getBucket()), termTermTreeNodeMap.get(term));
						listStore.add(term);
					}
				}
			});
			eventBus.addHandler(TermCategorizeEvent.TYPE, new TermCategorizeEvent.TermCategorizeHandler() {
				@Override
				public void onCategorize(List<Term> terms, Label label) {
					for(Term term : terms) {
						treeStore.remove(termTermTreeNodeMap.get(term));
						listStore.remove(term);
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
			TreeDragSource treeDragSource = new TreeDragSource(termTree);			
			DropTarget dropTarget = new DropTarget(this);
			// actual drop action is taken care of by events
			dropTarget.setOperation(Operation.COPY);
			dropTarget.addDropHandler(new DndDropHandler() {
				@Override
				public void onDrop(DndDropEvent event) {
					event.getData();
					if(DndDropEventExtractor.isSourceLabelPortlet(event)) {
						eventBus.fireEvent(new TermUncategorizeEvent(DndDropEventExtractor.getTerms(event), DndDropEventExtractor.getLabelPortletSource(event).getLabel()));
					}
				}
			});
		}
		
		public void setBuckets(List<Bucket> buckets) {
			bucketBucketTreeNodeMap = new HashMap<Bucket, BucketTreeNode>();
			termTermTreeNodeMap = new HashMap<Term, TermTreeNode>();
			treeStore.clear();
			listStore.clear();
			
			for(Bucket bucket : buckets) {
				BucketTreeNode bucketTreeNode = new BucketTreeNode(bucket);
				bucketBucketTreeNodeMap.put(bucket, bucketTreeNode);
				treeStore.add(bucketTreeNode);
				for(Term term : bucket.getTerms()) {
					TermTreeNode termTreeNode = new TermTreeNode(term);
					termTermTreeNodeMap.put(term, termTreeNode);
					treeStore.add(bucketTreeNode, termTreeNode);
					listStore.add(term);
				}
			}
		}
	}
	
	public class TermInfoView extends TabPanel {
		private LocationsView locationsView;
		private ContextView contextView;
		private OntologiesView ontologiesView;

		public TermInfoView() {
			super(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
			locationsView = new LocationsView(eventBus);
			contextView = new ContextView(eventBus);
			ontologiesView = new OntologiesView(eventBus);
			add(locationsView, "Locations");
			add(contextView, "Context");
			add(ontologiesView, "Ontologies");
		}
	}

	private int portalColumnCount = 6;
	private EventBus eventBus = new SimpleEventBus();
	private TermsView termsView = new TermsView();
	private LabelsView categoriesView = new LabelsView();
	private TermInfoView termInfoView = new TermInfoView();
	private Collection collection;

	public CategorizeView() {
		ContentPanel cp = new ContentPanel();
		cp.setHeadingText("West");

		cp.add(termsView);
		BorderLayoutData d = new BorderLayoutData(.20);
		d.setMargins(new Margins(0, 5, 5, 5));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		setWestWidget(cp, d);

		cp = new ContentPanel();
		cp.setHeadingText("Center");
		cp.add(categoriesView);
		d = new BorderLayoutData();
		d.setMargins(new Margins(0, 5, 5, 0));
		setCenterWidget(cp, d);

		cp = new ContentPanel();
		cp.setHeadingText("South");
		cp.add(termInfoView);
		d = new BorderLayoutData(.20);
		d.setMargins(new Margins(5));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		setSouthWidget(cp, d);
		
	}
	
	public void setBuckets(List<Bucket> buckets) {
		termsView.setBuckets(buckets);
	}

	public void setLabels(List<Label> labels) {
		categoriesView.setLabels(labels);
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
		this.setBuckets(collection.getBuckets());
		this.setLabels(collection.getLabels());
	}

	
	/*private void synchronizeTreeAndListStore() {
	treeStore.addStoreHandlers(new StoreHandlers<TextTreeNode>() {
		@Override
		public void onAdd(StoreAddEvent<TextTreeNode> event) {
			for(TextTreeNode node : event.getItems()) {
				if(node instanceof TermTreeNode) {
					listStore.add(((TermTreeNode) node).getTerm());
				}
			}
		}
		@Override
		public void onRemove(StoreRemoveEvent<TextTreeNode> event) {
			if(event.getItem() instanceof TermTreeNode) {
				listStore.remove(((TermTreeNode)event.getItem()).getTerm());
			}
		}
		@Override
		public void onFilter(StoreFilterEvent<TextTreeNode> event) {
			//want to do that hear or via button filter both?
		}
		@Override
		public void onClear(StoreClearEvent<TextTreeNode> event) {
			categoryCategoryTreeNodeMap = new HashMap<Category, CategoryTreeNode>();
			listStore.clear();
		}
		@Override
		public void onUpdate(StoreUpdateEvent<TextTreeNode> event) {
			for(TextTreeNode node : event.getItems()) {
				if(node instanceof TermTreeNode) {
					Term term = ((TermTreeNode) node).getTerm();
					//term.setText(text)
					listStore.update(term);
				}
			}
		}

		@Override
		public void onDataChange(StoreDataChangeEvent<TextTreeNode> event) {}

		@Override
		public void onRecordChange(StoreRecordChangeEvent<TextTreeNode> event) {
			TextTreeNode node = event.getRecord().getModel();
			if(node instanceof TermTreeNode) {
				listStore.update(((TermTreeNode)node).getTerm());
			}
		}

		@Override
		public void onSort(StoreSortEvent<TextTreeNode> event) {
			listStore.fireEvent(new StoreSortEvent<Term>());
		}
	});
	listStore.addStoreHandlers(new StoreHandlers<Term>() {
		@Override
		public void onAdd(StoreAddEvent<Term> event) {
			for(Term term : event.getItems()) {
				Category category = term.getCategory();
				CategoryTreeNode node = categoryCategoryTreeNodeMap.get(category);
				if(node != null) {
					treeStore.add(node, new TermTreeNode(term));
				}
			}
		}
		@Override
		public void onRemove(StoreRemoveEvent<Term> event) {
			Term term = event.getItem();
			treeStore.remove(termTermTreeNodeMap.get(term));
		}
		@Override
		public void onFilter(StoreFilterEvent<Term> event) {
			//want to do that hear or via button filter both?
		}
		@Override
		public void onClear(StoreClearEvent<Term> event) {
			termTermTreeNodeMap = new HashMap<Term, TermTreeNode>();
			treeStore.clear();
		}
		@Override
		public void onUpdate(StoreUpdateEvent<Term> event) {
			for(Term term : event.getItems()) {
				//term.setText(text)
				treeStore.update(termTermTreeNodeMap.get(term));
			}
		}
		@Override
		public void onDataChange(StoreDataChangeEvent<Term> event) {}
		@Override
		public void onRecordChange(StoreRecordChangeEvent<Term> event) {
			Term term = event.getRecord().getModel();
			treeStore.update(termTermTreeNodeMap.get(term));
		}
		@Override
		public void onSort(StoreSortEvent<Term> event) {
			treeStore.fireEvent(new StoreSortEvent<Term>());
		}
	});
}*/

}
