package edu.arizona.biosemantics.oto2.oto.client.categorize.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto2.oto.client.categorize.event.SynonymRemovalEvent.SynonymRemovalHandler;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label;
import edu.arizona.biosemantics.oto2.oto.shared.model.Term;

public class SynonymRemovalEvent extends GwtEvent<SynonymRemovalHandler> {

	public interface SynonymRemovalHandler extends EventHandler {
		void onSynonymRemoval(Label label, Term mainTerm, List<Term> synonyms);
	}
	
    public static Type<SynonymRemovalHandler> TYPE = new Type<SynonymRemovalHandler>();
	private Label label;
	private Term mainTerm;
	private List<Term> synonyms;
    
    
    public SynonymRemovalEvent(Label label, Term mainTerm, List<Term> synonyms) {
        this.label = label;
        this.mainTerm = mainTerm;
        this.synonyms = synonyms;
    }
	
	@Override
	public Type<SynonymRemovalHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SynonymRemovalHandler handler) {
		handler.onSynonymRemoval(label, mainTerm, synonyms);
	}

	public Label getLabel() {
		return label;
	}

	public Term getMainTerm() {
		return mainTerm;
	}

	public List<Term> getSynonyms() {
		return synonyms;
	}

}
