package edu.arizona.biosemantics.oto2.oto.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.arizona.biosemantics.oto2.oto.server.db.ConnectionPool;
import edu.arizona.biosemantics.oto2.oto.server.db.Query;

public class OTOServletContextListener implements ServletContextListener {
	private ConnectionPool connectionPool;

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if (event.getServletContext().getContextPath().contains("etcsite")) {
			try {
				connectionPool.shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		// if(event.getServletContext().getContextPath().contains("oto")) {
		try {
			// init connection pool
			connectionPool = new ConnectionPool();
			Query.connectionPool = connectionPool;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
