package edu.arizona.biosemantics.oto.oto.client.rest;

import java.util.LinkedList;
import java.util.List;

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

	public Collection get(String id, String secret) {
		String url = this.url + "rest/collection";
	    WebResource webResource = client.resource(url);
	    MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
	    queryParams.add("id", id);
	    queryParams.add("secret", secret);
	    try {
		    return webResource.queryParams(queryParams).get(Collection.class);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Client client = new Client("http://127.0.0.1:8888/");	
		Collection collection = client.put(createSampleCollection());
		System.out.println(client.get(String.valueOf(collection.getId()), collection.getSecret()));
	}
	
	public static Collection createSampleCollection() {
		List<Bucket> buckets = new LinkedList<Bucket>();
		Bucket b = new Bucket();
		Bucket b2 = new Bucket();
		Bucket b3 = new Bucket();
		Term t1 = new Term();
		t1.setTerm("leaf1");
		Term t2 = new Term();
		t2.setTerm("stem");
		Term t3 = new Term();
		t3.setTerm("apex");
		Term t4 = new Term();
		t4.setTerm("root");
		Term t5 = new Term();
		t5.setTerm("sepal");
		b.addTerm(t1);
		b.addTerm(t2);
		b.addTerm(t3);
		b.addTerm(t4);
		b.addTerm(t5);
		buckets.add(b);
		b.setName("structures");
		Term c1 = new Term("length");
		Term c2 = new Term("color");
		b2.addTerm(c1);
		b2.addTerm(c2);
		b2.setName("characters");
		b3.setName("others");
		Term o1 = new Term("asdfg");
		b3.addTerm(o1);
		buckets.add(b2);
		buckets.add(b3);
		
		Collection collection = new Collection();
		collection.setName("My test");
		collection.setBuckets(buckets);
		
		/*List<Label> labels = new LinkedList<Label>();
		Label l0 = new Label();
		l0.setName("structure");
		
		Label l1 = new Label();
		l1.setName("arrangement");
		
		Label l2 = new Label();
		l2.setName("architecture");
		
		Label l3 = new Label();
		l3.setName("coloration");		
		
		labels.add(l0);
		labels.add(l1);
		labels.add(l2);
		labels.add(l3);
		collection.setLabels(labels);*/
		
		collection.setSecret("my secret");
		return collection;
	}
}
