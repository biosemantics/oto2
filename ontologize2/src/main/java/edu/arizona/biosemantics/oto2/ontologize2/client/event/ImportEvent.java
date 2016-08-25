package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import java.io.Serializable;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.ImportEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;

public class ImportEvent extends GwtEvent<Handler> implements Serializable {

	public interface Handler extends EventHandler {
		void onImport(ImportEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private boolean isEffectiveInModel = false;
	private Edge.Type type;
	private String text;
	
	private ImportEvent() { }
	
    public ImportEvent(Edge.Type type, String text) {
    	this.type = type;
    	this.text = text;
    }
    
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onImport(this);
	}

	public String getText() {
		return text;
	}

	public boolean isEffectiveInModel() {
		return isEffectiveInModel ;
	}

	public void setEffectiveInModel(boolean isEffectiveInModel) {
		this.isEffectiveInModel = isEffectiveInModel;
	}

	public Edge.Type getType() {
		return type;
	}
	
}
