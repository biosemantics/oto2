package edu.arizona.biosemantics.oto2.ontologize2.client.tree.back;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.data.shared.loader.DataProxy;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.VertexTreeNodeProperties;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

public class AllVertexStore implements DataProxy<VertexTreeNode, List<VertexTreeNode>> {
	
	private static final VertexTreeNodeProperties vertexTreeNodeProperties = GWT.create(VertexTreeNodeProperties.class);
	private TreeStore<VertexTreeNode> store;
	
	public AllVertexStore() {
		store = new TreeStore<VertexTreeNode>(vertexTreeNodeProperties.key());
		store.setAutoCommit(true);
		store.addSortInfo(new StoreSortInfo<VertexTreeNode>(new Comparator<VertexTreeNode>() {
			@Override
			public int compare(VertexTreeNode o1, VertexTreeNode o2) {
				Date d1 = getCreationDate(o1.getVertex());
				Date d2 = getCreationDate(o2.getVertex());
				return d1.compareTo(d2);
			}
		}, SortDir.DESC));
	}
	
	protected Date getCreationDate(Vertex v) {
		OntologyGraph g = ModelController.getCollection().getGraph();
		Date result = new Date();
		for(Edge in : g.getInRelations(v)) {
			if(in.getCreation().compareTo(result) < 0)
				result = in.getCreation();
		}
		return result;
	}

	@Override
	public void load(VertexTreeNode loadConfig,	Callback<List<VertexTreeNode>, Throwable> callback) {
		if(loadConfig == null)
			callback.onSuccess(store.getRootItems());
		else 
			callback.onSuccess(store.getChildren(loadConfig));
	}

	public List<VertexTreeNode> getChildren(VertexTreeNode parent) {
		return store.getChildren(parent);
	}

	public TreeNode<VertexTreeNode> getSubTree(VertexTreeNode childNode) {
		return store.getSubTree(childNode);
	}

	public void removeChildren(VertexTreeNode parent) {
		store.removeChildren(parent);
	}

	public void addSubTree(VertexTreeNode parent, int i, List<TreeNode<VertexTreeNode>> childNodes) {
		store.addSubTree(parent, i, childNodes);
	}

	public int getChildCount(VertexTreeNode node) {
		return store.getChildCount(node);
	}

	public void remove(VertexTreeNode node) {
		store.remove(node);
	}

	public void clear() {
		store.clear();
	}

	public List<VertexTreeNode> getAllChildren(VertexTreeNode parent) {
		return store.getAllChildren(parent);
	}

	public VertexTreeNode getParent(VertexTreeNode node) {
		return store.getParent(node);
	}

	public void add(VertexTreeNode node) {
		store.add(node);
	}

	public void add(VertexTreeNode parent, VertexTreeNode child) {
		store.add(parent, child);
	}

	public boolean hasChildren(VertexTreeNode parent) {
		return store.hasChildren(parent);
	}

	public List<VertexTreeNode> getRootItems() {
		return store.getRootItems();
	}

	/*@Override
	public void load(PagingLoadConfig loadConfig, Callback<PagingLoadResult<Row>, Throwable> callback) {
		if(loadConfig instanceof FilterPagingLoadConfig) {
			List<FilterConfig> filters = ((FilterPagingLoadConfig)loadConfig).getFilters();
		}
		int offset = loadConfig.getOffset();
		int limit = loadConfig.getLimit();
		List<? extends SortInfo> sortInfo = loadConfig.getSortInfo();
		List<Row> data = new LinkedList<Row>();
		for(int i=offset; i<offset+limit; i++) {
			Row row = store.get(i);
			if(row != null)
				data.add(row);
		}
		MyPagingLoadResult result = new MyPagingLoadResult(data, store.size(), loadConfig.getOffset());
		callback.onSuccess(result);
	}*/

}
