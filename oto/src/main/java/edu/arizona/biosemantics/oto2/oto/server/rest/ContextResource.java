package edu.arizona.biosemantics.oto2.oto.server.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.arizona.biosemantics.oto2.oto.shared.model.Context;

/**
 * Just a REST-like wrapper around the RPC service
 * @author thomas
 */
@Path("/context")
public class ContextResource {

	@javax.ws.rs.core.Context
	UriInfo uriInfo;
	@javax.ws.rs.core.Context
	Request request;		
	
	private Logger logger;
	private edu.arizona.biosemantics.oto2.oto.server.rpc.CollectionService rpcCollectionService = 
			new edu.arizona.biosemantics.oto2.oto.server.rpc.CollectionService();
	
	public ContextResource() {
		logger =  LoggerFactory.getLogger(this.getClass());
	}	
	
	@Path("{collectionId}")
	@PUT
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public List<Context> put(@PathParam("collectionId") int collectionId, @QueryParam("secret") String secret, List<Context> contexts) {
		try {
			rpcCollectionService.insert(collectionId, secret, contexts);
			return rpcCollectionService.insert(collectionId, secret, contexts);
		} catch (Exception e) {
			logger.error("Exception " + e.toString());
			e.printStackTrace();
			return null;
		}
	}
}