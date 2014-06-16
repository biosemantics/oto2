package edu.arizona.biosemantics.oto.oto.server.rest;

import java.util.LinkedList;
import java.util.List;

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

import edu.arizona.biosemantics.oto.oto.server.db.CollectionDAO;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;

@Path("/glossary")
public class CollectionService {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;		
	
	private Logger logger;
	
	public CollectionService() {
		logger =  LoggerFactory.getLogger(this.getClass());
	}	
	
	@PUT
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Collection put(Collection collection) {
		try {
			if(collection.getLabels().isEmpty())
				collection.setLabels(createDefaultLabels());
			if(collection.getSecret().isEmpty())
				createDefaultSecret(collection);
			Collection result = CollectionDAO.getInstance().insert(collection);
			return result;
		} catch (Exception e) {
			logger.error("Exception " + e.toString());
			e.printStackTrace();
			return null;
		}
	}
	
	private void createDefaultSecret(Collection collection) {
		String secret = String.valueOf(collection.getId());// Encryptor.getInstance().encrypt(Integer.toString(collection.getId()));
		collection.setSecret(secret);
	}

	private List<Label> createDefaultLabels() {
		List<Label> result = new LinkedList<Label>();
		Label abc = new Label("abc", "d");
		result.add(abc);
		result.add(new Label("bcd", "d"));
		result.add(new Label("efg", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("efg", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("efg", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("efg", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("efg", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("bcd", "d"));
		result.add(new Label("efg", "d"));
		result.add(new Label("bcd", "d"));
		return result;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Collection get(@QueryParam("id") int id, @QueryParam("secret") String secret) {
		try {
			if(!CollectionDAO.getInstance().isValidSecret(id, secret))
				return null;
			return CollectionDAO.getInstance().get(id);
		} catch (Exception e) {
			logger.error("Exception " + e.toString());
			e.printStackTrace();
		}
		return null;
	}
}