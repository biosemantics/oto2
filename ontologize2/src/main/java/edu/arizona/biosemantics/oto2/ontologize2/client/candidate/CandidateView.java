package edu.arizona.biosemantics.oto2.ontologize2.client.candidate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DragSource;
import com.sencha.gxt.dnd.core.client.TreeDragSource;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeSelectionModel;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Bucket;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.BucketTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TermTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TextTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.TextTreeNodeProperties;
import edu.arizona.biosemantics.oto2.oto.client.categorize.all.LabelPortlet;
import edu.arizona.biosemantics.oto2.oto.client.common.dnd.MainTermSynonymsLabelDnd;

public class CandidateView extends SimpleContainer {

	private static final TextTreeNodeProperties textTreeNodeProperties = GWT.create(TextTreeNodeProperties.class);
	
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private Tree<TextTreeNode, TextTreeNode> tree;
	private Map<String, TermTreeNode> termTermTreeNodeMap = new HashMap<String, TermTreeNode>();
	private Map<String, BucketTreeNode> bucketTreeNodesMap = new HashMap<String, BucketTreeNode>();	
	private EventBus eventBus;
	private ToolBar buttonBar;
	private edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection collection;
	
	private CandidateView() {
		TreeStore<TextTreeNode> treeStore = new TreeStore<TextTreeNode>(textTreeNodeProperties.key());
		treeStore.setAutoCommit(true);
		treeStore.addSortInfo(new StoreSortInfo<TextTreeNode>(new Comparator<TextTreeNode>() {
			@Override
			public int compare(TextTreeNode o1, TextTreeNode o2) {
				return o1.getText().compareTo(o2.getText());
			}
		}, SortDir.ASC));
		tree = new Tree<TextTreeNode, TextTreeNode>(treeStore, new IdentityValueProvider<TextTreeNode>());
		tree.setIconProvider(new TermTreeNodeIconProvider());
		tree.setCell(new AbstractCell<TextTreeNode>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	TextTreeNode value, SafeHtmlBuilder sb) {
				sb.append(SafeHtmlUtils.fromTrustedString("<div>" + value.getText() +  "</div>"));
			}
		});
		tree.getElement().setAttribute("source", "termsview");
		tree.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
		tree.setAutoExpand(true);
		
		TreeDragSource<TextTreeNode> dragSource = new TreeDragSource<TextTreeNode>(tree) {
			@Override
			protected void onDragStart(DndDragStartEvent event) {
				super.onDragStart(event);
				List<Term> data = new LinkedList<Term>();
				for(TextTreeNode node : tree.getSelectionModel().getSelectedItems()) {
					addTermTreeNodes(node, data);
				}
				event.setData(data);
			}
		};
		
		buttonBar = new ToolBar();
		TextButton importButton = new TextButton("Import");
		importButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Import terms", "");
				box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						List<Term> importList = new LinkedList<Term>();
						String input = box.getValue();
						String[] lines = input.split("\\n");
						for(String line : lines) {
							String[] terms = line.split(",");
							//TODO register on server first
							for(String term : terms){
								// jin add
								if(!collection.hasTerm(term)) {
									collectionService.createTerm(collection.getId(), collection.getSecret(), 
											term, "", "", new AsyncCallback<List<GwtEvent<?>>>() {
										@Override
										public void onFailure(Throwable caught) {
										
										}
										@Override
										public void onSuccess(List<GwtEvent<?>> result) {
											fireEvents(result);
										}				
									});
									importList.add(new Term(term));
								}
								//jin add end
							}
						}
						addTerms(importList);
					}
				});
				box.show();
			}
		});
		
		
		
		TextButton removeButton = new TextButton("Remove");
		Menu removeMenu = new Menu();
		MenuItem selectedRemove = new MenuItem("Selected");
		selectedRemove.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				remove(tree.getSelectionModel().getSelectedItems());
			}
		});
		
		MenuItem allRemove = new MenuItem("All");
		allRemove.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				remove(tree.getStore().getAll());
			}
		});
		removeMenu.add(selectedRemove);
		removeMenu.add(allRemove);
		removeButton.setMenu(removeMenu);
		
		buttonBar.add(importButton);
		buttonBar.add(removeButton);
		
		HorizontalLayoutContainer hlc = new HorizontalLayoutContainer();
		final TextField termField = new TextField();
		TextButton addButton = new TextButton("Add");
		addButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				String newTerm = termField.getValue().trim();
				if(newTerm.isEmpty()) 
					Alerter.showAlert("Add Term", "Term field is empty");
				else if(collection.hasTerm(newTerm)) 
					Alerter.showAlert("Add Term", "Term already exists");
				else
					if(!collection.hasTerm(newTerm)) {
						collectionService.createTerm(collection.getId(), collection.getSecret(), 
								newTerm, "", "", new AsyncCallback<List<GwtEvent<?>>>() {
							@Override
							public void onFailure(Throwable caught) {
							
							}
							@Override
							public void onSuccess(List<GwtEvent<?>> result) {
								fireEvents(result);
								//After adding, make it disappeared
								termField.setValue("");
							}							
						});
					} else {
						Alerter.showAlert("Create Term", "Term already exists");
					}
			}
		});
		hlc.add(termField, new HorizontalLayoutData(1, -1));
		hlc.add(addButton);
		
		FieldLabel field = new FieldLabel(hlc, "Add Term");
		
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add(buttonBar, new VerticalLayoutData(1, -1));
		vlc.add(tree, new VerticalLayoutData(1, 1));
		vlc.add(field, new VerticalLayoutData(1, 40));
		this.add(vlc);
	}
	
	protected void remove(List<TextTreeNode> nodes) {
		List<Term> remove = new LinkedList<Term>();
		for(TextTreeNode node : nodes) {
			if(node instanceof BucketTreeNode) {
				addTerms((BucketTreeNode)node, remove);
			}
			if(node instanceof TermTreeNode) {
				remove.add(((TermTreeNode)node).getTerm());
			}
		}
		collectionService.removeTerm(collection.getId(), collection.getSecret(), remove, new AsyncCallback<List<GwtEvent<?>> >() {
			@Override
			public void onFailure(Throwable caught) {
				
			}
			@Override
			public void onSuccess(List<GwtEvent<?>> result) {
				fireEvents(result);
			}
		});
	}

	private void addTerms(BucketTreeNode node, List<Term> list) {
		for(TextTreeNode childNode : tree.getStore().getChildren(node)) {
			if(childNode instanceof TermTreeNode) {
				list.add(((TermTreeNode)childNode).getTerm());
			} else if(childNode instanceof BucketTreeNode) {
				this.addTerms((BucketTreeNode)childNode, list);
			}
		}
	}

	protected void addTermTreeNodes(TextTreeNode node, List<Term> data) {
		if(node instanceof BucketTreeNode) {
			for(TextTreeNode child : tree.getStore().getChildren(node)) {
				this.addTermTreeNodes(child, data);
			}
		} else if(node instanceof TermTreeNode) {
			Term term = ((TermTreeNode)node).getTerm();
			data.add(term);
		}
	}

	public CandidateView(EventBus eventBus) {
		this();
		this.eventBus = eventBus;
		
		bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				collection = event.getCollection();
				setCollection();
			}
		}); 
		eventBus.addHandler(CreateTermEvent.TYPE, new CreateTermEvent.Handler() {
			@Override
			public void onCreate(CreateTermEvent event) {
				addTerms(Arrays.asList(event.getTerms()));
			}
		});
		eventBus.addHandler(RemoveTermEvent.TYPE, new RemoveTermEvent.Handler() {
			@Override
			public void onCreate(RemoveTermEvent event) {
				removeTerms(event.getTerms());
			}
		});
	}
	
	public void setCollection() {
		tree.getStore().clear();
		termTermTreeNodeMap.clear();
		bucketTreeNodesMap.clear();
		addTerms(collection.getTerms());
		initializeCollapsing(bucketTreeNodesMap);
	}

	protected void removeTerms(Term[] terms) {
		for(Term term : terms)
			if(termTermTreeNodeMap.containsKey(term.getDisambiguatedValue()))
				tree.getStore().remove(termTermTreeNodeMap.get(term.getDisambiguatedValue()));
	}

	private void addTerms(Collection<Term> terms) {
		for(Term term : terms) {
			String bucketsPath = collection.getBucket(term.getDisambiguatedValue());
			createBucketNodes(bucketTreeNodesMap, bucketsPath);
			addTermTreeNode(bucketTreeNodesMap.get(bucketsPath), new TermTreeNode(term));
		}
	}

	protected void createBucketNodes(Map<String, BucketTreeNode> bucketsMap, String bucketsPath) {
		String[] buckets = bucketsPath.split("/");
		String cumulativePath = "";
		String parentPath = "";
		for(String bucket : buckets) {
			if(!bucket.isEmpty()) {
				cumulativePath += "/" + bucket;
				if(!bucketsMap.containsKey(cumulativePath)) {
					BucketTreeNode bucketTreeNode = new BucketTreeNode(new Bucket(bucket));
					if(parentPath.isEmpty())
						tree.getStore().add(bucketTreeNode);
					else
						tree.getStore().add(bucketsMap.get(parentPath), bucketTreeNode);
					bucketsMap.put(cumulativePath, bucketTreeNode);
				}
				parentPath = cumulativePath;
			}
		}
	}

	protected void addTermTreeNode(BucketTreeNode bucketNode, TermTreeNode termTreeNode) {
		this.termTermTreeNodeMap.put(termTreeNode.getTerm().getDisambiguatedValue(), termTreeNode);
		this.tree.getStore().add(bucketNode, termTreeNode);
	}
	
	private void initializeCollapsing(Map<String, BucketTreeNode> bucketTreeNodes) {
		for(BucketTreeNode node : bucketTreeNodes.values()) {
			if(tree.getStore().getChildren(node).get(0) instanceof TermTreeNode) {
				tree.setExpanded(node, false);
			} else {
				tree.setExpanded(node, true);
			}
		}
	}	
	
	private void fireEvents(List<GwtEvent<?>> result) {
		for(GwtEvent<?> event : result)
			eventBus.fireEvent(event);
	}
}
