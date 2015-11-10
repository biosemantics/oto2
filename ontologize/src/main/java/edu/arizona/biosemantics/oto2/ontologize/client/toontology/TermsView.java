package edu.arizona.biosemantics.oto2.ontologize.client.toontology;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto2.ontologize.client.Ontologize;
import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.common.AllowSurpressSelectEventsTreeSelectionModel;
import edu.arizona.biosemantics.oto2.ontologize.client.event.AddCommentEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologySynonymSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RefreshOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RefreshOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RefreshSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologySynonymSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SetColorEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.TermMarkUselessEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Color;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Comment;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.TermProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Bucket;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.BucketTreeNode;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.TermTreeNode;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.TextTreeNode;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.TextTreeNodeProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public class TermsView implements IsWidget {
	
	private class TermMenu extends Menu implements BeforeShowHandler {
		
		public TermMenu() {
			this.addBeforeShowHandler(this);
			this.setWidth(140);
		}

		@Override
		public void onBeforeShow(BeforeShowEvent event) {
			this.clear();
			List<Term> selected = new LinkedList<Term>();
			List<TextTreeNode> nodes = termTreeSelectionModel.getSelectedItems();	
			for(TextTreeNode node : nodes) {
				if(node instanceof TermTreeNode) {
					selected.add(((TermTreeNode)node).getTerm());
				} else if(node instanceof BucketTreeNode) {
					for(TextTreeNode child : treeStore.getChildren(node)) {
						if(node instanceof TermTreeNode) {
							selected.add(((TermTreeNode)node).getTerm());
						}
					}
				}
			}
			
			if(selected == null || selected.isEmpty()) {
				event.setCancelled(true);
				this.hide();
			} else {
				this.add(new HeaderMenuItem("Term"));
				this.add(createMarkUseless(selected));
				if(selected.size() == 1) {
					this.add(createRename(selected));
				}
				this.add(new HeaderMenuItem("Annotation"));
				this.add(createComment(selected));
				
				if(!collection.getColors().isEmpty()) {
					this.add(createColorize(selected));
				} 
			}
			
			if(this.getWidgetCount() == 0)
				event.setCancelled(true);
		}

		private Widget createColorize(final List<Term> selected) {
			final MenuItem colorizeItem = new MenuItem("Colorize");
			Menu colorMenu = new Menu();
			MenuItem offItem = new MenuItem("None");
			offItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					collection.setColorizations((java.util.Collection)selected, null);
					collectionService.update(collection, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Alerter.failedToSetColor();
						}
						@Override
						public void onSuccess(Void result) {
							eventBus.fireEvent(new SetColorEvent(selected, null, true));
						}
					});
				}
			});
			colorMenu.add(offItem);
			for(final Color color : collection.getColors()) {
				MenuItem colorItem = new MenuItem(color.getUse());
				colorItem.getElement().getStyle().setProperty("backgroundColor", "#" + color.getHex());
				colorItem.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						collection.setColorizations((java.util.Collection)selected, color);
						collectionService.update(collection, new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Alerter.failedToSetColor();
							}
							@Override
							public void onSuccess(Void result) {
								eventBus.fireEvent(new SetColorEvent(selected, color, true));
							}
						});
					}
				});
				colorMenu.add(colorItem);
			}
			colorizeItem.setSubMenu(colorMenu);
			return colorizeItem;
		}

		private Widget createComment(final List<Term> selected) {
			MenuItem comment = new MenuItem("Comment");
			final Term term = selected.get(0);
			comment.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Comment", "");
					box.getTextArea().setValue(getUsersComment(term));
					box.addHideHandler(new HideHandler() {
						@Override
						public void onHide(HideEvent event) {
							final Comment newComment = new Comment(Ontologize.user, box.getValue());
							collection.addComments((java.util.Collection)selected, newComment);
							collectionService.update(collection, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Alerter.addCommentFailed(caught);
								}
								@Override
								public void onSuccess(Void result) {
									eventBus.fireEvent(new AddCommentEvent(
											(java.util.Collection)selected, newComment));
									String comment = Format.ellipse(box.getValue(), 80);
									String message = Format.substitute("'{0}' saved", new Params(comment));
									Info.display("Comment", message);
								}
							});
						}
					});
					box.show();
				}
			});
			return comment;
		}

		private Widget createRename(List<Term> selected) {
			final Term term = selected.get(0);
			MenuItem rename = new MenuItem("Correct Spelling");
			rename.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					Alerter.dialogRename(eventBus, term, collection);
				}
			});
			return rename;
		}

		private Widget createMarkUseless(final List<Term> selected) {
			MenuItem markUseless = new MenuItem("Mark");
			Menu subMenu = new Menu();
			markUseless.setSubMenu(subMenu);
			MenuItem useless = new MenuItem("Not Usefull");
			MenuItem useful = new MenuItem("Useful");
			subMenu.add(useless);
			subMenu.add(useful);
			useless.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					for(Term term : selected)
						term.setRemoved(true);
					eventBus.fireEvent(new TermMarkUselessEvent(selected, true));
				}
			});
			useful.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					for(Term term : selected)
						term.setRemoved(false);
					eventBus.fireEvent(new TermMarkUselessEvent(selected, false));
				}
			});
			return markUseless;
		}

		protected String getUsersComment(Term term) {
			//collection.getC
			return "";
		}
	}

	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);
	private static final TermProperties termProperties = GWT.create(TermProperties.class);
	private static final TextTreeNodeProperties textTreeNodeProperties = GWT.create(TextTreeNodeProperties.class);
	
	private TreeStore<TextTreeNode> treeStore;
	private Map<Term, TermTreeNode> termTermTreeNodeMap = new HashMap<Term, TermTreeNode>();
	private Tree<TextTreeNode, TextTreeNode> termTree;
	private TextButton refreshButton = new TextButton("Refresh");
	private EventBus eventBus;
	private AllowSurpressSelectEventsTreeSelectionModel<TextTreeNode> termTreeSelectionModel = 
			new AllowSurpressSelectEventsTreeSelectionModel<TextTreeNode>();

	private BucketTreeNode availableTermsNode;
	private BucketTreeNode removedTermsNode;
	private BucketTreeNode removedStructureTermsNode;
	private BucketTreeNode availableStructureTermsNode;
	private BucketTreeNode availableCharacterTermsNode;
	private BucketTreeNode removedCharacterTermsNode;
	protected Collection collection;
	private VerticalLayoutContainer vertical;
	private TabPanel tabPanel;
	
	public TermsView(EventBus eventBus) {
		this.eventBus = eventBus;
		treeStore = new TreeStore<TextTreeNode>(textTreeNodeProperties.key());
		treeStore.setAutoCommit(true);
		treeStore.addSortInfo(new StoreSortInfo<TextTreeNode>(new Comparator<TextTreeNode>() {
			@Override
			public int compare(TextTreeNode o1, TextTreeNode o2) {
				return o1.getText().compareTo(o2.getText());
			}
		}, SortDir.ASC));
		termTree = new Tree<TextTreeNode, TextTreeNode>(treeStore, new IdentityValueProvider<TextTreeNode>());
		termTree.setIconProvider(new TermTreeNodeIconProvider(eventBus));
		termTree.setCell(new AbstractCell<TextTreeNode>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	TextTreeNode textTreeNode, SafeHtmlBuilder sb) {
					if(textTreeNode instanceof TermTreeNode) {
						TermTreeNode termTreeNode = (TermTreeNode)textTreeNode;
						Term term = termTreeNode.getTerm();
						String text = term.getTerm();
						
						String iris = "";
						for(String iri : collection.getExistingIRIs(term)) {
							iris += iri + ", ";
						}
						if(!iris.isEmpty())
							iris = iris.substring(0, iris.length() - 2);
						if(!iris.isEmpty())
							text += " (" + iris + ")";
						if(collection.hasColorization(term)) {
							String colorHex = collection.getColorization(term).getHex();
							sb.append(SafeHtmlUtils.fromTrustedString("<div style='padding-left:5px; padding-right:5px; background-color:#" + colorHex + 
									"'>" + 
									text + "</div>"));
						} else {
							sb.append(SafeHtmlUtils.fromTrustedString("<div style='padding-left:5px; padding-right:5px'>" + text +
									"</div>"));
						}
					} else {
						sb.append(SafeHtmlUtils.fromTrustedString("<div style='padding-left:5px; padding-right:5px'>" + textTreeNode.getText() +
								"</div>"));
					}
			}
		});
		termTree.setSelectionModel(termTreeSelectionModel);
		termTree.getElement().setAttribute("source", "termsview");
		termTree.setContextMenu(new TermMenu());

		vertical = new VerticalLayoutContainer();
		vertical.add(termTree, new VerticalLayoutData(1, 1));
		vertical.add(refreshButton, new VerticalLayoutData(1, -1));
		
		tabPanel = new TabPanel();
		tabPanel.add(vertical, "Terms");
		
		bindEvents();
	}
	
	private void bindEvents() {
		refreshButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				toOntologyService.refreshSubmissionStatuses(collection, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToRefreshSubmissions();
					}
					@Override
					public void onSuccess(Void result) {
						eventBus.fireEvent(new RefreshSubmissionsEvent());
					}
				});
			}
		});
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				TermsView.this.collection = event.getCollection();
				treeStore.clear();
				
				Map<String, BucketTreeNode> bucketTreeNodes = new HashMap<String, BucketTreeNode>();				
				for(Term term : event.getCollection().getTerms()) {
					String bucketsPath = term.getBuckets();
					createBucketNodes(bucketTreeNodes, bucketsPath);
					addTermTreeNode(bucketTreeNodes.get(bucketsPath), new TermTreeNode(term));
				}
				
				initializeCollapsing(bucketTreeNodes);
			}

			private void initializeCollapsing(Map<String, BucketTreeNode> bucketTreeNodes) {
				for(BucketTreeNode node : bucketTreeNodes.values()) {
					if(treeStore.getChildren(node).get(0) instanceof TermTreeNode) {
						termTree.setExpanded(node, false);
					} else {
						termTree.setExpanded(node, true);
					}
				}
			}
		});
		
		termTreeSelectionModel.addSelectionHandler(new SelectionHandler<TextTreeNode>() {
			@Override
			public void onSelection(SelectionEvent<TextTreeNode> event) {
				TextTreeNode node = event.getSelectedItem();
				if(node instanceof TermTreeNode) {
					TermTreeNode termTreeNode = (TermTreeNode)node;
					eventBus.fireEventFromSource(new TermSelectEvent(termTreeNode.getTerm()), TermsView.this);
				}
			}
		});
		
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.Handler() {
			@Override
			public void onSelect(TermSelectEvent event) {
				Term term = event.getTerm();				
				TermTreeNode termTreeNode = termTermTreeNodeMap.get(term);
				if(termTreeNode != null && treeStore.findModel(termTreeNode) != null && !termTreeSelectionModel.isSelected(termTreeNode)) {
					List<TextTreeNode> selectionTree = new LinkedList<TextTreeNode>();
					selectionTree.add(termTreeNode);
					termTreeSelectionModel.setSelection(selectionTree, true);
				}
			}
		});
		
		eventBus.addHandler(SetColorEvent.TYPE, new SetColorEvent.SetColorEventHandler() {
			@Override
			public void onSet(SetColorEvent event) {
				for(Object object : event.getObjects()) {
					if(object instanceof Term) {
						update((Term)object);
					}
				}
			}
		});
		
		eventBus.addHandler(TermMarkUselessEvent.TYPE, new TermMarkUselessEvent.Handler() {
			@Override
			public void onSelect(TermMarkUselessEvent event) {
				List<TextTreeNode> treeStoreContent = treeStore.getAll();
				for(Term term : event.getTerms()) {
					if(termTermTreeNodeMap.get(term) != null && treeStoreContent.contains(termTermTreeNodeMap.get(term))) 
						treeStore.update(termTermTreeNodeMap.get(term));
				}
			}
		});
		
		eventBus.addHandler(CreateOntologyClassSubmissionEvent.TYPE, 
				new CreateOntologyClassSubmissionEvent.Handler() {
					@Override
					public void onSubmission(CreateOntologyClassSubmissionEvent event) {
						for(OntologyClassSubmission submission : event.getClassSubmissions()) {
							if(submission.hasTerm())
								update(submission.getTerm());
						}
					}
		});
		eventBus.addHandler(CreateOntologySynonymSubmissionEvent.TYPE, 
				new CreateOntologySynonymSubmissionEvent.Handler() {
					@Override
					public void onSubmission(CreateOntologySynonymSubmissionEvent event) {
						if(event.getSynonymSubmission().hasTerm())
							update(event.getSynonymSubmission().getTerm());
					}
		});
		eventBus.addHandler(RemoveOntologyClassSubmissionsEvent.TYPE, new RemoveOntologyClassSubmissionsEvent.Handler() {
			@Override
			public void onRemove(RemoveOntologyClassSubmissionsEvent event) {
				for(OntologyClassSubmission submission : event.getOntologyClassSubmissions()) 
					if(submission.hasTerm())
						update(submission.getTerm());
			}
		});
		eventBus.addHandler(RemoveOntologySynonymSubmissionsEvent.TYPE, new RemoveOntologySynonymSubmissionsEvent.Handler() {
			@Override
			public void onRemove(RemoveOntologySynonymSubmissionsEvent event) {
				for(OntologySynonymSubmission submission : event.getOntologySynonymSubmissions())
					if(submission.hasTerm())
						update(submission.getTerm());
			}
		});
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
						treeStore.add(bucketTreeNode);
					else
						treeStore.add(bucketsMap.get(parentPath), bucketTreeNode);
					bucketsMap.put(cumulativePath, bucketTreeNode);
				}
				parentPath = cumulativePath;
			}
		}
	}

	protected void addTermTreeNode(BucketTreeNode bucketNode, TermTreeNode termTreeNode) {
		this.termTermTreeNodeMap.put(termTreeNode.getTerm(), termTreeNode);
		this.treeStore.add(bucketNode, termTreeNode);
	}
	
	@Override
	public Widget asWidget() {
		return tabPanel;
	}
	
	private void update(Term term) {
		List<TextTreeNode> treeStoreContent = treeStore.getAll();
		if(termTermTreeNodeMap.get(term) != null && treeStoreContent.contains(termTermTreeNodeMap.get(term))) 
			treeStore.update(termTermTreeNodeMap.get(term));
	
	}
}