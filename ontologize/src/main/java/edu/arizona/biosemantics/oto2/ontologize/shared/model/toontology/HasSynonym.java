package edu.arizona.biosemantics.oto2.ontologize.shared.model.toontology;

import java.util.List;

public interface HasSynonym {

	
	public List<Synonym> getSynonyms();
	
	public void setSynonyms(List<Synonym> synonyms);
	
	public void addSynonym(Synonym synonym);
	
	public void removeSynonym(Synonym synonym);
	
	public void clearSynonyms();
}
