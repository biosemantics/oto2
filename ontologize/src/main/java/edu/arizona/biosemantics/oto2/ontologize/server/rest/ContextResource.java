package edu.arizona.biosemantics.oto2.ontologize.server.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.google.inject.Inject;

import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.ontologize.server.persist.DAOManager;
import edu.arizona.biosemantics.oto2.ontologize.server.rpc.CollectionService;
import edu.arizona.biosemantics.oto2.ontologize.server.rpc.ContextService;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Context;

/**
 * Just a REST-like wrapper around the RPC service
 * @author thomas
 */
@Path("/ontologize/context")
public class ContextResource {

	@javax.ws.rs.core.Context
	UriInfo uriInfo;
	@javax.ws.rs.core.Context
	Request request;		

	//TODO: Inject singletons
	private ContextService contextService;
	
	@Inject
	public ContextResource() throws OWLOntologyCreationException {
		this.contextService = new ContextService(new DAOManager());
		log(LogLevel.DEBUG, "ContextResource initialized");
	}
	
	@Path("{collectionId}")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public List<Context> post(@PathParam("collectionId") int collectionId, @QueryParam("secret") String secret, List<Context> contexts) {
		try {
			return contextService.insert(collectionId, secret, contexts);
		} catch (Exception e) {
			log(LogLevel.ERROR, "Exception", e);
			return null;
		}
	}
}