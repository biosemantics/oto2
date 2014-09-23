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
					AlertMessageBox alert = new AlertMessageBox("Already contained", possibleMainTerm.getTerm() + " " +
							"already contained as synonym of " + addResult.parent.getTerm() + ".");
					alert.show();
				} else {
					AlertMessageBox alert = new AlertMessageBox("Already contained", possibleMainTerm.getTerm() + " " +
							"already contained.");
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

}
