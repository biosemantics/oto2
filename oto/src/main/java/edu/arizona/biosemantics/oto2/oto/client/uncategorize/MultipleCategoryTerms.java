package edu.arizona.biosemantics.oto2.oto.client.uncategorize;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto2.oto.client.common.AllowSurpressSelectEventsTreeSelectionModel;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyRemoveTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.CategorizeCopyTermEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelRemoveEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.LabelsMergeEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymCreationEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.SynonymRemovalEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermMarkUselessEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermRenameEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.oto.client.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto2.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermProperties;
import edu.arizona.biosemantics.oto2.oto.shared.model.TermTreeNode;
import edu.arizona.biosemantics.oto2.oto.shared.model.TextTreeNode;
import edu.arizona.biosemantics.oto2.oto.shared.model.TextTreeNodeProperties;

public class MultipleCategoryTerms extends SimpleContainer {

	private static final TermProperties termProperties = GWT.create(TermProperties.class);
	private static final TextTreeNodeProperties textTreeNodeProperties = GWT.create(TextTreeNodeProperties.class);

	public static class LabelTreeNode extends TextTreeNode {
		
		private Label label;
		private Term term;

		public LabelTreeNode(Term term, Label label) {
			this.term = term;
			this.label = label;
		}

		@Override
		public String getText() {
			return label.getName();
		}
		
		public Label getLabel() {
			return label;
		}

		public Term getTerm() {
			return term;
		}
		
		@Override
		public String getId() {
			return "term-" + term.getId() + "-label-" + label.getId();
		}
		
	}
	
	private Map<Term, TermTreeNode> termTermTreeNodeMap;
	private Map<Term, Set<Label>> termLabelMap;
	private TreeStore<TextTreeNode> multipleCategoriesStore;
	private Tree<TextTreeNode, TextTreeNode> multipleCategoriesTree;
	private AllowSurpressSelectEventsTreeSelectionModel<TextTreeNode> termTreeSelectionModel = 
			new AllowSurpressSelectEventsTreeSelectionModel<TextTreeNode>();
	private EventBus eventBus;
	private Collection collection;
	
	public MultipleCategoryTerms(EventBus eventBus) {
		this.eventBus = eventBus;
		termTermTreeNodeMap = new HashMap<Term, TermTreeNode>();
		termLabelMap = new HashMap<Term, Set<Label>>();
		
		multipleCategoriesStore = new TreeStore<TextTreeNode>(textTreeNodeProperties.key());
		multipleCategoriesStore.setAutoCommit(true);
		multipleCategoriesStore.addSortInfo(new StoreSortInfo<TextTreeNode>(new Comparator<TextTreeNode>() {
			@Override
			public int compare(TextTreeNode o1, TextTreeNode o2) {
				return o1.getText().compareTo(o2.getText());
			}
		}, SortDir.ASC));
		
		multipleCategoriesTree = new Tree<TextTreeNode, TextTreeNode>(multipleCategoriesStore, new IdentityValueProvider<TextTreeNode>());
		multipleCategoriesTree.setCell(new AbstractCell<TextTreeNode>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	TextTreeNode textTreeNode, SafeHtmlBuilder sb) {
					String colorHex = "";
					if(textTreeNode instanceof TermTreeNode) {
						TermTreeNode termTreeNode = (TermTreeNode)textTreeNode;
						Term term = termTreeNode.getTerm();
						/*if(term.getUseless()) {
							sb.append(SafeHtmlUtils.fromTrustedString("<div style='padding-left:5px; padding-right:5px; background-color:gray; " +
									"color:white'>" + 
									textTreeNode.getText() + "</div>"));
						} else {*/
							sb.append(SafeHtmlUtils.fromTrustedString("<div style='padding-left:5px; padding-right:5px'>" + textTreeNode.getText() +
									"</div>"));
						//}
					} else {
						sb.append(SafeHtmlUtils.fromTrustedString("<div style=''>" + textTreeNode.getText() +
								"</div>"));
					}
			}
		});
		

		multipleCategoriesTree.setSelectionModel(termTreeSelectionModel);
		this.add(multipleCategoriesTree);
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(LabelRemoveEvent.TYPE, new LabelRemoveEvent.RemoveLabelHandler() {
			@Override
			public void onRemove(LabelRemoveEvent event) {
				Label label = event.getLabel();
				updateTerms(label.getTerms());
			}
		});
		eventBus.addHandler(TermUncategorizeEvent.TYPE, new TermUncategorizeEvent.TermUncategorizeHandler() {
			@Override
			public void onUncategorize(TermUncategorizeEvent event) {
				updateTerms(event.getTerms());
			}
		});
		eventBus.addHandler(TermCategorizeEvent.TYPE, new TermCategorizeEvent.TermCategorizeHandler() {
			@Override
			public void onCategorize(TermCategorizeEvent event) {
				updateTerms(event.getTerms());
			}
		});
		eventBus.addHandler(CategorizeCopyRemoveTermEvent.TYPE, new CategorizeCopyRemoveTermEvent.CategorizeCopyRemoveTermHandler() {
			@Override
			public void onRemove(CategorizeCopyRemoveTermEvent event) {
				updateTerms(event.getTerms());
			}
		});
		eventBus.addHandler(CategorizeCopyTermEvent.TYPE, new CategorizeCopyTermEvent.CategorizeCopyTermHandler() {
			@Override
			public void onCategorize(CategorizeCopyTermEvent event) {
				for(Label target : event.getTargetCategories()) {
					updateTerms(target.getTerms());
				}
			}
		});
		eventBus.addHandler(LabelsMergeEvent.TYPE, new LabelsMergeEvent.MergeLabelsHandler() {
			@Override
			public void onMerge(LabelsMergeEvent event) {
				updateTerms(event.getDestination().getTerms());
			}
		});
		eventBus.addHandler(SynonymCreationEvent.TYPE, new SynonymCreationEvent.SynonymCreationHandler() {
			@Override
			public void onSynonymCreation(SynonymCreationEvent event) {
				updateTerms(event.getSynonymTerm());
			}
		});
		eventBus.addHandler(SynonymRemovalEvent.TYPE, new SynonymRemovalEvent.SynonymRemovalHandler() {
			@Override
			public void onSynonymRemoval(SynonymRemovalEvent event) {
				updateTerms(event.getSynonyms());
			}
		});
	}

	protected void updateTerms(java.util.Collection<Term> terms) {
		for(Term term : terms) {
			List<Label> labels = collection.getLabels(term);
			if(labels.size() > 1) {
				//add to multiple categories. Event has to be processed first by model controller;
				//multiple categoreis also initialized when collection is loaded
				TermTreeNode termTreeNode = new TermTreeNode(term);
				if(termTermTreeNodeMap.containsKey(term))
					termTreeNode = termTermTreeNodeMap.get(term);
				else
					termTermTreeNodeMap.put(term, termTreeNode);
				
				multipleCategoriesStore.add(termTreeNode);
				for(Label label : labels)
					if(!termLabelMap.get(term).contains(label)) {
						multipleCategoriesStore.add(termTreeNode, new LabelTreeNode(term, label));
						
					}
			} else {
				if(termTermTreeNodeMap.containsKey(term))
					multipleCategoriesStore.remove(termTermTreeNodeMap.get(term));
				termLabelMap.put(term, new HashSet<Label>());
			}
		}
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
		for(Term term : collection.getTerms())
			termLabelMap.put(term, new HashSet<Label>());
		updateTerms(collection.getTerms());
	}
}
