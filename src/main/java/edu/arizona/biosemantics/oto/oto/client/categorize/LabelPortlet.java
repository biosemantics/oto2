package edu.arizona.biosemantics.oto.oto.client.categorize;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.dnd.core.client.TreeDragSource;
import com.sencha.gxt.widget.core.client.Portlet;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.CategorizeCopyTermEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.CategorizeMoveTermEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelsMergeEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelRenameEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermSelectEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;
import edu.arizona.biosemantics.oto.oto.shared.model.TermTreeNode;
import edu.arizona.biosemantics.oto.oto.shared.model.TextTreeNode;
import edu.arizona.biosemantics.oto.oto.shared.model.TextTreeNodeProperties;

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
	
	private static final TextTreeNodeProperties textTreeNodeProperties = GWT.create(TextTreeNodeProperties.class);
	private TreeStore<TextTreeNode> portletStore;
	private Label label;
	private Tree<TextTreeNode, String> tree;
	private EventBus eventBus;
	private Map<Term, TermTreeNode> termTermTreeNodeMap = new HashMap<Term, TermTreeNode>();

	public LabelPortlet(EventBus eventBus, Label label) {
		this.eventBus = eventBus;
		this.label = label;
		this.setHeadingText(label.getName());
		
		this.setCollapsible(true);
		this.setAnimCollapse(false);
		/*this.getHeader().addTool(new ToolButton(ToolButton.GEAR));
		this.getHeader().addTool(
				new ToolButton(ToolButton.CLOSE, new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						portlet.removeFromParent();
					}
				}));*/
		
		portletStore = new TreeStore<TextTreeNode>(textTreeNodeProperties.key());
		tree = new Tree<TextTreeNode, String>(portletStore, textTreeNodeProperties.text());
		add(tree);
		addTermsToStore();
		bindEvents();
		setupDnD();
	}
	
	private void addToStore(MainTermTreeNode mainTermTreeNode) {
		portletStore.add(mainTermTreeNode);
		this.termTermTreeNodeMap.put(mainTermTreeNode.getTerm(), mainTermTreeNode);
	}
	
	private void addToStore(SynonymTermTreeNode synonymTreeNode, MainTermTreeNode mainTermTreeNode) {
		portletStore.add(mainTermTreeNode, synonymTreeNode);
		this.termTermTreeNodeMap.put(synonymTreeNode.getTerm(), synonymTreeNode);
	}
	
	private void removeFromStore(Term term) {
		portletStore.remove(termTermTreeNodeMap.get(term));
		termTermTreeNodeMap.remove(term);
	}

	private void bindEvents() {
		eventBus.addHandler(CategorizeCopyTermEvent.TYPE, new CategorizeCopyTermEvent.CategorizeCopyTermHandler() {
			@Override
			public void onCategorize(List<Term> terms, Label sourceCategory, Label targetCategory) {
				if(targetCategory.equals(label)) {
					for(Term term : terms) 
						addToStore(new MainTermTreeNode(term));
				}
			}


		});
		eventBus.addHandler(LabelRenameEvent.TYPE, new LabelRenameEvent.RenameLabelHandler()  {
			@Override
			public void onRename(Label label) {
				LabelPortlet.this.setHeadingText(label.getName());
			}
		});
		eventBus.addHandler(CategorizeMoveTermEvent.TYPE, new CategorizeMoveTermEvent.CategorizeMoveTermHandler() {
			@Override
			public void onCategorize(List<Term> terms, Label sourceLabel,	Label targetLabel) {
				if(targetLabel.equals(label)) {
					for(Term term : terms)
						addToStore(new MainTermTreeNode(term));
				}
				if(sourceLabel.equals(label)) {
					for(Term term : terms) {
						removeFromStore(term);
					}
				}
			}
		});
		eventBus.addHandler(TermCategorizeEvent.TYPE, new TermCategorizeEvent.TermCategorizeHandler() {
			@Override
			public void onCategorize(List<Term> terms, Label label) {
				if(LabelPortlet.this.label.equals(label)) {
					for(Term term : terms)
						addToStore(new MainTermTreeNode(term));
				}
			}
		});
		/*eventBus.addHandler(LabelsMergeEvent.TYPE, new LabelsMergeEvent.MergeLabelsHandler() {
			@Override
			public void onMerge(Label source, Label destination) {
				if(LabelPortlet.this.label.equals(destination)) {
					for(Term term : source.getTerms())
						addToStore(new MainTermTreeNode(term));
				}
			}
		});*/
		eventBus.addHandler(TermUncategorizeEvent.TYPE, new TermUncategorizeEvent.TermUncategorizeHandler() {
			@Override
			public void onUncategorize(List<Term> terms, Label oldLabel) {
				if(LabelPortlet.this.label.equals(label)) {
				// TODO Auto-generated method stub
				}
			}
		});
		
		/*portletStore.addStoreRemoveHandler(new StoreRemoveHandler<TextTreeNode>() {
			@Override
			public void onRemove(StoreRemoveEvent<TextTreeNode> event) {
				if(event.getItem() instanceof MainTermTreeNode) {
					MainTermTreeNode mainTermTreeNode = (MainTermTreeNode)event.getItem();
					Category oldCategory = mainTermTreeNode.getTerm().getCategory();
					mainTermTreeNode.getTerm().setCategory(null);
					mainTermTreeNode.getTerm().getInitialCategory().addTerm(mainTermTreeNode.getTerm());
					List<Term> terms = new LinkedList<Term>();
					terms.add(mainTermTreeNode.getTerm());
					eventBus.fireEvent(new TermUncategorizeEvent(terms, oldCategory));
				}
				if(event.getItem() instanceof SynonymTermTreeNode) {
					SynonymTermTreeNode synonymTermTreeNode = (SynonymTermTreeNode)event.getItem();
					TextTreeNode parent = portletStore.getParent(synonymTermTreeNode);
					if(parent instanceof MainTermTreeNode) {
						MainTermTreeNode mainTermTreeNode = (MainTermTreeNode)parent;
						mainTermTreeNode.getTerm().removeSynonym(synonymTermTreeNode.getTerm());
						eventBus.fireEvent(new SynonymRemovalEvent(synonymTermTreeNode.getTerm(), ((MainTermTreeNode) parent).getTerm()));
					}
				}
			}
		});
		portletStore.addStoreAddHandler(new StoreAddHandler<TextTreeNode>() {
			@Override
			public void onAdd(StoreAddEvent<TextTreeNode> event) {
				List<TextTreeNode> nodes = event.getItems();
				for(TextTreeNode node : nodes) {
					if(node instanceof MainTermTreeNode) {
						MainTermTreeNode mainTermTreeNode = (MainTermTreeNode)node;
						mainTermTreeNode.getTerm().getInitialCategory().removeTerm(mainTermTreeNode.getTerm());
						mainTermTreeNode.getTerm().setCategory(CategoryPortlet.this.category);
						List<Term> terms = new LinkedList<Term>();
						terms.add(mainTermTreeNode.getTerm());
						eventBus.fireEvent(new TermCategorizeEvent(terms, CategoryPortlet.this.category));
					}
					if(node instanceof SynonymTermTreeNode) {
						SynonymTermTreeNode synonymTermTreeNode = (SynonymTermTreeNode)node;
						TextTreeNode parent = portletStore.getParent(node);
						if(parent instanceof MainTermTreeNode) {
							MainTermTreeNode mainTermTreeNode = (MainTermTreeNode)parent;
							mainTermTreeNode.getTerm().addSynonym(synonymTermTreeNode.getTerm());
							eventBus.fireEvent(new SynonymCreationEvent(synonymTermTreeNode.getTerm(), ((MainTermTreeNode) parent).getTerm()));
						}
					}
				}
			}
		});*/
		
		tree.getSelectionModel().addSelectionHandler(new SelectionHandler<TextTreeNode>() {
			@Override
			public void onSelection(SelectionEvent<TextTreeNode> event) {
				TextTreeNode node = event.getSelectedItem();
				if(node instanceof TermTreeNode) {
					TermTreeNode termTreeNode = (TermTreeNode)node;
					Term term = termTreeNode.getTerm();
					eventBus.fireEvent(new TermSelectEvent(term));
				}
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
		TreeDragSource<TextTreeNode> dragSource = new TreeDragSource<TextTreeNode>(tree);
		
		//.addDropHandler(handler)
		/*TreeDropTarget<TextTreeNode> dropTarget = new TreeDropTarget<TextTreeNode>(tree);
		dropTarget.setAllowDropOnLeaf(true);
		dropTarget.setAllowSelfAsSource(true);
		//let our events take care of tree/list store updates
		dropTarget.setOperation(Operation.COPY);
		
		dropTarget.addDropHandler(new DndDropHandler() {
			@Override
			public void onDrop(DndDropEvent event) {
				event.getData();
			}
		});*/
		
		DropTarget dropTarget = new DropTarget(this);
		dropTarget.setOperation(Operation.COPY);
		dropTarget.addDropHandler(new DndDropHandler() {
			@Override
			public void onDrop(DndDropEvent event) {
				if(DndDropEventExtractor.isSourceCategorizeView(event)) {
					onDnd(event, DropSource.INIT);
				}
				if(DndDropEventExtractor.isSourceLabelPortlet(event)) {
					onDnd(event, DropSource.PORTLET);
				}
			}

			private void onDnd(final DndDropEvent dropEvent, DropSource source) {
				switch(source) {
				case INIT:
					eventBus.fireEvent(new TermCategorizeEvent(DndDropEventExtractor.getTerms(dropEvent), label));
					break;
				case PORTLET:
					Menu menu = new CopyMoveMenu(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							LabelPortlet sourcePortlet = DndDropEventExtractor.getLabelPortletSource(dropEvent);
							eventBus.fireEvent(new CategorizeCopyTermEvent(DndDropEventExtractor.getTerms(dropEvent), sourcePortlet.getLabel(), label));
						}
					}, new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							LabelPortlet sourcePortlet = DndDropEventExtractor.getLabelPortletSource(dropEvent);
							eventBus.fireEvent(new CategorizeMoveTermEvent(DndDropEventExtractor.getTerms(dropEvent), sourcePortlet.getLabel(), label));
						}
					});
					menu.show(LabelPortlet.this);
					break;
				default:
					break;
				}
			}
		});
	}

	protected Label getLabel() {
		return label;
	}

	private void addTermsToStore() {
		for(Term term : label.getTerms()) {
			MainTermTreeNode mainTermTreeNode = new MainTermTreeNode(term);
			addToStore(mainTermTreeNode);
			for(Term synonym : term.getSynonyms()) {
				SynonymTermTreeNode synonymTermTreeNode = new SynonymTermTreeNode(synonym);
				addToStore(synonymTermTreeNode, mainTermTreeNode);
			}
		}
	}

}
