package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreatePartEvent.Handler;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class CreatePartEvent extends GwtEvent<Handler> implements HasRowId, Serializable {

	public interface Handler extends EventHandler {
		void onCreate(CreatePartEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private Term parent;
	private Term[] parts = new Term[] { };
	private int rowId = -1;
    
	private CreatePartEvent() { }
	
    public CreatePartEvent(Term parent, Term... parts) {
    	this.parent = parent;
    	this.parts = parts;
    }
    
	public CreatePartEvent(Term parent, List<Term> parts) {
		this.parent = parent;
		this.parts = parts.toArray(this.parts);
	}

	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onCreate(this);
	}

	public Term getParent() {
		return parent;
	}

	public Term[] getParts() {
		return parts;
	}

	@Override
	public int getRowId() {
		return rowId;
	}

	@Override
	public void setRowId(int rowId) {
		this.rowId = rowId;
	}

	@Override
	public boolean hasRowId() {
		return rowId != -1;
	}	
	
	public boolean hasParts() {
		return parts.length > 0;
	}	
}
