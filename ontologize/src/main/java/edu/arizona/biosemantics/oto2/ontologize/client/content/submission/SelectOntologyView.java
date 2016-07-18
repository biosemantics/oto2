package edu.arizona.biosemantics.oto2.ontologize.client.content.submission;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.form.ComboBox;

import edu.arizona.biosemantics.oto2.ontologize.client.common.Alerter;
import edu.arizona.biosemantics.oto2.ontologize.client.event.LoadCollectionEvent;
import edu.arizona.biosemantics.oto2.ontologize.client.ModelController;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Ontology;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.OntologyProperties;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyService;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.toontology.IToOntologyServiceAsync;

public class SelectOntologyView implements IsWidget {

	private OntologyProperties ontologyProperties = GWT.create(OntologyProperties.class);
	private IToOntologyServiceAsync toOntologyService = GWT.create(IToOntologyService.class);
	
	private ComboBox<Ontology> ontologyComboBox;
	private ListStore<Ontology> ontologiesStore = new ListStore<Ontology>(ontologyProperties.key());
	private EventBus eventBus;
	
	public SelectOntologyView(EventBus eventBus) {
		this.eventBus = eventBus;
		
		ontologyComboBox = new ComboBox<Ontology>(ontologiesStore, ontologyProperties.prefixLabel());
	    ontologyComboBox.setAllowBlank(false);
	    ontologyComboBox.setAutoValidate(true);
	    ontologyComboBox.setForceSelection(false);
	    ontologyComboBox.setTriggerAction(TriggerAction.ALL);
	    
	    bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
			@Override
			public void onLoad(LoadCollectionEvent event) {				
				refreshOntologies(null);
			}
		});
	}
	
	private void refreshOntologies(final Ontology ontologyToSelect) {
		toOntologyService.getPermanentOntologies(ModelController.getCollection(), new AsyncCallback<List<Ontology>>() {
			@Override
			public void onFailure(Throwable caught) {
				Alerter.getOntologiesFailed(caught);
			}

			@Override
			public void onSuccess(List<Ontology> result) {
				Ontology select = ontologyToSelect;
				ontologiesStore.clear();
				ontologiesStore.addAll(result);
				if(select == null && ontologiesStore.size() == 1)
					select = ontologiesStore.get(0);
				if(select != null)
					ontologyComboBox.setValue(select);
			}
		});
	}
	
	@Override
	public Widget asWidget() {
		return ontologyComboBox;
	}
	
	public void setOntology(Ontology ontology) {
		ontologyComboBox.setValue(ontology);
	}
	
	public Ontology getOntology() {
		return ontologyComboBox.getValue();
	}

	public void setEnabled(boolean value) {
		ontologyComboBox.setEnabled(value);
	}

}
