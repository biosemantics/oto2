package edu.arizona.biosemantics.oto2.oto.client.categorize.single;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.Portlet;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.PortalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent.BeforeSelectHandler;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelPortlet;
import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelPortlet.LabelMenu;
import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelPortlet.MainTermTreeNode;
import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelPortlet.TermMenu;
import edu.arizona.biosemantics.oto2.oto.client.common.Alerter;
import edu.arizona.biosemantics.oto2.oto.client.common.UncategorizeDialog;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeMoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelsMergeEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymCreationEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymRemovalEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermRenameEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermTreeNode;
import edu.arizona.biosemantics.oto2.oto.shared.model.TextTreeNodeProperties;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label.AddResult;

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
		
		final ToolButton toolButton = new ToolButton(ToolButton.GEAR);
		toolButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				MainTermMenu menu = new MainTermMenu(eventBus, collection, label, mainTerm);
				menu.show(toolButton);
			}});
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
		
		bindEvents();
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
}
