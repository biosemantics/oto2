package edu.arizona.biosemantics.oto2.oto.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.event.TermRenameEvent.RenameTermHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class TermRenameEvent extends GwtEvent<RenameTermHandler> {

	public interface RenameTermHandler extends EventHandler {
		void onRename(TermRenameEvent event);
	}
	
    public static Type<RenameTermHandler> TYPE = new Type<RenameTermHandler>();
    
    private Term term;
    private String newName;
    
    public TermRenameEvent(Term term, String newName) {
        this.term = term;
        this.newName = newName;
    }
	
	@Override
	public Type<RenameTermHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RenameTermHandler handler) {
		handler.onRename(this);
	}

	public Term getTerm() {
		return term;
	}

	public String getNewName() {
		return newName;
	}

}
