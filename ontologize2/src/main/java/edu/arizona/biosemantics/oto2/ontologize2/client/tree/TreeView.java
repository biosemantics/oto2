package edu.arizona.biosemantics.oto2.ontologize2.client.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ToStringValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.candidate.TermTreeNodeIconProvider;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.BucketTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.PairTermTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TermTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TextTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TextTreeNodeProperties;
import edu.arizona.biosemantics.oto2.oto.client.event.LoadEvent;

public class TreeView extends SimpleContainer {
	
	private static final TextTreeNodeProperties textTreeNodeProperties = GWT.create(TextTreeNodeProperties.class);
	
	protected EventBus eventBus;
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);

	protected TreeStore<PairTermTreeNode> store;
	protected Tree<PairTermTreeNode, PairTermTreeNode> tree;
	//protected TreeGrid<TermTreeNode> treeGrid;
	//protected TreeGrid<TermTreeNode> tree;
	protected Map<String, PairTermTreeNode> termNodeMap = new HashMap<String, PairTermTreeNode>();
	protected edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection collection;
	
	protected PairTermTreeNode rootNode;

	private ToolBar buttonBar;

	private String rootName = "";
	
	private String treeTitle = "";//The title of the tree, it's different from the ontology root name

	protected TextButton refreshButton;
	
	/**
	 * customerize the keyprovider
	 * @author maojin
	 *
	 */
	class KeyProvider implements ModelKeyProvider<PairTermTreeNode>{

		@Override
		public String getKey(PairTermTreeNode item) {
			return item.getId();
		}
	}
	
	
	public TreeView(String treeTitle, EventBus eventBus) {
		this.treeTitle = treeTitle;
		this.eventBus = eventBus;
		
		buttonBar = new ToolBar();
		
		Label titleLabel = new Label(this.treeTitle);//The title of the tree
		buttonBar.add(titleLabel);
		titleLabel.getElement().getStyle().setFontSize(11, Unit.PX);
		titleLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		titleLabel.getElement().getStyle().setPaddingLeft(3, Unit.PX);
		titleLabel.getElement().getStyle().setColor("#15428b");
		
		refreshButton = new TextButton("Refresh");
		refreshButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				//add refresh tree
				refreshTree();
			}

		});
		buttonBar.add(refreshButton);
		
		
		/*store = new TreeStore<TermTreeNode>(textTreeNodeProperties.key());
		ColumnConfig<TermTreeNode, String> valueCol = new ColumnConfig<TermTreeNode, String>(textTreeNodeProperties.text(), 300, "Tree");
		List<ColumnConfig<TermTreeNode, ?>> list = new ArrayList<ColumnConfig<TermTreeNode, ?>>();
		list.add(valueCol);
		ColumnModel<TermTreeNode> cm = new ColumnModel<TermTreeNode>(list);
		tree = new TreeGrid<TermTreeNode>(store, cm, valueCol);
		//treeGrid = new TreeGrid<TermTreeNode>(store, cm, valueCol);
		*/
		//store = new TreeStore<TermTreeNode>(textTreeNodeProperties.key());
		store = new TreeStore<PairTermTreeNode>(new KeyProvider());
		store.setAutoCommit(true);
		store.addSortInfo(new StoreSortInfo<PairTermTreeNode>(new Comparator<PairTermTreeNode>() {
			@Override
			public int compare(PairTermTreeNode o1, PairTermTreeNode o2) {
				//return o1.getText().compareTo(o2.getText());
				return o1.compareTo(o2);
			}
		}, SortDir.ASC));
		tree = new Tree<PairTermTreeNode, PairTermTreeNode>(store, new IdentityValueProvider<PairTermTreeNode>());
		//tree.setIconProvider(new TermTreeNodeIconProvider());
		tree.setCell(new AbstractCell<PairTermTreeNode>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	PairTermTreeNode value, SafeHtmlBuilder sb) {
				sb.append(SafeHtmlUtils.fromTrustedString("<div>" + value.getText() +  "</div>"));
			}
		});
		tree.getElement().setAttribute("source", "termsview");
		tree.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
		tree.setAutoExpand(true);

		bindEvents();
		
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add(buttonBar, new VerticalLayoutData(1, -1));
		//vlc.add(treeGrid, new VerticalLayoutData(1, 1));
		vlc.add(tree, new VerticalLayoutData(1, 1));
		this.setWidget(vlc);
	}

	protected void bindEvents() {
	}
	
	protected void clearTree() {
		store.clear();
		termNodeMap.clear();
		rootNode = new PairTermTreeNode(null, new Term(rootName));
		store.add(rootNode);
	}
	
	protected void refreshTree(){
		
	}

	/*
	protected void replace(Term oldTerm, Term newTerm) {
		if(termNodeMap.containsKey(oldTerm.getDisambiguatedValue())) {
			TermTreeNode oldNode = termNodeMap.get(oldTerm.getDisambiguatedValue());
			
			//preserve child subtrees
			List<TermTreeNode> children = store.getChildren(oldNode);
			List<TreeNode<TermTreeNode>> subtrees = new LinkedList<TreeNode<TermTreeNode>>();
			for(TermTreeNode child : children) {
				TreeNode<TermTreeNode> subtree = store.getSubTree(child);
				subtrees.add(subtree);
			}
			int index = store.indexOf(oldNode);
			TermTreeNode parent = store.getParent(oldNode);
			store.remove(oldNode);
			termNodeMap.remove(oldTerm.getDisambiguatedValue());
			
			TermTreeNode newNode = new TermTreeNode(newTerm);
			termNodeMap.put(newTerm.getDisambiguatedValue(), newNode);
			if(parent != null && index >= 0) 
				store.insert(parent, index, newNode);
			else if(parent != null)
				store.add(parent, newNode);
			else
				store.add(newNode);
			
			store.addSubTree(newNode, 0, subtrees);
		}
	}*/
	
	
	protected void setRootName(String rootName) {
		this.rootName = rootName;
	}
	
}
