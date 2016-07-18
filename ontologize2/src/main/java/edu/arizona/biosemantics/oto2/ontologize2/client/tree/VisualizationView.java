package edu.arizona.biosemantics.oto2.ontologize2.client.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreatePartEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateSubclassEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateSynonymEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceTermInRelationsEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemovePartEvent;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.PairTermTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TermTreeNode;

public class VisualizationView extends SimpleContainer {

	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private EventBus eventBus;

	public VisualizationView(final EventBus eventBus) {
		this.eventBus = eventBus;
		
		TreeView subclassTree = new TreeView("Type-of Hierarchy", eventBus) {
			
			private List<PairTermTreeNode> foldedNodes = new ArrayList();
			//the first node of the term
			private Map<Term,PairTermTreeNode> termFirstNodeMap = new HashMap();
			
			
			
			@Override
			protected void bindEvents() {
				super.bindEvents();
				
				eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
					@Override
					public void onLoad(LoadCollectionEvent event) {
						collection = event.getCollection();
						setRootName("Thing");
						refreshTree();
					}
				});
				
				/*eventBus.addHandler(CreateSubclassEvent.TYPE, new CreateSubclassEvent.Handler() {
					@Override
					public void onCreate(CreateSubclassEvent event) {
						Term superclass = event.getSuperclass();
						
						if(!termNodeMap.containsKey(superclass.getDisambiguatedValue())) {
							termNodeMap.put(superclass.getDisambiguatedValue(), new TermTreeNode(superclass));
							store.add(rootNode, termNodeMap.get(superclass.getDisambiguatedValue()));
						}
						TermTreeNode superNode = termNodeMap.get(superclass.getDisambiguatedValue());
						List<TermTreeNode> subNodes = new LinkedList<TermTreeNode>();
						for(Term subclass : event.getSubclasses()) {
							if(!termNodeMap.containsKey(subclass.getDisambiguatedValue())) {
								TermTreeNode subNode = new TermTreeNode(subclass);
								termNodeMap.put(subclass.getDisambiguatedValue(), subNode);
								subNodes.add(subNode);
							}
						}
						
						store.add(superNode, subNodes);
						treeGrid.expandAll();
					}
				});*/
				
			}
			
			@Override
			protected void refreshTree() {
				clearTree();
				/*
				 * Map<String, List<String>> subclasses = collection.getSubclasses();
				 * for(String parent : subclasses.keySet()){
					Term parentTerm = collection.getTerm(parent);
					TermTreeNode parentNode = new TermTreeNode(parentTerm);
					//if(!termNodeMap.keySet().contains(parent)){
						termNodeMap.put(parent, parentNode);
						store.add(rootNode,parentNode);
						addSubclasses(parent, subclasses.get(parent));
					//}
				}*/
				Map<String, List<String>> subclasses = ModelController.getCollection().getFirstLevelClass();
				/**/
				//Breath-first
				Queue<PairTermNode> termQueue = new LinkedList<PairTermNode>();
				for(String parent : subclasses.keySet()){
					Term parentTerm = ModelController.getCollection().getTerm(parent);
					PairTermTreeNode parentNode = new PairTermTreeNode(rootNode,parentTerm);
					termNodeMap.put(parentNode.getId(), parentNode);
					addSubclasses(rootNode, parentNode, termQueue);
				}
				
				PairTermNode pairTerm = null;
				while((pairTerm = termQueue.poll())!=null){
					PairTermTreeNode parentNode = pairTerm.parent;
					PairTermTreeNode childNode = pairTerm.child;
					addSubclasses(parentNode, childNode, termQueue);
				}
				
				tree.expandAll();
				
				for(PairTermTreeNode foldedNode : foldedNodes){
					//tree.getSelectionModel().select(foldedNode, true);
					tree.setExpanded(foldedNode, false);
				}
			}
			
			
			/**
			 * add subclasses in the tree
			 * Depth-first Method
			 */
			protected void addSubclasses(PairTermTreeNode parentNode, List<Term> children) {
				for(Term child : children) {
					//if(!termNodeMap.keySet().contains(child.getValue())){
					PairTermTreeNode childNode = new PairTermTreeNode(parentNode,child);
						termNodeMap.put(child.getDisambiguatedValue(), childNode);
						store.add(parentNode, childNode);
						addSubclasses(childNode,ModelController.getCollection().getSubclasses(child));
					//}
				}
			}
			
			
			/**
			 * add subclasses in the tree
			 * Breadth-first Method
			 */
			protected void addSubclasses(PairTermTreeNode parentNode, PairTermTreeNode currentNode, Queue<PairTermNode> termQueue) {
				store.add(parentNode, currentNode);
				if(!termFirstNodeMap.keySet().contains(currentNode.getTerm())){
					termFirstNodeMap.put(currentNode.getTerm(), currentNode);
				}else{//this term already exist in one node, unfold both
					if(!subTreeOf(foldedNodes, currentNode)) foldedNodes.add(currentNode);
					PairTermTreeNode pttNode = termFirstNodeMap.get(currentNode.getTerm());
					if(!foldedNodes.contains(pttNode)) foldedNodes.add(pttNode);
				}
				
				try{
					
					List<Term> subclasses = ModelController.getCollection().getSubclasses(currentNode.getTerm());
					if(subclasses!=null){
						for(Term child:subclasses){
							PairTermTreeNode childNode = new PairTermTreeNode(currentNode,child);
							termNodeMap.put(childNode.getId(), childNode);
							termQueue.offer(new PairTermNode(currentNode,childNode));
						}
					}
				}catch(Exception e){
					Alerter.showAlert("exeption", e.getMessage());
				}
			}

			private boolean subTreeOf(List<PairTermTreeNode> foldedNodes2,
					PairTermTreeNode currentNode) {
				String curNodeId = currentNode.getId();
				for(PairTermTreeNode foldedNode : foldedNodes2){
					if(curNodeId.startsWith(foldedNode.getId())) return true;
				}
				return false;
			}
			
			@Override
			public void clearTree(){
				super.clearTree();
				foldedNodes.clear();
				termFirstNodeMap.clear();
			}
			
		};
		
		/**
		 * the three for PART relations
		 */
		TreeView partsTree = new TreeView("Part-of Hierarchy",eventBus) {
			//private List<CreatePartEvent> addPartEvents = new LinkedList<CreatePartEvent>();
			
			@Override
			protected void bindEvents() {
				super.bindEvents();
				eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
					@Override
					public void onLoad(LoadCollectionEvent event) {
						setRootName("Thing");
						refreshTree();
					}
				}); 
				
				/*eventBus.addHandler(AddPartEvent.TYPE, new AddPartEvent.Handler() {
					@Override
					public void onCreate(AddPartEvent event) {
						addPartEvents.add(event);
						addPart(event.getParent(), event.getParts());
					}

					private void addPart(Term parent, Term[] parts) {
						if(!termNodeMap.containsKey(parent.getDisambiguatedValue())) {
							termNodeMap.put(parent.getDisambiguatedValue(), new TermTreeNode(parent));
							store.add(rootNode, termNodeMap.get(parent.getDisambiguatedValue()));
						}
						TermTreeNode parentNode = termNodeMap.get(parent.getDisambiguatedValue());
						List<TermTreeNode> partNodes = new LinkedList<TermTreeNode>();
						List<TermTreeNode> moveNodes = new LinkedList<TermTreeNode>();
						for(Term part : parts) {
							if(!termNodeMap.containsKey(part.getDisambiguatedValue())) {
								TermTreeNode partNode = new TermTreeNode(part);
								termNodeMap.put(part.getDisambiguatedValue(), partNode);
								partNodes.add(partNode);
							} else {
								TermTreeNode partNode = termNodeMap.get(part.getDisambiguatedValue());
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
						if(termNodeMap.containsKey(event.getOldTerm().getDisambiguatedValue())) {
							replace(event.getOldTerm(), event.getNewTerm());
						}
					}
				});
				eventBus.addHandler(RemovePartEvent.TYPE, new RemovePartEvent.Handler() {
					@Override
					public void onRemove(RemovePartEvent event) {
						
					}
				});*/
				
				/*eventBus.addHandler(CreatePartEvent.TYPE, new CreatePartEvent.Handler() {
					@Override
					public void onCreate(CreatePartEvent event) {
						refreshTree();
					}
				});
				eventBus.addHandler(ReplaceTermInRelationsEvent.TYPE, new DisambiguateTermEvent.Handler() {
					@Override
					public void onDisambiguate(ReplaceTermInRelationsEvent event) {
						refreshTree();
					}
				});
				eventBus.addHandler(RemovePartEvent.TYPE, new RemovePartEvent.Handler() {
					@Override
					public void onRemove(RemovePartEvent event) {
						refreshTree();
					}
				});		*/		
			}
			
			@Override
			protected void refreshTree() {
				clearTree();
				Map<String, List<String>> parts = ModelController.getCollection().getFirstLevelParts();
				for(String parent : parts.keySet()){
					Term parentTerm = ModelController.getCollection().getTerm(parent);
					PairTermTreeNode parentNode = new PairTermTreeNode(rootNode, parentTerm);
					if(!termNodeMap.keySet().contains(parentNode.getId())){
						termNodeMap.put(parentNode.getId(), parentNode);
						store.add(rootNode,parentNode);
						addParts(parentNode, ModelController.getCollection().getParts(parentTerm));
					}
				}
				tree.expandAll();
			}
			
			
			/**
			 * add subclasses in the tree
			 */
			protected void addParts(PairTermTreeNode parentNode, List<Term> children) {
				for(Term child : children) {
					if(!termNodeMap.keySet().contains(child.getValue())){
						PairTermTreeNode childNode = new PairTermTreeNode(parentNode, child);
						termNodeMap.put(child.getDisambiguatedValue(), childNode);
						store.add(parentNode, childNode);
						addParts(childNode,ModelController.getCollection().getParts(child));
					}
				}
			}
			
			/**
			 * add parts in the PART tree
			
			private void addPart(String parent, List<String> parts) {
				if(!termNodeMap.containsKey(parent)) {
					termNodeMap.put(parent, new TermTreeNode(ModelController.getCollection().getTerm(parent)));
					store.add(rootNode, termNodeMap.get(parent));
				}
				TermTreeNode parentNode = termNodeMap.get(parent);
				List<TermTreeNode> partNodes = new LinkedList<TermTreeNode>();
				List<TermTreeNode> moveNodes = new LinkedList<TermTreeNode>();
				for(String part : parts) {
					if(!termNodeMap.containsKey(part)) {
						TermTreeNode partNode = new TermTreeNode(ModelController.getCollection().getTerm(part));
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
			} */
		};
		
		/*
		TreeView synonymsTree = new TreeView(eventBus) {
			@Override
			protected void bindEvents() {
				super.bindEvents();
				
				eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
					@Override
					public void onLoad(LoadCollectionEvent event) {
						setRootName("");
						clearTree();
					}
				}); 
				
				
				/*eventBus.addHandler(CreateSynonymEvent.TYPE, new CreateSynonymEvent.Handler() {
					@Override
					public void onCreate(CreateSynonymEvent event) {
						Term preferredTerm = event.getPreferredTerm();
						
						if(!termNodeMap.containsKey(preferredTerm.getDisambiguatedValue())) {
							termNodeMap.put(preferredTerm.getDisambiguatedValue(), new TermTreeNode(preferredTerm));
							store.add(rootNode, termNodeMap.get(preferredTerm.getDisambiguatedValue()));
						}
						TermTreeNode preferredNode = termNodeMap.get(preferredTerm.getDisambiguatedValue());
						List<TermTreeNode> synonymNodes = new LinkedList<TermTreeNode>();
						for(Term synonym : event.getSynonyms()) {
							if(!termNodeMap.containsKey(synonym.getDisambiguatedValue())) {
								TermTreeNode synonymNode = new TermTreeNode(synonym);
								termNodeMap.put(synonym.getDisambiguatedValue(), synonymNode);
								synonymNodes.add(synonymNode);
							}
						}
						
						store.add(preferredNode, synonymNodes);
						treeGrid.expandAll();
					}
				}); 
			}
		};*/
		
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add(subclassTree, new VerticalLayoutData(1, 0.5));
		vlc.add(partsTree, new VerticalLayoutData(1, 0.5));
		//vlc.add(synonymsTree, new VerticalLayoutData(1, 0.33));
		this.setWidget(vlc);
	}
	
	class PairTermNode{
		public PairTermTreeNode parent;
		public PairTermTreeNode child;
		
		public PairTermNode(PairTermTreeNode parent, PairTermTreeNode child){
			this.parent = parent;
			this.child = child;
		}
	}
}
