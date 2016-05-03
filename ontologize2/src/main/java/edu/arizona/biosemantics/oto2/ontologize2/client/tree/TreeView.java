package edu.arizona.biosemantics.oto2.ontologize2.client.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.ToStringValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
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
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TermTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TextTreeNodeProperties;
import edu.arizona.biosemantics.oto2.oto.client.event.LoadEvent;

public class TreeView extends SimpleContainer {
	
	private static final TextTreeNodeProperties textTreeNodeProperties = GWT.create(TextTreeNodeProperties.class);
	
	protected EventBus eventBus;
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);

	protected TreeStore<TermTreeNode> store;
	protected TreeGrid<TermTreeNode> treeGrid;
	protected Map<String, TermTreeNode> termNodeMap = new HashMap<String, TermTreeNode>();
	protected TermTreeNode rootNode;

	private ToolBar buttonBar;

	private String rootName = "";


	public TreeView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		buttonBar = new ToolBar();
		TextButton refreshButton = new TextButton("Refresh");
		refreshButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				
			}
		});
		buttonBar.add(refreshButton);
		
		store = new TreeStore<TermTreeNode>(textTreeNodeProperties.key());
		ColumnConfig<TermTreeNode, String> valueCol = new ColumnConfig<TermTreeNode, String>(textTreeNodeProperties.text(), 300, "Tree");
		List<ColumnConfig<TermTreeNode, ?>> list = new ArrayList<ColumnConfig<TermTreeNode, ?>>();
		list.add(valueCol);
		ColumnModel<TermTreeNode> cm = new ColumnModel<TermTreeNode>(list);
		treeGrid = new TreeGrid<TermTreeNode>(store, cm, valueCol);

		bindEvents();
		
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add(buttonBar, new VerticalLayoutData(1, -1));
		vlc.add(treeGrid, new VerticalLayoutData(1, 1));
		this.setWidget(vlc);
	}

	protected void bindEvents() {
		
	}
	
	protected void clearTree() {
		store.clear();
		termNodeMap.clear();
		rootNode = new TermTreeNode(new Term(rootName));
		store.add(rootNode);
	}

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
	}
	
	protected void setRootName(String rootName) {
		this.rootName = rootName;
	}
	
}
