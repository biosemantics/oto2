package edu.arizona.biosemantics.oto2.oto.client.categorize.single;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.DndDragStartHandler;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DragSource;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.dnd.core.client.TreeDragSource;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.widget.core.client.Portlet;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelMenu;
import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelPortlet;
import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelPortlet.CopyMoveMenu;
import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelPortlet.DropSource;
import edu.arizona.biosemantics.oto2.oto.client.common.DndDropEventExtractor;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeMoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelSelectEvent;
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

public class MainTermPortlet extends Portlet {
	
	private static final TextTreeNodeProperties textTreeNodeProperties = GWT.create(TextTreeNodeProperties.class);
	private EventBus eventBus;
	private Term mainTerm;
	private TreeStore<TermTreeNode> portletStore;
	private Tree<TermTreeNode, String> tree;
	private Label label;
	private Collection collection;
	private Map<Term, TermTreeNode> termTermTreeNodeMap = new HashMap<Term, TermTreeNode>();
	private PortalLayoutContainer portalLayoutContainer;
	private ToolButton toolButton;

	public MainTermPortlet(final EventBus eventBus, 
			final Collection collection, final Label label, final Term mainTerm, PortalLayoutContainer portalLayoutContainer) {
		this.eventBus = eventBus;
		this.mainTerm = mainTerm;
		this.label = label;
		this.collection = collection;
		this.portalLayoutContainer = portalLayoutContainer;
		this.setHeadingText(mainTerm.getTerm());
		this.setExpanded(false);
		this.setAnimationDuration(500);
		this.setCollapsible(true);
		this.setAnimCollapse(false);
		
		toolButton = new ToolButton(ToolButton.GEAR);
		this.getHeader().addTool(toolButton);
		this.setContextMenu(new MainTermMenu(eventBus, collection, label, mainTerm));
		
		portletStore = new TreeStore<TermTreeNode>(textTreeNodeProperties.key());
		portletStore.setAutoCommit(true);
		portletStore.addSortInfo(new StoreSortInfo<TermTreeNode>(new Comparator<TermTreeNode>() {
			@Override
			public int compare(TermTreeNode o1, TermTreeNode o2) {
				return o1.getText().compareTo(o2.getText());
			}
		}, SortDir.ASC));
		tree = new Tree<TermTreeNode, String>(portletStore, textTreeNodeProperties.text());
		tree.setContextMenu(new SynonymTermMenu(eventBus, collection, label, tree));
		
		FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
		flowLayoutContainer.add(tree);
		flowLayoutContainer.setScrollMode(ScrollMode.AUTO);
		flowLayoutContainer.getElement().getStyle().setProperty("maxHeight", "200px");
		
		add(flowLayoutContainer);
		
		for(Term synonymTerm : label.getSynonyms(mainTerm)) {
			addSynonymTerm(synonymTerm);
		}
		
		setupDnd();
		bindEvents();
	}

	private void setupDnd() {
		TreeDragSource<TermTreeNode> treeDragSource = new TreeDragSource<TermTreeNode>(tree);
		
		DragSource dragSource = new DragSource(this);
		dragSource.addDragStartHandler(new DndDragStartHandler() {
			@Override
			public void onDragStart(DndDragStartEvent event) {
				List<Object> dataList = new LinkedList<Object>();
				dataList.add(mainTerm);
				dataList.addAll(label.getSynonyms(mainTerm));
				dataList.add(label);
				event.setData(dataList);
			}
		});
		
		/*
			@Override
			protected void onDragStart(DndDragStartEvent event) {
				super.onDragStart(event);
				event.setData(mainTerm);
				List<Term> terms = DndDropEventExtractor.getTerms(event, collection);
				if (terms.isEmpty())
					event.setCancelled(true);
				else {
					setStatusText(terms.size() + " term(s) selected");
					event.getStatusProxy().update(
							Format.substitute(getStatusText(), terms.size()));
				}
			}
		};*/
	}

	private void bindEvents() {
		eventBus.addHandler(TermRenameEvent.TYPE, new TermRenameEvent.RenameTermHandler() {
			@Override
			public void onRename(TermRenameEvent event) {
				if(termTermTreeNodeMap.containsKey(event.getTerm())) {
					TermTreeNode node = termTermTreeNodeMap.get(event.getTerm());
					portletStore.update(node);
				}
				if(event.getTerm().equals(mainTerm))
					MainTermPortlet.this.setHeadingText(mainTerm.getTerm());
			}
		});
		eventBus.addHandler(TermUncategorizeEvent.TYPE, new TermUncategorizeEvent.TermUncategorizeHandler() {
			@Override
			public void onUncategorize(TermUncategorizeEvent event) {
				for(Term term : event.getTerms()) {
					if(termTermTreeNodeMap.containsKey(term)) {
						TermTreeNode node = termTermTreeNodeMap.remove(term);
						portletStore.remove(node);
					}
				}
			}
		});
		eventBus.addHandler(SynonymCreationEvent.TYPE, new SynonymCreationEvent.SynonymCreationHandler() {
			@Override
			public void onSynonymCreation(SynonymCreationEvent event) {
				if(event.getLabel().equals(label) && event.getMainTerm().equals(mainTerm)) {
					for(Term synonym : event.getSynonymTerm()) {
						addSynonymTerm(synonym);
					}
				}
			}
		});
		eventBus.addHandler(SynonymRemovalEvent.TYPE, new SynonymRemovalEvent.SynonymRemovalHandler() {
			@Override
			public void onSynonymRemoval(SynonymRemovalEvent event) {
				if(label.equals(event.getLabel()) && mainTerm.equals(event.getMainTerm())) {
					for(Term synonym : event.getSynonyms()) {
						removeSynonymTerm(synonym);
					}
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
		toolButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				MainTermMenu menu = new MainTermMenu(eventBus, collection,
						label, mainTerm);
				menu.show(toolButton);
			}
		});
		this.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new TermSelectEvent(mainTerm));
			}
		}, ClickEvent.getType());
	}
	
	//collapse button will only be available on attach, when initTools is called  in ContentPanel
	@Override
	protected void initTools() {
		super.initTools();
		for(Widget tool : this.getHeader().getTools()) {
			tool.addDomHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					eventBus.fireEvent(new TermSelectEvent(mainTerm));
				}
			}, ClickEvent.getType());
		}
	}

	protected void removeSynonymTerm(Term synonym) {
		TermTreeNode termTreeNode = termTermTreeNodeMap.remove(synonym);
		portletStore.remove(termTreeNode);
	}

	private void addSynonymTerm(Term term) {
		TermTreeNode termTreeNode = new TermTreeNode(term);
		if(!termTermTreeNodeMap.containsKey(term))  {
			portletStore.add(termTreeNode);
			this.termTermTreeNodeMap.put(term, termTreeNode);
		}
	}
		
	public Term getMainTerm() {
		return mainTerm;
	}
}
