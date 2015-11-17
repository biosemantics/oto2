package edu.arizona.biosemantics.oto2.ontologize.client.content.ontologyview;

import java.util.LinkedList;
import java.util.List;

import com.github.gwtd3.api.Arrays;
import com.github.gwtd3.api.Coords;
import com.github.gwtd3.api.D3;
import com.github.gwtd3.api.arrays.Array;
import com.github.gwtd3.api.arrays.ForEachCallback;
import com.github.gwtd3.api.arrays.NumericForEachCallback;
import com.github.gwtd3.api.core.Selection;
import com.github.gwtd3.api.core.Transition;
import com.github.gwtd3.api.core.UpdateSelection;
import com.github.gwtd3.api.core.Value;
import com.github.gwtd3.api.dsv.Dsv;
import com.github.gwtd3.api.dsv.DsvCallback;
import com.github.gwtd3.api.dsv.DsvObjectAccessor;
import com.github.gwtd3.api.dsv.DsvRow;
import com.github.gwtd3.api.dsv.DsvRows;
import com.github.gwtd3.api.functions.DatumFunction;
import com.github.gwtd3.api.functions.KeyFunction;
import com.github.gwtd3.api.layout.Force;
import com.github.gwtd3.api.layout.HierarchicalLayout;
import com.github.gwtd3.api.layout.Link;
import com.github.gwtd3.api.layout.Tree;
import com.github.gwtd3.api.layout.HierarchicalLayout.Node;
import com.github.gwtd3.api.scales.LinearScale;
import com.github.gwtd3.api.scales.OrdinalScale;
import com.github.gwtd3.api.svg.Area;
import com.github.gwtd3.api.svg.Axis;
import com.github.gwtd3.api.svg.Axis.Orientation;
import com.github.gwtd3.api.svg.Diagonal;
import com.github.gwtd3.api.svg.Line;
import com.github.gwtd3.api.time.TimeFormat;
import com.github.gwtd3.api.time.TimeScale;
import com.github.gwtd3.api.xhr.XmlHttpRequest;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsDate;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import edu.arizona.biosemantics.oto2.ontologize.client.content.ontologyview.TreeDemo.Bundle;
import edu.arizona.biosemantics.oto2.ontologize.client.content.ontologyview.TreeDemo.MyResources;

public class GraphDemo extends FlowPanel {
		
	public interface Bundle extends ClientBundle {
		public static final Bundle INSTANCE = GWT.create(Bundle.class);

		@Source("GraphStyles.css")
		public MyResources css();
	}
	
	interface MyResources extends CssResource {

		String link();

		String node();
	}
	
	private static class Graph extends JavaScriptObject {
       		
		protected Graph() {
			super();
		}
		
        public final native Array<GraphNode> nodes() /*-{
            return this.nodes;
        }-*/;
        
        public final native Array<GraphLink> links() /*-{
	        return this.links;
	    }-*/;
        
        public final native void nodes(Array<GraphNode> nodes)/*-{
            this.nodes = nodes;
        }-*/;
        
        public final native void links(Array<GraphLink> links)/*-{
            this.links = links;
        }-*/;
        
		protected final native int id() /*-{
			return this.id || -1;
		}-*/;
	
		protected final native int id(int id) /*-{
			return this.id = id;
		}-*/;
	
		protected final native void setAttr(String name, JavaScriptObject value) /*-{
			this[name] = value;
		}-*/;
	
		protected final native double setAttr(String name, double value) /*-{
			return this[name] = value;
		}-*/;
	
		protected final native JavaScriptObject getObjAttr(String name) /*-{
			return this[name];
		}-*/;
	
		protected final native double getNumAttr(String name) /*-{
			return this[name];
		}-*/;
        
	}
	
	private static class GraphNode extends Force.Node {
		protected GraphNode() {
			super();
		}

		protected final native int id() /*-{
			return this.id || -1;
		}-*/;

		protected final native int id(int id) /*-{
			return this.id = id;
		}-*/;

		protected final native void setAttr(String name, JavaScriptObject value) /*-{
			this[name] = value;
		}-*/;

		protected final native double setAttr(String name, double value) /*-{
			return this[name] = value;
		}-*/;

		protected final native JavaScriptObject getObjAttr(String name) /*-{
			return this[name];
		}-*/;

		protected final native double getNumAttr(String name) /*-{
			return this[name];
		}-*/;
	}
	
	private static class GraphLink extends Link {
		protected GraphLink() {
			super();
		}

		protected final native int id() /*-{
			return this.id || -1;
		}-*/;

		protected final native int id(int id) /*-{
			return this.id = id;
		}-*/;

		protected final native void setAttr(String name, JavaScriptObject value) /*-{
			this[name] = value;
		}-*/;

		protected final native double setAttr(String name, double value) /*-{
			return this[name] = value;
		}-*/;

		protected final native JavaScriptObject getObjAttr(String name) /*-{
			return this[name];
		}-*/;

		protected final native double getNumAttr(String name) /*-{
			return this[name];
		}-*/;
	}

	final int width = 960;
	final int height = 500;
	private Force force;
	private Selection svg;
	private Graph graph;
	final MyResources css = Bundle.INSTANCE.css();

	public void start() {
		/*
		String data = "{\r\n" + 
				"  \"nodes\":[\r\n" + 
				"    {\"name\":\"Myriel\",\"group\":1},\r\n" + 
				"    {\"name\":\"Napoleon\",\"group\":1},\r\n" + 
				"    {\"name\":\"Mlle.Baptistine\",\"group\":1},\r\n" + 
				"    {\"name\":\"Mme.Magloire\",\"group\":1},\r\n" + 
				"    {\"name\":\"CountessdeLo\",\"group\":1},\r\n" + 
				"    {\"name\":\"Geborand\",\"group\":1},\r\n" + 
				"    {\"name\":\"Champtercier\",\"group\":1},\r\n" + 
				"    {\"name\":\"Cravatte\",\"group\":1},\r\n" + 
				"    {\"name\":\"Count\",\"group\":1},\r\n" + 
				"    {\"name\":\"OldMan\",\"group\":1},\r\n" + 
				"    {\"name\":\"Labarre\",\"group\":2},\r\n" + 
				"    {\"name\":\"Valjean\",\"group\":2},\r\n" + 
				"    {\"name\":\"Marguerite\",\"group\":3},\r\n" + 
				"    {\"name\":\"Mme.deR\",\"group\":2},\r\n" + 
				"    {\"name\":\"Isabeau\",\"group\":2},\r\n" + 
				"    {\"name\":\"Gervais\",\"group\":2},\r\n" + 
				"    {\"name\":\"Tholomyes\",\"group\":3},\r\n" + 
				"    {\"name\":\"Listolier\",\"group\":3},\r\n" + 
				"    {\"name\":\"Fameuil\",\"group\":3},\r\n" + 
				"    {\"name\":\"Blacheville\",\"group\":3},\r\n" + 
				"    {\"name\":\"Favourite\",\"group\":3},\r\n" + 
				"    {\"name\":\"Dahlia\",\"group\":3},\r\n" + 
				"    {\"name\":\"Zephine\",\"group\":3},\r\n" + 
				"    {\"name\":\"Fantine\",\"group\":3},\r\n" + 
				"    {\"name\":\"Mme.Thenardier\",\"group\":4},\r\n" + 
				"    {\"name\":\"Thenardier\",\"group\":4},\r\n" + 
				"    {\"name\":\"Cosette\",\"group\":5},\r\n" + 
				"    {\"name\":\"Javert\",\"group\":4},\r\n" + 
				"    {\"name\":\"Fauchelevent\",\"group\":0},\r\n" + 
				"    {\"name\":\"Bamatabois\",\"group\":2},\r\n" + 
				"    {\"name\":\"Perpetue\",\"group\":3},\r\n" + 
				"    {\"name\":\"Simplice\",\"group\":2},\r\n" + 
				"    {\"name\":\"Scaufflaire\",\"group\":2},\r\n" + 
				"    {\"name\":\"Woman1\",\"group\":2},\r\n" + 
				"    {\"name\":\"Judge\",\"group\":2},\r\n" + 
				"    {\"name\":\"Champmathieu\",\"group\":2},\r\n" + 
				"    {\"name\":\"Brevet\",\"group\":2},\r\n" + 
				"    {\"name\":\"Chenildieu\",\"group\":2},\r\n" + 
				"    {\"name\":\"Cochepaille\",\"group\":2},\r\n" + 
				"    {\"name\":\"Pontmercy\",\"group\":4},\r\n" + 
				"    {\"name\":\"Boulatruelle\",\"group\":6},\r\n" + 
				"    {\"name\":\"Eponine\",\"group\":4},\r\n" + 
				"    {\"name\":\"Anzelma\",\"group\":4},\r\n" + 
				"    {\"name\":\"Woman2\",\"group\":5},\r\n" + 
				"    {\"name\":\"MotherInnocent\",\"group\":0},\r\n" + 
				"    {\"name\":\"Gribier\",\"group\":0},\r\n" + 
				"    {\"name\":\"Jondrette\",\"group\":7},\r\n" + 
				"    {\"name\":\"Mme.Burgon\",\"group\":7},\r\n" + 
				"    {\"name\":\"Gavroche\",\"group\":8},\r\n" + 
				"    {\"name\":\"Gillenormand\",\"group\":5},\r\n" + 
				"    {\"name\":\"Magnon\",\"group\":5},\r\n" + 
				"    {\"name\":\"Mlle.Gillenormand\",\"group\":5},\r\n" + 
				"    {\"name\":\"Mme.Pontmercy\",\"group\":5},\r\n" + 
				"    {\"name\":\"Mlle.Vaubois\",\"group\":5},\r\n" + 
				"    {\"name\":\"Lt.Gillenormand\",\"group\":5},\r\n" + 
				"    {\"name\":\"Marius\",\"group\":8},\r\n" + 
				"    {\"name\":\"BaronessT\",\"group\":5},\r\n" + 
				"    {\"name\":\"Mabeuf\",\"group\":8},\r\n" + 
				"    {\"name\":\"Enjolras\",\"group\":8},\r\n" + 
				"    {\"name\":\"Combeferre\",\"group\":8},\r\n" + 
				"    {\"name\":\"Prouvaire\",\"group\":8},\r\n" + 
				"    {\"name\":\"Feuilly\",\"group\":8},\r\n" + 
				"    {\"name\":\"Courfeyrac\",\"group\":8},\r\n" + 
				"    {\"name\":\"Bahorel\",\"group\":8},\r\n" + 
				"    {\"name\":\"Bossuet\",\"group\":8},\r\n" + 
				"    {\"name\":\"Joly\",\"group\":8},\r\n" + 
				"    {\"name\":\"Grantaire\",\"group\":8},\r\n" + 
				"    {\"name\":\"MotherPlutarch\",\"group\":9},\r\n" + 
				"    {\"name\":\"Gueulemer\",\"group\":4},\r\n" + 
				"    {\"name\":\"Babet\",\"group\":4},\r\n" + 
				"    {\"name\":\"Claquesous\",\"group\":4},\r\n" + 
				"    {\"name\":\"Montparnasse\",\"group\":4},\r\n" + 
				"    {\"name\":\"Toussaint\",\"group\":5},\r\n" + 
				"    {\"name\":\"Child1\",\"group\":10},\r\n" + 
				"    {\"name\":\"Child2\",\"group\":10},\r\n" + 
				"    {\"name\":\"Brujon\",\"group\":4},\r\n" + 
				"    {\"name\":\"Mme.Hucheloup\",\"group\":8}\r\n" + 
				"  ],\r\n" + 
				"  \"links\":[\r\n" + 
				"    {\"source\":1,\"target\":0,\"value\":1},\r\n" + 
				"    {\"source\":2,\"target\":0,\"value\":8},\r\n" + 
				"    {\"source\":3,\"target\":0,\"value\":10},\r\n" + 
				"    {\"source\":3,\"target\":2,\"value\":6},\r\n" + 
				"    {\"source\":4,\"target\":0,\"value\":1},\r\n" + 
				"    {\"source\":5,\"target\":0,\"value\":1},\r\n" + 
				"    {\"source\":6,\"target\":0,\"value\":1},\r\n" + 
				"    {\"source\":7,\"target\":0,\"value\":1},\r\n" + 
				"    {\"source\":8,\"target\":0,\"value\":2},\r\n" + 
				"    {\"source\":9,\"target\":0,\"value\":1},\r\n" + 
				"    {\"source\":11,\"target\":10,\"value\":1},\r\n" + 
				"    {\"source\":11,\"target\":3,\"value\":3},\r\n" + 
				"    {\"source\":11,\"target\":2,\"value\":3},\r\n" + 
				"    {\"source\":11,\"target\":0,\"value\":5},\r\n" + 
				"    {\"source\":12,\"target\":11,\"value\":1},\r\n" + 
				"    {\"source\":13,\"target\":11,\"value\":1},\r\n" + 
				"    {\"source\":14,\"target\":11,\"value\":1},\r\n" + 
				"    {\"source\":15,\"target\":11,\"value\":1},\r\n" + 
				"    {\"source\":17,\"target\":16,\"value\":4},\r\n" + 
				"    {\"source\":18,\"target\":16,\"value\":4},\r\n" + 
				"    {\"source\":18,\"target\":17,\"value\":4},\r\n" + 
				"    {\"source\":19,\"target\":16,\"value\":4},\r\n" + 
				"    {\"source\":19,\"target\":17,\"value\":4},\r\n" + 
				"    {\"source\":19,\"target\":18,\"value\":4},\r\n" + 
				"    {\"source\":20,\"target\":16,\"value\":3},\r\n" + 
				"    {\"source\":20,\"target\":17,\"value\":3},\r\n" + 
				"    {\"source\":20,\"target\":18,\"value\":3},\r\n" + 
				"    {\"source\":20,\"target\":19,\"value\":4},\r\n" + 
				"    {\"source\":21,\"target\":16,\"value\":3},\r\n" + 
				"    {\"source\":21,\"target\":17,\"value\":3},\r\n" + 
				"    {\"source\":21,\"target\":18,\"value\":3},\r\n" + 
				"    {\"source\":21,\"target\":19,\"value\":3},\r\n" + 
				"    {\"source\":21,\"target\":20,\"value\":5},\r\n" + 
				"    {\"source\":22,\"target\":16,\"value\":3},\r\n" + 
				"    {\"source\":22,\"target\":17,\"value\":3},\r\n" + 
				"    {\"source\":22,\"target\":18,\"value\":3},\r\n" + 
				"    {\"source\":22,\"target\":19,\"value\":3},\r\n" + 
				"    {\"source\":22,\"target\":20,\"value\":4},\r\n" + 
				"    {\"source\":22,\"target\":21,\"value\":4},\r\n" + 
				"    {\"source\":23,\"target\":16,\"value\":3},\r\n" + 
				"    {\"source\":23,\"target\":17,\"value\":3},\r\n" + 
				"    {\"source\":23,\"target\":18,\"value\":3},\r\n" + 
				"    {\"source\":23,\"target\":19,\"value\":3},\r\n" + 
				"    {\"source\":23,\"target\":20,\"value\":4},\r\n" + 
				"    {\"source\":23,\"target\":21,\"value\":4},\r\n" + 
				"    {\"source\":23,\"target\":22,\"value\":4},\r\n" + 
				"    {\"source\":23,\"target\":12,\"value\":2},\r\n" + 
				"    {\"source\":23,\"target\":11,\"value\":9},\r\n" + 
				"    {\"source\":24,\"target\":23,\"value\":2},\r\n" + 
				"    {\"source\":24,\"target\":11,\"value\":7},\r\n" + 
				"    {\"source\":25,\"target\":24,\"value\":13},\r\n" + 
				"    {\"source\":25,\"target\":23,\"value\":1},\r\n" + 
				"    {\"source\":25,\"target\":11,\"value\":12},\r\n" + 
				"    {\"source\":26,\"target\":24,\"value\":4},\r\n" + 
				"    {\"source\":26,\"target\":11,\"value\":31},\r\n" + 
				"    {\"source\":26,\"target\":16,\"value\":1},\r\n" + 
				"    {\"source\":26,\"target\":25,\"value\":1},\r\n" + 
				"    {\"source\":27,\"target\":11,\"value\":17},\r\n" + 
				"    {\"source\":27,\"target\":23,\"value\":5},\r\n" + 
				"    {\"source\":27,\"target\":25,\"value\":5},\r\n" + 
				"    {\"source\":27,\"target\":24,\"value\":1},\r\n" + 
				"    {\"source\":27,\"target\":26,\"value\":1},\r\n" + 
				"    {\"source\":28,\"target\":11,\"value\":8},\r\n" + 
				"    {\"source\":28,\"target\":27,\"value\":1},\r\n" + 
				"    {\"source\":29,\"target\":23,\"value\":1},\r\n" + 
				"    {\"source\":29,\"target\":27,\"value\":1},\r\n" + 
				"    {\"source\":29,\"target\":11,\"value\":2},\r\n" + 
				"    {\"source\":30,\"target\":23,\"value\":1},\r\n" + 
				"    {\"source\":31,\"target\":30,\"value\":2},\r\n" + 
				"    {\"source\":31,\"target\":11,\"value\":3},\r\n" + 
				"    {\"source\":31,\"target\":23,\"value\":2},\r\n" + 
				"    {\"source\":31,\"target\":27,\"value\":1},\r\n" + 
				"    {\"source\":32,\"target\":11,\"value\":1},\r\n" + 
				"    {\"source\":33,\"target\":11,\"value\":2},\r\n" + 
				"    {\"source\":33,\"target\":27,\"value\":1},\r\n" + 
				"    {\"source\":34,\"target\":11,\"value\":3},\r\n" + 
				"    {\"source\":34,\"target\":29,\"value\":2},\r\n" + 
				"    {\"source\":35,\"target\":11,\"value\":3},\r\n" + 
				"    {\"source\":35,\"target\":34,\"value\":3},\r\n" + 
				"    {\"source\":35,\"target\":29,\"value\":2},\r\n" + 
				"    {\"source\":36,\"target\":34,\"value\":2},\r\n" + 
				"    {\"source\":36,\"target\":35,\"value\":2},\r\n" + 
				"    {\"source\":36,\"target\":11,\"value\":2},\r\n" + 
				"    {\"source\":36,\"target\":29,\"value\":1},\r\n" + 
				"    {\"source\":37,\"target\":34,\"value\":2},\r\n" + 
				"    {\"source\":37,\"target\":35,\"value\":2},\r\n" + 
				"    {\"source\":37,\"target\":36,\"value\":2},\r\n" + 
				"    {\"source\":37,\"target\":11,\"value\":2},\r\n" + 
				"    {\"source\":37,\"target\":29,\"value\":1},\r\n" + 
				"    {\"source\":38,\"target\":34,\"value\":2},\r\n" + 
				"    {\"source\":38,\"target\":35,\"value\":2},\r\n" + 
				"    {\"source\":38,\"target\":36,\"value\":2},\r\n" + 
				"    {\"source\":38,\"target\":37,\"value\":2},\r\n" + 
				"    {\"source\":38,\"target\":11,\"value\":2},\r\n" + 
				"    {\"source\":38,\"target\":29,\"value\":1},\r\n" + 
				"    {\"source\":39,\"target\":25,\"value\":1},\r\n" + 
				"    {\"source\":40,\"target\":25,\"value\":1},\r\n" + 
				"    {\"source\":41,\"target\":24,\"value\":2},\r\n" + 
				"    {\"source\":41,\"target\":25,\"value\":3},\r\n" + 
				"    {\"source\":42,\"target\":41,\"value\":2},\r\n" + 
				"    {\"source\":42,\"target\":25,\"value\":2},\r\n" + 
				"    {\"source\":42,\"target\":24,\"value\":1},\r\n" + 
				"    {\"source\":43,\"target\":11,\"value\":3},\r\n" + 
				"    {\"source\":43,\"target\":26,\"value\":1},\r\n" + 
				"    {\"source\":43,\"target\":27,\"value\":1},\r\n" + 
				"    {\"source\":44,\"target\":28,\"value\":3},\r\n" + 
				"    {\"source\":44,\"target\":11,\"value\":1},\r\n" + 
				"    {\"source\":45,\"target\":28,\"value\":2},\r\n" + 
				"    {\"source\":47,\"target\":46,\"value\":1},\r\n" + 
				"    {\"source\":48,\"target\":47,\"value\":2},\r\n" + 
				"    {\"source\":48,\"target\":25,\"value\":1},\r\n" + 
				"    {\"source\":48,\"target\":27,\"value\":1},\r\n" + 
				"    {\"source\":48,\"target\":11,\"value\":1},\r\n" + 
				"    {\"source\":49,\"target\":26,\"value\":3},\r\n" + 
				"    {\"source\":49,\"target\":11,\"value\":2},\r\n" + 
				"    {\"source\":50,\"target\":49,\"value\":1},\r\n" + 
				"    {\"source\":50,\"target\":24,\"value\":1},\r\n" + 
				"    {\"source\":51,\"target\":49,\"value\":9},\r\n" + 
				"    {\"source\":51,\"target\":26,\"value\":2},\r\n" + 
				"    {\"source\":51,\"target\":11,\"value\":2},\r\n" + 
				"    {\"source\":52,\"target\":51,\"value\":1},\r\n" + 
				"    {\"source\":52,\"target\":39,\"value\":1},\r\n" + 
				"    {\"source\":53,\"target\":51,\"value\":1},\r\n" + 
				"    {\"source\":54,\"target\":51,\"value\":2},\r\n" + 
				"    {\"source\":54,\"target\":49,\"value\":1},\r\n" + 
				"    {\"source\":54,\"target\":26,\"value\":1},\r\n" + 
				"    {\"source\":55,\"target\":51,\"value\":6},\r\n" + 
				"    {\"source\":55,\"target\":49,\"value\":12},\r\n" + 
				"    {\"source\":55,\"target\":39,\"value\":1},\r\n" + 
				"    {\"source\":55,\"target\":54,\"value\":1},\r\n" + 
				"    {\"source\":55,\"target\":26,\"value\":21},\r\n" + 
				"    {\"source\":55,\"target\":11,\"value\":19},\r\n" + 
				"    {\"source\":55,\"target\":16,\"value\":1},\r\n" + 
				"    {\"source\":55,\"target\":25,\"value\":2},\r\n" + 
				"    {\"source\":55,\"target\":41,\"value\":5},\r\n" + 
				"    {\"source\":55,\"target\":48,\"value\":4},\r\n" + 
				"    {\"source\":56,\"target\":49,\"value\":1},\r\n" + 
				"    {\"source\":56,\"target\":55,\"value\":1},\r\n" + 
				"    {\"source\":57,\"target\":55,\"value\":1},\r\n" + 
				"    {\"source\":57,\"target\":41,\"value\":1},\r\n" + 
				"    {\"source\":57,\"target\":48,\"value\":1},\r\n" + 
				"    {\"source\":58,\"target\":55,\"value\":7},\r\n" + 
				"    {\"source\":58,\"target\":48,\"value\":7},\r\n" + 
				"    {\"source\":58,\"target\":27,\"value\":6},\r\n" + 
				"    {\"source\":58,\"target\":57,\"value\":1},\r\n" + 
				"    {\"source\":58,\"target\":11,\"value\":4},\r\n" + 
				"    {\"source\":59,\"target\":58,\"value\":15},\r\n" + 
				"    {\"source\":59,\"target\":55,\"value\":5},\r\n" + 
				"    {\"source\":59,\"target\":48,\"value\":6},\r\n" + 
				"    {\"source\":59,\"target\":57,\"value\":2},\r\n" + 
				"    {\"source\":60,\"target\":48,\"value\":1},\r\n" + 
				"    {\"source\":60,\"target\":58,\"value\":4},\r\n" + 
				"    {\"source\":60,\"target\":59,\"value\":2},\r\n" + 
				"    {\"source\":61,\"target\":48,\"value\":2},\r\n" + 
				"    {\"source\":61,\"target\":58,\"value\":6},\r\n" + 
				"    {\"source\":61,\"target\":60,\"value\":2},\r\n" + 
				"    {\"source\":61,\"target\":59,\"value\":5},\r\n" + 
				"    {\"source\":61,\"target\":57,\"value\":1},\r\n" + 
				"    {\"source\":61,\"target\":55,\"value\":1},\r\n" + 
				"    {\"source\":62,\"target\":55,\"value\":9},\r\n" + 
				"    {\"source\":62,\"target\":58,\"value\":17},\r\n" + 
				"    {\"source\":62,\"target\":59,\"value\":13},\r\n" + 
				"    {\"source\":62,\"target\":48,\"value\":7},\r\n" + 
				"    {\"source\":62,\"target\":57,\"value\":2},\r\n" + 
				"    {\"source\":62,\"target\":41,\"value\":1},\r\n" + 
				"    {\"source\":62,\"target\":61,\"value\":6},\r\n" + 
				"    {\"source\":62,\"target\":60,\"value\":3},\r\n" + 
				"    {\"source\":63,\"target\":59,\"value\":5},\r\n" + 
				"    {\"source\":63,\"target\":48,\"value\":5},\r\n" + 
				"    {\"source\":63,\"target\":62,\"value\":6},\r\n" + 
				"    {\"source\":63,\"target\":57,\"value\":2},\r\n" + 
				"    {\"source\":63,\"target\":58,\"value\":4},\r\n" + 
				"    {\"source\":63,\"target\":61,\"value\":3},\r\n" + 
				"    {\"source\":63,\"target\":60,\"value\":2},\r\n" + 
				"    {\"source\":63,\"target\":55,\"value\":1},\r\n" + 
				"    {\"source\":64,\"target\":55,\"value\":5},\r\n" + 
				"    {\"source\":64,\"target\":62,\"value\":12},\r\n" + 
				"    {\"source\":64,\"target\":48,\"value\":5},\r\n" + 
				"    {\"source\":64,\"target\":63,\"value\":4},\r\n" + 
				"    {\"source\":64,\"target\":58,\"value\":10},\r\n" + 
				"    {\"source\":64,\"target\":61,\"value\":6},\r\n" + 
				"    {\"source\":64,\"target\":60,\"value\":2},\r\n" + 
				"    {\"source\":64,\"target\":59,\"value\":9},\r\n" + 
				"    {\"source\":64,\"target\":57,\"value\":1},\r\n" + 
				"    {\"source\":64,\"target\":11,\"value\":1},\r\n" + 
				"    {\"source\":65,\"target\":63,\"value\":5},\r\n" + 
				"    {\"source\":65,\"target\":64,\"value\":7},\r\n" + 
				"    {\"source\":65,\"target\":48,\"value\":3},\r\n" + 
				"    {\"source\":65,\"target\":62,\"value\":5},\r\n" + 
				"    {\"source\":65,\"target\":58,\"value\":5},\r\n" + 
				"    {\"source\":65,\"target\":61,\"value\":5},\r\n" + 
				"    {\"source\":65,\"target\":60,\"value\":2},\r\n" + 
				"    {\"source\":65,\"target\":59,\"value\":5},\r\n" + 
				"    {\"source\":65,\"target\":57,\"value\":1},\r\n" + 
				"    {\"source\":65,\"target\":55,\"value\":2},\r\n" + 
				"    {\"source\":66,\"target\":64,\"value\":3},\r\n" + 
				"    {\"source\":66,\"target\":58,\"value\":3},\r\n" + 
				"    {\"source\":66,\"target\":59,\"value\":1},\r\n" + 
				"    {\"source\":66,\"target\":62,\"value\":2},\r\n" + 
				"    {\"source\":66,\"target\":65,\"value\":2},\r\n" + 
				"    {\"source\":66,\"target\":48,\"value\":1},\r\n" + 
				"    {\"source\":66,\"target\":63,\"value\":1},\r\n" + 
				"    {\"source\":66,\"target\":61,\"value\":1},\r\n" + 
				"    {\"source\":66,\"target\":60,\"value\":1},\r\n" + 
				"    {\"source\":67,\"target\":57,\"value\":3},\r\n" + 
				"    {\"source\":68,\"target\":25,\"value\":5},\r\n" + 
				"    {\"source\":68,\"target\":11,\"value\":1},\r\n" + 
				"    {\"source\":68,\"target\":24,\"value\":1},\r\n" + 
				"    {\"source\":68,\"target\":27,\"value\":1},\r\n" + 
				"    {\"source\":68,\"target\":48,\"value\":1},\r\n" + 
				"    {\"source\":68,\"target\":41,\"value\":1},\r\n" + 
				"    {\"source\":69,\"target\":25,\"value\":6},\r\n" + 
				"    {\"source\":69,\"target\":68,\"value\":6},\r\n" + 
				"    {\"source\":69,\"target\":11,\"value\":1},\r\n" + 
				"    {\"source\":69,\"target\":24,\"value\":1},\r\n" + 
				"    {\"source\":69,\"target\":27,\"value\":2},\r\n" + 
				"    {\"source\":69,\"target\":48,\"value\":1},\r\n" + 
				"    {\"source\":69,\"target\":41,\"value\":1},\r\n" + 
				"    {\"source\":70,\"target\":25,\"value\":4},\r\n" + 
				"    {\"source\":70,\"target\":69,\"value\":4},\r\n" + 
				"    {\"source\":70,\"target\":68,\"value\":4},\r\n" + 
				"    {\"source\":70,\"target\":11,\"value\":1},\r\n" + 
				"    {\"source\":70,\"target\":24,\"value\":1},\r\n" + 
				"    {\"source\":70,\"target\":27,\"value\":1},\r\n" + 
				"    {\"source\":70,\"target\":41,\"value\":1},\r\n" + 
				"    {\"source\":70,\"target\":58,\"value\":1},\r\n" + 
				"    {\"source\":71,\"target\":27,\"value\":1},\r\n" + 
				"    {\"source\":71,\"target\":69,\"value\":2},\r\n" + 
				"    {\"source\":71,\"target\":68,\"value\":2},\r\n" + 
				"    {\"source\":71,\"target\":70,\"value\":2},\r\n" + 
				"    {\"source\":71,\"target\":11,\"value\":1},\r\n" + 
				"    {\"source\":71,\"target\":48,\"value\":1},\r\n" + 
				"    {\"source\":71,\"target\":41,\"value\":1},\r\n" + 
				"    {\"source\":71,\"target\":25,\"value\":1},\r\n" + 
				"    {\"source\":72,\"target\":26,\"value\":2},\r\n" + 
				"    {\"source\":72,\"target\":27,\"value\":1},\r\n" + 
				"    {\"source\":72,\"target\":11,\"value\":1},\r\n" + 
				"    {\"source\":73,\"target\":48,\"value\":2},\r\n" + 
				"    {\"source\":74,\"target\":48,\"value\":2},\r\n" + 
				"    {\"source\":74,\"target\":73,\"value\":3},\r\n" + 
				"    {\"source\":75,\"target\":69,\"value\":3},\r\n" + 
				"    {\"source\":75,\"target\":68,\"value\":3},\r\n" + 
				"    {\"source\":75,\"target\":25,\"value\":3},\r\n" + 
				"    {\"source\":75,\"target\":48,\"value\":1},\r\n" + 
				"    {\"source\":75,\"target\":41,\"value\":1},\r\n" + 
				"    {\"source\":75,\"target\":70,\"value\":1},\r\n" + 
				"    {\"source\":75,\"target\":71,\"value\":1},\r\n" + 
				"    {\"source\":76,\"target\":64,\"value\":1},\r\n" + 
				"    {\"source\":76,\"target\":65,\"value\":1},\r\n" + 
				"    {\"source\":76,\"target\":66,\"value\":1},\r\n" + 
				"    {\"source\":76,\"target\":63,\"value\":1},\r\n" + 
				"    {\"source\":76,\"target\":62,\"value\":1},\r\n" + 
				"    {\"source\":76,\"target\":48,\"value\":1},\r\n" + 
				"    {\"source\":76,\"target\":58,\"value\":1}\r\n" + 
				"  ]\r\n" + 
				"}";*/
		
		
		String data = "{\r\n" + 
				"  \"nodes\":[\r\n" + 
				"    {\"name\":\"Myriel\",\"group\":1},\r\n" + 
				"    {\"name\":\"Napoleon\",\"group\":1}" + 
				"  ],\r\n" + 
				"  \"links\":[\r\n" + 
				"    {\"source\":1,\"target\":0,\"value\":1}" +
				"  ]\r\n" + 
				"}";
		
		graph = JSONParser.parseLenient(data).isObject().getJavaScriptObject().<Graph> cast();		
		
		System.out.println(graph.links().length());
		Array<GraphLink> graphLinks = graph.links();
		GraphLink l = graphLinks.get(0);
		com.github.gwtd3.api.layout.Node n = l.source();
		GraphNode graphNode = graph.links().get(0).source().cast();
		
		System.out.println(graphNode.id());
		
		
		//final OrdinalScale color = D3.scale.category20();
		
		force = D3.layout().force().charge(-120).linkDistance(30);//.size(new [width, height]);
		svg = D3.select(this).append("svg").attr("width", width).attr("height", height);

		
		force.nodes(graph.nodes()).links(graph.links()).start();
		
		final Selection link = svg.selectAll(".link")
			      .data(graph.links())
			      .enter().append("line")
			      .attr("class", css.link()).style("stroke-width", new DatumFunction<Double>() {
					@Override
					public Double apply(Element context, Value d, int index) {
						return Math.sqrt(d.asDouble());
					}
			      });
		
		final Selection gnodes = svg.selectAll("g.gnode")
			     .data(graph.nodes())
			     .enter()
			     .append("g")
			     .classed("gnode", true);
		
		final Selection node = gnodes.append("circle")
			      .attr("class", css.node())
			      .attr("r", 5)
			      /*.style("fill", new DatumFunction<Value>() {
					@Override
					public Value apply(Element context, Value d, int index) {

				    	  return color.apply(d); 
					} 
			      })*/.call(force.drag());

		final Selection	labels = gnodes.append("text").text(new DatumFunction<String>() {
			@Override
			public String apply(Element context, Value d, int index) {
				return "test";
			}
		});
		
		
		/*force.on("tick", new DatumFunction<Void>() {
			@Override
			public Void apply(Element context, Value d, int index) {
				link.attr("x1", new DatumFunction<Double>() {
					@Override
					public Double apply(Element context, Value d, int index) {
						Link link = d.<Link> as();
						return link.source().x();
					}
				});
				link.attr("y1", new DatumFunction<Double>() {
					@Override
					public Double apply(Element context, Value d, int index) {
						Link link = d.<Link> as();
						return link.source().y();
					}
				});
				link.attr("x2", new DatumFunction<Double>() {
					@Override
					public Double apply(Element context, Value d, int index) {
						Link link = d.<Link> as();
						return link.target().x();
					}
				});
				link.attr("y2", new DatumFunction<Double>() {
					@Override
					public Double apply(Element context, Value d, int index) {
						Link link = d.<Link> as();
						return link.target().y();
					}
				});
				
				gnodes.attr("transform", new DatumFunction<String>() {
					@Override
					public String apply(Element context, Value d, int index) {
						 return "translate(' + [d.x, d.y] + ')"; 
					}
				});
			       
				
				return null;
			}
		});*/
	
		
	}
}
