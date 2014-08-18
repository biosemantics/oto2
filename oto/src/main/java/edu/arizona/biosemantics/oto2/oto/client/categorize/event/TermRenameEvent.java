package edu.arizona.biosemantics.oto2.oto.client.categorize.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.categorize.event.TermRenameEvent.RenameTermHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class TermRenameEvent extends GwtEvent<RenameTermHandler> {

	public interface RenameTermHandler extends EventHandler {
		void onRename(Term term);
	}
	
    public static Type<RenameTermHandler> TYPE = new Type<RenameTermHandler>();
    
    private Term term;
    
    public TermRenameEvent(Term term) {
        this.term = term;
    }
	
	@Override
	public Type<RenameTermHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RenameTermHandler handler) {
		handler.onRename(term);
	}

}
