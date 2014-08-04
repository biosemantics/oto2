package edu.arizona.biosemantics.oto.oto.client.categorize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.dnd.core.client.ListViewDragSource;
import com.sencha.gxt.dnd.core.client.TreeDragSource;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermRenameEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermSelectEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;
import edu.arizona.biosemantics.oto.oto.shared.model.TermProperties;
import edu.arizona.biosemantics.oto.oto.shared.model.TermTreeNode;
import edu.arizona.biosemantics.oto.oto.shared.model.TextTreeNode;
import edu.arizona.biosemantics.oto.oto.shared.model.TextTreeNodeProperties;

public class TermsView extends TabPanel {
	
	public static class BucketTreeNode extends TextTreeNode {
		
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
	
	private static final TermProperties termProperties = GWT.create(TermProperties.class);
	private static final TextTreeNodeProperties textTreeNodeProperties = GWT.create(TextTreeNodeProperties.class);
	
	private TreeStore<TextTreeNode> treeStore;
	private ListStore<Term> listStore;

	private Map<Term, TermTreeNode> termTermTreeNodeMap;
	private Map<Bucket, BucketTreeNode> bucketBucketTreeNodeMap;
	private ListView<Term, String> listView;
	private Tree<TextTreeNode, String> termTree;
	private EventBus eventBus;
	
	public TermsView(EventBus eventBus) {
		super(GWT.<TabPanelAppearance> create(TabPanelBottomAppearance.class));
		this.eventBus = eventBus;
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
		eventBus.addHandler(TermRenameEvent.TYPE, new TermRenameEvent.RenameTermHandler() {
			@Override
			public void onRename(Term term) {
				//necessary to refresh anything here?
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