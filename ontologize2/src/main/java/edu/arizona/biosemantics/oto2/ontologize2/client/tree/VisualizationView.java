package edu.arizona.biosemantics.oto2.ontologize2.client.tree;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreatePartEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateSubclassEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateSynonymEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceTermInRelationsEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemovePartEvent;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TermTreeNode;

public class VisualizationView extends SimpleContainer {

	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private EventBus eventBus;

	public VisualizationView(final EventBus eventBus) {
		this.eventBus = eventBus;
		
		TreeView subclassTree = new TreeView(eventBus) {
			@Override
			protected void bindEvents() {
				super.bindEvents();
				
				
				eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
					@Override
					public void onLoad(LoadCollectionEvent event) {
						setRootName("Thing");
						clearTree();
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
		};
		TreeView partsTree = new TreeView(eventBus) {
			private List<CreatePartEvent> addPartEvents = new LinkedList<CreatePartEvent>();
			
			@Override
			protected void bindEvents() {
				super.bindEvents();
				eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
					@Override
					public void onLoad(LoadCollectionEvent event) {
						setRootName(event.getCollection().getTaxonGroup().getDisplayName());
						clearTree();
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
			
			/*protected void refreshTree() {
				clearTree();
				Map<String, List<String>> parts = ModelController.getCollection().getParts();
				for(String parent : parts.keySet()) 
					this.addPart(parent, parts.get(parent));
			}
			
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
			}*/
		};
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
				}); */
			}
		};
		
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add(subclassTree, new VerticalLayoutData(1, 0.33));
		vlc.add(partsTree, new VerticalLayoutData(1, 0.33));
		vlc.add(synonymsTree, new VerticalLayoutData(1, 0.33));
		this.setWidget(vlc);
	}
	
}
