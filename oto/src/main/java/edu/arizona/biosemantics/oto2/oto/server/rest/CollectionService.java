package edu.arizona.biosemantics.oto2.oto.server.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.arizona.biosemantics.oto2.oto.server.db.DAOManager;
import edu.arizona.biosemantics.oto2.oto.shared.model.Collection;

/**
 * Just a REST-like wrapper around the RPC service
 * @author thomas
 */
@Path("/collection")
public class CollectionService {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;		
	
	private Logger logger;
	private edu.arizona.biosemantics.oto2.oto.server.rpc.CollectionService rpcCollectionService = 
			new edu.arizona.biosemantics.oto2.oto.server.rpc.CollectionService();
	
	public CollectionService() {
		logger =  LoggerFactory.getLogger(this.getClass());
	}	
	
	@PUT
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Collection put(Collection collection) {
		try {
			return rpcCollectionService.insert(collection);
		} catch (Exception e) {
			logger.error("Exception " + e.toString());
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Collection get(@QueryParam("id") int id, @QueryParam("secret") String secret) {
		try {
			return rpcCollectionService.get(id, secret);
		} catch (Exception e) {
			logger.error("Exception " + e.toString());
			e.printStackTrace();
			return null;
		}
	}
}