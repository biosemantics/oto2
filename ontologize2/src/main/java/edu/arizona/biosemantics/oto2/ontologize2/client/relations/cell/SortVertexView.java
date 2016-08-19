package edu.arizona.biosemantics.oto2.ontologize2.client.relations.cell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.ListViewDragSource;
import com.sencha.gxt.dnd.core.client.ListViewDropTarget;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.OntologyGraph.Edge;

public class SortVertexView extends SimpleContainer {

	private ListStore<Edge> store;

	public SortVertexView(Collection<Edge> edges) {
		store = new ListStore<Edge>(new ModelKeyProvider<Edge>() {
			@Override
			public String getKey(Edge item) {
				return item.getDest().getValue();
			}
		});
		store.setAutoCommit(true);
		store.addAll(edges);
		ListView<Edge, String> listView = new ListView<Edge, String>(store, new ValueProvider<Edge, String>() {
			@Override
			public String getValue(Edge object) {
				return object.getDest().getValue();
			}
			@Override
			public void setValue(Edge object, String value) {	}

			@Override
			public String getPath() {
				return "dest-value";
			}
		});
		ListViewDragSource<Edge> source = new ListViewDragSource<Edge>(listView);
		ListViewDropTarget<Edge> target = new ListViewDropTarget<Edge>(listView);
		target.setFeedback(Feedback.INSERT);
		target.setAllowSelfAsSource(true);
		target.setOperation(Operation.MOVE);
		this.add(listView);
	}
	
	public List<Edge> getEdges() {
		return new ArrayList<Edge>(store.getAll());
	}
	
}
