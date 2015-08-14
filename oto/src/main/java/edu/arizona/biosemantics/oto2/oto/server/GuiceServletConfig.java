package edu.arizona.biosemantics.oto2.oto.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

import edu.arizona.biosemantics.oto2.oto.server.rpc.CollectionService;
import edu.arizona.biosemantics.oto2.oto.server.rpc.CommunityService;
import edu.arizona.biosemantics.oto2.oto.server.rpc.ContextService;
import edu.arizona.biosemantics.oto2.oto.server.rpc.OntologyService;

public class GuiceServletConfig extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new ServletModule() {
			/* http://www.gwtproject.org/doc/latest/DevGuideServerCommunication.html#DevGuideImplementingServices 
				-> Common pitfalls: for url-pattern help */
			@Override
			protected void configureServlets() {
				serve("/ontologize/oto_collection").with(CollectionService.class);
				serve("/ontologize/oto_community").with(CommunityService.class);
				serve("/ontologize/oto_ontology").with(OntologyService.class);
				serve("/ontologize/oto_context").with(ContextService.class);
			}
			
		}, new GuiceModule());
	}
}