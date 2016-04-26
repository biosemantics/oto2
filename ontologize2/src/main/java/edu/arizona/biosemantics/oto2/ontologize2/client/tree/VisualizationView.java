package edu.arizona.biosemantics.oto2.ontologize2.client.tree;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.AddPartEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.AddSuperclassEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.AddSynonymEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.DisambiguateTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TermTreeNode;

public class VisualizationView extends SimpleContainer {

	private EventBus eventBus;

	public VisualizationView(final EventBus eventBus) {
		this.eventBus = eventBus;
		
		TreeView superclassTree = new TreeView(eventBus) {
			@Override
			protected void bindEvents() {
				super.bindEvents();
				eventBus.addHandler(AddSuperclassEvent.TYPE, new AddSuperclassEvent.Handler() {
					@Override
					public void onCreate(AddSuperclassEvent event) {
						Term subclass = event.getSubclass();
						
						if(!termNodeMap.containsKey(subclass)) {
							termNodeMap.put(subclass, new TermTreeNode(subclass));
							store.add(rootNode, termNodeMap.get(subclass));
						}
						TermTreeNode subNode = termNodeMap.get(subclass);
						List<TermTreeNode> superNodes = new LinkedList<TermTreeNode>();
						for(Term superclass : event.getSuperclasses()) {
							if(!termNodeMap.containsKey(superclass)) {
								TermTreeNode superNode = new TermTreeNode(superclass);
								termNodeMap.put(superclass, superNode);
								superNodes.add(superNode);
							}
						}
						
						store.add(subNode, superNodes);
						treeGrid.expandAll();
					}
				});
			}
		};
		TreeView partsTree = new TreeView(eventBus) {
			@Override
			protected void bindEvents() {
				super.bindEvents();
				eventBus.addHandler(AddPartEvent.TYPE, new AddPartEvent.Handler() {
					@Override
					public void onCreate(AddPartEvent event) {
						Term parent = event.getParent();
						
						if(!termNodeMap.containsKey(parent)) {
							termNodeMap.put(parent, new TermTreeNode(parent));
							store.add(rootNode, termNodeMap.get(parent));
						}
						TermTreeNode parentNode = termNodeMap.get(parent);
						List<TermTreeNode> partNodes = new LinkedList<TermTreeNode>();
						List<TermTreeNode> moveNodes = new LinkedList<TermTreeNode>();
						for(Term part : event.getParts()) {
							if(!termNodeMap.containsKey(part)) {
								TermTreeNode partNode = new TermTreeNode(part);
								termNodeMap.put(part, partNode);
								partNodes.add(partNode);
							} else {
								TermTreeNode partNode = termNodeMap.get(part);
								moveNodes.add(partNode);
							}
						}
						
						List<TreeNode<TermTreeNode>> list = new LinkedList<TreeNode<TermTreeNode>>();
						for(TermTreeNode moveNode : moveNodes) {
							list.add(store.getSubTree(moveNode));
							store.remove(moveNode);
						}
						store.addSubTree(parentNode, 0, list);
						//store.addSubTree(parentNode, 0, moveNodes);
						store.add(parentNode, partNodes);
						treeGrid.expandAll();
					}
				});
				eventBus.addHandler(DisambiguateTermEvent.TYPE, new DisambiguateTermEvent.Handler() {
					@Override
					public void onDisambiguate(DisambiguateTermEvent event) {
						if(termNodeMap.containsKey(event.getOldTerm())) {
							replace(event.getOldTerm(), event.getNewTerm());
						}
					}
				});
				
			}
		};
		TreeView synonymsTree = new TreeView(eventBus) {
			@Override
			protected void bindEvents() {
				super.bindEvents();
				eventBus.addHandler(AddSynonymEvent.TYPE, new AddSynonymEvent.Handler() {
					@Override
					public void onCreate(AddSynonymEvent event) {
						Term preferredTerm = event.getPreferredTerm();
						
						if(!termNodeMap.containsKey(preferredTerm)) {
							termNodeMap.put(preferredTerm, new TermTreeNode(preferredTerm));
							store.add(rootNode, termNodeMap.get(preferredTerm));
						}
						TermTreeNode preferredNode = termNodeMap.get(preferredTerm);
						List<TermTreeNode> synonymNodes = new LinkedList<TermTreeNode>();
						for(Term synonym : event.getSynonyms()) {
							if(!termNodeMap.containsKey(synonym)) {
								TermTreeNode synonymNode = new TermTreeNode(synonym);
								termNodeMap.put(synonym, synonymNode);
								synonymNodes.add(synonymNode);
							}
						}
						
						store.add(preferredNode, synonymNodes);
						treeGrid.expandAll();
					}
				});
			}
		};
		
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add(superclassTree, new VerticalLayoutData(1, 0.33));
		vlc.add(partsTree, new VerticalLayoutData(1, 0.33));
		vlc.add(synonymsTree, new VerticalLayoutData(1, 0.33));
		this.setWidget(vlc);
	}
	
}
