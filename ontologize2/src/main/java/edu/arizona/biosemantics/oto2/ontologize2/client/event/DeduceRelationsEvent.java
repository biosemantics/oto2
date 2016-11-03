//package edu.arizona.biosemantics.oto2.ontologize2.client.event;
//
//import java.io.Serializable;
//import java.util.List;
//
//import com.google.gwt.event.shared.EventHandler;
//import com.google.gwt.event.shared.GwtEvent;
//import com.google.gwt.event.shared.GwtEvent.Type;
//
//import edu.arizona.biosemantics.oto2.ontologize2.client.event.DeduceRelationsEvent.Handler;
//import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
//
//public class DeduceRelationsEvent extends GwtEvent<Handler> implements Serializable, HasIsRemote {
//
//	public interface Handler extends EventHandler {
//		void onCreate(DeduceRelationsEvent event);
//	}
//	
//    public static Type<Handler> TYPE = new Type<Handler>();
//	private boolean isEffectiveInModel = false;
//	private boolean isRemote = true;
//	
//	
//	@Override
//	public Type<Handler> getAssociatedType() {
//		return TYPE;
//	}
//
//	@Override
//	protected void dispatch(Handler handler) {
//		handler.onCreate(this);
//	}
//	
//	public boolean isEffectiveInModel() {
//		return isEffectiveInModel;
//	}
//	
//	public void setIsRemote(boolean isRemote) {
//		this.isRemote = isRemote;
//	}
//
//	public boolean isRemote() {
//		return isRemote;
//	}
//
//	public void setEffectiveInModel(boolean isEffectiveInModel) {
//		this.isEffectiveInModel = isEffectiveInModel;
//	}
//}
