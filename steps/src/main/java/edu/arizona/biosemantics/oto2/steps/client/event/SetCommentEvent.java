package edu.arizona.biosemantics.oto2.steps.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.steps.client.event.SetCommentEvent.SetCommentEventHandler;
import edu.arizona.biosemantics.oto2.steps.shared.model.Comment;

public class SetCommentEvent extends GwtEvent<SetCommentEventHandler> {

	public interface SetCommentEventHandler extends EventHandler {
		void onSet(SetCommentEvent event);
	}
	
	public static Type<SetCommentEventHandler> TYPE = new Type<SetCommentEventHandler>();
	private Comment comment;
	private Object object;
	
	public SetCommentEvent(Object object, Comment comment) {
		this.object = object;
		this.comment = comment;
	}
	
	@Override
	public GwtEvent.Type<SetCommentEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SetCommentEventHandler handler) {
		handler.onSet(this);
	}

	public static Type<SetCommentEventHandler> getTYPE() {
		return TYPE;
	}

	public Object getObject() {
		return object;
	}

	public Comment getComment() {
		return comment;
	}
	
}