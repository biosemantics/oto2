package edu.arizona.biosemantics.oto2.ontologize2.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuBar;
import com.sencha.gxt.widget.core.client.menu.MenuBarItem;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.oto2.ontologize2.client.candidate.CandidateView;
import edu.arizona.biosemantics.oto2.ontologize2.client.common.TabPanelMessageBox;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.DownloadEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReduceGraphEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.UserLogEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.VisualizationConfigurationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.VisualizationRefreshEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.info.ContextView;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.RelationsView;
import edu.arizona.biosemantics.oto2.ontologize2.client.tree.VisualizationView;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.IUserLogService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.IUserLogServiceAsync;

public class Ontologize extends SimpleContainer {
	
	private IUserLogServiceAsync userLogService =  GWT.create(IUserLogService.class);
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	
	public class MenuView extends MenuBar {
		
		
		
		public MenuView() {
			Menu sub = new Menu();
			MenuBarItem item = new MenuBarItem("File", sub);
			
			/*MenuItem a1 = new MenuItem("O1");
			a1.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					collectionService.get(21, "", new AsyncCallback<Collection>() {
						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
						}
						@Override
						public void onSuccess(Collection result) {
							MessageBox box = Alerter.startLoading();
							eventBus.fireEvent(new LoadCollectionEvent(result));
							Alerter.stopLoading(box);
						}
						
					});
				}
			});
			MenuItem a2 = new MenuItem("O2");
			a2.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					collectionService.get(22, "", new AsyncCallback<Collection>() {
						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
						}
						@Override
						public void onSuccess(final Collection result) {
							final MessageBox box = Alerter.startLoading();
							Scheduler scheduler = Scheduler.get();
							scheduler.scheduleDeferred(new ScheduledCommand() {
								@Override
								public void execute() {
									eventBus.fireEvent(new LoadCollectionEvent(result));
									Alerter.stopLoading(box);
								}
							});
						}
						
					});
				}
			});
			MenuItem a3 = new MenuItem("O3");
			a3.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					collectionService.get(27, "", new AsyncCallback<Collection>() {
						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
						}
						@Override
						public void onSuccess(Collection result) {
							MessageBox box = Alerter.startLoading();
							OntologyGraph g= result.getGraph();
							System.out.println(g.getInRelations(new Vertex("seed")));
							eventBus.fireEvent(new LoadCollectionEvent(result));
							Alerter.stopLoading(box);
						}
						
					});
				}
			});
			sub.add(a1);
			sub.add(a2);
			sub.add(a3);*/
			
			
			
			MenuItem reduceItem = new MenuItem("Remove Redundant Relationships");
			reduceItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					eventBus.fireEvent(new ReduceGraphEvent());
					eventBus.fireEvent(new LoadCollectionEvent(ModelController.getCollection()));
					eventBus.fireEvent(new UserLogEvent(user, null, "Rm_red_rel",null));
				}
			});
			sub.add(reduceItem);
			MenuItem generateItem = new MenuItem("Generate OWL");
			generateItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					
					eventBus.fireEvent(new UserLogEvent(user, null, "gen_owl",null));
					final MessageBox box = Alerter.startLoading();
					collectionService.getOWL(ModelController.getCollection().getId(), 
							ModelController.getCollection().getSecret(), new AsyncCallback<String[][]>() {
							@Override
							public void onFailure(Throwable caught) {
								Alerter.stopLoading(box);
								Alerter.showAlert("Generate OWL", "Failed to generate OWL", caught);
							}
							@Override
							public void onSuccess(String[][] result) {
								Alerter.stopLoading(box);
								final TabPanelMessageBox box = new TabPanelMessageBox("OWL", "");
								box.setModal(true);
								box.setHideOnButtonClick(true);
								for(int i=0; i<result.length; i++) {
									TextArea textArea = new TextArea();
									textArea.setText(result[i][0]);
									box.addTab(result[i][1], textArea);
								}
								box.show();
							}
					});
				}
			});
			sub.add(generateItem);
			
			MenuItem downloadItem = new MenuItem("Download Ontology");//this may call etcsite download
			downloadItem.addSelectionHandler(new SelectionHandler<Item>() {
				
				@Override
				public void onSelection(SelectionEvent<Item> arg0) {
					
					eventBus.fireEvent(new UserLogEvent(user, null, "down_onto",null));
					eventBus.fireEvent(new DownloadEvent(ModelController.getCollection()));
				}
			});
			sub.add(downloadItem);
			add(item);
			
			sub = new Menu();
			item = new MenuBarItem("View", sub);
			CheckMenuItem highlightMultipleSuperclassesItem = new CheckMenuItem("Highlighting");
			highlightMultipleSuperclassesItem.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {
				@Override
				public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
					eventBus.fireEvent(new VisualizationConfigurationEvent(event.getItem().isChecked()));
					eventBus.fireEvent(new VisualizationRefreshEvent());
				}
			});
			sub.add(highlightMultipleSuperclassesItem);
			add(item);
		}
	}
	
	public String user;
	
	private EventBus eventBus = new SimpleEventBus();
	private ModelController modelController;
	
	
	public Ontologize(String user) {
		this.user = user;
		MenuView menuView = new MenuView();
		BorderLayoutContainer blc = new BorderLayoutContainer();
		
		CandidateView candidateView = new CandidateView(eventBus,user);
		ContentPanel cp = new ContentPanel();
		cp.setHeadingText("Candidate Terms");
		cp.add(candidateView);
		BorderLayoutData d = new BorderLayoutData(.20);
		d.setMargins(new Margins(0, 0, 0, 0));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		blc.setWestWidget(cp, d);
		
		VisualizationView visualizationView = new VisualizationView(eventBus);
		cp = new ContentPanel();
		cp.setHeadingText("Tree Visualizations");
		cp.add(visualizationView);
		d = new BorderLayoutData(.20);
		d.setMargins(new Margins(0, 0, 0, 0));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		blc.setEastWidget(cp, d);
		
		ContextView contextView = new ContextView(eventBus);
		cp = new ContentPanel();
		cp.setHeadingText("Context");
		cp.add(contextView);
		d = new BorderLayoutData(.20);
		d.setMargins(new Margins(0, 0, 0, 0));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		blc.setSouthWidget(cp, d);
		
		RelationsView relationsView = new RelationsView(eventBus);
		cp = new ContentPanel();
		cp.setHeadingText("Relations between Terms");
		cp.add(relationsView);
		d = new BorderLayoutData();
		d.setMargins(new Margins(0, 0, 0, 0));
		blc.setCenterWidget(cp, d);
		
		
		modelController = new ModelController(eventBus);
		

		VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
		verticalLayoutContainer.add(menuView, new VerticalLayoutData(1,-1));
		verticalLayoutContainer.add(blc, new VerticalLayoutData(1,1));
		this.setWidget(verticalLayoutContainer);
		
		modelController.setUser(user);
		collectionService.setUser(user,  new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Object result) {
				//Alerter.showAlert("evernt_bus", "set user to collection sucess");
			}
			
		});
	}
	
	
	public Ontologize() {
		MenuView menuView = new MenuView();
		BorderLayoutContainer blc = new BorderLayoutContainer();
		
		CandidateView candidateView = new CandidateView(eventBus);
		ContentPanel cp = new ContentPanel();
		cp.setHeadingText("Candidate Terms");
		cp.add(candidateView);
		BorderLayoutData d = new BorderLayoutData(.20);
		d.setMargins(new Margins(0, 0, 0, 0));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		blc.setWestWidget(cp, d);
		
		VisualizationView visualizationView = new VisualizationView(eventBus);
		cp = new ContentPanel();
		cp.setHeadingText("Tree Visualizations");
		cp.add(visualizationView);
		d = new BorderLayoutData(.20);
		d.setMargins(new Margins(0, 0, 0, 0));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		blc.setEastWidget(cp, d);
		
		ContextView contextView = new ContextView(eventBus);
		cp = new ContentPanel();
		cp.setHeadingText("Context");
		cp.add(contextView);
		d = new BorderLayoutData(.20);
		d.setMargins(new Margins(0, 0, 0, 0));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		blc.setSouthWidget(cp, d);
		
		RelationsView relationsView = new RelationsView(eventBus);
		cp = new ContentPanel();
		cp.setHeadingText("Relations between Terms");
		cp.add(relationsView);
		d = new BorderLayoutData();
		d.setMargins(new Margins(0, 0, 0, 0));
		blc.setCenterWidget(cp, d);
		
		
		modelController = new ModelController(eventBus);
		

		VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
		verticalLayoutContainer.add(menuView, new VerticalLayoutData(1,-1));
		verticalLayoutContainer.add(blc, new VerticalLayoutData(1,1));
		this.setWidget(verticalLayoutContainer);
		
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public void setUser(String user) {
		this.user = user;
		modelController.setUser(user);
		collectionService.setUser(user,  new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Object result) {
				//Alerter.showAlert("evernt_bus", "set user to collection sucess");
			}
			
		});
		//first, last name, email added from ETC
		//Alerter.showAlert("user", "username="+user);
	}

}
