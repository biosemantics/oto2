package edu.arizona.biosemantics.oto2.oto.client.common;

import java.util.List;
import java.util.Map;

import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

import edu.arizona.biosemantics.oto2.oto.shared.model.Term;
import edu.arizona.biosemantics.oto2.oto.shared.model.Label.AddResult;

public class Alerter {

	public static void alertNotAddedTerms(List<Term> possibleMainTerms, Map<Term, AddResult> addResults) {
		for(Term possibleMainTerm : possibleMainTerms) {
			AddResult addResult = addResults.get(possibleMainTerm);
			if(!addResult.result) {
				if(addResult.parent != null) {
					AlertMessageBox alert = new AlertMessageBox("Term exists in label", 
							"The term <b>" + possibleMainTerm.getTerm() + "</b> already exists in this label as synonym of <b>" + 
									addResult.parent.getTerm() + "</b>. A term can only appear once inside a label.");
					alert.show();
				} else {
					AlertMessageBox alert = new AlertMessageBox("Term exists in label", 
							"The term <b>" + possibleMainTerm.getTerm() + "</b> already exists in this label" + 
									". A term can only appear once inside a label.");
					alert.show();
				}
			}
		}
	}
	
	public static void alertNoOntoloygySelected() {
		AlertMessageBox alert = new AlertMessageBox("Ontology Selection", "Before you can use this feature" +
				" you have to select a set " +
				"of ontologies to search.");
		alert.show();
	}

	public static void alertFailedToLoadCollection() {
		AlertMessageBox alert = new AlertMessageBox("Load Collection Failed", "Failed to load the collection. Please come back later.");
		alert.show();
	}

	public static void alertTermWithNameExists(String newName) {
		AlertMessageBox alert = new AlertMessageBox("Term with name exists", "Failed to rename term. " +
				"Another term with the same spelling <b>" + newName + "</b> exists already.");
		alert.show();
	}

}
