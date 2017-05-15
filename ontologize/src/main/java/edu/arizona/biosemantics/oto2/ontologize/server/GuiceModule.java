package edu.arizona.biosemantics.oto2.ontologize.server;

//import com.google.gwt.logging.server.RemoteLoggingServiceImpl;
import java.io.File;

import com.google.gwt.logging.server.RemoteLoggingServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

import edu.arizona.biosemantics.oto2.ontologize.server.persist.DAOManager;
import edu.arizona.biosemantics.oto2.ontologize.server.rpc.CollectionService;
import edu.arizona.biosemantics.oto2.ontologize.server.rpc.ContextService;
import edu.arizona.biosemantics.oto2.ontologize.server.rpc.ToOntologyService;


public class GuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(CollectionService.class).in(Scopes.SINGLETON);
		bind(ToOntologyService.class).in(Scopes.SINGLETON);
		bind(ContextService.class).in(Scopes.SINGLETON);
		bind(RemoteLoggingServiceImpl.class).in(Scopes.SINGLETON);
		
		bind(DAOManager.class).in(Scopes.SINGLETON);
		
	}

}
