package edu.arizona.biosemantics.oto2.ontologize2.client.common;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.CheckBoxCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Vertex;

public class RelationSelectionDialog  extends Dialog {
	
	public interface VertexProperties extends PropertyAccess<Vertex> {
		  @Path("value")
		  ModelKeyProvider<Vertex> key();
		   
		  @Path("value")
		  LabelProvider<Vertex> nameLabel();
		 
		  ValueProvider<Vertex, String> value();
	}
	
	private static final VertexProperties vertexProperties = GWT.create(VertexProperties.class);
	
	public static class RelationSelectionView implements IsWidget {

		private ListView<Edge, String> listView;
		private ListStore<Edge> store;
		private VerticalLayoutContainer vlc;

		public RelationSelectionView(final List<Edge> relations, final Vertex preferred, final Vertex synonym) {
			store = new ListStore<Edge>(new ModelKeyProvider<Edge>() {
				@Override
				public String getKey(Edge item) {
					return item.toString();
				}
			});
			listView = new ListView<Edge, String>(store, new ValueProvider<Edge, String>() {
				@Override
				public String getValue(Edge object) {
					String edge = object.getSrc().getValue() + " " + 
							object.getType().getSourceLabel() + " of " + 
							object.getDest().getValue();
					if(object.getSrc().equals(synonym)) {
						edge += " -> " + preferred.getValue() + " " + 
								object.getType().getSourceLabel() + " of " + 
								object.getDest().getValue();
					} else if(object.getDest().equals(synonym)) {
						edge += " -> " + object.getSrc().getValue() + " " + 
								object.getType().getSourceLabel() + " of " + 
								preferred.getValue();
					}
					return edge;
				}
				@Override
				public void setValue(Edge object, String value) {	}
				@Override
				public String getPath() {
					return "string";
				}
			});
			
			store.addAll(relations);
			vlc = new VerticalLayoutContainer();
			vlc.add(new HTML("Select the relations you want to re-attach from the synonym to the preferred term."
					+ "<br/>Press <I>Ctrl</I>  in your keyboard to select multiple relations."), new VerticalLayoutData(1, -1));
			vlc.add(listView, new VerticalLayoutData(1, 1));
			//vlc.getScrollSupport().setScrollMode(ScrollMode.AUTOY);
		}

		@Override
		public Widget asWidget() {
			return vlc;
		}

		public List<Edge> getSelection() {
			return listView.getSelectionModel().getSelectedItems();
		}
	}

	private RelationSelectionView view;
	
	public RelationSelectionDialog(List<Edge> relations, Vertex preferred, Vertex synonym) {
		super();
		view = new RelationSelectionView(relations, preferred, synonym);
		this.setTitle("Select Relations");
		this.setHeading("Select Relations");
		this.setHeight(200);
		this.setWidget(view);
		this.setMaximizable(true);
		this.setHideOnButtonClick(true);
		
		this.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
	}

	public List<Edge> getSelection() {
		return view.getSelection();
	}
}
