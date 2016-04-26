package edu.arizona.biosemantics.oto2.ontologize2.client.candidate;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DragSource;
import com.sencha.gxt.dnd.core.client.TreeDragSource;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeSelectionModel;

import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.BucketTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TermTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TextTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TextTreeNodeProperties;
import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelPortlet;
import edu.arizona.biosemantics.oto2.oto.client.common.dnd.MainTermSynonymsLabelDnd;

public class CandidateView extends SimpleContainer {

	private static final TextTreeNodeProperties textTreeNodeProperties = GWT.create(TextTreeNodeProperties.class);
	
	private TreeStore<TextTreeNode> treeStore;
	private Tree<TextTreeNode, TextTreeNode> termTree;
	private Map<Term, TermTreeNode> termTermTreeNodeMap = new HashMap<Term, TermTreeNode>();

	private EventBus eventBus;
	
	private CandidateView() {
		treeStore = new TreeStore<TextTreeNode>(textTreeNodeProperties.key());
		treeStore.setAutoCommit(true);
		treeStore.addSortInfo(new StoreSortInfo<TextTreeNode>(new Comparator<TextTreeNode>() {
			@Override
			public int compare(TextTreeNode o1, TextTreeNode o2) {
				return o1.getText().compareTo(o2.getText());
			}
		}, SortDir.ASC));
		termTree = new Tree<TextTreeNode, TextTreeNode>(treeStore, new IdentityValueProvider<TextTreeNode>());
		termTree.setIconProvider(new TermTreeNodeIconProvider());
		termTree.setCell(new AbstractCell<TextTreeNode>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	TextTreeNode value, SafeHtmlBuilder sb) {
				sb.append(SafeHtmlUtils.fromTrustedString("<div>" + value.getText() +  "</div>"));
			}
		});
		termTree.getElement().setAttribute("source", "termsview");
		termTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		TreeDragSource<TextTreeNode> dragSource = new TreeDragSource<TextTreeNode>(termTree) {
			@Override
			protected void onDragStart(DndDragStartEvent event) {
				super.onDragStart(event);
				List<Term> data = new LinkedList<Term>();
				for(TextTreeNode node : termTree.getSelectionModel().getSelectedItems()) {
					addTermTreeNodes(node, data);
				}
				event.setData(data);
			}
		};
		this.add(termTree);
	}
	
	protected void addTermTreeNodes(TextTreeNode node, List<Term> data) {
		if(node instanceof BucketTreeNode) {
			for(TextTreeNode child : termTree.getStore().getChildren(node)) {
				this.addTermTreeNodes(child, data);
			}
		} else if(node instanceof TermTreeNode) {
			Term term = ((TermTreeNode)node).getTerm().clone(true);
			data.add(term);
		}
	}

	public CandidateView(EventBus eventBus) {
		this();
		this.eventBus = eventBus;
		
		bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				setCollection();
			}
		}); 
	}

	public void addSelectionChangedHandler(SelectionChangedHandler<TextTreeNode> handler) {
		termTree.getSelectionModel().addSelectionChangedHandler(handler);
	}

	public void setCollection() {
		treeStore.clear();
		termTermTreeNodeMap.clear();
		
		Map<String, BucketTreeNode> bucketTreeNodes = new HashMap<String, BucketTreeNode>();				
		for(Term term : ModelController.getCollection().getTerms()) {
			String bucketsPath = term.getBuckets();
			createBucketNodes(bucketTreeNodes, bucketsPath);
			addTermTreeNode(bucketTreeNodes.get(bucketsPath), new TermTreeNode(term));
		}
		
		initializeCollapsing(bucketTreeNodes);
	}

	protected void createBucketNodes(Map<String, BucketTreeNode> bucketsMap, String bucketsPath) {
		String[] buckets = bucketsPath.split("/");
		String cumulativePath = "";
		String parentPath = "";
		for(String bucket : buckets) {
			if(!bucket.isEmpty()) {
				cumulativePath += "/" + bucket;
				if(!bucketsMap.containsKey(cumulativePath)) {
					BucketTreeNode bucketTreeNode = new BucketTreeNode(new Bucket(bucket));
					if(parentPath.isEmpty())
						treeStore.add(bucketTreeNode);
					else
						treeStore.add(bucketsMap.get(parentPath), bucketTreeNode);
					bucketsMap.put(cumulativePath, bucketTreeNode);
				}
				parentPath = cumulativePath;
			}
		}
	}

	protected void addTermTreeNode(BucketTreeNode bucketNode, TermTreeNode termTreeNode) {
		this.termTermTreeNodeMap.put(termTreeNode.getTerm(), termTreeNode);
		this.treeStore.add(bucketNode, termTreeNode);
	}
	
	private void initializeCollapsing(Map<String, BucketTreeNode> bucketTreeNodes) {
		for(BucketTreeNode node : bucketTreeNodes.values()) {
			if(treeStore.getChildren(node).get(0) instanceof TermTreeNode) {
				termTree.setExpanded(node, false);
			} else {
				termTree.setExpanded(node, true);
			}
		}
	}

	public Tree<TextTreeNode, TextTreeNode> getTree() {
		return termTree;
	}

	public TreeStore<TextTreeNode> getTreeStore() {
		return treeStore;
	}

	public Map<Term, TermTreeNode> getTermTermTreeNodeMap() {
		return termTermTreeNodeMap;
	}
	
}
