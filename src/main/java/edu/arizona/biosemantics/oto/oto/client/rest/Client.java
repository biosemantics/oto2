package edu.arizona.biosemantics.oto.oto.client.rest;

import java.util.LinkedHashSet;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import edu.arizona.biosemantics.oto.oto.shared.model.Bucket;
import edu.arizona.biosemantics.oto.oto.shared.model.Collection;
import edu.arizona.biosemantics.oto.oto.shared.model.Context;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class Client {

	private String url;
	private com.sun.jersey.api.client.Client client;

	public Client(String url) {
		this.url = url;
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		client = com.sun.jersey.api.client.Client.create(clientConfig);
		client.addFilter(new LoggingFilter(System.out));
	}

	public Collection put(Collection collection) {
		String url = this.url + "rest/collection";
	    WebResource webResource = client.resource(url);
	    try {
		    collection = webResource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).put(Collection.class, collection);
		    return collection;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Collection get(Collection collection) {
		String url = this.url + "rest/collection";
	    WebResource webResource = client.resource(url);
	    MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
	    queryParams.add("id", String.valueOf(collection.getId()));
	    queryParams.add("secret", collection.getSecret());
	    try {
		    collection = webResource.queryParams(queryParams).get(Collection.class);
			return collection;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Client client = new Client("http://127.0.0.1:55634/");	
		Collection collection = client.put(createSampleCollection());
		System.out.println(client.get(collection));
	}
	
	public static Collection createSampleCollection() {
		LinkedHashSet<Bucket> buckets = new LinkedHashSet<Bucket>();
		Bucket b = new Bucket();
		Term t1 = new Term();
		t1.setTerm("test");
		Context c1 = new Context();
		c1.setSource("source");
		c1.setSentence("sentence");
		Context c2 = new Context();
		c2.setSource("source");
		c2.setSentence("sentence");
		//t1.addContext(c1);
		//t1.addContext(c2);
		Term t2 = new Term();
		
		t2.setTerm("test1");
		Term t3 = new Term();
		t3.setTerm("test2");
		b.addTerm(t1);
		b.addTerm(t2);
		b.addTerm(t3);
		buckets.add(b);
		b.setName("bucketName");
		
		Collection collection = new Collection();
		collection.setName("My test");
		collection.setBuckets(buckets);
		
		LinkedHashSet<Label> labels = new LinkedHashSet<Label>();
		Label l1 = new Label();
		l1.setName("label1");
		
		Label l2 = new Label();
		l2.setName("label1");
		
		Label l3 = new Label();
		l3.setName("label1");
		
		labels.add(l1);
		labels.add(l2);
		labels.add(l3);
		collection.setLabels(labels);
		return collection;
	}
}
