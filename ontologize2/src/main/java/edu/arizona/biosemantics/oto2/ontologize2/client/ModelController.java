package edu.arizona.biosemantics.oto2.ontologize2.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.ClearEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CloseRelationsEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CompositeModifyEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateCandidateEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.HasIsRemote;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReduceGraphEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveCandidateEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceRelationEvent;
import edu.arizona.biosemantics.oto2.ontologize2.client.event.OrderEdgesEvent;
import edu.arizona.biosemantics.oto2.ontologize2.shared.AddCandidateResult;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize2.shared.ICollectionServiceAsync;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Candidate;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Type;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge.Origin;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraphReducer;

public class ModelController {

	private static Collection collection;
	private ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private EventBus eventBus;

	public ModelController(EventBus eventBus) {
		this.eventBus = eventBus;
		bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(CompositeModifyEvent.TYPE, new CompositeModifyEvent.Handler() {
			@Override
			public void onModify(CompositeModifyEvent event) {
				compositeModify(event, event.isRemote());
			}
		});
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {
				loadCollection(event);
			}
		});
		eventBus.addHandler(ClearEvent.TYPE, new ClearEvent.Handler() {
			@Override
			public void onClear(ClearEvent event) {
				clearRelations(event);
			}
		});
		eventBus.addHandler(CreateRelationEvent.TYPE, new CreateRelationEvent.Handler() {
			@Override
			public void onCreate(CreateRelationEvent event) {
				createRelation(event);
			}
		});	
		eventBus.addHandler(RemoveRelationEvent.TYPE, new RemoveRelationEvent.Handler() {
			@Override
			public void onRemove(RemoveRelationEvent event) {
				removeRelation(event);
			}
		});
		eventBus.addHandler(ReplaceRelationEvent.TYPE, new ReplaceRelationEvent.Handler() {
			@Override
			public void onReplace(ReplaceRelationEvent event) {
				replaceRelation(event);
			}
		});
		eventBus.addHandler(CreateCandidateEvent.TYPE, new CreateCandidateEvent.Handler() {
			@Override
			public void onCreate(CreateCandidateEvent event) {
				createCandidate(event);
			}
		});
		eventBus.addHandler(RemoveCandidateEvent.TYPE, new RemoveCandidateEvent.Handler() {
			@Override
			public void onRemove(RemoveCandidateEvent event) {
				removeCandidate(event);
			}
		});
		eventBus.addHandler(CloseRelationsEvent.TYPE, new CloseRelationsEvent.Handler() {
			@Override
			public void onClose(CloseRelationsEvent event) {
				closeRelation(event);
			}
		});
		eventBus.addHandler(OrderEdgesEvent.TYPE, new OrderEdgesEvent.Handler() {
			@Override
			public void onOrder(OrderEdgesEvent event) {
				orderEdges(event);
			}
		});
		eventBus.addHandler(ReduceGraphEvent.TYPE, new ReduceGraphEvent.Handler() {
			@Override
			public void onReduce(ReduceGraphEvent event) {
				reduceGraph();
			}
		});
	}
	
	protected void compositeModify(final CompositeModifyEvent event, boolean remote) {
		if(remote) {
			final MessageBox box = Alerter.startLoading();
			collectionService.compositeModify(collection.getId(), collection.getSecret(), event, new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					Alerter.showAlert("Data out of sync", "The data became out of sync with the server. Please reload the window.", caught);
					Alerter.stopLoading(box);
				}
				@Override
				public void onSuccess(Void result) {
					compositeModify(event, false);
					Alerter.stopLoading(box);
				}
			});
		} else {
			for(GwtEvent<?> e : event.getEvents()) {
				if(e instanceof HasIsRemote) {
					((HasIsRemote)e).setIsRemote(false);
					eventBus.fireEvent(e);
				}
			}
		}
	}

	protected void reduceGraph() {
		final MessageBox box = Alerter.startLoading();
		final MessageBox box2 = Alerter.startLoading();
		collectionService.reduceGraph(collection.getId(), collection.getSecret(), new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.showAlert("Data out of sync", "The data became out of sync with the server. Please reload the window.", caught);
				Alerter.stopLoading(box);
			}
			@Override
			public void onSuccess(Void result) {
				Alerter.stopLoading(box);
			}
		});
		OntologyGraphReducer reducer = new OntologyGraphReducer();
		reducer.reduce(collection.getGraph());
		Alerter.stopLoading(box2);
	}

	protected void clearRelations(ClearEvent event) {
		if(!event.isEffectiveInModel()) {
			final MessageBox box = Alerter.startLoading();
			final MessageBox box2 = Alerter.startLoading();
			collectionService.clear(collection.getId(), collection.getSecret(), new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					Alerter.showAlert("Data out of sync", "The data became out of sync with the server. Please reload the window.", caught);
					Alerter.stopLoading(box);
				}
				@Override
				public void onSuccess(Void result) {
					Alerter.stopLoading(box);
				}
			});
			collection.getGraph().init();
			event.setIsEffectiveInModel(true);
			eventBus.fireEvent(event);
			Alerter.stopLoading(box2);
		}
	}

	protected void orderEdges(OrderEdgesEvent event) {
		if(!event.isEffectiveInModel()) {
			final MessageBox box = Alerter.startLoading();
			final MessageBox box2 = Alerter.startLoading();
			collectionService.order(collection.getId(), collection.getSecret(), 
					event.getSrc(), event.getEdges(), event.getType(), new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					Alerter.showAlert("Data out of sync", "The data became out of sync with the server. Please reload the window.", caught);
					Alerter.stopLoading(box);
				}
				@Override
				public void onSuccess(Void result) {
					Alerter.stopLoading(box);
				}
			});
			try {
				collection.getGraph().setOrderedEdges(event.getSrc(), event.getEdges(), event.getType());
			} catch (Exception e) {
				Alerter.showAlert("Data out of sync", "The data became out of sync with the server. Please reload the window.", e);
			}
			event.setEffectiveInModel(true);
			eventBus.fireEvent(event);
			Alerter.stopLoading(box2);
		} 
	}

	protected void closeRelation(CloseRelationsEvent event) {
		if(!event.isEffectiveInModel()) {
			final MessageBox box = Alerter.startLoading();
			final MessageBox box2 = Alerter.startLoading();
			collectionService.close(collection.getId(), collection.getSecret(), 
					event.getVertex(), event.getType(), event.isClose(), new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					Alerter.showAlert("Data out of sync", "The data became out of sync with the server. Please reload the window.", caught);
					Alerter.stopLoading(box);
				}
				@Override
				public void onSuccess(Void result) {
					Alerter.stopLoading(box);
				}
			});
			
			collection.getGraph().setClosedRelation(event.getVertex(), event.getType(), event.isClose());
			event.setEffectiveInModel(true);
			eventBus.fireEvent(event);
			Alerter.stopLoading(box2);
		} 
	}

	protected void removeCandidate(RemoveCandidateEvent event) {
		final MessageBox box = Alerter.startLoading();
		final MessageBox box2 = Alerter.startLoading();
		collectionService.remove(collection.getId(), collection.getSecret(), Arrays.asList(event.getCandidates()), 
				new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.showAlert("Data out of sync", "The data became out of sync with the server. Please reload the window.", caught);
				Alerter.stopLoading(box2);
			}
			@Override
			public void onSuccess(Void result) {
				Alerter.stopLoading(box2);
			}
		});
		for(Candidate candidate : event.getCandidates())
			collection.remove(candidate.getText());
		Alerter.stopLoading(box);
	}

	protected void createCandidate(CreateCandidateEvent event) {
		final MessageBox box = Alerter.startLoading();
		final MessageBox box2 = Alerter.startLoading();
		collectionService.add(collection.getId(), collection.getSecret(), Arrays.asList(event.getCandidates()), 
				new AsyncCallback<AddCandidateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.showAlert("Data out of sync", "The data became out of sync with the server. Please reload the window.", caught);
				Alerter.stopLoading(box2);
			}
			@Override
			public void onSuccess(AddCandidateResult result) {	
				Alerter.stopLoading(box2);
			}
		});
		for(Candidate candidate : event.getCandidates()) {
			collection.add(candidate);
		}
		Alerter.stopLoading(box);
	}

	protected void replaceRelation(ReplaceRelationEvent event) {
		if(!event.isEffectiveInModel()) {
			final MessageBox box = Alerter.startLoading();
			if(event.isRemote()) {
				final MessageBox box2 = Alerter.startLoading();
				collectionService.replace(collection.getId(), collection.getSecret(), event.getOldRelation(), event.getNewSource(), 
						new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Alerter.showAlert("Data out of sync", "The data became out of sync with the server. Please reload the window.", caught);
						Alerter.stopLoading(box2);
					}
					@Override
					public void onSuccess(Void result) {	
						Alerter.stopLoading(box2);
					}
				});
			}
			try {
				collection.getGraph().replaceRelation(event.getOldRelation(), event.getNewSource());
			} catch(Exception e) {
				Alerter.showAlert("Data out of sync", "The data became out of sync with the server. Please reload the window.", e);
			}
			event.setEffectiveInModel(true);
			eventBus.fireEvent(event);
			Alerter.stopLoading(box);
		}
	}

	protected void removeRelation(RemoveRelationEvent event) {
		if(!event.isEffectiveInModel()) {
			final MessageBox box = Alerter.startLoading();
			for(Edge relation : event.getRelations()) {
				if(event.isRemote()) {
					final MessageBox box2 = Alerter.startLoading();
					collectionService.remove(collection.getId(), collection.getSecret(), relation, 
							event.getRemoveMode(), new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Alerter.showAlert("Data out of sync", "The data became out of sync with the server. Please reload the window.", caught);
							Alerter.stopLoading(box2);
						}
						@Override
						public void onSuccess(Void result) {	
							Alerter.stopLoading(box2);
						}
					});
				}
				try {
					collection.getGraph().removeRelation(relation, event.getRemoveMode());
				} catch (Exception e) {
					Alerter.showAlert("Data out of sync", "The data became out of sync with the server. Please reload the window.", e);
				}
			}
			event.setEffectiveInModel(true);
			eventBus.fireEvent(event);
			Alerter.stopLoading(box);
		}
	}

	protected void loadCollection(LoadCollectionEvent event) {
		if(!event.isEffectiveInModel()) {
			collection = event.getCollection();
			event.setEffectiveInModel(true);
			eventBus.fireEvent(event);
		}
	}

	protected void createRelation(CreateRelationEvent event) {
		if(!event.isEffectiveInModel()) {
			final MessageBox box = Alerter.startLoading();
			for(Edge relation : event.getRelations()) {
				if(event.isRemote()) {
					final MessageBox box2 = Alerter.startLoading();
					collectionService.add(collection.getId(), collection.getSecret(), relation, new AsyncCallback<Boolean>() {
						@Override
						public void onFailure(Throwable caught) {
							Alerter.showAlert("Data out of sync", "The data became out of sync with the server. Please reload the window.", caught);
							Alerter.stopLoading(box2);
						}
						@Override
						public void onSuccess(Boolean result) {
							Alerter.stopLoading(box2);
						}
					});
				}
				try {
					collection.getGraph().addRelation(relation);
				} catch (Exception e) {
					Alerter.showAlert("Data out of sync", "The data became out of sync with the server. Please reload the window.", e);
				}
			}
			event.setEffectiveInModel(true);
			eventBus.fireEvent(event);
			Alerter.stopLoading(box);
		}
	}



	public static Collection getCollection() {
		if(collection == null)
			return new Collection();
		return collection;
	}
}
