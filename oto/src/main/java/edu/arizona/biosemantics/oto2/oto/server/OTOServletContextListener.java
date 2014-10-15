package edu.arizona.biosemantics.oto2.oto.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.arizona.biosemantics.bioportal.client.BioPortalClient;
import edu.arizona.biosemantics.oto2.oto.server.db.ConnectionPool;
import edu.arizona.biosemantics.oto2.oto.server.db.OntologyDAO;
import edu.arizona.biosemantics.oto2.oto.server.db.Query;
import edu.arizona.biosemantics.oto2.oto.shared.log.LogLevel;

public class OTOServletContextListener implements ServletContextListener {
	private ConnectionPool connectionPool;

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		log(LogLevel.INFO, "Destroy oto context " + event.getServletContext().getContextPath());
		try {
			log(LogLevel.INFO, "Shutting down conntection pool");
			connectionPool.shutdown();
			log(LogLevel.INFO, "Closing bioportal client");
			OntologyDAO.bioportalClient.close();
		} catch (Exception e) {
			log(LogLevel.ERROR, "Exception shutting down oto context", e);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		log(LogLevel.INFO, "Initializing oto context at context path: " + event.getServletContext().getContextPath());
		log(LogLevel.INFO, "Configuration used " + Configuration.asString());
		
		try {
			// init connection pool
			log(LogLevel.INFO, "Initializing connection pool");
			connectionPool = new ConnectionPool();
			Query.connectionPool = connectionPool;
			
			log(LogLevel.INFO, "Initializing bioportal client");
			OntologyDAO.bioportalClient = new BioPortalClient(Configuration.bioportalUrl, Configuration.bioportalApiKey);
			OntologyDAO.bioportalClient.open();
		} catch (Exception e) {
			log(LogLevel.ERROR, "Exception initializing oto context", e);
		}
	}
}
