package edu.arizona.biosemantics.oto2.ontologize.client.layout;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;

import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.ICollectionService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.ICollectionServiceAsync;

public class OntologizePresenter {
	
	private final ICollectionServiceAsync collectionService = GWT.create(ICollectionService.class);
	private Collection collection;
	private OntologizeView view;
	private EventBus eventBus;
	private ModelController modelControler;
	
	public OntologizePresenter(EventBus eventBus) {
		this.eventBus = eventBus;
		modelControler = new ModelController(eventBus);
		
		bindEvents();
	}
	
	private void bindEvents() {
		// TODO Auto-generated method stub
		
	}

	public void setView(OntologizeView view) {
		this.view = view;
	}

	public void loadCollection(int collectionId, String secret) {		
		Collection collection = new Collection();
		collection.setId(collectionId);
		collection.setSecret(secret);
		final MessageBox box = Alerter.startLoading();
		collectionService.get(collectionId, secret, new AsyncCallback<Collection>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.alertFailedToLoadCollection();
				Alerter.stopLoading(box);
			}
			@Override
			public void onSuccess(Collection result) {
				if(result != null)
					eventBus.fireEvent(new LoadCollectionEvent(result));
				Alerter.stopLoading(box);
				
			} 
		});
	}
}
