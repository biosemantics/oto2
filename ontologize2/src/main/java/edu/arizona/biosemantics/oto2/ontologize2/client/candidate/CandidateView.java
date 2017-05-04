package edu.arizona.biosemantics.oto2.ontologize2.client.candidate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.TreeDragSource;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.oto2.ontologize2.client.Alerter;
import edu.arizona.biosemantics.oto2.ontologize2.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize2.client.common.BatchCreateRelationValidator;
import edu.arizona.biosemantics.oto2.ontologize2.client.common.CreateRelationValidator;
import edu.arizona.biosemantics.oto2.ontologize2.client.common.ReplaceRelationValidator;
import edu.arizona.biosemantics.oto2.ontologize2.client.common.cell.CellImages;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ClearEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateCandidateEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.FilterEvent.FilterTarget;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveCandidateEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.SelectTermEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.UserLogEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.BucketTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.CandidateTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.TextTreeNode;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.node.TextTreeNodeProperties;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.CandidatePatternResult;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

public class CandidateView extends SimpleContainer {

	public interface CellTemplate extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template(""
				+ "<div style=\"height: 17px; padding: 2px 2px 0px 2px;\">"
				+ "<div id=\"wide\" style=\"float: right; width: calc(100% - 10px); color:{1}\">"
				+ "{0} <a href='javascript:void(0)'>{3}</a>"
				+ "</div>"
				+ "<div id=\"narrow\"  style=\"float: left; width: 10px;\">"
				+ ""
				+ ""
				+ "<div style=\"right:0px; top:6px; width:4px; height:4px; background-image: {2}\" ></div>"
				+ ""
				+ ""
				+ "</div>"
				+ "</div>"
				)
		SafeHtml cell(String value,	String textColor, String patternIcon, String numberOfPatterns);
	}
	
	
	private static CellImages cellImages = GWT.create(CellImages.class);
	protected static CellTemplate cellTemplate = GWT.create(CellTemplate.class);
	private static final TextTreeNodeProperties textTreeNodeProperties = GWT.create(TextTreeNodeProperties.class);

	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private Tree<TextTreeNode, TextTreeNode> tree;
	private TreeStore<TextTreeNode> treeStore;
	private Map<String, CandidateTreeNode> candidateNodeMap = new HashMap<String, CandidateTreeNode>();
	private Map<String, BucketTreeNode> bucketNodesMap = new HashMap<String, BucketTreeNode>();	
	private EventBus eventBus;
	private ToolBar buttonBar;

	private CheckMenuItem checkFilterItem;
	private TextField filterField;
	
	private DelayedTask addCandidatesTask = new DelayedTask() {
		@Override
		public void onExecute() {
			final String newTerms = addTermsField.getValue().trim();
			if(newTerms.isEmpty()) {
				Alerter.showAlert("Add Terms", "Term field is empty");
				return;
			}
			
			String[] newTermsArray = newTerms.split(",");
			List<Candidate> candidates = new LinkedList<Candidate>();
			for(String newTerm : newTermsArray) {
				int lastSeparatorIndex = newTerm.lastIndexOf("/");
				if(newTerm.length() == lastSeparatorIndex + 1) {
					Alerter.showAlert("Add terms", "Malformed input to add terms");
					return;
				}
				
				String term = newTerm.trim();
				String path = ""; 
				if(lastSeparatorIndex != -1) {
					term = newTerm.substring(lastSeparatorIndex + 1).trim();
					path = newTerm.substring(0, lastSeparatorIndex).trim();	
				}
				
				if(ModelController.getCollection().contains(term)) {
					String termPath = ModelController.getCollection().getCandidates().getPath(term);
					if(termPath == null)
						termPath = "/";
					Alerter.showAlert("Candidate exists", "Candidate <i>" + term + "</i> already exists at <i>" +
							termPath + "</i>");
					return;
				} else {
					if(path.isEmpty()) {
						/*BucketTreeNode bucketNode = getSelectedBucket();
						if(bucketNode != null)
							path = bucketNode.getPath();*/
					} else {
						if(!path.startsWith("/"))
							path = "/" + path;
					}
					candidates.add(new Candidate(term, path));
				}
			}
			addTermsField.setText("");
			eventBus.fireEvent(new CreateCandidateEvent(candidates));
			
			eventBus.fireEvent(new UserLogEvent("add_new_cand_terms",newTerms));
			
		}
	};
	
	private DelayedTask filterTask = new DelayedTask() {		
		@Override
		public void onExecute() {
			String filter = filterField.getText().trim();
			if(filter.isEmpty())
				checkFilterItem.setChecked(false);
			else
				checkFilterItem.setChecked(true);
			onFilter(filter);
		}
	};
	
	private TextField addTermsField;

	protected Map<Candidate, List<CandidatePatternResult>> candidatePatterns = 
			new HashMap<Candidate, List<CandidatePatternResult>>();
	
	private CandidateView() {
		treeStore = new TreeStore<TextTreeNode>(textTreeNodeProperties.key());
		treeStore.setAutoCommit(true);
		treeStore.addSortInfo(new StoreSortInfo<TextTreeNode>(new Comparator<TextTreeNode>() {
			@Override
			public int compare(TextTreeNode o1, TextTreeNode o2) {
				return o1.getText().compareTo(o2.getText());
			}
		}, SortDir.ASC));
		tree = new Tree<TextTreeNode, TextTreeNode>(treeStore, new IdentityValueProvider<TextTreeNode>());
		tree.setIconProvider(new TermTreeNodeIconProvider());
		tree.setCell(new AbstractCell<TextTreeNode>("click") {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	TextTreeNode value, SafeHtmlBuilder sb) {
				String patternIcon = "";
				String numberOfPatterns = "";
				if(value instanceof CandidateTreeNode) {
					Candidate candidate = ((CandidateTreeNode) value).getCandidate();
					if(candidatePatterns.containsKey(candidate)) {
						List<CandidatePatternResult> patterns = candidatePatterns.get(candidate);
						if(!patterns.isEmpty()) {
							patternIcon = "url(" + cellImages.blue().getSafeUri().asString() + ")";
							//numberOfPatterns = "(" + patterns.size() + " Patterns)";
							numberOfPatterns = "(recommended relations)";
						}
					}
				}
				OntologyGraph g = ModelController.getCollection().getGraph();
				if(g.getVertex(value.getText()) != null)
					sb.append(cellTemplate.cell(value.getText(), "gray", patternIcon, numberOfPatterns));
				else
					sb.append(cellTemplate.cell(value.getText(), "", patternIcon, numberOfPatterns));	
				
			}
			
			  public void onBrowserEvent(Context context, Element parent, TextTreeNode value,
			      NativeEvent event, ValueUpdater<TextTreeNode> valueUpdater) {
				//super.onBrowserEvent(context, parent, value, event, valueUpdater);
			    String eventType = event.getType();
			    //Alerter.showInfo("Target", event.getEventTarget().toString());
			    if(!event.getEventTarget().toString().trim().equals("<a href=\"javascript:void(0)\">(recommended relations)</a>")&&
			    		!event.getEventTarget().toString().trim().equals("javascript:void(0)")) return;
			    Candidate candidate = null;
				if(value instanceof CandidateTreeNode) 
					candidate = ((CandidateTreeNode) value).getCandidate();
				final Candidate c = candidate;
			    List patterns = candidatePatterns.get(c);
			    if (BrowserEvents.CLICK.equals(eventType)&&(patterns!=null&&patterns.size()>0)) {
			    	final CreateRelationsFromCandidateDialog dialog = new CreateRelationsFromCandidateDialog(c, 
							candidatePatterns.get(c));
					dialog.show();
					dialog.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							if(dialog.getSelectedEdges().size()<1) return;
							StringBuffer sb = new StringBuffer();
							for(Edge e : dialog.getSelectedEdges()) {
								sb.append(e.toString()).append("|");
							}
							eventBus.fireEvent(new UserLogEvent("add_by_pattern",sb.toString()));
							
							try {
								BatchCreateRelationValidator relationsValidator = new BatchCreateRelationValidator();
								CompositeModifyEvent patternEdgesEvent = relationsValidator.validate(dialog.getSelectedEdges());
								eventBus.fireEvent(patternEdgesEvent);
							} catch(Exception e) {
								Alerter.showAlert("Import failed", e.getMessage());
							}
							
						}
					});
			    }
			  }
			
		});
		tree.getElement().setAttribute("source", "termsview");
		tree.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
		/*tree.getSelectionModel().addSelectionHandler(new SelectionHandler<TextTreeNode>() {
			@Override
			public void onSelection(SelectionEvent<TextTreeNode> event) {
				eventBus.fireEvent(new SelectTermEvent(event.getSelectedItem().getText()));
			}
		});*/
		tree.setAutoExpand(true);
		tree.setContextMenu(createContextMenu());
		
		TreeDragSource<TextTreeNode> dragSource = new TreeDragSource<TextTreeNode>(tree) {
			@Override
			protected void onDragStart(DndDragStartEvent event) {
				super.onDragStart(event);
				List<Candidate> data = new LinkedList<Candidate>();
				for(TextTreeNode node : tree.getSelectionModel().getSelectedItems()) {
					addBucketTermsToList(node, data);
				}
				event.setData(data);
			}
			protected void addBucketTermsToList(TextTreeNode node, List<Candidate> data) {
				if(node instanceof BucketTreeNode) {
					//do not add offsprings but only this node
//					for(TextTreeNode child : tree.getStore().getChildren(node)) {
//						this.addBucketTermsToList(child, data);
//					}
					Candidate candidate = new Candidate();
					candidate.setText(node.getText());
					data.add(candidate);
				} else if(node instanceof CandidateTreeNode) {
					Candidate candidate = ((CandidateTreeNode)node).getCandidate();
					data.add(candidate);
				}
			}
		};
		
		buttonBar = new ToolBar();
		
		TextButton filterButton = new TextButton("Filter");
		final Menu menu = new Menu();
		checkFilterItem = new CheckMenuItem(DefaultMessages.getMessages().gridFilters_filterText());
		menu.add(checkFilterItem);
		
		final Menu filterMenu = new Menu();
		filterField = new TextField() {
			protected void onKeyUp(Event event) {
				super.onKeyUp(event);
				int key = event.getKeyCode();
				if (key == KeyCodes.KEY_ENTER) {
					event.stopPropagation();
					event.preventDefault();
					filterMenu.hide(true);
				}
				
				filterTask.delay(500);
			}
		};
		checkFilterItem.setSubMenu(filterMenu);
		checkFilterItem.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {
	        @Override
	        public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
	        	if(!checkFilterItem.isChecked())
	        		filterField.setText("");
	        	onFilter(filterField.getText());
	        }
	    });
		filterMenu.add(filterField);
		filterButton.setMenu(menu);
		buttonBar.add(filterButton);
		
//		TextButton importButton = new TextButton("Import");
//		importButton.addSelectHandler(new SelectHandler() {
//			@Override
//			public void onSelect(SelectEvent event) {
//				final TextAreaMessageBox box = new TextAreaMessageBox("Import terms", "");
//				/*box.setResizable(true);
//				box.setResize(true);
//				box.setMaximizable(true);*/
//				box.setModal(true);
//				box.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
//					@Override
//					public void onSelect(SelectEvent event) {
//						String input = box.getValue();
//						String[] lines = input.split("\\n");
//						List<Candidate> candidates = new LinkedList<Candidate>();
//						for(String line : lines) {
//							String[] candidatePath = line.split(",");
//							if(candidatePath.length == 1) {
//								String candidate = candidatePath[0];
//								if(!ModelController.getCollection().getCandidates().contains(candidate))
//									candidates.add(new Candidate(candidate));
//								else
//									Alerter.showAlert("Candidate exists", "Candidate + \"" + candidate + "\" already exists at \"" +
//											ModelController.getCollection().getCandidates().getPath(candidate) + "\"");
//							} else if(candidatePath.length >= 2) {
//								String candidate = candidatePath[0];
//								if(!ModelController.getCollection().getCandidates().contains(candidate))
//									candidates.add(new Candidate(candidatePath[0], candidatePath[1]));
//								else
//									Alerter.showAlert("Candidate exists", "Candidate + \"" + candidate + "\" already exists at \"" +
//											ModelController.getCollection().getCandidates().getPath(candidate) + "\"");
//							}
//						}
//						
//						eventBus.fireEvent(new CreateCandidateEvent(candidates));
//					}
//				});
//				box.show();
//			}
//		});
		
		TextButton removeButton = new TextButton("Remove");
		Menu removeMenu = new Menu();
		MenuItem selectedRemove = new MenuItem("Selected");
		selectedRemove.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				removeNodes(tree.getSelectionModel().getSelectedItems());
			}
		});
		
		MenuItem allRemove = new MenuItem("All");
		allRemove.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				removeNodes(tree.getStore().getRootItems());
			}
		});
		removeMenu.add(selectedRemove);
		removeMenu.add(allRemove);
		removeButton.setMenu(removeMenu);
		
		//buttonBar.add(importButton);
		buttonBar.add(removeButton);
		
		HorizontalLayoutContainer hlc = new HorizontalLayoutContainer();
		addTermsField = new TextField() {
			protected void onKeyUp(Event event) {
				super.onKeyUp(event);
				int key = event.getKeyCode();
				if (key == KeyCodes.KEY_ENTER) {
					event.stopPropagation();
					event.preventDefault();
					addCandidatesTask.delay(200);
				}
			}
		};	
		TextButton addButton = new TextButton("Add");
		addButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				addCandidatesTask.delay(200);
			}
		});
		hlc.add(addTermsField, new HorizontalLayoutData(1, -1));
		hlc.add(addButton);
		
		FieldLabel field = new FieldLabel(hlc, "Add Terms");
		
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.add(buttonBar, new VerticalLayoutData(1, -1));
		vlc.add(tree, new VerticalLayoutData(1, 1));
		vlc.add(field, new VerticalLayoutData(1, 25));
		this.add(vlc);
	}
	
	public void fire(GwtEvent<? extends EventHandler> e) {
		if(e instanceof CreateRelationEvent) {
			CreateRelationValidator createRelationValidator = new CreateRelationValidator(eventBus);
			createRelationValidator.validateAndFire((CreateRelationEvent)e);
		} else if(e instanceof ReplaceRelationEvent) {
			ReplaceRelationValidator replaceRelationValidator = new ReplaceRelationValidator(eventBus);
			replaceRelationValidator.validateAndFire((ReplaceRelationEvent)e);
		} else 
			eventBus.fireEvent(e);
	}
	
	protected void onFilter(final String text) {
		
		if(checkFilterItem.isChecked()) {
			treeStore.removeFilters();
			treeStore.addFilter(new StoreFilter<TextTreeNode>() {
				@Override
				public boolean select(Store<TextTreeNode> store, TextTreeNode parent, TextTreeNode item) {
					return item.getText().contains(text);
				}
			});
			treeStore.setEnableFilters(true);
			
			eventBus.fireEvent(new UserLogEvent("cand_filter",text));
		} else {
			treeStore.removeFilters();
			treeStore.setEnableFilters(false);
		}
	}

	private Menu createContextMenu() {
		final Menu menu = new Menu();
		menu.addBeforeShowHandler(new BeforeShowHandler() {
			@Override
			public void onBeforeShow(BeforeShowEvent event) {
				menu.clear();
				
				if(!tree.getSelectionModel().getSelectedItems().isEmpty()) {
					TextTreeNode node = tree.getSelectionModel().getSelectedItem();
					Candidate candidate = null;
					if(node instanceof CandidateTreeNode) 
						candidate = ((CandidateTreeNode) node).getCandidate();
					final String text = node.getText();
					
					final Candidate c = candidate;
					if(candidate != null && candidatePatterns != null && candidatePatterns.containsKey(candidate) && 
							!candidatePatterns.get(candidate).isEmpty()) {
						MenuItem showPatterns = new MenuItem("Show recommended relations");
						//MenuItem showPatterns = new MenuItem("Show patterns");
						showPatterns.addSelectionHandler(new SelectionHandler<Item>() {
							@Override
							public void onSelection(SelectionEvent<Item> event) {
								final CreateRelationsFromCandidateDialog dialog = new CreateRelationsFromCandidateDialog(c, 
										candidatePatterns.get(c));
								dialog.show();
								dialog.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
									@Override
									public void onSelect(SelectEvent event) {
										final OntologyGraph g = ModelController.getCollection().getGraph();
										//put super-subclass relations first
										StringBuffer sb = new StringBuffer();
										for(Edge e : dialog.getSelectedEdges()) {
											sb.append(e.toString()).append("|");
										}
										eventBus.fireEvent(new UserLogEvent("add_by_pattern",sb.toString()));
										
										try {
											BatchCreateRelationValidator relationsValidator = new BatchCreateRelationValidator();
											CompositeModifyEvent patternEdgesEvent = relationsValidator.validate(dialog.getSelectedEdges());
											eventBus.fireEvent(patternEdgesEvent);
										} catch(Exception e) {
											Alerter.showAlert("Import failed", e.getMessage());
										}
										
									}
								});
							}
						});
						menu.add(showPatterns);
					}
					
					MenuItem filterItem = new MenuItem("Filter: " + text);
					Menu filterMenu = new Menu();
					filterItem.setSubMenu(filterMenu);
					for(final FilterTarget filterTarget : FilterTarget.values()) {
						MenuItem menuItem = new MenuItem(filterTarget.getDisplayName());
						menuItem.addSelectionHandler(new SelectionHandler<Item>() {
							@Override
							public void onSelection(SelectionEvent<Item> event) {
									eventBus.fireEvent(new FilterEvent(text, 
											filterTarget, Type.values()));
							}
						});
						filterMenu.add(menuItem);
					}
					MenuItem context = new MenuItem("Show Term Context");
					context.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							eventBus.fireEvent(new SelectTermEvent(text));
							
							eventBus.fireEvent(new UserLogEvent("show_context_cand",text));
						}
					});
					menu.add(filterItem);
					menu.add(context);
				}
				event.setCancelled(menu.getWidgetCount() == 0);
			}
		});		
		return menu;
	}

	private BucketTreeNode getSelectedBucket() {
		List<TextTreeNode> selection = tree.getSelectionModel().getSelectedItems();
		if(!selection.isEmpty()) {
			TextTreeNode node = selection.get(0);
			if(node instanceof BucketTreeNode) {
				return (BucketTreeNode)node;
			}
			if(node instanceof CandidateTreeNode) {
				TextTreeNode parent = tree.getStore().getParent(node);
				if(parent instanceof BucketTreeNode)
					return (BucketTreeNode) parent;
			}
		}
		return null;
	}
	
	protected void removeNodes(List<TextTreeNode> nodes) {
		final Set<Candidate> remove = new HashSet<Candidate>();
		StringBuffer delTerms = new StringBuffer();
		for(TextTreeNode node : nodes) {
			if(node instanceof BucketTreeNode) {
				addBucketTermsToRemoveList((BucketTreeNode)node, remove);
				delTerms.append(node.getText()).append(", ");
			}
			if(node instanceof CandidateTreeNode) {
				CandidateTreeNode candidateTreeNode = (CandidateTreeNode)node;
				remove.add(candidateTreeNode.getCandidate());
				delTerms.append(node.getText()).append(", ");
			}
		}
		
		eventBus.fireEvent(new RemoveCandidateEvent(remove));
		eventBus.fireEvent(new UserLogEvent("del_cand_terms",delTerms.toString()));
	}

	private void addBucketTermsToRemoveList(BucketTreeNode node, Set<Candidate> remove) {
		for(TextTreeNode childNode : tree.getStore().getChildren(node)) {
			if(childNode instanceof CandidateTreeNode) {
				remove.add(((CandidateTreeNode)childNode).getCandidate());
			} else if(childNode instanceof BucketTreeNode) {
				this.addBucketTermsToRemoveList((BucketTreeNode)childNode, remove);
			}
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
				if(event.isEffectiveInModel())
					setCollection(event.getCollection());
			}
		}); 
		eventBus.addHandler(CreateCandidateEvent.TYPE, new CreateCandidateEvent.Handler() {
			@Override
			public void onCreate(CreateCandidateEvent event) {
				add(Arrays.asList(event.getCandidates()));
			}
		});
		eventBus.addHandler(RemoveCandidateEvent.TYPE, new RemoveCandidateEvent.Handler() {
			@Override
			public void onRemove(RemoveCandidateEvent event) {
				remove(Arrays.asList(event.getCandidates()));
			}
		});
		eventBus.addHandler(CreateRelationEvent.TYPE, new CreateRelationEvent.Handler() {
			@Override
			public void onCreate(CreateRelationEvent event) {
				if(event.isEffectiveInModel()) {
					for(Edge e : event.getRelations()) {
						String[] nodes = new String[] { e.getSrc().getValue(), e.getDest().getValue() };
						for(String node : nodes) {
							if(candidateNodeMap.containsKey(node)) {
								treeStore.update(candidateNodeMap.get(node));
							}
						}
					}
					updateCandidates();
				}
			}
		});
		eventBus.addHandler(RemoveRelationEvent.TYPE, new RemoveRelationEvent.Handler() {
			@Override
			public void onRemove(RemoveRelationEvent event) {
				if(event.isEffectiveInModel()) {
					OntologyGraph g = ModelController.getCollection().getGraph();
					for(Edge e : event.getRelations()) {
						String[] nodes = new String[] { e.getSrc().getValue(), e.getDest().getValue() };
						for(String node : nodes) {
							if(candidateNodeMap.containsKey(node)) {
								if(g.getVertex(node) == null)
									treeStore.update(candidateNodeMap.get(node));
							}
						}
					}
					updateCandidates();
				}
			}
		});
		eventBus.addHandler(ClearEvent.TYPE, new ClearEvent.Handler() {
			@Override
			public void onClear(ClearEvent event) {
				if(event.isEffectiveInModel()) {
					for(TextTreeNode node : treeStore.getAll())
						treeStore.update(node);
					updateCandidates();
				}
			}
		});
	}
	
	private void updateCandidate(final Candidate candidate) {
		final MessageBox box = Alerter.startLoading();
		collectionService.getCandidatePatternResults(ModelController.getCollection().getId(), 
				ModelController.getCollection().getSecret(), candidate, new AsyncCallback<List<CandidatePatternResult>>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.showAlert("Update candidate", "Updating candidate failed.", caught);
						Alerter.stopLoading(box);
					}
					@Override
					public void onSuccess(List<CandidatePatternResult> result) {
						candidatePatterns.put(candidate, result);
						updateNode(candidate);
						Alerter.stopLoading(box);
					}
		});
	}
	
	protected void updateCandidates() {
		final MessageBox box = Alerter.startLoading();
		collectionService.getCandidatePatternResults(ModelController.getCollection().getId(), ModelController.getCollection().getSecret(), 
				new AsyncCallback<Map<Candidate, List<CandidatePatternResult>>>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.showAlert("Update candidates", "Updating candidates failed.", caught);
						Alerter.stopLoading(box);
					}
					@Override
					public void onSuccess(Map<Candidate, List<CandidatePatternResult>> result) {
						CandidateView.this.candidatePatterns = result;
						updateNodes();
						Alerter.stopLoading(box);
					}
		});
	}

	public void setCollection(final Collection collection) {
		tree.getStore().clear();
		candidateNodeMap.clear();
		bucketNodesMap.clear();
		add(collection.getCandidates());
	}

	protected void updateNodes() {
		for(TextTreeNode node : tree.getStore().getAll()) {
			treeStore.update(node);
		}
	}
	
	protected void updateNode(Candidate candidate) {
		treeStore.update(candidateNodeMap.get(candidate.getText()));
	}

	protected void remove(Iterable<Candidate> candidates) {
		for(Candidate candidate : candidates) {
			candidatePatterns.remove(candidate);
			if(candidateNodeMap.containsKey(candidate.getText())) {
				CandidateTreeNode candidateNode = candidateNodeMap.get(candidate.getText());
				TextTreeNode bucket = treeStore.getParent(candidateNode);
				treeStore.remove(candidateNode);
				candidateNodeMap.remove(candidate.getText());
				if(bucket != null && treeStore.getChildCount(bucket) == 0 && bucket instanceof BucketTreeNode )
					remove((BucketTreeNode)bucket);
			}
		}
	}

	private void remove(BucketTreeNode bucket) {
		TextTreeNode parent = treeStore.getParent(bucket);
		treeStore.remove(bucket);
		bucketNodesMap.remove(bucket);
		if(parent != null && treeStore.getChildCount(parent) == 0 && parent instanceof BucketTreeNode) {
			this.remove((BucketTreeNode)parent);
		}
	}

	private void add(Iterable<Candidate> candidates) {
		for(final Candidate candidate : candidates) {
			createBucketNodes(candidate.getPath());
			addTermTreeNode(bucketNodesMap.get(candidate.getPath()), new CandidateTreeNode(candidate));
			updateCandidate(candidate);
		}
	}

	private boolean contains(Candidate candidate) {
		return candidateNodeMap.containsKey(candidate.getText());
	}

	protected void createBucketNodes(String path) {
		if(path == null) 
			return;
		String[] buckets = path.split("/");
		String cumulativePath = "";
		String parentPath = "";
		for(String bucket : buckets) {
			if(!bucket.isEmpty()) {
				cumulativePath += "/" + bucket;
				if(!bucketNodesMap.containsKey(cumulativePath)) {
					BucketTreeNode bucketTreeNode = new BucketTreeNode(cumulativePath);
					if(parentPath.isEmpty())
						tree.getStore().add(bucketTreeNode);
					else
						tree.getStore().add(bucketNodesMap.get(parentPath), bucketTreeNode);
					bucketNodesMap.put(cumulativePath, bucketTreeNode);
				}
				parentPath = cumulativePath;
			}
		}
	}

	protected void addTermTreeNode(BucketTreeNode bucketNode, CandidateTreeNode candidateTreeNode) {
		this.candidateNodeMap.put(candidateTreeNode.getCandidate().getText(), candidateTreeNode);
		if(bucketNode == null)
			this.tree.getStore().add(candidateTreeNode);
		else
			this.tree.getStore().add(bucketNode, candidateTreeNode);
	}
	
	private void initializeCollapsing(Map<String, BucketTreeNode> bucketTreeNodes) {
//		for(BucketTreeNode node : bucketTreeNodes.values()) {
//			if(tree.getStore().getChildren(node).get(0) instanceof TermTreeNode) {
//				tree.setExpanded(node, false);
//			} else {
//				tree.setExpanded(node, true);
//			}
//		}
	}	
}
