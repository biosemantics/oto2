package edu.arizona.biosemantics.oto2.ontologize.server.rest.client;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;

import edu.arizona.biosemantics.common.biology.TaxonGroup;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Collection;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Context;
import edu.arizona.biosemantics.oto2.ontologize.shared.model.Term;

public class Client implements AutoCloseable {

	private String url;
	private javax.ws.rs.client.Client client;
	private WebTarget target;	

	public Client(String url) {
		this.url = url;
	}
	
	public void open() {
		log(LogLevel.DEBUG, "Connect to " + url);
		client = ClientBuilder.newBuilder().withConfig(new ClientConfig()).register(JacksonFeature.class).build();
		client.register(new LoggingFilter(Logger.getAnonymousLogger(), true));
		
		//this doesn't seem to work for posts (among others), even though it is documented as such, use authentication header instead there
		//target = client.target(this.apiUrl).queryParam("apikey", this.apiKey);
		target = client.target(this.url);
	}
	
	public void close() {
		log(LogLevel.DEBUG, "Disconnect from " + url);
		client.close();
	}
	
	public Future<Collection> post(Collection collection) {
		return this.getCollectionPostInvoker().post(Entity.entity(collection, MediaType.APPLICATION_JSON), Collection.class);
	}
	
	public void post(Collection collection, InvocationCallback<List<Collection>> callback) {
		this.getCollectionPostInvoker().post(Entity.entity(collection, MediaType.APPLICATION_JSON), callback);
	}
	
	public Future<Collection> get(int id, String secret) {
		return this.getCollectionGetInvoker(id, secret).get(Collection.class);
	}
	
	public void get(int id, String secret, InvocationCallback<List<Collection>> callback) {
		this.getCollectionGetInvoker(id, secret).get(callback);
	}
	
	public Future<List<Context>> post(int collectionId, String secret, List<Context> contexts) {
		return this.getPostContextsInvoker(collectionId, secret).post(Entity.entity(contexts, MediaType.APPLICATION_JSON), new GenericType<List<Context>>(){});
	}
	
	public void post(int collectionId, String secret, List<Context> contexts, InvocationCallback<List<Context>> callback) {
		this.getPostContextsInvoker(collectionId, secret).post(Entity.entity(contexts, MediaType.APPLICATION_JSON), callback);
	}
	
	private AsyncInvoker getCollectionPostInvoker() {
		return target.path("rest").path("ontologize").path("collection").request(MediaType.APPLICATION_JSON).async();
	}
	
	private AsyncInvoker getCollectionGetInvoker(int id, String secret) {
		return target.path("rest").path("ontologize").path("collection").path(String.valueOf(id))
				.queryParam("secret", secret).request(MediaType.APPLICATION_JSON).async();
	}
	
	private AsyncInvoker getPostContextsInvoker(int collectionId, String secret) {
		return target.path("rest").path("ontologize").path("context").path(String.valueOf(collectionId)).queryParam("secret", secret).request(MediaType.APPLICATION_JSON).async();
	}
	
	/**
	 * @param args
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		System.out.println("connect");
		Client client = new Client("http://127.0.0.1:60763/");
		client.open();
		
		List<Term> terms = new LinkedList<Term>();
		terms.add(new Term("leaf", null, "/structure", "structure"));
		terms.add(new Term("stem", null, "/structure", "structure"));
		terms.add(new Term("absent", null, "/character/coloration", "coloration"));
		terms.add(new Term("blue", null, "/character/coloration", "coloration"));
		Collection collection = new Collection("name", TaxonGroup.PLANT, "my secret", terms);
		Future<Collection> col = client.post(collection);
		int id = col.get().getId();
		System.out.println(id);
		
		
		List<Context> contexts = new LinkedList<Context>();
		contexts.add(new Context(1, "a", "the blue house on the street"));
		contexts.add(new Context(1, "b", "the red car in the garage"));
		contexts.add(new Context(1, "c", "leafs on the ground and a leafish leaf"));
		contexts.add(new Context(1, "d", "long stems with wide branches at the stem"));
		
		Future<List<Context>> result = client.post(id, "my secret", contexts);
		result.get();
		
		
		/*
		Future<Collection> collectionFuture = client.put(createSampleCollection());
		Collection collection = collectionFuture.get();
		collectionFuture = client.get(String.valueOf(collection.getId()), collection.getSecret());
		System.out.println(collectionFuture.get());
		*/
		client.close();
	}
}