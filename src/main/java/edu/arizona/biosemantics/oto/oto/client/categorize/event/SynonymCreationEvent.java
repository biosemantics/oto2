package edu.arizona.biosemantics.oto.oto.client.categorize.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.oto.oto.client.categorize.event.SynonymCreationEvent.SynonymCreationHandler;
import edu.arizona.biosemantics.oto.oto.shared.model.Label;
import edu.arizona.biosemantics.oto.oto.shared.model.Term;

public class SynonymCreationEvent extends GwtEvent<SynonymCreationHandler> {

	public interface SynonymCreationHandler extends EventHandler {
		void onSynonymCreation(Label label, Term mainTerm, List<Term> synonymTerms);
	}
	
    public static Type<SynonymCreationHandler> TYPE = new Type<SynonymCreationHandler>();
    
    private List<Term> synonymTerms;
	private Term mainTerm;
	private Label label;
    
    public SynonymCreationEvent(Label label, Term mainTerm, List<Term> synonymTerms) {
        this.synonymTerms = synonymTerms;
        this.mainTerm = mainTerm;
        this.label = label;
    }
	
    public SynonymCreationEvent(Label label, Term mainTerm, Term synonymTerm) {
        this.synonymTerms = new LinkedList<Term>();
        this.synonymTerms.add(synonymTerm);
        this.mainTerm = mainTerm;
        this.label = label;
    }
	
	@Override
	public Type<SynonymCreationHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SynonymCreationHandler handler) {
		handler.onSynonymCreation(label, mainTerm, synonymTerms);
	}

	public Term getMainTerm() {
		return mainTerm;
	}
	
	public List<Term> getSynonymTerm() {
		return synonymTerms;
	}

	public Label getLabel() {
		return label;
	}	
}