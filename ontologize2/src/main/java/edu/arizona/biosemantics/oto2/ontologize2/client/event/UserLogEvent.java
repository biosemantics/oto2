package edu.arizona.biosemantics.oto2.ontologize2.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.ontologize2.client.event.UserLogEvent.Handler;


public class UserLogEvent extends GwtEvent<Handler>{

	public interface Handler extends EventHandler {
		void onLog(UserLogEvent event);
	}
	
    public static Type<Handler> TYPE = new Type<Handler>();
	private String user;
	private String sessionId;
	private String operation;
	private String content;
	
	private UserLogEvent() { }
	
    public UserLogEvent(String user, String sessionId, String operation, String content) {
    	this.user = user;
    	this.sessionId = sessionId;
    	this.operation = operation;
    	this.content = content;
    }
    

	public UserLogEvent(String operation, String content) {
		this.operation = operation;
    	this.content = content;
	}

	public String getUser() {
		return user;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getOperation() {
		return operation;
	}

	public String getContent() {
		return content;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onLog(this);
	}

}
