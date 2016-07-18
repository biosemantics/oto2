package edu.arizona.biosemantics.oto2.ontologize2.shared;

import edu.arizona.biosemantics.oto2.ontologize2.shared.model.Collection;

public interface IToOntologyService {
	public void storeLocalOntologiesToFile(Collection collection) throws Exception;

}
