package edu.arizona.biosemantics.oto2.ontologize.client.content.candidates;

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

import edu.arizona.biosemantics.oto2.ontologize.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize.client.Ontologize;
import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.common.AllowSurpressSelectEventsTreeSelectionModel;
import edu.arizona.biosemantics.oto2.ontologize.client.event.AddCommentEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyClassSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologySynonymSubmissionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
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

public class CandidateTermsView implements IsWidget {
	
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
					for(TextTreeNode child : termsView.getTreeStore().getChildren(node)) {
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
				
				if(!ModelController.getCollection().getColors().isEmpty()) {
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
					ModelController.getCollection().setColorizations((java.util.Collection)selected, null);
					collectionService.update(ModelController.getCollection(), new AsyncCallback<Void>() {
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
			for(final Color color : ModelController.getCollection().getColors()) {
				MenuItem colorItem = new MenuItem(color.getUse());
				colorItem.getElement().getStyle().setProperty("backgroundColor", "#" + color.getHex());
				colorItem.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						ModelController.getCollection().setColorizations((java.util.Collection)selected, color);
						collectionService.update(ModelController.getCollection(), new AsyncCallback<Void>() {
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
							ModelController.getCollection().addComments((java.util.Collection)selected, newComment);
							collectionService.update(ModelController.getCollection(), new AsyncCallback<Void>() {
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
					Alerter.dialogRename(eventBus, term, ModelController.getCollection());
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
	//private TextButton refreshButton = new TextButton("Refresh");
	private EventBus eventBus;
	private AllowSurpressSelectEventsTreeSelectionModel<TextTreeNode> termTreeSelectionModel = 
			new AllowSurpressSelectEventsTreeSelectionModel<TextTreeNode>();
	
	private VerticalLayoutContainer vertical;
	private TermsView termsView;
	
	public CandidateTermsView(EventBus eventBus) {
		this.eventBus = eventBus;

		termsView = new TermsView(eventBus);
		vertical = new VerticalLayoutContainer();
		vertical.add(termsView, new VerticalLayoutData(1, 1));
		//vertical.add(refreshButton, new VerticalLayoutData(1, -1));
		
		//tabPanel = new TabPanel();
		//tabPanel.add(vertical, "Terms");
		termsView.getTree().setSelectionModel(termTreeSelectionModel);
		termsView.getTree().setContextMenu(new TermMenu());
		
		bindEvents();
	}
	
	private void bindEvents() {
		/*refreshButton.addSelectHandler(new SelectHandler() {
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
		});*/		
		termTreeSelectionModel.addSelectionHandler(new SelectionHandler<TextTreeNode>() {
			@Override
			public void onSelection(SelectionEvent<TextTreeNode> event) {
				TextTreeNode node = event.getSelectedItem();
				if(node instanceof TermTreeNode) {
					TermTreeNode termTreeNode = (TermTreeNode)node;
					eventBus.fireEventFromSource(new TermSelectEvent(termTreeNode.getTerm()), CandidateTermsView.this);
				}
			}
		});
		
		eventBus.addHandler(TermSelectEvent.TYPE, new TermSelectEvent.Handler() {
			@Override
			public void onSelect(TermSelectEvent event) {
				Term term = event.getTerm();				
				TermTreeNode termTreeNode = termsView.getTermTermTreeNodeMap().get(term);
				if(termTreeNode != null && termsView.getTreeStore().findModel(termTreeNode) != null && !termTreeSelectionModel.isSelected(termTreeNode)) {
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
				List<TextTreeNode> treeStoreContent = termsView.getTreeStore().getAll();
				for(Term term : event.getTerms()) {
					if(termsView.getTermTermTreeNodeMap().get(term) != null && treeStoreContent.contains(termsView.getTermTermTreeNodeMap().get(term))) 
						termsView.getTreeStore().update(termsView.getTermTermTreeNodeMap().get(term));
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
	
	@Override
	public Widget asWidget() {
		return vertical;
	}
	
	private void update(Term term) {
		List<TextTreeNode> treeStoreContent = termsView.getTreeStore().getAll();
		if(termsView.getTermTermTreeNodeMap().get(term) != null && treeStoreContent.contains(termsView.getTermTermTreeNodeMap().get(term))) 
			termsView.getTreeStore().update(termsView.getTermTermTreeNodeMap().get(term));
	
	}
}