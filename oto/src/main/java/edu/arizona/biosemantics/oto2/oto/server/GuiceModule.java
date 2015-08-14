package edu.arizona.biosemantics.oto2.oto.server;

//import com.google.gwt.logging.server.RemoteLoggingServiceImpl;
import java.io.File;

import com.google.gwt.logging.server.RemoteLoggingServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

import edu.arizona.biosemantics.oto2.oto.server.db.DAOManager;
import edu.arizona.biosemantics.oto2.oto.server.rpc.CollectionService;
import edu.arizona.biosemantics.oto2.oto.server.rpc.CommunityService;
import edu.arizona.biosemantics.oto2.oto.server.rpc.ContextService;
import edu.arizona.biosemantics.oto2.oto.server.rpc.OntologyService;


public class GuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(CollectionService.class).in(Scopes.SINGLETON);
		bind(CommunityService.class).in(Scopes.SINGLETON);
		bind(OntologyService.class).in(Scopes.SINGLETON);
		bind(ContextService.class).in(Scopes.SINGLETON);
		bind(RemoteLoggingServiceImpl.class).in(Scopes.SINGLETON);
		
		bind(DAOManager.class).in(Scopes.SINGLETON);
		
	}

}
