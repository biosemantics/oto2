package edu.arizona.biosemantics.oto2.steps.client.toontology;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.dom.AutoScrollSupport;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.dnd.core.client.ListViewDragSource;
import com.sencha.gxt.dnd.core.client.TreeDragSource;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto2.steps.client.OtoSteps;
import edu.arizona.biosemantics.oto2.steps.client.common.AllowSurpressSelectEventsListViewSelectionModel;
import edu.arizona.biosemantics.oto2.steps.client.common.AllowSurpressSelectEventsTreeSelectionModel;
import edu.arizona.biosemantics.oto2.steps.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.steps.client.event.TermSelectEvent;
import edu.arizona.biosemantics.oto2.steps.shared.model.Collection;
import edu.arizona.biosemantics.oto2.steps.shared.model.Term;
import edu.arizona.biosemantics.oto2.steps.shared.model.TermProperties;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.Bucket;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.BucketTreeNode;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.TermTreeNode;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.TextTreeNode;
import edu.arizona.biosemantics.oto2.steps.shared.model.toontology.TextTreeNodeProperties;

public class TermsView implements IsWidget {
	
	private class TermMenu extends Menu implements BeforeShowHandler {
		
		public TermMenu() {
			this.addBeforeShowHandler(this);
			this.setWidth(140);
		}

		@Override
		public void onBeforeShow(BeforeShowEvent event) {
			// TODO Auto-generated method stub
			
		}

		/*@Override
		public void onBeforeShow(BeforeShowEvent event) {
			this.clear();
			
			List<Term> viewSelected = new LinkedList<Term>();
			if(TermsView.this.getActiveWidget().equals(TermsView.this.termTree)) {
				List<TextTreeNode> nodes = termTreeSelectionModel.getSelectedItems();	
				for(TextTreeNode node : nodes)
					if(node instanceof TermTreeNode) {
						viewSelected.add(((TermTreeNode)node).getTerm());
					} else if(node instanceof BucketTreeNode) {
						viewSelected.addAll(((BucketTreeNode)node).getBucket().getTerms());
					}
			} else if(TermsView.this.getActiveWidget().equals(TermsView.this.listView)) {
				viewSelected = listViewSelectionModel.getSelectedItems();
			}
			
			final List<Term> selected = viewSelected;
			
			if(selected == null || selected.isEmpty()) {
				event.setCancelled(true);
				this.hide();
			} else {				
				final List<Term> terms = new LinkedList<Term>(selected);
				
				if(!collection.getLabels().isEmpty()) {
					Menu categorizeMenu = new Menu();
					VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
					final List<Label> categorizeLabels = new LinkedList<Label>();
					final TextButton categorizeButton = new TextButton("Categorize");
					categorizeButton.setEnabled(false);
					
					FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
					VerticalLayoutContainer checkBoxPanel = new VerticalLayoutContainer();
					flowLayoutContainer.add(checkBoxPanel);
					flowLayoutContainer.setScrollMode(ScrollMode.AUTOY);
					flowLayoutContainer.getElement().getStyle().setProperty("maxHeight", "150px");
					for(final Label collectionLabel : collection.getLabels()) {
						CheckBox checkBox = new CheckBox();
						checkBox.setBoxLabel(collectionLabel.getName());
						checkBox.setValue(false);
						checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
							@Override
							public void onValueChange(ValueChangeEvent<Boolean> event) {
								if(event.getValue())
									categorizeLabels.add(collectionLabel);
								else
									categorizeLabels.remove(collectionLabel);
								categorizeButton.setEnabled(!categorizeLabels.isEmpty());
							}
						});
						checkBoxPanel.add(checkBox);
					}
					verticalLayoutContainer.add(flowLayoutContainer);
					categorizeButton.addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							eventBus.fireEvent(new TermCategorizeEvent(terms, categorizeLabels));
							TermMenu.this.hide();
						}
					});
					verticalLayoutContainer.add(categorizeButton);
					categorizeMenu.add(verticalLayoutContainer);
					MenuItem categorize = new MenuItem("Categorize to");
					categorize.setSubMenu(categorizeMenu);
					this.add(new HeaderMenuItem("Categorize"));
					this.add(categorize);
					
					this.add(new HeaderMenuItem("Term"));
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
							eventBus.fireEvent(new TermMarkUselessEvent(terms, true));
						}
					});
					useful.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							eventBus.fireEvent(new TermMarkUselessEvent(terms, false));
						}
					});
					this.add(markUseless);
				}
				
				if(selected.size() == 1) {
					final Term term = selected.get(0);
					MenuItem rename = new MenuItem("Correct Spelling");
					rename.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							Alerter.dialogRename(eventBus, term, collection);
						}
					});
					this.add(rename);
					/*MenuItem split = new MenuItem("Split Term");
					split.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							final PromptMessageBox box = new PromptMessageBox(
									"Split Term", "Please input splitted terms' separated by space.");
							box.getButton(PredefinedButton.OK).addBeforeSelectHandler(new BeforeSelectHandler() {
								@Override
								public void onBeforeSelect(BeforeSelectEvent event) {
									if(box.getTextField().getValue().trim().isEmpty()) {
										event.setCancelled(true);
										AlertMessageBox alert = new AlertMessageBox("Empty", "Empty not allowed");
										alert.show();
									}
								}
							});
							box.getTextField().setValue(term.getTerm());
							box.getTextField().setAllowBlank(false);
							box.addHideHandler(new HideHandler() {
								@Override
								public void onHide(HideEvent event) {
									String newName = box.getValue();
									eventBus.fireEvent(new TermSplitEvent(term, newName));
								}
							});
							box.show();
						}
					});
					this.add(split);*/
		/*		}
								
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
								Comment newComment = new Comment(Oto.user, box.getValue());
								for(final Term term : selected) {
									collectionService.addComment(newComment, term.getId(), new AsyncCallback<Comment>() {
										@Override
										public void onSuccess(Comment result) {
											eventBus.fireEvent(new CommentEvent(term, result));
											String comment = Format.ellipse(box.getValue(), 80);
											String message = Format.substitute("'{0}' saved", new Params(comment));
											Info.display("Comment", message);
										}
										@Override
										public void onFailure(Throwable caught) {
											Alerter.addCommentFailed(caught);
										}
									});
								}
							}
						});
						box.show();
					}
				});
				this.add(comment);
			}
			
			if(this.getWidgetCount() == 0)
				event.setCancelled(true);
		}*/
	}
	
	//private toOntologyServiceAsync toOntologyService = GWT.create(toOntologyService.class);
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
		termTree.setCell(new AbstractCell<TextTreeNode>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	TextTreeNode textTreeNode, SafeHtmlBuilder sb) {
					String colorHex = "";
					/*if(textTreeNode instanceof TermTreeNode) {
						TermTreeNode termTreeNode = (TermTreeNode)textTreeNode;
						Term term = termTreeNode.getTerm();
						if(term.getUseless()) {
							sb.append(SafeHtmlUtils.fromTrustedString("<div style='padding-left:5px; padding-right:5px; background-color:gray; " +
									"color:white'>" + 
									textTreeNode.getText() + "</div>"));
						} else {
							sb.append(SafeHtmlUtils.fromTrustedString("<div style='padding-left:5px; padding-right:5px'>" + textTreeNode.getText() +
									"</div>"));
						}
					} else {*/
						sb.append(SafeHtmlUtils.fromTrustedString("<div style=''>" + textTreeNode.getText() +
								"</div>"));
					//}
			}
		});
		
		termTree.setSelectionModel(termTreeSelectionModel);
		termTree.getElement().setAttribute("source", "termsview");
		termTree.setContextMenu(new TermMenu());
		
		addDefaultNodes();
		
		vertical = new VerticalLayoutContainer();
		vertical.add(termTree, new VerticalLayoutData(1, 1));
		vertical.add(refreshButton, new VerticalLayoutData(1, -1));
		
		bindEvents();
	}
	
	private void addDefaultNodes() {
		availableTermsNode = new BucketTreeNode(new Bucket("Available Terms"));
		removedTermsNode = new BucketTreeNode(new Bucket("Removed Terms"));
		availableStructureTermsNode = new BucketTreeNode(new Bucket("Structures"));
		removedStructureTermsNode = new BucketTreeNode(new Bucket("Structures"));
		availableCharacterTermsNode = new BucketTreeNode(new Bucket("Characters"));
		removedCharacterTermsNode = new BucketTreeNode(new Bucket("Characters"));
		
		treeStore.add(availableTermsNode);
		treeStore.add(availableTermsNode, availableStructureTermsNode);
		treeStore.add(availableTermsNode, availableCharacterTermsNode);
		treeStore.add(removedTermsNode);
		treeStore.add(removedTermsNode, removedStructureTermsNode);
		treeStore.add(removedTermsNode, removedCharacterTermsNode);
	}

	private void bindEvents() {
		refreshButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				refreshMatchSubmissionsStatus();
			}
		});
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				TermsView.this.collection = event.getCollection();
				treeStore.removeChildren(availableStructureTermsNode);
				treeStore.removeChildren(availableCharacterTermsNode);
				treeStore.removeChildren(removedStructureTermsNode);
				treeStore.removeChildren(removedCharacterTermsNode);
				
				for(Term term : event.getCollection().getTerms()) {
					switch(term.getCategory().toLowerCase()) {
					case "character":
						if(term.isRemoved()) {
							addTermTreeNode(removedCharacterTermsNode, new TermTreeNode(term));
							
						} else {
							addTermTreeNode(availableCharacterTermsNode, new TermTreeNode(term));
						}
						break;
					case "structure":
						if(term.isRemoved()) {
							addTermTreeNode(removedStructureTermsNode, new TermTreeNode(term));
							treeStore.add(removedStructureTermsNode, new TermTreeNode(term));
						} else {
							addTermTreeNode(availableStructureTermsNode, new TermTreeNode(term));
						}
						break;
					}
				}
				termTree.expandAll();
			}
		});
		
		termTreeSelectionModel.addSelectionHandler(new SelectionHandler<TextTreeNode>() {
			@Override
			public void onSelection(SelectionEvent<TextTreeNode> event) {
				TextTreeNode node = event.getSelectedItem();
				if(node instanceof TermTreeNode) {
					TermTreeNode termTreeNode = (TermTreeNode)node;
					eventBus.fireEvent(new TermSelectEvent(termTreeNode.getTerm()));
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
	}	
	
	protected void addTermTreeNode(BucketTreeNode bucketNode, TermTreeNode termTreeNode) {
		this.termTermTreeNodeMap.put(termTreeNode.getTerm(), termTreeNode);
		this.treeStore.add(bucketNode, termTreeNode);
	}

	private void refreshMatchSubmissionsStatus() {
		/*toOntologyService.getSubmissionStatus(collection, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						globalEventBus.fireEvent(new ProcessingEndEvent());
						// update the match and submission part
						Window.alert("Updated ontology matches and submissions successfully. ");
						updateMatchesAndSubmissions(selectedCandidateTerm,
								selectedCategory);
					}

					@Override
					public void onFailure(Throwable caught) {
						globalEventBus.fireEvent(new ProcessingEndEvent());
						Window.alert("Server Error: failed to Update ontology matches and ontology submissions of terms in this upload. \n\n"
								+ caught.getMessage());
					}
				});*/
	}

	@Override
	public Widget asWidget() {
		return vertical;
	}
}