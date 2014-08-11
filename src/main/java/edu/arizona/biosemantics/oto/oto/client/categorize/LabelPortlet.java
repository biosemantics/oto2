package edu.arizona.biosemantics.oto.oto.client.categorize;

import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeSelectEvent.BeforeSelectHandler;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent.DndDropHandler;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.dnd.core.client.TreeDragSource;
import com.sencha.gxt.dnd.core.client.TreeDropTarget;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Portlet;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.web.bindery.event.shared.HandlerRegistration;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.CategorizeCopyRemoveTermEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.CategorizeCopyTermEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.CategorizeMoveTermEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelModifyEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelRemoveEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.LabelsMergeEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.SynonymCreationEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermCategorizeEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermRenameEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermSelectEvent;
import edu.arizona.biosemantics.oto.oto.client.categorize.event.TermUncategorizeEvent;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;
import edu.arizona.biosemantics.oto.oto.shared.model.TermTreeNode;
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
	
	public class TermMenu extends Menu implements BeforeShowHandler {
		private MenuItem move;
		private MenuItem addSynonym;
		private MenuItem removeSynonym;
		private MenuItem removeAllSynonyms;
		private MenuItem rename;
		private HandlerRegistration renameRegistration;
		private MenuItem remove;
		private HandlerRegistration removeRegistration;
		private MenuItem copy;

		public TermMenu() {
			this.addBeforeShowHandler(this);
			this.setWidth(140);
			
			move = new MenuItem("Move to");
			copy = new MenuItem("Copy to");
			rename = new MenuItem("Rename");
			remove = new MenuItem("Remove");
			addSynonym = new MenuItem("Add Synonym");
			removeSynonym = new MenuItem("Remove Synonym");
			removeAllSynonyms = new MenuItem("Remove all Synonyms");
		}

		@Override
		public void onBeforeShow(BeforeShowEvent event) {
			this.clear();
			
			List<TermTreeNode> selected = tree.getSelectionModel().getSelectedItems();
			if(selected == null || selected.isEmpty()) {
				event.setCancelled(true);
				this.hide();
			} else {
				final List<Term> terms = new LinkedList<Term>();
				for(TermTreeNode node : selected) 
					terms.add(node.getTerm());
				
				if(renameRegistration != null)
					renameRegistration.removeHandler();
				if(removeRegistration != null)
					removeRegistration.removeHandler();

				if(collection.getLabels().size() > 1) {
					Menu moveMenu = new Menu();
					for(final Label collectionLabel : collection.getLabels())
						if(!label.equals(collectionLabel) && !collectionLabel.getTerms().containsAll(terms)) {
							moveMenu.add(new MenuItem(collectionLabel.getName(), new SelectionHandler<MenuItem>() {
								@Override
								public void onSelection(SelectionEvent<MenuItem> event) {
									collectionLabel.addTerms(terms);
									label.removeTerms(terms);
									eventBus.fireEvent(new CategorizeMoveTermEvent(terms, label, collectionLabel));
									TermMenu.this.hide();
								}
							}));
						}
					if(moveMenu.getWidgetCount() > 0) {
						move.setSubMenu(moveMenu);
						this.add(move);
					}
				}
				
				if(collection.getLabels().size() > 1) {
					Menu copyMenu = new Menu();
					VerticalPanel verticalPanel = new VerticalPanel();
					final Set<Label> copyLabels = new HashSet<Label>();
					final TextButton copyButton = new TextButton("Copy");
					copyButton.setEnabled(false);
					for(final Label collectionLabel : collection.getLabels()) {
						if(!label.equals(collectionLabel) && !collectionLabel.getTerms().containsAll(terms)) {
							CheckBox checkBox = new CheckBox();
							checkBox.setBoxLabel(collectionLabel.getName());
							checkBox.setValue(false);
							checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
								@Override
								public void onValueChange(ValueChangeEvent<Boolean> event) {
									if(event.getValue())
										copyLabels.add(collectionLabel);
									else
										copyLabels.remove(collectionLabel);
									copyButton.setEnabled(!copyLabels.isEmpty());
								}
							});
							verticalPanel.add(checkBox);
						}
					}
					if(verticalPanel.getWidgetCount() > 0) {
						copyButton.addSelectHandler(new SelectHandler() {
							@Override
							public void onSelect(SelectEvent event) {
								for(Label copyLabel : copyLabels) {
									copyLabel.addTerms(terms);
								}
								eventBus.fireEvent(new CategorizeCopyTermEvent(terms, label, copyLabels));
								TermMenu.this.hide();
							}
						});
						verticalPanel.add(copyButton);
						copyMenu.add(verticalPanel);
						copy.setSubMenu(copyMenu);
						this.add(copy);
					}
				}
				
				if(terms.size() == 1) {
					final Term term = terms.get(0);
					renameRegistration = rename.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							final PromptMessageBox box = new PromptMessageBox(
									"Term rename", "Please input new spelling");
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
							box.addHideHandler(new HideHandler() {
								@Override
								public void onHide(HideEvent event) {
									String newName = box.getValue();
									term.setTerm(newName);
									eventBus.fireEvent(new TermRenameEvent(term));
								}
							});
							box.show();
						}
					});
					this.add(rename);
				}
				
				removeRegistration = remove.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						for(Term term : terms) {
							Set<Label> labels = collection.getLabels(term);
							if(labels.size() > 1) {
								UncategorizeDialog dialog = new UncategorizeDialog(eventBus, label, 
										term, labels);
							} else {
								label.removeTerm(term);
								eventBus.fireEvent(new TermUncategorizeEvent(term, label));
							}
						}
					}
				});
				this.add(remove);
				
				if(terms.size() == 1 && label.getTerms().size() > 1) {
					final Term term = terms.get(0);
					Menu synonymMenu = new Menu();
					
					VerticalPanel verticalPanel = new VerticalPanel();
					final Set<Term> synonymTerms = new HashSet<Term>();
					final TextButton synonymButton = new TextButton("Synonomize");
					synonymButton.setEnabled(false);
					for(final Term synonymTerm : label.getTerms()) {
						if(!synonymTerm.equals(term)) {
							CheckBox checkBox = new CheckBox();
							checkBox.setBoxLabel(synonymTerm.getTerm());
							checkBox.setValue(false);
							checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
								@Override
								public void onValueChange(ValueChangeEvent<Boolean> event) {
									if(event.getValue())
										synonymTerms.add(synonymTerm);
									else
										synonymTerms.remove(synonymTerm);
									synonymButton.setEnabled(!synonymTerms.isEmpty());
								}
							});
							verticalPanel.add(checkBox);
						}
					}
					synonymButton.addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							label.addSynonyms(term, synonymTerms);
							eventBus.fireEvent(new SynonymCreationEvent(label, term, synonymTerms));
							TermMenu.this.hide();
						}
					});
					addSynonym.setSubMenu(synonymMenu);
					this.add(addSynonym);
				}
				
				this.add(removeSynonym);
				this.add(removeAllSynonyms);
			}
			
			if(this.getWidgetCount() == 0)
				event.setCancelled(true);
		}
	}
	
	public class LabelMenu extends Menu implements BeforeShowHandler {
		private MenuItem merge;

		public LabelMenu(final Label label) {
			this.addBeforeShowHandler(this);
			this.setWidth(140);
		}

		@Override
		public void onBeforeShow(BeforeShowEvent event) {
			this.clear();
			
			MenuItem modify = new MenuItem("Modify");
			modify.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					LabelModifyDialog modifyDialog = new LabelModifyDialog(label);
					modifyDialog.show();
				}
			});
			this.add(modify);
			MenuItem remove = new MenuItem("Remove");
			remove.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					collection.removeLabel(label);
					eventBus.fireEvent(new LabelRemoveEvent(label));
				}
			});
			this.add(remove);
			
			if(collection.getLabels().size() > 1) {
				merge = new MenuItem("Merge with");
				Menu mergeMenu = new Menu();
				VerticalPanel verticalPanel = new VerticalPanel();
				final Set<Label> mergeLabels = new HashSet<Label>();
				final TextButton mergeButton = new TextButton("Merge");
				mergeButton.setEnabled(false);
				for(final Label collectionLabel : collection.getLabels()) {
					if(!label.equals(collectionLabel)) {
						CheckBox checkBox = new CheckBox();
						checkBox.setBoxLabel(collectionLabel.getName());
						checkBox.setValue(false);
						checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
							@Override
							public void onValueChange(ValueChangeEvent<Boolean> event) {
								if(event.getValue())
									mergeLabels.add(collectionLabel);
								else
									mergeLabels.remove(collectionLabel);
								mergeButton.setEnabled(!mergeLabels.isEmpty());
							}
						});
						verticalPanel.add(checkBox);
					}
				}
				mergeButton.addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						collection.removeLabels(mergeLabels);
						for(Label mergeLabel : mergeLabels) {
							label.addTerms(mergeLabel.getTerms());
						}
						LabelMenu.this.hide();
						eventBus.fireEvent(new LabelsMergeEvent(label, mergeLabels));
					}
				});
				verticalPanel.add(mergeButton);
				mergeMenu.add(verticalPanel);
				merge.setSubMenu(mergeMenu);
				this.add(merge);
			}
			
			if(this.getWidgetCount() == 0)
				event.setCancelled(true);
		}
	}
	
	public static class LabelInfoContainer extends SimpleContainer {
		
		private TextField labelName;
		private TextArea labelDescription;

		public LabelInfoContainer(String initialName, String initialDescription) {
			FieldSet fieldSet = new FieldSet();
		    fieldSet.setHeadingText("Category Information");
		    fieldSet.setCollapsible(true);
		    this.add(fieldSet, new MarginData(10));
		 
		    VerticalLayoutContainer p = new VerticalLayoutContainer();
		    fieldSet.add(p);
		    
		    labelName = new TextField();
		    labelName.setAllowBlank(false);
		    labelName.setValue(initialName);
		    p.add(new FieldLabel(labelName, "Name"), new VerticalLayoutData(1, -1));
		 
		    labelDescription = new TextArea();
		    labelDescription.setValue(initialDescription);
		    labelDescription.setAllowBlank(true);
		    p.add(new FieldLabel(labelDescription, "Description"), new VerticalLayoutData(1, -1));
		}

		public TextField getLabelName() {
			return labelName;
		}

		public TextArea getLabelDescription() {
			return labelDescription;
		}
	}
	
	public class LabelModifyDialog extends Dialog {
		
		public LabelModifyDialog(final Label label) {
			this.setHeadingText("Modify Category");	
			LabelInfoContainer labelInfoContainer = new LabelInfoContainer(label.getName(), label.getDescription());
		    this.add(labelInfoContainer);
		 
		    final TextField labelName = labelInfoContainer.getLabelName();
		    final TextArea labelDescription = labelInfoContainer.getLabelDescription();
		    
		    getButtonBar().clear();
		    TextButton save = new TextButton("Save");
		    save.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					if(!labelName.validate()) {
						AlertMessageBox alert = new AlertMessageBox("Category Name", "A category name is required");
						alert.show();
						return;
					}
					label.setName(labelName.getText());
					label.setDescription(labelDescription.getText());
					eventBus.fireEvent(new LabelModifyEvent(label));
					LabelModifyDialog.this.hide();
				}
		    });
		    TextButton cancel =  new TextButton("Cancel");
		    cancel.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					LabelModifyDialog.this.hide();
				}
		    });
		    addButton(save);
		    addButton(cancel);
		}
	
	}
	
	private static final TextTreeNodeProperties textTreeNodeProperties = GWT.create(TextTreeNodeProperties.class);
	private TreeStore<TermTreeNode> portletStore;
	private Label label;
	private Tree<TermTreeNode, String> tree;
	private EventBus eventBus;
	private Map<Term, TermTreeNode> termTermTreeNodeMap = new HashMap<Term, TermTreeNode>();
	private Collection collection;

	public LabelPortlet(EventBus eventBus, Label label, Collection collection) {
		this.eventBus = eventBus;
		this.label = label;
		this.collection = collection;
		this.setHeadingText(label.getName());
		
		this.setCollapsible(true);
		this.setAnimCollapse(false);
		final ToolButton toolButton = new ToolButton(ToolButton.GEAR);
		toolButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				LabelMenu menu = new LabelMenu(LabelPortlet.this.label);
				menu.show(toolButton);
			}});
		this.getHeader().addTool(toolButton);
		this.setContextMenu(new LabelMenu(label));
		
		portletStore = new TreeStore<TermTreeNode>(textTreeNodeProperties.key());
		portletStore.setAutoCommit(true);
		tree = new Tree<TermTreeNode, String>(portletStore, textTreeNodeProperties.text());
		tree.setContextMenu(new TermMenu());
		add(tree);
		bindEvents();
		setupDnD();
		
		for(Term mainTerm : label.getTerms()) {
			addMainTerm(mainTerm);
			for(Term synonym : label.getSynonyms(mainTerm)) 
				this.addSynonymTerm(mainTerm, synonym);
		}
	}
	
	protected void addMainTerm(Term term) {
		MainTermTreeNode mainTermTreeNode = new MainTermTreeNode(term);
		if(!termTermTreeNodeMap.containsKey(term))  {
			portletStore.add(mainTermTreeNode);
			this.termTermTreeNodeMap.put(term, mainTermTreeNode);
		}
	}
	
	protected void addSynonymTerm(Term mainTerm, Term synonymTerm) {
		MainTermTreeNode mainTermTreeNode = null;
		TermTreeNode termTreeNode = termTermTreeNodeMap.get(mainTerm);
		if(termTreeNode == null) 
			mainTermTreeNode = new MainTermTreeNode(mainTerm);
		else if(termTreeNode instanceof MainTermTreeNode) {
			mainTermTreeNode = (MainTermTreeNode)termTreeNode;
		} else if(termTreeNode instanceof SynonymTermTreeNode) {
			return;
		}

		SynonymTermTreeNode synonymTermTreeNode = new SynonymTermTreeNode(synonymTerm);
		if(!termTermTreeNodeMap.containsKey(synonymTermTreeNode)) {
			portletStore.add(mainTermTreeNode, synonymTermTreeNode);
			this.termTermTreeNodeMap.put(synonymTerm, synonymTermTreeNode);
		}
	}
	
	private void removeTerm(Term term) {
		if(termTermTreeNodeMap.containsKey(term))
			portletStore.remove(termTermTreeNodeMap.remove(term));
	}
	
	private void removeTerms(List<Term> terms) {
		for(Term term : terms)
			this.removeTerm(term);
	}

	private void bindEvents() {
		eventBus.addHandler(SynonymCreationEvent.TYPE, new SynonymCreationEvent.SynonymCreationHandler() {
			@Override
			public void onSynonymCreation(Label label, Term mainTerm, Set<Term> synonymTerms) {
				if(LabelPortlet.this.label.equals(label)) {
					for(Term synonymTerm : synonymTerms)
						LabelPortlet.this.addSynonymTerm(mainTerm, synonymTerm);
				}
			}
		});
		eventBus.addHandler(TermRenameEvent.TYPE, new TermRenameEvent.RenameTermHandler() {
			@Override
			public void onRename(Term term) {
				if(portletStore.indexOf(termTermTreeNodeMap.get(term)) != -1) {
					portletStore.update(termTermTreeNodeMap.get(term));
				}
			}
		});
		eventBus.addHandler(CategorizeCopyTermEvent.TYPE, new CategorizeCopyTermEvent.CategorizeCopyTermHandler() {
			@Override
			public void onCategorize(List<Term> terms, Label sourceCategory, Set<Label> targetCategories) {
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
			public void onModify(Label label) {
				if(label.equals(LabelPortlet.this.label))
					LabelPortlet.this.setHeadingText(label.getName());
			}
		});
		eventBus.addHandler(CategorizeMoveTermEvent.TYPE, new CategorizeMoveTermEvent.CategorizeMoveTermHandler() {
			@Override
			public void onCategorize(List<Term> terms, Label sourceLabel, Label targetLabel) {
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
			public void onCategorize(List<Term> terms, Set<Label> labels) {
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
			public void onUncategorize(List<Term> terms, Set<Label> oldLabels) {
				if(LabelPortlet.this.label.equals(label)) {
					LabelPortlet.this.removeTerms(terms);
				}
			}
		});
		eventBus.addHandler(CategorizeCopyRemoveTermEvent.TYPE, new CategorizeCopyRemoveTermEvent.CategorizeCopyRemoveTermHandler() {
			@Override
			public void onRemove(List<Term> terms, Label label) {
				if(LabelPortlet.this.label.equals(label)) {
					LabelPortlet.this.removeTerms(terms);
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
		
		TreeDropTarget<TermTreeNode> treeDropTarget = new TreeDropTarget<TermTreeNode>(tree);
		treeDropTarget.setAllowDropOnLeaf(true);
		treeDropTarget.setAllowSelfAsSource(true);
		//let our events take care of tree/list store updates
		treeDropTarget.setOperation(Operation.COPY);
		treeDropTarget.addDropHandler(new DndDropHandler() {
			@Override
			public void onDrop(DndDropEvent event) {
				//model update
				System.out.println("drop");
				
				//ui update
				// <->
				//event fire
				event.getData();
			}
		}); 
		
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
					List<Term> terms = DndDropEventExtractor.getTerms(dropEvent);
					label.addTerms(terms);
					eventBus.fireEvent(new TermCategorizeEvent(terms, label));
					LabelPortlet.this.expand();
					break;
				case PORTLET:
					Menu menu = new CopyMoveMenu(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							LabelPortlet sourcePortlet = DndDropEventExtractor.getLabelPortletSource(dropEvent);
							List<Term> terms = DndDropEventExtractor.getTerms(dropEvent);
							label.addTerms(terms);
							eventBus.fireEvent(new CategorizeCopyTermEvent(terms, sourcePortlet.getLabel(), label));
						}
					}, new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							LabelPortlet sourcePortlet = DndDropEventExtractor.getLabelPortletSource(dropEvent);
							List<Term> terms = DndDropEventExtractor.getTerms(dropEvent);
							label.addTerms(terms);
							sourcePortlet.getLabel().removeTerms(terms);
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
		});
	}

	protected Label getLabel() {
		return label;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

}
