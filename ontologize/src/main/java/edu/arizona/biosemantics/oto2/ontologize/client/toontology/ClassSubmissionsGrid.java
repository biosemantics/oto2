package edu.arizona.biosemantics.oto2.ontologize.client.toontology;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import edu.arizona.biosemantics.oto2.ontologize.client.Ontologize;
import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.common.ColorableCell;
import edu.arizona.biosemantics.oto2.ontologize.client.common.ColorableCheckBoxCell;
import edu.arizona.biosemantics.oto2.ontologize.client.common.ColorableCheckBoxCell.CommentableColorableProvider;
import edu.arizona.biosemantics.oto2.ontologize.client.event.AddCommentEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.CreateOntologyEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.OntologyClassSubmissionSelectEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RefreshOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.RemoveOntologyClassSubmissionsEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectPartOfEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSuperclassEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SelectSynonymEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.event.SetColorEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Color;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Colorable;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Comment;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Commentable;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmissionProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmissionStatus;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologyClassSubmissionStatusProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmission;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.OntologySynonymSubmissionProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.PartOf;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Superclass;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Synonym;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Type;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public abstract class ClassSubmissionsGrid implements IsWidget {

	private OntologyClassSubmissionProperties ontologyClassSubmissionProperties = GWT.create(OntologyClassSubmissionProperties.class);
	private OntologySynonymSubmissionProperties ontologySynonymSubmissionProperties = GWT.create(OntologySynonymSubmissionProperties.class);
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);
	private EventBus eventBus;
	private Collection collection;
	private Grid<OntologyClassSubmission> grid;
	private ListStore<OntologyClassSubmission> classSubmissionStore;
	private ColumnConfig<OntologyClassSubmission, String> ontologyCol;
	private CheckBoxSelectionModel<OntologyClassSubmission> checkBoxSelectionModel;
	protected ColumnConfig<OntologyClassSubmission, String> termCol;
	protected ColumnConfig<OntologyClassSubmission, String> categoryCol;
	protected ColumnConfig<OntologyClassSubmission, String> definitionCol;
	//private List<Ontology> ontologies;
	protected ColumnConfig<OntologyClassSubmission, String> sourceCol;
	protected ColumnConfig<OntologyClassSubmission, String> sampleCol;
	protected ColumnConfig<OntologyClassSubmission, String> synonymsCol;
	protected ColumnConfig<OntologyClassSubmission, String> userCol;
	protected ColumnConfig<OntologyClassSubmission, String> partOfCol;
	protected ColumnConfig<OntologyClassSubmission, Type> typeCol;
	protected ColumnConfig<OntologyClassSubmission, String> statusCol;
	protected ColumnConfig<OntologyClassSubmission, String> iriCol;
	protected ColumnConfig<OntologyClassSubmission, String> submissionTermCol;
	protected ColumnConfig<OntologyClassSubmission, String> superClassCol;
	
	public ClassSubmissionsGrid(EventBus eventBus, ListStore<OntologyClassSubmission> classSubmissionStore) {		
		this.eventBus = eventBus;
		this.classSubmissionStore = classSubmissionStore;
		grid = new Grid<OntologyClassSubmission>(classSubmissionStore, createColumnModel(classSubmissionStore));
		
		final GroupingView<OntologyClassSubmission> groupingView = new GroupingView<OntologyClassSubmission>();
		groupingView.setShowGroupedColumn(false);
		groupingView.setForceFit(true);
		groupingView.groupBy(ontologyCol);
		
		grid.setView(groupingView);
		grid.setContextMenu(createClassSubmissionsContextMenu());
		
		grid.setSelectionModel(checkBoxSelectionModel);
		//grid.getView().setAutoExpandColumn(taxonBCol);
		//grid.setBorders(false);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		
		//classSubmissionStore.remove.
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				ClassSubmissionsGrid.this.collection = event.getCollection();
				/*toOntologyService.getOntologies(collection, new AsyncCallback<List<Ontology>>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.failedToGetOntologies();
					}
					@Override
					public void onSuccess(List<Ontology> result) {
						ontologies = new LinkedList<Ontology>(result);
					}
				});*/
			}
		});
		
		eventBus.addHandler(SetColorEvent.TYPE, new SetColorEvent.SetColorEventHandler() {
			@Override
			public void onSet(SetColorEvent event) {
				for(Object object : event.getObjects()) {
					if(object instanceof OntologyClassSubmission) {
						OntologyClassSubmission ontologyClassSubmission = (OntologyClassSubmission)object;
						if(grid.getStore().findModel(ontologyClassSubmission) != null)
							grid.getStore().update(ontologyClassSubmission);
					}
				}
			}
		});
		/*eventBus.addHandler(CreateOntologyEvent.TYPE, new CreateOntologyEvent.Handler() {
			@Override
			public void onCreate(CreateOntologyEvent event) {
				ontologies.add(event.getOntology());
			}
		});*/
		getSelectionModel().addSelectionHandler(new SelectionHandler<OntologyClassSubmission>() {
			@Override
			public void onSelection(SelectionEvent<OntologyClassSubmission> event) {
				eventBus.fireEvent(new OntologyClassSubmissionSelectEvent(event.getSelectedItem()));
			}
		});

	}

	private Menu createClassSubmissionsContextMenu() {
		final Menu menu = new Menu();
		menu.addBeforeShowHandler(new BeforeShowHandler() {
			@Override
			public void onBeforeShow(BeforeShowEvent event) {
				menu.clear();
				final List<OntologyClassSubmission> selected = checkBoxSelectionModel.getSelectedItems();
				if(!selected.isEmpty()) {
					menu.add(createAddItem(selected));
					menu.add(createRemoveItem(selected));
					menu.add(new HeaderMenuItem("Annotation"));
					menu.add(createCommentItem(selected));
					if(!collection.getColors().isEmpty()) {
						menu.add(createColorizeItem(selected));
					} 
				} 
				//menu.add(createViewItem());
				event.setCancelled(menu.getWidgetCount() == 0);
			}

			private Widget createColorizeItem(final List<OntologyClassSubmission> selected) {
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
									caught.printStackTrace();
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

			private Widget createCommentItem(final List<OntologyClassSubmission> selected) {
				MenuItem comment = new MenuItem("Comment");
				final OntologyClassSubmission ontologyClassSubmission = selected.get(0);
				comment.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Comment", "");
						box.getTextArea().setValue(getUsersComment(ontologyClassSubmission));
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

					private String getUsersComment(
							OntologyClassSubmission ontologyClassSubmission) {
						// TODO Auto-generated method stub
						return null;
					}
				});
				return comment;
			}
			

			private Widget createAddItem(final List<OntologyClassSubmission> selected) {
				final MenuItem additem = new MenuItem("Add");
				Menu addMenu = new Menu();
				MenuItem subclassItem = new MenuItem("Subclass");
				subclassItem.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						eventBus.fireEvent(new SelectSuperclassEvent(selected.get(0)));
					}
				});
				addMenu.add(subclassItem);
				MenuItem partOfItem = new MenuItem("Part");
				partOfItem.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						eventBus.fireEvent(new SelectPartOfEvent(selected.get(0)));
					}
				});
				addMenu.add(partOfItem);
				MenuItem synonymItem = new MenuItem("Synonym");
				synonymItem.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						eventBus.fireEvent(new SelectSynonymEvent(selected.get(0)));
					}
				});
				addMenu.add(synonymItem);
				
				additem.setSubMenu(addMenu);
				return additem;
			}
			
			/*private Widget createViewItem() {
				MenuItem viewItem = new MenuItem("View Submissions");
				final Menu viewMenu = new Menu();
				viewItem.setSubMenu(viewMenu);
				
				VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
				final TextButton viewButton = new TextButton("View");
				viewButton.setEnabled(false);
				
				FlowLayoutContainer flowLayoutContainer = new FlowLayoutContainer();
				VerticalLayoutContainer checkBoxPanel = new VerticalLayoutContainer();
				flowLayoutContainer.add(checkBoxPanel);
				flowLayoutContainer.setScrollMode(ScrollMode.AUTOY);
				flowLayoutContainer.getElement().getStyle().setProperty("maxHeight", "150px");
				
				final List<Ontology> selectedOntologies = new LinkedList<Ontology>();
				for(final Ontology ontology : ontologies) {
					CheckBox checkBox = new CheckBox();
					checkBox.setBoxLabel(ontology.getAcronym());
					checkBox.setValue(false);
					checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							if(event.getValue())
								selectedOntologies.add(ontology);
							else
								selectedOntologies.remove(ontology);
							viewButton.setEnabled(!selectedOntologies.isEmpty());
						}
					});
					checkBoxPanel.add(checkBox);
				}
				verticalLayoutContainer.add(flowLayoutContainer);
				viewButton.addSelectHandler(new SelectHandler() {
					@Override
					public void onSelect(SelectEvent event) {
						classSubmissionStore.clear();
						toOntologyService.getClassSubmissions(collection, selectedOntologies, true, new AsyncCallback<List<OntologyClassSubmission>>() {
							@Override
							public void onFailure(Throwable caught) {
								
							}
							@Override
							public void onSuccess(List<OntologyClassSubmission> result) {
								classSubmissionStore.addAll(result);
							}
						});
					}
				});
				verticalLayoutContainer.add(viewButton);
				viewMenu.add(verticalLayoutContainer);	
				return viewItem;
			}*/

			private Widget createRemoveItem(final List<OntologyClassSubmission> selected) {
				MenuItem deleteItem = new MenuItem("Remove");
				menu.add(deleteItem);
				deleteItem.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						for(OntologyClassSubmission submission : grid.getSelectionModel().getSelectedItems()) {
							toOntologyService.removeClassSubmission(collection, 
									submission, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Alerter.failedToRemoveOntologyClassSubmission();
								}
								@Override
								public void onSuccess(Void result) {
									eventBus.fireEvent(new RemoveOntologyClassSubmissionsEvent(grid.getSelectionModel().getSelectedItems()));
								}
							});
						}
					}
				});
				return deleteItem;
			}
		});		
		return menu;
	}

	@Override
	public Widget asWidget() {
		return grid;
	}

	public GridSelectionModel<OntologyClassSubmission> getSelectionModel() {
		return grid.getSelectionModel();
	}
	
	private ColumnModel<OntologyClassSubmission> createColumnModel(ListStore<OntologyClassSubmission> classSubmissionStore) {
		IdentityValueProvider<OntologyClassSubmission> identity = new IdentityValueProvider<OntologyClassSubmission>();
		checkBoxSelectionModel = new CheckBoxSelectionModel<OntologyClassSubmission>(
				identity);
		checkBoxSelectionModel.setSelectionMode(SelectionMode.MULTI);

		ColorableCell colorableCell = new ColorableCell(eventBus, collection);
		colorableCell.setCommentColorizableObjectsStore(classSubmissionStore, new ColorableCell.CommentableColorableProvider() {
			@Override
			public Colorable provideColorable(Object source) {
				return (OntologyClassSubmission)source;
			}
			@Override
			public Commentable provideCommentable(Object source) {
				return (OntologyClassSubmission)source;
			}
		});
		
		ValueProvider<OntologyClassSubmission, String> termValueProvider = new ValueProvider<OntologyClassSubmission, String>() {
			@Override
			public String getValue(OntologyClassSubmission object) {
				if(!object.hasTerm())
					return "";
				return object.getTerm().getTerm();
			}
			@Override
			public void setValue(OntologyClassSubmission object, String value) {	}
			@Override
			public String getPath() {
				return "term-term";
			}
		};
		termCol = new ColumnConfig<OntologyClassSubmission, String>(
				termValueProvider, 200, "Candidate Term");
		termCol.setCell(colorableCell);

		
		submissionTermCol = new ColumnConfig<OntologyClassSubmission, String>(
				ontologyClassSubmissionProperties.submissionTerm(), 200, "Term");
		submissionTermCol.setCell(colorableCell);
		
		ValueProvider<OntologyClassSubmission, String> categoryValueProvider = new ValueProvider<OntologyClassSubmission, String>() {
			@Override
			public String getValue(OntologyClassSubmission object) {
				if(!object.hasTerm())
					return "";
				return object.getTerm().getCategory();
			}
			@Override
			public void setValue(OntologyClassSubmission object, String value) {	}
			@Override
			public String getPath() {
				return "term-category";
			}
		};
		categoryCol = new ColumnConfig<OntologyClassSubmission, String>(
				categoryValueProvider, 200, "Category");
		categoryCol.setCell(colorableCell);

		//relationCol.setCell(colorableCell);
		ValueProvider<OntologyClassSubmission, String> ontlogyAcronymValueProvider = new ValueProvider<OntologyClassSubmission, String>() {
			@Override
			public String getValue(OntologyClassSubmission object) {
				return object.getOntology().getAcronym();
			}
			@Override
			public void setValue(OntologyClassSubmission object, String value) {	}
			@Override
			public String getPath() {
				return "ontology-prefix";
			}
		};
		ontologyCol = new ColumnConfig<OntologyClassSubmission, String>(ontlogyAcronymValueProvider, 200, "Ontology");
		ontologyCol.setCell(colorableCell);
		/*ontologyCol = new ColumnConfig<OntologyClassSubmission, Ontology>(
				ontologyClassSubmissionProperties.targetOntology(), 200, "Ontology");
		ontologyCol.setCell(new AbstractCell<Ontology>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,
					Ontology value, SafeHtmlBuilder sb) {
				sb.append(SafeHtmlUtils.fromSafeConstant(value.getName()));
			}
		}); */
		superClassCol = new ColumnConfig<OntologyClassSubmission, String>(
				new ValueProvider<OntologyClassSubmission, String>() {
					@Override
					public String getValue(OntologyClassSubmission object) {
						String result = "";
						for(Superclass superclass : object.getSuperclasses()) {
							result += superclass.toString() + ", ";
						}
						if(result.length() > 0)
							return result.substring(0, result.length() -2);
						return result;
					}
					@Override
					public void setValue(OntologyClassSubmission object, String value) {	}
					@Override
					public String getPath() {
						return "superclasses";
					}
					
				}, 200, "Superclasses");
		superClassCol.setCell(colorableCell);
		definitionCol = new ColumnConfig<OntologyClassSubmission, String>(
				ontologyClassSubmissionProperties.definition(), 200, "Defintion");
		definitionCol.setCell(colorableCell);
		synonymsCol = new ColumnConfig<OntologyClassSubmission, String>(
				new ValueProvider<OntologyClassSubmission, String>() {
					@Override
					public String getValue(OntologyClassSubmission object) {
						String result = "";
						for(Synonym synonym : object.getSynonyms()) {
							result += synonym.getSynonym() + ", ";
						}
						if(result.length() > 0)
							return result.substring(0, result.length() -2);
						return result;
					}
					@Override
					public void setValue(OntologyClassSubmission object, String value) {	}
					@Override
					public String getPath() {
						return "synonyms";
					}
					
				}, 200, "Synonyms");
		synonymsCol.setCell(colorableCell);
		sourceCol = new ColumnConfig<OntologyClassSubmission, String>(
				ontologyClassSubmissionProperties.source(), 200, "Source");
		sourceCol.setCell(colorableCell);
		sampleCol = new ColumnConfig<OntologyClassSubmission, String>(
				ontologyClassSubmissionProperties.sampleSentence(), 200, "Sample Sentence");
		sampleCol.setCell(colorableCell);
		partOfCol = new ColumnConfig<OntologyClassSubmission, String>(
				new ValueProvider<OntologyClassSubmission, String>() {
					@Override
					public String getValue(OntologyClassSubmission object) {
						String result = "";
						for(PartOf partOf : object.getPartOfs()) {
							result += partOf.toString() + ", ";
						}
						if(result.length() > 0)
							return result.substring(0, result.length() -2);
						return result;
					}
					@Override
					public void setValue(OntologyClassSubmission object, String value) {	}
					@Override
					public String getPath() {
						return "partofs";
					}
					
				}, 200, "Part Of");
		partOfCol.setCell(colorableCell);
		
		typeCol = new ColumnConfig<OntologyClassSubmission, Type>(
				ontologyClassSubmissionProperties.type(), 200, "Type");
		
		/*entityCol = new ColumnConfig<OntologyClassSubmission, Boolean>(
				ontologyClassSubmissionProperties.entity(), 200, "Entity");
		ColorableCheckBoxCell colorableCheckBoxCell = new ColorableCheckBoxCell(eventBus, collection);
		colorableCheckBoxCell.disable(null);
		colorableCheckBoxCell.setCommentColorizableObjectsStore((ListStore)classSubmissionStore, new CommentableColorableProvider() {
			@Override
			public Colorable provideColorable(Object source) {
				return (OntologyClassSubmission)source;
			}
			@Override
			public Commentable provideCommentable(Object source) {
				return (OntologyClassSubmission)source;
			}
		});
		entityCol.setCell(colorableCheckBoxCell);
		qualityCol = new ColumnConfig<OntologyClassSubmission, Boolean>(
				ontologyClassSubmissionProperties.quality(), 200, "Quality");
		qualityCol.setCell(colorableCheckBoxCell);*/
		
		
		statusCol = new ColumnConfig<OntologyClassSubmission, String>(
				new ValueProvider<OntologyClassSubmission, String>() {
					@Override
					public String getValue(OntologyClassSubmission object) {
						String status = "";
						for(OntologyClassSubmissionStatus ontologyClassSubmissionStatus : object.getSubmissionStatuses()) {
							//if(edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Status.valueOf(ontologyClassSubmissionStatus.getStatus().getName().toUpperCase())
							//		.equals(edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.Status.ACCEPTED))
								status += ontologyClassSubmissionStatus.getStatus().getName() + ", ";
						}
						return status.length() >= 2 ? status.substring(0, status.length() - 2) : "";
					}
					@Override
					public void setValue(OntologyClassSubmission object, String value) {	}
					@Override
					public String getPath() {
						return "status";
					}
				}, 200, "Status");
		statusCol.setCell(colorableCell);
		iriCol = new ColumnConfig<OntologyClassSubmission, String>(
				ontologyClassSubmissionProperties.classIRI()
				/*new ValueProvider<OntologyClassSubmission, String>() {
					@Override
					public String getValue(OntologyClassSubmission object) {
						for(OntologyClassSubmissionStatus ontologyClassSubmissionStatus : object.getSubmissionStatuses()) {
							if(edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.StatusEnum.valueOf(ontologyClassSubmissionStatus.getStatus().getName().toUpperCase())
									.equals(edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology.StatusEnum.ACCEPTED))
								return ontologyClassSubmissionStatus.getIri();
						}
						return "";
					}
					@Override
					public void setValue(OntologyClassSubmission object, String value) {	}
					@Override
					public String getPath() {
						return "status";
					}
				}*/, 200, "IRI");
		iriCol.setCell(colorableCell);
		userCol = new ColumnConfig<OntologyClassSubmission, String>(
				ontologyClassSubmissionProperties.user(), 200, "User");
		userCol.setCell(colorableCell);
		
		List<ColumnConfig<OntologyClassSubmission, ?>> columns = new ArrayList<ColumnConfig<OntologyClassSubmission, ?>>();
		columns.add(checkBoxSelectionModel.getColumn());
		columns.add(termCol);
		columns.add(submissionTermCol);
		columns.add(categoryCol);
		columns.add(ontologyCol);
		columns.add(statusCol);
		columns.add(iriCol);
		columns.add(superClassCol);
		columns.add(partOfCol);
		//columns.add(entityCol);
		//columns.add(qualityCol);
		columns.add(typeCol);
		columns.add(definitionCol);
		columns.add(sampleCol);
		columns.add(sourceCol);
		columns.add(synonymsCol);
		columns.add(userCol);
		
		setHiddenColumns();
		
//		ValueProvider<Articulation, String> commentValueProvider = new ValueProvider<Articulation, String>() {
//			@Override
//			public String getValue(Articulation object) {
//				if(model.hasComment(object))
//					return model.getComment(object);
//				return "";
//			}
//			@Override
//			public void setValue(Articulation object, String value) {
//				model.setComment(object, value);
//			}
//			@Override
//			public String getPath() {
//				return "comment";
//			}
//		};
//		
//		final ColumnConfig<Articulation, String> commentCol = new ColumnConfig<Articulation, String>(
//				commentValueProvider, 400, "Comment");
//		commentCol.setCell(colorableCell);
		
//		StringFilter<Articulation> createdFilter = new StringFilter<Articulation>(new ArticulationProperties.CreatedStringValueProvder());
//		StringFilter<Articulation> taxonAFilter = new StringFilter<Articulation>(new ArticulationProperties.TaxonAStringValueProvider());
//		StringFilter<Articulation> taxonBFilter = new StringFilter<Articulation>(new ArticulationProperties.TaxonBStringValueProvider());
//		StringFilter<Articulation> commentFilter = new StringFilter<Articulation>(commentValueProvider);
//		
//		ListFilter<Articulation, ArticulationType> relationFilter = new ListFilter<Articulation, ArticulationType>(
//				articulationProperties.type(), this.allTypesStore);
//
//		GridFilters<Articulation> filters = new GridFilters<Articulation>();
//		filters.addFilter(createdFilter);
//		filters.addFilter(taxonAFilter);
//		filters.addFilter(taxonBFilter);
//		filters.addFilter(relationFilter);
//		filters.addFilter(commentFilter);
//		filters.setLocal(true);
//		filters.initPlugin(grid);
//
//		GridInlineEditing<Articulation> editing = new GridInlineEditing<Articulation>(grid);
//		
//		ComboBox<ArticulationType> relationCombo = createRelationCombo();
//		
//		if(this.relationEditEnabled)
//			editing.addEditor(relationCol, relationCombo);
//		editing.addEditor(commentCol, new TextField());
//		editing.addStartEditHandler(new StartEditHandler<Articulation>() {
//			@Override
//			public void onStartEdit(StartEditEvent<Articulation> event) {
//				Articulation articulation = grid.getStore().get(event.getEditCell().getRow());
//				List<ArticulationType> availableTypes = getAvailableTypes(articulation);
//				availableTypesStore.clear();
//				availableTypesStore.addAll(availableTypes);
//			}
//		});
//		/*editing.addBeforeStartEditHandler(new BeforeStartEditHandler<Articulation>() {
//
//			@Override
//			public void onBeforeStartEdit(
//					BeforeStartEditEvent<Articulation> event) {
//				event.get
//			}
//			
//		}); */
//		editing.addCompleteEditHandler(new CompleteEditHandler<Articulation>() {
//			@Override
//			public void onCompleteEdit(CompleteEditEvent<Articulation> event) {			
//				GridCell cell = event.getEditCell();
//				Articulation articulation = grid.getStore().get(cell.getRow());
//				ColumnConfig<Articulation, ?> config = grid.getColumnModel().getColumn(cell.getCol());
//				if(config.equals(relationCol)) {
//					ArticulationType type = (ArticulationType)config.getValueProvider().getValue(articulation);
//					eventBus.fireEvent(new ModifyArticulationEvent(articulation, type));
//				}
//				if(config.equals(commentCol)) {
//					String comment = (String)config.getValueProvider().getValue(articulation);
//					eventBus.fireEvent(new SetCommentEvent(articulation, comment));
//				}
//			}
//		});
//		
		
		

		ColumnModel<OntologyClassSubmission> cm = new ColumnModel<OntologyClassSubmission>(columns);
		return cm;
	}

	protected abstract void setHiddenColumns();
	
}
