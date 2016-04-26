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
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.AddPartEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.DisambiguateTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TermTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TextTreeNodeProperties;
import edu.arizona.biosemantics.oto2.oto.client.event.LoadEvent;

public class TreeView extends SimpleContainer {
	
	private static final TextTreeNodeProperties textTreeNodeProperties = GWT.create(TextTreeNodeProperties.class);
	
	protected EventBus eventBus;

	protected TreeStore<TermTreeNode> store;
	protected TreeGrid<TermTreeNode> treeGrid;
	protected Map<Term, TermTreeNode> termNodeMap = new HashMap<Term, TermTreeNode>();
	protected TermTreeNode rootNode;


	public TreeView(EventBus eventBus) {
		this.eventBus = eventBus;
		store = new TreeStore<TermTreeNode>(textTreeNodeProperties.key());
		ColumnConfig<TermTreeNode, String> valueCol = new ColumnConfig<TermTreeNode, String>(textTreeNodeProperties.text());
		List<ColumnConfig<TermTreeNode, ?>> list = new ArrayList<ColumnConfig<TermTreeNode, ?>>();
		list.add(valueCol);
		ColumnModel<TermTreeNode> cm = new ColumnModel<TermTreeNode>(list);
		treeGrid = new TreeGrid<TermTreeNode>(store, cm, valueCol);
		this.add(treeGrid);
		
		bindEvents();
	}

	protected void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				store.clear();
				rootNode = new TermTreeNode(new Term(event.getCollection().getTaxonGroup().getDisplayName()));
				store.add(rootNode);
			}
		});
	}

	protected void replace(Term oldTerm, Term newTerm) {
		if(termNodeMap.containsKey(oldTerm)) {
			TermTreeNode oldNode = termNodeMap.get(oldTerm);
			
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
			
			TermTreeNode newNode = new TermTreeNode(newTerm);
			if(parent != null && index >= 0) 
				store.insert(parent, index, newNode);
			else if(parent != null)
				store.add(parent, newNode);
			else
				store.add(newNode);
			
			store.addSubTree(newNode, 0, subtrees);
		}
	}
	
}
