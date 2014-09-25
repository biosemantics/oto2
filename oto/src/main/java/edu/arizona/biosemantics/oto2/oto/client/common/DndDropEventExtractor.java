package edu.arizona.biosemantics.oto2.oto.client.common;

import java.util.LinkedList;
import java.util.List;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelPortlet;
import edu.arizona.biosemantics.oto2.oto.client.categorize.single.MainTermPortlet;
import edu.arizona.biosemantics.oto2.oto.client.uncategorize.TermsView;
import edu.arizona.biosemantics.oto2.oto.client.uncategorize.TermsView.BucketTreeNode;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermTreeNode;

public class DndDropEventExtractor {
	
	public static List<Term> getTerms(DndDragStartEvent event, Collection collection) {
		List<Term> terms = new LinkedList<Term>();
		Object data = event.getData();
		if (data != null) {
			return getTerms(data, collection);
		}
		return terms;
	}

	private static List<Term> getTerms(Object data, Collection collection) {
		List<Term> terms = new LinkedList<Term>();
		if (data instanceof List<?>) {
			for (Object element : (List<?>) data) {
				// drags from tree are of form treeNode, either from west or
				// another portlet
				if (element instanceof TreeStore.TreeNode<?>) {
					TreeStore.TreeNode<?> treeNode = (TreeStore.TreeNode<?>) element;

					// drags from west panel, only location of
					// labelTreeNodes
					if (treeNode.getData() instanceof BucketTreeNode) {
						BucketTreeNode bucketTreeNode = (BucketTreeNode) treeNode.getData();
						terms.addAll(bucketTreeNode.getBucket().getUncategorizedTerms(collection));
					}
					if (treeNode.getData() instanceof TermTreeNode) {
						TermTreeNode termTreeNode = (TermTreeNode) treeNode
								.getData();
						terms.add(termTreeNode.getTerm());
					}
				}
				// drags from listView are of Term
				if (element instanceof Term) {
					terms.add((Term) element);
				}

				// possibly still needed e.g. if from the inside the tree a
				// synonym is dropped on the portlet to remove synonymy and
				// add it as a main term
				/*
				 * if(element instanceof TermTreeNode) { TermTreeNode
				 * termTreeNode = (TermTreeNode)element;
				 * portletStore.add(new
				 * MainTermTreeNode(termTreeNode.getTerm())); } if(element
				 * instanceof CategoryTreeNode) { CategoryTreeNode
				 * categoryTreeNode = (CategoryTreeNode)element;
				 * 
				 * for(Term term :
				 * categoryTreeNode.getCategory().getTerms()) {
				 * portletStore.add(new MainTermTreeNode(term)); } }
				 */
			}
		}
		if(data instanceof Term)
			terms.add((Term)data);
		return terms;
	}
			
	public static List<Term> getTerms(DndDropEvent event, Collection collection) {
		List<Term> terms = new LinkedList<Term>();
		Object data = event.getData();
		if(data != null) {
			if(isSourceMainTermPortlet(event)) {
				if(data != null && data instanceof List) {
					List<Object> dataList = (List<Object>)data;
					Label label = (Label)dataList.remove(dataList.size() - 1);
					for(Object object : dataList) {
						terms.add((Term)object);
					}
				}
				return terms;
			} else {
				return getTerms(data, collection);
			}
		}
		return terms;
	}
	
	public static boolean isSourceLabelPortlet(DndDropEvent event) {
		if(event.getTarget() instanceof Tree) {
			Tree tree = (Tree)event.getTarget();
			return tree.getElement().getAttribute("source").startsWith("labelportlet");
		}
		return false;
	}
	
	public static boolean isSourceLabelOtherPortlet(DndDropEvent event, LabelPortlet portlet) {
		if(event.getTarget() instanceof Tree) {
			Tree tree = (Tree)event.getTarget();
			if(tree.getElement().getAttribute("source").startsWith("labelportlet")) {
				return !tree.getElement().getAttribute("source").split("-")[1].equals(
						portlet.getTree().getElement().getAttribute("source").split("-")[1]);
			}
		}
		return false;
	}
	
	public static boolean isSourceTermsView(DndDropEvent event) {
		if(event.getTarget() instanceof Tree) {
			Tree tree = (Tree)event.getTarget();
			return tree.getElement().getAttribute("source").equals("termsview");
		}
		if(event.getTarget() instanceof ListView) {
			ListView listView = (ListView)event.getTarget();
			return listView.getElement().getAttribute("source").equals("termsview");
		}
		return false;
	}
	
	public static LabelPortlet getLabelPortletSource(DndDropEvent event) {
		return (LabelPortlet)event.getTarget().getParent().getParent();
	}
	
	public static TermsView getTermsViewSource(DndDropEvent event) {
		return (TermsView)event.getTarget().getParent().getParent();
	}

	public static boolean isSourceMainTermPortlet(DndDropEvent event) {
		return event.getTarget() instanceof MainTermPortlet;
	}

	public static Label getLabel(DndDropEvent event) {
		Object data = event.getData();
		if(isSourceMainTermPortlet(event)) {
			if(data != null && data instanceof List) {
				List<Object> dataList = (List<Object>)data;
				if(dataList.size() == 2 && dataList.get(0) instanceof Term && dataList.get(1) instanceof Label) {
					return (Label)dataList.get(1);
				}
			}
		}
		return null;
	}
	
}
