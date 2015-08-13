package edu.arizona.biosemantics.oto2.ontologize.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

import edu.arizona.biosemantics.oto2.ontologize.server.rpc.CollectionService;
import edu.arizona.biosemantics.oto2.ontologize.server.rpc.ContextService;
import edu.arizona.biosemantics.oto2.ontologize.server.rpc.ToOntologyService;

public class GuiceServletConfig extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new ServletModule() {
			/* http://www.gwtproject.org/doc/latest/DevGuideServerCommunication.html#DevGuideImplementingServices 
				-> Common pitfalls: for url-pattern help */
			@Override
			protected void configureServlets() {
				serve("/ontologize/ontologize_collection").with(CollectionService.class);
				serve("/ontologize/ontologize_toOntology").with(ToOntologyService.class);
				serve("/ontologize/ontologize_context").with(ContextService.class);

			}
			
		}, new GuiceModule());
	}
}