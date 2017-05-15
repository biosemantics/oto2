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

public class TreeDemo extends FlowPanel {
	
	public interface Bundle extends ClientBundle {
		public static final Bundle INSTANCE = GWT.create(Bundle.class);

		@Source("TreeDemoStyles.css")
		public MyResources css();
	}
	
	interface MyResources extends CssResource {

		String link();

		String node();

		String border();
	}
	
	// Perhaps a mutable JSO class would be a nice feature?
	private static class TreeDemoNode extends com.github.gwtd3.api.layout.HierarchicalLayout.Node {
		protected TreeDemoNode() {
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
	
//	private static class Graph extends JavaScriptObject {
//		
//		protected Graph() {
//			super();
//		}
//		
//        public final native Array<GraphNode> nodes() /*-{
//            return this.children;
//        }-*/;
//        
//        public final native Array<GraphLink> links() /*-{
//	        return this.children;
//	    }-*/;
//        
//
//        public final native void nodes(Array<GraphNode> nodes)/*-{
//            this.nodes = nodes;
//        }-*/;
//        
//        public final native void links(Array<GraphLink> links)/*-{
//            this.links = links;
//        }-*/;
//        
//	}
//	
//	// Perhaps a mutable JSO class would be a nice feature?
//	private static class GraphNode extends Node {
//		protected GraphNode() {
//			super();
//		}
//
//		protected final native int id() /*-{
//			return this.id || -1;
//		}-*/;
//
//		protected final native int id(int id) /*-{
//			return this.id = id;
//		}-*/;
//
//		protected final native void setAttr(String name, JavaScriptObject value) /*-{
//			this[name] = value;
//		}-*/;
//
//		protected final native double setAttr(String name, double value) /*-{
//			return this[name] = value;
//		}-*/;
//
//		protected final native JavaScriptObject getObjAttr(String name) /*-{
//			return this[name];
//		}-*/;
//
//		protected final native double getNumAttr(String name) /*-{
//			return this[name];
//		}-*/;
//	}
//	
//	private static class GraphLink extends Link {
//		protected GraphLink() {
//			super();
//		}
//
//		protected final native int id() /*-{
//			return this.id || -1;
//		}-*/;
//
//		protected final native int id(int id) /*-{
//			return this.id = id;
//		}-*/;
//
//		protected final native void setAttr(String name, JavaScriptObject value) /*-{
//			this[name] = value;
//		}-*/;
//
//		protected final native double setAttr(String name, double value) /*-{
//			return this[name] = value;
//		}-*/;
//
//		protected final native JavaScriptObject getObjAttr(String name) /*-{
//			return this[name];
//		}-*/;
//
//		protected final native double getNumAttr(String name) /*-{
//			return this[name];
//		}-*/;
//	}

	private Tree tree;
	private Diagonal diagonal;
	private Selection svg;
	private TreeDemoNode root;
	final MyResources css = Bundle.INSTANCE.css();
	// global references for demo
	static int i = 0;
	final int duration = 750;
		
	/*private static class Data {
		private final String symbol;

		private final JsDate date;

		private final double price;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
	/*	@Override
		public String toString() {
			return "Data [date=" + date.getTime() + ", price=" + price + "]";
		}

		public Data(final String symbol, final JsDate date, final double price) {
			super();
			this.symbol = symbol;
			this.date = date;
			this.price = price;
		}

		public String getSymbol() {
			return symbol;
		}

		public JsDate getDate() {
			return date;
		}

		public double getPrice() {
			return price;
		}
	}*/

	/*var graph = {
			  "nodes":[
			    {"name":"Myriel","group":1},
			    ]
			    "links":[
    			{"source":1,"target":0,"value":1},
		      ]
		};*/
	public void start() {
		final int width = 960;
		final int height = 500;
		
		/*String data = "{\r\n" + 
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
		String data = "{\n\"children\": [\n{\n\"children\": [\n{},\n{},\n{\n\"children\": [\n{}\n]\n},\n{}\n]\n},\n{}\n]\n}";
		root = JSONParser.parseLenient(data).isObject().getJavaScriptObject()
				.<TreeDemoNode> cast();
		System.out.println(root.children().length());
		
		
		// get tree layout
		tree = D3.layout().tree().size(width, height);
		// set the global way to draw paths
		diagonal = D3.svg().diagonal()
				.projection(new DatumFunction<Array<Double>>() {
					@Override
					public Array<Double> apply(Element context, Value d,
							int index) {
						TreeDemoNode data = d.<TreeDemoNode> as();
						return Array.fromDoubles(data.x(), data.y());
					}
				});

		// add the SVG
		svg = D3.select(this).append("svg").attr("width", width + 20)
				.attr("height", height + 280).append("g")
				.attr("transform", "translate(10, 140)");

		// get the root of the tree and initialize it
		root = JSONParser.parseLenient(data).isObject().getJavaScriptObject()
				.<TreeDemoNode> cast();
		root.setAttr("x0", (width - 20) / 2);
		root.setAttr("y0", 0);
		if (root.children() != null) {
			root.children().forEach(new Collapse());
		}
		update(root);
		
		/*
		System.out.println(root);
		System.out.print(root.toSource());

		
		final OrdinalScale color = D3.scale.category20();
		
		
		final Force force = D3.layout().force();
		//final Selection svg = D3.select("body").append("svg").attr("width", width).attr("height", height);
		//final Dsv<Graph> dsv = D3.csv();
		/*D3.csv("", new DsvCallback<Data>() {
			@Override
			public void get(JavaScriptObject error, DsvRows<Data> data) {
				
			}
		});*//*
		Graph graph = JSONParser.parseStrict(data).isObject().getJavaScriptObject()
				.<Graph> cast();
				
		final Selection svg = D3.select(this).append("svg").attr("width", width)
				.attr("height", height);

		System.out.println(	graph.nodes().length());
		System.out.println(graph.links().length());
		force.nodes(graph.nodes()).links(graph.links()).start();
		
		final Selection link = svg.selectAll(".link")
			      .data(graph.links())
			      .enter().append("line")
			      .attr("class", "link");
		
		force.on("tick", new DatumFunction<Void>() {
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
				return null;
			}
		});
		
		
		/*final Selection link = svg.selectAll(".link").data(links).enter().append("line").attr("class", "link").style("stroke-width", new DatumFunction() {
			@Override
			public Object apply(Element context, Value d, int index) {
				return Math.sqrt(d.asDouble());
			}
		});
		final Selection gnodes = svg.selectAll("g.gnode").data(nodes).enter().append("g").classed("gnode", true);
		final Selection node = gnodes.append("circle").attr("class", "node").attr("r", 5).style("fill", new DatumFunction() {
			@Override
			public Object apply(Element context, Value d, int index) {
				return color.apply(d);
			}
		}).call(force.drag());
		
		final Selection labels = gnodes.append("text").text(new DatumFunction() {
			@Override
			public Object apply(Element context, Value d, int index) {
				return d.asString();
			}
		});*/
			
		
		//dsv.parse("");
		
		/*dsv.get(new DsvCallback<Data>() {
			@Override
			public void get(JavaScriptObject error, DsvRows<Data> dataRows) {
				List<Node> nodes = new LinkedList<Node>();
				List<Link> links = new LinkedList<Link>();
				for(Data data : dataRows.asList()) {
					for(MyNode myNode : data.nodes) 
						nodes.add(new Node(myNode.name));
					nodes.addAll(data.nodes);
					links.addAll(data.links);
				}
				force.nodes(nodes).links(links).start();
				svg.selectAll(".link").data(links).enter().append("path").attr("class", "link");
				svg.selectAll(".node").data(nodes).enter().append("circle").attr("class", "node").attr("r", 5)
					/*.style("fill", new DatumFunction() {
						@Override
						public Object apply(Element context, Value d, int index) {
							return color.(d.group); 
						} 
		//			})*///.call(force.drag());
		//		
		//	}
		//});
		
		/*final MyResources css = Bundle.INSTANCE.css();

		final int[] m = new int[] { 80, 80, 80, 80 };
		final int width = 960 - m[1] - m[3];
		final int height = 500 - m[0] - m[2];
		//final TimeFormat format = D3.time().format("%b %Y");

		// Scales and axes. Note the inverted domain for the y-scale: bigger is
		// up!
		//final TimeScale x = D3.time().scale().range(0, width);
		//final LinearScale y = D3.scale.linear().range(height, 0);
		final Axis xAxis = D3.svg().axis().scale(x).tickSize(-height);
		// removed .tickSubdivide(1);
		final Axis yAxis = D3.svg().axis().scale(y).orient(Orientation.RIGHT)
				.ticks(4);

		// An area generator, for the light fill.
		final Area area = D3.svg().area()
				.interpolate(Area.InterpolationMode.MONOTONE)
				// .x(function(d) { return x(d.date); })
				.x(new DatumFunction<Double>() {
					@Override
					public Double apply(final Element context, final Value d,
							final int index) {
						return x.apply(((Data) d.as()).getDate()).asDouble();
					}
				}).y0(height)
				// .y1(function(d) { return y(d.price); });
				.y1(new DatumFunction<Double>() {
					@Override
					public Double apply(final Element context, final Value d,
							final int index) {
						return y.apply(((Data) d.as()).getPrice()).asDouble();
					}
				});

		// A line generator, for the dark stroke.
		final Line line = D3.svg().line()
				.interpolate(Line.InterpolationMode.MONOTONE)
				// .x(function(d) { return x(d.date); })
				.x(new DatumFunction<Double>() {
					@Override
					public Double apply(final Element context, final Value d,
							final int index) {
						return x.apply(((Data) d.as()).getDate()).asDouble();
					}
				})
				// // .y(function(d) { return y(d.price); });
				.y(new DatumFunction<Double>() {
					@Override
					public Double apply(final Element context, final Value d,
							final int index) {
						return y.apply(d.<Data> as().getPrice()).asDouble();
					}
				});

		D3.csv("demo-data/readme.csv", new DsvObjectAccessor<Data>() {
			@Override
			public Data apply(final DsvRow d, final int index) {
				Value value = d.get("symbol");
				if ("S&P 500".equals(value.asString())) {
					String symbol = d.get("symbol").asString();
					JsDate date = format.parse(d.get("date").asString());
					double price = d.get("price").asDouble();
					return new Data(symbol, date, price);
				} else {
					return null;
				}
			}
		}, new DsvCallback<Data>() {
			@Override
			public void get(final JavaScriptObject error,
					final DsvRows<Data> values) {

				if (error != null) {
					XmlHttpRequest xhrError = error.cast();
					String message = xhrError.status() + " ("
							+ xhrError.statusText() + ")";
					Window.alert(message);
					throw new RuntimeException(message);
				}

				// // Compute the minimum and maximum date, and the maximum
				// price.
				x.domain(Array.fromObjects(values.getObject(0).getDate(),
						values.getObject(values.length() - 1).getDate()));

				int maxY = Arrays.max(values, new NumericForEachCallback() {
					@Override
					public double forEach(final Object thisArg,
							final Value element, final int index,
							final Array<?> array) {
						return element.<Data> as().getPrice();
					}
				}).asInt();
				System.out.println("the max Y is " + maxY + " among " + values);
				y.domain(Array.fromInts(0, maxY)).nice();
				// Add an SVG element with the desired dimensions and margin.
				final Selection svg = D3
						.select(AxisComponent.this)
						.append("svg:svg")
						.attr("class", css.svg())
						.attr("width", width + m[1] + m[3])
						.attr("height", height + m[0] + m[2])
						.append("svg:g")
						.attr("transform",
								"translate(" + m[3] + "," + m[0] + ")");

				// Add the clip path.
				svg.append("svg:clipPath").attr("id", "clip")
						.append("svg:rect").attr("width", width).attr("height", height);

				// Add the area path.
				svg.append("svg:path").attr("class", css.area())
						.attr("clip-path", "url(#clip)")
						.attr("d", area.apply(values));

				// Add the x-axis.
				svg.append("svg:g").attr("class", css.x() + " " + css.axis())
						.attr("transform", "translate(0," + height + ")")
						.call(xAxis);

				// Add the y-axis.
				svg.append("svg:g").attr("class", css.y() + " " + css.axis())
						.attr("transform", "translate(" + width + ",0)")
						.call(yAxis);

				// Add the line path.
				svg.append("svg:path").attr("class", css.line())
						.attr("clip-path", "url(#clip)")
						.attr("d", line.generate(values));

				// Add a small label for the symbol name.
				svg.append("svg:text").attr("x", width - 6).attr("y", height - 6)
						.attr("text-anchor", "end")
						.text(values.getObject(0).getSymbol());

				// On click, update the x-axis.
				svg.on(BrowserEvents.CLICK, new DatumFunction<Void>() {
					@Override
					public Void apply(final Element context, final Value d,
							final int index) {
						int n = values.length() - 1;
						int i = (int) Math.floor((Math.random() * n) / 2);
						int j = i + (int) Math.floor((Math.random() * n) / 2)
								+ 1;
						x.domain(Array.fromObjects(values.getObject(i)
								.getDate(), values.getObject(j).getDate()));
						Transition transition = svg.transition().duration(750);
						transition.select("." + css.x() + "." + css.axis())
								.call(xAxis);
						transition.select("." + css.area()).attr("d",
								area.apply(values));
						transition.select("." + css.line()).attr("d",
								line.generate(values));
						return null;
					};
				});
			}
		});
		*/
	}
	
	private void update(final TreeDemoNode source) {
		Array<HierarchicalLayout.Node> nodes = tree.nodes(root).reverse();
		Array<Link> links = tree.links(nodes);

		// normalize depth
		nodes.forEach(new ForEachCallback<Void>() {
			@Override
			public Void forEach(Object thisArg, Value element, int index,
					Array<?> array) {
				TreeDemoNode datum = element.<TreeDemoNode> as();
				datum.setAttr("y", datum.depth() * 180);
				return null;
			}
		});

		// assign ids to nodes
		UpdateSelection node = svg.selectAll("g." + css.node()).data(nodes,
				new KeyFunction<Integer>() {
					@Override
					public Integer map(Element context, Array<?> newDataArray,
							Value datum, int index) {
						TreeDemoNode d = datum.<TreeDemoNode> as();
						return ((d.id() == -1) ? d.id(++i) : d.id());
					}
				});

		// add click function on node click
		Selection nodeEnter = node
				.enter()
				.append("g")
				.attr("class", css.node())
				.attr("transform",
						"translate(" + source.getNumAttr("x0") + ","
								+ source.getNumAttr("y0") + ")")
				.on("click", new Click());

		// add circles to all entering nodes
		nodeEnter.append("circle").attr("r", 1e-6)
				.style("fill", new DatumFunction<String>() {
					@Override
					public String apply(Element context, Value d, int index) {
						JavaScriptObject node = d.<TreeDemoNode> as()
								.getObjAttr("_children");
						return (node != null) ? "lightsteelblue" : "#fff";
					}
				});

		// transition entering nodes
		Transition nodeUpdate = node.transition().duration(duration)
				.attr("transform", new DatumFunction<String>() {
					@Override
					public String apply(Element context, Value d, int index) {
						TreeDemoNode data = d.<TreeDemoNode> as();
						return "translate(" + data.x() + "," + data.y() + ")";
					}
				});

		nodeUpdate.select("circle").attr("r", 4.5)
				.style("fill", new DatumFunction<String>() {
					@Override
					public String apply(Element context, Value d, int index) {
						JavaScriptObject object = d.<TreeDemoNode> as()
								.getObjAttr("_children");
						return (object != null) ? "lightsteelblue" : "#fff";
					}
				});

		// transition exiting nodes
		Transition nodeExit = node.exit().transition().duration(duration)
				.attr("transform", new DatumFunction<String>() {
					@Override
					public String apply(Element context, Value d, int index) {
						return "translate(" + source.x() + "," + source.y()
								+ ")";
					}
				}).remove();

		nodeExit.select("circle").attr("r", 1e-6);

		// update svg paths for new node locations
		UpdateSelection link = svg.selectAll("path." + css.link()).data(links,
				new KeyFunction<Integer>() {
					@Override
					public Integer map(Element context, Array<?> newDataArray,
							Value datum, int index) {
						return datum.<Link> as().target().<TreeDemoNode> cast()
								.id();
					}
				});

		link.enter().insert("svg:path", "g").attr("class", css.link())
				.attr("d", new DatumFunction<String>() {
					@Override
					public String apply(Element context, Value d, int index) {
						Coords o = Coords.create(source.getNumAttr("x0"),
								source.getNumAttr("y0"));
						return diagonal.generate(Link.create(o, o));
					}
				});

		link.transition().duration(duration).attr("d", diagonal);

		link.exit().transition().duration(duration)
				.attr("d", new DatumFunction<String>() {
					@Override
					public String apply(Element context, Value d, int index) {
						Coords o = Coords.create(source.x(), source.y());
						return diagonal.generate(Link.create(o, o));
					}
				}).remove();

		// update locations on node
		nodes.forEach(new ForEachCallback<Void>() {
			@Override
			public Void forEach(Object thisArg, Value element, int index,
					Array<?> array) {
				TreeDemoNode data = element.<TreeDemoNode> as();
				data.setAttr("x0", data.x());
				data.setAttr("y0", data.y());
				return null;
			}
		});
	}

	private class Collapse implements ForEachCallback<Void> {
		@Override
		public Void forEach(Object thisArg, Value element, int index,
				Array<?> array) {
			TreeDemoNode datum = element.<TreeDemoNode> as();
			Array<HierarchicalLayout.Node> children = datum.children();
			if (children != null) {
				datum.setAttr("_children", children);
				datum.getObjAttr("_children").<Array<HierarchicalLayout.Node>> cast()
						.forEach(this);
				datum.setAttr("children", null);
			}
			return null;
		}
	}
	
	private class Click implements DatumFunction<Void> {
		@Override
		public Void apply(Element context, Value d, int index) {
			TreeDemoNode node = d.<TreeDemoNode> as();
			if (node.children() != null) {
				node.setAttr("_children", node.children());
				node.setAttr("children", null);
			} else {
				node.setAttr("children", node.getObjAttr("_children"));
				node.setAttr("_children", null);
			}
			update(node);
			return null;
		}
	}

}
