package edu.arizona.biosemantics.oto2.ontologize2.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.oto2.ontologize2.client.TermsGrid.Row;
import edu.arizona.biosemantics.oto2.ontologize2.client.candidate.CandidateView;
import edu.arizona.biosemantics.oto2.ontologize2.client.relations.RelationsView;
import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;

public class Ontologize extends SimpleContainer {

	private EventBus eventBus = new SimpleEventBus();
	private ModelController modelController = new ModelController(eventBus);
	
	public Ontologize() {
		BorderLayoutContainer blc = new BorderLayoutContainer();
		
		CandidateView candidateView = new CandidateView(eventBus);
		ContentPanel cp = new ContentPanel();
		cp.setHeadingText("Candidates");
		cp.add(candidateView);
		BorderLayoutData d = new BorderLayoutData(.30);
		d.setMargins(new Margins(0, 0, 0, 0));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		blc.setWestWidget(cp, d);
		
		Label toCome = new Label("Trees");
		cp = new ContentPanel();
		cp.setHeadingText("Trees");
		cp.add(toCome);
		d = new BorderLayoutData(.30);
		d.setMargins(new Margins(0, 0, 0, 0));
		d.setCollapsible(true);
		d.setSplit(true);
		d.setCollapseMini(true);
		blc.setEastWidget(cp, d);
		
		RelationsView relationsView = new RelationsView(eventBus);
		cp = new ContentPanel();
		cp.setHeadingText("Relations");
		cp.add(relationsView);
		d = new BorderLayoutData();
		d.setMargins(new Margins(0, 0, 0, 0));
		blc.setCenterWidget(cp, d);
		
		
		this.add(blc);
		
	}

	public EventBus getEventBus() {
		return eventBus;
	}

}
