package edu.arizona.biosemantics.oto2.oto.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.TermsSortEvent.SortTermsHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;

public class TermsSortEvent extends GwtEvent<SortTermsHandler> {

	public interface SortTermsHandler extends EventHandler {
		void onSort(TermsSortEvent event);
	}
	
    public static Type<SortTermsHandler> TYPE = new Type<SortTermsHandler>();
    
    private Label label;
    
    public TermsSortEvent(Label label) {
        this.label = label;
    }
	
	@Override
	public Type<SortTermsHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SortTermsHandler handler) {
		handler.onSort(this);
	}
	
	public Label getLabel(){
		return label;
	}

}

