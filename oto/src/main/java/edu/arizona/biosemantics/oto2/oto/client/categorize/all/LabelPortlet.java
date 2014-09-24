package edu.arizona.biosemantics.oto2.oto.client.categorize.all;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.AutoScrollSupport;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.dnd.core.client.TreeDragSource;
import com.sencha.gxt.widget.core.client.Portlet;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto2.oto.client.categorize.single.TermMenu;
import edu.arizona.biosemantics.oto2.oto.client.common.DndDropEventExtractor;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyRemoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeMoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelModifyEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymCreationEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymRemovalEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermRenameEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermTreeNode;
import edu.arizona.biosemantics.oto2.oto.shared.model.TextTreeNodeProperties;
import edu.arizona.biosemantics.oto2.oto.shared.model.TrashLabel;

public class LabelPortlet extends Portlet {
	
	public enum DropSource {
		INIT, PORTLET
	}
	
	public class MainTermTreeNode extends TermTreeNode {
		public MainTermTreeNode(Term term) {
			super(term);
		}
	}
	
	public class SynonymTermTreeNode extends TermTreeNode {
		public SynonymTermTreeNode(Term term) {
			super(term);
		} 
	}
	
	
	
	
	private static int ID;
	private static final TextTreeNodeProperties textTreeNodeProperties = GWT.create(TextTreeNodeProperties.class);

	private int id = ID++;
	private TreeStore<TermTreeNode> portletStore;
	private Label label;
	private Tree<TermTreeNode, String> tree;
	private EventBus eventBus;
	private Map<Term, TermTreeNode> termTermTreeNodeMap = new HashMap<Term, TermTreeNode>();
	private Collection collection;
	private LabelPortletsView labelPortletsView;

	public LabelPortlet(EventBus eventBus, Label label, Collection collection, LabelPortletsView labelPortletsView) {
		this(GWT.<FramedPanelAppearance> create(FramedPanelAppearance.class), eventBus, label, collection, labelPortletsView);
	}
	
	public LabelPortlet(FramedPanelAppearance appearance, final EventBus eventBus, Label label, final Collection collection, LabelPortletsView labelPortletsView) {
		super(appearance);
		this.eventBus = eventBus;
		this.label = label;
		this.collection = collection; 
		this.labelPortletsView = labelPortletsView;
		this.setHeadingText(label.getName());
		this.setExpanded(false);
		this.setAnimationDuration(500);
		this.setCollapsible(true);
		this.setAnimCollapse(false);
		
		if(!(this.label instanceof TrashLabel)) {
			final ToolButton toolButton = new ToolButton(ToolButton.GEAR);
			toolButton.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					LabelMenu menu = new LabelMenu(eventBus, LabelPortlet.this.label, collection);
					menu.show(toolButton);
				}});
			this.getHeader().addTool(toolButton);
			this.setContextMenu(new LabelMenu(eventBus, label, collection));
		}
		
		portletStore = new TreeStore<TermTreeNode>(textTreeNodeProperties.key());
		portletStore.setAutoCommit(true);
		portletStore.addSortInfo(new StoreSortInfo<TermTreeNode>(new Comparator<TermTreeNode>() {
			@Override
			public int compare(TermTreeNode o1, TermTreeNode o2) {
				return o1.getText().compareTo(o2.getText());
			}
		}, SortDir.ASC));
		tree = new Tree<TermTreeNode, String>(portletStore, textTreeNodeProperties.text());
		tree.getElement().setAttribute("source", "labelportlet-" + id);
		tree.setContextMenu(new TermMenu(eventBus, collection, label) {
			@Override
			public List<Term> getTerms() {
				final List<TermTreeNode> selected = tree.getSelectionModel().getSelectedItems();
				final List<Term> terms = new LinkedList<Term>();
				for(TermTreeNode node : selected) 
					terms.add(node.getTerm());
				return terms;
			}
		});
		FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
		flowLayoutContainer.add(tree);
		flowLayoutContainer.setScrollMode(ScrollMode.AUTO);
		flowLayoutContainer.getElement().getStyle().setProperty("maxHeight", "150px");
		
		add(flowLayoutContainer);
		
		bindEvents();
		setupDnD();
		
		for(Term mainTerm : label.getMainTerms()) {
			addMainTerm(mainTerm);
			for(Term synonym : label.getSynonyms(mainTerm)) 
				this.addSynonymTerm(mainTerm, synonym);
		}
	}
	
	public void addMainTerm(Term term) {
		MainTermTreeNode mainTermTreeNode = new MainTermTreeNode(term);
		if(!termTermTreeNodeMap.containsKey(term))  {
			portletStore.add(mainTermTreeNode);
			this.termTermTreeNodeMap.put(term, mainTermTreeNode);
		}
	}
	
	protected void addSynonymTerm(Term mainTerm, Term synonymTerm) {
		MainTermTreeNode mainTermTreeNode = null;
		TermTreeNode termTreeNode = termTermTreeNodeMap.get(mainTerm);
		if(termTreeNode != null) {
			if(termTreeNode instanceof MainTermTreeNode) {
				mainTermTreeNode = (MainTermTreeNode)termTreeNode;
				SynonymTermTreeNode synonymTermTreeNode = new SynonymTermTreeNode(synonymTerm);
				removeTerm(synonymTerm);
				termTermTreeNodeMap.put(synonymTerm, synonymTermTreeNode);
				portletStore.add(mainTermTreeNode, synonymTermTreeNode);
				this.termTermTreeNodeMap.put(synonymTerm, synonymTermTreeNode);
			} 	
		}
	}
	
	protected void removeSynonymTerm(Term mainTerm, Term synonym) {
		TermTreeNode termTreeNode = termTermTreeNodeMap.remove(synonym);
		if(termTreeNode != null && termTreeNode instanceof SynonymTermTreeNode) {
			portletStore.remove(termTreeNode);
			this.addMainTerm(synonym);
		}
	}
		
	private void removeTerm(Term term) {
		if(termTermTreeNodeMap.containsKey(term)) {
			TermTreeNode termTreeNode = termTermTreeNodeMap.get(term);
			if(termTreeNode instanceof MainTermTreeNode) {
				MainTermTreeNode mainTermTreeNode = (MainTermTreeNode)termTreeNode;
				List<TermTreeNode> synonyms = portletStore.getChildren(mainTermTreeNode);
				for(TermTreeNode synonym : synonyms)
					this.removeSynonymTerm(term, synonym.getTerm());
			}
			portletStore.remove(termTermTreeNodeMap.remove(term));
		}
	}
	
	private void removeTerms(List<Term> terms) {
		for(Term term : terms)
			this.removeTerm(term);
	}

	private void bindEvents() {
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.TermSelectHandler() {
			@Override
			public void onSelect(TermSelectEvent event) {
				Term term = event.getTerm();
				TermTreeNode termTreeNode = termTermTreeNodeMap.get(term);
				if(termTreeNode != null) {
					if(!tree.getSelectionModel().isSelected(termTreeNode)) {
						List<TermTreeNode> selectionTree = new LinkedList<TermTreeNode>();
						selectionTree.add(termTreeNode);
						tree.getSelectionModel().setSelection(selectionTree);
					}
					LabelPortlet.this.expand();
				}
			}
		});
		eventBus.addHandler(SynonymRemovalEvent.TYPE, new SynonymRemovalEvent.SynonymRemovalHandler() {
			@Override
			public void onSynonymRemoval(SynonymRemovalEvent event) {
				List<Term> synonyms = event.getSynonyms();
				Term mainTerm = event.getMainTerm();
				if(LabelPortlet.this.label.equals(label)) {
					for(Term synonym : synonyms) {
						LabelPortlet.this.removeSynonymTerm(mainTerm, synonym);
					}
				}
			}
		});
		eventBus.addHandler(SynonymCreationEvent.TYPE, new SynonymCreationEvent.SynonymCreationHandler() {
			@Override
			public void onSynonymCreation(SynonymCreationEvent event) {
				Term mainTerm = event.getMainTerm();
				List<Term> synonymTerms = event.getSynonymTerm();
				if(LabelPortlet.this.label.equals(label)) {
					for(Term synonymTerm : synonymTerms)
						LabelPortlet.this.addSynonymTerm(mainTerm, synonymTerm);
				}
			}
		});
		eventBus.addHandler(TermRenameEvent.TYPE, new TermRenameEvent.RenameTermHandler() {
			@Override
			public void onRename(TermRenameEvent event) {
				Term term = event.getTerm();
				if(termTermTreeNodeMap.get(term) != null) {
					if(portletStore.getAll().contains(termTermTreeNodeMap.get(term))) {
						portletStore.update(termTermTreeNodeMap.get(term));
					}
				}
			}
		});
		eventBus.addHandler(CategorizeCopyTermEvent.TYPE, new CategorizeCopyTermEvent.CategorizeCopyTermHandler() {
			@Override
			public void onCategorize(CategorizeCopyTermEvent event) {
				List<Term> terms = event.getTerms();
				List<Label> targetCategories = event.getTargetCategories();
				for(Label targetCategory : targetCategories) {
					if(targetCategory.equals(label)) {
						for(Term term : terms) 
							addMainTerm(term);
					}
				}
			}
		});
		eventBus.addHandler(LabelModifyEvent.TYPE, new LabelModifyEvent.ModifyLabelHandler()  {
			@Override
			public void onModify(LabelModifyEvent event) {
				if(label.equals(LabelPortlet.this.label))
					LabelPortlet.this.setHeadingText(label.getName());
			}
		});
		eventBus.addHandler(CategorizeMoveTermEvent.TYPE, new CategorizeMoveTermEvent.CategorizeMoveTermHandler() {
			@Override
			public void onCategorize(CategorizeMoveTermEvent event) {
				Label targetLabel = event.getTargetCategory();
				Label sourceLabel = event.getSourceCategory();
				List<Term> terms = event.getTerms();
				if(targetLabel.equals(label)) {
					for(Term term : terms)
						addMainTerm(term);
				}
				if(sourceLabel.equals(label)) {
					for(Term term : terms) {
						removeTerm(term);
					}
				}
			}
		});
		eventBus.addHandler(TermCategorizeEvent.TYPE, new TermCategorizeEvent.TermCategorizeHandler() {
			@Override
			public void onCategorize(TermCategorizeEvent event) {
				List<Label> labels = event.getLabels();
				List<Term> terms = event.getTerms();
				for(Label label : labels)
					if(LabelPortlet.this.label.equals(label)) {
						for(Term term : terms) {
							addMainTerm(term);
						}
					}
			}
		});
		eventBus.addHandler(TermUncategorizeEvent.TYPE, new TermUncategorizeEvent.TermUncategorizeHandler() {
			@Override
			public void onUncategorize(TermUncategorizeEvent event) {
				List<Label> oldLabels = event.getOldLabels();
				List<Term> terms = event.getTerms();
				for(Label oldLabel : oldLabels) {
					if(LabelPortlet.this.label.equals(oldLabel)) {
						for(Term term : terms)
							LabelPortlet.this.removeTerm(term);
					}
				}
			}
		});
		eventBus.addHandler(CategorizeCopyRemoveTermEvent.TYPE, new CategorizeCopyRemoveTermEvent.CategorizeCopyRemoveTermHandler() {
			@Override
			public void onRemove(CategorizeCopyRemoveTermEvent event) {
				Label label = event.getLabel();
				List<Term> terms = event.getTerms();
				if(LabelPortlet.this.label.equals(label)) {
					LabelPortlet.this.removeTerms(terms);
				}
			}
		});
		
		tree.getSelectionModel().addSelectionHandler(new SelectionHandler<TermTreeNode>() {
			@Override
			public void onSelection(SelectionEvent<TermTreeNode> event) {
				TermTreeNode termTreeNode = event.getSelectedItem();
				Term term = termTreeNode.getTerm();
				eventBus.fireEvent(new TermSelectEvent(term));
			}
		});
	}

	public class CopyMoveMenu extends Menu {
		
		public CopyMoveMenu(SelectionHandler<Item> copyHandler, SelectionHandler<Item> moveHandler) {
			MenuItem item = new MenuItem("Copy");
			item.addSelectionHandler(copyHandler);
			add(item);
			item = new MenuItem("Move");
			item.addSelectionHandler(moveHandler);
			add(item);
		}
		
	}
	
	private void setupDnD() {
		TreeDragSource<TermTreeNode> dragSource = new TreeDragSource<TermTreeNode>(tree);
		
		final DndDropHandler portalDropHandler = new DndDropHandler() {
			@Override
			public void onDrop(DndDropEvent event) {
				if(DndDropEventExtractor.isSourceCategorizeView(event)) {
					onDnd(event, DropSource.INIT);
				}
				if(DndDropEventExtractor.isSourceLabelOtherPortlet(event, LabelPortlet.this)) {
					onDnd(event, DropSource.PORTLET);
				}
			}

			private void onDnd(final DndDropEvent dropEvent, DropSource source) {
				switch(source) {
				case INIT:
					List<Term> terms = DndDropEventExtractor.getTerms(dropEvent, collection);
					eventBus.fireEvent(new TermCategorizeEvent(terms, label));
					LabelPortlet.this.expand();
					break;
				case PORTLET:
					Menu menu = new CopyMoveMenu(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							LabelPortlet sourcePortlet = DndDropEventExtractor.getLabelPortletSource(dropEvent);
							List<Term> terms = DndDropEventExtractor.getTerms(dropEvent, collection);
							
							eventBus.fireEvent(new CategorizeCopyTermEvent(terms, sourcePortlet.getLabel(), label));
						}
					}, new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							LabelPortlet sourcePortlet = DndDropEventExtractor.getLabelPortletSource(dropEvent);
							List<Term> terms = DndDropEventExtractor.getTerms(dropEvent, collection);
							eventBus.fireEvent(new CategorizeMoveTermEvent(terms, sourcePortlet.getLabel(), label));
						}
					});
					menu.show(LabelPortlet.this);
					LabelPortlet.this.expand();
					break;
				default:
					break;
				}
			}
		};
		DropTarget dropTarget = new DropTarget(this) {
			private AutoScrollSupport scrollSupport;

			//scrollSupport can only work correctly when initialized once the element to be scrolled is already attached to the page
			protected void onDragEnter(DndDragEnterEvent event) {
				super.onDragEnter(event);
				//AutoScrollSupport scrollSupport = ((PortalLayoutContainer)LabelPortlet.this.getParentLayoutWidget()).getScrollSupport();
				if (scrollSupport == null) {
					scrollSupport = new AutoScrollSupport(labelPortletsView.getElement());
					scrollSupport.setScrollRegionHeight(50);
					scrollSupport.setScrollDelay(100);
					scrollSupport.setScrollRepeatDelay(100);
				}	
				scrollSupport.start();
			}
		};
		dropTarget.setAllowSelfAsSource(false);
		dropTarget.setOperation(Operation.COPY);
		dropTarget.addDropHandler(portalDropHandler);
		
		// let our events take care of tree/list store updates, hence own
		// implementation to take care of move/copy		
		final StoreTargetTreeDropTarget<TermTreeNode> treeDropTarget = new StoreTargetTreeDropTarget<TermTreeNode>(
				tree);
		treeDropTarget.setAllowDropOnLeaf(true);
		treeDropTarget.setAllowSelfAsSource(true);
		treeDropTarget.setOperation(Operation.COPY);
		treeDropTarget.addDropHandler(new DndDropHandler() {
			@Override
			public void onDrop(DndDropEvent event) {
				Object data = event.getData();
				List<Term> synonymTerms = new LinkedList<Term>();
				if (data instanceof List) {
					List<?> list = (List<?>) data;
					if (list.get(0) instanceof TreeStore.TreeNode) {
						@SuppressWarnings("unchecked")
						List<TreeNode<TermTreeNode>> nodes = (List<TreeNode<TermTreeNode>>) list;
						for (TreeNode<TermTreeNode> node : nodes) {
							//drops from bucket tree are TermTreeNode, SynonymTermTreeNode can't be used as synonym again until made mainterm 
							if (node.getData() != null && node.getData() instanceof TermTreeNode && !(node.getData() instanceof SynonymTermTreeNode)) {
								synonymTerms.add(((TermTreeNode) node.getData()).getTerm());
							}					
						}
						//drops from bucket list
					} else if(list.get(0) instanceof Term) {
						List<Term> nodes = (List<Term>)list;
						synonymTerms.addAll(nodes);
					}
					
				}
				
				
				//not a target of this portlet, don't allow for now
				for(Term synonymTerm : synonymTerms)
					if(!label.containsTerm(synonymTerm)) {
						portalDropHandler.onDrop(event);
						return;
					}
						
				TermTreeNode target = treeDropTarget.getAndNullTarget();
				if(target != null) { 
					Term mainLabelTerm = target.getTerm();
					label.setSynonymy(mainLabelTerm, synonymTerms);
					eventBus.fireEvent(new SynonymCreationEvent(label,
							mainLabelTerm, synonymTerms));
				}
			}
		});
	}
	
	public Label getLabel() {
		return label;
	}
	
	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	public boolean containsMainTerm(Term term) {
		if(termTermTreeNodeMap.containsKey(term)) 
			return termTermTreeNodeMap.get(term) instanceof MainTermTreeNode;
		return false;
	}

	public Tree<TermTreeNode, String> getTree() {
		return tree;
	}	

}
