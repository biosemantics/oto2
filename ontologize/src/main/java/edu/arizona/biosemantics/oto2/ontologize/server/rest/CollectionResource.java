package edu.arizona.biosemantics.oto2.ontologize.server.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.DAOManager;
import edu.arizona.biosemantics.oto2.ontologize.server.rpc.CollectionService;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.rpc.ICollectionService;

/**
 * Just a REST-like wrapper around the RPC service
 * @author thomas
 */
@Path("/ontologize/collection")
public class CollectionResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	//TODO: Inject singletons
	private ICollectionService collectionService;	
	
	@Inject
	public CollectionResource() throws OWLOntologyCreationException {
		collectionService = new CollectionService(new DAOManager());
		log(LogLevel.DEBUG, "CollectionResource initialized");
	}
	
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Collection post(Collection collection) throws Exception {
		try {
			return collectionService.insert(collection);
		} catch (Exception e) {
			log(LogLevel.ERROR, "Exception", e);
			return null;
		}
	}

	@Path("{id}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Collection get(@PathParam("id") int id, @QueryParam("secret") String secret) {
		try {
			Collection result = collectionService.get(id, secret);
			return result;
		} catch (Exception e) {
			log(LogLevel.ERROR, "Exception", e);
			return null;
		}
	}

}