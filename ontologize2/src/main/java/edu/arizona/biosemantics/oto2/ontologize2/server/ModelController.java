package edu.arizona.biosemantics.oto2.ontologize2.server;
//package edu.arizona.biosemantics.oto2.ontologize2.client;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.google.gwt.event.shared.GwtEvent;
//import com.google.web.bindery.event.shared.EventBus;
//
//import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreatePartEvent;
//import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateSubclassEvent;
//import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateSynonymEvent;
//import edu.arizona.biosemantics.oto2.ontologize2.client.event.CreateTermEvent;
//import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveSubclassEvent;
//import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveSynonymEvent;
//import edu.arizona.biosemantics.oto2.ontologize2.client.event.ReplaceTermInRelationsEvent;
//import edu.arizona.biosemantics.oto2.ontologize2.client.event.LoadCollectionEvent;
//import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemovePartEvent;
//import edu.arizona.biosemantics.oto2.ontologize2.client.event.RemoveTermEvent;
//import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;
//import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Term;
//
////this facade should become a service later and return the events to inform client code to update UI
//public class ModelController {
//
//	private static Collection collection;
//	private static EventBus eventBus;
//	
//	/*public ModelController(EventBus eventBus) {
//		this.eventBus = eventBus;
//		//bindEvents();
//	}*/
//	
//	public static void setCollection(Collection collection) {
//		ModelController.collection = collection;
//	}
//
//	public static Collection getCollection() {
//		return collection;
//	}
//	
//	public static void loadCollection(Collection collection) {
//		ModelController.collection = collection;
//	}
//	
//	public static void createTerm(Term... terms) {
//		collection.createTerm(terms);
//	}
//	
//	public static void removeTerm(Term... terms) {
//		collection.removeTerms(terms);
//	}
//	
//	public static List<GwtEvent<?>> createPart(Term parent, List<Term> parts) {
//		collection.createPart(parent, parts);
//		List<GwtEvent<?>> disambiguationEvents = CollectionDisambiguator.disambiguateParts(parts);
//		return disambiguationEvents;
//	}
//	
//	public static List<GwtEvent<?>> createSubclass(Term superclass, List<Term> subclasses) {
//		collection.createSubclass(superclass, subclasses);
//		List<GwtEvent<?>> disambiguationEvents = CollectionDisambiguator.disambiguateClasses(subclasses);
//		return disambiguationEvents;
//	}
//	
//	public static List<GwtEvent<?>> createSynonym(Term preferredTerm, List<Term> synonyms) {
//		collection.createSynonym(preferredTerm, synonyms);
//		List<GwtEvent<?>> events = new ArrayList<GwtEvent<?>>();
//		events.add(new CreateSynonymEvent(preferredTerm, synonyms));
//		return events;
//	}
//	
//	public static List<GwtEvent<?>> removePart(Term parent, Term... parts) {
//		collection.removePart(parent, parts);
//		List<GwtEvent<?>> events = new ArrayList<GwtEvent<?>>();
//		events.add(new RemovePartEvent(parent, parts));
//		return events;
//	}
//	
//	public static List<GwtEvent<?>> removeSubclass(Term superclass, Term... subclasses) {
//		collection.removePart(superclass, subclasses);
//		List<GwtEvent<?>> events = new ArrayList<GwtEvent<?>>();
//		events.add(new RemoveSubclassEvent(superclass, subclasses));
//		return events;
//	}
//	
//	public static List<GwtEvent<?>> removeSynonym(Term preferredTerm, Term... synonyms) {
//		collection.removeSynonym(preferredTerm, synonyms);
//		List<GwtEvent<?>> events = new ArrayList<GwtEvent<?>>();
//		events.add(new RemoveSynonymEvent(preferredTerm, synonyms));
//		return events;
//	}
//
//	public static void setEventBus(com.google.gwt.event.shared.EventBus eventBus) {
//		ModelController.eventBus = eventBus;
//	}
//	
//	/*private void bindEvents() {
//		eventBus.addHandler(LoadCollectionEvent.TYPE, new LoadCollectionEvent.Handler() {
//			@Override
//			public void onLoad(LoadCollectionEvent event) {
//				collection = event.getCollection();
//			}
//		});
//		eventBus.addHandler(CreateTermEvent.TYPE, new CreateTermEvent.Handler() {
//			@Override
//			public void onCreate(CreateTermEvent event) {
//				collection.createTerm(event.getTerms());
//			}
//		});
//		eventBus.addHandler(RemoveTermEvent.TYPE, new RemoveTermEvent.Handler() {
//			@Override
//			public void onCreate(RemoveTermEvent event) {
//				collection.removeTerms(event.getTerms());
//			}
//		});
//		eventBus.addHandler(ReplaceTermInRelationsEvent.TYPE, new ReplaceTermInRelationsEvent.Handler() {
//			@Override
//			public void onDisambiguate(ReplaceTermInRelationsEvent event) {
//				collection.replaceTermInRelations(event.getOldTerm(), event.getNewTerm());
//			}
//		});
//		
//		eventBus.addHandler(CreatePartEvent.TYPE, new CreatePartEvent.Handler() {
//			@Override
//			public void onCreate(CreatePartEvent event) {
//				collection.createPart(event.getParent(), event.getParts());
//			}
//		});
//		eventBus.addHandler(RemovePartEvent.TYPE, new RemovePartEvent.Handler() {
//			@Override
//			public void onRemove(RemovePartEvent event) {
//				collection.removePart(event.getParent(), event.getParts());
//			}
//		});
//		eventBus.addHandler(CreateSubclassEvent.TYPE, new CreateSubclassEvent.Handler() {
//			@Override
//			public void onCreate(CreateSubclassEvent event) {
//				collection.createSubclass(event.getSuperclass(), event.getSubclasses());
//			}
//		});
//		eventBus.addHandler(RemoveSubclassEvent.TYPE, new RemoveSubclassEvent.Handler() {
//			@Override
//			public void onRemove(RemoveSubclassEvent event) {
//				collection.removeSubclass(event.getSuperclass(), event.getSubclasses());
//			}
//		});
//		eventBus.addHandler(CreateSynonymEvent.TYPE, new CreateSynonymEvent.Handler() {
//			@Override
//			public void onCreate(CreateSynonymEvent event) {
//				collection.createSynonym(event.getPreferredTerm(), event.getSynonyms());
//			}
//		});
//		eventBus.addHandler(RemoveSynonymEvent.TYPE, new RemoveSynonymEvent.Handler() {
//			@Override
//			public void onRemove(RemoveSynonymEvent event) {
//				collection.removeSubclass(event.getPreferredTerm(), event.getSynonyms());
//			}
//		});
//
//	}*/
//
//}
